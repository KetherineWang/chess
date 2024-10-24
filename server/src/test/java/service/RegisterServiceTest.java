package service;

import service.RegisterService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterServiceTest {
    private DataAccess dataAccess;
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
        registerService = new RegisterService(dataAccess);
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