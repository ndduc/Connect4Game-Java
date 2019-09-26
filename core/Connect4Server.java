package core;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import ui.Connect4GUI.GridPane;

/**
 * @author: Duc Nguyen
 * @version: 1.00
 * date: 09-23-19
 * **/


/**
 * Connect4Server
 * <br>
 * Connect4Server represent server view.
 * <br>
 * Showing server establishment status, and player ready status
 * <br>
 * Connect4Server extends with Application which is crucial for JavaFx implementation
 * <br>
 * Connect4Server implements Connect4Constant which contain some common constant variable that will be share among other classes
 * <br>
 * */
public class Connect4Server extends Application implements Connect4Constants 
{
	private int sessionNo = 1; // Number a session
	int tmpRow = 0;
	int tmpCol = 0;
	//indicate number of Green pieces and red pieces on board
	int countXGrn = 0;
	int countORed = 0;
	
	Thread thread;
	TextArea taLog;
	ServerSocket serverSocket;
	//boolean threadChecker;
	  /**
	   * Emply start method
	   * <br>
	   * @param primaryStage take variable as Stage
	   * **/
	@Override // Override the start method in the Application class
	public void  start(Stage primaryStage) 
	{}
	
	/**
	 * method allows system to initialize a scrollpane which contain crucial components and functionalities for server class
	 * @return scroll pane
	 * */
	public ScrollPane initScroll()
	{
		//textarea will show necessary information when host is online
		taLog = new TextArea();
		taLog.setEditable(false);
		//creating thread
		initThread().start();
		ScrollPane scroll = new ScrollPane(taLog);
		return scroll;
		
	}
	
