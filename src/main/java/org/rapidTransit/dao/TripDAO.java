package org.rapidTransit.dao;

import org.rapidTransit.model.Trip;

import java.time.LocalDate;
import java.util.List;

public interface TripDAO {
    Trip findById(long tripId);
    Trip findByRouteAndDate(int routeId, LocalDate date);
    List<Trip> findByRouteId(int routeId);
    List<Trip> getLastTrips();
    List<LocalDate> getUniqueDates(int routeId);
    boolean tripExists(int routeId, LocalDate date);
    void update(Trip trip);
    void save(Trip trip);
}
