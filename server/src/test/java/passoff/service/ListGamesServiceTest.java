package passoff.service;

import chess.ChessGame;
import service.ListGamesService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListGamesServiceTest {
    private DataAccess dataAccess;
    private ListGamesService listGamesService;
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

        try {
            dataAccess.createGame(new GameData(1, "testUser", "opponentUser", "First Game", new ChessGame()));
            dataAccess.createGame(new GameData(2, "testUser", null, "Second Game", new ChessGame()));
        } catch (DataAccessException ex) {
            fail("initial game creation should not fail");
        }

        listGamesService = new ListGamesService(dataAccess);
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        List<GameData> games = listGamesService.listGames(validAuthData.authToken());

        assertNotNull(games, "games list should not be null");
        assertEquals(2, games.size(), "games list should have two games");

        assertEquals("First Game", games.get(0).gameName(), "first game name should match");
        assertEquals("Second Game", games.get(1).gameName(), "second game name should match");
    }

    @Test
    void listGamesFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            listGamesService.listGames(invalidAuthData.authToken());
        });

        assertEquals("invalid auth token", ex.getMessage());
    }

    @Test
    void listGamesFailureNullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            listGamesService.listGames(null);
        });

        assertEquals("invalid auth token", ex.getMessage());
    }
}
