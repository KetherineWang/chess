package passoff.service;

import chess.ChessGame;
import service.CreateGameService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTest {
    private DataAccess dataAccess;
    private CreateGameService createGameService;
    private AuthData validAuthData;
    private AuthData invalidAuthData;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();

        validAuthData = new AuthData("testUser", "validAuthToken");
        invalidAuthData = new AuthData("testUser", "invalidAuthToken");

        try {
            dataAccess.createAuth(validAuthData);
        } catch (DataAccessException ex) {
            fail("initial auth creation should not fail");
        }

        createGameService = new CreateGameService(dataAccess);
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        GameData gameData = createGameService.createGame(validAuthData.authToken(), "valid test game");

        assertNotNull(gameData, "game data should not be null");
        assertEquals("testUser", gameData.whiteUsername(), "white username should match");
        assertEquals("valid test game", gameData.gameName(), "game name should match");
        assertNotNull(gameData.gameID(), "game id should be generated");
    }

    @Test
    void createGameFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(invalidAuthData.authToken(), "invalid test game");
        });

        assertEquals("invalid auth token", ex.getMessage());
    }

    @Test
    void createGameFailureNullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(null, "null test game");
        });

        assertEquals("invalid auth token", ex.getMessage());
    }

    @Test
    void createGameFailureEmptyGameName() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            createGameService.createGame("validAuthToken", "");
        });

        assertEquals("invalid game name", ex.getMessage());
    }
}