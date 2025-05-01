//package problems;
//
//import java.util.*;
//
///**
// * Represents a generalized Tic-Tac-Toe game of any board size.
// *
// * Assumptions:
// * - 'X' is the MAX player (human)
// * - 'O' is the MIN player (AI)
// */
//public class TicTacToe implements Game<Square>{
//
//    // Board size, (e.g., 3 for a 3x3 board)
//    private final int BOARD_SIZE;
//    // Internal board representation: maps each occupied square to a mark (X or O)
//    // Note: the map only contains *marked* squares.
//    private final Map<Square,Mark> board;
//
//    public TicTacToe(int size) {
//        this.BOARD_SIZE = size;
//        this.board = new HashMap<>();
//    }
//
//    /**
//     * Checks if the current state is terminal (win or draw).
//     *
//     * @return true if a player has won or the board is full (draw),
//     *         false otherwise
//     */
//    public boolean isTerminal(){
//        int utility = utility();
//        //A player has won the game.
//        if (utility == 1 || utility == -1){
//            return true;
//        }
//        //Game is either a draw or unfinished
//        return board.size() == BOARD_SIZE * BOARD_SIZE;
//    }
//
//
//    /**
//     * Applies a move to the board, placing an X or O depending on the player.
//     *
//     * @param move  the square to place the mark
//     * @param isMax true if it's the MAX player's move (X),
//     *              false for MIN (O)
//     */
//    public void execute(Square move, boolean isMax){
//        if(isMax) {
//            board.put(move, Mark.X);
//        }
//        else{
//            board.put(move, Mark.O);
//        }
//    }
//
//
//    /**
//     * Undoes a previous move.
//     *
//     * @param move  the square to unmark
//     * @param isMax true if the move was by the MAX player
//     *              false if by the MIN player
//     */
//    public void undo(Square move, boolean isMax){
//        board.remove(move);
//
//    }
//
//    /**
//     * Computes the utility of the current game state.
//     *
//     * @return +1 if X player wins,
//     *         -1 if O player wins,
//     *         0 otherwise
//     */
//    public int utility(){
//        //check rows
//        for(int row=0; row<BOARD_SIZE; row++) {
//            int rowSum = 0;
//            for (int col = 0; col<BOARD_SIZE; col++) {
//                Square square = new Square(row, col);
//                //When comparing enum values in Java, always use ==, not equals()
//                if (board.containsKey(square)) {
//                    if (board.get(square) == Mark.X) {
//                        rowSum++;
//                    } else {
//                        rowSum--;
//                    }
//                }
//            }
//            if (rowSum == BOARD_SIZE) {
//                return 1;
//            } else if (rowSum == - BOARD_SIZE) {
//                return -1;
//            }
//        }
//
//        //check columns
//        for(int col=0; col<BOARD_SIZE; col++) {
//            int colSum = 0;
//            for (int row = 0; row<BOARD_SIZE; row++) {
//                Square square = new Square(row, col);
//                if (board.containsKey(square)) {
//                    if (board.get(square) == Mark.X) {
//                        colSum++;
//                    } else {
//                        colSum--;
//                    }
//                }
//            }
//            if (colSum == BOARD_SIZE) {
//                return 1;
//            } else if (colSum == - BOARD_SIZE) {
//                return -1;
//            }
//        }
//
//        //check diagonal -- top left to bottom right
//        int diaSum = 0;
//        for(int d=0; d<BOARD_SIZE; d++) {
//            Square square = new Square(d, d);
//            if (board.containsKey(square)) {
//                if (board.get(square) == Mark.X) {
//                    diaSum++;
//                } else {
//                    diaSum--;
//                }
//            }
//        }
//        if (diaSum == BOARD_SIZE) {
//            return 1;
//        } else if (diaSum == - BOARD_SIZE) {
//            return -1;
//        }
//
//        //check diagonal -- top right to bottom left
//        diaSum = 0;
//        for(int d=0; d<BOARD_SIZE; d++) {
//            Square square = new Square(d, BOARD_SIZE - 1 - d);
//            if (board.containsKey(square)) {
//                if (board.get(square) == Mark.X) {
//                    diaSum++;
//                } else {
//                    diaSum--;
//                }
//            }
//        }
//        if (diaSum == BOARD_SIZE) {
//            return 1;
//        } else if (diaSum == - BOARD_SIZE) {
//            return -1;
//        }
//
//        //no one has won yet; either a draw, or unfinished
//        return 0;
//    }
//
//
//    /**
//     * Returns all empty (i.e., unmarked) squares on the board.
//     *
//     * @return a list of all empty squares
//     */
//    public List<Square> getAllRemainingMoves(){
//        List<Square> result = new ArrayList<>();
//        for(int row=0; row<BOARD_SIZE; row++){
//            for(int col=0; col<BOARD_SIZE; col++){
//                Square square = new Square(row, col);
//                if(!board.containsKey(square)) {
//                    //this square is not marked
//                    result.add(square);
//                }
//            }
//        }
//        return result;
//    }
//
//
//    /**
//     * Checks whether the specified square is currently marked (i.e., occupied by X or O).
//     *
//     * @param square the square to check
//     * @return true if the square has been marked,
//     *         false if it's still empty
//     */
//    public boolean markedSquare(Square square){
//        return board.containsKey(square);
//    }
//
//    /**
//     * Print the board in a neat format
//     */
//    public void printBoard() {
//        String RESET = "\u001B[0m";
//        String RED = "\u001B[31m";
//        String CYAN = "\u001B[36m";
//        //print column headers
//        System.out.print("   ");
//        for (int col = 0; col < BOARD_SIZE; col++) {
//            System.out.print(" " + col + "  ");
//        }
//        System.out.println();
//        for(int i=0; i<BOARD_SIZE; i++){
//            // Print row number
//            System.out.print(" " + i + " ");
//            //print each cell in the row
//            for (int j = 0; j < BOARD_SIZE; j++) {
//                Square square = new Square(i, j);
//                if (board.containsKey(square)) {
//                    if(board.get(square)==Mark.X) {
//                        System.out.print(" " + RED+ board.get(square) + RESET + " ");
//                    }else{
//                        System.out.print(" " + CYAN + board.get(square) + RESET + " ");
//                    }
//                } else {
//                    System.out.print(" " + " "+" ");
//                }
//                if (j < BOARD_SIZE - 1) {
//                    System.out.print("|");
//                }
//            }
//            System.out.println();
//            //print separator line between rows
//            if (i < BOARD_SIZE - 1) {
//                System.out.print("   ");
//                for (int j = 0; j < BOARD_SIZE; j++) {
//                    System.out.print("---");
//                    if (j < BOARD_SIZE - 1) {
//                        System.out.print("+");
//                    }
//                }
//                System.out.println();
//            }
//        }
//    }
//}
