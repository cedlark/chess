package server;

import chess.ChessGame;
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
            String token = ctx.header("authorization");
            if (token == null || token.isBlank()) {
                throw new DataAccessException("Error: unauthorized");
            }
            JoinGameBody body = gson.fromJson(ctx.body(), JoinGameBody.class);
            if (body == null) {
                throw new DataAccessException("Error: bad request");
            }
            ChessGame.TeamColor color;
            try {
                color = body.playerColor() == null ? null : ChessGame.TeamColor.valueOf(body.playerColor());
            } catch (IllegalArgumentException ex) {
                throw new DataAccessException("Error: bad request");
            }
            JoinRequest req = new JoinRequest(token, color, body.gameID());
            gameService.joinGame(req);
            ctx.status(200).json(Map.of());
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
