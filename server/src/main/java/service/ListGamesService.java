package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.List;

public class ListGamesService {
    private final DataAccess dataAccess;

    public ListGamesService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("invalid auth token");
        }

        return dataAccess.listGames();
    }
}