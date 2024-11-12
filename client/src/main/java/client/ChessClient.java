package client;

import model.*;
import java.util.List;

import exception.ResponseException;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final ChessApp chessApp;
    private String authToken;
    private List<GameData> currentGameList;

    public ChessClient(String serverURL, ChessApp chessApp) {
        this.serverFacade = new ServerFacade(serverURL);
        this.chessApp = chessApp;
    }

    public String register(String username, String password, String email) {
        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult registerResult = serverFacade.register(registerRequest);
            this.authToken = registerResult.authToken();

            chessApp.switchToPostLogin(authToken);
            return String.format("Registration successful. You are now logged in as %s.", username);
        } catch (ResponseException ex) {
            return handleError(ex, "register");
        } catch (Exception e) {
            return "Error: An unexpected error occurred during registration.";
        }
    }

    public String login(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = serverFacade.login(loginRequest);
            this.authToken = loginResult.authToken();

            chessApp.switchToPostLogin(authToken);
            return String.format("Login successful. You are now logged in as %s.", username);
        } catch (ResponseException ex) {
            return handleError(ex, "login");
        } catch (Exception e) {
            return "Error: An unexpected error occurred during login.";
        }
    }

    public String logout() throws ResponseException {
        assertLoggedIn();

        try {
            serverFacade.logout(authToken);
            authToken = null;

            chessApp.switchToPreLogin();
            return "Logged out successfully.";
        } catch (ResponseException ex) {
            return handleError(ex, "logout");
        } catch (Exception e) {
            return "Error: An unexpected error occurred during logout.";
        }
    }

    public String createGame(String gameName) throws ResponseException {
        assertLoggedIn();

        try {
            CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
            CreateGameResult createGameResult = serverFacade.createGame(authToken, createGameRequest);
            return String.format("Game created successfully. Game ID: %d", createGameResult.gameID());
        } catch (ResponseException ex) {
            return handleError(ex, "createGame");
        } catch (Exception ex) {
            return "Error: An unexpected error occurred while creating the game.";
        }
    }

    public String listGames() throws ResponseException {
        assertLoggedIn();

        try {
            currentGameList = serverFacade.listGames(authToken);
            StringBuilder listGamesResponse = new StringBuilder("Available games:\n");
            for (int i = 0; i < currentGameList.size(); i++) {
                GameData game = currentGameList.get(i);
                listGamesResponse.append(String.format("%d: %s (White: %s, Black: %s)\n",
                        i + 1, game.gameName(), game.whiteUsername(), game.blackUsername()));
            }
            return listGamesResponse.toString();
        } catch (ResponseException ex) {
            return handleError(ex, "listGames");
        } catch (Exception e) {
            return "Error: An unexpected error occurred while listing games.";
        }
    }

    public String joinGame(int gameID, String playerColor) throws ResponseException {
        assertLoggedIn();

        try {
            JoinGameRequest joinGameRequest = new JoinGameRequest(gameID, playerColor);
            serverFacade.joinGame(authToken, joinGameRequest);
            return String.format("Successfully join game %d as %s player.", gameID, playerColor);
        } catch (ResponseException ex) {
            return handleError(ex, "joinGame");
        } catch (Exception e) {
            return "Error: An unexpected error occurred while joining a game.";
        }
    }

    public String observeGame(int gameID) {
        return String.format("Observing game %d,", gameID);
    }

    private String handleError(ResponseException ex, String action) {
        switch (action) {
            case "register":
                if (ex.getMessage().contains("already taken")) {
                    return "Error: Username already exists. Please choose a different username.";
                }
                break;
            case "login":
                if (ex.getMessage().contains("unauthorized")) {
                    return "Error: Invalid username or password.";
                }
                break;
            case "logout":
                if (ex.getMessage().contains("unauthorized")) {
                    return "Error: You are not currently logged in, or you have already logged out.";
                }
                break;
            case "createGame":
                if (ex.getMessage().contains("unauthorized")) {
                    return "Error: You must be logged in to create a game.";
                }
                break;
            case "listGames":
                if (ex.getMessage().contains("unauthorized")) {
                    return "Error: You must be logged in to list the games.";
                }
                break;
            case "joinGame":
                if (ex.getMessage().contains("unauthorized")) {
                    return "Error: You must be logged in to join a game.";
                } else if (ex.getMessage().contains("already taken")) {
                    return "Error: The chosen player color has already been taken in the selected game.";
                }
                break;
            default:
                return "Error: " + ex.getMessage();
        }
        return "Error: Unable to process the request.";
    }

    private void assertLoggedIn() throws ResponseException {
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException(401, "You must be logged in to perform this action.");
        }
    }

    public List<GameData> getCurrentGameList() {
        return currentGameList;
    }
}