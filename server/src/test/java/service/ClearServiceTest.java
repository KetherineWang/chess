package service;

import chess.ChessGame;
import dataaccess.*;
import model.UserData;
import model.AuthData;
import model.GameData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ClearService clearService;
    private UserData testUser;
    private AuthData validAuthData;
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

        testUser = new UserData("testUser", "password1234", "testUser@email.com");
        validAuthData = new AuthData("testUser", "validAuthToken");
        gameData = new GameData(1, "testUser", null, "Test Game", new ChessGame());

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

        try {
            gameID = gameDAO.createGame(gameData);
        } catch (DataAccessException ex) {
            fail("Initial game creation should not fail.");
        }

        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    void clearSuccess() throws DataAccessException{
        assertDoesNotThrow(() -> clearService.clear(), "Clear method should not throw an exception.");

        assertNull(userDAO.getUser("testUser"), "User should not exist after clear");
        assertNull(authDAO.getAuth("validAuthToken"), "Auth token should not exist after clear");
        assertNull(gameDAO.getGame(gameID), "Game should not exist after clear");
    }
}