	/**
	 * method allows system to initialize thread
	 * */
	private Thread initThread()
	{
		this.thread = new Thread( () -> 
		{
			try 
			{
				// Create a server socket
				serverSocket = new ServerSocket(8004);
				Platform.runLater(() -> taLog.appendText(new Date() +
						": Server started at socket 8004\n"));
				// Ready to create a session for every two players
				while (true) 
				{
					Platform.runLater(() -> taLog.appendText(new Date() +
							": Wait for players to join session " + sessionNo + '\n'));
					// Connect to player 1
					Socket player1 = serverSocket.accept();
					Platform.runLater(() -> 
					{
						taLog.appendText(new Date() + ": Green Player joined session " + sessionNo + '\n');
						taLog.appendText("Green Player's IP address" +
								player1.getInetAddress().getHostAddress() + '\n');
					});
					// Notify that the player is Player 1
					new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);
					// Connect to player 2
					Socket player2 = serverSocket.accept();
					Platform.runLater(() -> 
					{
						taLog.appendText(new Date() +": Red Player joined session " + sessionNo + '\n');
						taLog.appendText("Red Player's IP address" +
								player2.getInetAddress().getHostAddress() + '\n');
					});
					// Notify that the player is Player 2
					new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);
					// Display this session and increment session number
					Platform.runLater(() -> 
									taLog.appendText(new Date() + ": Start a thread for session " + sessionNo++ + '\n'));
					
					
					// Launch a new thread for this session of two players
					new Thread(new HandleASession(player1, player2)).start();
					
					
					
				
				}
			}
			catch(IOException ex) 
			{
				//ex.printStackTrace();
			}
			
		});
		return this.thread;
	}
	/**
	 * method check for current host connection.
	 * <br>
	 * return false when current hosts are closed
	 * 
	 * @return boolean value
	 * */
	public boolean checkConnection()
	{
		try
		{
			if(!this.serverSocket.isClosed())
				return true;
			else
				return false;
		}
		catch(NullPointerException e)
		{
			
		}
		finally {};
		return false;
	}
	/**
	 * method allows system to completely shutdown existing threads and Socket
	 * @return boolean 
	 * */
	@SuppressWarnings("finally")
	public boolean closeConnection()
	{
		try
		{
			this.thread = null;
			if(!serverSocket.isClosed())
				this.serverSocket.close();
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			
		}
		finally {return false;}
		
		
	}
	
	
	/**
	 * This method handle several crucial network functionalities
	 * <br>
	 * Receive and Send date to client
	 * <br>
	 * 
	 * */
	// Define the thread class for handling a new session for two players
	class HandleASession implements Runnable, Connect4Constants 
	{
		private Socket player1;
		private Socket player2;
  
		// Create and initialize cells
		private char[][] cell =  new char[ROW][COL];
		private DataInputStream fromPlayer1;
		private DataOutputStream toPlayer1;
		private DataInputStream fromPlayer2;
		private DataOutputStream toPlayer2;
  
		// Continue to play
		//private boolean continueToPlay = true;
  
		/**
		 * HandleSession construction
		 * @param player1 Socket
		 * @param player2 Socket
		 * */
		public HandleASession(Socket player1, Socket player2) 
		{
			this.player1 = player1;
			this.player2 = player2;
  
			// Initialize cells
			for (int i = 0; i < ROW; i++)
				for (int j = 0; j < COL; j++)
					cell[i][j] = ' ';
		}
  
		/**Method Implement the run() method for the thread 
		 * <br>
		 * invoke winValidator to determine the game result
		 * 
		 * @exception IOException 
		 * */
		public void run() 
		{
			try 
			{
				// Create data input and output streams
				fromPlayer1 = new DataInputStream(player1.getInputStream());
				toPlayer1 = new DataOutputStream(player1.getOutputStream());
				fromPlayer2 = new DataInputStream(player2.getInputStream());
				toPlayer2 = new DataOutputStream(player2.getOutputStream());
				// Write anything to notify player 1 to start
				// This is just to let player 1 know to start
				toPlayer1.writeInt(1);
  
				// Continuously serve the players and determine and report
				// the game status to the players
				while (true) 
				{
					
					//String text = fromPlayer1.readUTF();
					// Receive a move from player 1
					//  int row = fromPlayer1.readInt();
					int column = fromPlayer1.readInt();
  
					for(int i = cell.length - 1; i >= 0; i--)
					{
						System.out.println("Test: " + cell[i][column]);
						if(cell[i][column] == ' ')
						{
							cell[i][column] = 'X';
							tmpRow = i;
							break;
						}
					}
					// Check if Player 1 wins
					if (winValidator2('X')) 
					{
						toPlayer1.writeInt(PLAYER1_WON);
						toPlayer2.writeInt(PLAYER1_WON);
						sendMove(toPlayer2, tmpRow, column);
						break; // Break the loop
					}
					else if (isDraw()) 
					{ // Check if all cells are filled
						toPlayer1.writeInt(DRAW);
						toPlayer2.writeInt(DRAW);
						sendMove(toPlayer2, tmpRow, column);
						break;
					}
					else 
					{
						// Notify player 2 to take the turn
						toPlayer2.writeInt(CONTINUE);
						// Send player 1's selected row and column to player 2
						sendMove(toPlayer2, tmpRow, column);
					}
					// Receive a move from Player 2
					//  row = fromPlayer2.readInt();
					column = fromPlayer2.readInt();				
					for(int i = 5; i >= 0; i--)
					{
						//  System.out.println("Test: " + cell[i][column]);
						if(cell[i][column] == ' ')
						{
							cell[i][column] = 'O';
							tmpRow = i;
							break;
						}
					}
					// Check if Player 2 wins
					if (winValidator2('O')) 
					{
						toPlayer1.writeInt(PLAYER2_WON);
						toPlayer2.writeInt(PLAYER2_WON);
						sendMove(toPlayer1, tmpRow, column);
						break;
					}
					else 
					{
						// Notify player 1 to take the turn
						toPlayer1.writeInt(CONTINUE);
						// Send player 2's selected row and column to player 1
						sendMove(toPlayer1, tmpRow, column);
					}
				}
			}
			catch(IOException ex) 
			{
				ex.printStackTrace();
			}
		}
  
		/**Method send the move to other player 
		 * @param out DataOutputStream
		 * @param row int selected row
		 * @param column int selected column
		 * 
		 * @exception IOException
		 * */
		private void sendMove(DataOutputStream out, int row, int column)throws IOException 
		{
			out.writeInt(row); // Send row index
			out.writeInt(column); // Send column index
		}
		
		/**
		 * Method determine when game is resulted in drawn
		 * */
		private boolean isDraw()
		{
			countXGrn = 0;
			countORed = 0;
			for (int i = 0; i < ROW; i++)
				for (int j = 0; j < COL; j++)
				{
					if (cell[i][j] == 'X')
						countXGrn++;
					else if(cell[i][j] == 'O')
						countORed++;
      
					//Draw when number of pieces equal to constant value which is 21
					if(countXGrn == PIECE || countORed == PIECE)
					{
						return true;
					}
				}
			return false;
		}
		

		
		/**Method determine if the player with the specified token wins
		 * @param token char
		 * */
		public Boolean winValidator2(char token)
		{
			char hz1 = ' ', hz2= ' ', hz3 = ' ', hz4 = ' ',
					vz1 = ' ', vz2 = ' ', vz3 = ' ', vz4 = ' ',
					diL1 = ' ', diL2= ' ', diL3 = ' ', diL4 = ' ',
					diR1 = ' ', diR2 = ' ', diR3 = ' ', diR4 = ' ';
			//ystem.out.println("Hit valid");
			//Following login determine winning condition
			//In this order Horizontal -> vertical -> DiRight -> DiLeft
			//The same logic from console is used
			//Try catch are intended to catch NullPointerException
			//After catch the finally block will perform conditional with hard-code value
			for(int i =0; i < ROW;i++)
			{
				for(int j = 0; j <= COL-1; j++)
				{
					try
					{
			
						if(cell[i][j] != ' '&& cell[i][j+1] != ' ' 
								&& cell[i][j+2]!= ' '&& cell[i][j+3] != ' ')
						{
							hz1 = cell[i][j];
							hz2 = cell[i][j+1];
							hz3 = cell[i][j+2];
							hz4 = cell[i][j+3];
							if(hz1 == hz2 && hz2 == hz3 && hz3 == hz4)
							{
								//System.out...println("Horizontal 1 Hit Found");
								return true;
							}
						}
					}catch(ArrayIndexOutOfBoundsException e) {}
					finally
					{
						if(cell[i][6] != ' ' && cell[i][5] != ' ' 
								&& cell[i][4]!= ' '&& cell[i][3] != ' ')
						{
							hz1 = cell[i][6];
							hz2 = cell[i][5];
							hz3 = cell[i][4];
							hz4 = cell[i][3];
							if(hz1 == (hz2) && hz2 == (hz3) && hz3 == (hz4))
							{
								//System.out...println("Horizontal 1 Hit Found");
								return true;
							}
						}
					}
				}
			}

			for(int i =0; i < ROW;i++)
			{
				for(int j = 0; j <= COL-1; j++)
				{
					try
					{
						if(cell[i][j] != ' ' && cell[i-1][j] != ' ' 
								&& cell[i-2][j]!= ' '&& cell[i-3][j] != ' ')
						{
							vz1= cell[i][j];
							vz2 = cell[i-1][j];
							vz3 = cell[i-2][j];
							vz4 = cell[i-3][j];
							if(vz1 == (vz2) && vz2 == (vz3) && vz3 == (vz4))
							{
								//System.out...println("Vertical 1 Hit Found");
								return true;
							}
						}
					}
					catch(ArrayIndexOutOfBoundsException e) {}
					finally {
						if(cell[0][j] != ' ' && cell[1][j] != ' ' 
								&& cell[2][j]!= ' '&& cell[3][j] != ' ')
						{
							vz1= cell[0][j];
							vz2 = cell[1][j];
							vz3 = cell[2][j];
							vz4 = cell[3][j];
							if(vz1 == (vz2) && vz2 == (vz3) && vz3 == (vz4))
							{
								//System.out...println("Vertical 1 Hit Found");
								return true;
							}
						}
					}
				}
			}
			
			for(int i =0; i < ROW;i++)
			{
				for(int j = 0; j <= COL-1; j++)
				{
					try
					{
						if(cell[i][j] != ' ' && cell[i-1][j+1] != ' ' 
								&& cell[i-2][j+2]!= ' '&& cell[i-3][j+3] != ' ')
						{
							diR1 = cell[i][j];
							diR2 = cell[i-1][j+1];
							diR3 = cell[i-2][j+2];
							diR4 = cell[i-3][j+3];
							if(diR1 == (diR2) && diR2 == (diR3) && diR3 == (diR4))
							{
								//System.out...println("Dia Right 1 Hit Found");
								return true;
							}
						}
					}
					catch(ArrayIndexOutOfBoundsException e) {} 
					finally
					{
						if(cell[0][6] != ' ' && cell[1][5] != ' ' 
								&& cell[2][4]!= ' '&& cell[3][3] != ' ')
						{
							//System.out..println("Hit 1st Cond");
							diR1 = cell[0][6];
							diR2 = cell[1][5];
							diR3 = cell[2][4];
							diR4 = cell[3][3];
							if(diR1 == (diR2) && diR2 == (diR3) && diR3 == (diR4))
							{
								//System.out...println("Dia Right 1 Hit Found");
								return true;
							}
						}

						if(cell[1][6] != ' ' && cell[2][5] != ' ' 
								&& cell[3][4]!= ' '&& cell[4][3] != ' ')
						{
							//System.out..println("Hit 2nd Cond");
							diR1 = cell[1][6];
							diR2 = cell[2][5];
							diR3 = cell[3][4];
							diR4 = cell[4][3];
							if(diR1 == (diR2) && diR2 == (diR3) && diR3 == (diR4))
							{
								//System.out...println("Dia Right 1 Hit Found");
								return true;
							}
						}

						if(cell[2][6] != ' ' && cell[3][5] != ' ' 
								&& cell[4][4]!= ' '&& cell[5][3] != ' ')
						{
							//System.out..println("Hit 3rd Cond");
							diR1 = cell[2][6];
							diR2 = cell[3][5];
							diR3 = cell[4][4];
							diR4 = cell[5][3];	 
							if(diR1 == (diR2) && diR2 == (diR3) && diR3 == (diR4))
							{
								//System.out...println("Dia Right 1 Hit Found");
								return true;
							}
						}
					}
				}
			}

			for(int i =0; i < ROW;i++)
			{
				for(int j = 0; j <= COL-1; j++)
				{
					try
					{
						if(cell[i][j] != ' ' && cell[i-1][j-1] != ' ' 
								&& cell[i-2][j-2]!= ' '&& cell[i-3][j-3] != ' ')
						{
							diL1 = cell[i][j];
							diL2 = cell[i-1][j-1];
							diL3 = cell[i-2][j-2];
							diL4 = cell[i-3][j-3];
							if(diL1 == (diL2) && diL2 == (diL3) && diL3 == (diL4))
							{
								//System.out...println("Dia Left 1 Hit Found");
								return true;
							}
						}
					}
					catch(Exception e) {}
					finally {
						if(cell[5][0] != ' ' && cell[4][1] != ' ' 
								&& cell[3][2]!= ' '&& cell[2][3] != ' ')
						{
							diL1 = cell[5][0];
							diL2 = cell[4][1];
							diL3 = cell[3][2];
							diL4 = cell[2][3];
							if(diL1 == (diL2) && diL2 == (diL3) && diL3 == (diL4))
							{
								//System.out...println("Dia Left 1 Hit Found");
								return true;
							}
						}	

						if(cell[4][0] != ' ' && cell[3][1] != ' ' 
								&& cell[2][2]!= ' '&& cell[1][3] != ' ')
						{
							diL1 = cell[4][0];
							diL2 = cell[3][1];
							diL3 = cell[2][2];
							diL4 = cell[1][3];
							if(diL1 == (diL2) && diL2 == (diL3) && diL3 == (diL4))
							{
								//System.out...println("Dia Left 1 Hit Found");
								return true;
							}
						}

						if(cell[3][0] != ' ' && cell[2][1] != ' ' 
								&& cell[1][2]!= ' '&& cell[0][3] != ' ')
						{
							diL1 = cell[3][0];
							diL2 = cell[2][1];
							diL3 = cell[1][2];
							diL4 = cell[0][3];
							if(diL1 == (diL2) && diL2 == (diL3) && diL3 == (diL4))
							{
								//System.out...println("Dia Left 1 Hit Found");
								return true;
							}
						}
					}
				}
			}
			return false;
		}
	}

}