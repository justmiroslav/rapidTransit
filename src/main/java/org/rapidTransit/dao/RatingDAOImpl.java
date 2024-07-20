package org.rapidTransit.dao;

import org.rapidTransit.model.Rating;
import org.rapidTransit.db.DatabaseConnection;

import java.sql.*;

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

    private void initializeSequence() {
        String sql = "SELECT setval('ratings_rating_id_seq', COALESCE((SELECT MAX(rating_id) FROM ratings), 10000))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing sequence: " + e.getMessage());
        }
    }
}
