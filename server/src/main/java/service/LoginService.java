package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import org.mindrot.jbcrypt.BCrypt;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData userData = userDAO.getUser(username);
        if (userData == null || !BCrypt.checkpw(password, userData.password())) {
            throw new DataAccessException("Invalid username or password.");
        }

        AuthData authData = new AuthData(username, generateAuthToken());
        authDAO.createAuth(authData);

        return authData;
    }

    private String generateAuthToken() { return java.util.UUID.randomUUID().toString(); }
}