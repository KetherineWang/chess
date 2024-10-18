package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

public class RegisterService {
    private final DataAccess dataAccess;

    public RegisterService (DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        if (dataAccess.getUser(userData.username()) != null) {
            throw new DataAccessException("Username already taken");
        }

        dataAccess.createUser(userData);

        AuthData authData = new AuthData(userData.username(), generateAuthToken());
        dataAccess.createAuth(authData);

        return authData;
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}