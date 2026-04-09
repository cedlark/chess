package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessClient;
import client.WebSocketFacade;
import model.GameData;
import java.util.List;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class InGame {
    private final Scanner scanner;
    private final ChessClient client;
    private final String color;
    private final WebSocketFacade ws;

    public InGame(Scanner scanner, ChessClient client, String color, WebSocketFacade ws){
        this.scanner = scanner;
        this.client = client;
        this.color = color;
        this.ws = ws;
    }
    public void playGame() throws IOException {
        ws.enterGame(client.getAuthToken(), client.getCurrentGame().getGameId());
        System.out.println("Joined Game");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (Objects.equals(input, "leave")){
                leave();
                break;
            }
            if (color.equalsIgnoreCase("observe")){
                observeEval(input);
            }else {
                eval(input);
            }
        }
    }
    private void observeEval(String input){
        switch(input){
            case "help":
                obHelp();
                break;
            case "redraw":
                redraw();
                break;
            default:
                System.out.println("Unknown command");
        }
    }
    private void obHelp(){
        System.out.println("Commands:");
        System.out.println("help - show commands");
        System.out.println("redraw - redraws chess board");
        System.out.println("leave - leave game");
    }
    private void eval(String input) throws IOException {
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
        client.drawBoard(client.getCurrentGame(), color, null);
    }

    public void makeMove(){
        try {

            List<String> letters = List.of("a","b","c","d","e","f","g","h");
            List<String> numbers = List.of("1","2","3","4","5","6","7","8");
            System.out.println("row of piece to move");
            String sr = scanner.nextLine();
            System.out.println("column of piece to move");
            String sc = scanner.nextLine().toLowerCase();
            System.out.println("row of new square");
            String er = scanner.nextLine();
            System.out.println("column of new square");
            String ec = scanner.nextLine().toLowerCase();
            if(!numbers.contains(sr) || !numbers.contains(er) || !letters.contains(sc) || !letters.contains(ec)){
                System.out.println("Invalid move");
                return;
            }
            int startCol = sc.toLowerCase().charAt(0) - 'a' + 1;
            int endCol = ec.toLowerCase().charAt(0) - 'a' + 1;
            int startRow = Integer.parseInt(sr);
            int endRow = Integer.parseInt(er);

            ChessPosition start = new ChessPosition(Integer.parseInt(sr), startCol);
            ChessPosition end = new ChessPosition(Integer.parseInt(er), endCol);
            ChessPiece.PieceType promotion = null;
            ChessPiece piece = client.getCurrentGame().getGame().getBoard().getPiece(start);
            if(piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN){
                boolean whitePromotes = piece.getTeamColor() == ChessGame.TeamColor.WHITE && endRow == 8;
                boolean blackPromotes = piece.getTeamColor() == ChessGame.TeamColor.BLACK && endRow == 1;
                if(whitePromotes || blackPromotes){
                    Collection<ChessMove> valid = client.getCurrentGame().getGame().validMoves(start);
                    boolean isValid = valid.stream().anyMatch(m -> m.getEndPosition().equals(end));
                    if(!isValid){
                        System.out.println("Invalid move");
                        return;
                    }
                    promotion = askPromotion();
                    if(promotion == null) return;
                }
            }
            ChessMove move = new ChessMove(start, end, promotion);
            try {
                ws.makeMove(move, client.getAuthToken(), client.getCurrentGame().getGameId());
            } catch (IOException e) {
                System.out.println("make move failed" + e);
            }
        } catch (Exception e){
            System.out.println("Error making the move" + e);
        }

    }
    private ChessPiece.PieceType askPromotion(){
        System.out.println("Pawn promotion! Choose a piece:");
        System.out.println("1 - Queen");
        System.out.println("2 - Rook");
        System.out.println("3 - Bishop");
        System.out.println("4 - Knight");
        String choice = scanner.nextLine();
        return switch(choice){
            case "1" -> ChessPiece.PieceType.QUEEN;
            case "2" -> ChessPiece.PieceType.ROOK;
            case "3" -> ChessPiece.PieceType.BISHOP;
            case "4" -> ChessPiece.PieceType.KNIGHT;
            default -> {
                System.out.println("Invalid choice");
                yield null;
            }
        };
    }
    public void resign() throws IOException {
        System.out.println("confirm resign yes/no");
        String confirm = scanner.nextLine();
        if (confirm.equals("yes")){
            ws.resign(client.getAuthToken(), client.getCurrentGame().getGameId());
        }

    }
    public void highlight(){
        System.out.println("Row");
        String row = scanner.nextLine();
        System.out.println("Column");
        String col = scanner.nextLine();
        int column = col.toLowerCase().charAt(0) - 'a' + 1;
        ChessPosition pos = new ChessPosition(Integer.parseInt(row), column);
        Collection<ChessMove> moves = client.getCurrentGame().getGame().validMoves(pos);
        client.drawBoard(client.getCurrentGame(), color, moves);
    }
    public void leave() throws IOException {
        ws.leaveGame(client.getAuthToken(), client.getCurrentGame().getGameId());
        System.out.println("Left Game");
    }



}
