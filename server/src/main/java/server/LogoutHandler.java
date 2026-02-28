package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class LogoutHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService){
        this.userService = userService;
    }
    public void handle(Context ctx){
        try {
            LogoutRequest request = gson.fromJson(ctx.body(), LogoutRequest.class);
            userService.logout(request);
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e){
            if (e.getMessage().equals("Error: unauthorized")) {
                ctx.status(401);
            } else {
                ctx.status(500);
            }
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}
