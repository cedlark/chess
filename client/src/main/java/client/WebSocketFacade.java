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

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler handler) throws ResponseException {
        try {
            url = url.replace("http","ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = handler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message){
                    handleMessage(message);
                }
            });
        }
        catch(Exception ex){
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
    public void handleMessage(String message){
        Gson gson = new Gson();
        ServerMessage base = gson.fromJson(message, ServerMessage.class);
        switch(base.getServerMessageType()){
            case LOAD_GAME:
                LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                notificationHandler.loadGame(load);
                break;
            case NOTIFICATION:
                NotificationMessage note = gson.fromJson(message, NotificationMessage.class);
                break;
            case ERROR:
                ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                break;
        }
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
    public void resign(String authToken, int gameID) throws IOException {
        UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        session.getBasicRemote().sendText(new Gson().toJson(resign));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        try {
            UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            session.getBasicRemote().sendText(new Gson().toJson(leave));
        } catch (IOException ex) {
            throw new IOException(IOException.Code.ServerError, ex.getMessage());
        }
    }

}

