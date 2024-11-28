package client.websocket;

import com.google.gson.*;
import javax.websocket.*;
import java.net.URI;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;
import websocket.ServerMessageObserver;
import websocket.messages.ServerMessageDeserializer;

public class WebSocketCommunicator extends Endpoint {
    private final ServerMessageObserver serverMessageObserver;
    private Session session;
    private final Gson gson;

    public WebSocketCommunicator(ServerMessageObserver serverMessageObserver) {
        this.serverMessageObserver = serverMessageObserver;
        this.gson = new GsonBuilder().registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer()).create();
    }

    public void connect(String wsURL) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, new URI(wsURL));

        this.session.addMessageHandler(String.class, this::onMessage);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Connected to WebSocket server.");
    }

    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            serverMessageObserver.notify(serverMessage);
        } catch (Exception ex) {
            serverMessageObserver.notify(new ErrorMessage("Error processing server message: " + ex.getMessage()));
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Disconnected from WebSocket server: "  + closeReason.getReasonPhrase());
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void send(String message) throws Exception {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(message);
        } else {
            throw new IllegalStateException("Cannot send message: WebSocket session is not open.");
        }
    }
}