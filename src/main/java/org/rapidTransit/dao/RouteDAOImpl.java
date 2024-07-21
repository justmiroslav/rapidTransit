package org.rapidTransit.dao;

import org.rapidTransit.db.DatabaseConnection;
import org.rapidTransit.model.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteDAOImpl implements RouteDAO {
    private final Connection connection;

    public RouteDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Route findById(int id) {
        String sql = "SELECT * FROM routes WHERE route_id = ?";
        return executeQuery(sql, id);
    }

    @Override
    public Route findRouteId(String departureCity, String arrivalCity) {
        String sql = "SELECT * FROM routes WHERE departure_city = ? AND arrival_city = ?";
        return executeQuery(sql, departureCity, arrivalCity);
    }

    @Override
    public List<String> getUniqueCities() {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT departure_city FROM routes UNION SELECT DISTINCT arrival_city FROM routes";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cities.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error getting unique cities: " + e.getMessage());
        }
        return cities;
    }

    @Override
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes ORDER BY route_id ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                routes.add(new Route(rs.getInt("route_id"), rs.getString("departure_city"),
                        rs.getString("arrival_city"), rs.getFloat("travel_time")));
            }
        } catch (SQLException e) {
            System.out.println("Error getting all routes: " + e.getMessage());
        }
        return routes;
    }

    @Override
    public void update(Route route) {
        String sql = "UPDATE routes SET departure_city = ?, arrival_city = ?, travel_time = ? WHERE route_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, route.getDepartureCity());
            pstmt.setString(2, route.getArrivalCity());
            pstmt.setFloat(3, route.getTravelTime());
            pstmt.setInt(4, route.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating route: " + e.getMessage());
        }
    }

    private Route executeQuery(String sql, Object... parameters) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Route(rs.getInt("route_id"), rs.getString("departure_city"),
                        rs.getString("arrival_city"), rs.getFloat("travel_time"));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return null;
    }
}
