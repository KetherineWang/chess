package passoff.service;

import service.LoginService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    private DataAccess dataAccess;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();

        UserData testUser = new UserData("testUser", "password123", "testuser@email.com");

        try {
            dataAccess.createUser(testUser);
        } catch (DataAccessException ex) {
            fail("initial user creation should not fail");
        }

        loginService = new LoginService(dataAccess);
    }

    @Test
    void login_success() throws DataAccessException {
        AuthData authData = loginService.login("testUser", "password123");

        assertNotNull(authData, "authData should not be null");
        assertEquals("testUser", authData.username(), "username should match");
        assertNotNull(authData.authToken(), "auth token should not be null");
    }

    @Test
    void login_failure_invalidUsername() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            loginService.login("invalidUsername", "password123");
        });

        assertEquals("invalid username or password", ex.getMessage(), "error message should match");
    }

    @Test
    void login_failure_invalidPassword() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            loginService.login("testUser", "invalidPassword");
        });

        assertEquals("invalid username or password", ex.getMessage(), "error message should match");
    }
}
