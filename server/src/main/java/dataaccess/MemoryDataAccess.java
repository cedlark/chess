package dataaccess;

import model.*;
import org.eclipse.jetty.server.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryDataAccess {
    private int nextId = 1;
    final private HashMap<String, AuthData> authTokens = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, UserData> users = new HashMap<>();

    public GameData addGame(GameData game){
        game = new GameData(nextId++, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
        games.put(game.getGameId(), game);
        return game;
    }
    public GameData getGame(Integer gameId){
        return games.get(gameId);
    }
    public List<GameData> getGames() {
        return new ArrayList<>(games.values());
    }
    public void updateGame(Integer gameId, GameData game){
        game = new GameData(nextId++, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
        games.replace(gameId, game);
    }

    public AuthData addAuth(AuthData auth){
        String token = generateToken();
        auth = new AuthData(token, auth.getUsername());
        authTokens.put(token, auth);
        return auth;
    }
    public AuthData getAuth(String authToken){
        return authTokens.get(authToken);
    }
    public void deleteAuth(String authToken){
        authTokens.remove(authToken);
    }
    public UserData addUser(UserData user){
        user = new UserData(user.getUsername(), user.getPassword(), user.getEmail());
        users.put(user.getUsername(), user);
        return user;
    }
    public UserData getUser(String username){
        return users.get(username);
    }

    public void clearAll(){
        authTokens.clear();
        games.clear();
        users.clear();
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
