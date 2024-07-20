package org.rapidTransit.dao;

import org.rapidTransit.db.DatabaseConnection;
import org.rapidTransit.model.User;

import java.sql.*;

public class UserDAOImpl implements UserDAO {
    private final Connection connection;

    public UserDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        initializeSequence();
    }

    @Override
    public User findByEmail(String email) {
        return getUser("SELECT * FROM users WHERE user_email = ?", email);
    }

    @Override
    public User findById(long id) {
        return getUser("SELECT * FROM users WHERE user_id = ?", id);
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (user_email, user_password, user_name, balance, is_blocked) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = prepareStatement(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), user)) {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET user_email = ?, user_password = ?, user_name = ?, balance = ?, is_blocked = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = prepareStatement(connection.prepareStatement(sql), user)) {
            pstmt.setLong(6, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    private void initializeSequence() {
        String sql = "SELECT setval('users_user_id_seq', COALESCE((SELECT MAX(user_id) FROM users), 1000000))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing sequence: " + e.getMessage());
        }
    }

    private User getUser(String query, Object param) {
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (param instanceof String) {
                pstmt.setString(1, (String) param);
            } else if (param instanceof Long) {
                pstmt.setLong(1, (Long) param);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("user_email"),
                        rs.getString("user_password"), rs.getString("user_name"),
                        rs.getFloat("balance"), rs.getBoolean("is_blocked"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    private PreparedStatement prepareStatement(PreparedStatement pstmt, User user) throws SQLException {
        pstmt.setString(1, user.getEmail());
        pstmt.setString(2, user.getPassword());
        pstmt.setString(3, user.getName());
        pstmt.setFloat(4, user.getBalance());
        pstmt.setBoolean(5, user.isBlocked());
        return pstmt;
    }
}
