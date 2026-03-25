package service;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import requests.*;

import java.util.UUID;

public class UserService {
    private final DataAccess dao;

    public UserService(DataAccess dao){
        this.dao = dao;
    }
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        if (username == null || password == null || email == null ||
                username.isBlank() || password.isBlank() || email.isBlank()){
            throw new DataAccessException("Error: bad request");
        }
        if (dao.getUser(username) != null){
            throw new DataAccessException("Error: already taken");
        }
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData newUser = new UserData(username, hashed, email);
        dao.addUser(newUser);
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        dao.addAuth(auth);
        return new RegisterResult(username, token);

    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || password == null ||
            username.isBlank() || password.isBlank()){
            throw new DataAccessException("Error: bad request");
        }
        if (dao.getUser(username) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        UserData user = dao.getUser(username);
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String token = MemoryDataAccess.generateToken();
        AuthData auth = new AuthData(token, username);
        dao.addAuth(auth);
        return new LoginResult(username, token);
    }
    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String token = logoutRequest.authToken();
        if (token == null || token.isBlank()){
            throw new DataAccessException("Error: bad request");
        }
        if (dao.getAuth(token) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        dao.deleteAuth(token);
    }
}
