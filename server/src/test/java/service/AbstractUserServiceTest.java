package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractUserServiceTest {

    protected DataAccess dao;
    private UserService userService;

    protected abstract DataAccess createDataAccess() throws Exception;
    @BeforeEach
    void setup() throws Exception {

        dao = createDataAccess();

        dao.clearAll();

        userService = new UserService(dao);
    }


    @Test
    public void registerPositive() throws Exception {
        RegisterRequest request =
                new RegisterRequest("rick", "pass", "email@test.com");

        RegisterResult result = userService.register(request);

        assertEquals("rick", result.username());
        assertNotNull(result.authToken());
        assertNotNull(dao.getUser("rick"));
    }

    @Test
    public void registerAlreadyTaken() throws Exception {
        RegisterRequest request =
                new RegisterRequest("rick", "pass", "email@test.com");

        userService.register(request);

        assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest request =
                new RegisterRequest(null, "pass", "email");

        assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
    }

    // -------- LOGIN --------

    @Test
    public void loginPositive() throws Exception {
        userService.register(
                new RegisterRequest("rick", "pass", "email"));

        LoginRequest request =
                new LoginRequest("rick", "pass");

        LoginResult result = userService.login(request);

        assertEquals("rick", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginUnauthorized() throws Exception {
        userService.register(
                new RegisterRequest("rick", "pass", "email"));

        LoginRequest request =
                new LoginRequest("rick", "wrongPass");

        assertThrows(DataAccessException.class, () -> {
            userService.login(request);
        });
    }

    @Test
    public void loginBadRequest() {
        LoginRequest request =
                new LoginRequest(null, "pass");

        assertThrows(DataAccessException.class, () -> {
            userService.login(request);
        });
    }

    // -------- LOGOUT --------

    @Test
    public void logoutPositive() throws Exception {
        RegisterResult result =
                userService.register(
                        new RegisterRequest("rick", "pass", "email"));

        LogoutRequest request =
                new LogoutRequest(result.authToken());

        userService.logout(request);

        assertNull(dao.getAuth(result.authToken()));
    }

    @Test
    public void logoutUnauthorized() {
        LogoutRequest request =
                new LogoutRequest("badToken");

        assertThrows(DataAccessException.class, () -> {
            userService.logout(request);
        });
    }
}