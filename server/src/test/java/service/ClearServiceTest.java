package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.UserData;
import model.AuthData;
import model.GameData;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private ClearService clearService;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        // Using memory-based DAOs for testing
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        // Injecting into the ClearService
        clearService = new ClearService(userDAO, authDAO, gameDAO);

        // Add some initial data for the test
        userDAO.createUser(new UserData("user1", "password1", "user1@example.com"));
        authDAO.createAuth(new AuthData("user1", "authToken1"));
        gameDAO.createGame(new GameData(1, "user1", "user2", "First Game", null));
    }

    @Test
    void clearSuccess() throws DataAccessException{
        // Ensure data exists before clearing
        assertNotNull(userDAO.getUser("user1"), "User should exist before clear");
        assertNotNull(authDAO.getAuth("authToken1"), "Auth token should exist before clear");
        assertNotNull(gameDAO.getGame(1), "Game should exist before clear");

        // Call the clear() method
        assertDoesNotThrow(() -> clearService.clear(), "clear() method threw an exception");

        // Assert that data has been cleared
        assertNull(userDAO.getUser("user1"), "User should not exist after clear");
        assertNull(authDAO.getAuth("authToken1"), "Auth token should not exist after clear");
        assertNull(gameDAO.getGame(1), "Game should not exist after clear");
    }
}