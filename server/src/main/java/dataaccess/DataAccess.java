package dataaccess;

import model.*;
import java.util.List;

public interface DataAccess {

    UserData addUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    AuthData addAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    int addGame(GameData game) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    List<GameData> getGames() throws DataAccessException;

    void updateGame(Integer gameID, GameData game) throws DataAccessException;

    void clearAll() throws DataAccessException;
}