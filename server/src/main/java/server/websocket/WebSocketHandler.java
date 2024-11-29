package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);
        MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> handleConnect(session, userGameCommand);
            case MAKE_MOVE -> handleMakeMove(session, makeMoveCommand);
            default -> sendError(session, "Unsupported command: " + userGameCommand.getCommandType());
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        try {
            String username = authenticateUser(session, command.getAuthToken());
            if (username == null) { return; }

            GameData gameData = fetchGame(session, command.getGameID());
            if (gameData == null) { return; }

            String role = determineRole(username, gameData);

            connections.add(command.getGameID(), username, session);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData, role);
            connections.getConnection(command.getGameID(), username).send(gson.toJson(loadGameMessage));

            NotificationMessage connectNotification = new NotificationMessage(username + " joined as " + role);
            connections.broadcast(command.getGameID(), username, gson.toJson(connectNotification));
        } catch (Exception ex) {
            sendError(session, "Error processing CONNECT command: " + ex.getMessage());
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand makeMoveCommand) throws IOException {
        try {
            String username = authenticateUser(session, makeMoveCommand.getAuthToken());
            if (username == null) {
                return;
            }

            GameData gameData = fetchGame(session, makeMoveCommand.getGameID());
            if (gameData == null) {
                return;
            }

            String role = determineRole(username, gameData);

            ChessGame chessGame = gameData.chessGame();
            ChessMove chessMove = makeMoveCommand.getMove();

            try {
                chessGame.makeMove(chessMove);
            } catch (InvalidMoveException ex) {
                sendError(session, "Invalid move: " + ex.getMessage());
                return;
            }

            try {
                gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), chessGame));
            } catch (DataAccessException ex) {
                sendError(session, "Error updating game data: " + ex.getMessage());
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData, role);
            connections.getConnection(makeMoveCommand.getGameID(), username).send(gson.toJson(loadGameMessage));
            connections.broadcast(makeMoveCommand.getGameID(), username, gson.toJson(loadGameMessage));

            String startPosition = reformatPosition(chessMove.getStartPosition());
            String endPosition = reformatPosition(chessMove.getEndPosition());
            String makeMoveMessage = String.format("%s moved from %s to %s", username, startPosition, endPosition);
            NotificationMessage makeMoveNotification = new NotificationMessage(makeMoveMessage);
            connections.broadcast(makeMoveCommand.getGameID(), username, gson.toJson(makeMoveNotification));

            if (chessGame.isInCheck(chessGame.getTeamTurn())) {
                String checkMessage = String.format("%s is in check!", username);
                NotificationMessage checkNotification = new NotificationMessage(checkMessage);
                connections.broadcast(makeMoveCommand.getGameID(), username, gson.toJson(checkNotification));
            } else if (chessGame.isInCheckmate(chessGame.getTeamTurn())) {
                String checkmateMessage = String.format("%s is in checkmate!", username);
                NotificationMessage checkmateNotification = new NotificationMessage(checkmateMessage);
                connections.broadcast(makeMoveCommand.getGameID(), username, gson.toJson(checkmateNotification));
            } else if (chessGame.isInStalemate(chessGame.getTeamTurn())) {
                String stalemateMessage = "The game is in stalemate!";
                NotificationMessage stalemateNotification = new NotificationMessage(stalemateMessage);
                connections.broadcast(makeMoveCommand.getGameID(), username, gson.toJson(stalemateNotification));
            }
        } catch (Exception ex) {
            sendError(session, "Error processing MAKE_MOVE command: " + ex.getMessage());
        }
    }

    private String authenticateUser(Session session, String authToken) throws IOException {
        try {
            String username = retrieveUsername(authToken);
            if (username == null) {
                sendError(session, "Invalid auth token.");
            }
            return username;
        } catch (Exception ex) {
            sendError(session, "Error authenticating user: " + ex.getMessage());
            return null;
        }
    }

    private GameData fetchGame(Session session, int gameID) throws IOException {
        try {
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Game not found.");
            }
            return gameData;
        } catch (DataAccessException ex) {
            sendError(session, "Error fetching game data: " + ex.getMessage());
            return null;
        }
    }

    private String retrieveUsername(String authToken) {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            return authData != null ? authData.username() : null;
        } catch (DataAccessException ex) {
            return "Error retrieving username: " + ex.getMessage();
        }
    }

    private String determineRole(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return "white";
        } else if (username.equals(gameData.blackUsername())) {
            return "black";
        } else {
            return "observer";
        }
    }

    private String reformatPosition(ChessPosition chessPosition) {
        if (chessPosition == null) {
            return "";
        }

        char columnChar = (char) ('a' + chessPosition.getColumn() - 1);

        int row = chessPosition.getRow();
        return String.valueOf(columnChar) + row;
    }

    private void sendError(Session session, String error) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(error);
        session.getRemote().sendString(gson.toJson(errorMessage));
    }
}