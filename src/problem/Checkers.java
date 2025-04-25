package problem;

import java.util.*;

public class Checkers implements Game<Square, Mark> {

    private final int BOARD_SIZE;

    private final Map<Square,Mark> board;

    public Checkers(int size){
        this.BOARD_SIZE = size;
        this.board = new HashMap<>();
    }

    public boolean isTerminal(){
        //Check utility
        int utility = utility();

        if (utility == 1 || utility == -1){
            //If someone has won, return true
            return true;
        }
        //If no one won, game is a draw or unfinished
        return board.size() == BOARD_SIZE * BOARD_SIZE;
    }

    public void execute(Square move, boolean isMax){
        //This 'executes' the move by placing the mark on the board
        if (isMax) {
            //R is red, the Max move
            if (board.get(move) == Mark.R) {
                board.put(move, Mark.R);
            }
        }
        else{
            //B is black, the Min move
            if (board.get(move) == Mark.B) {
                board.put(move, Mark.B);
            }
        }
    }

    public void undo(Square move, boolean isMax){
        //Removes move from map
        board.remove(move);
    }

    public int utility(){
        int red = 0; //AI is red piece
        int black = 0; //Player is black piece
        for (int row = 0; row < BOARD_SIZE; row++){
            for (int col = 0; col< BOARD_SIZE; col++){
                Square square = new Square(row, col);
                if (board.containsKey(square)){
                    if (board.get(square) == Mark.R){
                        red++;
                    }else if (board.get(square) == Mark.B){
                        black++;
                    }
                }
            }
        }
        if (red == 1 && black == 0){
            return 1;
        }else if (red == 0 && black == 1){
            return -1;
        }
        return 0;
    }

    public List<Square> getAllRemainingMoves() {
        //Only remaining moves can be in diagonals
        //Going to have to include bit where it limits the amount of rows
        List<Square> result = new ArrayList<>();
        for (int x = 0; x < BOARD_SIZE; x++) {
            Square square = new Square(x,x);
            Square uright_di = new Square(x + 1, x + 1); //upper right
            Square dleft_di = new Square(x - 1, x - 1); //lower left
            Square dright_di = new Square(x + 1, x - 1); //lower right
            Square uleft_di = new Square(x - 1, x + 1); //upper left
            if (!board.containsKey(square)) {
                result.add(square);
            }
            //Adds moves to possible moves if the move is not already there
            else if (!board.containsKey(uright_di)) {
                result.add(uright_di);
            } else if (!board.containsKey(dleft_di)) {
                result.add(dleft_di);
            } else if (!board.containsKey(dright_di)) {
                result.add(dright_di);
            } else if (!board.containsKey(uleft_di)) {
                result.add(uleft_di);
            }
            //Adds jumps to possible moves
            else if (board.get(uright_di) == Mark.B) {
                if (!board.containsKey(new Square(x + 2, x + 2))) result.add(new Square(x + 2, x + 2)); //upper right
            } else if (board.get(dleft_di) == Mark.B) {
                if (!board.containsKey(new Square(x - 2, x - 2))) result.add(new Square(x - 2, x - 2)); //lower left
            } else if (board.get(dright_di) == Mark.B) {
                if (!board.containsKey(new Square(x + 2, x - 2))) result.add(new Square(x + 2, x - 2)); //lower right
            } else if (board.get(uleft_di) == Mark.B) {
                if (!board.containsKey(new Square(x - 2, x + 2))) result.add(new Square(x - 2, x + 2)); //upper left
            }

        }
        return result;
    }

    //Checks if there is already a move there
    public boolean markedSquare(Square square){ return board.containsKey(square); }

    public void makeBoard(){
        //3x3, 1 occupied, 1 empty
        //5x5, 2 occupied, 1 empty
        //7x7, 3 occupied, 1 empty
        //9x9, 3 occupied, 3 empty
        //11x11 3 occupied, 5 empty
        if (BOARD_SIZE == 3) { //3x3
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if ((row == 0) && ((col == 0) || (col == 2))){
                        Square square = new Square(row, col);
                        board.put(square, Mark.R);
                    }else if((row == 2) && ((col == 0) || (col == 2))){
                        Square square = new Square(row, col);
                        board.put(square, Mark.B);
                    }
                }
            }
        }if (BOARD_SIZE == 5){ //5x5
            for (int row = 1; row <= BOARD_SIZE; row++) {
                for (int col = 1; col <= BOARD_SIZE; col++) {
                    if ((row <= 2) && (col % 2 == 1) && (row % 2 == 1)) {
                        Square square = new Square(row, col);
                        board.put(square, Mark.R);
                    }else if((row >= BOARD_SIZE - 2) && (col % 2 == 0) && (row % 2 == 0)){ //Occupies 2
                        Square square = new Square(row, col);
                        board.put(square, Mark.B);
                    }
                }
            }
        }else if (BOARD_SIZE >= 7){
            for (int row = 1; row <= BOARD_SIZE; row++) {
                for (int col = 1; col <= BOARD_SIZE; col++) {
                    if ((row <= 3) && (col % 2 == 1) && (row % 2 == 1)){
                        Square square = new Square(row, col);
                        board.put(square, Mark.R);
                    }else if((row >= BOARD_SIZE - 3) && (col % 2 == 1) && (row % 2 == 1)){
                        Square square = new Square(row, col);
                        board.put(square, Mark.B);
                    }
                }
            }
        }
    }

    public void printBoard(){
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
                if (board.containsKey(square)){
                    if(board.get(square) == Mark.R){
                        System.out.print(" " + RED + board.get(square) + RESET + " ");
                    }if(board.get(square) == Mark.B){
                        System.out.print(" " + WHITE + board.get(square) + RESET + " ");
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
//    public static void main(String[] args){
//        Checkers checkers = new Checkers(5);
//        checkers.printBoard();
//    }
}


