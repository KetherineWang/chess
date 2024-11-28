package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<Integer, Map<String, Connection>> gameConnections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session) {
        gameConnections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put(username, new Connection(username, session));
    }

    public void remove(String username) {
        gameConnections.values().forEach(connections ->
                connections.values().removeIf(connection -> connection.username.equals(username))
        );
        gameConnections.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public Connection getConnection(int gameID, String username) {
        Map<String, Connection> connections = gameConnections.get(gameID);
        return connections != null ? connections.get(username) : null;
    }

    public void broadcast(int gameID, String excludedUsername, String message) throws IOException {
        Map<String, Connection> connections = gameConnections.get(gameID);
        if (connections != null) {
            List<String> closedConnections = new ArrayList<>();
            for (var connection : connections.values()) {
                if (connection.session.isOpen() && !connection.username.equals(excludedUsername)) {
                    connection.send(message);
                } else if (!connection.session.isOpen()) {
                    closedConnections.add(connection.username);
                }
            }
            closedConnections.forEach(connections::remove);
        }
    }
}
