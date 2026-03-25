package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.LogoutRequest;
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
            String token = ctx.header("authorization");
            LogoutRequest request = new LogoutRequest(token);
            userService.logout(request);
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e){
            ErrorHandler.handle(ctx, e);
        }
    }
}
