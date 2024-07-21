package org.rapidTransit.dao;

import org.rapidTransit.model.Rating;
import org.rapidTransit.model.Route;
import org.rapidTransit.model.User;

import java.util.List;

public interface RatingDAO {
    void save(Rating rating);
    List<Rating> findByUserId(long userId);
    List<Rating> findByRouteId(int routeId);
    List<User> getAllUsersWithRatings();
    List<Route> getAllRoutesWithRatings();
}
