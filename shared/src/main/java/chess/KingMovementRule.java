package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMovementRule extends BaseMovementRule {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        var validMoves = new HashSet<ChessMove>();

        int[][] directions = {
                {1, 0}, {-1, 0},
                {0, 1}, {0, -1},
                {1, 1}, {1, -1},
                {-1, 1}, {-1, -1}
        };

        calculateMoves(board, position, directions, validMoves, true);

        return validMoves;
    }
}