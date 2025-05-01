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

    private Mark turn = Mark.R;
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
                game.execute(getUserMove(), true);
                turn = Mark.R;
            }else{
                System.out.println("AI turn: ");
                game.execute(minimaxSearch(), false);
                turn = Mark.B;
            }
        }
        game.printBoard(game.board);
        announceWinner(game.utility(game.board));
    }

    private Checkers.Moves<Square,Square, Square> getUserMove(){
        int row=-1, col=-1, grab_row=-1, grab_col=-1;
        boolean validInput = false;
        Scanner scan = new Scanner(System.in);
        while(!validInput){
            System.out.println("Your turn: enter row and column of piece to move (seperated by a space)" +
                    " and then after two spaces enter where to move it (seperated by a space)");

            if (scan.hasNextInt()){
                grab_row = scan.nextInt();
            }else{
                scan.next();
                continue;
            }
            if (scan.hasNextInt()) {
                grab_col = scan.nextInt();
            }else{
                scan.next();
                continue;
            }if (scan.hasNextInt()){
                row = scan.nextInt();
            }else{
                scan.next();
                continue;
            }
            if (scan.hasNextInt()) {
                col = scan.nextInt();
            }else{
                scan.next();
                continue;
            }
            if (isValidMove(row, col) && isValidMove(grab_row, grab_col)){
                validInput = true;
            }else{
                System.out.println("Invalid please try again");
            }
        }
        return new Checkers.Moves<>(new Square(grab_row, grab_col), new Square(row, col), null);
    }

    private boolean isValidMove(int row, int col){
        boolean isWithinBounds = row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
        return isWithinBounds && !game.markedSquare(new Square(row, col));
    }

    private void announceWinner(int utility){
        if (utility == 1){
            System.out.println("\nPlayer (R) wins!!");
        }else if(utility == -1){
            System.out.println("\nAI (B) wins!!");
        }else{
            System.out.println("Its a draw!!");
        }
    }

    public static void main(String[] args){
        CheckersRunner runner  = new CheckersRunner(new Checkers(BOARD_SIZE));
        runner.play();
    }

}
