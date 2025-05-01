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
        //Check is there is one red or black piece left
        boolean redLeft = currentBoard.containsValue(Mark.R);
        boolean blackLeft = currentBoard.containsValue(Mark.B);

        if (!redLeft || !blackLeft) {
            //If there are none, game is over, no pieces left for a player
            return true;
        }
        if (getAllRemainingMoves(currentBoard, currentMark).isEmpty()) {
            //If there are no valid moves, game must end
            return true;
        }
        //Game is not over
        return false;
    }
    public Mark isKing(Square piece, Mark currentMark){
        //Checks if something can be kinged, depends on mark being fed in

        if (piece.row() == 0 && currentMark == Mark.B){ //Back row, Black
            return Mark.BK;
        }
        if (piece.row() == BOARD_SIZE - 1 && currentMark == Mark.R){ //Front row, Red
            return Mark.RK;
        }
        //Neither, keep same mark
        return currentMark;
    }

    public void execute(Moves<Square, Square, Square> move, boolean isMax) {
        //This 'executes' the move by placing the mark on the board
        Mark currentMark;
        if (isMax) { //Max is Black
            currentMark = Mark.B;

            //Check if either moves are king, to show when it's there, we do to,
            //to show it when it leaves, we do from
            currentMark = isKing(move.from(), currentMark);
            currentMark = isKing(move.to(), currentMark);

        } else { //Min is Red
            currentMark = Mark.R;

            //Same kinging logic as max
            currentMark = isKing(move.from(), currentMark);
            currentMark = isKing(move.to(), currentMark);
        }
        if (move.jump != null) { //Jump

            board.remove(move.from()); //remove previous place
            board.remove(move.jump()); //remove jumped over piece

            board.put(move.to(), currentMark);//Add where we are going

        } else {
            //Walk if jump is null

            board.remove(move.from());//remove from
            board.put(move.to(), currentMark);//place to

        }
    }

    public void undo(Moves<Square, Square, Square> move, boolean isMax) {
        //Removes move from map
        Mark currentMark;
        Mark enemyMark;

        if (isMax) {//Max is black

            currentMark = Mark.B;
            enemyMark = Mark.R;

        } else { //Min is red

            currentMark = Mark.R;
            enemyMark = Mark.B;

        }

        if (move.jump != null) { //Same logic as execute

            board.remove(move.to()); //remove where we are going
            board.remove(move.jump()); //remove jumped over piece

            board.put(move.from(), currentMark); //Put the mark back to where it belongs
            board.put(move.jump(), enemyMark); //replace jumped piece
            return;
        }

        //Walk if jump is null
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
                        //Check for either red piece or red king
                        score--;
                    }
                    if ((currentBoard.get(square) == Mark.B) || (currentBoard.get(square) == Mark.BK)) {
                        //Check for either black piece or black king
                        score++;
                    }
                }
            }
        }
        boolean redLeft = currentBoard.containsValue(Mark.R) || currentBoard.containsValue(Mark.RK); //Same logic as isTerminal
        boolean blackLeft = currentBoard.containsValue(Mark.B) || currentBoard.containsValue(Mark.BK);

        if (!redLeft) return 10; //Can increase these values, but left at 10 for tightness
        if (!blackLeft) return -10;

        return score;
    }

    private boolean inRange(int number1, int number2, int upperbound, int lowerbound) {
        //Important private function to check if a square is inrange of the board
        //lowerbound is often 0

        return (upperbound > number1 && lowerbound <= number1) && (number2 >= lowerbound && number2 < upperbound);
    }

    private List<Moves<Square, Square, Square>> walkCheck(Square square, int walkX, int walkY, Map<Square, Mark> currentBoard, Mark mark) {
        //Check if we can walk

        List<Moves<Square, Square, Square>> result = new ArrayList<>();
        if (inRange(walkY, walkX, BOARD_SIZE, 0)) { //is in range
            Square walked = new Square(walkY, walkX);

            boolean Check4 = (!currentBoard.containsKey(walked)); //Check 1, and 2 are in jumps, Check4 was uneeded
                                                                  //Left in from before seperation of getremainingmoves
            if (Check4) {
                result.add(new Moves<>(square, walked, null)); //jump is null for walk
            }
        }

        return result;
    }

    private List<Moves<Square, Square, Square>> jumpCheck(Square square, int jumpX, int jumpY, Map<Square, Mark> currentBoard, Mark mark) {
        //Check jumps

        Mark enemyKing;
        Mark enemyMark;

        if ((mark == Mark.B) || (mark == Mark.BK)) {
            //Statement made to ensure we can jump over kings too
            //Black enemy is RED
            enemyMark = Mark.R;
            enemyKing = Mark.RK;
        }
        else {
            //Red enemy is BLACK
            enemyMark = Mark.B;
            enemyKing = Mark.BK;
        }

        List<Moves<Square, Square, Square>> result = new ArrayList<>(); //must return empty list lest we upset the nullpointerexception

        if (inRange(jumpY, jumpX, BOARD_SIZE, 0)) {//is in range

            Square jumped = new Square(jumpY, jumpX);

            Square over = new Square(((square.row() + jumpY) / 2), ((square.column() + jumpX) / 2)); //simplified over equation
            Mark overMark = currentBoard.get(over);//ensure we are using the correct mark

            boolean Check1 = (!currentBoard.containsKey(jumped));
            boolean Check2 = (overMark == enemyMark || overMark == enemyKing); //Told you

            if (Check1) { //board has jumped
                if (Check2) { //over is an enemy

                    result.add(new Moves<>(square, jumped, over));
                }
            }
        }
        return result; //CAN BE EMPTY, CANNOT BE NULL
    }


    public List<Moves<Square, Square, Square>> getAllRemainingMoves(Map<Square, Mark> currentBoard, Mark currentMark) {
        //Only remaining moves can be in diagonals
        //Going to have to include bit where it limits the amount of rows
        //Needs to return moves

        Mark normalMark;
        Mark kingMark;

        if (currentMark == Mark.R || currentMark == Mark.RK){
            //Same logic as checks
            //Red enemy BLACK
            normalMark = Mark.R;
            kingMark = Mark.RK;

        }else{
            //Same logic as checks
            //Black enemy RED
            normalMark = Mark.B;
            kingMark = Mark.BK;
        }

        List<Moves<Square, Square, Square>> jumps = new ArrayList<>();
        List<Moves<Square, Square, Square>> walks = new ArrayList<>();

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                Square square = new Square(y, x);

                if(currentBoard.containsKey(square)) { //we are in the board right?

                    if (currentBoard.get(square) == normalMark) { //as far as the logic goes now, we are taking in a R or B piece, not king

                        if (normalMark == Mark.R) { //check Red
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
                        if (normalMark == Mark.B) { //check black
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
                    if (currentBoard.get(square) == kingMark) { //KINGS BABY
                        if (kingMark == Mark.RK) { //Red king check
                            Square square2 = new Square(y, x);

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

                        if (kingMark == Mark.BK) { //Black king check
                            Square square2 = new Square(y, x);

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
            //jumps is more important than walks as they are more effective
            return jumps;
        } else {
            //Walks are still effective too, just not as much
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

        System.out.println("   ");
        for (int col = 0; col < BOARD_SIZE; col++) {
            System.out.print("  " + col + " ");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(" " + i + " ");

            for (int j = 0; j < BOARD_SIZE; j++) {
                Square square = new Square(i, j);
                if (currentBoard.containsKey(square)) {
                    if (currentBoard.get(square) == Mark.R) { //Red print
                        System.out.print(" " + RED + currentBoard.get(square) + RESET + " ");
                    }
                    if (currentBoard.get(square) == Mark.B) { //Black print
                        System.out.print(" " + WHITE + currentBoard.get(square) + RESET + " ");
                    }
                    if (currentBoard.get(square) == Mark.RK) { //Red King
                        System.out.print(" " + RED + currentBoard.get(square) + RESET + " ");
                    }
                    if (currentBoard.get(square) == Mark.BK) { //Black king
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



