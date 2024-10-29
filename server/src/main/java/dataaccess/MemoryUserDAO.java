package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    final private Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public UserData getUser(String username) { return users.get(username); }

    @Override
    public void createUser(UserData userData) { users.put(userData.username(), userData); }
}