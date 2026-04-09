package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public static class Connection {

        private final int gameID;
        private final String username;
        private final Session session;

        public Connection(int gameID, String username, Session session){
            this.gameID = gameID;
            this.username = username;
            this.session = session;

        }

        public int getGameID(){
            return gameID;
        }

        public String getUsername(){
            return username;
        }

        public Session getSession(){
            return session;
        }
    }

    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Session,Connection>>
            connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session){
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put
                (session, new Connection(gameID, username, session));

    }

    public void remove(Session session){
        for(var game : connections.values()){
            game.remove(session);
        }
    }

    public void send(Session session, ServerMessage msg) throws IOException{
        System.out.println("SERVER SENDING TO CLIENT: " + msg);
        if(session != null && session.isOpen()){
            session.getRemote().sendString(new Gson().toJson(msg));
        }
    }

    public void broadcast(int gameID, Session exclude, ServerMessage msg) throws IOException{
        var gameSessions = connections.get(gameID);
        if(gameSessions == null){
            System.out.println("broadcast: no sessions for game " + gameID);
            return;
        }

        String json = new Gson().toJson(msg);
        System.out.println("broadcast: sending to game " + gameID);
        System.out.println("broadcast payload: " + json);

        for(Session s : gameSessions.keySet()){
            System.out.println("broadcast session open? " + s.isOpen() + ", excluded? " + s.equals(exclude));
            if(s.isOpen() && !s.equals(exclude)){
                s.getRemote().sendString(json);
            }
        }
    }
    public void broadcastAll(int gameID, ServerMessage msg) throws IOException{
        var gameSessions = connections.get(gameID);
        if(gameSessions == null){
            System.out.println("broadcastAll: no sessions for game " + gameID);
            return;
        }

        String json = new Gson().toJson(msg);
        System.out.println("broadcastAll: sending to " + gameSessions.size() + " sessions");
        System.out.println("broadcastAll payload: " + json);

        for(Session s : gameSessions.keySet()){
            System.out.println("broadcastAll session open? " + s.isOpen());
            if(s.isOpen()){
                s.getRemote().sendString(json);
            }
        }
    }
}