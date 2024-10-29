package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {
    private AuthDAO authDAO;
    private LogoutService logoutService;
    private AuthData validAuthData;
    private AuthData invalidAuthData;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();

        validAuthData = new AuthData("testUser", "validAuthToken");
        invalidAuthData = new AuthData("testUser", "invalidAuthToken");

        try {
            authDAO.createAuth(validAuthData);
        } catch (DataAccessException ex) {
            fail("initial auth creation should not fail");
        }

        logoutService = new LogoutService(authDAO);
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        logoutService.logout(validAuthData.authToken());

        AuthData authData = authDAO.getAuth(validAuthData.authToken());
        assertNull(authData, "auth data should be null after successful logout");
    }

    @Test
    void logoutFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            logoutService.logout(invalidAuthData.authToken());
        });

        assertEquals("invalid auth token", ex.getMessage());
    }

    @Test
    void logoutFailureNullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            logoutService.logout(null);
        });

        assertEquals("invalid auth token", ex.getMessage());
    }

}