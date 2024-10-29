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

class LoginServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();

        UserData testUser = new UserData("testUser", "password123", "testuser@email.com");

        try {
            userDAO.createUser(testUser);
        } catch (DataAccessException ex) {
            fail("initial user creation should not fail");
        }

        loginService = new LoginService(userDAO, authDAO);
    }

    @Test
    void loginSuccess() throws DataAccessException {
        AuthData authData = loginService.login("testUser", "password123");

        assertNotNull(authData, "authData should not be null");
        assertEquals("testUser", authData.username(), "username should match");
        assertNotNull(authData.authToken(), "auth token should not be null");
    }

    @Test
    void loginFailureInvalidUsername() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            loginService.login("invalidUsername", "password123");
        });

        assertEquals("invalid username or password", ex.getMessage(), "error message should match");
    }

    @Test
    void loginFailureInvalidPassword() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            loginService.login("testUser", "invalidPassword");
        });

        assertEquals("invalid username or password", ex.getMessage(), "error message should match");
    }
}
