package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class JoinGameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new DataAccessException("Invalid auth token.");
        }

        GameData gameData = gameDAO.getGame(gameId);
        if (gameData == null) {
            throw new DataAccessException("Game not found.");
        }

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new DataAccessException("Invalid player color.");
        }

        if (playerColor.equals("WHITE") && gameData.whiteUsername() != null) {
            throw new DataAccessException("White player color already taken.");
        }
        if (playerColor.equals("BLACK") && gameData.blackUsername() != null) {
            throw new DataAccessException("Black player color already taken.");
        }

        if (playerColor.equals("WHITE")) {
            gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
        } else {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.chessGame());
        }

        gameDAO.updateGame(gameData);
    }
}