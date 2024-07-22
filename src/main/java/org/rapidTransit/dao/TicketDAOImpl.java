package org.rapidTransit.dao;

import org.rapidTransit.model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAOImpl implements TicketDAO {
    private final Connection connection;

    public TicketDAOImpl(Connection connection) {
        this.connection = connection;
        initializeSequence();
    }

    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO tickets (trip_id, user_id, seat_number, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = prepareStatement(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), ticket)) {
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                ticket.setTicketId(rs.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Error saving ticket: " + e.getMessage());
        }
    }

    @Override
    public boolean hasUserTickets(long userId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking user tickets: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Ticket> findByUserId(long userId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        try (PreparedStatement pstmt = prepareStatementForQuery(sql, userId);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) tickets.add(createTicketFromResultSet(rs));
        } catch (SQLException e) {
            System.out.println("Error finding user tickets: " + e.getMessage());
        }
        return tickets;
    }

    @Override
    public Ticket findByTripAndUser(long tripId, long userId) {
        String sql = "SELECT * FROM tickets WHERE trip_id = ? AND user_id = ?";
        return findTicket(sql, tripId, userId);
    }

    @Override
    public Ticket findById(long ticketId) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        return findTicket(sql, ticketId);
    }

    private void initializeSequence() {
        String sql = "SELECT setval('tickets_ticket_id_seq', COALESCE((SELECT MAX(ticket_id) FROM tickets), 10000000))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing sequence: " + e.getMessage());
        }
    }

    private PreparedStatement prepareStatement(PreparedStatement pstmt, Ticket ticket) throws SQLException {
        pstmt.setLong(1, ticket.getTripId());
        pstmt.setLong(2, ticket.getUserId());
        pstmt.setInt(3, ticket.getSeatNumber());
        pstmt.setFloat(4, ticket.getTicketPrice());
        return pstmt;
    }

    private Ticket findTicket(String sql, Object... params) {
        try (PreparedStatement pstmt = prepareStatementForQuery(sql, params);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return createTicketFromResultSet(rs);
        } catch (SQLException e) {
            System.out.println("Error finding ticket: " + e.getMessage());
        }
        return null;
    }

    private PreparedStatement prepareStatementForQuery(String sql, Object... params) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        return pstmt;
    }

    private Ticket createTicketFromResultSet(ResultSet rs) throws SQLException {
        return new Ticket(rs.getLong("ticket_id"), rs.getLong("trip_id"),
                rs.getLong("user_id"), rs.getInt("seat_number"), rs.getFloat("price"));
    }
}
