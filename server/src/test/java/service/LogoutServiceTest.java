package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private LogoutService logoutService;
    private UserData testUser;
    private AuthData validAuthData;
    private AuthData invalidAuthData;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();

        userDAO.clear();
        authDAO.clear();

        testUser = new UserData("testUser", "password123", "testUser@email.com");
        validAuthData = new AuthData("testUser", "validAuthToken");
        invalidAuthData = new AuthData("testUser", "invalidAuthToken");

        try {
            userDAO.createUser(testUser);
        } catch (DataAccessException ex) {
            fail("Initial user creation should not fail.");
        }

        try {
            authDAO.createAuth(validAuthData);
        } catch (DataAccessException ex) {
            fail("Initial auth creation should not fail.");
        }

        logoutService = new LogoutService(authDAO);
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        logoutService.logout(validAuthData.authToken());

        AuthData authData = authDAO.getAuth(validAuthData.authToken());
        assertNull(authData, "Auth data should be null after successful logout.");
    }

    @Test
    void logoutFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            logoutService.logout(invalidAuthData.authToken());
        });

        assertEquals("Invalid auth token.", ex.getMessage());
    }

    @Test
    void logoutFailureNullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            logoutService.logout(null);
        });

        assertEquals("Invalid auth token.", ex.getMessage());
    }

}