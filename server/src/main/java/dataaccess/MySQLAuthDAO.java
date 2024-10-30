package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authData.username());
            preparedStatement.setString(2, authData.authToken());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error creating auth: " + ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT username FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement  = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);

            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    return new AuthData(username, authToken);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error getting auth: " + ex.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Error deleting auth: Auth token not found.");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error deleting auth: " + ex.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing auth table: " + ex.getMessage());
        }
    }
}
