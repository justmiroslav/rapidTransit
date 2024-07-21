package org.rapidTransit.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Trip {
    private long tripId;
    private final int routeId;
    private final int busId;
    private final LocalDate tripDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private List<Integer> availableSeats;

    public Trip(long tripId, int routeId, int busId, LocalDate tripDate, LocalTime departureTime, LocalTime arrivalTime, List<Integer> availableSeats) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.busId = busId;
        this.tripDate = tripDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public int getRouteId() {
        return routeId;
    }

    public int getBusId() {
        return busId;
    }

    public LocalDate getTripDate() {
        return tripDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<Integer> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<Integer> availableSeats) {
        this.availableSeats = availableSeats;
    }
}
