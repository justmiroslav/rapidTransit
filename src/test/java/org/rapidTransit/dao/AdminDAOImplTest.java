package org.rapidTransit.dao;

import org.rapidTransit.model.Admin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private AdminDAOImpl adminDAO;

    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        adminDAO = new AdminDAOImpl(mockConnection);
    }

    @Test
    public void findTest() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("admin_id")).thenReturn(1);
        when(mockResultSet.getString("admin_email")).thenReturn("admin@example.com");
        when(mockResultSet.getString("admin_password")).thenReturn("password");
        when(mockResultSet.getString("admin_name")).thenReturn("Admin Name");

        // Act
        Admin result = adminDAO.findByEmail("admin@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("Admin Name", result.getName());
    }
}
