package org.rapidTransit.dao;

import org.rapidTransit.model.Ticket;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TicketDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private TicketDAOImpl ticketDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockStatement.execute(anyString())).thenReturn(true);

        ticketDAO = new TicketDAOImpl(mockConnection);
    }

    @Test
    public void testSave() throws SQLException {
        // Arrange
        Ticket ticket = new Ticket(0, 1, 1, 10, 100.0f);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        // Act
        ticketDAO.save(ticket);

        // Assert
        assertEquals(1L, ticket.getTicketId());
        verify(mockPreparedStatement).setLong(1, 1L);
    }

    @Test
    public void testHasUserTickets() throws SQLException {
        // Arrange
        long userId = 1L;
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        // Act
        boolean result = ticketDAO.hasUserTickets(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testGetList() throws SQLException {
        // Arrange
        long userId = 1L;
        when(mockResultSet.next()).thenReturn(true, true, false);
        setupMockResultSet();

        // Act
        List<Ticket> tickets = ticketDAO.findByUserId(userId);

        // Assert
        assertEquals(2, tickets.size());
        assertEquals(1L, tickets.get(0).getTicketId());
        assertEquals(2L, tickets.get(1).getTicketId());
    }

    @Test
    public void testFind() throws SQLException {
        // Arrange
        long ticketId = 1L;
        when(mockResultSet.next()).thenReturn(true);
        setupMockResultSet();

        // Act
        Ticket ticket = ticketDAO.findById(ticketId);

        // Assert
        assertNotNull(ticket);
        assertEquals(ticketId, ticket.getTicketId());
    }

    private void setupMockResultSet() throws SQLException {
        when(mockResultSet.getLong("ticket_id")).thenReturn(1L, 2L);
        when(mockResultSet.getLong("trip_id")).thenReturn(1L, 2L);
        when(mockResultSet.getLong("user_id")).thenReturn(1L, 2L);
        when(mockResultSet.getInt("seat_number")).thenReturn(10, 11);
        when(mockResultSet.getFloat("price")).thenReturn(100.0f, 110.0f);
    }
}