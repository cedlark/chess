package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import model.AuthData;
import webSocketMessages.Action;
import webSocketMessages.Notification;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            ServerMessage base = gson.fromJson(message, ServerMessage.class);
            switch(base.getServerMessageType()){
                case LOAD_GAME:
                    LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                    handler.loadGame(load);
                    break;

                case NOTIFICATION:
                    NotificationMessage note = gson.fromJson(message, NotificationMessage.class);
                    handler.notify(note);
                    break;

                case ERROR:
                    ErrorMessage err = gson.fromJson(message, ErrorMessage.class);
                    handler.error(err);
                    break;
            }
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterGame(String authToken, int gameID) throws IOException {
        try {
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            session.getBasicRemote().sendText(new Gson().toJson(connect));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
    public void observeGame(String authToken, int gameID) throws IOException {
        UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(connect));
    }
    public void makeMove(ChessMove move, String authToken, int gameID){
        MakeMoveCommand cmd = new MakeMoveCommand(authToken, gameID, move);
        session.getBasicRemote().sendText(new Gson().toJson(cmd));

    }

    public void leaveGame(String visitorName) throws ResponseException {
        try {
            var action = new Action(Action.Type.EXIT, visitorName);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

}

