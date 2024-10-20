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

        if (authToken == null) {
            throw new DataAccessException("invalid auth token");

            GameData newGame = new GameData(generateGameID(), authData.username(), null, gameName, new ChessGame());
        }
    }
}