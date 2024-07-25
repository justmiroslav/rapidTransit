package org.rapidTransit.service;

import org.rapidTransit.model.*;
import org.rapidTransit.dao.*;
import org.rapidTransit.util.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import java.util.Scanner;

public class ManageRoutesService {
    private final RouteDAO routeDAO;
    private final TripDAO tripDAO;
    private final BusDAO busDAO;
    private final Scanner scanner;

    public ManageRoutesService(RouteDAO routeDAO, TripDAO tripDAO, BusDAO busDAO) {
        this.routeDAO = routeDAO;
        this.tripDAO = tripDAO;
        this.busDAO = busDAO;
        this.scanner = new Scanner(System.in);
    }

    public void manageRoutesProcess() {
        int choice = Utils.getDefaultChoice(scanner, "\n1-Change Route Time; 2-Add New Trips; 3-Exit");

        switch (choice) {
            case 1 -> changeRouteTime();
            case 2 -> addNewTrips();
            case 3 -> {}
        }
    }

    private void changeRouteTime() {
        displayRouteList();
        int routeId = (int) Utils.getValidNumber(scanner, "Enter route ID: ");
        if (routeId == 0) return;
        Route route = routeDAO.findById(routeId);

        displayCurrentTravelTime(route);
        float newTravelTime = (float) Utils.getValidNumber(scanner, "Enter new travel time: ");
        if (newTravelTime == 0) return;

        if (Math.abs(newTravelTime - route.getTravelTime()) > 1.5) {
            System.out.println("Travel time difference is too big.");
            return;
        }

        System.out.println("Route time updated successfully.");
        updateRoutesTime(route, newTravelTime);
    }

    private void displayRouteList() {
        List<Route> routes = routeDAO.getAllRoutes();
        routes.forEach(route -> System.out.printf("%d: (%s - %s)\n", route.getId(), route.getDepartureCity(), route.getArrivalCity()));
    }

    private void displayCurrentTravelTime(Route route) {
        int hours = (int) route.getTravelTime();
        int minutes = (int) ((route.getTravelTime() - hours) * 60);
        if (minutes == 0) System.out.printf("Current travel time takes %d hours.\n", hours);
        else System.out.printf("Current travel time takes %d hours and %02d minutes.\n", hours, minutes);
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
        LocalDate lastTripDate = trips.getLast().getTripDate();

        for (LocalDate date : List.of(lastTripDate.plusDays(1), lastTripDate.plusDays(2))) {
            trips.forEach(trip -> {
                if (trip.getTripDate().equals(date.minusDays(2))) addNewTrip(trip, date);
            });
        }
        System.out.println("New trips added successfully for the next two days.");
    }

    private void addNewTrip(Trip trip, LocalDate date) {
        Bus bus = busDAO.findById(trip.getBusId());
        tripDAO.save(new Trip(0, trip.getRouteId(), trip.getBusId(), date, trip.getDepartureTime(), trip.getArrivalTime(), generateAvailableSeats(bus.seatsCount())));
    }

    private List<Integer> generateAvailableSeats(int seatsCount) {
        return IntStream.rangeClosed(1, seatsCount).boxed().toList();
    }
}
