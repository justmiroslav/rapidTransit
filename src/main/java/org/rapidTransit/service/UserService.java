package org.rapidTransit.service;

import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.model.User;
import org.rapidTransit.util.Utils;

import java.util.Scanner;

public class UserService {
    private final User user;
    private final UserDAO userDAO;
    private final Scanner scanner;

    public UserService(User user, UserDAO userDAO) {
        this.user = user;
        this.userDAO = userDAO;
        this.scanner = new Scanner(System.in);
    }

    public void updateUserName(String newName) {
        user.setName(newName);
        userDAO.update(user);
    }

    public void updateUserPassword(String newPassword) {
        user.setPassword(newPassword);
        userDAO.update(user);
    }

    public void updateBalance(float amount) {
        user.setBalance(user.getBalance() + amount);
        userDAO.update(user);
    }

    public void manageAccountProcess() {
        displayUserInfo();
        int choice = Utils.getChoice(scanner, "\n1-Change Name; 2-Change Password; 3-Update balance; 4-Exit", 4);
        switch (choice) {
            case 1 -> handleChangeName();
            case 2 -> handleChangePassword();
            case 3 -> handleUpdateBalance();
            case 4 -> {}
        }
    }

    private void displayUserInfo() {
        System.out.println("--- My Account ---");
        System.out.println(STR."Name: \{user.getName()}");
        System.out.println(STR."Email: \{user.getEmail()}");
        System.out.println("Password: *****");
        System.out.println(STR."Balance: \{user.getBalance()} UAH");
    }

    private void handleChangeName() {
        String newName = Utils.getValidString(scanner, "Enter new name: ");
        updateUserName(newName);
        System.out.println("Name updated successfully.");
    }

    private void handleChangePassword() {
        String currentPassword = Utils.getValidString(scanner, "Enter current password: ");
        if (user.getPassword().equals(currentPassword)) {
            String newPassword = getNewPassword();
            updateUserPassword(newPassword);
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Incorrect current password.");
        }
    }

    private String getNewPassword() {
        while (true) {
            String newPassword = Utils.getValidString(scanner, "Enter new password: ");
            String confirmPassword = Utils.getValidString(scanner, "Confirm new password: ");
            if (newPassword.equals(confirmPassword)) {
                return newPassword;
            }
            System.out.println("Passwords do not match. Please try again.");
        }
    }

    private void handleUpdateBalance() {
        displayBalanceInfo("Your current");
        float amount = getValidAmount();
        if (amount > 0) {
            updateBalance(amount);
            displayBalanceInfo("Balance updated successfully. Your new");
        } else {
            System.out.println("Invalid amount. Balance update cancelled.");
        }
    }

    private void displayBalanceInfo(String action) {
        System.out.printf("%s balance is: %.2f UAH\n", action, user.getBalance());
    }

    private float getValidAmount() {
        String input = Utils.getValidString(scanner, "Enter the amount to add (or 'exit' to cancel): ");
        if (input.equalsIgnoreCase("exit")) {
            return 0;
        }

        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
