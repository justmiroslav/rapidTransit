package org.rapidTransit.dao;

import org.rapidTransit.model.Trip;

import java.time.LocalDate;

public interface TripDAO {
    Trip findById(long tripId);
    Trip findByRouteAndDate(int routeId, LocalDate date);
    boolean tripExists(int routeId, LocalDate date);
    void updateAvailableSeats(Trip trip);
}
