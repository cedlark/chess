package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoves {

    public static Collection<ChessMove> getMoves(
            ChessBoard board,
            ChessPosition pos,
            ChessGame.TeamColor color) {

        List<ChessMove> moves = new ArrayList<>();

        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        int startRow = color == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promotionRow = color == ChessGame.TeamColor.WHITE ? 8 : 1;

        int forwardRow = pos.getRow() + direction;

        addForwardMoves(board, pos, moves, direction, startRow, promotionRow);
        addCaptureMove(board, pos, moves, color, forwardRow, pos.getColumn() + 1, promotionRow);
        addCaptureMove(board, pos, moves, color, forwardRow, pos.getColumn() - 1, promotionRow);

        return moves;
    }

    private static void addForwardMoves(
            ChessBoard board,
            ChessPosition pos,
            List<ChessMove> moves,
            int direction,
            int startRow,
            int promotionRow) {

        int row = pos.getRow() + direction;
        int col = pos.getColumn();

        if (!inBounds(row, col)){
            return;
        }

        ChessPosition oneStep = new ChessPosition(row, col);
        if (board.getPiece(oneStep) != null){
            return;
        }

        if (row == promotionRow) {
            addPromotions(pos, oneStep, moves);
            return;
        }

        moves.add(new ChessMove(pos, oneStep, null));

        if (pos.getRow() == startRow) {
            int twoRow = row + direction;
            ChessPosition twoStep = new ChessPosition(twoRow, col);
            if (inBounds(twoRow, col) && board.getPiece(twoStep) == null) {
                moves.add(new ChessMove(pos, twoStep, null));
            }
        }
    }

    private static void addCaptureMove(
            ChessBoard board,
            ChessPosition pos,
            List<ChessMove> moves,
            ChessGame.TeamColor color,
            int row,
            int col,
            int promotionRow) {

        if (!inBounds(row, col)){
            return;
        }

        ChessPosition targetPos = new ChessPosition(row, col);
        ChessPiece target = board.getPiece(targetPos);

        if (target == null || target.getTeamColor() == color){
            return;
        }

        if (row == promotionRow) {
            addPromotions(pos, targetPos, moves);
        } else {
            moves.add(new ChessMove(pos, targetPos, null));
        }
    }

    private static void addPromotions(
            ChessPosition start,
            ChessPosition end,
            List<ChessMove> moves) {

        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
    }

    private static boolean inBounds(int r, int c) {
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }
}