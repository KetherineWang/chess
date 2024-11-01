package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

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
        ChessGame chessGame1 = new ChessGame();
        GameData gameData1 = new GameData(1, null, null, "Test Game", chessGame1);
        assertDoesNotThrow(() -> mySQLGameDAO.createGame(gameData1));
        int generatedGameID = mySQLGameDAO.createGame(gameData1);

        GameData retrievedGameData1 = mySQLGameDAO.getGame(generatedGameID);
        assertNotNull(retrievedGameData1, "Retrieved game data should not be null.");
        assertEquals("Test Game", retrievedGameData1.gameName(), "Game name should match.");
    }

    @Test
    @DisplayName("Negative test case for createGame - Null game name")
    void createGameFailureNull() {
        ChessGame chessGame1 = new ChessGame();
        GameData gameData = new GameData(1, null, null, null, chessGame1);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            mySQLGameDAO.createGame(gameData);
        });

        assertTrue(ex.getMessage().contains("Error creating game"), "Game creation with a null game name should fail.");
    }

    @Test
    @DisplayName("Positive test case for getGame")
    void getGameSuccess() throws DataAccessException {
        ChessGame chessGame2 = new ChessGame();
        GameData gameData2 = new GameData(2, "whitePlayer", null, "Existing Game", chessGame2);
        int generatedGameID = mySQLGameDAO.createGame(gameData2);

        GameData retrievedGameData2 = mySQLGameDAO.getGame(generatedGameID);
        assertNotNull(retrievedGameData2, "Retrieved game data should not be null.");
        assertEquals("Existing Game", retrievedGameData2.gameName(), "Game name should match.");
    }

    @Test
    @DisplayName("Negative test case for getGame - Game ID not found")
    void gameGameFailureNotFound() throws DataAccessException {
        GameData retrievedGameData = mySQLGameDAO.getGame(11111);
        assertNull(retrievedGameData, "Retrieved game data should be null.");
    }

    @Test
    @DisplayName("Positive test case for listGames")
    void listGamesSuccess() throws DataAccessException {
        ChessGame chessGame3 = new ChessGame();
        ChessGame chessGame4 = new ChessGame();
        GameData gameData3 = new GameData(3, null, "blackPlayer", "Game 3", chessGame3);
        GameData gameData4 = new GameData(4, "whitePlayer", "blackPlayer",  "Game 4", chessGame4);

        mySQLGameDAO.createGame(gameData3);
        mySQLGameDAO.createGame(gameData4);

        List<GameData> games = mySQLGameDAO.listGames();
        assertEquals(2, games.size(), "There should be 2 games listed.");
        assertEquals("Game 3", games.getFirst().gameName(), "First game name should match.");
        assertEquals("Game 4", games.getLast().gameName(), "Second game name should match.");
    }

    @Test
    @DisplayName("Negative test case for listGames - No games in the database")
    void listGamesFailureEmpty() throws DataAccessException {
        mySQLGameDAO.clear();

        List<GameData> games = mySQLGameDAO.listGames();

        assertNotNull(games, "Games list should not be null even if database is empty.");
        assertEquals(0, games.size(), "Games list should be empty when there are no games in database");
    }

    @Test
    @DisplayName("Positive test case for updateGame")
    void updateGameSuccess() throws DataAccessException {
        ChessGame chessGame5 = new ChessGame();
        GameData gameData5 = new GameData(5, null, null, "Initial Game", chessGame5);
        int generatedGameID = mySQLGameDAO.createGame(gameData5);

        GameData retrievedInitialGame = mySQLGameDAO.getGame(generatedGameID);
        assertNotNull(retrievedInitialGame, "Retrieved initial game should not be null.");
        assertEquals("Initial Game", retrievedInitialGame.gameName(), "Initial game name should match.");
        assertNull(retrievedInitialGame.whiteUsername(), "White player username should be null.");
        assertNull(retrievedInitialGame.blackUsername(), "Black player username should be null.");

        gameData5 = new GameData(generatedGameID, "whitePlayer", "blackPlayer", "Updated Game", chessGame5);
        mySQLGameDAO.updateGame(gameData5);

        GameData retrievedUpdatedGame = mySQLGameDAO.getGame(generatedGameID);
        assertNotNull(retrievedUpdatedGame, "Retrieved updated game should not be null.");
        assertEquals("Updated Game", retrievedUpdatedGame.gameName(), "Game name should be updated.");
        assertEquals("whitePlayer", retrievedUpdatedGame.whiteUsername(), "White player username should be updated.");
        assertEquals("blackPlayer", retrievedUpdatedGame.blackUsername(), "Black player username should be updated.");
    }

    @Test
    @DisplayName("Negative test case for updateGame - Game ID not found")
    void updateGameFailureNotFound() throws DataAccessException {
        ChessGame chessGame6 = new ChessGame();
        GameData gameData6 = new GameData(11111, "whitePlayer", "blackPlayer", "Nonexistent Game", chessGame6);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            mySQLGameDAO.updateGame(gameData6);  // Attempt to update a non-existent game
        });
        assertTrue(ex.getMessage().contains("Game ID not found."), "Nonexistent game update should fail.");
    }

    @Test
    @DisplayName("Positive test case for clear")
    void clearSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        GameData gameData1 = new GameData(1, null, null, "Test Game", chessGame1);
        int generatedGameID = mySQLGameDAO.createGame(gameData1);

        mySQLGameDAO.clear();

        GameData retrievedGameData = mySQLGameDAO.getGame(generatedGameID);
        assertNull(retrievedGameData, "Game table should be cleared.");
    }
}