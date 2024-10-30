package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    int createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    void clear() throws DataAccessException;
}