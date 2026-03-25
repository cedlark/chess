package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessClient;
import client.ServerFacade;
import model.GameData;
import requests.GamesResult;

import java.io.PrintStream;
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
            GameData game = currentGames.get(number-1);
            server.joinGame(client.getAuthToken(), color, game.getGameId());
            System.out.println("Joined game");
            drawBoard(game, color);
        }
        catch(Exception e){
            System.out.println("Game join failed ");
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
            System.out.println("Observing game");
            drawBoard(game,"white");
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

    public void drawBoard(GameData game, String color){
        var out = new PrintStream(System.out);
        out.print(EscapeSequences.ERASE_SCREEN);
        ChessBoard board = game.getGame().getBoard();
        boolean whitePerspective = color.equalsIgnoreCase("white");
        drawHeaders(out, whitePerspective);

        int startRow = whitePerspective ? 8 : 1;
        int endRow   = whitePerspective ? 0 : 9;
        int rowStep  = whitePerspective ? -1 : 1;

        int startCol = whitePerspective ? 1 : 8;
        int endCol   = whitePerspective ? 9 : 0;
        int colStep  = whitePerspective ? 1 : -1;

        for(int row = startRow;
            row != endRow;
            row += rowStep){
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ");
            for(int col = startCol;
                col != endCol;
                col += colStep){
                boolean light = (row + col) % 2 == 0;
                setSquareColor(out, light);
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece = board.getPiece(pos);
                out.print(getPieceString(piece));
            }
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ");
            out.print(EscapeSequences.RESET_BG_COLOR);
            out.println();
        }
        drawHeaders(out, whitePerspective);
    }
    private void drawHeaders(PrintStream out, boolean whitePerspective){
        out.print("   ");
        if(whitePerspective){
            String[] headers = {" a "," b "," c "," d ", " e "," f "," g "," h "};
            for(String h : headers){
                out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK);
                out.print(h);
            }
        }
        else{
            String[] headers = {" h "," g "," f "," e ", " d "," c "," b "," a "};
            for(String h : headers){
                out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK);
                out.print(h);
            }
        }
        out.print(EscapeSequences.RESET_BG_COLOR);
        out.print(EscapeSequences.RESET_TEXT_COLOR);
        out.println();
    }
    private void setSquareColor(PrintStream out, boolean light){
        if(light){
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK);
        }
        else{
            out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_WHITE);
        }
    }
    private String getPieceString(ChessPiece piece){
        if(piece == null){
            return EscapeSequences.EMPTY;
        }
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();
        if(color == ChessGame.TeamColor.WHITE){
            return switch (type) {
                case KING -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_QUEEN;
                case ROOK -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_ROOK;
                case BISHOP -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_KNIGHT;
                case PAWN -> EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.WHITE_PAWN;
            };
        }
        else{
            return switch (type) {
                case KING -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_QUEEN;
                case ROOK -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_ROOK;
                case BISHOP -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_KNIGHT;
                case PAWN -> EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.BLACK_PAWN;
            };
        }
    }
}
