package chess;

import java.util.Collection;

public class SlidingMoves {

    public static void addMoves(
            ChessBoard board,
            ChessPosition start,
            ChessGame.TeamColor color,
            int rowChange,
            int colChange,
            Collection<ChessMove> moves) {

        int r = start.getRow() + rowChange;
        int c = start.getColumn() + colChange;

        while (inBounds(r, c)) {
            ChessPosition end = new ChessPosition(r, c);
            ChessPiece target = board.getPiece(end);

            if (target == null) {
                moves.add(new ChessMove(start, end, null));
            } else {
                if (target.getTeamColor() != color) {
                    moves.add(new ChessMove(start, end, null));
                }
                break;
            }

            r += rowChange;
            c += colChange;
        }
    }

    private static boolean inBounds(int r, int c) {
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }
}