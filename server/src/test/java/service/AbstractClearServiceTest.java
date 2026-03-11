package service;

import dataaccess.DataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractClearServiceTest {

    protected DataAccess dao;
    protected GameService gameService;
    protected UserService userService;
    protected ClearService clearService;

    protected abstract DataAccess createDataAccess() throws Exception;

    @BeforeEach
    void setup() throws Exception {

        dao = createDataAccess();

        dao.clearAll();

        gameService = new GameService(dao);
        userService = new UserService(dao);
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