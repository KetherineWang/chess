package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    private final DataAccess dataAccess;

    public JoinGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid authToken");
        }

        GameData gameData = dataAccess.getGame(gameId);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new DataAccessException("Invalid playerColor");
        }

        if (playerColor.equals("WHITE") && gameData.whiteUsername() != null) {
            throw new DataAccessException("WHITE playerColor already taken.");
        }
        if (playerColor.equals("BLACK") && gameData.blackUsername() != null) {
            throw new DataAccessException("BLACK playerColor already taken.");
        }

        if (playerColor.equals("WHITE")) {
            gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
        } else {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.chessGame());
        }

        dataAccess.updateGame(gameData);
    }
}