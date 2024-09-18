package chess;

import java.util.HashSet;
import java.util.Collection;

public class KnightMovementRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        var validMoves = new HashSet<ChessMove>();

        int[][] directions = {
                {2, 1}, {2, -1},
                {-2, 1}, {-2, -1},
                {1, 2}, {1, -2},
                {-1, 2}, {-1, -2}
        };

        calculateMoves(board, position, directions, validMoves, true);

        return validMoves;
    }
}