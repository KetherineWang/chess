package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class CreateGameService {
    private final DataAccess dataAccess;

    public CreateGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("auth token not found");
        }

        GameData gameData = new GameData(generateGameID(), authData.username(), null, gameName, new ChessGame());
        dataAccess.createGame(gameData);

        return gameData;
    }

    private int generateGameID() {
        return (int) (Math.random() * 100000);
    }
}