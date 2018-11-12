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

public class GameState {
	
	

	private int gameSize;

	private int[] newPieceIndex;
	private char[][] playTracker;
	private char[][] currentboard;
	private int alpha, beta;
	private int utilityValue;
	private GameState[] children;
	private boolean terminalState;
	private char playerPiece;
	private char oppPiece;
	private boolean opponentIsFirst;
	private boolean TwoIsUsed = false;
	private boolean playerTurn;
	
	public GameState(boolean opponentIsFirst, boolean playerTurn , char[][] currentboard) {
		this.currentboard = currentboard;
		this.opponentIsFirst = opponentIsFirst;
		this.playerTurn = playerTurn;
		newPieceIndex = new int[2];
		//gameSize = UI.getGameSize();
		gameSize = currentboard.length;
		playTracker = new char[gameSize][gameSize];
		
		if(opponentIsFirst) {
			playerPiece = 'O';			// just means player went second so got the 'O' chip
			oppPiece = 'X';
			//System.out.println("piece is " + playerPiece);
		}else {
			playerPiece = 'X';			// just means player went first so got the 'X' chip
			oppPiece = 'O';
		}
		
		if(checkGameboard(currentboard)) {			// if true we've hit a terminal state and there is at least one winner
			terminalState = true;
			setUtilityValue(getActualUtilityValue());		// reached a true utility state so return a value accordingly 
		}else if(boardIsFull()){
			terminalState = true;
			setUtilityValue(0);							// if the board has been completely filled
		}else {
			setUtilityValue(estimateUtility(currentboard));
			terminalState = false;
		}
	
		
		
		
	}
	
	public boolean stateIsTerminal() {
		return terminalState;
	}
	
	public void setNewPieceIndex(int[] index) {
		newPieceIndex = index;
	}
	
	
	public String getNewPlay() {
		String play = "";
		char location = '-'; 
		
		switch(newPieceIndex[0]) {
		case 0:
			location = 'A';
			break;
		case 1:
			location = 'B';
			break;
		case 2:
			location = 'C';
			break;
		case 3:
			location = 'D';
			break;
		case 4:
			location = 'E';
			break;
		case 5:
			location = 'F';
			break;
		case 6:
			location = 'G';
			break;
		case 7: 
			location = 'H';
			break;
		}
		
		play = "" + location + (newPieceIndex[1] + 1);
		
		
		return play;
	}
	
	
	
	/**
	 * used to return an estimated Utility value in the case that the deepest nodes visited are not terminal states.
	 * 
	 * @param currentboard
	 * @return an integer value 
	 */
	public int estimateUtility(char[][] currentboard) {
		
		int tempUtility = 0;
		int utility = 0;
		int playerUtility = 0, oppUtility = 0;
		
		for(int i = 0; i < gameSize; ++i) {
			for(int j = 0; j< gameSize; ++j) {
				
				if(currentboard[i][j] == playerPiece) {	// used to get an estimate for player
						
					
					
					if(playTracker[i][j] != 'H') {
						
						tempUtility = UtilityCheckRight(i,j);
						
						if( tempUtility == 2) {
							if(!TwoIsUsed) {
								TwoIsUsed = true;
								playerUtility += tempUtility;
							}
							
						}else{
							playerUtility += tempUtility;
						}
						
						//playerUtility += UtilityCheckRight(i,j);
						
					}
					if(playTracker[i][j] != 'V') {
						
						tempUtility = UtilityCheckDown(i,j);
						
						if( tempUtility == 2) {
							if(!TwoIsUsed) {
								TwoIsUsed = true;
								playerUtility += tempUtility;
							}
							
						}else if( tempUtility == 3) {
							playerUtility += tempUtility;
						}
						
						
						//playerUtility += UtilityCheckDown(i,j); 
					} 
					
					
					//**** make sure that we never count a XX or OO twice so add a boolean and reset after each test
					
				}
					///*************** NEED TO MAKE IT SO I DONT REPEAT THE CHECKING OR ADDING OF VERTICAL AND HORIZOTAL GAME STATE SETTUPS
				
			}
		}
		resetPlaytracker();
		TwoIsUsed = false;
		
		for(int i = 0; i< gameSize; ++i) {
			for(int j = 0; j< gameSize; ++j) {
				if(currentboard[i][j] == oppPiece) { // used to get an estimate for opponent
					
					if(playTracker[i][j] != 'H') {
						
						tempUtility = UtilityCheckRight(i,j);
						
						if( tempUtility == 2) {
							if(!TwoIsUsed) {
								TwoIsUsed = true;
								oppUtility += tempUtility;
							}
							
						}else{
							oppUtility += tempUtility;
						}
						
						
						//oppUtility -= UtilityCheckRight(i,j);
					}
					if(playTracker[i][j] != 'V') {
						
						tempUtility = UtilityCheckDown(i,j);
						
						if( tempUtility == 2) {
							if(!TwoIsUsed) {
								TwoIsUsed = true;
								oppUtility += tempUtility;
							}
							
						}else if( tempUtility == 3) {
							oppUtility += tempUtility;
						}
						
						//oppUtility -= UtilityCheckDown(i,j); 
					} 	
					
				
				}
			}
		}
		resetPlaytracker();
		TwoIsUsed = false;
		
		//utility = playerUtility + oppUtility;
		
		if ( playerUtility < oppUtility ) {
			utility = -1;	
		}else if( playerUtility >= oppUtility) {
			utility = 1;
		}
		
		this.utilityValue = utility;
		
		return utility;
	}
	
	
	
	
	
