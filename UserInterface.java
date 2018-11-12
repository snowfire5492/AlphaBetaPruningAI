
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


public class UserInterface {
	
	
	private double MAXIMUM_TIME_RANGE = 25.0;
	private int GAMESIZE = 8;						// gameBoard = GAMESIZE x GAMESIZE 
	
	
	private boolean nextIsX;						// to keep track of what variable will be used next
	private boolean opponentIsFirst;				// default is that player will go first, but will never hit default
	private String[] storedMoves;
	private int numOfMoves;
	char[] aThruH = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H' };			// used for print format and input check
	char[][] currentGameboard;
	
	
	Scanner keyboard;
	
	
	public UserInterface() {
		
		keyboard = new Scanner(System.in);			// used for input throughout program
		
		nextIsX = true;								// first move will be 'X' no matter who goes first
		opponentIsFirst = false;					// initialize values to defaults
		storedMoves = new String[(GAMESIZE * GAMESIZE)];
		numOfMoves = 0;
		
		currentGameboard = initialGameboard();
	}
	
	/**
	 * used to prompt for max allowed search time. Also checks for range of 0 - 25 seconds
	 * 
	 * @return maxTime allowed
	 */
	public double maxTimePrompt() { 	
		
		double maxTime = MAXIMUM_TIME_RANGE;
		boolean passedRangeCheck = false;

		
		while (!passedRangeCheck) {					// loop until range check is passed
		
			System.out.print("Please enter Maximum amount of time for search in seconds: ");
			maxTime = keyboard.nextDouble();
			passedRangeCheck = rangeCheck(maxTime);
		}
		
		return maxTime;
	}		
	
	/**
	 * used to run a range check on maxTime given by user, must be MAX 25 seconds.
	 * @param maxTime 
	 * @return boolean true if value passes 
	 */
	public boolean rangeCheck( double maxTime ) {
		
		boolean passed = false;
		
		if(maxTime <= MAXIMUM_TIME_RANGE && maxTime > 0 ) {
			passed = true;
		}else
			System.out.println("Please enter a value less than or equal to 25.0 seconds...");
		return passed;
	}
	
	
	/**
	 * prints prompt to ask user who will make first game move. 
	 * @return boolean true if opponent will go first
	 */
	public void whoGoesFirst() {
		
		boolean correctAnswer = false;
		String input;
		
		while(!correctAnswer) {
		
			System.out.print("Is your opponent making the first move? ('Y' or 'N') : ");
			input = keyboard.next();
		
			if(input.equalsIgnoreCase("y")) {
				opponentIsFirst = true;
				correctAnswer = true;
			
			}else if(input.equalsIgnoreCase("n")) {
				opponentIsFirst = false;
				correctAnswer = true;
				
			}else
				correctAnswer = false;
		}
	}
	
	/**
	 * This function prompts for opponent information and returns input in string form
	 * @return
	 */
	public String askOpponentMove() {
		
		String input = "";
		boolean validInput = false;
		
		while(!validInput) {
			System.out.print("\nChoose Opponent's next move: ");
			input = keyboard.next().toUpperCase();
	
			for(int i = 0; i < GAMESIZE; ++i) {						
				if(aThruH[i] == input.charAt(0)) {					// check initial value, ( A - H ) 
					
					String tempS = "";
					tempS += input.charAt(1);
					int tempI = Integer.parseInt(tempS); 
					
					if(tempI > 0 && tempI <= GAMESIZE) {			// check next value, ( 1 - 8 ) 
						
						loop: for(int j = 0; j < numOfMoves; ++j) {
							
							if(storedMoves[j].equalsIgnoreCase(input)) {	// input matches an already stored move
								validInput = false;
								break loop;
							}else 
								validInput = true;
						}
						if(numOfMoves == 0)									// used for the first case
							validInput = true;
					}
				}
			}
			if(!validInput) 
				System.out.println("Input was incorrect, please re-enter"); 	
		}
		return input;
	}
	
	
	public int getGameSize() {
		return GAMESIZE;
	}
	
