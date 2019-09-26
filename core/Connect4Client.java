package core;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import ui.Connect4GUI;
/**
 * @author: Duc Nguyen
 * @version: 1.00
 * date: 09-23-19
 * **/

/**
 * Connect4Client
 * <br>
 * Connect4Client represent player view.
 * <br>
 * when player make their, data will be sent to server and other player will receive the sent data.
 * <br>
 * Connect4Client extends with Application which is crucial for JavaFx implementation
 * <br>
 * Connect4Client implements Connect4Constant which contain some common constant variable that will be share among other classes
 * <br>
 * */
public class Connect4Client extends Application implements Connect4Constants 
{
	  // Indicate whether the player has the turn
	  private boolean myTurn = false;
	  // Indicate the token for the player
	  private char myToken = ' ';
	  // Indicate the token for the other player
	  private char otherToken = ' ';
	  // Create and initialize cells
	  private Cell[][] cell =  new Cell[ROW][COL];
	  // Create and initialize a title label
	  private Label lblTitle = new Label();
	  // Create and initialize a status label
	  private TextArea lblStatus = new TextArea();
	  // Indicate selected row and column by the current move
	  @SuppressWarnings("unused")
	  private int rowSelected;
	  private int columnSelected;
	  // Input and output streams from/to server
	  private DataInputStream fromServer;
	  private DataOutputStream toServer;
	  // Continue to play?
	  private boolean continueToPlay = true;
	  // Wait for the player to mark a cell
	  private boolean waiting = true;
	  @SuppressWarnings("unused")
	  private boolean checkConnection = false;
	  // Host name or ip
	  private String host = "localhost";
	  
