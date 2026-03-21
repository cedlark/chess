package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import service.RegisterRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public AuthData register(String username, String password, String email){
        var body = new RegisterRequest(username, password, email);
        var request = buildRequest("POST","/user",body,null);
        var response = sendRequest(request);
        return handleResponse(response);
    }

    public AuthData login(String username, String password)

    public void logout(String authToken)

    public List<GameData> listGames(String authToken)

    public void createGame(String authToken, String gameName)

    public void joinGame(String authToken, String color, int gameID){

    }
    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }
}
