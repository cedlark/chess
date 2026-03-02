package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        List<ChessMove> moves = new ArrayList<ChessMove>();

        int[][] directions = new int[][]{
                {1,0}, {-1,0}, {0,1}, {0,-1}
        };
        for (int[] dir : directions){
            int newR = myPosition.getRow() + dir[0];
            int newC = myPosition.getColumn() + dir[1];

            while (inBounds(newR, newC)){
                ChessPosition end = new ChessPosition(newR, newC);
                ChessPiece target = board.getPiece(end);
                if (target == null){
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != color) {
                        moves.add(new ChessMove(myPosition, end, null));

                    }
                    break;
                }
                newR += dir[0];
                newC += dir[1];
            }




        }

        return moves;
    }

    private static Boolean inBounds(int r, int c){
        return (r >= 1 && r <= 8 && c >= 1 && c <= 8);
    }
}
