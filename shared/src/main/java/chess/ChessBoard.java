package chess;
import java.utils.Arrays;
import java.util.Objects
/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        board[position.getRow() - 1][position.getColumn() - 1] = peice;

    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() -1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */

    public void resetBoard()
        vector chess_board = [
                ["r","h","b","q","k","b","h","r"],
            ["p","p","p","p","p","p","p","p"],
            ["","","","","","","",""],
            ["","","","","","","",""],
            ["","","","","","","",""],
            ["","","","","","","",""],
            ["P","P","P","P","P","P","P","P"],
            ["R","H","B","K","Q","B","H","R"]]

    }

    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        ChessBoard that = (ChessBoard) object;
        return java.util.Objects.deepEquals(board, that.board) && java.util.Objects.equals(chess_board, that.chess_board);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.deepHashCode(board), chess_board);
    }

}
