package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class ListGamesHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService){
        this.gameService = gameService;
    }
    public void handle(Context ctx){
        try {
            String token = ctx.header("authorization");
            GamesRequest request = new GamesRequest(token);
            GamesResult result = gameService.listGames(request);
            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e){
            ErrorHandler.handle(ctx, e);
        }
    }
}
