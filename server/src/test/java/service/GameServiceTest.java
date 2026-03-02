package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private MemoryDataAccess dao;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        dao = new MemoryDataAccess();
        userService = new UserService(dao);
        gameService = new GameService(dao);
    }

    // -------- CREATE GAME --------

    @Test
    public void createGamePositive() throws Exception {
        RegisterResult user =
                userService.register(
                        new RegisterRequest("rick", "pass", "email"));

        MakeGameResult result =
                gameService.createGame(
                        new MakeGameRequest(user.authToken(), "Game"));

        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createGameUnauthorized() {
        MakeGameRequest request =
                new MakeGameRequest("badToken", "Game");

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(request);
        });
    }

    @Test
    public void createGameBadRequest() throws Exception {
        RegisterResult user =
                userService.register(
                        new RegisterRequest("rick", "pass", "email"));

        MakeGameRequest request =
                new MakeGameRequest(user.authToken(), "");

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(request);
        });
    }

    // -------- LIST GAMES --------

    @Test
    public void listGamesPositive() throws Exception {
        RegisterResult user =
                userService.register(
                        new RegisterRequest("rick", "pass", "email"));

        gameService.createGame(
                new MakeGameRequest(user.authToken(), "Game"));

        GamesResult result =
                gameService.listGames(
                        new GamesRequest(user.authToken()));

        assertEquals(1, result.games().size());
    }

    @Test
    public void listGamesUnauthorized() {
        assertThrows(DataAccessException.class, () -> {
            gameService.listGames(
                    new GamesRequest("badToken"));
        });
    }

    // -------- JOIN GAME --------

    @Test
    public void joinGamePositive() throws Exception {
        RegisterResult user =
                userService.register(
                        new RegisterRequest("rick", "pass", "email"));

        int gameID = gameService
                .createGame(new MakeGameRequest(user.authToken(), "Game"))
                .gameID();

        JoinRequest request =
                new JoinRequest(user.authToken(),
                        ChessGame.TeamColor.WHITE,
                        gameID);

        gameService.joinGame(request);

        GameData game = dao.getGame(gameID);
        assertEquals("rick", game.getWhiteUsername());
    }

    @Test
    public void joinGameUnauthorized() {
        JoinRequest request =
                new JoinRequest("badToken",
                        ChessGame.TeamColor.WHITE,
                        1);

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(request);
        });
    }

    @Test
    public void joinGameAlreadyTaken() throws Exception {
        RegisterResult user1 =
                userService.register(
                        new RegisterRequest("rick", "pass", "email"));

        RegisterResult user2 =
                userService.register(
                        new RegisterRequest("bob", "pass", "email"));

        int gameID = gameService
                .createGame(new MakeGameRequest(user1.authToken(), "Game"))
                .gameID();

        gameService.joinGame(
                new JoinRequest(user1.authToken(),
                        ChessGame.TeamColor.WHITE,
                        gameID));

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(
                    new JoinRequest(user2.authToken(),
                            ChessGame.TeamColor.WHITE,
                            gameID));
        });
    }

    @Test
    public void joinGameBadRequest() throws Exception {
        RegisterResult user =
                userService.register(
                        new RegisterRequest("rick", "pass", "email"));

        int gameID = gameService
                .createGame(new MakeGameRequest(user.authToken(), "Game"))
                .gameID();

        JoinRequest request =
                new JoinRequest(user.authToken(),
                        null,
                        gameID);

        assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(request);
        });
    }
}