package org.rapidTransit.ui;

import org.rapidTransit.model.Admin;
import org.rapidTransit.dao.*;
import org.rapidTransit.service.AdminService;


public class AdminMenu {
    private final Admin admin;
    private final AdminService adminService;

    public AdminMenu(Admin admin, RouteDAO routeDAO, BusDAO busDAO, TripDAO tripDAO, UserDAO userDAO, RatingDAO ratingDAO) {
        this.admin = admin;
        this.adminService = new AdminService(routeDAO, busDAO, tripDAO, userDAO, ratingDAO);
    }

    public boolean show() {
        System.out.println("Welcome to RapidTransit Admin Panel, " + admin.getName() + "!");
        System.out.println("You have full access to manage the system. Please use your powers responsibly.");

        while (true) {
            displayMainMenu();
            int choice = adminService.getValidChoice(1, 5);

            switch (choice) {
                case 1 -> handleManageUsers();
                case 2 -> handleManageRoutes();
                case 3 -> handleCheckReports();
                case 4 -> { System.out.println("Logging out..."); return true; }
                case 5 -> { System.out.println("Thank you for using RapidTransit. Goodbye!"); return false; }
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n1-Manage Users; 2-Manage Routes; 3-Check Reports; 4-Log out; 5-Exit");
        System.out.print("Please select an option: ");
    }

    private void handleManageUsers() {
        adminService.manageUsersProcess();
    }

    private void handleManageRoutes() {
        adminService.manageRoutesProcess();
    }

    private void handleCheckReports() {
        adminService.checkReportsProcess();
    }
}
