package org.rapidTransit.dao;

import org.rapidTransit.db.DatabaseConnection;
import org.rapidTransit.model.Trip;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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
    public List<Trip> findByRouteId(int routeId) {
        String sql = "SELECT * FROM trips WHERE route_id = ? ORDER BY trip_id ASC";
        return findTrips(sql, routeId);
    }

    @Override
    public List<Trip> getLastTrips() {
        String sql = "SELECT * FROM trips WHERE trip_date IN (SELECT DISTINCT trip_date FROM trips ORDER BY trip_date DESC LIMIT 2) ORDER BY trip_date ASC";
        return findTrips(sql);
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

    @Override
    public void update(Trip trip) {
        String sql = "UPDATE trips SET route_id = ?, bus_id = ?, trip_date = ?, departure_time = ?, arrival_time = ?, available_seats = ? WHERE trip_id = ?";
        try (PreparedStatement pstmt = prepareStatement(connection.prepareStatement(sql), trip)) {
            pstmt.setLong(7, trip.getTripId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating trip: " + e.getMessage());
        }
    }

    @Override
    public void save(Trip trip) {
        String sql = "INSERT INTO trips (route_id, bus_id, trip_date, departure_time, arrival_time, available_seats) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = prepareStatement(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), trip)) {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                trip.setTripId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Error saving trip: " + e.getMessage());
        }
    }

    private PreparedStatement prepareStatement(PreparedStatement pstmt, Trip trip) throws SQLException {
        pstmt.setInt(1, trip.getRouteId());
        pstmt.setInt(2, trip.getBusId());
        pstmt.setDate(3, Date.valueOf(trip.getTripDate()));
        pstmt.setTime(4, Time.valueOf(trip.getDepartureTime()));
        pstmt.setTime(5, Time.valueOf(trip.getArrivalTime()));
        pstmt.setArray(6, connection.createArrayOf("INTEGER", trip.getAvailableSeats().toArray()));
        return pstmt;
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

    private List<Trip> findTrips(String sql, Object... params) {
        List<Trip> trips = new ArrayList<>();
        try (PreparedStatement pstmt = prepareStatementForQuery(sql, params); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                trips.add(createTripFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return trips;
    }
}
