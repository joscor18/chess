package client;


import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(String username, String password, String email) throws Exception{
        var path = "/user";
        var reqBody = new RegisterRequest(username, password, email);
        var req = buildRequest("POST", path, reqBody);
        var res = sendRequest(req);
        return handleResponse(res, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception{
        var path = "/session";
        var reqBody = new LoginRequest(username, password);
        var req = buildRequest("POST", path, reqBody);
        var res = sendRequest(req);
        return handleResponse(res, AuthData.class);
    }

    public void clear() throws Exception{
        var path = "/db";
        var req = buildRequest("DELETE", path, null);
        var res = sendRequest(req);
        handleResponse(res, null);
    }

    public void logout(String authToken) throws Exception{
        var path = "/session";
        var req = buildRequest("DELETE", path, null, authToken);
        var res = sendRequest(req);
        handleResponse(res, null);
    }

    public CreateGameResponse createGames(String gameName, String authToken) throws Exception{
        var path = "/game";
        var reqBody = new CreateGameRequest(gameName);
        var req = buildRequest("POST", path, reqBody, authToken);
        var res = sendRequest(req);
        return handleResponse(res, CreateGameResponse.class);
    }

    public Collection<GameData> listGames(String authToken) throws Exception{
        var path = "/game";
        var request = buildRequest("GET", path, null, authToken);
        var response = sendRequest(request);
        var res = handleResponse(response, ListGameResponse.class);
        return res.games();
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws Exception{
        var path = "/game";
        var reqBody = new JoinGameRequest(playerColor, gameID);
        var req = buildRequest("PUT", path, reqBody, authToken);
        var response = sendRequest(req);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body){
        return buildRequest(method, path, body, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        if(authToken != null){
            request.setHeader("authorization", authToken);
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if(msg == null){
                msg = "Connection failed. Check server. \n";
            }
            throw new Exception(msg);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            String msg = "Error: " + status;
            if(response.body() != null){
                try{
                    var error = new Gson().fromJson(response.body(), java.util.Map.class);
                    if(error.containsKey("message")){
                        msg = (String) error.get("message");
                    }else {
                        msg = response.body();
                    }
                }catch (Exception ex){
                    msg = response.body();
                }
            }
            throw new Exception(msg);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
