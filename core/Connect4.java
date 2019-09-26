package core;
import java.util.Scanner;

/**
 * @author: Duc Nguyen
 * @version: 2.00
 * date: 09-16-19
 * **/

/**
 * Connect4 Class provide basic logic for the entire program except
 * <br>
 * Except, for the Connect4GUI which is an independent.
 * 
 * 
 * **/
public class Connect4{
	int pieceOne = 21;
	int pieceTwo = 21;
	int column=15;
	//int column = 7;
	int row=7;
	String player1 = "X";
	String player2 = "O";
	String [][] board;
	/**
	*	This method is a constructor.
	*	<br>
	*	Brief explanation:
	*	When being called it will initialize the game board
	*	with its column value and row value.
	*	<br>
	*	Column is equivalent with width.
	*	<br>
	*	Row is equivalent with height.
	**/
	public Connect4()
	{
		board=new String[row][column];
	}
	public Connect4(String opt)
	{
		
	}
	/**
	*	This method used for adding wall "|" to and "-" to the game board.
	*	<br>
	*	@return return board with column number and row number.
	*	<br><br><br>
	*
	*	Brief explanation: 
	*		Method takes 2 for loops (n^2) to complete the cycle.
	*	<br>
	*		i = row, j = column.
	*	<br>
	*		If odd number column found then add wall. Otherwise add space.
	*	<br>
	*		Add "-" for the last row, this is only for visualize purpose.
	*	<br>
	*	At the end method return a fully setup board with column and row.
	* 
	* 	<br><br>
	* 	This diagram visualize the detail how the game is built
	* 	<br>
	* 	Column is equivalent to Width
	* 	<br>
	*	Row is equivalent to Height
	*	<br>
	*	For column, there are 15 columns being chose instead of 7 columns because,
	*	we needed to add "|" to both side of each column.
	*	<br>
	*<br>
	*	Check diagram below for more detail on column explanation.
	*	<br><br><br><br>
	*  0 1 2 3 4 5 6 7 8 9 10 11 12 13 14  15
	*  <br>
	*  0   
	* <br>
	*  1
	* <br>
	*  2
	* <br>
	*  3
	* <br>
	*  4
	* <br>
	*  5
	* <br>
	*  6
	* <br>
	* <br>
	* X and O should be in one of these column 1 3 5 7 9 11 13, for " " will be added to these columns.
	* <br> "|" will be added to other column
	* <br>
	* 2 * 0 + 1 = 1
	* <br>
	* 2 * 1 + 1 = 3
	* <br>
	* 2 * 2 + 1 = 5
	* <br>
	* 2 * 3 + 1 = 7
	* <br>
	* 2 * 4 + 1 = 9
	* <br>
	* 2 * 5 + 1 = 11
	* <br>
	* 2 * 6 + 1 = 13
	* <br>
	**/
	public String[][] createBoard()
	{
		for(int i = 0; i < board.length;i++)
		{
			for(int j = 0; j < board[i].length;j++)
			{
				if(j%2 == 0)
				{
					board[i][j] = "|";
				}
				else
				{
					board[i][j] = " ";
				}
				
				Integer x = 1;
				if(i == 6)
				{
					for(int n = 0; n < 15; n++)
					{
						if(n%2 == 0)
						{
							board[i][n] =" ";
						}
						else
						{
							board[i][n] = String.valueOf(x);
							x++;
						}
						
					}
					
				}
			}
		}
		return board;
	}
	/**
	*	This method is used to visualize board to the users.
	*	<br>
	*	@param board pass an initialized to board to method.
	*	<br><br><br>
	*	Brief Explanation: this class will be called through the entire game session, because once it is called, it will show the current state of the game board.
	*	<br>
	*	Example:
	*	<br> 
	*			Object object = new Object;
	*	<br>
	*			 board[][] = object.initizliedBoard;
	*	<br>		 
	*			object.printBoard(board)
	**/
	public void printBoard(String [][]board)
	{
		for(int i = 0; i < board.length;i++)
		{
			for(int j = 0; j < board[i].length;j++)
			{
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}

	/**
	 * method is intended to identify player
	 * <br>
	 * @param board pass an initialized to board to method.
	 * @param ply pass a string value that represented player
	 * @exception NumberFormatException for input value
	 * 
	 * **/
	public void player(String[][] board, String ply)
	{
		//obs();
		boolean check2 = true;
				String strturn;
				Scanner sc = new Scanner(System.in);
				//convert 1 2 3 4 5 6 to 1 3 5 7 9 11 13
				boolean checker = true;
				while(checker)
				{
					if(!check2)
					{
						System.out.println("Column is Full");
						check2 = true;
					}
					System.out.print("Player " + ply + " Turn, please enter [1-7]: ");
					strturn = sc.next();
					if(isNumeric(strturn))
					{
						int turn = 0;
						try
						{
							turn = Integer.valueOf(strturn);
						}
						catch(NumberFormatException e)
						{
							e.printStackTrace();
						}
						turn = turn - 1;
						if(turn >= 0 && turn < 7)
						{
							int convert = 2 * turn + 1;
							for(int i = 5; i >= 0;i--) 
							{
								if(board[i][convert] == " ")
								{
									board[i][convert] = ply;
									if(ply.equals(getPlayer1()))
										pieceOne = pieceOne - 1;
									else if(ply.equals(getPlayer2()))
										pieceTwo = pieceTwo - 1;
									//observation();
									checker = false;
									//observation();
									break;
								}
								check2 = false;
								if(!check2)
								{
									checker = true;
									
								}
							}
						}
						else
						{
							System.out.println("Out of Range, please choose another column.");
							checker = true;
						}
					}
					else
					{
						System.out.println("Please enter numerical value only");
						checker =true;
					}
				}
	}
	
	/**
	*	This is validate method, it used to determine the result of the game
	*	<br>
	*	@param board pass an initialized to board to method.
	*	<br>
	*	@return return board with current element.
	*	@exception NullPointerException null value
	**/
	public String validator_Win(String[][] board)
	{
		//determine winner
		//Check horizontal line
		
		// assume i = 5
		//
		//
		for(int i = 0; i < 6; i++)
		{
			for(int j = 0; j < 7; j+= 2)
			{
				try
				{
				if((board[i][j+1] != " ")&&
						(board[i][j+3]!= " ")&& 
						(board[i][j+5] != " ")&& 
						(board[i][j+7] != " ")&& 
						(
							(board[i][j+1] == board[i][j+3]) &&
							(board[i][j+3] == board[i][j+5]) &&
							(board[i][j+5] == board[i][j+7])
						)
				  )
				{
					//System.out.println("Match Found");
					return board[i][j+1];
				} 
				}
				catch(NullPointerException e)
				{
					e.printStackTrace();
				}
						
			}
		}
		//check vertical line
		//1 3 5 7 9 11 13 
		for(int i = 1; i < 15; i+= 2)
		{
			for(int j = 0; j < 3; j++)
			{
				try
				{
				//[0][1]
				if((board[j][i] != " ") &&
						(board[j+1][i] != " ") &&
						(board[j+2][i] != " ") &&
						(board[j+3][i] != " ") &&
						(
								(board[j][i] == board[j+1][i]) &&
								(board[j+1][i] == board[j+2][i]) &&
								(board[j+2][i] == board[j+3][i]) 
						)
				  )
				{
					//System.out.println("Match Found");
					return board[j][i];
				}
				}
				catch(NullPointerException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		
		//diganol line
		for (int i=0;i<3;i++)
	    {
	      for (int j=1;j<9;j+=2)
	      {
	    	  try
	    	  {
	            if((board[i][j] != " ") &&
	            		(board[i+1][j+2] != " ") &&
	            		(board[i+2][j+4] != " ") &&
	            		(board[i+3][j+6] != " ") &&
	            		(
	            				(board[i][j] == board[i+1][j+2]) &&
	            				(board[i+1][j+2] == board[i+2][j+4]) &&
	            				(board[i+2][j+4] == board[i+3][j+6])
	            		)
	               )
	            {
	            	//System.out.println("Match Found");
	            	return board[i][j];  
	            }
	    	  }
	    	  catch(NullPointerException e)
	    	  {
	    		  e.printStackTrace();
	    	  }
	      }  
	    }
		for (int i=0;i<3;i++)
	    {
	      for (int j=7;j<15;j+=2)
	      {
	    	  try
	    	  {
	            if((board[i][j] != " ") &&
	            		(board[i+1][j-2] != " ") &&
	            		(board[i+2][j-4] != " ") &&
	            		(board[i+3][j-6] != " ") &&
	            		(
	            				(board[i][j] == board[i+1][j-2]) &&
	            				(board[i+1][j-2] == board[i+2][j-4]) &&
	            				(board[i+2][j-4] == board[i+3][j-6])
	            		)
	              )
	            {
	            	//System.out.println("Match Found");
	            	return board[i][j];  
	            }
	    	  }
	    	  catch(NullPointerException e)
	    	  {
	    		  e.printStackTrace();
	    	  }
	              
	      }  
	    }
		return null;
	}
	
	/**
	 *	This boolean method is intended to identity input
	 *  <br>
	 *  If input is anything except number then notify user.
	 *  
	 *  @return true or false, only true if input is number
	 *  @param str String
	 **/
	public boolean isNumeric(String str) 
	{
        if (str == null || str.length() == 0) 
        {
            return false;
        }
        for (char c : str.toCharArray()) 
        {
            if (!Character.isDigit(c)) 
            {
                return false;
            }
        }
        return true;

    }
	
	/**
	 *	This void method is intended to clear the board when a match is finish
	 *  <br>
	 *  Detail: it set index on to board back to " "
	 * 
	 **/
	public void clearBoard()
	{
		for(int i = 0; i < board.length-1; i++)
		{
			for(int j = 1; j < 15; j+= 2)
			{
				board[i][j] = " ";	
				//System.out.println(board[i][j]);
			}
		}
	}
	/**
	*	This method return the current Piece of player1
	*	<br>
	*	@return current number of pieceOne
	**/
	public int getPieceOne()
	{
		return pieceOne;
	}
	/**
	*	This method return the current Piece of player2
	*	<br>
	*	@return current number of pieceTwo
	**/
	public int getPieceTwo()
	{
		return pieceTwo;
	}
	/**
	 *	This method return player1
	 *	@return player1 
	 * 
	 **/
	public String getPlayer1() {
		return player1;
	}
	/**
	 *	This method return player1
	 *	@return player2 
	 * 
	 **/
	public String getPlayer2() {
		return player2;
	}
	
}