package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MemoryDataAccess {
    private int nextId = 1;
    final private HashMap<String, AuthData> authTokens = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, UserData> users = new HashMap<>();

    public int addGame(GameData game){
        game = new GameData(nextId++, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
        games.put(game.getGameId(), game);
        return game.getGameId();
    }
    public GameData getGame(Integer gameId) throws DataAccessException {
        if (gameId == null) {
            throw new DataAccessException("Error: bad request");
        }
        return games.get(gameId);
    }
    public List<GameData> getGames() {
        return new ArrayList<>(games.values());
    }
    public void updateGame(Integer gameId, GameData game) throws DataAccessException {
        if (!games.containsKey(gameId)) {
            throw new DataAccessException("Error: bad request");
        }
        GameData updated = new GameData(gameId, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
        games.put(gameId, updated);
    }

    public AuthData addAuth(AuthData auth) throws DataAccessException {
        if (auth == null || auth.getAuthToken() == null || auth.getAuthToken().isBlank()) {
            throw new DataAccessException("Error: bad request");
        }
        authTokens.put(auth.getAuthToken(), auth);
        return auth;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Error: invalid auth token");
        }
        authTokens.remove(authToken);
    }
    public UserData addUser(UserData user){
        user = new UserData(user.getUsername(), user.getPassword(), user.getEmail());
        users.put(user.getUsername(), user);
        return user;
    }
    public UserData getUser(String username) throws DataAccessException {
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