	/**
	 * used to print out gameBoard in the required format given current 2d-array
	 * ( -  blank space , x Player space, o Opponent space
	 * 
	 * @param gameBoard
	 */
	public void printBoard(char[][] gameboard) {
		
		int tempCount = numOfMoves;
		int moveCount = 0;
		
		if(opponentIsFirst) {
			System.out.println("\n  1 2 3 4 5 6 7 8        Opponent vs. Player");
		}else
			System.out.println("\n  1 2 3 4 5 6 7 8        Player vs. Opponent");
		
		
		for(int i = 0; i < GAMESIZE; ++i) {
			
			System.out.print(aThruH[i]);
			
			for(int j = 0; j < GAMESIZE; ++j) {
				
				System.out.print(" " + gameboard[i][j]);	
			}
			
			if(tempCount == 1) {
				System.out.print("          " + (i+1) + ". ");
				
				System.out.print(storedMoves[moveCount++]);
				--tempCount;
			}else if(tempCount >= 2) {
				System.out.print("          " + (i+1) + ". ");
				for(int k = 0; k < 2; ++k) {
					System.out.print(storedMoves[moveCount++] + "  ");
					--tempCount;
				}
			}
			System.out.println();
		}
		
		int count = GAMESIZE;
		
		while(tempCount > 0) {						// used in-case more than 8 turns are taken to complete game
			
			if(tempCount == 1) {
				System.out.print("                           " + (count+1) + ". ");
				System.out.print(storedMoves[moveCount++]);
				--tempCount;
				
			}else if(tempCount >= 2) {
				System.out.print("                           " + (count+1) + ". ");
				for(int k = 0; k < 2; ++k) {
					System.out.print(storedMoves[moveCount++] + "  ");
					--tempCount;
				}
				System.out.println();
			}
			
			++count;
		}
	}
	
	
	/**
	 * builds initial gameboard of char[][] that is initialized to blank (-) spaces
	 * @return
	 */
	public char[][] initialGameboard(){
		
		char[][] gameboard = new char[GAMESIZE][GAMESIZE];
		
		for(int i = 0; i< GAMESIZE; ++i) {
			
			for(int j = 0; j < GAMESIZE; ++j) {
	
				gameboard[i][j] = '-';
			}	
		}
		return gameboard;
	}
	
	
	/**
	 * used to set the currentGameboard array with next play
	 * @param play
	 */
	public void setGameboard(String play){
		
		char[][] gameboard = currentGameboard;
		int y_coord = 0;
		int x_coord = 0;
		
		storedMoves[numOfMoves++] = play.toUpperCase();
		
		for(int i = 0; i < GAMESIZE; ++i) {
			if(aThruH[i] == play.toUpperCase().charAt(0)) {
				x_coord = i;
			}
		}
		
		String temp = "";
		temp += play.charAt(1);
		y_coord = Integer.parseInt(temp);
		--y_coord;
		
		if(nextIsX) {
			gameboard[x_coord][y_coord] = 'X';
			nextIsX = false;
		}else {
			gameboard[x_coord][y_coord] = 'O';
			nextIsX = true;
		}
		
		currentGameboard = gameboard;
		
	}
	
	
	/**
	 * function to check gameboard for a win on either team
	 * @param gameboard
	 * @return boolean true if game has been won and false if not
	 */
	public boolean checkGameboard(char[][] gameboard) {
		boolean gameWon = false;
		
		loop: for(int i = 0; i < GAMESIZE; ++i) {
			for(int j = 0; j < GAMESIZE; ++j) {
				if( gameboard[i][j] != '-') {
					
					gameWon = checkRight(gameboard, i, j);
					
					if(!gameWon) {
						gameWon = checkDown(gameboard, i, j);
					}
				}		
				if(gameWon)				// gameWon so stop searching 
					break loop;
			}
		}
		return gameWon;
	}
	
	
	/**
	 * function to check towards the right of any discovered game piece
	 * 
	 * @param gameboard current state of game
	 * @param i	index
	 * @param j index
	 * @return true if a game has been won in this direction and false if not
	 */
	public boolean checkRight(char[][] gameboard, int i, int j) {
		boolean gameWon = false;
		int tempJ = j + 1;
		
		if(tempJ < GAMESIZE) {
			if(gameboard[i][j] == gameboard[i][tempJ++]){
				if(tempJ < GAMESIZE) {
					if(gameboard[i][j] == gameboard[i][tempJ++]) {
						if(tempJ < GAMESIZE) {
							if(gameboard[i][j] == gameboard[i][tempJ]) {
								gameWon = true;
							}
						}
					}
				}
			}
		}
		return gameWon;
	}
	
	
	/**
	 * function to check towards the down of any discovered game piece
	 * 
	 * @param gameboard current state of game
	 * @param i
	 * @param j
	 * @return true if a game has been won in this direction and false if not
	 */
	public boolean checkDown(char[][] gameboard, int i, int j) {
		boolean gameWon = false; 
		int tempI = i + 1;
		
		if(tempI < GAMESIZE) {
			if(gameboard[i][j] == gameboard[tempI++][j]){
				if(tempI < GAMESIZE) {
					if(gameboard[i][j] == gameboard[tempI++][j]) {
						if(tempI < GAMESIZE) {
							if(gameboard[i][j] == gameboard[tempI][j]) {
								gameWon = true;
							}
						}
					}
				}
			}
		}
		return gameWon;
	}
	
	
	
	public void playerWins() {
		System.out.println("Player Wins!!!");
	}
	
	public void opponentWins() {
		System.out.println("Opponent wins...");
	}
	
	
	public boolean getOpponentFirst() {
		return opponentIsFirst;
	}
	
	public void printStoredMoves() {
		
		for(int i = 0; i < numOfMoves; ++i) {
			System.out.println(storedMoves[i]);
		}
	}

}
