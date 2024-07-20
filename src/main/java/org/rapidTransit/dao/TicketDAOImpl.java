package org.rapidTransit.dao;

import org.rapidTransit.db.DatabaseConnection;
import org.rapidTransit.model.Ticket;

import java.sql.*;

public class TicketDAOImpl implements TicketDAO {
    private final Connection connection;

    public TicketDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        initializeSequence();
    }

    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO tickets (user_id, trip_id, seat_number, price) VALUES (?, ?, ?, ?)";
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

    private void initializeSequence() {
        String sql = "SELECT setval('tickets_ticket_id_seq', COALESCE((SELECT MAX(ticket_id) FROM tickets), 10000000))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing sequence: " + e.getMessage());
        }
    }

    private PreparedStatement prepareStatement(PreparedStatement pstmt, Ticket ticket) throws SQLException {
        pstmt.setLong(1, ticket.getUserId());
        pstmt.setLong(2, ticket.getTripId());
        pstmt.setInt(3, ticket.getSeatNumber());
        pstmt.setFloat(4, ticket.getTicketPrice());
        return pstmt;
    }
}
