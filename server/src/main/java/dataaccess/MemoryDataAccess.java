package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    final private Map<String, UserData> users = new HashMap<>();
    final private Map<Integer, GameData> games = new HashMap<>();
    final private Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        try {
            users.clear();
            games.clear();
            authTokens.clear();
        } catch (Exception e) {
            throw new DataAccessException("Failed to clear database");
        }
    }
}