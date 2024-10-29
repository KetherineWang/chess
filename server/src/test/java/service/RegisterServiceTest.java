package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        registerService = new RegisterService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() throws DataAccessException {
        UserData newUser = new UserData("newUser", "password123", "newUser@email.com");

        AuthData authData = registerService.register(newUser);

        assertEquals(newUser.username(), authData.username());
        assertNotNull(authData.authToken(), "Auth token should not be null.");
    }

    @Test
    void registerFailureUsernameAlreadyTaken() {
        UserData existingUser = new UserData("existingUser", "password456", "existingUser@email.com");

        try {
            registerService.register(existingUser);
        } catch (DataAccessException ex) {
            fail("Initial user registration should not fail: " + ex.getMessage());
        }

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            registerService.register(existingUser);
        });

        assertEquals("Username already exists.", ex.getMessage());
    }
}