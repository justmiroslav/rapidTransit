package org.rapidTransit.service;

import org.rapidTransit.model.*;
import org.rapidTransit.dao.*;
import org.rapidTransit.util.Utils;

import java.util.List;
import java.util.Scanner;

public class ManageRatingsService {
    private final RouteDAO routeDAO;
    private final TripDAO tripDAO;
    private final UserDAO userDAO;
    private final RatingDAO ratingDAO;
    private final Scanner scanner;

    public ManageRatingsService(RouteDAO routeDAO, TripDAO tripDAO, UserDAO userDAO, RatingDAO ratingDAO) {
        this.routeDAO = routeDAO;
        this.tripDAO = tripDAO;
        this.userDAO = userDAO;
        this.ratingDAO = ratingDAO;
        this.scanner = new Scanner(System.in);
    }

    public void checkReportsProcess() {
        int choice = Utils.getDefaultChoice(scanner, "\n1-View User Ratings; 2-View Route Ratings; 3-Exit");

        switch (choice) {
            case 1 -> displayUserRatings();
            case 2 -> displayRouteRatings();
            case 3 -> {}
        }
    }

    private void displayUserRatings() {
        List<User> users = ratingDAO.getAllUsersWithRatings();
        if (users.isEmpty()) { System.out.println("No users with ratings found."); return; }
        displayUserListRatings(users);
        long userId = Utils.getValidNumber(scanner, "Enter user ID: ");
        if (userId == 0) return;
        displayUserRatingsInfo(userId);
    }

    private void displayRouteRatings() {
        List<Route> routes = ratingDAO.getAllRoutesWithRatings();
        if (routes.isEmpty()) { System.out.println("No routes with ratings found."); return; }
        displayRouteListRating(routes);
        int routeId = (int) Utils.getValidNumber(scanner, "Enter route ID: ");
        if (routeId == 0) return;
        displayRouteRatingsInfo(routeId);
    }

    private void displayUserListRatings(List<User> users) {
        users.forEach(user -> System.out.printf("%d: %s\n", user.getId(), user.getName()));
    }

    private void displayRouteListRating(List<Route> routes) {
        routes.forEach(route -> System.out.printf("%d: (%s - %s)\n", route.getId(), route.getDepartureCity(), route.getArrivalCity()));
    }

    private void displayUserRatingsInfo(long userId) {
        User user = userDAO.findById(userId);
        List<Rating> ratings = ratingDAO.findByUserId(userId);
        if (user == null || ratings.isEmpty()) {
            System.out.println("User not found or has no ratings.");
            return;
        }

        System.out.printf("User: %s, Average Rating: %.2f\n", user.getName(), calculateAverageRating(ratings));
        ratings.forEach(this::displayFinalUserRatingInfo);
    }

    private void displayFinalUserRatingInfo(Rating rating) {
        Trip trip = tripDAO.findById(rating.getTripId());
        Route route = routeDAO.findById(trip.getRouteId());
        System.out.printf("%d: (%s - %s) - Rating: %d, Comment: %s\n", route.getId(), route.getDepartureCity(), route.getArrivalCity(), rating.getRating(), rating.getComment());
    }

    private void displayRouteRatingsInfo(int routeId) {
        Route route = routeDAO.findById(routeId);
        List<Rating> ratings = ratingDAO.findByRouteId(routeId);
        if (route == null || ratings.isEmpty()) {
            System.out.println("Route not found or has no ratings.");
            return;
        }

        System.out.printf("Route: %s - %s, Average Rating: %.2f\n", route.getDepartureCity(), route.getArrivalCity(), calculateAverageRating(ratings));
        ratings.forEach(this::displayFinalRouteRatingInfo);
    }

    private void displayFinalRouteRatingInfo(Rating rating) {
        User user = userDAO.findById(rating.getUserId());
        System.out.printf("%d: %s - Rating: %d, Comment: %s\n", user.getId(), user.getName(), rating.getRating(), rating.getComment());
    }

    private double calculateAverageRating(List<Rating> ratings) {
        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0);
    }
}