	public void resetPlaytracker() {
		for(int i = 0; i < gameSize; ++i) {
			for(int j = 0; j< gameSize; ++j) {
				playTracker[i][j] = '-';
			}
		}
	}
	
	
	/**
	 * This function checks the given location for attached similar game pieces and returns a utility count.    
	 * 
	 * @param x (the i value from estimateUtility)
	 * @param y (the j value from estimateUtility)
	 * @return an estimated utility value for one set up game pieces
	 */
	public int UtilityCheckRight(int x, int y) {
		
		//int utility = 0;
		int xVal = x;
		int yVal = y;
		int utilityCounter = 0;
		int endPiecesToPlay = 2;
			
		if( yVal+1 < gameSize ) {
			if( currentboard[xVal][yVal] == currentboard[xVal][yVal + 1]) { 		// XX or OO
				utilityCounter = 2;
				playTracker[xVal][yVal] = 'H';
				playTracker[xVal][yVal + 1] = 'H';
				if( yVal+2 < gameSize ) {
					if( currentboard[xVal][yVal] == currentboard[xVal][yVal + 2]) {	// XXX or OOO
						utilityCounter = 3;
						playTracker[xVal][yVal +2] = 'H';
						if( yVal+3 < gameSize ) {
							if( currentboard[xVal][yVal + 3] != '-') {				// one 
								endPiecesToPlay -= 1;
							}
						}else {
							endPiecesToPlay -= 1;
						}
						if(yVal-1 >= 0) {
							if( currentboard[xVal][yVal -1] != '-') {
								endPiecesToPlay -= 1;
							}
							//if (currentboard)
						}else
							endPiecesToPlay -= 1;
						
						if(endPiecesToPlay < 1) {
							utilityCounter = 0;
						}
						
					}else {
						if( currentboard[xVal][yVal + 2] != '-') {				// one 
							endPiecesToPlay -= 1;	
							
						}else {
							if(yVal+3 < gameSize ) {
								if(currentboard[xVal][yVal + 3] == '-') {
									endPiecesToPlay += 1;
								}
							}
						}
						
						
						if(yVal-1 >= 0) {
							if( currentboard[xVal][yVal -1] != '-') {
								endPiecesToPlay -= 1;
								
							}else {
								if(yVal - 2 >= 0) {
									if( currentboard[xVal][yVal -2] == '-') {
										endPiecesToPlay += 1;
									}
								}
							}
							//if (currentboard)
						}else
							endPiecesToPlay -= 1;
						
						if(endPiecesToPlay < 2) {
							utilityCounter = 0;
						}
					}
				}else {
					
					endPiecesToPlay -= 1;
					
					if(yVal-1 >= 0) {
						if( currentboard[xVal][yVal -1] != '-') {
							endPiecesToPlay -= 1;
						}else if(yVal-2 >= 0) {
							if( currentboard[xVal][yVal -2] != '-') {
								endPiecesToPlay -= 1;
							}
						}else
							endPiecesToPlay -= 1;
						//if (currentboard)
					}else
						endPiecesToPlay -= 1;
					
					if(endPiecesToPlay < 1) {
						utilityCounter = 0;
					}
				} 					
			}
		}
		

		return utilityCounter;
		
	}
	
