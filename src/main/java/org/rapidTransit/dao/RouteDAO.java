package org.rapidTransit.dao;

import org.rapidTransit.model.Route;

import java.util.List;

public interface RouteDAO {
    Route findById(int id);
    Route findRouteId(String departureCity, String arrivalCity);
    List<String> getUniqueCities();
}
