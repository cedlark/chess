package service;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;

public class GameService {
    private final MemoryDataAccess dao;

    public GameService(MemoryDataAccess dao){
        this.dao = dao;
    }
    public GamesResult listGames(GamesRequest request) throws DataAccessException {
        String token = request.authToken();
        if (dao.getAuth(token) == null){
            throw new DataAccessException("Error: Unauthorized");
        }
        return new GamesResult(dao.getGames());
    }
    public MakeGameResult createGame(MakeGameRequest request) throws DataAccessException {
        String token = request.token();
        String gameName = request.GameName();
        if (dao.getAuth(token) == null){
            throw new DataAccessException("Error: Unauthorized");
        }
        if (gameName == null || gameName.isBlank()) {
            throw new DataAccessException("Error: bad request");
        }
        GameData newGame = new GameData(0,null, null, gameName, new ChessGame());
        Integer gameId = dao.addGame(newGame);
        return new MakeGameResult(gameId);
    }
    public void joinGame(JoinRequest request) throws DataAccessException {
        String token = request.authToken();
        ChessGame.TeamColor color = request.color();
        Integer gameId = request.gameId();
        if (dao.getAuth(token) == null){
            throw new DataAccessException("Error: Unauthorized");
        }
        if (color == null || gameId == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData game = dao.getGame(gameId);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        String username = dao.getAuth(token).getUsername();
        if (color == ChessGame.TeamColor.WHITE){
            if (game.getWhiteUsername()!= null){
                throw new DataAccessException("Error: Already taken");
            }
            game = new GameData(game.getGameId(), username, game.getBlackUsername(), game.getGameName(), game.getGame());

        } else if (color == ChessGame.TeamColor.BLACK){
            if (game.getBlackUsername() != null){
                throw new DataAccessException("Error: Already taken");
            }
            game = new GameData(game.getGameId(), game.getWhiteUsername(), username, game.getGameName(), game.getGame());
        }
        dao.updateGame(gameId, game);

    }
}
