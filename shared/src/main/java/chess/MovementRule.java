package chess;

import java.util.Collection;

public interface MovementRule {
    Collection<ChessMove> moves(ChessBoard board, ChessPosition position);
}