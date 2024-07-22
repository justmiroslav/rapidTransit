package org.rapidTransit.service;

import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.dao.AdminDAO;
import org.rapidTransit.model.User;
import org.rapidTransit.model.Admin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AuthenticationServiceTest {

    @Mock
    private UserDAO userDAOMock;
    @Mock
    private AdminDAO adminDAOMock;

    private AuthenticationService authenticationService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userDAOMock, adminDAOMock);
    }

    @Test
    public void authenticateWithExitDuringEmailInputReturnsNull() {
        // Arrange
        String[] credentials = {"exit", "password"};

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertNull(result);
    }

    @Test
    public void authenticateWithExitDuringPasswordInputReturnsNull() {
        // Arrange
        String[] credentials = {"email", "exit"};

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertNull(result);
    }

    @Test
    public void authenticateWithValidUserCredentialsReturnsUser() {
        // Arrange
        String[] credentials = {"user@example.com", "password"};
        User expectedUser = new User(1, "user@example.com", "password", "User", 0.0f, false);
        when(userDAOMock.findByEmail("user@example.com")).thenReturn(expectedUser);

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertTrue(result instanceof User);
        assertEquals(expectedUser, result);
    }

    @Test
    public void authenticateWithValidAdminCredentialsReturnsAdmin() {
        // Arrange
        String[] credentials = {"admin@example.com", "password"};
        Admin expectedAdmin = new Admin(1, "admin@example.com", "password", "Admin");
        when(adminDAOMock.findByEmail("admin@example.com")).thenReturn(expectedAdmin);

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertTrue(result instanceof Admin);
        assertEquals(expectedAdmin, result);
    }

    @Test
    public void authenticateWithInvalidPasswordReturnsNullUser() {
        // Arrange
        String[] credentials = {"user@example.com", "wrongpassword"};
        User expectedUser = new User(1, "user@example.com", "password", "User", 0.0f, false);
        when(userDAOMock.findByEmail("user@example.com")).thenReturn(expectedUser);

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertNull(result);
    }

    @Test
    public void authenticateWithInvalidPasswordReturnsNullAdmin() {
        // Arrange
        String[] credentials = {"admin@example.com", "wrongpassword"};
        Admin expectedAdmin = new Admin(1, "admin@example.com", "password", "Admin");
        when(adminDAOMock.findByEmail("admin@example.com")).thenReturn(expectedAdmin);

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertNull(result);
    }

    @Test
    public void authenticateWithBlockedUserReturnsNull() {
        // Arrange
        String[] credentials = {"blockeduser@example.com", "password"};
        User blockedUser = new User(1, "blockeduser@example.com", "password", "Blocked User", 0.0f, true);
        when(userDAOMock.findByEmail("blockeduser@example.com")).thenReturn(blockedUser);

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertNull(result);
    }

    @Test
    public void registerNewUserWithMismatchedPasswordsReturnsNull() {
        // Arrange
        String[] credentials = {"newuser@example.com", "password", "wrongpassword"};

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertNull(result);
    }

    @Test
    public void registerNewUserWithValidCredentialsReturnsUser() {
        // Arrange
        String[] credentials = {"newuser@example.com", "password", "password", "New User"};

        // Act
        Object result = authenticationService.authenticate(credentials);

        // Assert
        assertTrue(result instanceof User);
        assertEquals("New User", ((User) result).getName());
    }
}
