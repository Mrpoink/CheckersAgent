package problem;

import java.text.Normalizer;
import java.util.*;

public class Checkers implements Game<Checkers.Moves<Square, Square, Square>, Mark> {

    private final int BOARD_SIZE;

    public final Map<Square, Mark> board;

    public Checkers(int size) {
        this.BOARD_SIZE = size;
        this.board = new HashMap<>();
    }

    public record Moves<A, B, C>(Square from, Square to, Square jump) {
    }

    public Map<Square, Mark> getBoard() {
        return this.board;
    }

    public boolean isTerminal(Map<Square, Mark> currentBoard, Mark currentMark) {
        //Check utility
        boolean redLeft = currentBoard.containsValue(Mark.R);
        boolean blackLeft = currentBoard.containsValue(Mark.B);

        if (!redLeft || !blackLeft) {
            return true;
        }
        if (getAllRemainingMoves(currentBoard, currentMark).isEmpty()) {
            return true;
        }
        return false;
    }
    public Mark isKing(Square piece, Mark currentMark){

        System.out.println("FOUND KING");

        if (piece.row() == 0 && currentMark == Mark.B){
            return Mark.BK;
        }
        if (piece.row() == BOARD_SIZE - 1 && currentMark == Mark.R){
            return Mark.RK;
        }
        return currentMark;
    }

    public void execute(Moves<Square, Square, Square> move, boolean isMax) {
        //This 'executes' the move by placing the mark on the board
        Mark currentMark;
        if (isMax) {
            currentMark = Mark.B;
            currentMark = isKing(move.from(), currentMark);
            currentMark = isKing(move.to(), currentMark);
        } else {
            currentMark = Mark.R;
            currentMark = isKing(move.from(), currentMark);
            currentMark = isKing(move.to(), currentMark);
        }
        //System.out.println("Checking: " + move.from() + ", " + move.to() + " over " + move.jump());
        if (move.jump != null) { // Jump

            board.remove(move.from());
            board.remove(move.jump());

            System.out.println("B: " + move.from() + ", " + move.to());
            //System.out.println("Board before: " + board);
            board.put(move.to(), currentMark);
            //System.out.println("Board after: " + board);
        } else {
            //Walk

            System.out.println("R: " + move.from() + "," + move.to());
            board.remove(move.from());

            //System.out.println("Board before: " + board);
            board.put(move.to(), currentMark);
            //System.out.println("Board after: " + board);
        }
        System.out.println(board);
    }

    public void undo(Moves<Square, Square, Square> move, boolean isMax) {
        //Removes move from map
        Mark currentMark;
        Mark enemyMark;
        if (isMax) {
            currentMark = Mark.B;
            enemyMark = Mark.R;
        } else {
            currentMark = Mark.R;
            enemyMark = Mark.B;
        }
        if (move.jump != null) {
            board.remove(move.to());
            board.remove(move.jump());
            board.put(move.from(), currentMark);
            board.put(move.jump(), enemyMark);
            return;
        }
        board.remove(move.to());
        board.put(move.from(), currentMark);
    }

