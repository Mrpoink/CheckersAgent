//package solutions;
//
//import core_algorithms.*;
//import problems.Mark;
//import problems.Square;
//import problems.TicTacToe;
//
//import java.util.Scanner;
//
//
///**
// * A console-based runner for playing Tic-Tac-Toe.
// *
// * Assumptions:
// * - The human player is 'X' (MAX player)
// * - The AI is 'O' (MIN player)
// */
//public class TicTacToeRunner extends Minimax<Square> {
//
//    //The size of the board (e.g., 3 for 3x3 board)
//    private static final int BOARD_SIZE = 3;
//    //Specifies which player makes the first move.
//    //Mark.X: human player moves first
//    //Mark.O: AI moves first
//    private Mark turn = Mark.O;
//    private final TicTacToe game;
//
//    public TicTacToeRunner(TicTacToe game) {
//        super(game);
//        this.game = game;
//    }
//
//    /**
//     * Main loop to play the game. Alternates between human and
//     * AI turns until the game reaches a terminal state.
//     */
//    public void play() {
//        while (!game.isTerminal()) {
//            game.printBoard();
//            System.out.println();
//            if(turn == Mark.X) {
//                game.execute(getUserMove(), true);
//                turn = Mark.O;
//            }else{
//                System.out.println("AI's turn: ");
//                game.execute(minimaxSearch(), false);
//                turn = Mark.X;
//            }
//        }
//        game.printBoard();
//        announceWinner(game.utility());
//    }
//
//    /**
//     * Prompts the human player to enter a move via the console.
//     * Re-prompts until a valid move is provided.
//     *
//     * A valid move satisfies the following conditions:
//     * - The input includes two integers separated by space: a row and a column
//     * - The row and column are within the board bounds (i.e., 0 to BOARD_SIZE-1)
//     * - The selected square is currently unmarked
//     *
//     * @return the valid square chosen by the human player
//     */
//    private Square getUserMove() {
//        int row=-1, col=-1;
//        boolean validInput = false;
//        Scanner scanner = new Scanner(System.in);
//        while (!validInput) {
//            System.out.print("Your turn: enter row & column separated by space: ");
//            if (scanner.hasNextInt()) {
//                row = scanner.nextInt();
//            } else {
//                scanner.next();
//                continue;
//            }
//            if (scanner.hasNextInt()) {
//                col = scanner.nextInt();
//            } else {
//                scanner.next();
//                continue;
//            }
//            if (isValidMove(row,col)) {
//                validInput = true;
//      //          System.out.println();
//            } else {
//                System.out.println("Invalid position, please try again.");
//            }
//        }
//        //Human player plays as X
//        return new Square(row,col);
//    }
//
//    /**
//     * Checks if a move is valid (within board bounds and not already marked).
//     *
//     * @param row the row number of the square
//     * @param col the column number of the square
//     * @return true if the row # and the colum # are within the board bounds and unmarked,
//     *         false otherwise
//     */
//    private boolean isValidMove(int row, int col) {
//        boolean isWithinBounds = row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
//        return isWithinBounds && !game.markedSquare(new Square(row,col));
//    }
//
//    /**
//     * Announces the result of the game based on the utility value.
//     *
//     * @param utility the final utility value:
//     *                +1 = human wins,
//     *                -1 = AI wins,
//     *                0 = draw
//     */
//    private void announceWinner(int utility) {
//        if (utility == 1) {
//            System.out.println("\nPlayer (X) wins!");
//        } else if (utility == -1) {
//            System.out.println("\nAI (O) wins!");
//        } else {
//            System.out.println("\nIt's a draw!");
//        }
//    }
//
//    public static void main(String[] args) {
//        TicTacToeRunner runner = new TicTacToeRunner(new TicTacToe(BOARD_SIZE));
//        runner.play();
//    }
//}
