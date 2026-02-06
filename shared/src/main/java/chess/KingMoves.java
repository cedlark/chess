package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoves {

    public static Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color){
        List<ChessMove> moves = new ArrayList<ChessMove>();
        for (int r = -1; r < 2; r++){
            for (int c = -1; c < 2; c++){
                int new_r = myPosition.getRow() + r;
                int new_c = myPosition.getColumn() + c;

                if (new_r < 1 | new_r > 8 | new_c < 1|new_c>8) continue;

                ChessPosition end = new ChessPosition(new_r, new_c);
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
