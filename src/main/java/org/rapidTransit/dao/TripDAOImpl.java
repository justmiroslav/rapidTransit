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
    public Trip findById(int id) {
        String sql = "SELECT * FROM trips WHERE trip_id = ?";
        return executeQuery(sql, id);
    }

    @Override
    public Trip findByRouteAndDate(int routeId, LocalDate date) {
        String sql = "SELECT * FROM trips WHERE route_id = ? AND trip_date = ?";
        return executeQuery(sql, routeId, date);
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

    private Trip executeQuery(String sql, Object... parameters) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Trip(rs.getInt("trip_id"), rs.getInt("route_id"),
                        rs.getInt("bus_id"), rs.getDate("trip_date").toLocalDate(),
                        rs.getTime("departure_time").toLocalTime(), rs.getTime("arrival_time").toLocalTime(),
                        List.of((Integer[]) rs.getArray("available_seats").getArray()));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return null;
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
