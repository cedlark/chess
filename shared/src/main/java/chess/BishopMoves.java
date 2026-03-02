package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoves {

    public static Collection<ChessMove> getMoves(
            ChessBoard board,
            ChessPosition position,
            ChessGame.TeamColor color) {

        List<ChessMove> moves = new ArrayList<>();

        int[][] directions = {
                {1,1}, {-1,1}, {-1,-1}, {1,-1}
        };

        for (int[] d : directions) {
            SlidingMoves.addMoves(board, position, color, d[0], d[1], moves);
        }

        return moves;
    }
}