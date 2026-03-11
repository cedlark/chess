package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess{
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    public int addGame(GameData game) throws DataAccessException {
        String statement = "INSERT INTO game (whiteUsername,blackUsername,gameName, game) VALUES (?,?,?,?)";
        String json = new Gson().toJson(game.getGame());
        try (Connection conn = DatabaseManager.getConnection()) {
            PreparedStatement ps =
                    conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, game.getWhiteUsername());
            ps.setString(2, game.getBlackUsername());
            ps.setString(3, game.getGameName());
            ps.setString(4, json);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new DataAccessException("Error: failed to get game id");
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to add game");
        }
    }
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT gameID, whiteUsername,blackUsername,gameName, game FROM game WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, String.valueOf(gameID));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Error: unable to find game");
        }
        return null;
    }
    public List<GameData> getGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString("game"),ChessGame.class);
                        games.add(new GameData(rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game));
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Error: unable to list games");
        }
        return games;
    }
    public void updateGame(Integer gameId, GameData game) throws DataAccessException {
        String json = new Gson().toJson(game.getGame());
        String statement = """
                UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?
                """;
        executeUpdate(statement,
                game.getWhiteUsername(),
                game.getBlackUsername(),
                game.getGameName(),
                json,
                gameId);
    }

    public AuthData addAuth(AuthData auth) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        executeUpdate(statement,
                auth.getAuthToken(),
                auth.getUsername());
        return auth;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Error: unable to get auth");
        }
        return null;
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "DELETE  FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Error: unable to delete auth");
        }
    }
    public UserData addUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.getUsername(), user.getPassword(), user.getEmail());
        return user;
    }
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM user WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email")
                        );
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to get user");
        }

        return null;
    }

    public void clearAll() throws DataAccessException {
        try {
            executeUpdate("TRUNCATE auth");
            executeUpdate("TRUNCATE game");
            executeUpdate("TRUNCATE user");
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to clear all");
        }
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to execute update");
        }
    }



    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {}
            }
        }
    }
    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS user (
          username VARCHAR(50) NOT NULL,
          password VARCHAR(255) NOT NULL,
          email VARCHAR(100) NOT NULL,
          PRIMARY KEY (username)
        )
        """,

                    """
        CREATE TABLE IF NOT EXISTS auth (
          authToken VARCHAR(255) NOT NULL,
          username VARCHAR(50) NOT NULL,
          PRIMARY KEY (authToken)
        )
        """,

                    """
        CREATE TABLE IF NOT EXISTS game (
          gameID INT NOT NULL AUTO_INCREMENT,
          whiteUsername VARCHAR(50),
          blackUsername VARCHAR(50),
          gameName VARCHAR(100) NOT NULL,
          game TEXT NOT NULL,
          PRIMARY KEY (gameID)
        )
        """
    };
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Failed to configure data base");
        }
    }

}
