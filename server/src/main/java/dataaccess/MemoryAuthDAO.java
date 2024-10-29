package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    final private Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        authTokens.clear();
    }

    @Override
    public void createAuth(AuthData authData) { authTokens.put(authData.authToken(), authData); }

    @Override
    public AuthData getAuth(String authToken) { return authTokens.get(authToken); }

    @Override
    public void deleteAuth(String authToken) { authTokens.remove(authToken); }
}