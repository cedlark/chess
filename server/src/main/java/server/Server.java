package server;

import dataaccess.MemoryDataAccess;
import io.javalin.*;
import com.google.gson.Gson;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.Map;
public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        MemoryDataAccess dao = new MemoryDataAccess();
        UserService userService = new UserService(dao);
        GameService gameService = new GameService(dao);
        ClearService clearService = new ClearService(dao);
        // Register your endpoints and exception handlers here.
        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        ClearHandler clearHandler = new ClearHandler(clearService);
        javalin.post("/user", registerHandler::handle);
        javalin.post("/session", loginHandler::handle);
        javalin.delete("/session", logoutHandler::handle);
        javalin.get("/game", listGamesHandler::handle);
        javalin.post("/game", createGameHandler::handle);
        javalin.put("/game", joinGameHandler::handle);
        javalin.delete("/db", clearHandler::handle);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
