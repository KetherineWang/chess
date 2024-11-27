package server.websocket;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import model.AuthData;
import model.GameData;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Map<Session, String> connectedClients = new ConcurrentHashMap<>();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session.getRemoteAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);

            if (userGameCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                handleConnectCommand(session, userGameCommand);
            } else {
                sendError(session, "Unsupported command: " + userGameCommand.getCommandType());
            }
        } catch (Exception ex) {
            sendError(session, "Invalid message format: " + ex.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connectedClients.remove(session);
        System.out.println("Client disconnected: " + session.getRemoteAddress());
    }

    private void handleConnectCommand(Session session, UserGameCommand userGameCommand) {
        try {
            String username = getUsernameFromAuthToken(userGameCommand.getAuthToken());

            GameData gameData = gameDAO.getGame(userGameCommand.getGameID());
            if (gameData == null) {
                sendError(session, "Game not found.");
                return;
            }

            connectedClients.put(session, userGameCommand.getAuthToken());

            String message;
            if (gameData.whiteUsername().equals(username)) {
                message = "joined as white";
            } else if (gameData.blackUsername().equals(username)) {
                message = "joined as black";
            } else {
                message = "joined as observer";
            }

            ServerMessage.LoadGameMessage loadGameMessage = new ServerMessage.LoadGameMessage(gameData);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));

            ServerMessage.NotificationMessage notificationMessage = new ServerMessage.NotificationMessage(username + " " + message);
            broadcastToOtherClients(session, notificationMessage);
        } catch (DataAccessException ex) {
            sendError(session, "Error accessing game data: " + ex.getMessage());
        } catch (Exception e) {
            sendError(session, "Error handling CONNECT command: " + e.getMessage());
        }
    }

    private void sendError(Session session, String errorMessage) {
        try {
            ServerMessage.ErrorMessage error = new ServerMessage.ErrorMessage(errorMessage);
            session.getRemote().sendString(new Gson().toJson(error));
        } catch (Exception ex) {
            System.err.println("Error sending error message: " + ex.getMessage());
        }
    }

    private void broadcastToOtherClients(Session sender, ServerMessage message) {
        connectedClients.keySet().stream()
                .filter(session -> !session.equals(sender))
                .forEach(session -> {
                    try {
                        session.getRemote().sendString(new Gson().toJson(message));
                    } catch (Exception ex) {
                        System.err.println("Error broadcasting message: " + ex.getMessage());
                    }
                });
    }

    private String getUsernameFromAuthToken(String authToken) {
        try {
            AuthData authData = authDAO.getAuth(authToken);

            if (authData != null) {
                return authData.username();
            } else {
                return "Username not found.";
            }
        } catch (DataAccessException ex) {
            return "Error retrieving username: " + ex.getMessage();
        }
    }
}