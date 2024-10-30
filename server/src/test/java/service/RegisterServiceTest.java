package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.*;

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
    void registerFailureUsernameAlreadyTaken() throws DataAccessException {
        UserData existingUser = new UserData("existingUser", "password456", "existingUser@email.com");
        registerService.register(existingUser);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            registerService.register(existingUser);
        });

        assertEquals("Username already exists.", ex.getMessage());
    }
}