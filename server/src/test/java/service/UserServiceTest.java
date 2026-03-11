package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class UserServiceTest extends AbstractUserServiceTest {

    @Override
    protected DataAccess createDataAccess() {
        return new MemoryDataAccess();
    }
}