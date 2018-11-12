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

import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class Alpha_Beta_Pruning {
	
	
	
	private int maxTime;								// max amount of time allowed for search
	private boolean firstMove;
	private boolean firstCheckReq;
	private boolean opponentIsFirst;
	private boolean withinTimeLimit = true;
	
	public Alpha_Beta_Pruning() {
		firstCheckReq = true;
	}
	
	
	
	
	
	/**
	 * Used to generate the AI's best play using alpha-beta pruning techniques and constrained to an alloted time
	 * @param gameboard
	 * @return next best play in form of 'A1' to 'H8'
	 */
	public String generatePlay(char[][] gameboard) {
		String play = null;
		
		Timer timer = new Timer();
		
		if(firstCheckReq) {
			firstMove = checkIfFirst(gameboard);
			firstCheckReq = false;
		}
		
		if(firstMove) {
			play = getFirstMove();
			firstMove = false;
		}else {
			
			timer.schedule(new TimerTask() {
				public void run() {
					withinTimeLimit = false;  		// too much time has elapsed so set false 
//					searchCost = 0;
//					++failedPuzzleCount;
					timer.cancel();
				}
			}, maxTime);
			
			GameState head = new GameState(opponentIsFirst, true,  gameboard);
			
			play = alphaBetaSearch(head);
			// 2nd move and on using alpha beta pruning 
		}
			
		return play;
	}
	
	private int[] utilityChecker;
	
	public String alphaBetaSearch(GameState head) {
		String play = "";
		
		int depth = 10;
		
		
		int utility = maxValue(head, depth, Integer.MAX_VALUE, Integer.MIN_VALUE);
		
		GameState[] children = head.getChildren();
		
		utilityChecker = new int[children.length];
		
		for(int i = 0; i< children.length; ++i) {
			
			if( children[i].getUtilityValue() == utility) {
				
				utilityChecker[i] = utility;
				
				//System.out.println("utility: " + utility);
				play = children[i].getNewPlay();
			}
			
			
			
		}
		// figure out the play based on the utility value and the children[] in head and the newPieceIndex
		
		return play;
	}
	
	
	public int maxValue(GameState state, int depth, int alpha, int beta) {
		int utility = 0;
		
		if(state.stateIsTerminal() || !withinTimeLimit || depth == 0) {
			
			//System.out.println("utility : " + state.getUtilityValue());
			return state.getUtilityValue();
		}
		--depth;
		utility = Integer.MIN_VALUE;
		
		GameState[] successors = state.generateChildren();
		
		//System.exit(0);
		
		for(int i = 0; i < successors.length; ++i) {
			
			utility = max(utility, minValue(successors[i], depth, alpha, beta));
			
			if( utility >= beta) {
				return utility;
			}
			
			alpha = max(alpha, utility);
			
		}
		
		return utility;
	}
	
	
	public int minValue(GameState state, int depth, int alpha, int beta) {
		int utility = 0;
		
		if(state.stateIsTerminal() || !withinTimeLimit || depth == 0) {
			return state.getUtilityValue();
		}
		--depth;
		utility = Integer.MAX_VALUE;
		
		GameState[] successors = state.generateChildren();
		
		for(int i = 0; i < successors.length; ++i) {
			
			utility = min(utility, maxValue(successors[i], depth, alpha, beta));
			
			if( utility >= beta) {
				return utility;
			}
			
			alpha = min(alpha, utility);
			
		}
		
		return utility;
	}
	
	public int min(int a, int b) {
		int min = 0;
		
		if(a > b) {
			min = b;
		}else
			min = a;
		
		return min;
	}
	
	
	public int max(int a, int b) {
		int max = 0;
		
		if(a > b) {
			max = a;
		}else
			max = b;
		
		return max;
	}
	
	
	/**
	 * checks gameboard to see if anyone has played a move yet
	 * @param gameboard
	 * @return true if no move has been made yet
	 */
	public boolean checkIfFirst(char[][] gameboard) {
		boolean isFirst = true;
		
		loop: for(int i = 0 ; i < gameboard.length ; ++i) {
			for(int j = 0; j < gameboard.length ; ++j) {
				
				if(gameboard[i][j] != '-') {
					isFirst = false;
					break loop;
				}
			}
		}
		return isFirst;
	}
	
	

	/**
	 * Returns a somewhat random, somewhat chosen first location for AI move, in case of AI going first
	 * @return
	 */
	public String getFirstMove() {
		String play = null;
		
		Random rand = new Random();
		int row = rand.nextInt(5);
		int column = rand.nextInt(5);
		
		switch(row) {
		case 0:
			play = "C" + column;
			break;
		case 1:
			play = "D" + column;
			break;
		case 2:
			play = "E" + column;
			break;
		case 3:
			play = "F" + column;
			break;
		case 4:
			play = "G" + column;
			break;
		default:
			break;
		}
		
		return play;
	}
	
	
	
	public void setMaxTime(double maxTime) {
		
		this.maxTime = (int) (maxTime * 1000);	// this.maxTime is in milliseconds
	}
	
	public void setOpponentFirst(boolean OppIsFirst) {
		this.opponentIsFirst = OppIsFirst;
	}
	
}
