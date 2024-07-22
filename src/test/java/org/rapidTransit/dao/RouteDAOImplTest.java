package org.rapidTransit.dao;

import org.rapidTransit.model.Route;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RouteDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private RouteDAOImpl routeDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        routeDAO = new RouteDAOImpl(mockConnection);
    }

    @Test
    public void testFind() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("route_id")).thenReturn(1);
        when(mockResultSet.getString("departure_city")).thenReturn("City A");
        when(mockResultSet.getString("arrival_city")).thenReturn("City B");
        when(mockResultSet.getFloat("travel_time")).thenReturn(2.5f);

        // Act
        Route result = routeDAO.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals("City A", result.getDepartureCity());
        assertEquals("City B", result.getArrivalCity());
    }

    @Test
    public void testGetList() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString(1)).thenReturn("City A", "City B");

        // Act
        List<String> result = routeDAO.getUniqueCities();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("City A"));
        assertTrue(result.contains("City B"));
    }

    @Test
    public void testUpdate() throws SQLException {
        // Arrange
        Route route = new Route(1, "City A", "City B", 2.5f);

        // Act
        routeDAO.update(route);

        // Assert
        verify(mockPreparedStatement, times(1)).executeUpdate();
        verify(mockPreparedStatement).setString(1, "City A");
    }
}
