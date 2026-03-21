package client;

import model.AuthData;
import model.GameData;

import java.util.List;

public class ServerFacade {
    public AuthData register(String username, String password, String email)

    public AuthData login(String username, String password)

    public void logout(String authToken)

    public List<GameData> listGames(String authToken)

    public void createGame(String authToken, String gameName)

    public void joinGame(String authToken, String color, int gameID)
}
