package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;
import service.RegisterRequest;
import service.RegisterResult;
import service.*;

import java.util.Map;

public class ClearHandler {
    private final ClearService clearService;
    private final Gson gson = new Gson();

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }
    public void handle(Context ctx){
        try {
            clearService.clear();
            ctx.status(200);
            ctx.json(Map.of());
        } catch (DataAccessException e){
            ctx.status(500);
            ctx.json(Map.of("message", e.getMessage()));
        }
    }
}
