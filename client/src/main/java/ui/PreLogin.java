package ui;

import client.ClientMain;
import client.ServerFacade;
import model.AuthData;

import java.util.Scanner;

public class PreLogin {
    private final ServerFacade server;
    private final Scanner scanner;
    private final ClientMain client;

    public PreLogin(ServerFacade server, Scanner scanner, ClientMain client){
        this.server = server;
        this.scanner = scanner;
        this.client = client;
    }
    public void eval(String input){
        switch(input){
            case "help":
                help();
                break;
            case "login":
                login();
                break;
            case "register":
                register();
                break;
            default:
                System.out.println("Unknown command");
        }
    }
    public void help(){
        System.out.println("Commands:");
        System.out.println("help - show commands");
        System.out.println("login - login user");
        System.out.println("register - create account");
        System.out.println("quit - exit");
    }
    public void login(){
        try{
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            var authData = server.login(username,password);
            client.loginSuccess(authData.getAuthToken());

        } catch(Exception e){
            System.out.println("Login Failed:");
        }

    }
    public void register(){
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        var authData = server.register(username, password, email);
        client.loginSuccess(authData.getAuthToken());
    }
}
