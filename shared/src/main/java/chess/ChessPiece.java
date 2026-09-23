package chess;

import java.util.ArrayList;/////
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    /**
     * The four diagonal directions as (row, col) offset pairs.
     * Used by the bishop, queen and king
     */
    private static final int[][] DIAGONAL_DIRECTIONS =
            {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    /**
     * The four straight directions as (row, col) offset pairs.
     * Used by the rook
     */
    private static final int[][] STRAIGHT_DIRECTIONS =
            {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /**
     * All eight directions as (row, col) offset pairs
     * Used by the queen and the king
     */
    private static final int[][] ALL_DIRECTIONS =
            {{1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    /**
     * The knight's eight moves as (row, col) offset.
     * These are not directions to repeat like the other cases.
     * The knight applies each one once and each (row, col) offset corresponds to one of knights 8 moves.
     */
    private static final int[][] KNIGHT_OFFSETS =
            {{2, 1}, {2, -1}, {1, 2}, {1, -2},
            {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};

    /** The piece types a pawn may promote to on reaching the far rank. */
    private static final ChessPiece.PieceType[] PROMOTION_TYPES = {
            PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT
    };

    //These are used as modifiers for the offsetMoves function when certain pieces are passed
    /**
     * The longest run a sliding piece can make 7.  one edge of the board to the opposite edge
     * Used for calculations
     */
    private static final int MAX_SLIDE = ChessBoard.BOARD_SIZE - 1;

    /** One step for pieces that can not slide
     * Used by king and knight
     * */
    private static final int SINGLE_STEP = 1;

    //Default
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
//        throw new RuntimeException("Not implemented");
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
//        throw new RuntimeException("Not implemented");
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
//        throw new RuntimeException("Not implemented");

        //Switch Statement on the type of piece passed in
        switch (type) {
            case BISHOP:
                return offsetMoves(board, myPosition, DIAGONAL_DIRECTIONS, MAX_SLIDE);
            case ROOK:
                return offsetMoves(board, myPosition, STRAIGHT_DIRECTIONS, MAX_SLIDE);
            case QUEEN:
                return offsetMoves(board, myPosition, ALL_DIRECTIONS, MAX_SLIDE);
            case KING:
                return offsetMoves(board, myPosition, ALL_DIRECTIONS, SINGLE_STEP);
            case KNIGHT:
                return offsetMoves(board, myPosition, KNIGHT_OFFSETS, SINGLE_STEP);
            case PAWN:
                return pawnMoves(board, myPosition);
            default:
                return List.of();
        }
    }
    /**
     * Gets moves for any piece using offsets. Each offset is applied repeatedly up to
     * maxDistance times. It is stopped early at the edge of the board or at another
     * piece.
     *
     * @param offsets     Each entry is a (row, col) Offset step
     * @param maxDistance How many times to repeat each offset
     *                    SINGLE_STEP is passed for pieces that do not slide and is defined as 1
     */
    private Collection<ChessMove> offsetMoves(ChessBoard board, ChessPosition myPosition, int[][] offsets, int maxDistance) {

        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] offset : offsets) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            for (int i = 0; i < maxDistance; i++) {
                row += offset[0];
                col += offset[1];

                //Check if move puts the piece off the board
                if (!isOnBoard(row, col)) {
                    break;
                }

                //Code to Add valid moves to our running array
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece occupant = board.getPiece(newPosition);

                //If there is an occupant of the space check to see if it's the apposing team which is a valid move
                //breaks the loop without adding is space is blocked
                if (occupant != null) {
                    if (occupant.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
                //Add empty valid spaces.
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        return moves;
    }

    /**
     * Gets moves for a pawn.
     * which advances in one direction and captures diagonally
     * promotes on reaching the opposite side
     */
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        //White advances up the board toward row 8
        // Black advances down toward row 1
        int direction;
        int startRow;
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
        } else {
            direction = -1;
            startRow = 7;
        }

        //get row and col fo later use
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        //STARTING POSITION//
        //*****************
        //Pawns in the starting position have the choice of moving one or two spaces forward
        ChessPosition oneForward = new ChessPosition(row + direction, col);
        if (isOnBoard(row + direction, col)  && board.getPiece(oneForward) == null) {

            addPawnMove(moves, myPosition, oneForward);

            //Checks for if moving two spaces is allowed and adds if so
            //iff the space is blocked it is not allowed
            ChessPosition twoForward = new ChessPosition(row + 2 * direction, col);
            if (row == startRow && board.getPiece(twoForward) == null) {
                addPawnMove(moves, myPosition, twoForward);
            }
        }


        //DIAGONAL//
        //*****************
        //Pawns can move diagonally if there is an enemy piece
        int captureRow = row + direction;

        // Check the diagonal to the left
        int leftCol = col - 1;
        if (isOnBoard(captureRow, leftCol)) {
            ChessPosition target = new ChessPosition(captureRow, leftCol);
            ChessPiece leftSpaceValue = board.getPiece(target);

            boolean isEnemy = leftSpaceValue != null && leftSpaceValue.getTeamColor() != pieceColor;

            if (isEnemy) {
                addPawnMove(moves, myPosition, target);
            }
        }

        // Check the diagonal to the right
        int rightCol = col + 1;
        if (isOnBoard(captureRow, rightCol)) {
            ChessPosition target = new ChessPosition(captureRow, rightCol);
            ChessPiece rightSpaceValue = board.getPiece(target);

            boolean isEnemy = rightSpaceValue != null && rightSpaceValue.getTeamColor() != pieceColor;

            if (isEnemy) {
                addPawnMove(moves, myPosition, target);
            }
        }

        return moves;
    }

    /**
     * Adds a pawn move
     */
    private void addPawnMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        // A pawn promotes on the rank farthest from its own side
        // white advances upward to row 8
        // black advances downward to row 1.
        int promotionRow;
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            promotionRow = 8;
        } else {
            promotionRow = 1;
        }

        // An ordinary move does not result in a promotion but the move is added still
        if (end.getRow() != promotionRow) {
            moves.add(new ChessMove(start, end, null));
            return;
        }

        // Promotion occurs. The player chooses what the pawn becomes counting each choice as one move
        // promotion types are stored  under PROMOTION_TYPES
        for (PieceType promotionType : PROMOTION_TYPES) {
            moves.add(new ChessMove(start, end, promotionType));
        }
    }

    /**
     * @return whether the given one-based row and column fall within the board
     */
    private static boolean isOnBoard(int row, int col) {
        //ChessBoard.BOARD_SIZE is equal to 8 and defined as a final under chess board
        return row >= 1 && row <= ChessBoard.BOARD_SIZE
                && col >= 1 && col <= ChessBoard.BOARD_SIZE;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
