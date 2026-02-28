package server;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.*;

import java.util.Map;

public class RegisterHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService){
        this.userService = userService;
    }
    public void handle(Context ctx){
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e){
            switch (e.getMessage()){
                case "Error: already taken" -> ctx.status(403);
                case "Error: bad request" -> ctx.status(400);
                default -> ctx.status(500);
            }
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}
