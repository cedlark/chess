package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        List<ChessMove> moves = new ArrayList<ChessMove>();
        for (int r = -1; r < 2; r++){
            for (int c = -1; c < 2; c++){
                int newR = myPosition.getRow() + r;
                int newC = myPosition.getColumn() + c;

                if (newR < 1 | newR > 8 | newC < 1|newC>8){
                    continue;
                }

                ChessPosition end = new ChessPosition(newR, newC);
                ChessPiece target = board.getPiece(end);
                if (target == null){
                    moves.add(new ChessMove(myPosition, end, null));
                }
                if (target != null && target.getTeamColor() != color) {
                    moves.add(new ChessMove(myPosition, end, null));
                }

            }
        }

        return moves;
    }
}
