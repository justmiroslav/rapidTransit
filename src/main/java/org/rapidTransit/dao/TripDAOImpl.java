package org.rapidTransit.dao;

import org.rapidTransit.db.DatabaseConnection;
import org.rapidTransit.model.Trip;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class TripDAOImpl implements TripDAO {
    private final Connection connection;

    public TripDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        initializeSequence();
    }

    @Override
    public Trip findById(long tripId) {
        String sql = "SELECT * FROM trips WHERE trip_id = ?";
        return findTrip(sql, tripId);
    }

    @Override
    public Trip findByRouteAndDate(int routeId, LocalDate date) {
        String sql = "SELECT * FROM trips WHERE route_id = ? AND trip_date = ?";
        return findTrip(sql, routeId, date);
    }

    @Override
    public boolean tripExists(int routeId, LocalDate date) {
        String sql = "SELECT * FROM trips WHERE route_id = ? AND trip_date = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, routeId);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking if trip exists: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void updateAvailableSeats(Trip trip) {
        String sql = "UPDATE trips SET available_seats = ? WHERE trip_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setArray(1, connection.createArrayOf("INTEGER", trip.getAvailableSeats().toArray()));
            pstmt.setLong(2, trip.getTripId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating available seats: " + e.getMessage());
        }
    }

    private Trip findTrip(String sql, Object... params) {
        try (PreparedStatement pstmt = prepareStatementForQuery(sql, params); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return createTripFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return null;
    }

    private PreparedStatement prepareStatementForQuery(String sql, Object... params) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        return pstmt;
    }

    private Trip createTripFromResultSet(ResultSet rs) throws SQLException {
        return new Trip(rs.getLong("trip_id"), rs.getInt("route_id"),
                rs.getInt("bus_id"), rs.getDate("trip_date").toLocalDate(),
                rs.getTime("departure_time").toLocalTime(), rs.getTime("arrival_time").toLocalTime(),
                List.of((Integer[]) rs.getArray("available_seats").getArray()));
    }

    private void initializeSequence() {
        String sql = "SELECT setval('trips_trip_id_seq', COALESCE((SELECT MAX(trip_id) FROM trips), 100000))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing sequence: " + e.getMessage());
        }
    }
}
