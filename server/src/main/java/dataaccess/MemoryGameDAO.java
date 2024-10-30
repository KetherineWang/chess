package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    final private Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public List<GameData> listGames() { return new ArrayList<>(games.values()); }

    @Override
    public int createGame(GameData gameData) { games.put(gameData.gameID(), gameData); return gameData.gameID(); }

    @Override
    public GameData getGame(int gameID) {return games.get(gameID); }

    @Override
    public void updateGame(GameData gameData) { games.put(gameData.gameID(), gameData); }
}