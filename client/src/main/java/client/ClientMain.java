package client;
import java.util.Scanner;
import chess.*;
import ui.PostLogin;
import ui.PreLogin;

public class ClientMain {
    private final ServerFacade server;
    private final Scanner scanner;

    private boolean loggedIn = false;
    private String authToken = null;

    private PreLogin preLogin;
    private PostLogin postLogin;

    public ChessClient(int port){

        server = new ServerFacade(port);

        scanner = new Scanner(System.in);

        preLogin = new PreLogin(server, scanner, this);

        postLogin = new PostLogin(server, scanner, this);

    }

    public void run() {
        System.out.println("Welcome to Chess! Type 'help' to get started.");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equals("quit")) {
                break;
            }
            if(!loggedIn){
                preLogin.eval(input);
            }
            else{
                postLogin.eval(input);
            }
        }
    }
    public void loginSuccess(String token){
        authToken = token;
        loggedIn = true;
        System.out.println("Login successful");
    }
    public void logout(){
        authToken = null;
        loggedIn = false;
        System.out.println("Logged out");
    }
    public String getAuthToken(){
        return authToken;
    }
}
