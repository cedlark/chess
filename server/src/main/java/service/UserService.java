package service;
import dataaccess.MemoryDataAccess;
import model.*;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        try{
            var port = 8080;

            MemoryDataAccess dataAccess = new MemoryDataAccess();
            user = dataAccess.getUser()
        }

    }
    public LoginResult login(LoginRequest loginRequest) {

    }
    public void logout(LogoutRequest logoutRequest) {

    }
}
