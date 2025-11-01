package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.util.ArrayList;
import java.util.List;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var statement = conn.createStatement()){
                statement.executeUpdate("TRUNCATE TABLE user");
                statement.executeUpdate("TRUNCATE TABLE auth");
                statement.executeUpdate("TRUNCATE TABLE game");
            }
        }catch (SQLException | DataAccessException ex){
            throw new DataAccessException("clear failed",ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        var statement = "SELECT username, password, email FROM user WHERE username =?";
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String user = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    return new UserData(user, password, email);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to get user data: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException{
        var statement = "SELECT username, authToken FROM auth WHERE authToken =?";
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String token = rs.getString("authToken");
                    return new AuthData(username, token);
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Unable to get auth data: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement,auth.authToken(), auth.username());
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        var statement = "DELETE FROM auth WHERE authToken =?";
        executeUpdate(statement, authToken);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException{
        var statement = "INSERT INTO game (gameID, gameName, whiteUsername, blackUsername, gameJSON) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername(), game.gameJSON());
        return game;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, gameJSON FROM game WHERE gameID =?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int ID = rs.getInt("gameID");
                    String gameName = rs.getString("gameName");
                    String whiteUser = rs.getString("whiteUsername");
                    String blackUser = rs.getString("blackUsername");
                    String gameJSON = rs.getString("gameJSON");
                    return GameData.fromJSON(ID, gameName, whiteUser, blackUser, gameJSON);
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Unable to get game data: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public List<GameData> getGames() throws DataAccessException{
       List<GameData> games = new ArrayList<>();
        var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, gameJSON FROM game";
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(statement);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int gameID = rs.getInt("gameID");
                    String gameName = rs.getString("gameName");
                    String whiteUser = rs.getString("whiteUsername");
                    String blackUser = rs.getString("blackUsername");
                    String gameJSON = rs.getString("gameJSON");

                    GameData game = GameData.fromJSON(gameID, gameName, whiteUser, blackUser, gameJSON);
                    games.add(game);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to read game data: " + ex.getMessage(), ex);
        }
        return games;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);

                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("SQL update failed: " + statement,ex);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game (
              gameID int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `gameJSON` TEXT,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements){
                try (var preparedStatement = conn.prepareStatement(statement)){
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException ex){
            throw new DataAccessException("Database config failed",ex);
        }
    }
}
