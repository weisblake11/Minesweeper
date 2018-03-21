
package cs1302.p1;

import java.io.File;
import java.util.Scanner;
import java.util.Random;

/**
 * This class represents a Minesweeper game.
 *
 * @author Frederick Blake Weis <fbw77130@uga.edu>
 */
public class Minesweeper {

    private int numberOfRows, numberOfCols, numberOfBombs;
    private int roundsCompleted = 0;
    private static int score = 0;
    private static int[][] grid;
    private static String[][] displayGrid;
    private boolean stillPlaying;
    private boolean justUsedNoFog;

    /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * information provided in <code>seedFile</code>. Documentation about the 
     * format of seed files can be found in the project's <code>README.md</code>
     * file.
     *
     * @param seedFile the seed file used to construct the game
     * @see            <a href="https://github.com/mepcotterell-cs1302/cs1302-minesweeper-alpha/blob/master/README.md#seed-files">README.md#seed-files</a>
     */
    public Minesweeper(File seedFile) {
	
	try {
	    Scanner s = new Scanner(seedFile);

	    Scanner firstLine = new Scanner(s.nextLine());
	    if (firstLine.hasNextInt()) {
		numberOfRows = firstLine.nextInt();
	    } else {
		printBadFile(seedFile);
		System.exit(0);
	    }
	    if (firstLine.hasNextInt()) {
		numberOfCols = firstLine.nextInt();
	    } else {
		printBadFile(seedFile);
		System.exit(0);
	    }
	    if ((numberOfRows < 1) || (numberOfCols < 1) || (numberOfRows > 10) || (numberOfCols > 10)) {
		printBadMineField();
		System.exit(0);
	    } else {
		createSeedGrid(numberOfRows, numberOfCols);
	    }

	    Scanner secondLine = new Scanner(s.nextLine());
	    if (secondLine.hasNextInt()) {
		numberOfBombs = secondLine.nextInt();
	    } else {
		printBadFile(seedFile);
		System.exit(0);
	    }
	    if (numberOfBombs > (numberOfRows * numberOfCols)) {
		System.out.println();
		System.out.println("Too many mines for this grid size.");
		System.out.println();
		System.exit(0);
	    }

	    int bombRow = -1;
	    int bombCol = -1;
	    for (int i = 0; i < numberOfBombs; i++) {
		if (s.hasNextLine()) {
		    Scanner theNextLine = new Scanner(s.nextLine());
		    if (theNextLine.hasNextInt()) {
		        bombRow = theNextLine.nextInt();
		    } else {
		        printBadFile(seedFile);
		        System.exit(0);
		    }
		    if (theNextLine.hasNextInt()) {
		        bombCol = theNextLine.nextInt();
		    } else {
		        printBadFile(seedFile);
		        System.exit(0);
		    }
		    if (isInBounds(bombRow, bombCol)) {
		        grid[bombRow][bombCol] = 1;
		    } else {
		        System.out.println();
		        System.out.println("Error: Bomb out of range");
		        System.out.println();
		        System.exit(0);
		    }
		} else {
		    printBadFile(seedFile);
		    System.exit(0);
		}
	    }

	    createDisplayGrid();
        
	} catch (Exception e) {
	}

	// Scanner reader = new Scanner(new File("seedFile"));
        // TODO implement

    }  // Minesweeper

    // This method creates the grid of bombs and non bombs for a game created from a seed file.
    public void createSeedGrid(int numberOfRows, int numberOfCols) {
	grid = new int[numberOfRows][numberOfCols];

	for (int row = 0; row < numberOfRows; row++) {
	    for (int col = 0; col < numberOfCols; col++) {
		grid[row][col] = 0;
	    }
	}
    }
    
    // Print this error when the seed file is not formatted correctly.
    public void printBadFile(File seedFile) {
	System.out.println();
	System.out.println("Cannot create game with " + seedFile + ", because it is not formatted correctly.");
	System.out.println();
    }

    /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * <code>rows</code> and <code>cols</code> values as the game grid's number
     * of rows and columns respectively. Additionally, One quarter (rounded up) 
     * of the squares in the grid will will be assigned mines, randomly.
     *
     * @param rows the number of rows in the game grid
     * @param cols the number of cols in the game grid
     */
    public Minesweeper(int rows, int cols) {
	
	numberOfRows = rows;
	numberOfCols = cols;
	if ((numberOfRows < 1) || (numberOfCols < 1) || (numberOfRows > 10) || (numberOfCols > 10)) {
	    printBadMineField();
	    System.out.println();
	    System.exit(0);
	}
	numberOfBombs = (int)Math.ceil((((double)numberOfRows * numberOfCols) /4) );
       
	createGrid(numberOfRows, numberOfCols);
	createDisplayGrid();

    } // Minesweeper


