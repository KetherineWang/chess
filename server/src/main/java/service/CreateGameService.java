package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class CreateGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid authToken");
        }

        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Invalid gameName");
        }

        GameData gameData = new GameData(generateGameID(), null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);

        return gameData;
    }

    private int generateGameID() {
        return (int) (Math.random() * 99999) + 1;
    }
}