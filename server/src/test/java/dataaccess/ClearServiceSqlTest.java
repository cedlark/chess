package dataaccess;

import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;
import service.AbstractClearServiceTest;

public class ClearServiceSqlTest extends AbstractClearServiceTest {

    @Override
    protected DataAccess createDataAccess() throws DataAccessException {
        return new MySqlDataAccess();
    }
}