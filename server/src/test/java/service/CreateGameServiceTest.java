package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private CreateGameService createGameService;
    private UserData testUser;
    private AuthData validAuthData;
    private AuthData invalidAuthData;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

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

        createGameService = new CreateGameService(authDAO, gameDAO);
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        int gameID = createGameService.createGame(validAuthData.authToken(), "Valid Test Game");

        assertNotNull(gameID, "game id should be generated.");
    }

    @Test
    void createGameFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(invalidAuthData.authToken(), "Invalid Test Game");
        });

        assertEquals("Invalid auth token.", ex.getMessage());
    }

    @Test
    void createGameFailureNullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(null, "Null Test Game");
        });

        assertEquals("Invalid auth token.", ex.getMessage());
    }

    @Test
    void createGameFailureEmptyGameName() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame("validAuthToken", "");
        });

        assertEquals("Invalid game name.", ex.getMessage());
    }
}