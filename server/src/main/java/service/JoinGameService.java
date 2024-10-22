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

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("invalid auth token");
        }

        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("invalid game id");
        }

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new DataAccessException("invalid player color");
        }

        if (playerColor.equals("WHITE") && gameData.whiteUsername() != null) {
            throw new DataAccessException("white player color already taken");
        }
        if (playerColor.equals("BLACK") && gameData.blackUsername() != null) {
            throw new DataAccessException("black player color already taken");
        }

        if (playerColor.equals("WHITE")) {
            gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
        } else {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.chessGame());
        }

        dataAccess.updateGame(gameData);
    }
}