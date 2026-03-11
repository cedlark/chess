package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

public class ClearService {
    private final DataAccess dao;
    public ClearService(DataAccess dao) {
        this.dao = dao;
    }
    public void clear() throws DataAccessException {
        dao.clearAll();
    }
}
