package org.rapidTransit.dao;

import org.rapidTransit.model.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class BusDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private BusDAOImpl busDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        busDAO = new BusDAOImpl(mockConnection);
    }

    @Test
    public void testFind() throws SQLException {
        // Arrange
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("bus_id")).thenReturn(1);
        when(mockResultSet.getString("bus_number")).thenReturn("404");
        when(mockResultSet.getInt("seats")).thenReturn(20);

        // Act
        Bus result = busDAO.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.id());
    }
}