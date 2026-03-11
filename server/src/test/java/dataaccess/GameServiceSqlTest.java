package dataaccess;

import service.AbstractGameServiceTest;

public class GameServiceSqlTest extends AbstractGameServiceTest {

    @Override
    protected DataAccess createDataAccess() throws Exception {
        return new MySqlDataAccess();
    }
}