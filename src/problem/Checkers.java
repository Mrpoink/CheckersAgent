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

        if (utility == 1 || utility == -1){
            //If someone has won, return true
            return true;
        }
        //If no one won, game is a draw or unfinished
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
        //Removes move from map
        if (isMax) {
            board.remove(move.to());
            board.put(move.from(), Mark.R);
            return;
        }
        board.remove(move.to());
        board.put(move.from(), Mark.B);
    }

    public int utility(Map<Square,Mark> currentBoard){
        int red = 0; //AI is red piece
        int black = 0; //Player is black piece
        for (int row = 0; row < BOARD_SIZE; row++){
            for (int col = 0; col< BOARD_SIZE; col++){
                Square square = new Square(row, col);
                if (currentBoard.containsKey(square)){
                    if (board.get(square) == Mark.R){
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

    private boolean inRange(int number1, int number2,int upperbound, int lowerbound){
        return (upperbound > number1 && lowerbound <= number1) && (number2 >= lowerbound && number2 < upperbound);
    }

    private List<Moves<Square, Square>> jumpWalkCheck(Square square, int jumpY, int jumpX, int walkY, int walkX, Map<Square, Mark> currentBoard, Mark mark) {
        Mark player;
        Mark enemy;
        if (mark == Mark.B){
            player = mark;
            enemy = Mark.R;
        }else{
            player = mark;
            enemy = Mark.B;
        }

        List<Moves<Square, Square>> result = new ArrayList<>();
        if (inRange(jumpY, jumpX, BOARD_SIZE, 0)) {


            Square jumped = new Square(jumpY, jumpX);
            Square over = new Square(walkY, walkX);

            System.out.println("Check 1: " + jumped + " " + over + " " + (currentBoard.containsKey(jumped) && (currentBoard.get(over) == enemy)));
            System.out.println("Check 2: " + jumped + " " + over + " " + ((currentBoard.get(jumped) != enemy) || (currentBoard.get(jumped) != player)));
            if ((currentBoard.containsKey(jumped) && (currentBoard.get(over) == enemy)) && ((currentBoard.get(jumped) != enemy) || (currentBoard.get(jumped) != player)) ) {
                System.out.println("Found jump");
                result.add(new Moves<>(square, jumped));
            }
        }
        if (inRange(walkY, walkX, BOARD_SIZE, 0)) {
            Square walked = new Square(walkY, walkX);

            System.out.println("Check3: "+ " " + walked + " " + ((currentBoard.get(walked) != enemy) && (currentBoard.get(walked) != player)));
            System.out.println("Check4: "+ " " + walked + " "  + (!currentBoard.containsKey(walked) && ((currentBoard.get(walked) != enemy) || (currentBoard.get(walked) != player))));
            if (!currentBoard.containsKey(walked) && ((currentBoard.get(walked) != enemy) && (currentBoard.get(walked) != player))) {
                result.add(new Moves<>(square, walked));
            }
        }
        return result;
    }


    public List<Moves<Square, Square>> getAllRemainingMoves(Map<Square, Mark> currentBoard) {
        //Only remaining moves can be in diagonals
        //Going to have to include bit where it limits the amount of rows
        //Needs to return moves
        List<Moves<Square, Square>> result = new ArrayList<>();
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                Square square = new Square(y, x);

                if (currentBoard.get(square) == Mark.R){

                    int jumpY = y + 2; int jumpX = x - 2; int walkY = y + 1; int walkX = x - 1;//lower left
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.B));

                    jumpY = y + 2; jumpX = x + 2; walkY = y + 1; walkX = x + 1;//lower right
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.B));
//
//                    jumpY = y - 2; jumpX = x - 2; walkY = y + 1; walkX = x - 1;//upper left,                  R can't go up
//                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.R));
//
//                    jumpY = y - 2; jumpX = x + 2; walkY = y + 1; walkX = x + 1;//upper right
//                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.R));

                }
                if (board.get(square) == Mark.B){
//                    int jumpY = y + 2; int jumpX = x - 2; int walkY = y + 1; int walkX = x - 1;//lower left,   B cant go down
//                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.B));
//
//                    jumpY = y + 2; jumpX = x + 2; walkY = y + 1; walkX = x + 1;//lower right
//                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, currentBoard, Mark.B));

                    int jumpY = y - 2; int jumpX = x - 2; int walkY = y + 1; int walkX = x - 1;//upper left
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, board, Mark.B));

                    jumpY = y - 2; jumpX = x + 2; walkY = y + 1; walkX = x + 1;//upper right
                    result.addAll(jumpWalkCheck(square, jumpY, jumpX, walkY, walkX, board, Mark.B));

                }

            }
        }
        System.out.println("Result: " + result);
        return result;
    }

    //Checks if there is already a move there
    public boolean markedSquare(Square square){ return board.containsKey(square); }

    public void makeBoard() {
        //3x3, 1 occupied, 1 empty
        //5x5, 2 occupied, 1 empty
        //7x7, 3 occupied, 1 empty
        //9x9, 3 occupied, 3 empty
        //11x11 3 occupied, 5 empty
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
//          COMMENTED OUT BECAUSE WE NEED TO GET 3X3 TO WORK FIRST
//        }if (BOARD_SIZE == 5){ //5x5
//            for (int row = 1; row <= BOARD_SIZE; row++) {
//                for (int col = 1; col <= BOARD_SIZE; col++) {
//                    if ((row <= 2) && (col % 2 == 1) && (row % 2 == 1)) {
//                        Square square = new Square(row, col);
//                        board.put(square, Mark.R);
//                    }else if((row >= BOARD_SIZE - 2) && (col % 2 == 0) && (row % 2 == 0)){ //Occupies 2
//                        Square square = new Square(row, col);
//                        board.put(square, Mark.B);
//                    }
//                }
//            }
//        }else if (BOARD_SIZE >= 7){
//            for (int row = 1; row <= BOARD_SIZE; row++) {
//                for (int col = 1; col <= BOARD_SIZE; col++) {
//                    if ((row <= 3) && (col % 2 == 1) && (row % 2 == 1)){
//                        Square square = new Square(row, col);
//                        board.put(square, Mark.R);
//                    }else if((row >= BOARD_SIZE - 3) && (col % 2 == 1) && (row % 2 == 1)){
//                        Square square = new Square(row, col);
//                        board.put(square, Mark.B);
//                    }
//                }
//            }
//        }
//    }
        }           //Extra parenthesis due to comment
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
//    public static void main(String[] args){
//        Checkers checkers = new Checkers(5);
//        checkers.printBoard();
//    }
}


