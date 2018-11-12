/**
 * CS 4200
 * Professor: D. Atanasio
 *
 * Project #3
 *
 *	Make a program to play a (4-in-a-line) game on an 8x8 board. Players take turns placing a piece on any 
 *	grid, and first player to get 4 in a line (either row, column) (DIAGONALS ARE NOT COUNTED) wins. The 
 *	Maximum amount of time allowed for generating a new move is 25 seconds. At the very beginning, your program
 *	should ask the user for the max amount time (in seconds) allowed for the computer to generate the answer and 
 *	make sure that your search will stop when there's no time left. Your program should also ask the user to decide
 *	who is going to move first.
 *
 *	Program must use alpha-beta pruning to determine computers move. will need to make some changes to alpha-beta
 *	pruning introduced in book. These include: an evaluation function so that you can evaluate non-terminal states and a
 *	cut-off test to replace the terminal test so that your program will return the best solution found so far given a 
 *	specific period of time. 
 *	
 *
 *
 * @author Eric Schenck
 * last modified: 11/12/18
 */

import java.util.Scanner;


public class project3 {
	
	private static boolean gameIsWon = false;
	private static boolean opponentIsFirst;
	
	public static void main(String[] args) {
		
		runGame();
	}
	
	public static void runGame() {
		
		UserInterface UI = new UserInterface();
		Alpha_Beta_Pruning ABP = new Alpha_Beta_Pruning();
		
//		//////////////////////
//		UserInterface UTI = new UserInterface();
//		
//		UTI.whoGoesFirst();
//		UTI.setGameboard("b4");
//		UTI.setGameboard("b3");
//		UTI.setGameboard("c4");
//		UTI.setGameboard("c3");
//		UTI.setGameboard("d4");
//		UTI.setGameboard("d3");
//		UTI.setGameboard("e4");
//		UTI.printBoard(UTI.currentGameboard);
//		
//		GameState gs = new GameState(UTI.getOpponentFirst(), UTI.currentGameboard);
//		//gs.setGameSize(UTI.getGameSize());
//		if(gs.playerWins()) {
//			System.out.println("Boom");
//			System.exit(0);
//		}
//		
//		//////////////////////////////////////
							
		ABP.setMaxTime(UI.maxTimePrompt());				// getting maxTime from user input
		
		UI.whoGoesFirst();								// getting which player will go first
		
		opponentIsFirst = UI.getOpponentFirst();
		

		
		//ABP.checkIfFirst(UI.currentGameboard);
		
		if(opponentIsFirst) {						// if opponent is to go first 
			ABP.setOpponentFirst(true);
			UI.setGameboard(UI.askOpponentMove());		// get opponenet move and set gameboard 
			UI.printBoard(UI.currentGameboard);
		}else
			ABP.setOpponentFirst(false);			
		
		loop: while(!gameIsWon) {						// while game has not been won
			
		

			
			UI.setGameboard(ABP.generatePlay(UI.currentGameboard));	// get alphaBeta Pruning best move
						
			gameIsWon = UI.checkGameboard(UI.currentGameboard);	// check if won after every move
			
			if(gameIsWon) {										
				UI.playerWins();
				break loop;
			}
			
			UI.printBoard(UI.currentGameboard);	

			UI.setGameboard(UI.askOpponentMove());		// get opponent move and set gameboard
			
			gameIsWon = UI.checkGameboard(UI.currentGameboard);
			if(gameIsWon) {
				UI.opponentWins();
			}else
				UI.printBoard(UI.currentGameboard);
		}
		
		UI.printBoard(UI.currentGameboard);	
	}
	
	

	
	
	
	
}
