package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import service.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var body = new RegisterRequest(username, password, email);
        var request = buildRequest("POST","/user",body,null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        var body = new LoginRequest(username, password);
        var request = buildRequest("POST","/session",body,null);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE","/session",null,authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GamesResult listGames(String authToken) throws Exception {
        var request = buildRequest("GET","/game",null,authToken);
        var response = sendRequest(request);
        return handleResponse(response, GamesResult.class);
    }

    public void createGame(String authToken, String gameName) throws Exception {
        var body = new MakeGameRequest(authToken, gameName);
        var request = buildRequest("POST","/game",body,authToken);
        var response = sendRequest(request);
        handleResponse(response,null);
    }

    public void joinGame(String authToken, String color, int gameID) throws Exception {
        ChessGame.TeamColor teamColor;
        if (color.equals("white")){
            teamColor = ChessGame.TeamColor.WHITE;
        } else {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        var body = new JoinRequest(authToken, teamColor, gameID);
        var request = buildRequest("PUT","/game",body,authToken);
        var response = sendRequest(request);
        handleResponse(response,null);
    }
    private HttpRequest buildRequest(String method, String path, Object body, String authToken){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if(body != null){
            request.setHeader("Content-Type", "application/json");
        }
        if(authToken != null){
            request.setHeader("authorization", authToken);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request){
        if(request != null){
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        }
        else{
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception{
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception{
        var status = response.statusCode();
        if(status < 200 || status >= 300){
            String message = response.body();
            if(message == null || message.isBlank()){
                message = "Request failed: " + status;
            }
            throw new Exception(message);
        }
        if(responseClass != null){
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }
}
