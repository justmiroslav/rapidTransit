package org.rapidTransit.service;

import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.dao.AdminDAO;
import org.rapidTransit.model.User;
import org.rapidTransit.model.Admin;

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
        String[] credentials = requestCredentials(params);
        if (credentials == null || credentials.length < 2) return null;

        Object authResult = authenticateUserOrAdmin(credentials[0], credentials[1]);
        if (authResult instanceof String[]) {
            return registerNewUser(credentials[0], credentials[1]);
        }
        return authResult;
    }

    private String[] requestCredentials(String[] params) {
        String email, password;

        if (params.length == 2) {
            email = params[0];
            password = params[1];
        } else {
            System.out.print("Enter email: ");
            email = scanner.nextLine();
            if (email.equals("exit")) return null;

            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (password.equals("exit")) return null;
        }
        return new String[]{email, password};
    }

    private Object authenticateUserOrAdmin(String email, String password) {
        Admin admin = adminDAO.findByEmail(email);
        User user = userDAO.findByEmail(email);

        if (admin != null || user != null) {
            if ((admin != null && admin.getPassword().equals(password)) || (user != null && user.getPassword().equals(password))) {
                if (user != null && user.isBlocked()) {
                    System.out.println("Your account is blocked. Get out!");
                    return null;
                }
                return admin != null ? admin : user;
            }
            System.out.println("Invalid password for an existing email");
            return null;
        }
        return new String[]{email, password};
    }

    private User registerNewUser(String email, String password) {
        System.out.print("Confirm your password: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return null;
        }

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        User newUser = new User(0, email, password, name, 0.0f, false);
        userDAO.save(newUser);
        return newUser;
    }
}
