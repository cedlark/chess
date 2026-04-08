package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebSocketHandler
        implements WsConnectHandler,
        WsMessageHandler,
        WsCloseHandler {

    private final ConnectionManager connections =
            new ConnectionManager();

    private final DataAccess dataAccess;

    private final Gson gson =
            new Gson();

    public WebSocketHandler(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    @Override
    public void handleConnect(WsConnectContext ctx){
        ctx.enableAutomaticPings();
    }
    @Override
    public void handleMessage(WsMessageContext ctx){
        try{
            UserGameCommand base = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch(base.getCommandType()) {
                case CONNECT:
                    connect(base, ctx.session);
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand move = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(move, ctx.session);
                    break;
                case LEAVE:
                    leave(base, ctx.session);
                    break;
                case RESIGN:
                    resign(base, ctx.session);
                    break;
            }
        }
        catch(Exception ex){
            try{
                connections.send(ctx.session, new ErrorMessage(ex.getMessage()));
            }
            catch(IOException ignored){}
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx){
        connections.remove(ctx.session);
    }

    private void connect(UserGameCommand cmd, Session session) throws Exception{
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        if(auth == null){
            connections.send(session, new ErrorMessage("Invalid auth"));
            return;
        }
        GameData game = dataAccess.getGame(cmd.getGameID());

        if(game == null){
            connections.send(session, new ErrorMessage("Game not found"));
            return;
        }
        connections.add(cmd.getGameID(), auth.getUsername(), session);
        connections.send(session, new LoadGameMessage(game));
        String message = buildConnectMessage(auth.getUsername(), game);
        connections.broadcast(cmd.getGameID(), session, new NotificationMessage(message));
    }

    private void makeMove(MakeMoveCommand cmd, Session session) throws Exception{
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        if(auth == null){
            connections.send(session, new ErrorMessage("auth not valid"));
            return;
        }
        GameData gameData = dataAccess.getGame(cmd.getGameID());
        if(gameData == null){
            connections.send(session, new ErrorMessage("Game not found"));
            return;
        }
        ChessGame game = gameData.getGame();
        ChessMove move = cmd.getMove();
        game.makeMove(move);
        dataAccess.updateGame(cmd.getGameID(), gameData);
        connections.broadcastAll(cmd.getGameID(), new LoadGameMessage(gameData));
        connections.broadcast(cmd.getGameID(), session, new NotificationMessage(auth.getUsername() + " made a move"));
    }

    private void leave(UserGameCommand cmd, Session session) throws Exception{
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        if(auth == null){
            connections.send(session, new ErrorMessage("Invalid auth"));
            return;
        }
        GameData game = dataAccess.getGame(cmd.getGameID());
        connections.remove(session);
        connections.broadcast(cmd.getGameID(), session, new NotificationMessage(auth.getUsername() + " left the game"));
    }

    private void resign(UserGameCommand cmd, Session session) throws Exception{
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        if(auth == null){
            connections.send(session, new ErrorMessage("Invalid auth"));
            return;
        }
        connections.broadcastAll(cmd.getGameID(), new NotificationMessage(auth.getUsername() + " resigned"));
    }

    private String buildConnectMessage(String username, GameData game){
        if(username.equals(game.getWhiteUsername())){
            return username + " connected as white";
        }
        if(username.equals(game.getBlackUsername())){
            return username + " connected as black";
        }
        return username + " connected as observer";
    }
}