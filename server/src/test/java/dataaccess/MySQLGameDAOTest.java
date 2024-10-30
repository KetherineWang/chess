package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class MySQLGameDAOTest {
    private MySQLUserDAO mySQLUserDAO;
    private MySQLGameDAO mySQLGameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        mySQLUserDAO = new MySQLUserDAO();
        mySQLGameDAO = new MySQLGameDAO();

        mySQLUserDAO.clear();
        mySQLGameDAO.clear();

        UserData whitePlayer = new UserData("whitePlayer", "password123", "whitePlayer@email.com");
        mySQLUserDAO.createUser(whitePlayer);
        UserData blackPlayer = new UserData("blackPlayer", "password456", "blackPlayer@email.com");
        mySQLUserDAO.createUser(blackPlayer);
    }

    @Test
    @DisplayName("Positive test case for createGame")
    void createGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, "whitePlayer", "blackPlayer", "Test Game", chessGame);
        int generatedGameID = mySQLGameDAO.createGame(gameData);

        GameData retrievedGameData = mySQLGameDAO.getGame(generatedGameID);
        assertNotNull(retrievedGameData, "Retrieved game data should not be null.");
        assertEquals("Test Game", retrievedGameData.gameName(), "Game name should match.");
    }
}