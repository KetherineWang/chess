package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.google.gson.Gson;

import java.util.List;
import java.util.ArrayList;

public class MySQLGameDAO implements GameDAO {
    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, new Gson().toJson(gameData.chessGame()));

            preparedStatement.executeUpdate();

            try (var rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedGameID = rs.getInt(1);
                    System.out.println("Generated Game ID: " + generatedGameID);
                    return generatedGameID;
                } else {
                    return 0;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error creating game: " + ex.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setInt(1, gameID);

            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    ChessGame chessGame = new Gson().fromJson(rs.getString("chessGame"), ChessGame.class);

                    return new GameData(rs.getInt("gameID"),
                                        rs.getString("whiteUsername"),
                                        rs.getString("blackUsername"),
                                        rs.getString("gameName"),
                                        chessGame);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting game: " + ex.getMessage());
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game";

        List<GameData> gameList = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement);
             var rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                ChessGame chessGame = new Gson().fromJson(rs.getString("chessGame"), ChessGame.class);

                GameData gameData = new GameData(rs.getInt("gameID"),
                                                 rs.getString("whiteUsername"),
                                                 rs.getString("blackUsername"),
                                                 rs.getString("gameName"),
                                                 chessGame);

                gameList.add(gameData);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error listing games: " + ex.getMessage());
        }

        return gameList;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, chessGame = ? WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameData.gameName());
            preparedStatement.setString(4, new Gson().toJson(gameData.chessGame()));
            preparedStatement.setInt(5, gameData.gameID());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error updating game: " + ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM game";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing game table" + ex.getMessage());
        }
    }
}