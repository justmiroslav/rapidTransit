package org.rapidTransit.dao;

import org.rapidTransit.model.Rating;
import org.rapidTransit.db.DatabaseConnection;
import org.rapidTransit.model.Route;
import org.rapidTransit.model.User;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class RatingDAOImpl implements RatingDAO {
    private final Connection connection;

    public RatingDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        initializeSequence();
    }

    @Override
    public void save(Rating rating) {
        String sql = "INSERT INTO ratings (user_id, trip_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, rating.getUserId());
            pstmt.setLong(2, rating.getTripId());
            pstmt.setInt(3, rating.getRating());
            pstmt.setString(4, rating.getComment());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                rating.setRatingId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Error saving rating: " + e.getMessage());
        }
    }

    @Override
    public List<Rating> findByUserId(long userId) {
        String sql = "SELECT * FROM ratings WHERE user_id = ? ORDER BY rating_id ASC";
        return findSomething(sql, stmt -> stmt.setLong(1, userId), this::mapResultSetToRating);
    }

    @Override
    public List<Rating> findByRouteId(int routeId) {
        String sql = "SELECT * FROM ratings WHERE trip_id IN (SELECT trip_id FROM trips WHERE route_id = ?) ORDER BY rating_id ASC";
        return findSomething(sql, stmt -> stmt.setInt(1, routeId), this::mapResultSetToRating);
    }

    @Override
    public List<User> getAllUsersWithRatings() {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT user_id FROM ratings) ORDER BY user_id ASC";
        return findSomething(sql, stmt -> {}, this::mapResultSetToUser);
    }

    @Override
    public List<Route> getAllRoutesWithRatings() {
        String sql = "SELECT * FROM routes WHERE route_id IN (SELECT route_id FROM trips WHERE trip_id IN (SELECT trip_id FROM ratings)) ORDER BY route_id ASC";
        return findSomething(sql, stmt -> {}, this::mapResultSetToRoute);
    }

    private <T> List<T> findSomething(String sql, PreparedStatementSetter setter, ResultSetMapper<T> mapper) {
        List<T> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setter.setValues(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return results;
    }

    private void initializeSequence() {
        String sql = "SELECT setval('ratings_rating_id_seq', COALESCE((SELECT MAX(rating_id) FROM ratings), 10000))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing sequence: " + e.getMessage());
        }
    }
    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        return new Rating(rs.getInt("rating_id"), rs.getLong("trip_id"), rs.getLong("user_id"),
                rs.getInt("rating"), rs.getString("comment"));
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(rs.getLong("user_id"), rs.getString("user_email"), rs.getString("user_password"),
                rs.getString("user_name"), rs.getFloat("balance"), rs.getBoolean("is_blocked"));
    }

    private Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        return new Route(rs.getInt("route_id"), rs.getString("departure_city"),
                rs.getString("arrival_city"), rs.getFloat("travel_time"));
    }

    @FunctionalInterface
    private interface PreparedStatementSetter {
        void setValues(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    private interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}