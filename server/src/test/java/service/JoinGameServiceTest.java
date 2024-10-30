package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private JoinGameService joinGameService;
    private UserData whitePlayer;
    private UserData blackPlayer;
    private AuthData validAuthDataWhite;
    private AuthData validAuthDataBlack;
    private AuthData invalidAuthDataWhite;
    private GameData gameData;
    private int gameID;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        whitePlayer = new UserData("whitePlayer", "password123", "whitePlayer@email.com");
        blackPlayer = new UserData("blackPlayer", "password123", "blackPlayer@email.com");
        validAuthDataWhite = new AuthData("whitePlayer", "validAuthTokenWhite");
        validAuthDataBlack = new AuthData("blackPlayer", "validAuthTokenBlack");
        invalidAuthDataWhite = new AuthData("whitePlayer", "invalidAuthTokenWhite");
        gameData = new GameData(1, null, null, "Test Game", new ChessGame());

        try {
            userDAO.createUser(whitePlayer);
            userDAO.createUser(blackPlayer);
        } catch (DataAccessException ex) {
            fail("Initial user creation should not fail.");
        }

        try {
            authDAO.createAuth(validAuthDataWhite);
            authDAO.createAuth(validAuthDataBlack);
        } catch (DataAccessException ex) {
            fail("Initial auth creation should not fail.");
        }

        try {
            gameID = gameDAO.createGame(gameData);
        } catch (DataAccessException ex) {
            fail("Initial game creation should not fail.");
        }

        joinGameService = new JoinGameService(authDAO, gameDAO);
    }

    @Test
    void joinGameSuccessOnePlayer() throws DataAccessException {
        joinGameService.joinGame(validAuthDataBlack.authToken(), gameID, "BLACK");

        GameData updatedGameData = gameDAO.getGame(gameID);
        assertNotNull(updatedGameData, "Updated game data should not be null after first player joins.");
        assertEquals("blackPlayer", updatedGameData.blackUsername(), "Black username should be set to black player.");
        assertNull(updatedGameData.whiteUsername(), "White username should still be null.");
    }

    @Test
    void joinGameSuccessTwoPlayers() throws DataAccessException {
        joinGameService.joinGame(validAuthDataWhite.authToken(), gameID, "WHITE");

        GameData updatedGameData = gameDAO.getGame(gameID);
        assertNotNull(updatedGameData, "Updated game data should not be null after first player joins.");
        assertEquals("whitePlayer", updatedGameData.whiteUsername(), "White username should be set to white player.");
        assertNull(updatedGameData.blackUsername(), "Black username should still be null.");

        joinGameService.joinGame(validAuthDataBlack.authToken(), gameID, "BLACK");

        updatedGameData = gameDAO.getGame(gameID);
        assertNotNull(updatedGameData, "Updated game data should not be null after second player joins.");
        assertEquals("whitePlayer", updatedGameData.whiteUsername(), "White username should still be white player.");
        assertEquals("blackPlayer", updatedGameData.blackUsername(), "Black username should be set to black player.");
    }

    @Test
    void joinGameFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(invalidAuthDataWhite.authToken(), gameID, "WHITE");
        });

        assertEquals("Invalid auth token.", ex.getMessage());
    }

    @Test
    void joinGameFailureInvalidGameId() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataBlack.authToken(), 11111, "BLACK");
        });

        assertEquals("Game not found.", ex.getMessage());
    }

    @Test
    void joinGameFailureInvalidPlayerColor() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataWhite.authToken(), gameID, "GREY");
        });

        assertEquals("Invalid player color.", ex.getMessage());
    }

    @Test
    void joinGameFailurePlayerColorAlreadyTaken() throws DataAccessException {
        joinGameService.joinGame(validAuthDataBlack.authToken(), gameID, "BLACK");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(validAuthDataBlack.authToken(), gameID, "BLACK");
        });

        assertEquals("Black player color already taken.", ex.getMessage());
    }
}