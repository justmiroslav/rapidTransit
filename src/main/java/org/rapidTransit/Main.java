package org.rapidTransit;

import org.rapidTransit.db.DatabaseConnection;
import org.rapidTransit.dao.*;
import org.rapidTransit.model.User;
import org.rapidTransit.model.Admin;
import org.rapidTransit.service.AuthenticationService;
import org.rapidTransit.ui.UserMenu;
import org.rapidTransit.ui.AdminMenu;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        UserDAO userDAO = new UserDAOImpl(connection);
        AdminDAO adminDAO = new AdminDAOImpl(connection);
        TripDAO tripDAO = new TripDAOImpl(connection);
        TicketDAO ticketDAO = new TicketDAOImpl(connection);
        RouteDAO routeDAO = new RouteDAOImpl(connection);
        BusDAO busDAO = new BusDAOImpl(connection);
        RatingDAO ratingDAO = new RatingDAOImpl(connection);
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
