package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

public class ClearService {
    private final MemoryDataAccess dao;
    public ClearService(MemoryDataAccess dao) {
        this.dao = dao;
    }
    public void clear() throws DataAccessException {
        dao.clearAll();
    }
}
