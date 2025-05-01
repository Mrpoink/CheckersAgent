package solution;

import core.*;

import problem.*;
import problem.Mark;
import problem.Square;
import problem.Checkers;
import java.util.*;

public class CheckersRunner extends Minimax<Square, Mark> {

    private static final int BOARD_SIZE = 3;

    //Mark.R: AI
    //Mark.B: Human

    private Mark turn = Mark.B; // Start with human player
    private final Checkers game;

    public CheckersRunner(Checkers game) {
        super(game);
        this.game = game;
    }

    public void play(){
        game.makeBoard();
        while (!game.isTerminal(game.board)){
            game.printBoard(game.board);
            System.out.println();
            if(turn == Mark.B){
                System.out.println("Your turn (B):");
                Checkers.Moves<Square, Square> userMove = getUserMove();
                if (userMove != null) {
                    game.execute(userMove, true);
                    turn = Mark.R;
                } else {
                    System.out.println("No valid moves available for player. Game over.");
                    break;
                }
            } else {
                System.out.println("AI turn (R):");
                Checkers.Moves<Square, Square> aiMove = minimaxSearch();
                if (aiMove != null) {
                    game.execute(aiMove, false);
                    turn = Mark.B;
                } else {
                    System.out.println("No valid moves available for AI. Game over.");
                    break;
                }
            }
        }
        game.printBoard(game.board);
        announceWinner(game.utility(game.board));
    }

    private Checkers.Moves<Square,Square> getUserMove(){
        List<Checkers.Moves<Square, Square>> validMoves = game.getAllRemainingMoves(game.getBoard());
        if (validMoves.isEmpty()) {
            return null;
        }

        System.out.println("Valid moves: " + validMoves);

        int fromRow = -1, fromCol = -1, toRow = -1, toCol = -1;
        boolean validInput = false;
        Scanner scan = new Scanner(System.in);

        while(!validInput){
            System.out.println("Your turn: enter row and column of piece to move (separated by a space)" +
                    " and then enter row and column of destination (separated by a space)");

            try {
                fromRow = scan.nextInt();
                fromCol = scan.nextInt();
                toRow = scan.nextInt();
                toCol = scan.nextInt();

                Square from = new Square(fromRow, fromCol);
                Square to = new Square(toRow, toCol);
                Checkers.Moves<Square, Square> move = new Checkers.Moves<>(from, to);

                // Check if the move is in the list of valid moves
                if (validMoves.contains(move)) {
                    validInput = true;
                    return move;
                } else {
                    System.out.println("Invalid move. Please select one of the valid moves.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter integers for row and column.");
                scan.nextLine(); // Clear the scanner buffer
            }
        }

        return null; // Should never reach here
    }

    private void announceWinner(int utility){
        // Count pieces for each player
        int redCount = 0;
        int blackCount = 0;
        for (Mark mark : game.board.values()) {
            if (mark == Mark.R) redCount++;
            if (mark == Mark.B) blackCount++;
        }

        if (redCount == 0) {
            System.out.println("\nPlayer (B) wins!!");
        } else if (blackCount == 0) {
            System.out.println("\nAI (R) wins!!");
        } else if (utility > 0) {
            System.out.println("\nAI (R) wins with score: " + utility);
        } else if (utility < 0) {
            System.out.println("\nPlayer (B) wins with score: " + utility);
        } else {
            System.out.println("It's a draw!!");
        }
    }

    public static void main(String[] args){
        CheckersRunner runner = new CheckersRunner(new Checkers(BOARD_SIZE));
        runner.play();
    }
}