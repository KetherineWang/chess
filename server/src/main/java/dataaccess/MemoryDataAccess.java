package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    final private Map<String, UserData> users = new HashMap<>();
    final private Map<Integer, GameData> games = new HashMap<>();
    final private Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        authTokens.clear();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        if (users.containsKey(userData.username())) {
            throw new DataAccessException("user already exists");
        }

        users.put(userData.username(), userData);
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        authTokens.put(authData.authToken(), authData);
    }
}