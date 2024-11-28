package websocket.messages;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ServerMessageDeserializer implements JsonDeserializer<ServerMessage> {
    @Override
    public ServerMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ServerMessage.ServerMessageType serverMessageType = ServerMessage.ServerMessageType.valueOf(jsonObject.get("serverMessageType").getAsString());

        switch (serverMessageType) {
            case NOTIFICATION:
                return new Gson().fromJson(jsonObject, NotificationMessage.class);
            case ERROR:
                return new Gson().fromJson(jsonObject, ErrorMessage.class);
            case LOAD_GAME:
                return new Gson().fromJson(jsonObject, LoadGameMessage.class);
            default:
                throw new JsonParseException("Unknown server message type: " + serverMessageType);
        }
    }
}
