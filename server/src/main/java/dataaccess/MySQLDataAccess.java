package dataaccess;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLDataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }
    public int addGame(GameData game){
        game = new GameData(nextId++, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
        games.put(game.getGameId(), game);
        return game.getGameId();
    }
    public GameData getGame(Integer gameId) throws DataAccessException {
        if (gameId == null) {
            throw new DataAccessException("Error: bad request");
        }
        return games.get(gameId);
    }
    public List<GameData> getGames() {
        return new ArrayList<>(games.values());
    }
    public void updateGame(Integer gameId, GameData game) throws DataAccessException {
        if (!games.containsKey(gameId)) {
            throw new DataAccessException("Error: bad request");
        }
        GameData updated = new GameData(gameId, game.getWhiteUsername(), game.getBlackUsername(), game.getGameName(), game.getGame());
        games.put(gameId, updated);
    }

    public AuthData addAuth(AuthData auth) throws DataAccessException {
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        executeUpdate(statement,
                auth.getAuthToken(),
                auth.getUsername());
        return auth;
    }
    public AuthData getAuth(String authToken) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DataAccessException("Error reading auth");
        }
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Error: invalid auth token");
        }
        authTokens.remove(authToken);
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
            throw new DataAccessException("Error reading user");
        }

        return null;
    }

    public void clearAll(){
        authTokens.clear();
        games.clear();
        users.clear();
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case PetType p -> ps.setString(i + 1, p.toString());
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
        } catch (SQLException e) {
            throw new DataAccessException("Error executing update");
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
            throw new DataAccessException("Failed to configure data base");
        }
    }

}
