package org.rapidTransit.service;

import org.rapidTransit.dao.UserDAO;
import org.rapidTransit.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;

public class UserServiceTest {

    @Mock
    private UserDAO mockUserDAO;
    private User testUser;
    private UserService userService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User(1, "john@example.com", "password123", "John Doe", 100.0f, false);
        userService = new UserService(testUser, mockUserDAO);
    }

    @Test
    public void updateIncreasesUserBalance() {
        // Arrange
        float amount = 50.0f;

        // Act
        userService.updateBalance(amount);

        // Assert
        assertEquals(150.0f, testUser.getBalance(), 0.0f);
    }

    @Test
    public void updateDecreasesUserBalance() {
        // Arrange
        float amount = 50.0f;

        // Act
        userService.updateBalance(-amount);

        // Assert
        assertEquals(50.0f, testUser.getBalance(), 0.0f);
    }

    @Test
    public void updateUserNameChangesUserName() {
        // Arrange
        String newName = "Jane Doe";

        // Act
        userService.updateUserName(newName);

        // Assert
        assertEquals(newName, testUser.getName());
    }

    @Test
    public void updateUserPasswordChangesUserPassword() {
        // Arrange
        String newPassword = "newPassword123";

        // Act
        userService.updateUserPassword(newPassword);

        // Assert
        assertEquals(newPassword, testUser.getPassword());
    }
}
