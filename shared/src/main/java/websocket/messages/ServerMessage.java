package websocket.messages;

import model.GameData;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    public static class LoadGameMessage extends ServerMessage {
        private final GameData game;

        public LoadGameMessage(GameData game) {
            super(ServerMessageType.LOAD_GAME);
            this.game = game;
        }

        public GameData getGame() {
            return game;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof LoadGameMessage)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            LoadGameMessage that = (LoadGameMessage) o;
            return Objects.equals(getGame(), that.getGame());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), getGame());
        }
    }

    public static class NotificationMessage extends ServerMessage {
        private final String message;

        public NotificationMessage(String message) {
            super(ServerMessageType.NOTIFICATION);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof NotificationMessage)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            NotificationMessage that = (NotificationMessage) o;
            return Objects.equals(getMessage(), that.getMessage());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), getMessage());
        }
    }

    public static class ErrorMessage extends ServerMessage {
        private final String errorMessage;

        public ErrorMessage(String errorMessage) {
            super(ServerMessageType.ERROR);
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof ErrorMessage)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            ErrorMessage that = (ErrorMessage) o;
            return Objects.equals(getErrorMessage(), that.getErrorMessage());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), getErrorMessage());
        }
    }
}
