package ui;
import java.util.Scanner;
import core.Connect4;
import core.Connect4ComputerPlayer;
/**
 * @author: Duc Nguyen
 * @version: 2.00
 * date: 09-16-19
 * **/
/**
 * This is console class, when launch if user choose to play in terminal.
 * <br>
 * Then this is where the environment is built
 * 
 * **/
public class Connect4TextConsole {
	/*
	*	This is main method, where the actual game take place	*
	*/
	public static boolean checker;
	public static int count;
	public static Connect4 g;
	public static Connect4ComputerPlayer pc;
	public static String[][] board;
	/**
	 * main method
	 * 09-07-19 update: 
	 * 		add switch - for player options
	 * 
	 * 	This main is can simply identify as terminal board where each player will enter their piece.
	 * 	<br>
	 * 	Main method will initialize the Connect4 class as an object.
	 * 	<br>
	 *  Scanner in this method allows player to enter their command.
	 *  <br>
	 *  Note: for case option: 
	 *  <br>
	 *  To play with another player please enter PVP
	 *  <br>
	 *  To play with computer player please enter PVA
	 * **/
	
	public static void main() {//(String[] args) {
		// TODO Auto-generated method stub
		//Initialize object
		g = new Connect4();
		
		/*
		*	count variable determine players turn
		*	if count is even then player1 turn
		*	if count is odd then player2 turn
		*
		*	checker variable determine the existing of while loop
		*	if game is conclude then checker is false. Therefore, the while loop end
		*/
		System.out.println("Welcome to Connect4 Game Terminal Mode");
		count = 0;
		checker = true;
		Scanner sc = new Scanner(System.in);
		//call createBoard
		//call printBoard
		board = g.createBoard();
		pc = new Connect4ComputerPlayer();
		String option;
		boolean tmpChecker = true;
		
		while(tmpChecker)
		{
			System.out.println("Please enter the following command to initialize game mode: ");
			System.out.println("Player vs Player -> \t\t PVP");
			System.out.println("Player vs Computer -> \t\t PVA");
			System.out.print("Enter your command here: "); 
			option = sc.next();
			
			switch(option.toUpperCase())
			{
				case "PVP":
					checker = true;
					g.printBoard(board);
					vsHuGame();
					break;
				case "PVA":
					
					checker = true;
					g.printBoard(board);
					vsPcGame();
					break;
			}
			
			String tmpOption;
			System.out.println("Would you like to play another game?");
			System.out.println("To play another game please enter \"YES\" ");
			System.out.println("To exit game please enter \"NO\" or any random string");
			System.out.print("Enter your command here: ");
			tmpOption = sc.next();
			if(tmpOption.toUpperCase().equals("YES"))
			{
				count = 0;
				g.clearBoard();
				tmpChecker = true;
			}
			else
			{
				System.out.println("You have successfully exit the program");
				tmpChecker = false;
			}
		}
	}
	/**
	 *	This static method is invoked when user enter PVA in main
	 * 	<br>
	 * 	This method allows human player plays with computer player
	 * 	<br>
	 * 	Brief explanation: the login is continuously check the entire board through the game
	 *  <br>
	 *  When a special condition is found such as there exist 4 X pieces in the horizontal line, 
	 *  then the validator_win will trigger it event and tell player who is a winner
	 * 	
	 **/
	public static void vsPcGame()
	{
		while(checker)
		{
			//determine turn
			if(count % 2 == 0)
			{
				g.player(board, "X");
				g.printBoard(board);
			}
			else
			{
				pc.computerMove(board);
				g.printBoard(board);
			}
			count++;
			if(g.validator_Win(board) != null)
			{
				if(g.validator_Win(board) == "O")
				{
					System.out.println("Player O win");
				}
					
				else if(g.validator_Win(board) == "X")
				{
					System.out.println("Player X win");
				}
				checker = false;
			}
			//determine players pieces
			if(g.getPieceOne() == 0 || pc.getPiecePC() == 0)
			{
				System.out.println("Draw match");
				checker = false;
			}
		}
	}
	
	/**
	 *	This static method is invoked when user enter PVP in main
	 * 	<br>
	 * 	This method allows human player plays with another human player
	 *  <br>
	 *  When a special condition is found such as there exist 4 X pieces in the horizontal line, 
	 *  then the validator_win will trigger it event and tell player who is a winner
	 * 	
	 * 
	 **/
	public static void vsHuGame()
	{
		while(checker)
		{
			//determine turn
			if(count % 2 == 0)
			{
				g.player(board, "X");
				g.printBoard(board);
			}
			else
			{
				g.player(board, "O");
				g.printBoard(board);
			}
			
			//count increase after player finish the turn
			count++;
			
			//result validator
			
			if(g.validator_Win(board) != null)
			{
				if(g.validator_Win(board) == "X")
				{
					System.out.println("Player X win");
				}
					
				else if(g.validator_Win(board) == "O")
				{
					System.out.println("Player O win");
				}
				
				//checker become false when validator return result
				checker = false;
			}
			//determine players pieces
			if(g.getPieceOne() == 0 || g.getPieceTwo() == 0)
			{
				System.out.println("Draw match");
				//checker retunr false when players are out of pieces.
				//the game is also conclude as draw.
				checker = false;
			}
		}
	}



}
