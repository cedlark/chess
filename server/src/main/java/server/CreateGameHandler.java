package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.MakeGameRequest;
import requests.MakeGameResult;
import service.*;

import java.util.Map;

public class CreateGameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context ctx){
        try {
            String token = ctx.header("authorization");
            var body = gson.fromJson(ctx.body(), Map.class);
            String gameName = (String) body.get("gameName");
            MakeGameRequest req = new MakeGameRequest(token, gameName);
            MakeGameResult res = gameService.createGame(req);
            ctx.status(200).json(res);
        } catch (DataAccessException e){
            ErrorHandler.handle(ctx, e);
        }
    }
}
