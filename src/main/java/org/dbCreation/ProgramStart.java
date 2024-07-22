package org.dbCreation;

import org.rapidTransit.db.DatabaseConnection;

import java.sql.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public class ProgramStart {
    public static void main() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection(); Statement stmt = conn.createStatement()) {
            createTables(stmt);

            for (String tableName : List.of("routes", "buses", "admins", "users", "trips")) {
                insertDataFromFile(conn, "data/" + tableName + ".txt", tableName);
            }

            System.out.println("Data inserted successfully!");

        } catch (SQLException | IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Integer[] parseSeatsArray(String seats) {
        String[] seatsArray = seats.substring(1, seats.length() - 1).split("; ");
        Integer[] result = new Integer[seatsArray.length];
        for (int i = 0; i < seatsArray.length; i++) {
            result[i] = Integer.parseInt(seatsArray[i]);
        }
        return result;
    }

    private static void createTables(Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE routes (route_id INT PRIMARY KEY, departure_city VARCHAR(50), arrival_city VARCHAR(50), travel_time FLOAT)");
        stmt.executeUpdate("CREATE TABLE buses (bus_id INT PRIMARY KEY, bus_number VARCHAR(10), seats INT)");
        stmt.executeUpdate("CREATE TABLE admins (admin_id INT PRIMARY KEY, admin_email VARCHAR(100), admin_password VARCHAR(100), admin_name VARCHAR(50))");
        stmt.executeUpdate("CREATE TABLE users (user_id SERIAL PRIMARY KEY, user_email VARCHAR(100), user_password VARCHAR(100), user_name VARCHAR(50), balance FLOAT, is_blocked BOOLEAN)");
        stmt.executeUpdate("CREATE TABLE trips (trip_id SERIAL PRIMARY KEY, route_id INT, bus_id INT, trip_date DATE, departure_time TIME, arrival_time TIME, available_seats INT[], FOREIGN KEY (route_id) REFERENCES routes (route_id), FOREIGN KEY (bus_id) REFERENCES buses (bus_id))");
        stmt.executeUpdate("CREATE TABLE tickets (ticket_id SERIAL PRIMARY KEY, trip_id BIGINT, user_id BIGINT, seat_number INT, price FLOAT, FOREIGN KEY (trip_id) REFERENCES trips (trip_id), FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE)");
        stmt.executeUpdate("CREATE TABLE ratings (rating_id SERIAL PRIMARY KEY, trip_id BIGINT, user_id BIGINT, rating INT, comment TEXT, FOREIGN KEY (trip_id) REFERENCES trips (trip_id), FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE)");
    }

    private static void insertDataFromFile(Connection conn, String fileName, String tableName) throws SQLException, IOException {
        String sql = switch (tableName) {
            case "routes" -> "INSERT INTO routes (route_id, departure_city, arrival_city, travel_time) VALUES (?, ?, ?, ?)";
            case "buses" -> "INSERT INTO buses (bus_id, bus_number, seats) VALUES (?, ?, ?)";
            case "admins" -> "INSERT INTO admins (admin_id, admin_email, admin_password, admin_name) VALUES (?, ?, ?, ?)";
            case "users" -> "INSERT INTO users (user_id, user_email, user_password, user_name, balance, is_blocked) VALUES (?, ?, ?, ?, ?, ?)";
            case "trips" -> "INSERT INTO trips (trip_id, route_id, bus_id, trip_date, departure_time, arrival_time, available_seats) VALUES (?, ?, ?, ?, ?, ?, ?)";
            default -> throw new IllegalArgumentException("Unsupported table: " + tableName);
        };

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            for (String line : lines) {
                String[] values = line.split(",");
                switch (tableName) {
                    case "routes":
                        pstmt.setInt(1, Integer.parseInt(values[0]));
                        pstmt.setString(2, values[1]);
                        pstmt.setString(3, values[2]);
                        pstmt.setFloat(4, Float.parseFloat(values[3]));
                        break;
                    case "buses":
                        pstmt.setInt(1, Integer.parseInt(values[0]));
                        pstmt.setString(2, values[2]);
                        pstmt.setInt(3, Integer.parseInt(values[1]));
                        break;
                    case "admins":
                        pstmt.setInt(1, Integer.parseInt(values[0]));
                        pstmt.setString(2, values[1]);
                        pstmt.setString(3, values[2]);
                        pstmt.setString(4, values[3]);
                        break;
                    case "users":
                        pstmt.setLong(1, Long.parseLong(values[0]));
                        pstmt.setString(2, values[1]);
                        pstmt.setString(3, values[2]);
                        pstmt.setString(4, values[3]);
                        pstmt.setFloat(5, Float.parseFloat(values[4]));
                        pstmt.setBoolean(6, Boolean.parseBoolean(values[5]));
                        break;
                    case "trips":
                        pstmt.setLong(1, Long.parseLong(values[0]));
                        pstmt.setInt(2, Integer.parseInt(values[1]));
                        pstmt.setInt(3, Integer.parseInt(values[2]));
                        pstmt.setDate(4, Date.valueOf(values[3]));
                        pstmt.setTime(5, Time.valueOf(values[4] + ":00"));
                        pstmt.setTime(6, Time.valueOf(values[5] + ":00"));
                        pstmt.setArray(7, conn.createArrayOf("INTEGER", parseSeatsArray(values[6])));
                        break;
                }
                pstmt.executeUpdate();
            }
        }
    }
}