    /**     * Starts the game and execute the game loop.
     */
    public void run() {
       
        stillPlaying = true;

	// Print the Minesweeper title and set up the game.
	printMinesweeper();
	// printGrid();
	System.out.println();
	System.out.println("  Rounds Completed: " + roundsCompleted);
	printInterface();
	
	// Keep asking for the player's next move until they lose, win, or quit.
	while(stillPlaying) {
	    getNextMoveMakeNextMove();
	    System.out.println();
	    System.out.println("  Rounds Completed: " + roundsCompleted);
	    if (justUsedNoFog == false) {
	        printInterface();
	        if ((numberOfBombs == getNumberCorrectlyFlaggedMines()) && (numberOfBombs == getNumberFlagsDown())) {
		    score = (numberOfRows * numberOfCols) - numberOfBombs - roundsCompleted;
		    printDoge();
		    stillPlaying = false;
		}
	    }
	}
    } // run

    // This method sets up a bomb location grid of a manullly created game.
    public void createGrid(int rows, int cols) {

	grid = new int[rows][cols];

	//generate random bomb locations and set 
        for(int i = 0; i < numberOfBombs; i++) {
	    Random r = new Random();
	    boolean stillNeedLocation = true;
	    while (stillNeedLocation) {
		int bombRow = r.nextInt(rows);
		int bombCol = r.nextInt(cols);
		if (grid[bombRow][bombCol] != 1) {
		    grid[bombRow][bombCol] = 1;
		    stillNeedLocation = false;
		} // if checking to make sure bomb is not at spot
	    } // while we still need an appropriate bomb location
	} // for loop to assign every bomb a location
    }

    // This method occurs every round, asks the user for their move then does that action.
    public void getNextMoveMakeNextMove() {
	Scanner keyboard = new Scanner(System.in);
	int row = -1;
	int col = -1;
	String move = "none";
	
	System.out.println();
	System.out.print(" minesweeper-alpha$ ");
   
	String userLineInput = keyboard.nextLine();
	Scanner lineReader = new Scanner(userLineInput);

	if (lineReader.hasNext()) {
	    move = lineReader.next();
	} else {
	    printBadCommand();
	    roundsCompleted++;
	    return;
	}

	justUsedNoFog = false;
	
	switch (move) {
	case "reveal":
	case "r":
	    roundsCompleted++;
	    if (lineReader.hasNextInt()) {
		row = lineReader.nextInt();
	    } else {
		printBadCommand();
		break;
	    }
	    if (lineReader.hasNextInt()) {
		col = lineReader.nextInt();
	    } else {
		printBadCommand();
		break;
	    } 
	    //
	    if (lineReader.hasNext()) {
		printBadCommand();
		break;
	    }
	    //
	    if (isInBounds(row,col)) {
		updateDisplayGrid(row,col);
		if (grid[row][col] == 1) {
		    printInterface();
		    printGameOver();
		    System.exit(0);
		}
	    } else {
		printOutOfBounds();
		break;
	    }
	    break;

	case "mark":
	case "m":
	    roundsCompleted++;
	    if (lineReader.hasNextInt()) {
                row = lineReader.nextInt();
            } else {
                printBadCommand();
                break;
            }
	    if (lineReader.hasNextInt()) {
	        col = lineReader.nextInt();
	    } else {
	        printBadCommand();
		break;
	    }
	    //
	    if (lineReader.hasNext()) {
		printBadCommand();
		break;
	    }
	    //
	    if (isInBounds(row,col)) {
		displayGrid[row][col] = "F";
	    } else {
		printOutOfBounds();
		break;
	    }
	    break;

	case "guess":
	case "g":
	    roundsCompleted++;
	    if (lineReader.hasNextInt()) {
                row = lineReader.nextInt();
            } else {
                printBadCommand();
                break;
            }
	    if (lineReader.hasNextInt()) {
	        col = lineReader.nextInt();
	    } else {
	        printBadCommand();
	        break;
	    }
	    //
	    if (lineReader.hasNext()) {
		printBadCommand();
		break;
	    }
	    //
	    if (isInBounds(row,col)) {
	        displayGrid[row][col] = "?";
	    } else {
	        printOutOfBounds();
	        break;
	    }
	    break;

	case "help":
	case "h":
	    roundsCompleted++;
	    //
	if (lineReader.hasNext()) {
	    printBadCommand();
	    break;
	}
	    //
	    System.out.println();
	    System.out.println(" Commands Available...");
	    System.out.println("  - Reveal: r/reveal row col");
	    System.out.println("  -   Mark: m/mark   row col");
	    System.out.println("  -  Guess: g/guess  row col");
	    System.out.println("  -   Help: h/help");
	    System.out.println("  -   Quit: q/quit");
	    break;

	case "quit":
	case "q":
	    printQuit();
	    System.exit(0);
	    break;
	    
	case "nofog":
	    roundsCompleted++;
	    printNoFog();
	    justUsedNoFog = true;
	    break;

	default:
	    roundsCompleted++;
	    printBadCommand();
	    break;
	}
    }

