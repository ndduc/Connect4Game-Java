package core;
import java.util.Random;
/**
 * @author: Duc Nguyen
 * @version: 2.00
 * date: 09-16-19
 * **/
/**
 * Class is intended to generate computer step
 * 
 * **/
public class Connect4ComputerPlayer extends Connect4{
	public String pc_player = "O";
	
	public int piecePC = 21;
	
	/**
	 * This child class of Connect4, thus it inherited Connect4's behaviors
	 * **/
	public Connect4ComputerPlayer()
	{
		super();
	}
	
	
	/**
	 * This oddGenerator class will generate random odd number between 0 to 15
	 * <br>
	 * For this is a method that determine computer player movement on [Column]
	 * 
	 * <br>
	 * @return return the the odd random number between (0-15).
	 * **/
	public int oddGenerator()
	{
		Random random = new Random();
		
		boolean checker = true;
		int i = 0;
		while(checker)
		{
			i = random.nextInt(15);
			if (i % 2 != 0) 
			{
				checker = false;
				return i;
			}
			
		}
		return i;
	}
	
	/**
	 * This method is similar to Player method in the parent class
	 * <br>
	 * The method allow computer to take random odd number from oddGenerator() as column number
	 * then from this method the system will randomly determine computer player's movement
	 * 
	 * <br>
	 * @param board String[][]
	 * **/
	public void computerMove(String[][] board)
	{
		//1 3 5 7 9 11 13
		boolean checker = true;
		while(checker)
		{
			///1 3 5 7 9 11 13
			System.out.println("Turn "+pc_player+" Turn");
			int gen = oddGenerator();
			for(int i = 5; i >= 0;i--)
			{
				if(board[i][gen] == " ")
				{
					board[i][gen] = pc_player;
					piecePC = piecePC - 1;
					checker = false;
					return;
				}
			}
		}
	}
	
	
	/**
	 * This method return number of pc pieces
	 * @return int piecePC
	 **/
	public int getPiecePC() {
		return piecePC;
	}

}
