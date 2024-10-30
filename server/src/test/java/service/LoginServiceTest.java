package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private LoginService loginService;

    @BeforeEach
    void setUp()  throws DataAccessException{
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();

        userDAO.clear();
        authDAO.clear();

        UserData testUser = new UserData("testUser", "password123", "testUser@email.com");

        try {
            userDAO.createUser(testUser);
        } catch (DataAccessException ex) {
            fail("Initial user creation should not fail.");
        }

        loginService = new LoginService(userDAO, authDAO);
    }

    @Test
    void loginSuccess() throws DataAccessException {
        AuthData authData = loginService.login("testUser", "password123");

        assertNotNull(authData, "Auth data should not be null.");
        assertEquals("testUser", authData.username(), "Username should match.");
        assertNotNull(authData.authToken(), "Auth token should not be null.");
    }

    @Test
    void loginFailureInvalidUsername() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            loginService.login("invalidUsername", "password123");
        });

        assertEquals("Invalid username or password.", ex.getMessage());
    }

    @Test
    void loginFailureInvalidPassword() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            loginService.login("testUser", "invalidPassword");
        });

        assertEquals("Invalid username or password.", ex.getMessage());
    }
}
