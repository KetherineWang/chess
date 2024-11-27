package client.websocket;

import com.google.gson.Gson;
import websocket.ServerMessageObserver;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketCommunicator {
    private Session session;
    private final ServerMessageObserver observer;

    public WebSocketCommunicator(ServerMessageObserver observer) {
        this.observer = observer;
    }

    public void connect(String wsURL) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                System.out.println("WebSocket client connection established.");
                session.addMessageHandler(String.class, this::onMessage);
            }

            private void onMessage(String message) {
                try {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                } catch (Exception ex) {
                    observer.notify(new ErrorMessage("Error parsing WebSocket server message: " + ex.getMessage()));
                }
            }
        }, URI.create(wsURL));
    }

    public void send(String message) throws Exception {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        } else {
            throw new IllegalStateException("WebSocket client is not connected.");
        }
    }

    public void close() throws Exception {
        if (session != null) {
            session.close();
        }
    }
}
