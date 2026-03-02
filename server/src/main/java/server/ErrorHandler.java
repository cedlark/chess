package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;

import java.util.Map;

public class ErrorHandler {

    public static void handle(Context ctx, DataAccessException e) {

        switch (e.getMessage()) {
            case "Error: bad request" -> ctx.status(400);
            case "Error: unauthorized" -> ctx.status(401);
            case "Error: already taken" -> ctx.status(403);
            default -> ctx.status(500);
        }

        ctx.json(Map.of("message", e.getMessage()));
    }
}
