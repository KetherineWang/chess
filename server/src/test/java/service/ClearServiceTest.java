package service;

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
    void clearSuccess() {
        assertDoesNotThrow(() -> clearService.clear(), "clear() method threw an exception");
    }

    @Test
    void clearFailure() {
        DataAccess faultyDataAccess = new DataAccess() {
            @Override
            public void clear() throws DataAccessException {
                throw new DataAccessException("clear_failure() test exception");
            }

            @Override
            public void createUser(UserData userData) {}

            @Override
            public UserData getUser(String username) { return null; }

            @Override
            public void createAuth(AuthData authData) {}

            @Override
            public AuthData getAuth(String authToken) { return null; }

            @Override
            public void deleteAuth(String authToken) {}

            @Override
            public List<GameData> listGames() { return null; }

            @Override
            public void createGame(GameData gameData) {}

            @Override
            public GameData getGame(int gameID) { return null; }

            @Override
            public void updateGame(GameData gameData) {}
        };

        ClearService faultyClearService = new ClearService(faultyDataAccess);

        DataAccessException ex = assertThrows(DataAccessException.class, faultyClearService::clear);
        assertEquals("clear_failure() test exception", ex.getMessage());
    }
}