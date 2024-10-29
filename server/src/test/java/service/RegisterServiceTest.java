package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
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
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        registerService = new RegisterService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() throws DataAccessException {
        UserData newUser = new UserData("newTestUser", "password123", "newtestuser@email.com");

        AuthData authData = registerService.register(newUser);

        assertEquals(newUser.username(), authData.username());
        assertNotNull(authData.authToken(), "auth token should not be null");
    }

    @Test
    void registerFailureUsernameAlreadyTaken() {
        UserData existingUser = new UserData("existingTestUser", "password456", "existingtestuser@email.com");

        try {
            registerService.register(existingUser);
        } catch (DataAccessException ex) {
            fail("initial user registration should not fail: " + ex.getMessage());
        }

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            registerService.register(existingUser);
        });

        assertEquals("username already exists", ex.getMessage());
    }
}