	  // indicate current game s
	  private Stage gameStage;
	  private Stage primaryStage;
	  // indicate number of green piece on board
	  private int pieceXGrn = 0;
	  // indicate number of red piece on board
	  private int pieceORed = 0;

	  

 
  /**
   * Method allow game to be launched
   * <br>
   * As well as create components and have them places in the appropriate location on pane.
   * 
   * <br>
   * @param primaryStage take variable as Stage
   * **/
	  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) 
  {
		  this.primaryStage = primaryStage;
	  // Pane to hold cell
	  GridPane pane = new GridPane(); 
	  // pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
	  for (int i = 0; i < ROW; i++)
		  for (int j = 0; j < COL; j++)
			  pane.add(cell[i][j] = new Cell(i, j), j, i);

	  BorderPane border = new BorderPane();
	  VBox vbox = new VBox();
	  //vbox.getChildren().add(lblTitle);
	  vbox.getChildren().add(pane);
	  //vbox.getChildren().add(lblStatus);

	  lblStatus.setEditable(false);
	  lblStatus.setPrefSize(100, 200);
	  lblStatus.setWrapText(true);
	  VBox vboxSub = new VBox(10);
	  //These following textfield is intended to show the number of piece of each player by each move
	  TextField lblPieceX = new TextField("Green Piece: " + String.valueOf(getPieceXGrn()));
	  lblPieceX.setEditable(false);
	  TextField lblPieceO = new TextField("Red Piece: " + String.valueOf(getPieceORed()));
	  lblPieceO.setEditable(false);
	  vboxSub.getChildren().addAll(lblTitle, lblPieceX, lblPieceO, lblStatus);


	  //number of pieces is calculate base on Mouse Click Event
	  border.setOnMouseClicked(e -> {
		  lblPieceX.clear();
		  lblPieceX.setText("Green Piece: " + String.valueOf(getPieceXGrn()));
		  lblPieceO.clear();
		  lblPieceO.setText("Red Piece: " + String.valueOf(getPieceORed()));
	  });
	  border.setCenter(vbox);
	  border.setLeft(vboxSub);
	  // Create a scene and place it in the stage
	  Scene scene = new Scene(border, 1000, 700);
	  this.primaryStage.setOnCloseRequest(e-> {
		  e.consume();
		  close();
	  });
	  this.primaryStage.setResizable(false);
	  this.primaryStage.setTitle("Connect4 Client"); // Set the stage title
	  this.primaryStage.setScene(scene); // Place the scene in the stage
	  this.primaryStage.show(); // Display the stage   

	  gameStage =  this.primaryStage;
	  // Connect to the server
	  connectToServer();
  }
  
  /**
   *	This method allow system connect to server.
   *<br>
   *	Once the connection is establish, player can able to view and retrieve their opponent view and movement
   *<br>
   *	Server is connection via either local host or host ip address along with port number
   *<br>
   *
   **/
  private void connectToServer() 
  {
	    try 
	    {
	    	// Create a socket to connect to the server
	    	Socket socket = new Socket(host, 8004);
	    	// Create an input stream to receive data from the server
	    	fromServer = new DataInputStream(socket.getInputStream());
	    	// Create an output stream to send data to the server
	    	toServer = new DataOutputStream(socket.getOutputStream());
	    }
	    catch (Exception ex) 
	    {
	    	ex.printStackTrace();
	    }
	    // Control the game on a separate thread
	    new Thread(() -> 
	    {
	    	try 
	    	{
	    		// Get notification from the server
	    		int player = fromServer.readInt();
	    		// Am I player 1 or 2?
	    		if (player == PLAYER1) 
	    		{
	    			myToken = 'X';
	    			otherToken = 'O';
	    			Platform.runLater(() -> {
	    				lblTitle.setText("Player Green");
	    				lblStatus.setText("Waiting for player 2 to join");
	    			});
  
	    			// Receive startup notification from the server
	    			fromServer.readInt(); // Whatever read is ignored
	    			Platform.runLater(() -> {
	    				lblStatus.setText("Player 2 has joined. I start first");
	    				checkConnection = true;
	    			});
      
	    			// It is my turn
	    			myTurn = true;
	    		}
	    		else if (player == PLAYER2) 
	    		{
	    			myToken = 'O';
	    			otherToken = 'X';
	    			Platform.runLater(() -> 
	    			{
	    				lblTitle.setText("Player Red");
	    				lblStatus.setText("Waiting for player 1 to move");
	    				checkConnection = true;
	    			});
	    		}
	    		
  
	    		// Continue to play
	    		/*
	    		 * void wait ->
	    		 * void sendMove -> 
	    		 * void receive 
	    		 **/
	    		while (continueToPlay) 
	    		{      
	    			if (player == PLAYER1) 
	    			{
	    				waitForPlayerAction(); // Wait for player 1 to move
	    				sendMove(); // Send the move to the server
	    				receiveInfoFromServer(); // Receive info from the server
	    			}
	    			else if (player == PLAYER2) 
	    			{
	    				receiveInfoFromServer(); // Receive info from the server
	    				waitForPlayerAction(); // Wait for player 2 to move
	    				sendMove(); // Send player 2's move to the server
	    			}
	    		}
	    		

	    	}
	    	catch (Exception ex) 
	    	{
	    		ex.printStackTrace();
	    	}
	    }).start();
  	}

  /**
   * Method allow when player finish his/her move to wait for the opponent to finish thier move
   * @exception InterruptedException exception that is crucial for Thread.sleep
   * */
  private void waitForPlayerAction() throws InterruptedException 
  {
	  while (waiting) 
	  {
		  Thread.sleep(100);
	  }

	  waiting = true;
  }

  /**
   * Method send column that selected by player to server
   * <br>
   * 
   * **/
  private void sendMove() throws IOException 
  {
	   //toServer.writeInt(rowSelected); // Send the selected row
	  toServer.writeInt(columnSelected); // Send the selected column
  }
  /**
   * Method allow current player to receive their game status from server
   * <br>
   * Basically, this method allow system to determine game result
   * 
   * @exception IOException 
   * */
  private void receiveInfoFromServer() throws IOException 
  {
	  // Receive game status
	  
	  int status = fromServer.readInt();

	  if (status == PLAYER1_WON) 
	  {
		  // Player 1 won, stop playing
		  continueToPlay = false;
		  if (myToken == 'X') 
		  {
			  Platform.runLater(() -> {lblStatus.setText("Green Player Won");
			  gameDialog("X");});
		  }
		  else if (myToken == 'O') 
		  {
			  Platform.runLater(() -> 
			  {
				  gameDialog("X");
				  lblStatus.setText("Green Play have won!");});
			  receiveMove(); 
		  }
	  }
	  else if (status == PLAYER2_WON) 
	  {
		  // Player 2 won, stop playing
		  continueToPlay = false;
		  if (myToken == 'O') 
		  {
			  Platform.runLater(() -> 
			  {
				  lblStatus.setText("Red Player Won");
				  gameDialog("O");
			  });
		  }
		  else if (myToken == 'X') 
		  {
			  Platform.runLater(() ->
			  { 
				  lblStatus.setText("Red Player have won");
				  gameDialog("O");
			  });
			  receiveMove();
		  }
	  }
	  else if (status == DRAW) 
	  {
		  // No winner, game is over
		  continueToPlay = false;
		  Platform.runLater(() -> 
		  {
			  lblStatus.setText("Game is over, no winner!");
			  gameDialog("XO");
		  });
		  if (myToken == 'O') 
		  {
			  receiveMove();
		  }
	  }
	  else 
	  {
		  receiveMove();
		  Platform.runLater(() -> lblStatus.setText("Your Turn, Please select column"));
		  myTurn = true; // It is my turn
	  }
  	}
  /**
   * Method allow current player to receive their component move from server
   * <br>
   * Basically, this method allow system to update player view
   * 
   * @exception IOException 
   * */
  private void receiveMove() throws IOException 
  {
	  // Get the other player's move
	  int row = fromServer.readInt();
	  int column = fromServer.readInt();
	  Platform.runLater(() -> cell[row][column].setToken(otherToken));
  }

  /**
   * Following method and nested method are to create and stylize the game board
   * <br>
   * class Cell extends Pane, because cell (table like structure) will be represented on Pane panel
   * */
  public class Cell extends Pane 
  {
	  // Indicate the row and column of this cell in the board
	  private int row;
	  private int column;
	  // Token used for this cell
	  private char token = ' ';
	  /**
	   * Nested Cell construction
	   * 
	   * @param row selected row
	   * @param column seletect column
	   * */
	  public Cell(int row, int column) 
	  {
		  this.row = row;
		  this.column = column;
		  this.setPrefSize(150, 150); // What happens without this?
		  //Set both border and background of Pane to Black
		  this.setStyle("-fx-border-color: black;"
				  	+ "-fx-background-color: black;"); // Set cell's border
		  //There will an appropriate action when player click on this pane
		  this.setOnMouseClicked(e -> handleMouseClick());
		  
		  //Create hole (circle) object on game board
		  for(int i = 0; i < ROW; i++)
		  {
			  for(int j = 0; j < COL; j++)
			  {
				  Circle c = new Circle();
				  c.setCenterX(60);
				  c.setCenterY(60);
				  c.setRadius(48);
				  c.setFill(javafx.scene.paint.Color.WHITE);
				  //r = 100 or -100
				  this.getChildren().add(c);
			  }
		  }
	
	  }

	  /**
	   * this method is to get current piece (token)
	   * @return token this indicate piece
	   * */
	  public char getToken() 
	  {
		  return token;
	  }

	  /**
	   * Method is to set new piece (token)
	   * @param c char value indicate player piece
	   * 
	   * */
	  public void setToken(char c) 
	  {
		  //System.out.println(getPieceXGrn());
		  token = c;
		  //once piece is chose, the system will create piece object which correct color on game board
		  repaint();
	  }

	  /**
	   * This method is intended to create Circle object with color GREEN or RED.
	   * <br>
	   * These objects are player pieces
	   * */
	  protected void repaint() 
	  {
		  Double div = 3.0;
		  if (token == 'X') 
		  {
			  Ellipse ellipse = new Ellipse(this.getWidth() / div, 
					  this.getHeight() / div, this.getWidth() / div - 10, 
					  this.getHeight() / div - 10);
			  ellipse.centerXProperty().bind(this.widthProperty().divide(2));
			  ellipse.centerYProperty().bind(this.heightProperty().divide(2));
			  ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));        
			  ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));   
			  ellipse.setStroke(Color.GREEN);
			  ellipse.setFill(Color.GREEN);
			  getChildren().add(ellipse); // Add the ellipse to the pane
		  }
		  else if (token == 'O') 
		  {
			  Ellipse ellipse = new Ellipse(this.getWidth() /div, 
					  this.getHeight() / div, this.getWidth() / div - 10, 
					  this.getHeight() / div - 10);
			  ellipse.centerXProperty().bind(this.widthProperty().divide(2));
			  ellipse.centerYProperty().bind(this.heightProperty().divide(2));
			  ellipse.radiusXProperty().bind(this.widthProperty().divide(2).subtract(10));        
			  ellipse.radiusYProperty().bind(this.heightProperty().divide(2).subtract(10));   
			  ellipse.setStroke(Color.RED);
			  ellipse.setFill(Color.RED);
			  getChildren().add(ellipse); // Add the ellipse to the pane
		  }
	  }

	  /**
	   * Handle Mouse Click event method
	   * <br>
	   * when player click on board, certain action will be invoked such as piece will be placed on selected column
	   * 
	   * 
	   */
	  private void handleMouseClick() 
	  {
		  // If cell is not occupied and the player has the turn	
		  if (myTurn) {
			  columnSelected = column;
			  for(int i = cell.length-1; i >= 0; i--)
			  {
				  if(i < 0)
				  {
					  System.out.println("HIT");
					  alert();
					  return;
				  }
				  if(cell[i][columnSelected].getToken() == ' ')
				  {
					 // System.out.println(column);
					 // System.out.println("Row: " + i);
					 // System.out.println("Checker;");
					  cell[i][columnSelected].setToken(myToken);  // Set the player's token in the cell
					  calPiece();
					  rowSelected = i;
					  lblStatus.setText("Waiting for the other player to move");
					  waiting = false; // Just completed a successful move
					  myTurn = false;
					  break;
				  }
			  }
		  }
	  }
  }

  /**
   * Helper method, give player a notification when their selected column is full
   * */
  public void alert()
  {
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
		a.setContentText("The column is full, please choose another column");
		ButtonType ok = new ButtonType("Ok");
		a.getButtonTypes().setAll(ok);
		Optional<ButtonType> opt = a.showAndWait();
		if(opt.get() == ok)
		{
			a.close();
		}
  }
  /**
   * Helper method, give player an option yes or no when they click on exit
   * */
  public void close()
  {
		/**
		 * This event occur when user click on close symbol on window frame
		 * 
		 * **/
	  Alert a = new Alert(Alert.AlertType.CONFIRMATION);
	  a.setContentText("Would you like to exit the current program now?");
	  ButtonType yes = new ButtonType("Yes");
	  ButtonType no = new ButtonType("No");
      a.getButtonTypes().setAll(yes, no);
      Optional<ButtonType> opt = a.showAndWait();
      if (opt.get() == yes) 
      {
          //Platform.exit();
    	  primaryStage.close();
    	 // Connect4GUI g = new Connect4GUI();
    	 // this.primaryStage = new Stage();
		//	g.start(primaryStage);
    	  
      } else if (opt.get() == no) 
      {
          a.close();
      }
	}
  
  /**
   * This method will pop up when the current game is finish
   * <br>
   * It will let the player know the game result
   * <br>
   * And allow players to choose the next action, Play again or Exit
   * 
   * @param winner String
   * */
  public void gameDialog(String winner)
  {
	  Stage stage = new Stage();
	  stage.setTitle("Gameover");
	  Button btnTry = new Button("Confirm");
	  //Button btnExit = new Button("Exit");
	  Label lblPrompt = new Label("Please click confirm to be directed to online menu.");
	  Label lblPrompt1 = new Label("Gameover");
	  btnTry.setOnAction(e -> 
	  {
		  stage.close();
		  gameStage.close();
		//  Platform.runLater(()-> new Connect4GUI().setMenu(new Stage()));
	  });
/*  
	  btnExit.setOnAction(e -> 
	  {
		  System.exit(0);
	  });*/
  
	  VBox vbox = new VBox(10);
	  Label res = new Label();
  
	  if(winner.equals("X"))
		  res.setText("Green Player is a winner");
	  else if(winner.equals("O"))
		  res.setText("Red Player is a winner");
	  else if(winner.equals("XO"))
		  res.setText("Game is draw");
	  
	  vbox.getChildren().addAll(lblPrompt1, res,lblPrompt, btnTry);
	  vbox.setAlignment(Pos.CENTER);
	  Scene scene = new Scene(vbox, 400,400);
	  stage.setScene(scene);
	  stage.show();  
  }
  /**
   * Method calculate number of piece on board
   * <br>
   * Red and green pieces will be counted separately
   * 
   * */
  public void calPiece()
  {
	  pieceXGrn = 0;
	  pieceORed = 0;
	  for(int i = 0; i < ROW; i++)
	  {
		  for(int j =0 ; j < COL; j++)
		  {
			  //Counting green
			  if(cell[i][j].getToken() == 'X')
			  {
				  pieceXGrn++;
			  }
			  //Counting red
			  else if(cell[i][j].getToken() == 'O')
			  {
				  pieceORed++;
			  }
		  }
	  }
  }

/**
 * System generated methods
 * */
  
  
  /**
   * get Green piece method
   * @return pieceXGreen
   * */
  public int getPieceXGrn() {
	return pieceXGrn;
  }

  /**
   * set Green piece method
   * @param pieceXGrn int
   * */

  public void setPieceXGrn(int pieceXGrn) {
	this.pieceXGrn = pieceXGrn;
  }

  /**
   * get Red piece method
   * @return pieceORed
   * */
  public int getPieceORed() {
	return pieceORed;
  }

  /**
   * set Red piece method
   * @param pieceORed int
   * */

  public void setPieceORed(int pieceORed) {
	this.pieceORed = pieceORed;
  }
  

}