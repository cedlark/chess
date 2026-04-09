package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
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
        if (checkCmd(cmd, session)){
            return;
        }
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        GameData game= dataAccess.getGame(cmd.getGameID());

        connections.add(cmd.getGameID(), auth.getUsername(), session);
        connections.send(session, new LoadGameMessage(game));
        String message = buildConnectMessage(auth.getUsername(), game);
        connections.broadcast(cmd.getGameID(), session, new NotificationMessage(message));
        System.out.println("CONNECTED: " + auth.getUsername());
    }

    private void makeMove(MakeMoveCommand cmd, Session session) throws Exception {

        if (checkCmd(cmd, session)){
            return;
        }
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        GameData gameData = dataAccess.getGame(cmd.getGameID());
        if(gameData.getGame().getIsGameOver()){
            connections.send(session, new ErrorMessage("Game is already over"));
            return;
        }
        String username = auth.getUsername();
        ChessGame game = gameData.getGame();
        ChessMove move = cmd.getMove();
        if(move == null){
            connections.send(session, new ErrorMessage("Move required"));
            return;
        }
        boolean colorWhite = username.equals(gameData.getWhiteUsername());
        boolean colorBlack = username.equals(gameData.getBlackUsername());
        if(!colorWhite && !colorBlack){
            connections.send(session, new ErrorMessage("Observers cannot make moves"));
            return;
        }
        ChessGame.TeamColor playerColor = colorWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if(game.getTeamTurn() != playerColor){
            connections.send(session, new ErrorMessage("Not your turn"));
            return;
        }
        try{
            game.makeMove(move);
        }
        catch(Exception e){
            connections.send(session, new ErrorMessage("Invalid move"));
            return;
        }
        gameData.setGame(game);
        dataAccess.updateGame(cmd.getGameID(), gameData);
        connections.broadcastAll(cmd.getGameID(), new LoadGameMessage(gameData));
        connections.broadcast(cmd.getGameID(), session, new NotificationMessage(username + " made a move"));
        ChessGame.TeamColor nextTurn = game.getTeamTurn();
        String nextPlayer = nextTurn == ChessGame.TeamColor.WHITE ?
                gameData.getWhiteUsername() : gameData.getBlackUsername();
        if(game.isInCheckmate(nextTurn)){
            game.setIsGameOver(true);
            dataAccess.updateGame(cmd.getGameID(), gameData);
            connections.broadcastAll(cmd.getGameID(), new NotificationMessage(nextPlayer + " is in checkmate"));
        }
        else if(game.isInStalemate(nextTurn)){
            game.setIsGameOver(true);
            dataAccess.updateGame(cmd.getGameID(), gameData);
            connections.broadcastAll(cmd.getGameID(), new NotificationMessage("Stalemate"));
        }
        else if(game.isInCheck(nextTurn)){
            connections.broadcastAll(cmd.getGameID(), new NotificationMessage(nextPlayer + " is in check"));
        }
    }
    private boolean checkCmd(UserGameCommand cmd, Session session) throws DataAccessException, IOException {
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        GameData gameData = dataAccess.getGame(cmd.getGameID());
        if(auth == null){
            connections.send(session, new ErrorMessage("Invalid auth"));
            return true;
        }
        if(gameData == null){
            connections.send(session, new ErrorMessage("Game not found"));
            return true;
        }
        return false;
    }

    private void leave(UserGameCommand cmd, Session session) throws Exception{

        if (checkCmd(cmd, session)){
            return;
        }
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        GameData gameData = dataAccess.getGame(cmd.getGameID());

        String username = auth.getUsername();
        if(username.equals(gameData.getWhiteUsername())){
            gameData.setWhiteUsername(null);
        }
        else if(username.equals(gameData.getBlackUsername())){
            gameData.setBlackUsername(null);
        }
        dataAccess.updateGame(cmd.getGameID(),gameData);
        connections.remove(session);
        connections.broadcast(cmd.getGameID(), session, new NotificationMessage(username + " left the game"));
    }

    private void resign(UserGameCommand cmd, Session session) throws Exception{
        if (checkCmd(cmd, session)){
            return;
        }
        AuthData auth = dataAccess.getAuth(cmd.getAuthToken());
        GameData gameData = dataAccess.getGame(cmd.getGameID());
        String username = auth.getUsername();
        if(gameData.getGame().getIsGameOver()){
            connections.send(session, new ErrorMessage("Game is already over"));
            return;
        }
        if(!username.equals(gameData.getWhiteUsername()) && !username.equals(gameData.getBlackUsername())){
            connections.send(session, new ErrorMessage("Observers cannot resign"));
            return;
        }
        gameData.getGame().setIsGameOver(true);
        dataAccess.updateGame(cmd.getGameID(), gameData);
        connections.broadcastAll(cmd.getGameID(), new NotificationMessage(username + " resigned"));
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