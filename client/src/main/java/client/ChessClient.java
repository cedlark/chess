package client;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Scanner;

import chess.*;
import model.GameData;
import ui.EscapeSequences;
import ui.PostLogin;
import ui.PreLogin;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class ChessClient implements NotificationHandler{
    private final Scanner scanner;

    private boolean loggedIn = false;
    private String authToken = null;

    private final PreLogin preLogin;
    private final PostLogin postLogin;

    private String currentColor;
    private GameData currentGame;

    public ChessClient(String serverUrl){

        ServerFacade server = new ServerFacade(serverUrl);

        scanner = new Scanner(System.in);

        preLogin = new PreLogin(server, scanner, this);

        postLogin = new PostLogin(server, scanner, this, serverUrl);

    }
    public GameData getCurrentGame(){
        return currentGame;
    }
    public void setCurrentGame(GameData game){
        currentGame = game;
    }
    public void setCurrentColor(String color){
        currentColor = color;
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

    public void drawBoard(GameData game, String color, Collection<ChessMove> moves){
        var out = new PrintStream(System.out);
        out.print(EscapeSequences.ERASE_SCREEN);
        ChessBoard board = game.getGame().getBoard();
        currentColor = color;
        boolean whitePerspective = color.equalsIgnoreCase("white") ||
                color.equalsIgnoreCase("observe");
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
            for(int col = startCol; col != endCol; col += colStep){
                ChessPosition pos = new ChessPosition(row, col);
                boolean light = (row + col) % 2 != 0;
                out.print(getSquareBackground(pos, moves, light));
                ChessPiece piece = board.getPiece(pos);
                out.print(getPieceString(piece));
            }
            out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ");
            out.print(EscapeSequences.RESET_BG_COLOR);
            out.println();
        }
        drawHeaders(out, whitePerspective);
    }

    private String getSquareBackground(ChessPosition pos, Collection<ChessMove> moves, boolean light) {
        if (moves != null && !moves.isEmpty()) {
            ChessPosition start = moves.iterator().next().getStartPosition();
            if (pos.equals(start)) {
                return EscapeSequences.SET_BG_COLOR_YELLOW;
            }
            for (ChessMove move : moves) {
                if (pos.equals(move.getEndPosition())) {
                    return EscapeSequences.SET_BG_COLOR_GREEN;
                }
            }
        }
        return light ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK :
                EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.SET_TEXT_COLOR_WHITE;
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
    public void loadGame(LoadGameMessage message){

        GameData gameData = message.getGame();

        this.currentGame = new GameData(
                gameData.getGameId(),
                gameData.getWhiteUsername(),
                gameData.getBlackUsername(),
                gameData.getGameName(),
                gameData.getGame()
        );

        redraw();
    }
    public void redraw(){
        drawBoard(currentGame, currentColor, null);
    }
    public void notify(NotificationMessage message){
        System.out.println();
        System.out.println(message.getNotification());
        System.out.print("> ");
    }
    public void error(ErrorMessage message){
        System.out.println("SERVER ERROR: ");
    }
}
