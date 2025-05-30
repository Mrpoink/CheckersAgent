package solution;

import core.*;

import problem.*;
import problem.Mark;
import problem.Square;
import problem.Checkers;
import java.util.*;

public class CheckersRunner extends Minimax<Square, Mark> {

    private static final int BOARD_SIZE = 5;

    //Mark.R: AI
    //Mark.B: Human

    private Mark turn = Mark.R;
    private final Checkers game;

    public CheckersRunner(Checkers game) {
        super(game);
        this.game = game;
    }

    public void play(){
        game.makeBoard(); //Must call this because our board isn't created with the game

        while (!game.isTerminal(game.board, turn)){ //Stops is isterminal

            game.printBoard(game.board); //I wanna see it!!
            System.out.println();
            if(turn == Mark.B){
                game.execute(getUserMove(), true); //Black
                turn = Mark.R;
            }else{
                System.out.println("AI turn: ");
                game.execute(minimaxSearch(), false); //Red
                turn = Mark.B;
            }
        }
        game.printBoard(game.board);
        announceWinner(game.utility(game.board));
    }

    private Checkers.Moves<Square,Square, Square> getUserMove(){
        int row=-1, col=-1, grab_row=-1, grab_col=-1;
        Checkers.Moves<Square, Square, Square> playerMove = null;
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
            playerMove = isValidMove(grab_row, grab_col, row, col);
            if (playerMove != null){
                validInput = true;
            }else{
                System.out.println("Invalid please try again");
            }
        }
        return playerMove;
    }

    private Checkers.Moves<Square, Square, Square> isValidMove(int frow, int fcol, int trow, int tcol){
        //From row, From column, To row, To column

        List<Checkers.Moves<Square, Square, Square>> validMoves = game.getAllRemainingMoves(game.board, Mark.B);
        //Returns possible moves for player
        //I worked really hard on this so let me use it where i can >:(

        for (Checkers.Moves<Square, Square, Square> move : validMoves){
            //Checks if the player move exists in possible moves
            if ((new Square(frow, fcol)).equals(move.from()) && (new Square(trow, tcol)).equals(move.to())){
                return move;
            }
        }
        return null;
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
