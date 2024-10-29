package dataaccess;

import java.sql.SQLException;

public class DatabaseInitializer {
    private static final String[] createStatements = {
            """
           CREATE TABLE IF NOT EXISTS user (
               `username` VARCHAR(255) NOT NULL,
               `password` VARCHAR(255) NOT NULL,
               `email` VARCHAR(255) NOT NULL,
               PRIMARY KEY (`username`)
           ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
           """,
           """
           CREATE TABLE IF NOT EXISTS auth (
               `username` VARCHAR(255) NOT NULL,
               `authToken` VARCHAR(255) NOT NULL,
               PRIMARY KEY (`authToken`),
               FOREIGN KEY (`username`) REFERENCES user(`username`) ON DELETE CASCADE
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
           """
    };


    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database and create tables: " + ex.getMessage());
        }
    }

}