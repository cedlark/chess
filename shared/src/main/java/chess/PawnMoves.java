package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        List<ChessMove> moves = new ArrayList<ChessMove>();
        int startRow;
        int endRow;
        int direction;

        if (color == ChessGame.TeamColor.WHITE) {
            startRow = 2;
            endRow = 8;
            direction = 1;
        } else {
            startRow = 7;
            endRow = 1;
            direction = -1;
        }

        int oneRow = myPosition.getRow() + direction;
        if (inBounds(oneRow, myPosition.getColumn())){
            ChessPosition end = new ChessPosition(oneRow, myPosition.getColumn());
            ChessPiece target = board.getPiece(end);
            if (target == null && oneRow != endRow) {
                moves.add(new ChessMove(myPosition, end, null));
                if (myPosition.getRow() == startRow) {
                    int twoRow = oneRow + direction;
                    if (inBounds(twoRow, myPosition.getColumn())) {
                        ChessPosition end2 = new ChessPosition(twoRow, myPosition.getColumn());
                        ChessPiece target2 = board.getPiece(end2);
                        if (target2 == null) {
                            moves.add(new ChessMove(myPosition, end2, null));
                        }
                    }
                }
            }
            if (target == null && oneRow == endRow) {
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.BISHOP));
            }

        }
        int rightCol = myPosition.getColumn() + 1;
        if (inBounds(oneRow, rightCol)) {
            ChessPosition end = new ChessPosition(oneRow, rightCol);
            ChessPiece target = board.getPiece(end);
            if (target != null && target.getTeamColor() != color && oneRow != endRow) {
                moves.add(new ChessMove(myPosition, end, null));
            }
            if (target != null && target.getTeamColor() != color && oneRow == endRow) {
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.BISHOP));
            }
        }
        int leftCol = myPosition.getColumn() - 1;
        if (inBounds(oneRow, leftCol)) {
            ChessPosition end = new ChessPosition(oneRow, leftCol);
            ChessPiece target = board.getPiece(end);
            if (target != null && target.getTeamColor() != color && oneRow != endRow) {
                moves.add(new ChessMove(myPosition, end, null));
            }
            if (target != null && target.getTeamColor() != color && oneRow == endRow) {
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.BISHOP));
            }
        }


        return moves;
    }
    private static Boolean inBounds(int r, int c){
        return (r >= 1 && r <= 8 && c >= 1 && c <= 8);
    }
}
