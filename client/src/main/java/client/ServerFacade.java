package client;

import model.*;
import com.google.gson.Gson;
import exception.ResponseException;

import java.io.*;
import java.net.*;
import java.util.List;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        String path = "/user";
        return makeRequest("POST", path, registerRequest, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        String path = "/session";
        return makeRequest("POST", path, loginRequest, LoginResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        String path = "/session";
        makeAuthRequest("DELETE", path, null, authToken, Void.class);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws ResponseException {
        String path = "/game";
        return makeAuthRequest("POST", path, createGameRequest, authToken, CreateGameResult.class);
    }

    public List<GameData> listGames(String authToken) throws ResponseException {
        String path = "/game";
        return makeAuthRequest("GET", path, null, authToken, ListGameResult.class).games();
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ResponseException {
        String path = "/game";
        makeAuthRequest("PUT", path, joinGameRequest, authToken, Void.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        return makeRequestWithAuth(method, path, request, null, responseClass);
    }

    private <T> T makeAuthRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        return makeRequestWithAuth(method, path, request, authToken, responseClass);
    }

    private <T> T makeRequestWithAuth(String method, String path, Object request, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            if (request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String requestData = new Gson().toJson(request);
                try (OutputStream requestBody = http.getOutputStream()) {
                    requestBody.write(requestData.getBytes());
                }
            }

            return handleResponse(http, responseClass);
        } catch (IOException ex) {
            throw new ResponseException(500, "Server connection error: " + ex.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T handleResponse(HttpURLConnection http, Class<T> responseClass) throws ResponseException {
        try {
            int statusCode = http.getResponseCode();

            if (statusCode >= 200 && statusCode < 300) {
                if (responseClass == Void.class) {
                    return null;
                } else {
                    try (InputStream responseBody = http.getInputStream();
                         InputStreamReader reader = new InputStreamReader(responseBody)) {
                        return new Gson().fromJson(reader, responseClass);
                    }
                }
            } else {
                try (InputStream errorBody = http.getErrorStream();
                     InputStreamReader reader = new InputStreamReader(errorBody)) {
                    String errorResponse = new Gson().fromJson(reader, ErrorResponse.class).message();
                    throw new ResponseException(statusCode, errorResponse);
                }
            }
        } catch (IOException e) {
            throw new ResponseException(500, "Error reading server response: " + e.getMessage());
        }
    }

    private static class ErrorResponse {
        private String message;

        public String message() {
            return message;
        }
    }
}