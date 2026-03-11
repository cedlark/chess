package dataaccess;

import service.AbstractUserServiceTest;

public class UserServiceSqlTest extends AbstractUserServiceTest {

    @Override
    protected DataAccess createDataAccess() throws Exception{
        return new MySqlDataAccess();
    }
}