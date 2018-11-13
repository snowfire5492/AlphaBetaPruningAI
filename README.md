# Alpha Beta Pruning Artificial Inteligent game playing agent


Make a program to play a (4-in-a-line) game on an 8x8 board. Players 
take turns placing a piece on any grid, and first player to get 4 in a line (either row, column) 
(DIAGONALS ARE NOT COUNTED) wins. The Maximum amount of time allowed for generating a new move is 25 
seconds. At the very beginning, your program should ask the user for the 
max amount time (in seconds) allowed 
for the computer to generate the answer and make sure that your search will stop when there's no time left. 
Your program should also ask the user to decide who is going to move 
first.
 Program must use alpha-beta pruning to determine computers move. 
will need to make some changes to alpha-beta pruning introduced in book. 
	These include: an evaluation 
function so that you can evaluate non-terminal states and a cut-off test 
to replace the terminal test so that your program 
will return the best solution found so far given a specific period of 
time. 
