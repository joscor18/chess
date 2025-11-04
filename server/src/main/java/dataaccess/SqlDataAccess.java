package dataaccess;

import com.google.gson.Gson;
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
                statement.executeUpdate("DELETE FROM user");
                statement.executeUpdate("DELETE FROM auth");
                statement.executeUpdate("DELETE FROM game");
            }
        }catch (SQLException | DataAccessException ex){
            throw new DataAccessException("clear failed",ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username, email, password) VALUES ( ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(statement);

            ps.setString(1, user.username());
            ps.setString(2, user.email());
            ps.setString(3, user.password());
            ps.executeUpdate();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Unable to create user: " + ex.getMessage(), ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        var statement = "SELECT username, email, password FROM user WHERE username =?";
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String user = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    return new UserData(user, email, password);
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
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameJSON) VALUES ( ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, new Gson().toJson(game.game()));

                ps.executeUpdate();

                try(ResultSet keys = ps.getGeneratedKeys()){
                    if(keys.next()){
                        int gameID = keys.getInt(1);
                        return new GameData(gameID, game.gameName(), game.whiteUsername(), game.blackUsername(), game.game());
                    }else{
                        throw new DataAccessException("failed to get gameID");
                    }
                }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Unable to update game: " + ex.getMessage(), ex);
        }
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, gameJSON FROM game WHERE gameID =?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("gameID");
                    String gameName = rs.getString("gameName");
                    String whiteUser = rs.getString("whiteUsername");
                    String blackUser = rs.getString("blackUsername");
                    String gameJSON = rs.getString("gameJSON");
                    return GameData.fromJSON(id, gameName, whiteUser, blackUser, gameJSON);
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Unable to get game data: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE game SET gameName = ?, whiteUsername = ?, blackUsername = ?, gameJSON = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(statement);
            String gameJSON = game.gameJSON();

            ps.setString(1, game.gameName());
            ps.setString(2, game.whiteUsername());
            ps.setString(3, game.blackUsername());
            ps.setString(4, gameJSON);
            ps.setInt(5, game.gameID());

            int rows = ps.executeUpdate();
            if(rows == 0){
                throw new DataAccessException("No game found with gameID: "+game.gameID());
            }

        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Unable to update game: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<GameData> getGames() throws DataAccessException{
       List<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, gameJSON FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ResultSet rs = ps.executeQuery();
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
            PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
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
        } catch (SQLException ex) {
            throw new DataAccessException("SQL update failed: " + statement,ex);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(256) PRIMARY KEY,
              `email` varchar(256),
              `password` varchar(256) NOT NULL
              
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
