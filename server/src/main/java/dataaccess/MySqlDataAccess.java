package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        throw new DataAccessException("clear_failure() test exception");
    }

    @Override
    public void createUser(UserData userData) {}

    @Override
    public UserData getUser(String username) { return null; }

    @Override
    public void createAuth(AuthData authData) {}

    @Override
    public AuthData getAuth(String authToken) { return null; }

    @Override
    public void deleteAuth(String authToken) {}

    @Override
    public List<GameData> listGames() { return null; }

    @Override
    public void createGame(GameData gameData) {}

    @Override
    public GameData getGame(int gameID) { return null; }

    @Override
    public void updateGame(GameData gameData) {}

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
                `username` VARCHAR(255) NOT NULL,
                `password` VARCHAR(255) NOT NULL,
                `email` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
                `gameID` INT AUTO_INCREMENT NOT NULL,
                `whiteUsername` VARCHAR(255),
                `blackUsername` VARCHAR(255),
                `gameName` VARCHAR(255) NOT NULL,
                `chessGame` TEXT NOT NULL,
                PRIMARY KEY (`gameID`),
                FOREIGN KEY (`whiteUsername`) REFERENCES user(`username`) ON DELETE SET NULL,
                FOREIGN KEY (`blackUsername`) REFERENCES user(`username`) ON DELETE SET NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
                `authToken` VARCHAR(255) NOT NULL,
                `username` VARCHAR(255) NOT NULL,
                PRIMARY KEY (`authToken`),
                FOREIGN KEY (`username`) REFERENCES user(`username`) ON DELETE CASCADE
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database and create tables.");
        }
    }
}