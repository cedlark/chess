package ui;

import chess.ChessMove;
import chess.ChessPosition;
import client.ChessClient;
import client.ServerFacade;
import client.WebSocketFacade;
import model.GameData;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class InGame {
    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessClient client;
    private final GameData game;
    private final String color;
    private final WebSocketFacade ws;

    public InGame(ServerFacade server, Scanner scanner, ChessClient client,
                  GameData game, String color, WebSocketFacade ws){
        this.server = server;
        this.scanner = scanner;
        this.client = client;
        this.game = game;
        this.color = color;
        this.ws = ws;
    }
    public void PlayGame() throws IOException {
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

    public void makeMove() throws IOException {
        System.out.println("row of piece to move");
        String sr = scanner.nextLine();
        System.out.println("column of piece to move");
        String sc = scanner.nextLine();
        System.out.println("row of new square");
        String er = scanner.nextLine();
        System.out.println("column of new square");
        String ec = scanner.nextLine();
        ChessPosition start = new ChessPosition(Integer.parseInt(sr),Integer.parseInt(sc));
        ChessPosition end = new ChessPosition(Integer.parseInt(er),Integer.parseInt(ec));
        ChessMove move = new ChessMove(start, end, null);
        try {
            ws.makeMove(move, client.getAuthToken(), game.getGameId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void resign() throws IOException {
        System.out.println("confirm resign yes/no");
        String confirm = scanner.nextLine();
        if (confirm.equals("yes")){
            ws.resign(client.getAuthToken(), game.getGameId());
        }

    }
    public void highlight(){
        System.out.println("Row");
        String row = scanner.nextLine();
        System.out.println("Column");
        String col = scanner.nextLine();
        ChessPosition pos = new ChessPosition(Integer.parseInt(row), Integer.parseInt(col));
        var moves = game.getGame().validMoves(pos);
        client.drawHighlighted(game, color, moves);
    }
    public void leave() throws IOException {
        ws.leaveGame(client.getAuthToken(), game.getGameId());
        System.out.println("Left Game");
    }



}
