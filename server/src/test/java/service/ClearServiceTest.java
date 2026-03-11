package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class ClearServiceTest extends AbstractClearServiceTest {

    @Override
    protected DataAccess createDataAccess() {
        return new MemoryDataAccess();
    }
}