package service;

import chess.ChessGame;
import service.JoinGameService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTest {
    private DataAccess dataAccess;
    private JoinGameService joinGameService;
    private AuthData validAuthDataWhite;
    private AuthData validAuthDataBlack;
    private AuthData invalidAuthDataWhite;
    private GameData gameData;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();

        validAuthDataWhite = new AuthData("whitePlayer", "validAuthTokenWhite");
        validAuthDataBlack = new AuthData("blackPlayer", "validAuthTokenBlack");
        invalidAuthDataWhite = new AuthData("whitePlayer", "invalidAuthTokenWhite");

        try {
            dataAccess.createAuth(validAuthDataWhite);
            dataAccess.createAuth(validAuthDataBlack);
            dataAccess.createGame(new GameData(1, null, null, "test game", new ChessGame()));
        } catch (DataAccessException ex) {
            fail("initial auth and game creation should not fail");
        }

        joinGameService = new JoinGameService(dataAccess);
    }

    @Test
    void joinGameSuccessOnePlayer() throws DataAccessException {
        joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");

        GameData updatedGameData = dataAccess.getGame(1);
        assertNotNull(updatedGameData, "game data should not be null");
        assertNull(updatedGameData.whiteUsername(), "white player should be null");
        assertEquals("blackPlayer", updatedGameData.blackUsername(), "black player should be set to blackTestUser");
    }

    @Test
    void joinGameSuccessTwoPlayers() throws DataAccessException {
        joinGameService.joinGame(validAuthDataWhite.authToken(), 1, "WHITE");

        GameData updatedGameData = dataAccess.getGame(1);
        assertNotNull(updatedGameData, "game data should not be null after first player joins");
        assertNull(updatedGameData.blackUsername(), "black player should still be null");
        assertEquals("whitePlayer", updatedGameData.whiteUsername(), "white player should be set to whitePlayer");

        joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");

        updatedGameData = dataAccess.getGame(1);
        assertNotNull(updatedGameData, "game data should not be null after second player joins");
        assertEquals("whitePlayer", updatedGameData.whiteUsername(), "white player should still be whitePlayer");
        assertEquals("blackPlayer", updatedGameData.blackUsername(), "black player should be set to blackPlayer");
    }

    @Test
    void joinGameFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(invalidAuthDataWhite.authToken(), 1, "WHITE");
        });

        assertEquals("invalid auth token", ex.getMessage());
    }

    @Test
    void joinGameFailureInvalidGameId() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataBlack.authToken(), 2, "BLACK");
        });

        assertEquals("invalid game id", ex.getMessage());
    }

    @Test
    void joinGameFailureInvalidPlayerColor() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataWhite.authToken(), 1, "GREY");
        });

        assertEquals("invalid player color", ex.getMessage());
    }

    @Test
    void joinGameFailurePlayerColorAlreadyTaken() throws DataAccessException {
        joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");
        });

        assertEquals("black player color already taken", ex.getMessage());
    }
}