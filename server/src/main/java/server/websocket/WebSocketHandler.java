package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

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
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> handleConnect(session, userGameCommand);
            default -> sendError(session, "Unsupported command: " + userGameCommand.getCommandType());
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        try {
            String username = getUsernameFromAuthToken(command.getAuthToken());
            if (username == null) {
                sendError(session, "Invalid auth token.");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Game not found.");
                return;
            }

            String role = determineRole(username, gameData);

            connections.add(username, session);

            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            connections.getConnection(username).send(gson.toJson(loadGameMessage));

            NotificationMessage notificationMessage = new NotificationMessage(username + " " + role);
            connections.broadcast(username, gson.toJson(notificationMessage));
        } catch (DataAccessException ex) {
            sendError(session, "Error accessing game data: " + ex.getMessage());
        } catch (Exception e) {
            sendError(session, "Error handling CONNECT command: " + e.getMessage());
        }
    }

    private String determineRole(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return "joined as white";
        } else if (username.equals(gameData.blackUsername())) {
            return "joined as black";
        } else {
            return "joined as observer";
        }
    }

    private String getUsernameFromAuthToken(String authToken) {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            return authData != null ? authData.username() : null;
        } catch (DataAccessException ex) {
            return "Error retrieving username: " + ex.getMessage();
        }
    }

    private void sendError(Session session, String error) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(error);
        session.getRemote().sendString(gson.toJson(errorMessage));
    }
}