    // This method makes sure a location has coordinates within the grid of the game.
    public boolean isInBounds(int row, int col) {

	if ((row >= 0) && (row < numberOfRows) && (col >= 0) && (col < numberOfCols)) {
	    return true;
	} else {
	    return false;
	}
    }

    // This method counts the number of bombs touching each spot.
    public int getNumAdjBombs(int row, int col) {

	int numAdjBombs = 0;
	
	// check if it is a 1x1 game
	if (numberOfRows == 1 && numberOfCols == 1) {
	    return 0;
	}

	// check if there is only 1 row
	if (numberOfRows == 1) {
	    if (col == 0) {
		numAdjBombs = grid[row][col+1];
	    }
	    else if (col == numberOfCols - 1) {
		numAdjBombs = grid[row][col-1];
	    }
	    else {
		numAdjBombs = grid[row][col-1] + grid[row][col+1];
	    }
	}
			    
	// check if there is only 1 column
	else if (numberOfCols == 1) {
	    if (row == 0) {
		numAdjBombs = grid[row+1][col];
	    }
	    else if (row == numberOfRows - 1) {
		numAdjBombs = grid[row-1][col];
	    }
	    else {
		numAdjBombs = grid[row-1][col] + grid[row+1][col];
	    }
	}

	// every normal game
	else {
	    if ((row == 0) && (col == 0)) {
	        numAdjBombs = grid[row][col+1] + grid[row+1][col] + grid[row+1][col+1];
	    } // top left corner check
	    else if ((row == 0) && (col == numberOfCols - 1)) {
       	        numAdjBombs = grid[row][col-1] + grid[row+1][col] + grid[row+1][col-1];
       	    } // top right corner check
       	    else if ((row == numberOfRows - 1) && (col == 0)) {
       	        numAdjBombs = grid[row-1][col] + grid[row][col+1] + grid[row-1][col+1];
       	    } // bottom left corner check
       	    else if ((row == numberOfRows - 1) && (col == numberOfCols - 1)) {
       	        numAdjBombs = grid[row][col-1] + grid[row-1][col] + grid[row-1][col-1];
       	    } // bottom right corner check

       	    else if (col == 0) {
       	        numAdjBombs = grid[row-1][col] + grid[row-1][col+1] + grid[row][col+1] + grid[row+1][col+1] + grid[row+1][col];
       	    } // left side check
       	    else if (row == 0) {
       	        numAdjBombs = grid[row][col-1] + grid[row+1][col-1] + grid[row+1][col] + grid[row+1][col+1] + grid[row][col+1];
       	    } // top side check
       	    else if (col == numberOfCols - 1) {
       	        numAdjBombs = grid[row-1][col] + grid[row-1][col-1] + grid[row][col-1] + grid[row+1][col-1] + grid[row+1][col];
       	    } // right side check
       	    else if (row == numberOfRows - 1) {
       	        numAdjBombs = grid[row][col-1] + grid[row-1][col-1] + grid[row-1][col] + grid[row-1][col+1] + grid[row][col+1];
       	    } // bottom side check

       	    else {
	        numAdjBombs = grid[row-1][col-1] + grid[row-1][col] + grid[row-1][col+1] + grid[row][col-1] + grid[row][col+1] + grid[row+1][col-1] + grid[row+1][col] + 
		    grid[row+1][col+1];
	    } // everything in the middle
	}

	return numAdjBombs;
    } // return number of adjacent bombs

