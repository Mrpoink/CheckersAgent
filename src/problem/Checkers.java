package problem;

import java.util.*;

public class Checkers implements Game<Checkers.Moves<Square, Square>, Mark> {

    private final int BOARD_SIZE;

    public final Map<Square,Mark> board;

    public Checkers(int size){
        this.BOARD_SIZE = size;
        this.board = new HashMap<>();
    }

    public record Moves<A, B> (Square from, Square to){}

    public Map<Square, Mark> getBoard(){
        return this.board;
    }

    public boolean isTerminal(Map<Square,Mark> currentBoard){
        //Check utility
        int utility = utility(currentBoard);

        // Count pieces for each player
        int redCount = 0;
        int blackCount = 0;
        for (Mark mark : currentBoard.values()) {
            if (mark == Mark.R) redCount++;
            if (mark == Mark.B) blackCount++;
        }

        // Game is terminal if one player has no pieces left
        if (redCount == 0 || blackCount == 0) {
            return true;
        }

        // Game is terminal if no valid moves remain
        List<Moves<Square, Square>> validMoves = getAllRemainingMoves(currentBoard);
        if (validMoves.isEmpty()) {
            return true;
        }

        //If someone has won, return true
        if (utility == 1 || utility == -1){
            return true;
        }

        //If no one won, game is a draw if the board is full
        return board.size() == BOARD_SIZE * BOARD_SIZE;
    }

    public void execute(Moves<Square, Square> move, boolean isMax){
        //This 'executes' the move by placing the mark on the board
        System.out.println("Checking: " + move.from() + ", " + move.to());
        if (isMax) {
            //B is black, the Max move
            board.put(move.to(), Mark.B);
            System.out.println("B: " + move.from() + ", " + move.to());
            System.out.println("Board before: " + board);
            board.remove(move.from());
            System.out.println("Board after: " + board);
        }
        else{
            //R is red, the Min move
            board.put(move.to(), Mark.R);
            System.out.println("R: " + move.from() + ", " + move.to());
            System.out.println("Board before: " + board);
            board.remove(move.from());
            System.out.println("Board after: " + board);
        }
        printBoard(board);
    }

    public void undo(Moves<Square,Square> move, boolean isMax){
        //Restores the previous state
        if (isMax) {
            board.remove(move.to());
            board.put(move.from(), Mark.B);
        } else {
            board.remove(move.to());
            board.put(move.from(), Mark.R);
        }
    }

    public int utility(Map<Square,Mark> currentBoard){
        int red = 0; //AI is red piece
        int black = 0; //Player is black piece
        for (int row = 0; row < BOARD_SIZE; row++){
            for (int col = 0; col< BOARD_SIZE; col++){
                Square square = new Square(row, col);
                if (currentBoard.containsKey(square)){
                    if (currentBoard.get(square) == Mark.R){
                        red = red + Math.abs(row-BOARD_SIZE);
                    }
                    if (currentBoard.get(square) == Mark.B){
                        black = black + Math.abs(BOARD_SIZE - row);
                    }
                }
            }
        }
        if (red > black){
            return red;
        }else if (red < black){
            return black;
        }
        return 0;
    }

    private boolean inRange(int number1, int number2, int upperbound, int lowerbound){
        return (number1 >= lowerbound && number1 < upperbound) &&
                (number2 >= lowerbound && number2 < upperbound);
    }

    private List<Moves<Square, Square>> jumpWalkCheck(Square square, int jumpY, int jumpX, int walkY, int walkX, Map<Square, Mark> currentBoard, Mark mark) {
        Mark player = mark;
        Mark enemy = (mark == Mark.B) ? Mark.R : Mark.B;

        List<Moves<Square, Square>> result = new ArrayList<>();

        // Check if jump destination is in range
        if (inRange(jumpY, jumpX, BOARD_SIZE, 0)) {
            Square jumped = new Square(jumpY, jumpX);
            Square over = new Square(walkY, walkX);

            // Jump is valid if destination is empty and there's an enemy piece to jump over
            if (!currentBoard.containsKey(jumped) && currentBoard.containsKey(over) &&
                    currentBoard.get(over) == enemy) {
                System.out.println("Found jump");
                result.add(new Moves<>(square, jumped));
            }
        }

        // Check if walk destination is in range
        if (inRange(walkY, walkX, BOARD_SIZE, 0)) {
            Square walked = new Square(walkY, walkX);
            // Walk is valid if destination is empty
            if (!currentBoard.containsKey(walked)) {
                result.add(new Moves<>(square, walked));
            }
        }

        return result;
    }

    public List<Moves<Square, Square>> getAllRemainingMoves(Map<Square, Mark> currentBoard) {
        List<Moves<Square, Square>> result = new ArrayList<>();

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                Square square = new Square(y, x);

                // Only consider squares that have a piece
                if (!currentBoard.containsKey(square)) {
                    continue;
                }

                Mark piece = currentBoard.get(square);

                if (piece == Mark.R) {
                    // Red pieces can only move down
                    int jumpY, jumpX, walkY, walkX;

                    // Lower left diagonal
                    jumpY = y + 2; jumpX = x - 2; walkY = y + 1; walkX = x - 1;
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.R));

                    // Lower right diagonal
                    jumpY = y + 2; jumpX = x + 2; walkY = y + 1; walkX = x + 1;
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.R));
                }

                if (piece == Mark.B) {
                    // Black pieces can only move up
                    int jumpY, jumpX, walkY, walkX;

                    // Upper left diagonal
                    jumpY = y - 2; jumpX = x - 2; walkY = y - 1; walkX = x - 1;
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.B));

                    // Upper right diagonal
                    jumpY = y - 2; jumpX = x + 2; walkY = y - 1; walkX = x + 1;
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.B));
                }
            }
        }

        System.out.println("Result: " + result);
        return result;
    }

    //Checks if there is already a move there
    public boolean markedSquare(Square square){ return board.containsKey(square); }

    public void makeBoard() {
        if (BOARD_SIZE == 3) { //3x3
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if ((row == 0) && ((col == 0) || (col == 2))) {
                        Square square = new Square(row , col);
                        board.put(square, Mark.R);
                    } else if ((row == 2) && ((col == 0) || (col == 2))) {
                        Square square = new Square(row , col );
                        board.put(square, Mark.B);
                    }
                }
            }
        }
    }

    public void printBoard(Map<Square,Mark> currentBoard){
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String WHITE = "\u001B[37m";

        System.out.println("  ");
        for (int col = 0; col < BOARD_SIZE; col++){
            System.out.print(" " + col + " ");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++){
            System.out.print(" " + i + " ");

            for (int j = 0; j < BOARD_SIZE; j++){
                Square square = new Square(i, j);
                if (currentBoard.containsKey(square)){
                    if(currentBoard.get(square) == Mark.R){
                        System.out.print(" " + RED + currentBoard.get(square) + RESET + " ");
                    }if(currentBoard.get(square) == Mark.B){
                        System.out.print(" " + WHITE + currentBoard.get(square) + RESET + " ");
                    }
                } else{
                    System.out.print(" " +  " " + " ");
                }if (j < BOARD_SIZE - 1){
                    System.out.print("|");
                }
            }
            System.out.println();

            if (i < BOARD_SIZE - 1){
                System.out.print("   ");
                for (int j = 0; j < BOARD_SIZE; j++){
                    System.out.print("---");
                    if (j < BOARD_SIZE - 1){
                        System.out.print("+");
                    }
                }
                System.out.println();
            }
        }
    }
}