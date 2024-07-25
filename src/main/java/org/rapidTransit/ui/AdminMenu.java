package org.rapidTransit.ui;

import org.rapidTransit.model.Admin;
import org.rapidTransit.dao.*;
import org.rapidTransit.service.ManageRatingsService;
import org.rapidTransit.service.ManageRoutesService;
import org.rapidTransit.service.ManageUsersService;
import org.rapidTransit.util.Utils;

import java.util.Scanner;

public class AdminMenu {
    private final Admin admin;
    private final ManageUsersService manageUsersService;
    private final ManageRoutesService manageRoutesService;
    private final ManageRatingsService manageRatingsService;

    public AdminMenu(Admin admin, RouteDAO routeDAO, BusDAO busDAO, TripDAO tripDAO, UserDAO userDAO, RatingDAO ratingDAO) {
        this.admin = admin;
        this.manageUsersService = new ManageUsersService(userDAO);
        this.manageRoutesService = new ManageRoutesService(routeDAO, tripDAO, busDAO);
        this.manageRatingsService = new ManageRatingsService(routeDAO, tripDAO, userDAO, ratingDAO);
    }

    public boolean show() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(STR."Welcome to RapidTransit Admin Panel, \{admin.getName()}!");
        System.out.println("You have full access to manage the system. Please use your powers responsibly.");

        while (true) {
            int choice = Utils.getChoice(scanner, "\n1-Manage Users; 2-Manage Routes; 3-Check Reports; 4-Log out; 5-Exit", 5);

            switch (choice) {
                case 1 -> handleManageUsers();
                case 2 -> handleManageRoutes();
                case 3 -> handleCheckReports();
                case 4 -> { System.out.println("Logging out..."); return true; }
                case 5 -> { System.out.println("Thank you for using RapidTransit. Goodbye!"); return false; }
            }
        }
    }

    private void handleManageUsers() {
        manageUsersService.manageUsersProcess();
    }

    private void handleManageRoutes() {
        manageRoutesService.manageRoutesProcess();
    }

    private void handleCheckReports() {
        manageRatingsService.checkReportsProcess();
    }
}
