package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

        Collection<ChessMove> validMoves = new HashSet<>();
        for (ChessMove move : possibleMoves) {
            ChessBoard tempBoard = new ChessBoard();
            tempBoard.setBoard(board.getBoard());
            tempBoard.movePiece(move);

            if (!isInCheck(piece.getTeamColor(), tempBoard)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    private ChessPosition findKingPosition(TeamColor teamColor, ChessBoard board) {

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING ) {
                    return position;
                }
            }
        }

        return null;
    }

    private boolean isInCheck(TeamColor teamColor, ChessBoard board) {
//        ChessPosition kingPosition = null;
//
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition position = new ChessPosition(row, col);
//                ChessPiece piece = board.getPiece(position);
//
//                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
//                    kingPosition = position;
//                    break;
//                }
//            }
//
//            if (kingPosition != null) {
//                break;
//            }
//        }
//
//        if (kingPosition == null) {
//            return false;
//        }

        ChessPosition kingPosition = findKingPosition(teamColor, board);
        if (kingPosition == null) {
            return false;
        }

        TeamColor opposingTeamColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == opposingTeamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, position);

                    for (ChessMove move : possibleMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());

        if (pieceToMove == null) {
            throw new InvalidMoveException("No piece at the start position.");
        }

        if (pieceToMove.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("It's not this team's turn.");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        if(!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move.");
        }

        board.movePiece(move);

        setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
//        ChessPosition kingPosition = null;
//
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition position = new ChessPosition(row, col);
//                ChessPiece piece = board.getPiece(position);
//
//                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
//                    kingPosition = position;
//                    break;
//                }
//            }
//
//            if (kingPosition != null) {
//                break;
//            }
//        }
//
//        if (kingPosition == null) {
//            return false;
//        }
//
//        TeamColor opposingTeamColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition position = new ChessPosition(row, col);
//                ChessPiece piece = board.getPiece(position);
//
//                if (piece != null && piece.getTeamColor() == opposingTeamColor) {
//                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, position);
//
//                    for (ChessMove move : possibleMoves) {
//                        if (move.getEndPosition().equals(kingPosition)) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//
//        return false;
        return isInCheck(teamColor, this.board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(position);

                    for (ChessMove move : possibleMoves) {
                        ChessBoard tempBoard = new ChessBoard();
                        tempBoard.setBoard(board.getBoard());
                        tempBoard.movePiece(move);

                        if (!isInCheck(teamColor, tempBoard)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = validMoves(position);

                    if (!validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
