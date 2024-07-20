package org.rapidTransit.service;

import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.model.User;
import org.rapidTransit.util.Utils;

import java.util.Scanner;
import java.util.Optional;

public class UserService {
    private final User user;
    private final UserDAO userDAO;
    private final Scanner scanner;

    public UserService(User user, UserDAO userDAO) {
        this.user = user;
        this.userDAO = userDAO;
        this.scanner = new Scanner(System.in);
    }

    public int getValidChoice(int min, int max) {
        return Utils.getValidChoice(min, max, scanner);
    }

    public void updateBalance(float amount) {
        user.setBalance(user.getBalance() + amount);
        userDAO.update(user);
    }

    public void updateBalanceProcess() {
        displayBalanceInfo("Your current");
        float amount = getValidAmount();
        if (amount > 0) {
            updateBalance(amount);
            displayBalanceInfo("Balance updated successfully. Your new");
        }
    }

    public void manageAccountProcess() {
        displayUserInfo();
        int choice = getManageAccountChoice();
        switch (choice) {
            case 1 -> handleChangeName();
            case 2 -> handleChangePassword();
            case 3 -> {}
        }
    }

    private void displayBalanceInfo(String action) {
        System.out.printf("%s balance is: %.2f UAH\n", action, user.getBalance());
    }

    private float getValidAmount() {
        while (true) {
            String input = readUserInput();
            if (input.equalsIgnoreCase("exit")) {
                return 0;
            }
            Optional<Float> amount = parseAmount(input);
            if (amount.isPresent() && isPositiveAmount(amount.get())) {
                return amount.get();
            }
        }
    }

    private String readUserInput() {
        System.out.print("Enter the amount to add (or 'exit' to cancel): ");
        return scanner.nextLine();
    }

    private Optional<Float> parseAmount(String input) {
        try {
            return Optional.of(Float.parseFloat(input));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return Optional.empty();
        }
    }

    private boolean isPositiveAmount(float amount) {
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return false;
        }
        return true;
    }

    private void displayUserInfo() {
        System.out.println("--- My Account ---");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Password: *****");
        System.out.println("Balance: " + user.getBalance() + " UAH");
    }

    private int getManageAccountChoice() {
        System.out.println("\n1-Change Name; 2-Change Password; 3-Exit");
        System.out.print("Please select an option: ");
        return getValidChoice(1, 3);
    }

    private void handleChangeName() {
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        updateUserName(newName);
        System.out.println("Name updated successfully.");
    }

    private void handleChangePassword() {
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
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
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();
            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();
            if (newPassword.equals(confirmPassword)) {
                return newPassword;
            }
            System.out.println("Passwords do not match. Please try again.");
        }
    }

    private void updateUserName(String newName) {
        user.setName(newName);
        userDAO.update(user);
    }

    private void updateUserPassword(String newPassword) {
        user.setPassword(newPassword);
        userDAO.update(user);
    }
}
