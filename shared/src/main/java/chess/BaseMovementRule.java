package chess;

import java.util.Collection;

public abstract class BaseMovementRule implements MovementRule {
    protected void calculateMoves(ChessBoard board, ChessPosition position, int[][] directions,
                                  Collection<ChessMove> validMoves, boolean oneSquare) {
        for (int[] direction : directions) {
            int rowChange = direction[0];
            int colChange = direction[1];
            int currentRow = position.getRow();
            int currentCol = position.getColumn();

            while (true) {
                currentRow += rowChange;
                currentCol += colChange;

                if (currentRow < 1 || currentRow > 8 || currentCol < 1 || currentCol > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition (currentRow, currentCol);
                ChessPiece occupyingPiece = board.getPiece(newPosition);

                if (occupyingPiece == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                } else {
                    if (occupyingPiece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        validMoves.add(new ChessMove(position, newPosition, null));
                    }

                    break;
                }

                if (oneSquare) {
                    break;
                }
            }
        }
    }
}