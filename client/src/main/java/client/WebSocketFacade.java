package client;


import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;

import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;


public class WebSocketFacade extends Endpoint {

    private Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler handler) {
        try {
            url = url.replace("http","ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = handler;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session s = container.connectToServer(this, socketURI);
            System.out.println("CONNECTED SESSION " + s.getId());

        } catch(Exception ex){
            System.out.println("error " + ex);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("CLIENT onOpen called: " + session.getId());
        this.session = session;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println("CLIENT MESSAGE HANDLER INVOKED: " + message);
                handleMessage(message);
            }
        });
    }
    public void handleMessage(String message){
        System.out.println("CLIENT RECEIVED RAW: " + message);
        Gson gson = new Gson();
        ServerMessage base = gson.fromJson(message, ServerMessage.class);
        switch(base.getServerMessageType()){
            case LOAD_GAME:
                System.out.println("CLIENT RECEIVED LOAD_GAME");
                LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                notificationHandler.loadGame(load);
                break;
            case NOTIFICATION:
                System.out.println("CLIENT RECEIVED NOTIFICATION");
                NotificationMessage note = gson.fromJson(message, NotificationMessage.class);
                notificationHandler.notify(note);
                break;
            case ERROR:
                System.out.println("CLIENT RECEIVED ERROR");
                ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                notificationHandler.error(error);
                break;
        }
    }

    public void enterGame(String authToken, int gameID) throws IOException {
        System.out.println("FACADE ENTER GAME");
        UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(connect));
    }
    public void observeGame(String authToken, int gameID) throws IOException {
        UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(connect));
    }
    public void makeMove(ChessMove move, String authToken, int gameID) throws IOException {
        System.out.println("FACADE INSTANCE " + this);
        MakeMoveCommand cmd = new MakeMoveCommand(authToken, gameID, move);
        String json = new Gson().toJson(cmd);
        System.out.println("CLIENT JSON SENT: " + json);
        this.session.getBasicRemote().sendText(json);
        System.out.println("end of WebSocketFacade");
    }
    public void resign(String authToken, int gameID) throws IOException {
        UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(resign));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(leave));

    }

}

