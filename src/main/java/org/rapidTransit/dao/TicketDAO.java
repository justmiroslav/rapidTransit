package org.rapidTransit.dao;

import org.rapidTransit.model.Ticket;

import java.util.List;

public interface TicketDAO {
    void save(Ticket ticket);
    boolean hasUserTickets(long userId);
    List<Ticket> findByUserId(long userId);
    Ticket findByTripAndUser(long tripId, long userId);
    Ticket findById(long ticketId);
}
