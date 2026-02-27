package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;

public class GameService {
    private final MemoryDataAccess dao;

    GameService(MemoryDataAccess dao){
        this.dao = dao;
    }
    public GamesResult listGames(GamesRequest request) throws DataAccessException {
        String token = request.authToken();
        if (dao.getAuth(token) == null){
            throw new DataAccessException("Error: Unauthorized");
        }
        return new GamesResult(dao.getGames());
    }

}
