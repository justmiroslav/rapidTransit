package org.rapidTransit.dao;

import org.rapidTransit.model.Bus;
import java.sql.*;

public class BusDAOImpl implements BusDAO {
    private final Connection connection;

    public BusDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Bus findById(int id) {
        String sql = "SELECT * FROM buses WHERE bus_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Bus(rs.getInt("bus_id"), rs.getString("bus_number"),
                        rs.getInt("seats"));
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        return null;
    }
}
