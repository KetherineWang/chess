package passoff.service;

import service.ClearService;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    private ClearService clearService;
    private DataAccess dataAccess;

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
            public UserData getUser(String username) throws DataAccessException {
                return null;
            }

            @Override
            public void createAuth(AuthData authData) throws DataAccessException {}
        };

        ClearService faultyClearService = new ClearService(faultyDataAccess);

        DataAccessException ex = assertThrows(DataAccessException.class, faultyClearService::clear);
        assertEquals("clear_failure() test exception", ex.getMessage());
    }
}