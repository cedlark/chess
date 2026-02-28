package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class LoginHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService){
        this.userService = userService;
    }
    public void handle(Context ctx){
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(request);
            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e){
            switch (e.getMessage()){
                case "Error: bad request" -> ctx.status(400);
                case "Error: unauthorized" -> ctx.status(401);
                default -> ctx.status(500);
            }
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}