	/**
	 * This function checks the given location for attached similar game pieces and returns a utility count.    
	 * 
	 * @param x (the i value from estimateUtility)
	 * @param y (the j value from estimateUtility)
	 * @return an estimated utility value for one set up game pieces
	 */
	public int UtilityCheckDown(int x, int y) {
		
		//int utility = 0;
		int xVal = x;
		int yVal = y;
		int utilityCounter = 0;
		int endPiecesToPlay = 2;
			
		if( xVal+1 < gameSize ) {
			if( currentboard[xVal][yVal] == currentboard[xVal +1][yVal]) { 		// XX or OO
				utilityCounter = 2;
				playTracker[xVal][yVal] = 'V';
				playTracker[xVal + 1][yVal] = 'V';
				if( xVal+2 < gameSize ) {
					if( currentboard[xVal][yVal] == currentboard[xVal + 2][yVal]) {	// XXX or OOO
						utilityCounter = 3;
						playTracker[xVal + 2 ][yVal] = 'V';
						if( xVal+3 < gameSize ) {
							if( currentboard[xVal + 3][yVal] != '-') {				// one 
								endPiecesToPlay -= 1;
							}
						}else {
							endPiecesToPlay -= 1;
						}
						if(xVal-1 >= 0) {
							if( currentboard[xVal -1][yVal] != '-') {
								endPiecesToPlay -= 1;
							}
							//if (currentboard)
						}else
							endPiecesToPlay -= 1;
						
						if(endPiecesToPlay < 1) {
							utilityCounter = 0;
						}
						
					}else {
						if( currentboard[xVal + 2][yVal] != '-') {				// one 
							endPiecesToPlay -= 1;
						}else {
							if(xVal+3 < gameSize ) {
								if(currentboard[xVal + 3][yVal] == '-') {
									endPiecesToPlay += 1;
								}
							}
						}
						
						if(xVal-1 >= 0) {
							if( currentboard[xVal -1][yVal] != '-') {
								endPiecesToPlay -= 1;
							}else {
								if(xVal - 2 >= 0) {
									if( currentboard[xVal -2][yVal] == '-') {
										endPiecesToPlay += 1;
									}
								}
							}
							//if (currentboard)
						}else
							endPiecesToPlay -= 1;
						
						if(endPiecesToPlay < 2) {
							utilityCounter = 0;
						}
					}
				}else {
					
					endPiecesToPlay -= 1;
					
					if(xVal-1 >= 0) {
						if( currentboard[xVal -1][yVal] != '-') {
							endPiecesToPlay -= 1;
						}else if(xVal-2 >= 0) {
							if( currentboard[xVal -2][yVal] != '-') {
								endPiecesToPlay -= 1;
							}
						}else
							endPiecesToPlay -= 1;
						//if (currentboard)
					}else
						endPiecesToPlay -= 1;
					
					if(endPiecesToPlay < 1) {
						utilityCounter = 0;
					}
				} 					
			}
		}
		

		return utilityCounter;
		
	}
	

	
	
