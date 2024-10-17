package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class ClearService{
    private DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}