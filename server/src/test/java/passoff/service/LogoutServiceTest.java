package passoff.service;

import service.LogoutService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {
    private DataAccess dataAccess;
    private LogoutService logoutService;
    private AuthData validAuthData;
    private AuthData invalidAuthData;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();

        validAuthData = new AuthData("testUser", "validAuthToken");

        try {
            dataAccess.createAuth(validAuthData);
        } catch (DataAccessException ex) {
            fail("initial auth creation should not fail");
        }

        logoutService = new LogoutService(dataAccess);

        invalidAuthData = new AuthData("testUser", "invalidAuthToken");
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        logoutService.logout(validAuthData.authToken());

        AuthData authData = dataAccess.getAuth(validAuthData.authToken());
        assertNull(authData, "authData should be null after successful logout");
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