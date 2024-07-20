package org.rapidTransit.ui;

import org.rapidTransit.model.Admin;

import java.util.Scanner;

public class AdminMenu {
    private final Admin admin;

    public AdminMenu(Admin admin) {
        this.admin = admin;
    }

    public boolean show() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to RapidTransit Admin Panel, " + admin.getName() + "!");
        System.out.println("You have full access to manage the system. Please use your powers responsibly.");

        while (true) {
            System.out.println("\nPlease select an option:");
            System.out.println("1-Manage Users; 2-Manage Routes; 3-Check Reports; 4-Log out; 5-Exit");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (1 <= choice && choice <= 3) System.out.println("Under construction. Please check back later.");
            else if (choice == 4) { System.out.println("Logging out..."); return true; }
            else if (choice == 5) { System.out.println("Thank you for using RapidTransit. Goodbye!"); return false; }
            else System.out.println("Invalid option. Please try again.");
        }
    }
}
