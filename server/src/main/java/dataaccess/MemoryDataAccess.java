package dataaccess;

import model.UserData;
import model.GameData;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private Map<String, UserData> users = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();
    private Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        games.clear();
        authTokens.clear();
    }
}