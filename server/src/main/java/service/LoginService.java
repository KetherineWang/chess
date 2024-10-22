package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class LoginService {
    private final DataAccess dataAccess;

    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData userData = dataAccess.getUser(username);
        if (userData == null || !userData.password().equals(password)) {
            throw new DataAccessException("invalid username or password");
        }

        AuthData authData = new AuthData(username, generateAuthToken());
        dataAccess.createAuth(authData);

        return authData;
    }

    private String generateAuthToken() { return java.util.UUID.randomUUID().toString(); }
}