package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        List<ChessMove> moves = new ArrayList<ChessMove>();

        int[][] directions = new int[][]{
                {1,1}, {-1,1}, {-1,-1}, {1,-1}
        };
        for (int[] dir : directions){
            int new_r = myPosition.getRow() + dir[0];
            int new_c = myPosition.getColumn() + dir[1];

            while (inBounds(new_r, new_c)){
                ChessPosition end = new ChessPosition(new_r, new_c);
                ChessPiece target = board.getPiece(end);
                if (target == null){
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != color) {
                        moves.add(new ChessMove(myPosition, end, null));

                    }
                    break;
                }
                new_r += dir[0];
                new_c += dir[1];
            }




        }

        return moves;
    }

    private static Boolean inBounds(int r, int c){
        return (r >= 1 && r <= 8 && c >= 1 && c <= 8);
    }
}
