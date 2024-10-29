package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTest {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private CreateGameService createGameService;
    private AuthData validAuthData;
    private AuthData invalidAuthData;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        validAuthData = new AuthData("testUser", "validAuthToken");
        invalidAuthData = new AuthData("testUser", "invalidAuthToken");

        try {
            authDAO.createAuth(validAuthData);
        } catch (DataAccessException ex) {
            fail("initial auth creation should not fail");
        }

        createGameService = new CreateGameService(authDAO, gameDAO);
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        GameData gameData = createGameService.createGame(validAuthData.authToken(), "valid test game");

        assertNotNull(gameData, "game data should not be null");
        assertNull(gameData.whiteUsername(), "white username should be null");
        assertNull(gameData.blackUsername(), "black username should be null");
        assertEquals("valid test game", gameData.gameName(), "game name should match");
        assertNotNull(gameData.gameID(), "game id should be generated");
    }

    @Test
    void createGameFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(invalidAuthData.authToken(), "invalid test game");
        });

        assertEquals("Invalid authToken", ex.getMessage());
    }

    @Test
    void createGameFailureNullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(null, "null test game");
        });

        assertEquals("Invalid authToken", ex.getMessage());
    }

    @Test
    void createGameFailureEmptyGameName() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame("validAuthToken", "");
        });

        assertEquals("Invalid gameName", ex.getMessage());
    }
}