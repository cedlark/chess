package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.AbstractGameServiceTest;

public class GameServiceTest extends AbstractGameServiceTest {

    @Override
    protected DataAccess createDataAccess() {
        return new MemoryDataAccess();
    }
}