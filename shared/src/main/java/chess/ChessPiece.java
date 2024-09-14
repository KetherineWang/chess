package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // BishopMove
        if (pieceType == PieceType.BISHOP) {
            int[][] directions = {
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
            };

            for (int[] direction : directions) {
                int rowChange = direction[0];
                int colChange = direction[1];
                int currentRow = myPosition.getRow();
                int currentCol = myPosition.getColumn();

                while (true) {
                    currentRow += rowChange;
                    currentCol += colChange;

                    if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                        break;
                    }

                    ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
                    ChessPiece occupyingPiece = board.getPiece(newPosition);

                    if (occupyingPiece == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    } else {
                        if (occupyingPiece.getTeamColor() != this.teamColor) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }

                        break;
                    }
                }
            }
        }

        return validMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                "}";
    }
}
