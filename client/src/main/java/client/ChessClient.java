package client;

import chess.ChessMove;
import chess.ChessBoard;
import chess.ChessPosition;
import model.*;
import ui.ChessBoardUI;
import client.websocket.WebSocketCommunicator;
import websocket.ServerMessageObserver;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;
import exception.ResponseException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade serverFacade;
    private final ChessApp chessApp;
    private String authToken;
    private String username;
    private List<GameData> currentGameList;
    private WebSocketCommunicator webSocketCommunicator;
    private final Gson gson = new Gson();

    public ChessClient(String serverURL, ChessApp chessApp) {
        this.serverFacade = new ServerFacade(serverURL);
        this.chessApp = chessApp;
        this.webSocketCommunicator = new WebSocketCommunicator(this);
    }

    public String register(String username, String password, String email) {
        try {
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult registerResult = serverFacade.register(registerRequest);
            this.authToken = registerResult.authToken();
            this.username = registerResult.username();

            chessApp.switchToPostLogin();
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
            this.username = loginResult.username();

            chessApp.switchToPostLogin();
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
            serverFacade.createGame(authToken, createGameRequest);

            currentGameList = serverFacade.listGames(authToken);

            return String.format("Game %s created successfully.", gameName);
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
                listGamesResponse.append(String.format("%d. %s (WHITE: %s, BLACK: %s)\n",
                        i + 1, game.gameName(), game.whiteUsername(), game.blackUsername()));
            }

            return listGamesResponse.toString();
        } catch (ResponseException ex) {
            return handleError(ex, "listGames");
        } catch (Exception e) {
            return "Error: An unexpected error occurred while listing games.";
        }
    }

    public String joinGame(int gameNumber, int gameID, String playerColor) throws ResponseException {
        assertLoggedIn();

        try {
            JoinGameRequest joinGameRequest = new JoinGameRequest(gameID, playerColor);
            serverFacade.joinGame(authToken, joinGameRequest);

            connectToGame(gameID);

            chessApp.switchToGameplay(gameID);
            return String.format("Successfully joined game %d as %s player.", gameNumber, playerColor);
        } catch (ResponseException ex) {
            return handleError(ex, "joinGame");
        } catch (Exception e) {
            return "Error: An unexpected error occurred while joining a game.";
        }
    }

    public String observeGame(int gameNumber, int gameID) {
        try {
            connectToGame(gameID);

            chessApp.switchToGameplay(gameID);
            return String.format("Observing game %d.", gameNumber);
        } catch (Exception ex) {
            return "Error: An unexpected error occurred while observing a game.";
        }
    }

    public String makeMove(int gameID, ChessMove chessMove) {
        try {
            MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameID, chessMove);
            webSocketCommunicator.send(gson.toJson(makeMoveCommand));
            return "Make move request sent to WebSocket server.";
        } catch (Exception ex) {
            return "Error: An unexpected error occurred while making a move.";
        }
    }

    public String leaveGame(int gameID) {
        try {
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            webSocketCommunicator.send(gson.toJson(leaveCommand));

            chessApp.switchToPostLogin();

            return "Leave request sent to WebSocket Server.";
        } catch (Exception ex) {
            return "Error: An unexpected error occurred while leaving a game.";
        }
    }

    public String resignGame(int gameID) {
        try {
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            webSocketCommunicator.send(gson.toJson(resignCommand));
            return "Resign request sent to WebSocket server.";
        } catch (Exception ex) {
            return "Error: An unexpected error occurred while resigning from a game.";
        }
    }

    public String highlightLegalMoves(int gameID, ChessPosition chessPosition) {
        try {
            GameData gameData = serverFacade.getGame(authToken, gameID);
            System.out.println("Fetched GameData: " + gson.toJson(gameData));
            if (gameData == null) {
                return "Error: Unable to fetch game data.";
            }

            Collection<ChessMove> legalMoves = gameData.chessGame().validMoves(chessPosition);
            if (legalMoves == null || legalMoves.isEmpty()) {
                return "No legal moves available for this piece at this position.";
            }

            List<ChessPosition> highlightedPositions = legalMoves.stream().map(ChessMove::getEndPosition).toList();
            highlightedPositions.add(chessPosition);

            ChessBoard chessBoard = gameData.chessGame().getBoard();
            boolean whiteBottom = username.equals(gameData.whiteUsername()) ||
                    (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername()));
            ChessBoardUI.drawHighlightedChessBoard(chessBoard, whiteBottom, highlightedPositions);

            return "Legal moves highlighted.";
        } catch (Exception ex) {
            return "Error: An unexpected error occurred while highlighting legal moves." + ex.getMessage();
        }
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
                } else if (ex.getMessage().contains("bad request")) {
                    return "Error: Invalid player color.";
                }
                break;
            default:
                return "Error: " + ex.getMessage();
        }
        return "Error: Unable to process the command.";
    }

    private void assertLoggedIn() throws ResponseException {
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException(401, "You must be logged in to perform this action.");
        }
    }

    public List<GameData> getCurrentGameList() {
        return currentGameList;
    }

    public String connectToGame(int gameID) {
        try {
            String wsURL = "ws://localhost:8080/ws";
            webSocketCommunicator = new WebSocketCommunicator(this);
            webSocketCommunicator.connect(wsURL);

            UserGameCommand connectCommand = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT, authToken, gameID
            );
            webSocketCommunicator.send(gson.toJson(connectCommand));

            return "Connected to game!";
        } catch (Exception ex) {
            return "Error connecting to game: " + ex.getMessage();
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch(serverMessage.getServerMessageType()) {
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) serverMessage;
                System.out.println(notificationMessage.getMessage());
            }
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) serverMessage;
                GameData gameData = loadGameMessage.getGame();

                boolean whiteBottom = username.equals(gameData.whiteUsername()) ||
                        (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername()));

                ChessBoard chessBoard = gameData.chessGame().getBoard();
                ChessBoardUI.drawChessBoard(chessBoard, whiteBottom);
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) serverMessage;
                System.err.println("Error: " + errorMessage.getErrorMessage());
            }

        }
    }
}