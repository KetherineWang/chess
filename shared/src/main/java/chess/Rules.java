package chess;

import java.util.HashMap;

public class Rules {
    static private final HashMap<ChessPiece.PieceType, MovementRule> RULES = new HashMap<>();

    static {
        RULES.put(ChessPiece.PieceType.KING, new KingMovementRule());
        RULES.put(ChessPiece.PieceType.QUEEN, new QueenMovementRule());
        RULES.put(ChessPiece.PieceType.KNIGHT, new KnightMovementRule());
        RULES.put(ChessPiece.PieceType.BISHOP, new BishopMovementRule());
        RULES.put(ChessPiece.PieceType.ROOK, new RookMovementRule());
        RULES.put(ChessPiece.PieceType.PAWN, new PawnMovementRule());
    }

    public static MovementRule pieceRule(ChessPiece.PieceType pieceType) {
        return RULES.get(pieceType);
    }
}