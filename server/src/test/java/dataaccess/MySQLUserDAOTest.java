package dataaccess;

import model.UserData;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MySQLUserDAOTest {
    private MySQLUserDAO mySQLUserDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        mySQLUserDAO = new MySQLUserDAO();
        mySQLUserDAO.clear();
    }

    @Test
    @DisplayName("Positive test case for createUser")
    void createUserSuccess() throws DataAccessException {
        UserData testUser = new UserData("testUser", "password123", "testUser@email.com");
        assertDoesNotThrow(() -> mySQLUserDAO.createUser(testUser));

        UserData retrievedTestUser = mySQLUserDAO.getUser("testUser");
        assertNotNull(retrievedTestUser, "Retrieved test user should not be null.");
        assertEquals("testUser", retrievedTestUser.username(), "Username should match.");
        assertEquals("password123", retrievedTestUser.password(), "Password should match.");
        assertEquals("testUser@email.com", retrievedTestUser.email(), "Email should match.");
    }

    @Test
    @DisplayName("Negative test case for createUser - Username already exist")
    void createUserFailureAlreadyTaken() throws DataAccessException {
        UserData duplicateUser = new UserData("duplicateUser", "password456", "duplicateUser@email.com");
        mySQLUserDAO.createUser(duplicateUser);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            mySQLUserDAO.createUser(duplicateUser);
        });

        assertTrue(ex.getMessage().contains("Error creating user"), "Duplicate user creation should fail.");
    }

    @Test
    @DisplayName("Positive test case for getUser")
    void getUserSuccess() throws DataAccessException {
        UserData existingUser = new UserData("existingUser", "password789", "existingUser@email.com");
        mySQLUserDAO.createUser(existingUser);

        UserData retrievedExistingUser = mySQLUserDAO.getUser("existingUser");
        assertNotNull(retrievedExistingUser, "Retrieved existing user should not be null.");
        assertEquals("existingUser", retrievedExistingUser.username());
        assertEquals("password789", retrievedExistingUser.password());
        assertEquals("existingUser@email.com", retrievedExistingUser.email());
    }

    @Test
    @DisplayName("Negative test case for getUser - Username not found")
    void getUserFailureNotFound() throws DataAccessException {
        UserData existingUser = new UserData("existingUser", "password789", "existingUser@email.com");
        mySQLUserDAO.createUser(existingUser);

        UserData retrievedNonExistingUser = mySQLUserDAO.getUser("nonExistingUser");
        assertNull(retrievedNonExistingUser, "Retrieved non existing user should be null.");
    }

    @Test
    @DisplayName("Positive test case for clear")
    void clearSuccess() throws DataAccessException {
        UserData testUser = new UserData("testUser", "password123", "testUser@email.com");
        mySQLUserDAO.createUser(testUser);

        mySQLUserDAO.clear();

        UserData retrievedTestUser = mySQLUserDAO.getUser("testUser");
        assertNull(retrievedTestUser, "User table should be cleared.");
    }
}