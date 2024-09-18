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
        return Rules.pieceRule(pieceType).moves(board, myPosition);
    }
//        Collection<ChessMove> validMoves = new ArrayList<>();
//
//        if (pieceType == PieceType.BISHOP) {
//            int[][] directions = {
//                    {1, 1},
//                    {1, -1},
//                    {-1, 1},
//                    {-1, -1}
//            };
//
//            for (int[] direction : directions) {
//                int rowChange = direction[0];
//                int colChange = direction[1];
//                int currentRow = myPosition.getRow();
//                int currentCol = myPosition.getColumn();
//
//                while (true) {
//                    currentRow += rowChange;
//                    currentCol += colChange;
//
//                    if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
//                        break;
//                    }
//
//                    ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
//                    ChessPiece occupyingPiece = board.getPiece(newPosition);
//
//                    if (occupyingPiece == null) {
//                        validMoves.add(new ChessMove(myPosition, newPosition, null));
//                    } else {
//                        if (occupyingPiece.getTeamColor() != this.teamColor) {
//                            validMoves.add(new ChessMove(myPosition, newPosition, null));
//                        }
//
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (pieceType == PieceType.KING) {
//            int[][] directions = {
//                    {1, 0},
//                    {-1, 0},
//                    {0, 1},
//                    {0, -1},
//                    {1, 1},
//                    {1, -1},
//                    {-1, 1},
//                    {-1, -1}
//            };
//
//            for (int[] direction : directions) {
//                int newRow = myPosition.getRow() + direction[0];
//                int newCol = myPosition.getColumn() + direction[1];
//
//                if (newRow >= 1 && newRow <= 8 && newCol >=1 && newCol <= 8) {
//                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
//                    ChessPiece occupyingPiece = board.getPiece(newPosition);
//
//                    if (occupyingPiece == null) {
//                        validMoves.add(new ChessMove(myPosition, newPosition, null));
//                    } else if (occupyingPiece.getTeamColor() != this.teamColor) {
//                        validMoves.add (new ChessMove(myPosition, newPosition, null));
//                    }
//                }
//            }
//        }
//
//        if (pieceType == PieceType.KNIGHT) {
//            int[][] directions = {
//                    {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
//                    {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
//            };
//
//            for (int[] direction : directions) {
//                int newRow = myPosition.getRow() + direction[0];
//                int newCol = myPosition.getColumn() + direction[1];
//
//                if (newRow >= 1 && newRow <= 8 && newCol >=1 && newCol <= 8) {
//                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
//                    ChessPiece occupyingPiece = board.getPiece(newPosition);
//
//                    if (occupyingPiece == null) {
//                        validMoves.add(new ChessMove(myPosition, newPosition, null));
//                    } else if (occupyingPiece.getTeamColor() != this.teamColor) {
//                        validMoves.add (new ChessMove(myPosition, newPosition, null));
//                    }
//                }
//            }
//        }
//
//        return validMoves;

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
