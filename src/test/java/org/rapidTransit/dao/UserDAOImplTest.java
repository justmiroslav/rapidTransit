package org.rapidTransit.dao;

import org.rapidTransit.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class UserDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private UserDAOImpl userDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockStatement.execute(anyString())).thenReturn(true);

        userDAO = new UserDAOImpl(mockConnection);
    }

    @Test
    public void testFind() throws SQLException {
        // Arrange
        String email = "test@example.com";
        when(mockResultSet.next()).thenReturn(true);
        setupMockResultSet();

        // Act
        User user = userDAO.findByEmail(email);

        // Assert
        assertNotNull(user);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void testGetList() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true, true, false);
        setupMockResultSet();

        // Act
        List<User> users = userDAO.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        verify(mockStatement).executeQuery(anyString());
    }

    @Test
    public void testSave() throws SQLException {
        // Arrange
        User user = new User(0, "test@example.com", "password", "Test User", 100.0f, false);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        // Act
        userDAO.save(user);

        // Assert
        assertEquals(1L, user.getId());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    public void testUpdate() throws SQLException {
        // Arrange
        User user = new User(1, "test@example.com", "password", "Test User", 100.0f, false);

        // Act
        userDAO.update(user);

        // Assert
        verify(mockPreparedStatement).setString(1, user.getEmail());
        verify(mockPreparedStatement).executeUpdate();
    }

    private void setupMockResultSet() throws SQLException {
        when(mockResultSet.getLong("user_id")).thenReturn(1L, 2L);
        when(mockResultSet.getString("user_email")).thenReturn("test@example.com", "test2@example.com");
        when(mockResultSet.getString("user_password")).thenReturn("password", "password2");
        when(mockResultSet.getString("user_name")).thenReturn("Test User", "Test User 2");
        when(mockResultSet.getFloat("balance")).thenReturn(100.0f, 200.0f);
        when(mockResultSet.getBoolean("is_blocked")).thenReturn(false, false);
    }
}