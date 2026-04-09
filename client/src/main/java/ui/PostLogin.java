package ui;

import client.ChessClient;
import client.ServerFacade;
import client.WebSocketFacade;
import model.GameData;
import requests.GamesResult;


import java.util.List;
import java.util.Scanner;

public class PostLogin{
    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessClient client;
    private final WebSocketFacade ws;


    public PostLogin(ServerFacade server, Scanner scanner, ChessClient client, String serverUrl){
        this.server = server;
        this.scanner = scanner;
        this.client = client;
        ws = new WebSocketFacade(serverUrl, client);

    }
    public void eval(String input) throws Exception {
        switch(input){
            case "help":
                help();
                break;
            case "logout":
                logout();
                break;
            case "create":
                create();
                break;
            case "list":
                listGames();
                break;
            case "play":
                play();
                break;
            case "observe":
                observe();
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    public void help(){
        System.out.println("Commands:");
        System.out.println("help - show commands");
        System.out.println("logout - logout");
        System.out.println("create - create game");
        System.out.println("list - list games");
        System.out.println("play - join game");
        System.out.println("observe - observe game");
    }
    public void logout() throws Exception {
        server.logout(client.getAuthToken());
        client.logout();
    }
    public void create(){
        try {
            System.out.print("Enter Game Name: ");
            String gameName = scanner.nextLine();
            server.createGame(client.getAuthToken(), gameName);
            System.out.println("Game Created");
        } catch (Exception e) {
            System.out.println("Game creation failed");
        }
    }
    public void listGames(){
        try{
            GamesResult result = server.listGames(client.getAuthToken());
            List<GameData> currentGames = result.games();
            if(currentGames.isEmpty()){
                System.out.println("No games found");
                return;
            }
            printGames(currentGames);
        }
        catch(Exception e){
            System.out.println("Game listing failed");
        }
    }
    public void play(){
        try{
            GamesResult result = server.listGames(client.getAuthToken());
            List<GameData> currentGames = result.games();
            if(currentGames == null || currentGames.isEmpty()){
                System.out.println("No games available");
                return;
            }
            printGames(currentGames);
            System.out.print("Enter Game Number: ");
            int number = Integer.parseInt(scanner.nextLine());
            if(number < 1 || number > currentGames.size()){
                System.out.println("Invalid game number");
                return;
            }
            System.out.print("Enter Game Color: ");
            String color = scanner.nextLine().toLowerCase();
            if(!color.equals("white") && !color.equals("black")){
                System.out.println("Invalid game color");
                return;
            }
            GameData game = currentGames.get(number-1);
            client.setCurrentGame(game);
            client.setCurrentColor(color);
            server.joinGame(client.getAuthToken(), color, game.getGameId());
            new InGame(scanner, client, color, ws).playGame();


        }
        catch(Exception e){
            System.out.println("Game join failed " );
        }
    }
    public void observe(){
        try{
            GamesResult result = server.listGames(client.getAuthToken());
            List<GameData> games = result.games();
            if(games == null || games.isEmpty()){
                System.out.println("No games available");
                return;
            }
            printGames(games);
            System.out.print("Enter Game Number: ");
            int number = Integer.parseInt(scanner.nextLine());

            if(number <1 || number>games.size()){
                System.out.println("Invalid game number");
                return;
            }
            GameData game = games.get(number-1);
            client.setCurrentGame(game);
            client.setCurrentColor("observe");
            new InGame(scanner, client, "observe", ws).playGame();

        }
        catch(Exception e){
            System.out.println("Observe failed");
        }
    }
    private void printGames(List<GameData> games){
        for(int i = 0; i < games.size(); i++){
            GameData g = games.get(i);
            String white = g.getWhiteUsername()==null ? "-" : g.getWhiteUsername();
            String black = g.getBlackUsername()==null ? "-" : g.getBlackUsername();

            System.out.println((i+1)+". "+ g.getGameName() +
                    " | White: "+white +
                    " | Black: "+black);
        }
    }



}
