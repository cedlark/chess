package ui;

import client.ClientMain;
import client.ServerFacade;
import model.AuthData;

import java.util.Scanner;

public class PreLogin {
    private ServerFacade server;
    private Scanner scanner;
    private ClientMain client;

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

    }
    public void login(){

    }
    public void register(){

    }
}
