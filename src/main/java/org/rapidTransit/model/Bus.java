package org.rapidTransit.model;

public class Bus {
    private final int id;
    private final String busNumber;
    private final int seatsCount;

    public Bus(int id, String busNumber, int seatsCount) {
        this.id = id;
        this.busNumber = busNumber;
        this.seatsCount = seatsCount;
    }

    public int getId() {
        return id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public int getSeatsCount() {
        return seatsCount;
    }
}
