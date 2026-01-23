package chess;

import java.util.Collection;
import java.util.HashSet;
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
//        ChessPiece piece = board.getPiece(myPosition);
//        if (piece.getPieceType() == PieceType.BISHOP) {
//            return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
//        }
//        return List.of();
        Set<ChessMove> moves = new HashSet<>();
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
        for (int dr = -1; dr <=1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                tryAdd(board, p, p.getRow() + dr, p.getColumn() + dc, out, null);
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
    private void queenMoves(ChessBoard board, ChessPosition p, Set <ChessMove> out) {
        bishopMoves(board, p, out);
        rookMoves(board, p, out);
    }
    private void slideMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out, int [][] directions) {
        for (int[] dir : directions) {
            int r = p.getRow() + dir[0];
            int c = p.getColumn() + dir[1];
            while (inBounds(r,c)) {
                ChessPiece target = board.getPiece(new ChessPosition(r,c));
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

    private void addPawnAdvance(ChessPosition start, int endRow, int endCol, int promoRow, Set<ChessMove> out) {
        ChessPosition end = new ChessPosition(endRow, endCol);
        if (endRow == promoRow) {
            out.add(new ChessMove(start, end, PieceType.QUEEN));
            out.add(new ChessMove(start, end, PieceType.ROOK));
            out.add(new ChessMove(start, end, PieceType.BISHOP));
            out.add(new ChessMove(start, end, PieceType.KNIGHT));
        } else {
            out.add(new ChessMove(start, end, null));
        }
    }


    private void pawnMoves(ChessBoard board, ChessPosition p, Set<ChessMove> out) {
        int dir = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promoRow = (pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int oneRow = p.getRow() + dir;

        // Forward move
        if (inBounds(oneRow, p.getColumn()) &&
                board.getPiece(new ChessPosition(oneRow, p.getColumn())) == null) {

            addPawnAdvance(p, oneRow, p.getColumn(), promoRow, out);

            // Double move
            int twoRow = p.getRow() + 2 * dir;
            if (p.getRow() == startRow &&
                    board.getPiece(new ChessPosition(twoRow, p.getColumn())) == null) {
                out.add(new ChessMove(p, new ChessPosition(twoRow, p.getColumn()), null));
            }
        }

        // Diagonal captures (EDGE SAFE)
        int[] capCols = {p.getColumn() - 1, p.getColumn() + 1};
        for (int c : capCols) {
            if (c < 1 || c > 8) continue;

            ChessPosition end = new ChessPosition(oneRow, c);
            ChessPiece target = board.getPiece(end);

            if (target != null && target.getTeamColor() != pieceColor) {
                addPawnAdvance(p, oneRow, c, promoRow, out);
            }
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessPiece that)) return false;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
