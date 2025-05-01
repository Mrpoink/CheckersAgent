package core;

import problem.Checkers;
import problem.Game;
import problem.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the classic Minimax search algorithm (without alpha-beta pruning).
 *
 * Assumptions:
 * - Human is the MAX player
 * - AI is the MIN player
 *
 * @param <A> the type representing a move or action in the game
 */
public class Minimax<A, B> {
    protected final Checkers game;
    private static final int MAX_DEPTH = 5; // Limit the search depth

    /**
     * Record to store the score of a game state and the path of moves leading to it.
     */
    public record ScoreMove<A> (int score, List<Checkers.Moves<Square,Square>> pathOfMoves){}

    public Minimax(Checkers game) {
        this.game = game;
    }

    /**
     * Performs a minimax search and returns the best move for the current player.
     *
     * @return the first move on the path to the best outcome for the MIN player (i.e., AI)
     */
    public Checkers.Moves<Square, Square> minimaxSearch(){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        ScoreMove<A> b = min(alpha, beta, 0);

        // Check if the path of moves is empty before getting the first element
        if (b.pathOfMoves().isEmpty()) {
            // If no valid moves found, return a default move or handle it appropriately
            List<Checkers.Moves<Square, Square>> availableMoves = game.getAllRemainingMoves(game.getBoard());
            if (!availableMoves.isEmpty()) {
                return availableMoves.get(0); // Return first available move as fallback
            } else {
                // No moves available, might be a terminal state
                System.out.println("No valid moves available!");
                return null; // Handle this case in the calling code
            }
        }

        return b.pathOfMoves().getFirst();
    }

    /**
     * MAX node in the minimax tree. Returns the move path that maximizes utility.
     *
     * @return best score and path of moves for the MAX player
     */
    public ScoreMove<A> max(Integer alpha, Integer beta, int depth){
        System.out.println("Max (depth " + depth + "): " + game.utility(game.board));
        List<Checkers.Moves<Square, Square>> bestPath = new ArrayList<>();

        // Return utility value if terminal node or maximum depth reached
        if(game.isTerminal(game.board) || depth >= MAX_DEPTH){
            return new ScoreMove<>(game.utility(game.board), bestPath);
        }else{
            int bestScore = Integer.MIN_VALUE;
            List<Checkers.Moves<Square, Square>> moves = game.getAllRemainingMoves(game.getBoard());

            if (moves.isEmpty()) {
                return new ScoreMove<>(game.utility(game.board), bestPath);
            }

            for(Checkers.Moves<Square, Square> move : moves){
                System.out.println("Depth " + depth + ": " + move);
                game.execute(move, true);
                ScoreMove<A> a = min(alpha, beta, depth + 1);
                game.undo(move, true);

                if (a.score() > bestScore) {
                    bestScore = a.score();
                    List<Checkers.Moves<Square, Square>> newPath = new ArrayList<>();
                    newPath.add(move);
                    newPath.addAll(a.pathOfMoves());
                    bestPath = newPath;
                }

                if (bestScore >= beta) {
                    return new ScoreMove<>(bestScore, bestPath);
                }

                alpha = Math.max(alpha, bestScore);
            }
            return new ScoreMove<>(bestScore, bestPath);
        }
    }

    /**
     * MIN node in the minimax tree. Returns the move path that minimizes utility.
     *
     * @return best score and path of moves for the MIN player
     */
    public ScoreMove<A> min(Integer alpha, Integer beta, int depth){
        System.out.println("Min (depth " + depth + "): " + game.utility(game.board));
        List<Checkers.Moves<Square, Square>> bestPath = new ArrayList<>();

        // Return utility value if terminal node or maximum depth reached
        if(game.isTerminal(game.board) || depth >= MAX_DEPTH){
            return new ScoreMove<>(game.utility(game.board), bestPath);
        }else {
            int bestScore = Integer.MAX_VALUE;
            List<Checkers.Moves<Square, Square>> moves = game.getAllRemainingMoves(game.getBoard());

            if (moves.isEmpty()) {
                return new ScoreMove<>(game.utility(game.board), bestPath);
            }

            for (Checkers.Moves<Square, Square> move : moves) {
                System.out.println("Depth " + depth + ": " + move);
                game.execute(move, false);
                ScoreMove<A> b = max(alpha, beta, depth + 1);
                game.undo(move, false);

                if (b.score() < bestScore) {
                    bestScore = b.score();
                    List<Checkers.Moves<Square, Square>> newPath = new ArrayList<>();
                    newPath.add(move);
                    newPath.addAll(b.pathOfMoves());
                    bestPath = newPath;
                }

                if (bestScore <= alpha) {
                    return new ScoreMove<>(bestScore, bestPath);
                }

                beta = Math.min(beta, bestScore);
            }
            return new ScoreMove<>(bestScore, bestPath);
        }
    }
}