    // This is an unused method to help check that my getNumAdjBombs method is working correctly.
    public void justChecking() {
	for (int i = 0; i < numberOfRows; i++) {
	    for (int j = 0; j < numberOfCols; j++) {
		if (grid[i][j] == 1) {
		    System.out.print("B\t");
		}
		else {
		    System.out.print(getNumAdjBombs(i,j) + "\t");
		}
	    }
		System.out.println();
	}
    }

    // This method counts the number of bombs that the user has correctly marked.
    public int getNumberCorrectlyFlaggedMines() {
	int correctlyMarkedMines = 0;
	for (int row = 0; row < numberOfRows; row++) {
	    for (int col = 0; col < numberOfCols; col++) {
		if ((grid[row][col] == 1) && (displayGrid[row][col] == "F")) {
		    correctlyMarkedMines++;
		}
	    }
	}
	return correctlyMarkedMines;
    }

    // This method counts the total number of marks the user has made.
    public int getNumberFlagsDown() {
	int flagsDown = 0;
	for (int row = 0; row < numberOfRows; row++) {
	    for (int col = 0; col < numberOfCols; col++) {
		if (displayGrid[row][col] == "F") {
		    flagsDown++;
		}
	    }
	}
	return flagsDown;
    }

    // This method creates the "invisible" grid that will be manipulated and displyed to the user through the interface.
    public void createDisplayGrid() {

	displayGrid = new String[numberOfRows][numberOfCols];

	for (int row = 0; row < numberOfRows; row++) {
	    for (int col = 0; col < numberOfCols; col++) {
		displayGrid[row][col] = " ";
	    }
	}
    }

    // This method changes the location of interest from "invisible" to revealed.
    public void updateDisplayGrid(int row, int col) {
	
	if (grid[row][col] == 1) {
	    displayGrid[row][col] = "B";   
	} else {
	    displayGrid[row][col] = "" + getNumAdjBombs(row,col);
	}
	
    }

    // This method generates the interface that the user will see during the game.
    public void printInterface() {
	
	System.out.println();

	for (int row = 0; row < numberOfRows; row++) {
	    System.out.print("  ");
	    for (int col = 0; col < numberOfCols; col++) {
		if (col == 0) {
		    System.out.print(row + " | " + displayGrid[row][col] + " |");
		}
		else {
		    System.out.print(" " + displayGrid[row][col] + " |");
		}
	    } // ending inner loop
	    System.out.println();
	} // ending outter loop

	// print out lower numbering
	for (int col = 0; col < numberOfCols; col ++) {
	    if (col == 0) {
		System.out.print("      " + col);
	    }
	    else {
		System.out.print("   " + col);
	    }
	}
	System.out.println();
    }

    // EXTRA CREDIT method that prints out the interface without the "fog of war".
    public void printNoFog() {
	System.out.println();

	for (int row = 0; row < numberOfRows; row++) {
	    System.out.print("  ");
	    for (int col = 0; col < numberOfCols; col++) {

		if (col == 0) {
		    if (grid[row][col] == 1) {
			System.out.print(row + " |<" + displayGrid[row][col] + ">|");
		    } else {
		    System.out.print(row + " | " + displayGrid[row][col] + " |");
		    }
		}
		    
	        else {
		    if (grid[row][col] == 1) {
			System.out.print("<" + displayGrid[row][col] + ">|");
		    } else {
			System.out.print(" " + displayGrid[row][col] + " |");
		    }
		}  
	    } // ending inner loop
	    System.out.println();
	} // ending outer loop

	// print out lower numbers
	for (int col = 0; col < numberOfCols; col++) {
	    if (col == 0) {
		System.out.print("      " + col);
	    } else {
		System.out.print("   " + col);
	    }
	}
	System.out.println();
    }

    // This is a helper method the prints out the location of the bombs for my personal use.
    public void printGrid() {
	System.out.println();
	for(int i = 0; i < numberOfRows; i++) {
	    for(int j = 0; j < numberOfCols; j++) {
		System.out.print(grid[i][j] + "\t");
	    }
	    System.out.println();
	}
    }

