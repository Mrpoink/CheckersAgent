package problem;

import java.util.List;
import java.util.Map;

/**
 * A generic interface for turn-based games that can be used with minimax search.
 *
 * @param <A> the type representing a move or action in the game
 */
public interface Game<A, B> {
    /**
     * Returns a list of all legal moves that can be made from the current state.
     *
     * @return a list of remaining valid moves
     */
    List<A> getAllRemainingMoves(Map<Square, Mark> currentBoard);

    /**
     * Creates the board, should be used on initial execution
     * @return void
     */

    void makeBoard();

    Map<Square, Mark> getBoard();

    /**
     * Computes the utility of the current game state.
     * This value is used by minimax to evaluate terminal states.
     *
     * @return an integer utility value: higher means better for the MAX player
     */
    int utility();

    /**
     * Checks whether the game is over.
     *
     * @return true if the current state is terminal; false otherwise
     */
    boolean isTerminal();

    /**
     * Applies the given move to the current game state.
     *
     * @param move   the move to apply
     * @param isMax  true if the move is by the MAX player,
     *               false if by the MIN player
     */
    void execute(A move, boolean isMax);


    /**
     * Undoes a previously applied move, restoring the previous game state.
     *
     * @param move   the move to undo
     * @param isMax  true if the move was by the MAX player
     *               false if by the MIN player
     */
    void undo(A move, boolean isMax);
}
