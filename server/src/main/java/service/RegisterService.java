package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import org.mindrot.jbcrypt.BCrypt;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        if (userDAO.getUser(userData.username()) != null) {
            throw new DataAccessException("Username already exists.");
        }

        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        UserData hashedUserData = new UserData(userData.username(), hashedPassword, userData.email());

        userDAO.createUser(hashedUserData);

        AuthData authData = new AuthData(userData.username(), generateAuthToken());
        authDAO.createAuth(authData);

        return authData;
    }

    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }
}