	/**
	 * only used by getActualUtilityValue() in case a true terminal state is reached and a good utility value can 
	 * be found.
	 * 
	 * @return true if playerWins the match or false if opponent wins the match
	 */
	public boolean playerWins() {

		boolean playerWon = false;
		
		loop: for(int i = 0; i < gameSize; ++i) {
			for(int j = 0; j < gameSize; ++j) {
				if(currentboard[i][j] == playerPiece) {
					playerWon = checkRight(currentboard, i, j);
					
					if(!playerWon) {
						playerWon = checkDown(currentboard, i, j);
					}
				}
				if(playerWon)
					break loop;
			}
		}
		return playerWon;
	}
	
	
	public boolean checkRight(char[][] gameboard, int i, int j) {
		boolean gameWon = false;
		int tempJ = j + 1;
		
		if(tempJ < gameSize) {
			if(gameboard[i][j] == gameboard[i][tempJ++]){
				if(tempJ < gameSize) {
					if(gameboard[i][j] == gameboard[i][tempJ++]) {
						if(tempJ < gameSize) {
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
	
	
	public boolean checkDown(char[][] gameboard, int i, int j) {
	boolean gameWon = false; 
	int tempI = i + 1;
	
	if(tempI < gameSize) {
		if(gameboard[i][j] == gameboard[tempI++][j]){
			if(tempI < gameSize) {
				if(gameboard[i][j] == gameboard[tempI++][j]) {
					if(tempI < gameSize) {
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
	
	public boolean boardIsFull() {
		boolean boardIsFull = false;
		
		loop : for(int i = 0 ; i< gameSize; ++i) {
			for(int j = 0; j < gameSize; ++j) {
				if(currentboard[i][j] == '-') {
					boardIsFull = false;
					break loop;
				}else
					boardIsFull = true;
			}
		}
		
		return boardIsFull;
	}
	
	
	/**
	 * function to check gameboard for a win on either team
	 * @param gameboard
	 * @return boolean true if game has been won and false if not
	 */
	public boolean checkGameboard(char[][] gameboard) {
		boolean gameWon = false;
		
		loop: for(int i = 0; i < gameSize; ++i) {
			for(int j = 0; j < gameSize; ++j) {
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
	 * GameState is a winning state for someone, if UI.getOpponentIsFirst is true then 
	 * opponent has the X piece, else the player does. 
	 * @return utility value for winning terminal states. -3 (lose), 0 (tie), 3 (win)
	 */
	public int getActualUtilityValue() {
		
		int utilityValue = 0;  // -1 (lose), 0 (tie) , 1 (win)
		
		if(playerWins()) {
			utilityValue = 1;
		}else {
			utilityValue = -1;
		}
		
		return utilityValue;
	}
	
	
	public boolean getUtilityState() {
		return terminalState;
	}
	
	public GameState[] generateChildren() {
		
		// get number of open spots i.e. number of next generations
		int openSpots = getNumOpenSpots();
		children = new GameState[openSpots];
		int indexCount = 0;
		int[] index = new int[2];
		//UserInterface UI = new UserInterface();
		
		
		if(playerTurn) {
			
			for(int i = 0 ; i < gameSize ; ++i) {
				for(int j = 0; j < gameSize ; ++j) {
					
					
					
					
					if(currentboard[i][j] == '-') {
						char[][] tempboard = currentboard;
						tempboard[i][j] = playerPiece;
						children[indexCount++] = new GameState(opponentIsFirst, false, tempboard);
						
						
						
						index[0] = i;
						index[1] = j;
						children[indexCount - 1].setNewPieceIndex(index);
						
						//System.out.println(index[0] + " " + index[1]);
						//System.exit(0);
						//UI.printBoard(tempboard);
						
						tempboard[i][j] = '-';
						
						//System.exit(0);
					
					}
				}
			}
			//System.exit(0);
			
			
		}else {
			
			for(int i = 0 ; i < gameSize ; ++i) {
				for(int j = 0; j < gameSize ; ++j) {
					
					if(currentboard[i][j] == '-') {
						char[][] tempboard = currentboard;
						tempboard[i][j] = oppPiece;
						children[indexCount++] = new GameState(opponentIsFirst, true, tempboard);
						
						index[0] = i;
						index[1] = j;
						children[indexCount - 1].setNewPieceIndex(index);
					
						//UI.printBoard(tempboard);
						
						tempboard[i][j] = '-';
					}
					
					
				}
			}
			// use opp piece
		}
		
		
		
		return children;
	}
	
	public int getNumOpenSpots() {
		
		int count = 0;
		
		for(int i = 0 ; i < gameSize; ++i) {
			for(int j = 0; j < gameSize; ++j) {
				if(currentboard[i][j] == '-') {
					++count;
				}
			}
		}
	
		return count;
	}
	
	public char[][] getCurrentboard(){
		
		return currentboard;
	}
	
	public int getAlpha() {
		return alpha;
	}
	
	public int getBeta() {
		return beta;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public void setBeta(int beta) {
		this.beta = beta;
	}


	public int getUtilityValue() {
		return utilityValue;
	}


	public void setUtilityValue(int utilityValue) {
		this.utilityValue = utilityValue;
	}


	public GameState[] getChildren() {
		return children;
	}


	public void setChildren(GameState[] children) {
		this.children = children;
	}
	

}
