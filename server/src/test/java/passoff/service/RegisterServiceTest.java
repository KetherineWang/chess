package passoff.service;

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
    void register_success() throws DataAccessException {
        UserData newUser = new UserData("ketherine_wang", "password0528", "ketherine.wang@email.com");

        AuthData authData = registerService.register(newUser);

        assertEquals(newUser.username(), authData.username());
        assertNotNull(authData.authToken(), "auth token should not be null");
    }

    @Test
    void register_failure_usernameAlreadyTaken() {
        UserData existingUser = new UserData("hongting_wang", "password0718", "hongting.wang@email.com");

        try {
            registerService.register(existingUser);
        } catch (DataAccessException ex) {
            fail("initial user registration should not fil: " + ex.getMessage());
        }

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            registerService.register(existingUser);
        });

        assertEquals("username already taken", ex.getMessage());
    }
}