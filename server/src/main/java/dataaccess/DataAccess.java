package dataaccess;

import model.UserData;
import model.AuthData;

public interface DataAccess {
    void clear() throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData authData) throws DataAccessException;
}