package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

public class LogoutService {
    private final DataAccess dataAccess;

    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("auth token not found");
        }

        dataAccess.deleteAuth(authToken);
    }
}