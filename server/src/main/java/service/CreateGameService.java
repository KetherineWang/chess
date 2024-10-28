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

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid authToken");
        }

        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Invalid gameName");
        }

        GameData gameData = new GameData(generateGameID(), null, null, gameName, new ChessGame());
        dataAccess.createGame(gameData);

        return gameData;
    }

    private int generateGameID() {
        return (int) (Math.random() * 99999) + 1;
    }
}