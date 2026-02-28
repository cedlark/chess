package service;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private MemoryDataAccess dao;
    private ClearService clearService;

    @BeforeEach
    public void setup() {
        dao = new MemoryDataAccess();
        clearService = new ClearService(dao);
    }

    @Test
    public void clearPositive() throws Exception {

        dao.addUser(new UserData("a", "b", "c"));
        assertNotNull(dao.getUser("a"));

        clearService.clear();

        assertNull(dao.getUser("a"));
    }
}