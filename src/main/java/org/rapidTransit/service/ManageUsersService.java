package org.rapidTransit.service;

import org.rapidTransit.model.User;
import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.util.Utils;

import java.util.List;
import java.util.Scanner;

public class ManageUsersService {
    private final UserDAO userDAO;

    public ManageUsersService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void manageUsersProcess() {
        Scanner scanner = new Scanner(System.in);
        displayUserList();
        int choice = Utils.getDefaultChoice(scanner,"\n1-Block User; 2-Unblock User; 3-Exit");
        if (choice == 3) return;

        long userId = Utils.getValidNumber(scanner, "Enter user ID: ");
        if (userId == 0) return;
        User user = userDAO.findById(userId);

        switch (choice) {
            case 1 -> toggleUserBlock(user, true);
            case 2 -> toggleUserBlock(user, false);
        }
    }

    private void displayUserList() {
        List<User> users = userDAO.getAllUsers();
        users.forEach(user -> System.out.printf("%d: (%s%s)\n", user.getId(), user.isBlocked() ? "[X]" : "[V]", user.getName()));
    }

    private void toggleUserBlock(User user, boolean block) {
        if (user == null || user.isBlocked() == block) {
            System.out.println(STR."User not found or already \{block ? "blocked." : "unblocked."}");
            return;
        }
        user.setBlocked(block);
        userDAO.update(user);
        System.out.println(STR."User \{block ? "blocked" : "unblocked"} successfully.");
    }
}
