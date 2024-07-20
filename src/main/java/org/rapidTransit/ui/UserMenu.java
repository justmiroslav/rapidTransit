package org.rapidTransit.ui;

import org.rapidTransit.model.User;
import org.rapidTransit.service.UserService;
import org.rapidTransit.service.TicketService;
import org.rapidTransit.service.TripService;
import org.rapidTransit.dao.*;

public class UserMenu {
    private final User user;
    private final TicketService ticketService;
    private final UserService userService;
    private final TripService tripService;

    public UserMenu(User user, RouteDAO routeDAO, BusDAO busDAO, TripDAO tripDAO, UserDAO userDAO, TicketDAO ticketDAO, RatingDAO ratingDAO) {
        this.user = user;
        this.userService = new UserService(user, userDAO);
        this.ticketService = new TicketService(user, routeDAO, busDAO, tripDAO, ticketDAO);
        this.tripService = new TripService(user, routeDAO, busDAO, tripDAO, ticketDAO, ratingDAO);
    }

    public boolean show() {
        System.out.println("Welcome to RapidTransit, " + user.getName() + "!");
        System.out.println("We're glad to have you on board. Enjoy comfortable and efficient bus travel across Ukraine.");

        while (true) {
            displayMainMenu();
            int choice = userService.getValidChoice(1, 7);

            if (choice == 1) handlePurchaseTickets();
            else if (choice == 2) handleUpdateBalance();
            else if (choice == 3) handleTripsHistory();
            else if (choice == 4) handleManageAccount();
            else if (choice == 5) System.out.println(getAboutUsText());
            else if (choice == 6) { System.out.println("Logging out..."); return true; }
            else if (choice == 7) { System.out.println("Thank you for using RapidTransit. Goodbye!"); return false; }
            else System.out.println("Invalid option. Please try again.");
        }
    }

    private void displayMainMenu() {
        System.out.println("\n1-Purchase Tickets; 2-Update balance; 3-Trips History; 4-Manage Account; 5-About Us; 6-Log out; 7-Exit");
        System.out.print("Please select an option: ");
    }

    private void handlePurchaseTickets() {
        float price = ticketService.purchaseTicketProcess();
        if (price > 0) userService.updateBalance(-price);
    }

    private void handleUpdateBalance() {
        userService.updateBalanceProcess();
    }

    private void handleTripsHistory() {
        tripService.tripsHistoryProcess();
    }

    private void handleManageAccount() {
        userService.manageAccountProcess();
    }

    private String getAboutUsText() {
        return """
               RapidTransit: Your Gateway to Ukrainian Adventures

               Founded in 2020, RapidTransit has quickly become Ukraine's premier intercity bus service.
               We connect the vibrant cities of Kyiv, Lviv, Kharkiv, Odessa, Dnipro, Vinnytsia, and Sumy
               with comfort, reliability, and efficiency.

               Our fleet of modern buses ensures a smooth journey, while our dedicated team works
               tirelessly to provide you with the best travel experience. Whether you're a local
               commuter or an adventurous tourist, RapidTransit is your trusted partner in exploring
               the rich cultural tapestry of Ukraine.

               At RapidTransit, we believe in more than just transportation – we're about connecting
               people, cultures, and dreams. Join us on a journey across Ukraine, where every trip
               is an opportunity for new discoveries and unforgettable memories.

               RapidTransit: Connecting Ukraine, One Journey at a Time.""";
    }
}
