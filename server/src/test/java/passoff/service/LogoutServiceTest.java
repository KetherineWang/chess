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
    private AuthData validAuthToken;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
        logoutService = new LogoutService(dataAccess);

        try {
            validAuthToken = new AuthData("testUser", "validAuthToken");
            dataAccess.createAuth(validAuthToken);
        } catch (DataAccessException ex) {
            fail("initial auth creation should not fail");
        }
    }

    @Test
    void logout_success() throws DataAccessException {
        logoutService.logout(validAuthToken.authToken());

        AuthData authData = dataAccess.getAuth(validAuthToken.authToken());
        assertNull(authData, "authData should be null after successful logout");
    }

    @Test
    void logout_failure_invalidAuthToken() {
        String invalidAuthToken = "invalidAuthToken";

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            logoutService.logout(invalidAuthToken);
        });

        assertEquals("invalid authToken", ex.getMessage());
    }

    @Test
    void logout_failure_nullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            logoutService.logout(null);
        });

        assertEquals("invalid authToken", ex.getMessage());
    }

}