package client.websocket;

import client.ChessClient;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;

public class GameplayWebSocketHandler extends Endpoint {
    private final ChessClient chessClient;

    public GameplayWebSocketHandler(ChessClient chessClient) {
        this.chessClient = chessClient;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("WebSocket connected to server.");

        // Register a message handler for incoming WebSocket messages
        session.addMessageHandler(String.class, message -> {
            try {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> System.out.println("Game loaded: " + message);
                    case ERROR -> System.err.println("Error: " + message);
                    case NOTIFICATION -> System.out.println("Notification: " + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onError(Session session, Throwable thr) {
        System.err.println("WebSocket error: " + thr.getMessage());
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed: " + closeReason.getReasonPhrase());
    }
}