    // This method prints the Minesweeper title
    public void printMinesweeper() {
    
	System.out.println();
        System.out.println("   /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __");
        System.out.println("  /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|");
        System.out.println(" / /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |");
        System.out.println(" \\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|");
        System.out.println("                              ALPHA EDITION |_| v2017.f");

    }

    // This method prints game over.
    public void printGameOver() {

	System.out.println();
	System.out.println("  Oh no... You revealed a mine!");
	System.out.println("   __ _  __ _ _ __ ___   ___    _____   _____ _ __");
        System.out.println("  / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|");
	System.out.println(" | (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |");
	System.out.println("  \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|");
        System.out.println("  |___/");
	System.out.println();

    }

    // This method prints the winner screen.
    public void printDoge() {

	System.out.println();
	System.out.println(" ░░░░░░░░░▄░░░░░░░░░░░░░░▄░░░░ \"So Doge\"");
	System.out.println(" ░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌░░░");
	System.out.println(" ░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐░░░ \"Such Score\"");
	System.out.println(" ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░░");
	System.out.println(" ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐░░░ \"Much Minesweeping\"");
	System.out.println(" ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░░");
	System.out.println(" ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒▌░░ \"Wow\"");
	System.out.println(" ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░░");
	System.out.println(" ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄▌░");
	System.out.println(" ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒▌░");
	System.out.println(" ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒▐░");
	System.out.println(" ▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒▒▒▌");
	System.out.println(" ▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒▒▐░");
	System.out.println(" ░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒▒▌░");
	System.out.println(" ░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒▐░░");
	System.out.println(" ░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒▌░░");
	System.out.println(" ░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀░░░ CONGRATULATIONS!");
	System.out.println(" ░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀░░░░░ YOU HAVE WON!");
	System.out.println(" ░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▀▀░░░░░░░░ SCORE: " + score);
	System.out.println();
    }

    // This method prints the screen when the player quits.
    public void printQuit() {
	System.out.println();
	System.out.println(" ლ(ಠ_ಠლ)");
	System.out.println(" Y U NO PLAY MORE?");
	System.out.println(" Bye!");
	System.out.println();
    }

    // This method prints the screen when the player makes an unreccognized command.
    public void printBadCommand() {
	System.out.println();
	System.out.println(" ಠ_ಠ says, \"Command not recognized!\"");
    }

    // This method prints the screen when the player tries to create a game with inappropriate sizes.
    public void printBadMineField() {
	System.out.println();
	System.out.println(" ಠ_ಠ says, \"Cannot create a mine field with that many rows and/or columns!\"");
	System.out.println();
    }

    // This method prints the screen when an action done in a location outside the boundaries of the game grid.
    public void printOutOfBounds() {
	System.out.println();
	System.out.println(" ಠ_ಠ says, \"Out of Bounds!\"");
    }


    /**
     * The entry point into the program. This main method does implement some
     * logic for handling command line arguments. If two integers are provided
     * as arguments, then a Minesweeper game is created and started with a 
     * grid size corresponding to the integers provided and with 10% (rounded
     * up) of the squares containing mines, placed randomly. If a single word 
     * string is provided as an argument then it is treated as a seed file and 
     * a Minesweeper game is created and started using the information contained
     * in the seed file. If none of the above applies, then a usage statement
     * is displayed and the program exits gracefully. 
     *
     * @param args the shell arguments provided to the program
     */
    public static void main(String[] args) {

	/*
	  The following switch statement has been designed in such a way that if
	  errors occur within the first two cases, the default case still gets
	  executed. This was accomplished by special placement of the break
	  statements.
	*/

	Minesweeper game = null;
	
	switch (args.length) {

        // random game
	case 2: 

	    int rows, cols;

	    // try to parse the arguments and create a game
	    try {
		rows = Integer.parseInt(args[0]);
		cols = Integer.parseInt(args[1]);
		game = new Minesweeper(rows, cols);
		break;
	    } catch (NumberFormatException nfe) { 
		// line intentionally left blank
	    } // try

	// seed file game
	case 1: 

	    String filename = args[0];
	    File file = new File(filename);

	    if (file.isFile()) {
		game = new Minesweeper(file);
		break;
	    } // if
    
        // display usage statement
	default:

	    System.out.println("Usage: java Minesweeper [FILE]");
	    System.out.println("Usage: java Minesweeper [ROWS] [COLS]");
	    System.exit(0);

	} // switch

	// if all is good, then run the game
	game.run();

    } // main


} // Minesweeper
