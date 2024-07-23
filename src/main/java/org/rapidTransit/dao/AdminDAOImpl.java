package org.rapidTransit.dao;

import org.rapidTransit.model.Admin;

import java.sql.*;

public class AdminDAOImpl implements AdminDAO {
    private final Connection connection;

    public AdminDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Admin findByEmail(String email) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM admins WHERE admin_email = ?")) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Admin(rs.getInt("admin_id"), rs.getString("admin_email"),
                        rs.getString("admin_password"), rs.getString("admin_name"));
            }
        } catch (SQLException e) {
            System.out.println(STR."Error finding user: \{e.getMessage()}");
        }
        return null;
    }
}
