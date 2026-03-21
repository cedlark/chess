package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public AuthData register(String username, String password, String email){
        var body = Map.of("username",username,"password",password,"email",email);
        String json = gson.toJson(body);
    }

    public AuthData login(String username, String password)

    public void logout(String authToken)

    public List<GameData> listGames(String authToken)

    public void createGame(String authToken, String gameName)

    public void joinGame(String authToken, String color, int gameID)
}
