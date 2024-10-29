package dataaccess;

import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTest {
    private MySQLUserDAO mySQLUserDAO;
    private MySQLAuthDAO mySQLAuthDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        mySQLUserDAO = new MySQLUserDAO();
        mySQLAuthDAO = new MySQLAuthDAO();
        mySQLUserDAO.clear();
        mySQLAuthDAO.clear();
        UserData testUser = new UserData("testUser", "password123", "testUser@email.com");
        mySQLUserDAO.createUser(testUser);
    }

    @Test
    @DisplayName("Positive test case for createAuth")
    public void createAuthSuccess() throws DataAccessException {
        AuthData validAuthData = new AuthData("testUser", "validAuthToken");
        assertDoesNotThrow(() -> mySQLAuthDAO.createAuth(validAuthData));
    }

    @Test
    @DisplayName("Negative test case for createAuth - Auth token already exists")
    public void createAuthFailureAlreadyTaken() throws DataAccessException {
        AuthData duplicateAuthData = new AuthData("testUser", "duplicateAuthToken");
        mySQLAuthDAO.createAuth(duplicateAuthData);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            mySQLAuthDAO.createAuth(duplicateAuthData);
        });

        assertTrue(ex.getMessage().contains("Error creating auth"), "Duplicate auth creation should fail.");
    }

    @Test
    @DisplayName("Positive test case for getAuth")
    public void getAuthSuccess() throws DataAccessException {
        AuthData validAuthData = new AuthData("testUser", "validAuthToken");
        mySQLAuthDAO.createAuth(validAuthData);

        AuthData retrievedValidAuthData = mySQLAuthDAO.getAuth("validAuthToken");
        assertNotNull(retrievedValidAuthData, "Retrieved valid auth data should not be null.");
        assertEquals("testUser", retrievedValidAuthData.username(), "Username should match.");
        assertEquals("validAuthToken", retrievedValidAuthData.authToken(), "Auth token should match.");
    }

    @Test
    @DisplayName("Negative test case for getAuth - Auth token not found")
    public void getAuthFailureNotFound() throws DataAccessException {
        AuthData validAuthData = new AuthData("testUser", "validAuthToken");
        mySQLAuthDAO.createAuth(validAuthData);

        AuthData retrievedNonExistentAuthData = mySQLAuthDAO.getAuth("nonExistentAuthToken");
        assertNull(retrievedNonExistentAuthData, "Retrieved non existent auth data should be null.");
    }

    @Test
    @DisplayName("Positive test case for deleteAuth")
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData validAuthData = new AuthData("testUser", "validAuthToken");
        mySQLAuthDAO.createAuth(validAuthData);

        assertDoesNotThrow(() -> mySQLAuthDAO.deleteAuth("validAuthToken"));

        AuthData deletedAuthData = mySQLAuthDAO.getAuth("validAuthToken");
        assertNull(deletedAuthData, "Deleted auth data should be null.");
    }

    @Test
    @DisplayName("Negative test case for deleteAuth - Auth token not found")
    public void deleteAuthFailureNotFound() throws DataAccessException {
        AuthData validAuthData = new AuthData("testUser", "validAuthToken");
        mySQLAuthDAO.createAuth(validAuthData);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            mySQLAuthDAO.deleteAuth("nonExistentAuthToken");
        });

        assertTrue(ex.getMessage().contains("Auth token not found."), "Deleting non existent auth token should fail.");
    }

    @Test
    @DisplayName("Positive test case for clear")
    void clearSuccess() throws DataAccessException {
        AuthData validAuthData = new AuthData("testUser", "validAuthToken");
        mySQLAuthDAO.createAuth(validAuthData);

        mySQLAuthDAO.clear();

        AuthData retrievedValidAuthData = mySQLAuthDAO.getAuth("validAuthToken");
        assertNull(retrievedValidAuthData, "Auth table should be cleared.");
    }
}