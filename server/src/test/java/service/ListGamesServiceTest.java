package service;

import chess.ChessGame;
import dataaccess.*;
import model.UserData;
import model.AuthData;
import model.GameData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ListGamesServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ListGamesService listGamesService;
    private UserData testUser;
    private UserData opponentTestUser;
    private AuthData validAuthData;
    private AuthData invalidAuthData;

    @BeforeEach
    void setUp() throws DataAccessException{
        userDAO = new MySQLUserDAO();
        authDAO = new MySQLAuthDAO();
        gameDAO = new MySQLGameDAO();

        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();

        testUser = new UserData("testUser", "password123", "testUser@email.com");
        opponentTestUser = new UserData("opponentUser", "password456", "opponentUser@gmail.com");
        validAuthData = new AuthData("testUser", "validAuthToken");
        invalidAuthData = new AuthData("testUser", "invalidAuthToken");

        try {
            userDAO.createUser(testUser);
            userDAO.createUser(opponentTestUser);
        } catch (DataAccessException ex) {
            fail("Initial user creation should not fail.");
        }

        try {
            authDAO.createAuth(validAuthData);
        } catch (DataAccessException ex) {
            fail("Initial auth creation should not fail.");
        }

        try {
            gameDAO.createGame(new GameData(1, "testUser", "opponentUser", "First Game", new ChessGame()));
            gameDAO.createGame(new GameData(2, "testUser", null, "Second Game", new ChessGame()));
        } catch (DataAccessException ex) {
            fail("Initial game creation should not fail.");
        }

        listGamesService = new ListGamesService(authDAO, gameDAO);
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        List<GameData> games = listGamesService.listGames(validAuthData.authToken());

        assertNotNull(games, "Games list should not be null.");
        assertEquals(2, games.size(), "Games list should have two games.");

        assertEquals("First Game", games.get(0).gameName(), "first game name should match.");
        assertEquals("Second Game", games.get(1).gameName(), "second game name should match.");
    }

    @Test
    void listGamesFailureInvalidAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            listGamesService.listGames(invalidAuthData.authToken());
        });

        assertEquals("Invalid auth token.", ex.getMessage());
    }

    @Test
    void listGamesFailureNullAuthToken() {
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            listGamesService.listGames(null);
        });

        assertEquals("Invalid auth token.", ex.getMessage());
    }
}
