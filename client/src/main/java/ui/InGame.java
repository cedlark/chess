package ui;

import client.ChessClient;
import client.ServerFacade;
import model.GameData;

import java.util.Objects;
import java.util.Scanner;

public class InGame {
    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessClient client;
    private final GameData game;
    private final String color;

    public InGame(ServerFacade server, Scanner scanner, ChessClient client, GameData game, String color){
        this.server = server;
        this.scanner = scanner;
        this.client = client;
        this.game = game;
        this.color = color;
    }
    public void PlayGame(){
        System.out.println("Joined Game");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (Objects.equals(input, "leave")){
                leave();
                break;
            }
            eval(input);
        }
    }
    private void eval(String input){
        switch(input){
            case "help":
                help();
                break;

            case "redraw":
                redraw();
                break;

            case "move":
                makeMove();
                break;

            case "resign":
                resign();
                break;

            case "highlight":
                highlight();
                break;

            default:
                System.out.println("Unknown command");
        }
    }
    public void help(){
        System.out.println("Commands:");
        System.out.println("help - show commands");
        System.out.println("redraw - redraws chess board");
        System.out.println("leave - leave game");
        System.out.println("move - make move");
        System.out.println("resign - give up");
        System.out.println("highlight - highlight legal moves");
    }
    public void redraw(){
        client.drawBoard(game, color);
    }

    public void makeMove(){

    }
    public void resign(){

    }
    public void highlight(){

    }
    public void leave(){

    }



}
