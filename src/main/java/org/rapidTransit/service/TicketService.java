package org.rapidTransit.service;

import org.rapidTransit.model.*;
import org.rapidTransit.dao.*;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.time.Year;

public class TicketService {
    private final TicketDAO ticketDAO;
    private final TripDAO tripDAO;
    private final RouteDAO routeDAO;
    private final BusDAO busDAO;
    private final Scanner scanner;
    private final int COEFFICIENT;

    public TicketService() {
        this.ticketDAO = new TicketDAOImpl();
        this.tripDAO = new TripDAOImpl();
        this.routeDAO = new RouteDAOImpl();
        this.busDAO = new BusDAOImpl();
        this.scanner = new Scanner(System.in);
        this.COEFFICIENT = 150;
    }

    public float purchaseTicketProcess(User user) {
        SimpleEntry<String, String> cities = selectCities(routeDAO.getUniqueCities());
        if (cities == null) return 0;

        LocalDate tripDate = selectDate();
        Route route = routeDAO.findRouteId(cities.getKey(), cities.getValue());

        Trip trip = findTrip(route, tripDate);
        if (trip == null) return 0;

        Bus bus = busDAO.findById(trip.getBusId());
        displayTripInfo(bus, trip);

        int seatNumber = selectSeat(trip);
        if (seatNumber == -1) return 0;

        if (!checkBalanceAndCalculatePrice(user, route)) return 0;

        float price = calculatePrice(route);
        purchaseAndDisplayTicket(user.getId(), seatNumber, price, bus, trip, route);
        return price;
    }

    private SimpleEntry<String, String> selectCities(List<String> availableCities) {
        System.out.println("Available cities: " + String.join(", ", availableCities));
        System.out.print("Enter departure city: ");
        String departureCity = scanner.nextLine();
        System.out.print("Enter arrival city: ");
        String arrivalCity = scanner.nextLine();

        if (!validateCities(availableCities, departureCity, arrivalCity)) {
            System.out.println("Invalid cities. Please try again.");
            return null;
        }
        return new SimpleEntry<>(departureCity, arrivalCity);
    }

    private LocalDate selectDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        LocalDate date = null;
        while (date == null) {
            System.out.print("Enter date (dd:MM): ");
            String input = scanner.nextLine() + ":" + Year.now().getValue();
            try {
                date = LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please try again using dd:MM format.");
            }
        }
        return date;
    }

    private Trip findTrip(Route route, LocalDate tripDate) {
        if (!tripDAO.tripExists(route.getId(), tripDate)) {
            System.out.println("No trips available on this date. Please try again.");
            return null;
        }
        return tripDAO.findByRouteAndDate(route.getId(), tripDate);
    }

    private int selectSeat(Trip trip) {
        System.out.print("Enter seat number: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Value must be an integer. Please try again.");
            scanner.next();
        }
        int seatNumber = scanner.nextInt();
        scanner.nextLine();
        if (!trip.getAvailableSeats().contains(seatNumber)) {
            System.out.println("Invalid seat number");
            return -1;
        }
        return seatNumber;
    }

    private boolean checkBalanceAndCalculatePrice(User user, Route route) {
        float price = calculatePrice(route);
        boolean isConfirmed = confirmPayment(price);
        if (!isConfirmed) return false;
        if (user.getBalance() < price) {
            System.out.println("Insufficient balance. Please add funds to your account.");
            return false;
        }
        return true;
    }

    private boolean confirmPayment(float price) {
        System.out.printf("This trip will cost you %.2f UAH. Are you ready to pay? (yes/no): ", price);
        String response = scanner.nextLine().trim().toLowerCase();
        while (!response.equals("yes") && !response.equals("no")) {
            System.out.print("Invalid input. Please enter 'yes' or 'no': ");
            response = scanner.nextLine().trim().toLowerCase();
        }
        return response.equals("yes");
    }

    private void purchaseAndDisplayTicket(long userId, int seatNumber, float price, Bus bus, Trip trip, Route route) {
        long ticketId = purchaseTicket(trip, userId, seatNumber, price);
        displayTicketInfo(ticketId, price, seatNumber, bus.getBusNumber(), trip, route);
    }

    private boolean validateCities(List<String> availableCities, String departureCity, String arrivalCity) {
        return !departureCity.equals(arrivalCity) && availableCities.contains(departureCity) && availableCities.contains(arrivalCity);
    }

    private void displayTripInfo(Bus bus, Trip trip) {
        System.out.println("\n--- Quick info about the trip ---");
        System.out.println("Bus number: " + bus.getBusNumber() + ", Trip time: (" + trip.getDepartureTime() + " - " + trip.getArrivalTime() + ")");
        System.out.println("Available seats: " + String.join(", ", trip.getAvailableSeats().toString()));
    }

    private float calculatePrice(Route route) {
        return route.getTravelTime() * COEFFICIENT;
    }

    private long purchaseTicket(Trip trip, long userId, int seatNumber, float price) {
        updateAvailableSeats(trip, seatNumber);
        Ticket ticket = new Ticket(0, trip.getTripId(), userId, seatNumber, price);
        ticketDAO.save(ticket);
        return ticket.getTicketId();
    }

    private void updateAvailableSeats(Trip trip, int seatNumber) {
        trip.setAvailableSeats(trip.getAvailableSeats().stream().filter(seat -> seat != seatNumber).toList());
        tripDAO.updateAvailableSeats(trip);
    }

    private void displayTicketInfo(long ticketId, float price, int seatNumber, String busNumber, Trip trip, Route route) {
        System.out.println("\n--- Ticket Information ---");
        System.out.printf("Ticket number: %d, Price: %.2f UAH\n", ticketId, price);
        System.out.printf("Route: %s - %s, Date: %s, (%s - %s)\n", route.getDepartureCity(), route.getArrivalCity(), trip.getTripDate(), trip.getDepartureTime(), trip.getArrivalTime());
        System.out.printf("Bus number: %s, Seat: %d\n", busNumber, seatNumber);
        System.out.println("\nThank you for choosing RapidTransit! Have a safe and pleasant journey.");
        System.out.println("     _____              _     _ _______                  _ _   ");
        System.out.println("    |  __ \\            (_)   | |__   __|                (_) |  ");
        System.out.println("    | |__) |__ _ _ __   _  __| |  | |_ __ __ _ _ __  ___ _| |_ ");
        System.out.println("    |  _  // _` | '_ \\ | |/ _` |  | | '__/ _` | '_ \\/ __| | __|");
        System.out.println("    | | \\ \\ (_| | |_) || | (_| |  | | | | (_| | | | \\__ \\ | |_ ");
        System.out.println("    |_|  \\_\\__,_| .__/ |_|\\__,_|  |_|_|  \\__,_|_| |_|___/_|\\__|");
        System.out.println("                | |                                            ");
        System.out.println("                |_|                                            ");
    }
}
