package org.rapidTransit.service;

import org.rapidTransit.model.*;
import org.rapidTransit.dao.*;
import org.rapidTransit.util.Utils;

import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.time.Year;
import java.util.stream.Collectors;

public class TicketService {
    private final User user;
    private final RouteDAO routeDAO;
    private final BusDAO busDAO;
    private final TripDAO tripDAO;
    private final TicketDAO ticketDAO;
    private final Scanner scanner;
    private final int COEFFICIENT;

    public TicketService(User user, RouteDAO routeDAO, BusDAO busDAO, TripDAO tripDAO, TicketDAO ticketDAO) {
        this.user = user;
        this.routeDAO = routeDAO;
        this.busDAO = busDAO;
        this.tripDAO = tripDAO;
        this.ticketDAO = ticketDAO;
        this.scanner = new Scanner(System.in);
        this.COEFFICIENT = 150;
    }

    public float purchaseTicketProcess() {
        SimpleEntry<String, String> cities = selectCities(routeDAO.getUniqueCities());
        if (cities == null) return 0;

        Route route = routeDAO.findRouteId(cities.getKey(), cities.getValue());
        LocalDate tripDate = selectDate(route.getId());
        if (tripDate == null) return 0;

        Trip trip = findTrip(route, tripDate);
        if (trip == null) return 0;

        Bus bus = busDAO.findById(trip.getBusId());
        displayTripInfo(bus, trip);

        int seatNumber = selectSeat(trip);
        if (seatNumber == 0) return 0;

        float price = checkBalanceAndCalculatePrice(route);
        if (price == 0) return 0;

        purchaseAndDisplayTicket(seatNumber, price, bus, trip, route);
        return price;
    }

    private SimpleEntry<String, String> selectCities(List<String> availableCities) {
        System.out.println(STR."Available cities: \{String.join(", ", availableCities)}");
        String departureCity = Utils.getValidString(scanner, "Enter departure city: ");
        String arrivalCity = Utils.getValidString(scanner, "Enter arrival city: ");

        if (!validateCities(availableCities, departureCity, arrivalCity)) {
            System.out.println("Invalid cities. Please try again.");
            return null;
        }
        return new SimpleEntry<>(departureCity, arrivalCity);
    }

    private LocalDate selectDate(int routeId) {
        boolean isOk = displayValidDates(tripDAO.getUniqueDates(routeId));
        if (!isOk) return null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM:yyyy");
        LocalDate date = null;
        while (date == null) {
            String input = STR."\{Utils.getValidString(scanner, "Enter date (dd:MM): ")}:\{Year.now().getValue()}";
            try {
                date = LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please try again using dd:MM format.");
            }
        }
        return date;
    }

    private boolean displayValidDates(List<LocalDate> dates) {
        if (dates.isEmpty()) {
            System.out.println("No trips available for this route. Please try again.");
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM");
        String formattedDates = dates.stream().map(date -> date.format(formatter)).collect(Collectors.joining(", "));
        System.out.println(STR."Available dates: \{formattedDates}");
        return true;
    }

    private Trip findTrip(Route route, LocalDate tripDate) {
        if (!tripDAO.tripExists(route.getId(), tripDate)) {
            System.out.println("No trips available on this date. Please try again.");
            return null;
        }
        return tripDAO.findByRouteAndDate(route.getId(), tripDate);
    }

    private int selectSeat(Trip trip) {
        int seatNumber = (int) Utils.getValidNumber(scanner, "Enter seat number: ");
        if (!trip.getAvailableSeats().contains(seatNumber)) {
            System.out.println("Invalid seat number");
            return 0;
        }
        return seatNumber;
    }

    private float checkBalanceAndCalculatePrice(Route route) {
        float price = route.getTravelTime() * COEFFICIENT;
        boolean isConfirmed = confirmPayment(price);
        if (!isConfirmed) return 0;
        if (user.getBalance() < price) {
            System.out.println("Insufficient balance. Please add funds to your account.");
            return 0;
        }
        return price;
    }

    private boolean confirmPayment(float price) {
        String response = Utils.getValidString(scanner, STR."This trip will cost you \{price} UAH. Are you ready to pay? (yes/no): ").trim().toLowerCase();
        while (!response.equals("yes") && !response.equals("no")) {
            response = Utils.getValidString(scanner, "Invalid input. Please enter 'yes' or 'no': ").trim().toLowerCase();
        }
        return response.equals("yes");
    }

    private void purchaseAndDisplayTicket(int seatNumber, float price, Bus bus, Trip trip, Route route) {
        long ticketId = purchaseTicket(trip, seatNumber, price);
        displayTicketInfo(ticketId, price, seatNumber, bus.busNumber(), trip, route);
    }

    private boolean validateCities(List<String> availableCities, String departureCity, String arrivalCity) {
        return !departureCity.equals(arrivalCity) && availableCities.contains(departureCity) && availableCities.contains(arrivalCity);
    }

    private void displayTripInfo(Bus bus, Trip trip) {
        System.out.println("\n--- Quick info about the trip ---");
        System.out.println(STR."Bus number: \{bus.busNumber()}, Trip time: (\{trip.getDepartureTime()} - \{trip.getArrivalTime()})");
        System.out.println(STR."Available seats: \{String.join(", ", trip.getAvailableSeats().toString())}");
    }

    private long purchaseTicket(Trip trip, int seatNumber, float price) {
        updateAvailableSeats(trip, seatNumber);
        Ticket ticket = new Ticket(0, trip.getTripId(), user.getId(), seatNumber, price);
        ticketDAO.save(ticket);
        return ticket.getTicketId();
    }

    private void updateAvailableSeats(Trip trip, int seatNumber) {
        trip.setAvailableSeats(trip.getAvailableSeats().stream().filter(seat -> seat != seatNumber).toList());
        tripDAO.update(trip);
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
