package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public static final int BOARD_SIZE = 8;
    
    public ChessBoard() {
        
    }
    //copy constructor
    public ChessBoard(ChessBoard other) {
        for (int row = 0; row < BOARD_SIZE; row++){
            for (int col = 0; col< BOARD_SIZE; col++){
                squares[row][col] = other.squares[row][col];
            }
        }
    }//end copy constructor

    
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
//        throw new RuntimeException("Not implemented");
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
//        throw new RuntimeException("Not implemented");
        return squares[position.getRow()-1][position.getColumn()-1];

    }

    /**
     * Sets the board to the default starting board
     */
    public void resetBoard() {
//        throw new RuntimeException("Not implemented");
        //BOARD_SIZE defined as 8 in this class
        squares = new ChessPiece[BOARD_SIZE][BOARD_SIZE];

        // Only four rows are used at the start of a chess board
        // This for loop is used to pass over all the columns of the board.
        // Each iteration is placing 4 pieces on the current column
        for (int col = 1; col <= BOARD_SIZE; col++) {

            // BACK_ROW_ORDER is zero indexed while chess columns start at 1 must subtract 1
            //Set whites pieces on rows 1 and 2
            // row 2 is all pawns
            addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, BACK_ROW_ORDER[col - 1]));
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

            //Set Blacks pieces on rows 7 and 8
            // row 7 is all pawns
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, BACK_ROW_ORDER[col - 1]));
        }
    }

    /**
     * The order of pieces along a back rank from column 1 to 8
     * Both colors use the same sequence
     */
    private static final ChessPiece.PieceType[] BACK_ROW_ORDER = {
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.KING,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.ROOK
    };

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }
}
