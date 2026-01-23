package chess;

import java.util.Collection;
import java.util.HastSet;
import java.util.Set;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;

    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Set<ChessMove> moves = new HastSet<>();
        switch (type) {
            case KING -> kingMoves(board, myPosition, moves);
            case QUEEN -> queenMoves(board, myPosition, moves);
            case ROOK -> rookMoves(board, myPosition, moves);
            case BISHOP -> bishopMoves(board, myPosition, moves);
            case KNIGHT -> knightMoves(board, myPosition, moves);
            case PAWN -> pawnMoves(board, myPosition, moves);
        }
        return moves;
    }
    private void kingMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out) {
        for (int r = -1; r <=1; r++;) {
            for (int c = -1; r <= 1; c++;){
                if (r == 0 && c == 0) continue;
                tryAdd(board, p, p.getRow() + r, p.getColumn() + c, out, null);
            }
        }
    }

    private void knightMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out) {
        for (int r = -2; r<=2; r++) {
            for (int c = -2; c<= 2; c++) {
                if (r*c == -2 | r*c == 2) {
                    tryAdd(board, p, p.getRow() + r, p.getColumn() + c, out, null);
                }
            }
        }
    }
    private void bishopMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out) {
        slideMoves(board, p, out, new int[][]{
                {1,1},{-1,1},{-1,-1},{1,-1}
        });
    }
    private void rookMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out) {
        slideMoves(board, p, out, new int [][]{
                {1,0}, {-1,0}, {0,1}, {0,-1}
        });
    }
    private void queenMoves(ChessBoard board, ChessPiece p, Set <ChessMove> out) {
        bishopMoves(board, p, out);
        rookMoves(board, p, out);
    }
    private void slideMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out, int [][] directions) {
        for (int dir : directions) {
            int r = p.getRow() + dir[0];
            int c = p.getColumn() + dir[1];
            while (inBounds(r,c)) {
                ChessPiece target = board.getPeice(new ChessPosition(r,c));
                if (target == null) {
                    out.add(new ChessMove(p, new ChessPosition(r,c), null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        out.add(new ChessMove(p, new ChessPosition(r,c), null));
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];

            }
        }
    }
    private void pawnMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out){
        int dir = (peiceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (peiceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (peiceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int oneStep = p.getRow() + dir;
        int twoStep = oneStep + dir;
        if (inBounds(oneStep, p.getColumn()) && board.getPiece(new ChessPosition(oneStep, p.getColumn())) == null) {
            movePawn(p, oneStep, p.getColumn(), promotionRow, out);
            if (p.getRow() == startRow && inBounds(twoStep, p.getColumn())
                    && board.getPiece(new ChessPosition(twoStep, p.getColumn())) == null) {
                movePawn(p, twoStep, p.getColumn(), promotionRow, out);
            }
        }
        int sideCol = {p.getColumn() +1,p.getColumn()-1};
        for (int col : sideCol){
            if (inBounds(oneStep, col)) continue;
            ChessPiece target = board.getPiece(new ChessPosition(oneStep, col));
            if (target != null && target.getTeamColor() != this.pieceColor) {
                movePawn(p, oneStep, col, promotionRow, out);
            }
        }
    }

    private void movePawn(ChessPosition start, int endRow, int endCol, int promotionRow, Set<ChessMove> out) {
        ChessPosition end = new ChessPosition(endRow, endCol);
        if (endRow == promotionRow) {
            out.add(new ChessMove(start, end, PieceType.QUEEN));
            out.add(new ChessMove(start, end, PieceType.ROOK));
            out.add(new ChessMove(start, end, PieceType.BISHOP));
            out.add(new ChessMove(start, end, PieceType.KNIGHT));
        } else {
            out.add(new ChessMove(start, end, null));
        }
    }
    private void tryAdd(ChessBoard board, ChessPosition start, int r, int c, Set<ChessMove> out, PieceType promo) {
        if (!inBounds(r, c)) return;
        ChessPosition end = new ChessPosition(r, c);
        ChessPiece target = board.getPiece(end);
        if (target == null || target.getTeamColor() != this.pieceColor) {
            out.add(new ChessMove(start, end, promo));
        }
    }

    private boolean inBounds(int r, int c) {
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }

    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        ChessPiece that = (ChessPiece) object;
        return java.util.Objects.equals(pieceColor, that.pieceColor) && type == that.type;
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), pieceColor, type);
    }
}
