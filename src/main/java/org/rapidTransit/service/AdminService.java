package org.rapidTransit.service;

import org.rapidTransit.model.*;
import org.rapidTransit.dao.*;
import org.rapidTransit.util.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class AdminService {
    private final RouteDAO routeDAO;
    private final BusDAO busDAO;
    private final TripDAO tripDAO;
    private final UserDAO userDAO;
    private final RatingDAO ratingDAO;
    private final Scanner scanner;

    public AdminService(RouteDAO routeDAO, BusDAO busDAO, TripDAO tripDAO, UserDAO userDAO, RatingDAO ratingDAO) {
        this.routeDAO = routeDAO;
        this.busDAO = busDAO;
        this.tripDAO = tripDAO;
        this.userDAO = userDAO;
        this.ratingDAO = ratingDAO;
        this.scanner = new Scanner(System.in);
    }

    public int getValidChoice(int min, int max) {
        return Utils.getValidChoice(min, max, scanner);
    }

    public void manageUsersProcess() {
        displayUserList();
        int choice = getChoice("\n1-Block User; 2-Unblock User; 3-Exit");
        if (choice == 3) return;

        long userId = getValidUserId();
        if (userId == 0) return;
        User user = userDAO.findById(userId);

        switch (choice) {
            case 1 -> toggleUserBlock(user, true);
            case 2 -> toggleUserBlock(user, false);
        }
    }

    public void manageRoutesProcess() {
        int choice = getChoice("\n1-Change Route Time; 2-Add New Trips; 3-Exit");

        switch (choice) {
            case 1 -> changeRouteTime();
            case 2 -> addNewTrips();
            case 3 -> {}
        }
    }

    public void checkReportsProcess() {
        int choice = getChoice("\n1-View User Ratings; 2-View Route Ratings; 3-Exit");

        switch (choice) {
            case 1 -> displayUserRatings();
            case 2 -> displayRouteRatings();
            case 3 -> {}
        }
    }

    private long getValidUserId() {
        System.out.print("Enter user ID: ");
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number");
            return 0;
        }
    }

    private void displayUserList() {
        List<User> users = userDAO.getAllUsers();
        users.forEach(user -> System.out.printf("%d: (%s%s)\n", user.getId(), user.isBlocked() ? "❌" : "✅", user.getName()));
    }

    private void displayRouteList() {
        List<Route> routes = routeDAO.getAllRoutes();
        routes.forEach(route -> System.out.printf("%d: (%s - %s)\n", route.getId(), route.getDepartureCity(), route.getArrivalCity()));
    }

    private int getChoice(String message) {
        System.out.println(message);
        System.out.print("Please select an option: ");
        return getValidChoice(1, 3);
    }

    private void toggleUserBlock(User user, boolean block) {
        if (user == null || user.isBlocked() == block) {
            System.out.println("User not found or already " + (block ? "blocked." : "unblocked."));
            return;
        }
        user.setBlocked(block);
        userDAO.update(user);
        System.out.println("User " + (block ? "blocked" : "unblocked") + " successfully.");
    }

    private void changeRouteTime() {
        displayRouteList();
        int routeId = getValidRouteId();
        if (routeId == 0) return;
        Route route = routeDAO.findById(routeId);

        displayCurrentTravelTime(route);
        float newTravelTime = getNewTravelTime();
        if (newTravelTime == 0) return;

        if (Math.abs(newTravelTime - route.getTravelTime()) > 1.5) {
            System.out.println("Travel time difference is too big.");
            return;
        }

        System.out.println("Route time updated successfully.");
        updateRoutesTime(route, newTravelTime);
    }

    private int getValidRouteId() {
        System.out.print("Enter route ID: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number");
            return 0;
        }
    }

    private void displayCurrentTravelTime(Route route) {
        int hours = (int) route.getTravelTime();
        int minutes = (int) ((route.getTravelTime() - hours) * 60);
        System.out.printf("Current travel time takes %d hours and %02d minutes.\n", hours, minutes);
    }

    private float getNewTravelTime() {
        System.out.print("Enter new travel time (float number): ");
        try {
            return Float.parseFloat(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number");
            return 0;
        }
    }

    private void updateRoutesTime(Route route, float newTravelTime) {
        updateSingleRoute(route, newTravelTime);
        Route reverseRoute = routeDAO.findRouteId(route.getArrivalCity(), route.getDepartureCity());
        updateSingleRoute(reverseRoute, newTravelTime);
    }

    private void updateSingleRoute(Route route, float newTravelTime) {
        route.setTravelTime(newTravelTime);
        routeDAO.update(route);
        updateTripsTime(route, newTravelTime);
    }

    private void updateTripsTime(Route route, float newTravelTime) {
        List<Trip> trips = tripDAO.findByRouteId(route.getId());
        trips.forEach(trip -> updateTripTime(trip, newTravelTime));
        displayNewTripTime(route, trips);
    }

    private void updateTripTime(Trip trip, float newTravelTime) {
        LocalTime arrivalTime = calculateNewArrivalTime(trip.getDepartureTime(), newTravelTime);
        if (arrivalTime.equals(LocalTime.of(23, 0)) || arrivalTime.equals(LocalTime.of(5, 0))) {
            trip.setDepartureTime(arrivalTime.minusMinutes((long) (newTravelTime * 60)));
        }
        trip.setArrivalTime(arrivalTime);
        tripDAO.update(trip);
    }

    private LocalTime calculateNewArrivalTime(LocalTime departureTime, float travelTime) {
        LocalTime arrivalTime = departureTime.plusMinutes((long) (travelTime * 60));
        if (arrivalTime.isAfter(LocalTime.of(23, 0)) || arrivalTime.isBefore(LocalTime.of(5, 0))) {
            return arrivalTime.isAfter(LocalTime.of(2, 0)) ? LocalTime.of(5, 0) : LocalTime.of(23, 0);
        }
        return arrivalTime;
    }

    private void displayNewTripTime(Route route, List<Trip> trips) {
        trips.stream().findFirst().ifPresent(trip -> System.out.printf("New (%s - %s) trip time: %s - %s\n",
                route.getDepartureCity(), route.getArrivalCity(), trip.getDepartureTime(), trip.getArrivalTime()));
    }

    private void addNewTrips() {
        List<Trip> trips = tripDAO.getLastTrips();
        LocalDate lastTripDate = trips.get(trips.size() - 1).getTripDate();

        for (LocalDate date : List.of(lastTripDate.plusDays(1), lastTripDate.plusDays(2))) {
            trips.forEach(trip -> {
                if (trip.getTripDate().equals(date.minusDays(2))) addNewTrip(trip, date);
            });
        }
        System.out.println("New trips added successfully for the next two days.");
    }

    private void addNewTrip(Trip trip, LocalDate date) {
        Bus bus = busDAO.findById(trip.getBusId());
        tripDAO.save(new Trip(0, trip.getRouteId(), trip.getBusId(), date, trip.getDepartureTime(), trip.getArrivalTime(), generateAvailableSeats(bus.getSeatsCount())));
    }

    private List<Integer> generateAvailableSeats(int seatsCount) {
        return IntStream.rangeClosed(1, seatsCount).boxed().toList();
    }

    private void displayUserRatings() {
        List<User> users = ratingDAO.getAllUsersWithRatings();
        if (users.isEmpty()) { System.out.println("No users with ratings found."); return; }
        displayUserListRatings(users);
        long userId = getValidUserId();
        if (userId == 0) return;
        displayUserRatingsInfo(userId);
    }

    private void displayRouteRatings() {
        List<Route> routes = ratingDAO.getAllRoutesWithRatings();
        if (routes.isEmpty()) { System.out.println("No routes with ratings found."); return; }
        displayRouteListRating(routes);
        int routeId = getValidRouteId();
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
