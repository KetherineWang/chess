package chess;

import java.util.HashSet;
import java.util.Collection;

public class BishopMovementRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        var validMoves = new HashSet<ChessMove>();

        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        calculateMoves(board, position, directions, validMoves, false);

        return validMoves;
    }
}