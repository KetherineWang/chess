package passoff.service;

import service.ClearService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    private DataAccess dataAccess;
    private ClearService clearService;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
        clearService = new ClearService(dataAccess);
    }

    @Test
    void clear_success() {
        assertDoesNotThrow(() -> clearService.clear(), "clear() method threw an exception");
    }

    @Test
    void clear_failure() {
        DataAccess faultyDataAccess = new DataAccess() {
            @Override
            public void clear() throws DataAccessException {
                throw new DataAccessException("clear_failure() test exception");
            }

            @Override
            public void createUser(UserData userData) throws DataAccessException {}

            @Override
            public UserData getUser(String username) throws DataAccessException { return null; }

            @Override
            public void createAuth(AuthData authData) throws DataAccessException {}

            @Override
            public AuthData getAuth(String authToken) throws DataAccessException { return null; }

            @Override
            public void deleteAuth(String authToken) throws DataAccessException {}

            @Override
            public List<GameData> listGames() throws DataAccessException { return null; }
        };

        ClearService faultyClearService = new ClearService(faultyDataAccess);

        DataAccessException ex = assertThrows(DataAccessException.class, faultyClearService::clear);
        assertEquals("clear_failure() test exception", ex.getMessage());
    }
}