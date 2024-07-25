package org.rapidTransit.service;

import org.rapidTransit.dao.*;
import org.rapidTransit.model.*;
import org.rapidTransit.util.Utils;

import java.util.List;
import java.util.Scanner;

public class TripService {
    private final User user;
    private final RouteDAO routeDAO;
    private final BusDAO busDAO;
    private final TripDAO tripDAO;
    private final TicketDAO ticketDAO;
    private final RatingDAO ratingDAO;
    private final Scanner scanner;

    public TripService(User user, RouteDAO routeDAO, BusDAO busDAO, TripDAO tripDAO, TicketDAO ticketDAO, RatingDAO ratingDAO) {
        this.user = user;
        this.routeDAO = routeDAO;
        this.busDAO = busDAO;
        this.tripDAO = tripDAO;
        this.ticketDAO = ticketDAO;
        this.ratingDAO = ratingDAO;
        this.scanner = new Scanner(System.in);
    }

    public void tripsHistoryProcess() {
        if (!hasTrips()) {
            System.out.println("You haven't been in any trip yet");
            return;
        }
        displayTripHistory();
        handleTripHistoryCommands();
    }

    private boolean hasTrips() {
        return ticketDAO.hasUserTickets(user.getId());
    }

    private void displayTripHistory() {
        List<Ticket> tickets = ticketDAO.findByUserId(user.getId());
        tickets.forEach(this::displayTripInfo);
    }

    private void displayTripInfo(Ticket ticket) {
        Trip trip = tripDAO.findById(ticket.getTripId());
        Route route = routeDAO.findById(trip.getRouteId());
        System.out.printf("Trip number %d - (%s - %s), date: %s, ticket: %d\n",
                trip.getTripId(), route.getDepartureCity(), route.getArrivalCity(), trip.getTripDate(), ticket.getTicketId());
    }

    private void handleTripHistoryCommands() {
        int choice = Utils.getDefaultChoice(scanner, "\n1-Ticket Details; 2-Rate Trip; 3-Exit");
        switch (choice) {
            case 1 -> displayTicketDetails();
            case 2 -> rateTrip();
            case 3 -> {}
        }
    }

    private void displayTicketDetails() {
        long ticketId = Utils.getValidNumber(scanner, "Enter ticket id: ");
        Ticket ticket = ticketDAO.findById(ticketId);
        if (ticket == null) {
            System.out.println("Ticket not found");
            return;
        }
        displayTicketInfo(ticket);
    }

    private void displayTicketInfo(Ticket ticket) {
        Trip trip = tripDAO.findById(ticket.getTripId());
        Route route = routeDAO.findById(trip.getRouteId());
        Bus bus = busDAO.findById(trip.getBusId());
        System.out.println("\n--- Ticket Information ---");
        System.out.printf("Ticket number: %d, Price: %.2f UAH\n", ticket.getTicketId(), ticket.getTicketPrice());
        System.out.printf("Route: %s - %s, Date: %s, (%s - %s)\n", route.getDepartureCity(), route.getArrivalCity(), trip.getTripDate(), trip.getDepartureTime(), trip.getArrivalTime());
        System.out.printf("Bus number: %s, Seat: %d\n", bus.busNumber(), ticket.getSeatNumber());
    }

    private void rateTrip() {
        long tripId = Utils.getValidNumber(scanner, "Enter trip id: ");
        if (tripId == 0) return;
        if (ticketDAO.findByTripAndUser(tripId, user.getId()) == null) {
            System.out.println("You haven't been in this trip");
            return;
        }
        System.out.print("Enter rating (1-5): ");
        int rating = Utils.getValidChoice(1, 5, scanner);
        String comment = getComment();
        ratingDAO.save(new Rating(0, tripId, user.getId(), rating, comment));
        System.out.println("Thank you for sharing your trip experience!");
    }

    private String getComment() {
        String comment = Utils.getValidString(scanner, "Enter comment (or '-' for default): ");
        return comment.equals("-") ? "The rating I provided speaks for itself" : comment;
    }
}
