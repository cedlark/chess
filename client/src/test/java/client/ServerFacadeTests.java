package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import requests.GamesResult;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }


    @Test
    void registerSuccess() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        assertNotNull(auth);
        assertNotNull(auth.getAuthToken());
        assertEquals("player1", auth.getUsername());
    }

    @Test
    void registerFailure() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Exception ex = assertThrows(Exception.class, () ->
                facade.register("player1", "password", "p1@email.com"));
        assertNotNull(ex.getMessage());
    }
    @Test
    void loginSuccess() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData auth = facade.login("player1", "password");
        assertNotNull(auth);
        assertNotNull(auth.getAuthToken());
        assertEquals("player1", auth.getUsername());
    }

    @Test
    void loginFailure() {
        Exception ex = assertThrows(Exception.class, () -> facade.login("George", "Washington"));
        assertNotNull(ex.getMessage());
    }
    @Test
    void logoutSuccess() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.getAuthToken()));
    }
    @Test
    void logoutFailure() {
        Exception ex = assertThrows(Exception.class, () -> facade.logout("bad-token"));
        assertNotNull(ex.getMessage());
    }
    @Test
    void listGamesSuccess() throws Exception {
        AuthData auth = facade.register("Ben", "Franklin", "p1@email.com");
        GamesResult result = facade.listGames(auth.getAuthToken());
        assertNotNull(result);
        assertNotNull(result.games());
    }
    @Test
    void listGamesFailure() {
        Exception ex = assertThrows(Exception.class, () -> facade.listGames("bad-token"));
        assertNotNull(ex.getMessage());
    }
    @Test
    void createGameSuccess()throws Exception {
        AuthData auth = facade.register("Dipper", "Pines", "hehe");
        assertDoesNotThrow(()-> facade.createGame(auth.getAuthToken(), "Good-Game"));
    }
    @Test
    void createGameFailure() {
        Exception ex = assertThrows(Exception.class, () ->
                facade.createGame("Me", "Test Game"));
        assertNotNull(ex.getMessage());
    }
    @Test
    void joinGameSuccess() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        facade.createGame(auth.getAuthToken(), "Test Game");
        GamesResult result = facade.listGames(auth.getAuthToken());
        int gameID = result.games().getFirst().getGameId();
        assertDoesNotThrow(() -> facade.joinGame(auth.getAuthToken(), "white", gameID));
    }
    @Test
    void joinGameFailure() {
        Exception ex = assertThrows(Exception.class, () ->
                facade.joinGame("bad-token", "white", 999));
        assertNotNull(ex.getMessage());
    }
}
