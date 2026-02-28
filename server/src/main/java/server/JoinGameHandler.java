package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;
import service.JoinRequest;

import java.util.Map;

public class JoinGameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context ctx){
        try {
            JoinRequest request = gson.fromJson(ctx.body(), JoinRequest.class);
            gameService.joinGame(request);
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e){
            switch (e.getMessage()){
                case "Error: bad request" -> ctx.status(400);
                case "Error: unauthorized" -> ctx.status(401);
                case "Error: already taken" -> ctx.status(403);
                default -> ctx.status(500);
            }
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}
