package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMoves {

    public static Collection<ChessMove> getMoves(
            ChessBoard board,
            ChessPosition position,
            ChessGame.TeamColor color) {

        List<ChessMove> moves = new ArrayList<>();

        int[][] directions = {
                {1,0}, {-1,0}, {0,1}, {0,-1}
        };

        for (int[] d : directions) {
            SlidingMoves.addMoves(board, position, color, d[0], d[1], moves);
        }

        return moves;
    }
}