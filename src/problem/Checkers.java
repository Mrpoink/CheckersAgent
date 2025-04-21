package problem;

import java.util.*;

public class Checkers implements Game<Square> {

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
        return board.size() = BOARD_SIZE * BOARD_SIZE;
    }

    public void execute(Square move, boolean isMax){
        //This 'executes' the move by placing the mark on the board
        if (isMax) {
            //R is red, the Max move
            board.put(move, Mark.R);
        }
        else{
            //B is black, the Min move
            board.put(move, Mark.B);
        }
    }

    public void undo(Square move, boolean isMax){
        //Removes move from map
        board.remove(move);
    }

    public int utility(){
        //Could try incorporating space jumps,
        //and distance from other side

        //This is where we do our checks
        int dSum = 0; //Check diagonals, this shows how many diagonal conflicts
        for (int i = 0; i < BOARD_SIZE; i++){
            Integer hop = 0;
            Square square = new Square(i, i);
            //Make new square to check with
            if (board.containsKey(square)){
                //board should contain the new square unless it's out of bounds
                if (board.get(square) == Mark.R) { //Distance from other side
                    dSum = BOARD_SIZE - i + (dSum + 1);
                }else {
                    dSum = BOARD_SIZE - i + (dSum - 1);
                }
                if (board.get(new Square(i-1, i-1)) == Mark.B) {
                    dSum++;
                }
                if (board.get(new Square(i+1, i+1)) == Mark.B){
                    dSum++;
                }
            }
        }
        if (dSum >= BOARD_SIZE){ //Max won
            return 1;
        }else if (dSum <= -BOARD_SIZE){ //Min won
            return -1;
        }
        return 0; //No one won
    }

    public List<Square> getAllRemainingMoves() {
        //Only remaining moves can be in diagonals
        //Going to have to include bit where it limits the amount of rows
        List<Square> result = new ArrayList<>();
        for (int row = 0; row < BOARD_SIZE; row++) {
            Square square = new Square(row, row);
            Square uright_di = new Square(row + 1, row + 1);
            Square dleft_di = new Square(row - 1, row - 1);
            Square dright_di = new Square(row + 1, row - 1);
            Square uleft_di = new Square(row - 1, row + 1);
            if (!board.containsKey(square)) {
                result.add(square);
            }
            //Adds diagonals to possible moves
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
                if (!board.containsKey(new Square(row + 2, row + 2))) result.add(new Square(row + 2, row + 2));
            } else if (board.get(dleft_di) == Mark.B) {
                if (!board.containsKey(new Square(row - 2, row - 2))) result.add(new Square(row - 2, row - 2));
            } else if (board.get(dright_di) == Mark.B) {
                if (!board.containsKey(new Square(row + 2, row - 2))) result.add(new Square(row + 2, row - 2));
            } else if (board.get(uleft_di) == Mark.B) {
                if (!board.containsKey(new Square(row - 2, row + 2))) result.add(new Square(row - 2, row + 2));
            }

        }
        return result;
    }

    //Checks if there is already a move there
    public boolean markedSquare(Square square){ return board.containsKey(square); }

    public void printBoard(){
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String WHITE = "\u001B[37m";

        System.out.println("  ");
        for (int col = 0; col < BOARD_SIZE; col++){
            System.out.println(" " + col + " ");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++){
            System.out.println(" " + i + " ");

            for (int j = 0; j < BOARD_SIZE; j++){
                Square square = new Square(i, j);
                if (board.containsKey(square)){
                    if(board.get(square) == Mark.R){
                        System.out.println(" " + RED + board.get(square) + RESET + " ");
                    }if(board.get(square) == Mark.B){
                        System.out.println(" " + WHITE + board.get(square) + RESET + " ");
                    }
                } else{
                    System.out.println(" " +  " " + " ");
                }if (j < BOARD_SIZE - 1){
                    System.out.println("|");
                }
            }
            System.out.println();

            if (i < BOARD_SIZE - 1){
                System.out.println("   ");
                for (int j = 0; j < BOARD_SIZE; j++){
                    System.out.println("---");
                    if (j < BOARD_SIZE - 1){
                        System.out.println("+");
                    }
                }
                System.out.println();
            }
        }
    }
}
