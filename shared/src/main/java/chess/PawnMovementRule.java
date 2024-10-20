package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMovementRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        var validMoves = new HashSet<ChessMove>();
        ChessPiece pawn = board.getPiece(position);

        if (pawn == null || pawn.getPieceType() != ChessPiece.PieceType.PAWN) {
            return validMoves;
        }

        int direction = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

        ChessPosition oneStepForward = new ChessPosition(position.getRow() + direction, position.getColumn());
        if (isValidPosition(oneStepForward) && board.getPiece(oneStepForward) == null) {
            addPromotionMoves(validMoves, position, oneStepForward, pawn.getTeamColor());
        }

        if ((position.getRow() == 2 && pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ||
                (position.getRow() == 7 && pawn.getTeamColor() == ChessGame.TeamColor.BLACK)) {
            ChessPosition twoStepsForward = new ChessPosition(position.getRow() + 2 * direction, position.getColumn());
            if (board.getPiece(oneStepForward) == null && board.getPiece(twoStepsForward) == null) {
                validMoves.add(new ChessMove(position, twoStepsForward, null));
            }
        }

        int[][] captureDirections = {{direction, 1}, {direction, -1}};
        for (int[] captureDirection : captureDirections) {
            ChessPosition capturePosition = new ChessPosition(position.getRow() + captureDirection[0], position.getColumn() + captureDirection[1]);
            if (isValidPosition(capturePosition)) {
                ChessPiece occupyingPiece = board.getPiece(capturePosition);
                if (occupyingPiece != null && occupyingPiece.getTeamColor() != pawn.getTeamColor()) {
                    addPromotionMoves(validMoves, position, capturePosition, pawn.getTeamColor());
                }
            }
        }

        return validMoves;
    }

    private void addPromotionMoves(Collection<ChessMove> validMoves, ChessPosition startPosition,
                                   ChessPosition endPosition, ChessGame.TeamColor teamColor) {
        if ((teamColor == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) ||
                (teamColor == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1)) {
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        } else {
            validMoves.add(new ChessMove(startPosition, endPosition, null));
        }
    }

    private boolean isValidPosition(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
    }
}