    public int utility(Map<Square, Mark> currentBoard) {
        int score = 0;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Square square = new Square(row, col);
                if (currentBoard.containsKey(square)) {
                    if ((currentBoard.get(square) == Mark.R) || (currentBoard.get(square) == Mark.RK)) {
                        score--;
                    }
                    if ((currentBoard.get(square) == Mark.B) || (currentBoard.get(square) == Mark.BK)) {
                        score++;
                    }
                }
            }
        }
        boolean redLeft = currentBoard.containsValue(Mark.R) || currentBoard.containsValue(Mark.RK);
        boolean blackLeft = currentBoard.containsValue(Mark.B) || currentBoard.containsValue(Mark.BK);

        if (!redLeft) return 10;
        if (!blackLeft) return -10;
        return score;
    }

    private boolean inRange(int number1, int number2, int upperbound, int lowerbound) {
        return (upperbound > number1 && lowerbound <= number1) && (number2 >= lowerbound && number2 < upperbound);
    }

    private List<Moves<Square, Square, Square>> walkCheck(Square square, int walkX, int walkY, Map<Square, Mark> currentBoard, Mark mark) {

        List<Moves<Square, Square, Square>> result = new ArrayList<>();
        if (inRange(walkY, walkX, BOARD_SIZE, 0)) {
            Square walked = new Square(walkY, walkX);

            boolean Check4 = (!currentBoard.containsKey(walked));

            if (Check4) {
                result.add(new Moves<>(square, walked, null));
            }
        }
        return result;
    }

    private List<Moves<Square, Square, Square>> jumpCheck(Square square, int jumpX, int jumpY, Map<Square, Mark> currentBoard, Mark mark) {
        Mark enemyKing;
        Mark enemyMark;
        if ((mark == Mark.B) || (mark == Mark.BK)) {
            enemyMark = Mark.R;
            enemyKing = Mark.RK;
        }
        else {
            enemyMark = Mark.B;
            enemyKing = Mark.BK;
        }

        List<Moves<Square, Square, Square>> result = new ArrayList<>();
        if (inRange(jumpY, jumpX, BOARD_SIZE, 0)) {
            //System.out.println(square + " in range for jump");


            Square jumped = new Square(jumpY, jumpX);

            //System.out.println(jumped + " in jump");
            Square over = new Square(((square.row() + jumpY) / 2), ((square.column() + jumpX) / 2));
            //System.out.println(over + " in over");

            Mark overMark = currentBoard.get(over);
            boolean Check1 = (!currentBoard.containsKey(jumped));
            boolean Check2 = (overMark == enemyMark || overMark == enemyKing);
            if (Check1) {
                if (Check2) {
                    //System.out.println("Found jump");
                    result.add(new Moves<>(square, jumped, over));
                }
            }
        }
        return result;
    }


    public List<Moves<Square, Square, Square>> getAllRemainingMoves(Map<Square, Mark> currentBoard, Mark currentMark) {
        //Only remaining moves can be in diagonals
        //Going to have to include bit where it limits the amount of rows
        //Needs to return moves
        Mark normalMark;
        Mark kingMark;
        if (currentMark == Mark.R || currentMark == Mark.RK){
            normalMark = Mark.R;
            kingMark = Mark.RK;
        }else{
            normalMark = Mark.B;
            kingMark = Mark.BK;
        }
        List<Moves<Square, Square, Square>> jumps = new ArrayList<>();
        List<Moves<Square, Square, Square>> walks = new ArrayList<>();

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                Square square = new Square(y, x);
                if(currentBoard.containsKey(square)) {
                    if (currentBoard.get(square) == normalMark) {
                        System.out.println("True: " + square + " " + currentMark);
                        if (normalMark == Mark.R) {
                            Square square1 = new Square(y, x);
                            //System.out.println("Red found" + " " + square1);

                            int jumpY = y + 2;
                            int jumpX = x - 2;
                            int walkY = y + 1;
                            int walkX = x - 1;//lower left
                            jumps.addAll(jumpCheck(square1, jumpX, jumpY, currentBoard, Mark.R));
                            walks.addAll(walkCheck(square1, walkX, walkY, currentBoard, Mark.R));

                            jumpY = y + 2;
                            jumpX = x + 2;
                            walkY = y + 1;
                            walkX = x + 1;//lower right
                            jumps.addAll(jumpCheck(square1, jumpX, jumpY, currentBoard, Mark.R));
                            walks.addAll(walkCheck(square1, walkX, walkY, currentBoard, Mark.R));

                        }
                        if (normalMark == Mark.B) {
                            Square square2 = new Square(y, x);

                            int jumpY = y - 2;
                            int jumpX = x - 2;
                            int walkY = y - 1;
                            int walkX = x - 1;//upper left
                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.B));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.B));

                            jumpY = y - 2;
                            jumpX = x + 2;
                            walkY = y - 1;
                            walkX = x + 1;//upper right
                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.B));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.B));


                        }
                    if (currentBoard.get(square) == kingMark) {
                        if (kingMark == Mark.RK) {
                            Square square2 = new Square(y, x);
                            System.out.println("Red King found" + " " + square2 + "****************************");

                            int walkY = y - 1;
                            int jumpY = y - 2;//upper

                            int jumpX = x - 2;
                            int walkX = x - 1;//left

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.RK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.RK));

                            jumpX = x + 2;
                            walkX = x + 1;//right

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.RK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.RK));

                            jumpY = y + 2;
                            walkY = y + 1;//lower

                            walkX = x - 1;//left
                            jumpX = x - 2;

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.RK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.RK));


                            jumpX = x + 2;
                            walkX = x + 1;//right

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.RK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.RK));
                        }

                        if (kingMark == Mark.BK) {
                            Square square2 = new Square(y, x);
                            System.out.println("Black King found" + " " + square2 + "****************************");

                            int walkY = y - 1;
                            int jumpY = y - 2;//upper

                            int jumpX = x - 2;
                            int walkX = x - 1;//left

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.BK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.BK));

                            jumpX = x + 2;
                            walkX = x + 1;//right

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.BK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.BK));

                            jumpY = y + 2;
                            walkY = y + 1;//lower

                            walkX = x - 1;//left
                            jumpX = x - 2;

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.BK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.BK));


                            jumpX = x + 2;
                            walkX = x + 1;//right

                            jumps.addAll(jumpCheck(square2, jumpX, jumpY, currentBoard, Mark.BK));
                            walks.addAll(walkCheck(square2, walkX, walkY, currentBoard, Mark.BK));
                        }
                    }
                    }
                }

            }
        }
        if (!jumps.isEmpty()) {
            //System.out.println("Jumps: " + jumps);
            return jumps;
        } else {
            System.out.println("Walks: "+ walks);
            return walks;
        }
    }

    //Checks if there is already a move there
    public boolean markedSquare(Square square) {
        return board.containsKey(square);
    }

    public void makeBoard() {
        //3x3, 1 occupied, 1 empty
        //5x5, 2 occupied, 1 empty
        //7x7, 3 occupied, 1 empty
        //9x9, 3 occupied, 3 empty
        //11x11 3 occupied, 5 empty
//        if (BOARD_SIZE == 3) { //3x3
//            for (int row = 0; row < BOARD_SIZE; row++) {
//                for (int col = 0; col < BOARD_SIZE; col++) {
//                    if ((row == 0) && ((col == 0) || (col == 2))) {
//                        Square square = new Square(row , col);
//                        board.put(square, Mark.R);
//                    } else if ((row == 2) && ((col == 0) || (col == 2))) {
//                        Square square = new Square(row , col );
//                        board.put(square, Mark.B);
//                    }
//                }
//            }
////          COMMENTED OUT BECAUSE WE NEED TO GET 3X3 TO WORK FIRST
        if (BOARD_SIZE == 5) { //5x5
            for (int row = 0; row <= BOARD_SIZE - 1; row++) {
                for (int col = 0; col <= BOARD_SIZE - 1; col++) {
                    if (row < 2) {
                        if (((col % 2 == 1) && (row % 2 == 1)) || ((col % 2 == 0) && (row % 2 == 0))) {
                            Square square = new Square(row, col);
                            board.put(square, Mark.R);
                        }
                    }
                    if ((row >= BOARD_SIZE - 2)) {
                        if (((col % 2 == 0) && (row % 2 == 0)) || (col % 2 == 1) && (row % 2 == 1)) { //Occupies 2
                            Square square = new Square(row, col);
                            board.put(square, Mark.B);
                        }
                    }
                }
            }
        }
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


    public void printBoard(Map<Square, Mark> currentBoard) {
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String WHITE = "\u001B[37m";

        System.out.println("  ");
        for (int col = 0; col < BOARD_SIZE; col++) {
            System.out.print(" " + col + " ");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(" " + i + " ");

            for (int j = 0; j < BOARD_SIZE; j++) {
                Square square = new Square(i, j);
                if (currentBoard.containsKey(square)) {
                    if (currentBoard.get(square) == Mark.R) {
                        System.out.print(" " + RED + currentBoard.get(square) + RESET + " ");
                    }
                    if (currentBoard.get(square) == Mark.B) {
                        System.out.print(" " + WHITE + currentBoard.get(square) + RESET + " ");
                    }
                    if (currentBoard.get(square) == Mark.RK) {
                        System.out.print(" " + RED + currentBoard.get(square) + RESET + " ");
                    }
                    if (currentBoard.get(square) == Mark.BK) {
                        System.out.print(" " + WHITE + currentBoard.get(square) + RESET + " ");
                    }
                } else {
                    System.out.print(" " + " " + " ");
                }
                if (j < BOARD_SIZE - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();

            if (i < BOARD_SIZE - 1) {
                System.out.print("   ");
                for (int j = 0; j < BOARD_SIZE; j++) {
                    System.out.print("---");
                    if (j < BOARD_SIZE - 1) {
                        System.out.print("+");
                    }
                }
                System.out.println();
            }
        }
    }
}
//    public static void main(String[] args){
//        Checkers checkers = new Checkers(5);
//        checkers.printBoard();
//    }



