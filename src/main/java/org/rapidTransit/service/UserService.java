package org.rapidTransit.service;

import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.dao.UserDAOImpl;
import org.rapidTransit.model.User;

import java.util.Scanner;
import java.util.Optional;

public class UserService {
    private final User user;
    private final UserDAO userDAO;
    private final Scanner scanner;

    public UserService(User user) {
        this.user = user;
        this.userDAO = new UserDAOImpl();
        this.scanner = new Scanner(System.in);
    }

    public int getValidChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                scanner.nextLine();
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
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
}
