package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
        connections.put(username, connection);
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public Connection getConnection(String username) {
        return connections.get(username);
    }

    public void broadcast(String excludedUsername, String message) throws IOException {
        var closedConnections = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (!connection.username.equals(excludedUsername)) {
                    connection.send(message);
                }
            } else {
                closedConnections.add(connection);
            }
        }

        for (var connection : closedConnections) {
            connections.remove(connection.username);
        }
    }
}