package org.rapidTransit.model;

public class Ticket {
    private long ticketId;
    private final long tripId;
    private final long userId;
    private final int seatNumber;
    private final float ticketPrice;

    public Ticket(long ticketId, long tripId, long userId, int seatNumber, float ticketPrice) {
        this.ticketId = ticketId;
        this.tripId = tripId;
        this.userId = userId;
        this.seatNumber = seatNumber;
        this.ticketPrice = ticketPrice;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getTripId() {
        return tripId;
    }

    public long getUserId() {
        return userId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }
}
