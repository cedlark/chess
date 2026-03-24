package ui;

import client.ChessClient;
import client.ServerFacade;
import com.google.gson.JsonArray;
import model.GameData;
import service.GamesResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostLogin {
    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessClient client;

    public PostLogin(ServerFacade server, Scanner scanner, ChessClient client){
        this.server = server;
        this.scanner = scanner;
        this.client = client;
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
            for(int i=0;i<currentGames.size();i++){
                GameData gameData = currentGames.get(i);
                String white = gameData.getWhiteUsername() == null ? "-" : gameData.getWhiteUsername();
                String black = gameData.getBlackUsername() == null ? "-" : gameData.getBlackUsername();
                System.out.println((i+1)+". "+ gameData.getGameName()+ " | White: "+white+ " | Black: "+black);
            }
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
            for(int i=0;i<currentGames.size();i++){
                GameData gameData = currentGames.get(i);
                String white = gameData.getWhiteUsername()==null ? "-" : gameData.getWhiteUsername();
                String black = gameData.getBlackUsername()==null ? "-" : gameData.getBlackUsername();
                System.out.println((i+1)+". "+ gameData.getGameName()+ " | White: "+white+ " | Black: "+black);
            }
            System.out.print("Enter Game Number: ");
            int number = Integer.parseInt(scanner.nextLine());
            if(number < 1 || number > currentGames.size()){
                System.out.println("Invalid game number");
                return;
            }
            System.out.print("Enter Game Color: ");
            String color = scanner.nextLine().toLowerCase();
            GameData game = currentGames.get(number-1);
            server.joinGame(client.getAuthToken(), color, game.getGameId());
            System.out.println("Joined game");
        }
        catch(Exception e){
            System.out.println("Game join failed");
        }
    }
    public void observe(){
        System.out.println("Observe Game");
    }
}
