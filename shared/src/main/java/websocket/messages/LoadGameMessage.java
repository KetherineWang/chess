package websocket.messages;

import model.GameData;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {
    private final GameData game;
    private final String role;

    public LoadGameMessage(GameData game, String role) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.role = role;
    }

    public GameData getGame() {
        return game;
    }

    public String getRole() {
        return role;
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