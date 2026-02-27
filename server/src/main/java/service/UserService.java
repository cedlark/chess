package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;

public class UserService {
    private final MemoryDataAccess dao;

    public UserService(MemoryDataAccess dao){
        this.dao = dao;
    }
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        if (username == null || password == null || email == null ||
                username.isBlank() || password.isBlank() || email.isBlank()){
            throw new DataAccessException("Error: Bad request");
        }
        if (dao.getUser(username) != null){
            throw new DataAccessException("Error: Already Taken");
        }
        UserData newUser = new UserData(username, password, email);
        dao.addUser(newUser);
        String token = MemoryDataAccess.generateToken();
        AuthData auth = new AuthData(token, username);
        dao.addAuth(auth);
        return new RegisterResult(username, token);

    }
    public LoginResult login(LoginRequest loginRequest) {

    }
    public void logout(LogoutRequest logoutRequest) {

    }
}
