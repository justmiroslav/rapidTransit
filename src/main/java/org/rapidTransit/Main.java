package org.rapidTransit;

import org.rapidTransit.dao.*;
import org.rapidTransit.model.User;
import org.rapidTransit.model.Admin;
import org.rapidTransit.service.AuthenticationService;
import org.rapidTransit.ui.UserMenu;
import org.rapidTransit.ui.AdminMenu;

public class Main {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAOImpl();
        AdminDAO adminDAO = new AdminDAOImpl();
        TripDAO tripDAO = new TripDAOImpl();
        TicketDAO ticketDAO = new TicketDAOImpl();
        RouteDAO routeDAO = new RouteDAOImpl();
        BusDAO busDAO = new BusDAOImpl();
        RatingDAO ratingDAO = new RatingDAOImpl();
        AuthenticationService authService = new AuthenticationService(userDAO, adminDAO);

        while (true) {
            Object person = authService.authenticate(args);
            args = new String[0];

            if (person instanceof Admin) {
                if (!new AdminMenu((Admin) person, routeDAO, busDAO, tripDAO, userDAO, ratingDAO).show()) {
                    break;
                }
            } else if (person instanceof User) {
                if (!new UserMenu((User) person, routeDAO, busDAO, tripDAO, userDAO, ticketDAO, ratingDAO).show()) {
                    break;
                }
            } else {
                System.out.println("Authentication failed. Exiting...");
                break;
            }
        }
    }
}
