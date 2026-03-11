package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClearServiceSqlTest {

    private DataAccess dao;
    private ClearService clearService;

    @BeforeEach
    public void setup() throws DataAccessException {
        dao = new MySqlDataAccess();
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