package org.rapidTransit.service;

import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.dao.AdminDAO;
import org.rapidTransit.model.User;
import org.rapidTransit.model.Admin;

import java.util.List;
import java.util.Scanner;

public class AuthenticationService {
    private final UserDAO userDAO;
    private final AdminDAO adminDAO;
    private final Scanner scanner;

    public AuthenticationService(UserDAO userDAO, AdminDAO adminDAO) {
        this.userDAO = userDAO;
        this.adminDAO = adminDAO;
        this.scanner = new Scanner(System.in);
    }

    public Object authenticate(String[] params) {
        if (params.length == 1 || params.length > 4) return null;

        List<String> credentials = requestCredentials(params);
        if (credentials == null) return null;

        Object authResult = authenticateUserOrAdmin(credentials);
        return (authResult instanceof List) ? registerNewUser(credentials) : authResult;
    }

    private List<String> requestCredentials(String[] params) {
        String email, password;
        List<String> paramsList;

        if (params.length >= 2) {
            if (params[0].equals("exit") || params[1].equals("exit")) return null;
            paramsList = List.of(params);
        } else {
            email = promptUser("Enter your email: ");
            if (email == null) return null;
            password = promptUser("Enter your password: ");
            if (password == null) return null;
            paramsList = List.of(email, password);
        }

        return paramsList;
    }

    private Object authenticateUserOrAdmin(List<String> credentials) {
        String email = credentials.get(0);
        String password = credentials.get(1);

        Admin admin = adminDAO.findByEmail(email);
        User user = userDAO.findByEmail(email);

        if (admin != null && admin.getPassword().equals(password)) return admin;
        if (user != null && user.getPassword().equals(password)) {
            return user.isBlocked() ? handleBlockedUser() : user;
        }

        if (admin != null || user != null) {
            System.out.println("Invalid password for an existing email");
            return null;
        }

        return credentials;
    }

    private User registerNewUser(List<String> credentials) {
        String email = credentials.get(0);
        String password = credentials.get(1);

        if (email.equals("exit") || password.equals("exit")) return null;

        String confirmPassword = (credentials.size() > 2) ? credentials.get(2) : promptUser("Confirm your password: ");
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return null;
        }

        String name = (credentials.size() > 3) ? credentials.get(3) : promptUser("Enter your name: ");
        if (name == null) return null;

        User newUser = new User(0, email, password, name, 0.0f, false);
        userDAO.save(newUser);
        return newUser;
    }

    private String promptUser(String message) {
        System.out.print(message);
        String input = scanner.nextLine();
        return input.equals("exit") ? null : input;
    }

    private Object handleBlockedUser() {
        System.out.println("Your account is blocked. Get out!");
        return null;
    }
}
