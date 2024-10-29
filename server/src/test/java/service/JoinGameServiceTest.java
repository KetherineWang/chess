package service;

import chess.ChessGame;
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

class JoinGameServiceTest {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private JoinGameService joinGameService;
    private AuthData validAuthDataWhite;
    private AuthData validAuthDataBlack;
    private AuthData invalidAuthDataWhite;
    private GameData gameData;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        validAuthDataWhite = new AuthData("whitePlayer", "validAuthTokenWhite");
        validAuthDataBlack = new AuthData("blackPlayer", "validAuthTokenBlack");
        invalidAuthDataWhite = new AuthData("whitePlayer", "invalidAuthTokenWhite");

        try {
            authDAO.createAuth(validAuthDataWhite);
            authDAO.createAuth(validAuthDataBlack);
            gameDAO.createGame(new GameData(1, null, null, "test game", new ChessGame()));
        } catch (DataAccessException ex) {
            fail("Initial auth and game creation should not fail.");
        }

        joinGameService = new JoinGameService(authDAO, gameDAO);
    }

    @Test
    void joinGameSuccessOnePlayer() throws DataAccessException {
        joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");

        GameData updatedGameData = gameDAO.getGame(1);
        assertNotNull(updatedGameData, "updatedGameData should not be null after first player joins.");
        assertEquals("blackPlayer", updatedGameData.blackUsername(), "blackUsername should be set to blackPlayer.");
        assertNull(updatedGameData.whiteUsername(), "whiteUsername should still be null.");
    }

    @Test
    void joinGameSuccessTwoPlayers() throws DataAccessException {
        joinGameService.joinGame(validAuthDataWhite.authToken(), 1, "WHITE");

        GameData updatedGameData = gameDAO.getGame(1);
        assertNotNull(updatedGameData, "updatedGameData should not be null after first player joins.");
        assertEquals("whitePlayer", updatedGameData.whiteUsername(), "whiteUsername should be set to whitePlayer.");
        assertNull(updatedGameData.blackUsername(), "blackUsername should still be null.");

        joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");

        updatedGameData = gameDAO.getGame(1);
        assertNotNull(updatedGameData, "updatedGameData should not be null after second player joins.");
        assertEquals("whitePlayer", updatedGameData.whiteUsername(), "whiteUsername should still be whitePlayer.");
        assertEquals("blackPlayer", updatedGameData.blackUsername(), "blackUsername should be set to blackPlayer.");
    }

    @Test
    void joinGameFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(invalidAuthDataWhite.authToken(), 1, "WHITE");
        });

        assertEquals("Invalid authToken", ex.getMessage());
    }

    @Test
    void joinGameFailureInvalidGameId() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataBlack.authToken(), 2, "BLACK");
        });

        assertEquals("Game not found.", ex.getMessage());
    }

    @Test
    void joinGameFailureInvalidPlayerColor() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataWhite.authToken(), 1, "GREY");
        });

        assertEquals("Invalid playerColor", ex.getMessage());
    }

    @Test
    void joinGameFailurePlayerColorAlreadyTaken() throws DataAccessException {
        joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataBlack.authToken(), 1, "BLACK");
        });

        assertEquals("BLACK playerColor already taken.", ex.getMessage());
    }
}