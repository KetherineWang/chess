package server.websocket;

import dataaccess.DataAccessException;
import model.GameData;
import dataaccess.GameDAO;
import dataaccess.MySQLGameDAO;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebSocketHandler {
    private final GameDAO gameDAO = new MySQLGameDAO();
    private final Map<Session, String> clients = new HashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connected: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                handleConnectCommand(session, command);
            } else {
                System.out.println("Unsupported command received: " + command.getCommandType());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleConnectCommand(Session session, UserGameCommand command) {
        try {
            if(command.getAuthToken() == null || command.getGameID() == null) {
                sendErrorMessage(session, "Invalid CONNECT command. Missing auth token or game ID.");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Game not found for gameID: " + command.getGameID());
                return;
            }

            clients.put(session, command.getAuthToken());

            ServerMessage.LoadGameMessage loadGame = new ServerMessage.LoadGameMessage(gameData.chessGame());
            session.getRemote().sendString(new Gson().toJson(loadGame));

            ServerMessage.NotificationMessage notification = new ServerMessage.NotificationMessage(
                    String.format("User %s joined the game.", command.getAuthToken())
            );
            broadcastNotification(session, notification);
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage(session, "An error occurred while processing the CONNECT command.");
        }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ServerMessage.ErrorMessage error = new ServerMessage.ErrorMessage(errorMessage);
            session.getRemote().sendString(new Gson().toJson(error));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastNotification(Session session, ServerMessage.NotificationMessage notification) {
        try {
            String notificationJson = new Gson().toJson(notification);

            for (Session s : clients.keySet()) {
                if (s != session && s.isOpen()) {
                    s.getRemote().sendString(notificationJson);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, String reason) {
        System.out.println("WebSocket closed: " + session + " Reason: " + reason);
        clients.remove(session);
    }
}