package core;

import problem.Checkers;
import problem.Game;
import problem.Mark;
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

    /**
     * Record to store the score of a game state and the path of moves leading to it.
     */
    public record ScoreMove<A> (int score, List<Checkers.Moves<Square,Square, Square>> pathOfMoves){}

    public Minimax(Checkers game) {
        this.game = game;
    }

    /**
     * Performs a minimax search and returns the best move for the current player.
     *
     * @return the first move on the path to the best outcome for the MAX player (i.e., AI)
     */
    public Checkers.Moves<Square, Square, Square> minimaxSearch(){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        ScoreMove<A> a = max(alpha, beta);
        return a.pathOfMoves().getFirst();
    }

    /**
     * MAX node in the minimax tree. Returns the move path that maximizes utility.
     *
     * @return best score and path of moves for the MAX player
     */
    public ScoreMove<A> max(Integer alpha, Integer beta){
        Mark currentMark = Mark.B; //Max is black in it's own head

        List<Checkers.Moves<Square, Square, Square>> bestPath = new ArrayList<>(); //Best path is a path of MOVES

        if(game.isTerminal(game.board, currentMark)){
            //Add best
            return new ScoreMove<>(game.utility(game.board),bestPath);
        }else{

            for(Checkers.Moves<Square, Square, Square> move : game.getAllRemainingMoves(game.getBoard(), currentMark)){

                game.execute(move, true);
                ScoreMove<A> a = min(alpha, beta);
                game.undo(move, true);

                if (a.score() >= beta){
                    return new ScoreMove<>(a.score(), a.pathOfMoves());
                }
                else if (a.score() > alpha){
                    alpha = a.score();
                    bestPath = a.pathOfMoves();
                    bestPath.addFirst(move);
                }
            }
            return new ScoreMove<>(alpha,bestPath);
        }
    }

    /**
     * MIN node in the minimax tree. Returns the move path that minimizes utility.
     *
     * @return best score and path of moves for the MIN player
     */
    public ScoreMove<A> min(Integer alpha, Integer beta){
        Mark currentMark = Mark.R; //Min is red in it's own head

        List<Checkers.Moves<Square, Square, Square>> bestPath = new ArrayList<>();//Best path is a path of MOVES

        if(game.isTerminal(game.board, currentMark)){
            return new ScoreMove<>(game.utility(game.board), bestPath);
        }else {

            for (Checkers.Moves<Square, Square, Square> move : game.getAllRemainingMoves(game.getBoard(), currentMark)) {

                game.execute(move, false);
                ScoreMove<A> b = max(alpha, beta);
                game.undo(move, false);

                if (b.score() <= alpha){
                    return new ScoreMove<>(b.score(), b.pathOfMoves());
                }
                else if (b.score() < beta) {
                    beta = b.score();
                    bestPath = b.pathOfMoves();
                    bestPath.addFirst(move);
                }
            }
            return new ScoreMove<>(beta, bestPath);
        }

    }
}
