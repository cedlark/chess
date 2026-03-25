package client;
import java.util.Scanner;

import ui.PostLogin;
import ui.PreLogin;

public class ChessClient {
    private final Scanner scanner;

    private boolean loggedIn = false;
    private String authToken = null;

    private final PreLogin preLogin;
    private final PostLogin postLogin;

    public ChessClient(String serverUrl){

        ServerFacade server = new ServerFacade(serverUrl);

        scanner = new Scanner(System.in);

        preLogin = new PreLogin(server, scanner, this);

        postLogin = new PostLogin(server, scanner, this);

    }

    public void run() throws Exception {
        System.out.println("Welcome to Chess! Type 'help' to get started.");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equals("quit")) {

                if(loggedIn){
                    postLogin.logout();
                }
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
