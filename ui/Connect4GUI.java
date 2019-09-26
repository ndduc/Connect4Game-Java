package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import core.Connect4Client;
import core.Connect4Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/**
 * @author: Duc Nguyen
 * @version: 2.01
 * date: 09-16-19
 * **/

/**
 * Connect4GUI is crucial to GUI representation and functionalities
 * <br>
 * Components will be built in this class
 * <br>
 * Necessary action will also be invoked in this class
 * 
 * Update-2.01 - 09-23-2019
 * 		added - online option to menu pane
 * 		added - new online pane which show contain 3 option Host and Play Game and Exit
 * 		added - close method for online pane
 * */
public class Connect4GUI extends Application
{
	/**
	 * Brief explanation for all variable
	 * <br>
	 * Row and column indicate row as Y and column as X for gridpane structure.
	 * <br>
	 * Size indicates overall size of grid structure and its child components.
	 * <br> 
	 * Piece1 and piece2 indicate the player turn
	 * <br>
	 * Player1 indicates the first player such true is player 1 and false is player 2
	 * <br>
	 * computer indicates computer player such if it is true then the system will player computer player
	 * <br>
	 * gird_pane is simple a grid panel where element will be represented
	 * <br>
	 * reset come to play when user choose to play again
	 * 
	 * **/
	private int row = 6;
	private int col = 7;
	private int size = 80;
	
	private int piece1 = 21;
	private int piece2 = 21;
	boolean player1 = false; //GREEN = player 1
	boolean computer = false;
	
	private Stage stage;
	private Stage stageGame, stageServer, stageClient;
	private Scene scene;
	private Pane pane_main;
	private GridPane [][] grid_pane;//; //game board
	
	private Button btnGeneric1, btnGeneric2, btnGeneric3, btnGeneric4;
	private Label lblResult;
	
	private boolean reset = false;
	private String valid = "";
	
	Connect4Server server  = new Connect4Server();
	boolean checker;
	
	/**
	 * Constructor Method
	 * <br>
	 * Initialize main components
	 * 
	 * **/
	public Connect4GUI()
	{
		pane_main = new Pane();
		//grid_pane = new GridPane[getCol()][getRow()];
		grid_pane = new GridPane[getRow()][getCol()];
	}
	
	/**
	 * This method extends GridPane with Circle object
	 * <br>
	 * These circle will be indicate as player's piece
	 * <br>
	 * 2 Type of circles which are Green circle and Red circle
	 * **/
	public class GridPane extends Circle
	{
		boolean player;
		/**
 		 * Method determine whether player object is red or green
 		 * <br>
		 *
		 * @param player boolean if true then object is Red, else object is green
		 **/
		public GridPane(boolean player)
		{
			
			super(size/2, player? Color.RED : Color.GREEN);
			this.player = player;
			//X and Y with size/2 to assure that object is placed in the right place
			setCenterX(size/2);
			setCenterY(size/2);
		}
	}
	
	/**
	 * Method is intended to be the main menu of this program
	 * <br>
	 * 3 options will be visible for user to choose.
	 * <br> 
	 * Player can choose to play against another player or a computer player
	 * <br>
	 * The third option is to switch to terminal mode
	 * <br>
	 * @param stage setup the current stage
	 * 
	 **/
	public void setMenu(Stage stage)
	{
		
		stage = new Stage();
		this.stage = stage;
		this.stage.setTitle("Menu");
		Label lblPromp1 = new Label("Welcome Player");
		Label lblPromp = new Label("Please, choose your game mode");
		btnGeneric1 = new Button("2 Players Mode");
		btnGeneric1.setOnMouseClicked(e ->
		{
			this.stage.close();
			computer = false;
			lchGame(this.stage);
		});
		
		btnGeneric2 = new Button("1 Player Mode");
		btnGeneric2.setOnMouseClicked(e ->{
			
			this.stage.close();
			computer = true;
			lchGame(this.stage);
			//Logic here
		});
		
		btnGeneric3 = new Button("Terminal Mode");
		btnGeneric3.setOnMouseClicked(e -> {
			this.stage.close();
			computer = false;
			Connect4TextConsole.main();
		});
		
		btnGeneric4 = new Button("Online Mode");
		btnGeneric4.setOnMouseClicked(e -> {
			this.stage.close();
			lchOnline(this.stage);
			
		});
		
		//organize button on frame
		//set up panel
		VBox pnlSetMenu = new VBox(10);
		pnlSetMenu.getChildren().addAll(lblPromp1,lblPromp, btnGeneric1, btnGeneric2, btnGeneric3, btnGeneric4);
		pnlSetMenu.setAlignment(Pos.CENTER);
		this.stage.setOnCloseRequest(e -> {
			e.consume();
			close();
		});
		this.scene = new Scene(pnlSetMenu,400,400);
		this.stage.setResizable(false);
		this.stage.setScene(this.scene);
		this.stage.show();
	}
	
	/**
	 * Method is immediately launch when player choose 1 player mode or 2 players mode
	 * <br>
	 * This method simply is Stage which represent components
	 *  <br>
	 *  @param stage setup the current stage
	 *  @return stage
	 **/
	public Stage lchGame(Stage stage)
	{
		//testing with new Object
		this.stageGame = stage;
		this.stageGame = new Stage();
		this.stageGame.setTitle("Game Panel");
		this.stageGame.setOnCloseRequest(e -> {
			e.consume();
			close();
		});
		this.stageGame.setScene(new Scene(initBoard(), 640,700));
		this.stageGame.getScene().getWindow();
		this.stageGame.setResizable(false);
		this.stageGame.show();
		return this.stageGame;
	}
	
	/**
	 * Method launch Online pane, allow player to choose host or play as client
	 * @param stage Stage, set up stage
	 * */
	public void lchOnline(Stage stage)
	{
		this.stage = stage;
		this.stage = new Stage();
		this.stage.setTitle("Online Menu");
		
		//server = new Connect4Server();
	//	HBox hbox  =new HBox(10);
		checker = false;
		Label lblPromp1 = new Label("Welcome to online section");
		Label lblPromp = new Label("If you like to host a server, please select Host button");
		Label lblPromp2 = new Label("If you like to join another host, please select Play button");
		VBox pnlSetMenu = new VBox(10);
		
		btnGeneric1 = new Button("Host Game");
		btnGeneric1.setOnMouseClicked(e ->
		{
			checker = true;
			this.stageServer = new Stage();
			//server.start(this.stageServer);
			//hbox.setPrefSize(700, prefHeight);
			
			pnlSetMenu.getChildren().add(server.initScroll());
		});
		
		btnGeneric2 = new Button("Play Game");
		btnGeneric2.setOnMouseClicked(e ->{
			if(server.checkConnection())
			{
				Connect4Client client = new Connect4Client();
				this.stageClient = new Stage();
				client.start(stageClient);
				//Logic here
			}
			else
			{
				onlineAlert("Notice: No existing host found, unable to start game");
				//System.out.println("No Host Found");
			}
		});
		
		btnGeneric3 = new Button("Exit Online Section");
		btnGeneric3.setOnMouseClicked(e -> {
			closeOnline();
			/*
			if(checker)
			{
				checker = false;
				closeOnline();
			}
			else if(checker = false)
			{
				this.stage.close();	
				setMenu(this.stage);
			}*/
		});
		
		pnlSetMenu.getChildren().addAll(lblPromp1,lblPromp, lblPromp2, btnGeneric1, btnGeneric2, btnGeneric3);
		pnlSetMenu.setAlignment(Pos.CENTER);
		
		//hbox.getChildren().addAll(pnlSetMenu);
		//hbox.setAlignment(Pos.CENTER);
		this.stage.setOnCloseRequest(e -> {
			e.consume();
			close();
		});
		
		
		this.scene = new Scene(pnlSetMenu,400,450);
		this.stage.setScene(this.scene);
		this.stage.setResizable(false);
		this.stage.show();
		
	}
	
	/**
	 * method allows system to completely shutdown lchOnine and any associated link
	 * */
	public void closeOnline()
	{
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Warning: closing this window will terminate current host status");
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        a.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> opt = a.showAndWait();
        if (opt.get() == yes) 
        {
			if(server.closeConnection())
			{
				this.stage.close();	
				setMenu(this.stage);
			}
			else
			{
				this.stage.close();	
				setMenu(this.stage);
			}
        } 
	}
	
	/**
	 * method prompts a notification when user choose to play while there isn't any hosts exist in the system
	 * @param info a string value that represents information on the alert pane
	 * */
	public void onlineAlert(String info)
	{
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText(info);
        ButtonType yes = new ButtonType("Confirm");
        a.getButtonTypes().setAll(yes);
        Optional<ButtonType> opt = a.showAndWait();
        if (opt.get() == yes) 
        {
			a.close();
        } 
	}
	
	/**
	 * This method is triggered at the same time with lchGame method
	 * <br>
	 * Main purpose of this method is to initialize game board, and have elements in it correct location
	 * <br>
	 * Additional, this method is also initialize pieces when user click on the grid pane
	 * <br>
	 * Additional, method have 2 different pane, Pane and VBox.
	 * <br>
	 * VBox is the main pane which store everything. Pane is to store Grid.
	 * <br>
	 * @return VBox pnl_root
	 * **/
	public Parent initBoard()
	{
		
		Label piece1 = new Label();
		Label piece2 = new Label();
		//test.setEditable(false);
		//System.out...println("Check Piece: " + getPiece1());
		Pane pane = new Pane();
		VBox pnl_Root = new VBox(10);
		
		//Check player piece
		pnl_Root.setOnMouseClicked(e ->
		{
			piece1.setText( "Player 1's Piece: " + String.valueOf(getPiece1()));
			piece2.setText( "Player 2's Piece: " + String.valueOf(getPiece2()));
		});
		
		pane.getChildren().add(this.pane_main);
		//Shape shape = initGrid();
		Shape gridShape = new Rectangle((getCol() + 1) * getSize(), (getRow() + 1) * getSize());
		//create hole on grid
		//Error __ BUG +1 and -1 to solve the bug -- Solved
		for(int i = 0; i < getRow(); i++) //Row Vertical --  Y
			for(int j = 0; j < getCol(); j++) //Column Horizontal -- X
			{ 
				Circle c = new Circle(getSize() / 2);
				c.setCenterX(getSize()/2);
				c.setCenterY(getSize()/2);
				c.setTranslateX(j * (getSize() + 6) + getSize() / 4);
				c.setTranslateY(i * (getSize() + 6) + getSize() / 4);
				gridShape = Shape.subtract(gridShape, c);
			}
		//Explanation:
		//only gridShape is added to pane
		//pane is added to VBox pnl_Root, other elements are also added to pnl_Root
		pane.getChildren().add(gridShape);
		pane.getChildren().addAll(initPiece());
		pnl_Root.getChildren().add(piece1);
		pnl_Root.getChildren().add(piece2);
		pnl_Root.getChildren().add(pane);
		return pnl_Root;
	}
	/**
	 * This method is where pieces are initialized
	 * <br>
	 * For visualize purpose, a transparent rectangle shape will visible when players move their mouse pointer to designated areas.
	 * <br>
	 * When mouse is clicked, placePiece will be triggered (a piece will be place to grid)
	 * <br>
	 * @return ArrayList tempList
	 **/
	private List<Rectangle> initPiece() 
	{
      ArrayList<Rectangle> tempList = new ArrayList<>();

      for (int i = 0; i < getCol(); i++) 
      {
    	  //Create transparent object
          Rectangle r = new Rectangle(getSize(), (getRow()+ 1) * getSize());
          r.setTranslateX(i * (getSize() + 6) + getSize()/ 3);
          r.setFill(Color.TRANSPARENT);
          int x = i; //Selected Column
          //Mouse click listener
          r.setOnMouseClicked(e -> 
          {
        	  
              	if(computer)
              	{
              		placePiece(new GridPane(player1), x);
              		placePiecePc(new GridPane(player1));
              	}
              	else
              	{
              		placePiece(new GridPane(player1), x);
              	}
              	
          });
          tempList.add(r);
      }
      return tempList;

	}
	
	/**
	 * This is optional method, which return true when GridPane contain value
	 * <br>
	 * @param col selected column
	 * @param row selected row
	 * @return Optional element in grid pane
	 * **/
	public Optional<GridPane> getPiece(int col, int row)
	{
		if(col < 0 || col >= getCol() || row < 0 || row >= getRow())
			return Optional.empty();
		//if exist something then return 
		return Optional.ofNullable(grid_pane[row][col]);
		
	}
	
	
	/**
	 * THis method is intended to place piece on the grid 
	 * <br>
	 * @param grid indicate game board
	 * @param col selected column
	 * @exception ArrayIndexOutOfBoundsException Exception
	 * 
	 **/
	public void placePiece(GridPane grid, int col)
	{
		try
		{
			int row = getRow() - 1;
			while(row >= 0)
			{
				if(!getPiece(col, row).isPresent())
				{
					break;
				}
				row--;
			}
			if(row < 0) 
				{
				//System.out...println("Reached 0");
				alert();
				return;
				}
			grid_pane[row][col] = grid;
			pane_main.getChildren().add(grid);
			grid.setTranslateX(col * (getSize() + 6) + getSize() / 4);
			grid.setTranslateY(row * (getSize() + 6) + getSize() / 4);
			
			 winValidator2(grid_pane);
			/**
			 * 		0	1	2	3	4	5	6
			 * 0 
			 * 
			 * 1
			 * 
			 * 2
			 * 
			 * 3
			 * 
			 * 5
			 * 
			 * 
			 * Red = 	0xff0000ff
			 * Green = 	0x008000ff
			 * **/
			//switch player
			
			if(drawValidator())
			{
				gameDiaglog();
				//System.out..println("DRAW");
			}
			//if(winValidator(col, row))
			if(getValid() != "")
			{
				gameDiaglog();
				this.valid = "";
				//pane_main.getChildren().removeAll();
				//System.out..println("WIN");
			}
			
			if(player1)
			{
				setPiece1(piece1 - 1);
				//System.out..println("piece 1: " + getPiece1());
			}
			else if(!player1)
			{
				setPiece2(piece2 - 1); 
				//System.out..println("piece 2: " + getPiece1());
			}
			//checker = false;
			player1 = !player1;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//e.printStackTrace();
		}
	}
	
	/**
	 * This method is similar to placePiece, except doesn't not include parameter column
	 * <br>
	 * Because column will be generated randomly by the system
	 * <br>
	 * @param grid indicate game board
	 * 
	 * 
	 * **/
	public void placePiecePc(GridPane grid)
	{
		int row = getRow() - 1;
		Random random = new Random();
		int col = random.nextInt(7);
		while(row >= 0)
		{
			if(!getPiece(col, row).isPresent())
			{
				break;
			}
			row--;
		}
		if(row < 0)
		{
			//System.out...println("Reached 0");
			//alert();
			return;
		}
		grid_pane[row][col] = grid;
		pane_main.getChildren().add(grid);
		grid.setTranslateX(col * (getSize() + 6) + getSize() / 4);
		grid.setTranslateY(row * (getSize() + 6) + getSize() / 4);
		winValidator2(grid_pane);
		if(drawValidator())
		{
			gameDiaglog();
			//System.out...println("DRAW");
		}
		//if(winValidator(col, row))
		
		if(player1)
		{
			setPiece1(piece1 - 1);
			//System.out...println("piece 1: " + getPiece1());
		}
		else if(!player1)
		{
			setPiece2(piece2 - 1);
			//System.out...println("piece 2: " + getPiece1());
		}
		if(getValid() != "")
		{
			this.valid= "";
			gameDiaglog();
			//System.out...println("WIN");
		}
		player1 = !player1;
	}
	/**
	 * Method identify the winner
	 * <br>
	 * Applied the same technique used in console validate
	 * <br>
	 * 
	 * @param grid_pane indicate gameboard
	 * @return valid indicate such if string contain something then there exist a winner
	 * @exception ArrayIndexOutOfBoundsException Exception, such some winner case is near the edge of the grid which always cause an exception for the program
	 * **/
	public String winValidator2(GridPane[][] grid_pane)
	{
		String hz1 = null, hz2= null, hz3 = null, hz4 = null,
				vz1 = null, vz2 = null, vz3 = null, vz4 = null,
				diL1 = null, diL2= null, diL3 = null, diL4 = null,
				diR1 = null, diR2 = null, diR3 = null , diR4 = null;
		//ystem.out.println("Hit valid");
		
		//Following login determine winning condition
		//In this order Horizontal -> vertical -> DiRight -> DiLeft
		//The same logic from console is used
		//Try catch are intended to catch NullPointerException
		//After catch the finally block will perform conditional with hard-code value
		for(int i =0; i < getRow();i++)
		{
			for(int j = 0; j <= getCol()-1; j++)
			{
				try
				{
					
					if(grid_pane[i][j] != null && grid_pane[i][j+1] != null 
							&& grid_pane[i][j+2]!= null&& grid_pane[i][j+3] != null)
					{
						 hz1 = grid_pane[i][j].toString();
						 hz2 = grid_pane[i][j+1].toString();
						 hz3 = grid_pane[i][j+2].toString();
						 hz4 = grid_pane[i][j+3].toString();
						if(hz1.equals(hz2) && hz2.equals(hz3) && hz3.equals(hz4))
						{
							//System.out...println("Horizontal 1 Hit Found");
							this.valid = "hz";
							return valid;
						}
					}
				}catch(ArrayIndexOutOfBoundsException e) {}
				finally
				{
					if(grid_pane[i][6] != null && grid_pane[i][5] != null 
							&& grid_pane[i][4]!= null&& grid_pane[i][3] != null)
					{
						 hz1 = grid_pane[i][6].toString();
						 hz2 = grid_pane[i][5].toString();
						 hz3 = grid_pane[i][4].toString();
						 hz4 = grid_pane[i][3].toString();
						if(hz1.equals(hz2) && hz2.equals(hz3) && hz3.equals(hz4))
						{
							//System.out...println("Horizontal 1 Hit Found");
							this.valid = "hz";
							return valid;
						}
					}
				}
			}
		}
		
		for(int i =0; i < getRow();i++)
		{
			for(int j = 0; j <= getCol()-1; j++)
			{
				try
				{
					if(grid_pane[i][j] != null && grid_pane[i-1][j] != null 
							&& grid_pane[i-2][j]!= null&& grid_pane[i-3][j] != null)
					{
						 vz1= grid_pane[i][j].toString();
						 vz2 = grid_pane[i-1][j].toString();
						 vz3 = grid_pane[i-2][j].toString();
						 vz4 = grid_pane[i-3][j].toString();
						if(vz1.equals(vz2) && vz2.equals(vz3) && vz3.equals(vz4))
						{
							//System.out...println("Vertical 1 Hit Found");
							this.valid = "vz";
							return valid;
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {}
				finally {
					if(grid_pane[0][j] != null && grid_pane[1][j] != null 
							&& grid_pane[2][j]!= null&& grid_pane[3][j] != null)
					{
						 vz1= grid_pane[0][j].toString();
						 vz2 = grid_pane[1][j].toString();
						 vz3 = grid_pane[2][j].toString();
						 vz4 = grid_pane[3][j].toString();
						if(vz1.equals(vz2) && vz2.equals(vz3) && vz3.equals(vz4))
						{
							//System.out...println("Vertical 1 Hit Found");
							this.valid = "vz";
							return valid;
						}
					}
				}
			}
		}
		
		for(int i =0; i < getRow();i++)
		{
			for(int j = 0; j <= getCol()-1; j++)
			{
				try
				{
					if(grid_pane[i][j] != null && grid_pane[i-1][j+1] != null 
							&& grid_pane[i-2][j+2]!= null&& grid_pane[i-3][j+3] != null)
					{
						diR1 = grid_pane[i][j].toString();
						 diR2 = grid_pane[i-1][j+1].toString();
						 diR3 = grid_pane[i-2][j+2].toString();
						 diR4 = grid_pane[i-3][j+3].toString();
						if(diR1.equals(diR2) && diR2.equals(diR3) && diR3.equals(diR4))
						{
							//System.out...println("Dia Right 1 Hit Found");
							this.valid = "dr";
							return valid;
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {} 
				finally
				{
					if(grid_pane[0][6] != null && grid_pane[1][5] != null 
							&& grid_pane[2][4]!= null&& grid_pane[3][3] != null)
					{
						//System.out..println("Hit 1st Cond");
						diR1 = grid_pane[0][6].toString();
						 diR2 = grid_pane[1][5].toString();
						 diR3 = grid_pane[2][4].toString();
						 diR4 = grid_pane[3][3].toString();
						if(diR1.equals(diR2) && diR2.equals(diR3) && diR3.equals(diR4))
						{
							//System.out...println("Dia Right 1 Hit Found");
							this.valid = "dr";
							return valid;
						}
					}
					
					if(grid_pane[1][6] != null && grid_pane[2][5] != null 
							&& grid_pane[3][4]!= null&& grid_pane[4][3] != null)
					{
						//System.out..println("Hit 2nd Cond");
						diR1 = grid_pane[1][6].toString();
						 diR2 = grid_pane[2][5].toString();
						 diR3 = grid_pane[3][4].toString();
						 diR4 = grid_pane[4][3].toString();
						if(diR1.equals(diR2) && diR2.equals(diR3) && diR3.equals(diR4))
						{
							//System.out...println("Dia Right 1 Hit Found");
							this.valid = "dr";
							return valid;
						}
					}
					
					if(grid_pane[2][6] != null && grid_pane[3][5] != null 
							&& grid_pane[4][4]!= null&& grid_pane[5][3] != null)
					{
						//System.out..println("Hit 3rd Cond");
						 diR1 = grid_pane[2][6].toString();
						 diR2 = grid_pane[3][5].toString();
						 diR3 = grid_pane[4][4].toString();
						 diR4 = grid_pane[5][3].toString();	 
						if(diR1.equals(diR2) && diR2.equals(diR3) && diR3.equals(diR4))
						{
							//System.out...println("Dia Right 1 Hit Found");
							this.valid = "dr";
							return valid;
						}
					}
				}
			}
		}
		
		for(int i =0; i < getRow();i++)
		{
			for(int j = 0; j <= getCol()-1; j++)
			{
				try
				{
					if(grid_pane[i][j] != null && grid_pane[i-1][j-1] != null 
							&& grid_pane[i-2][j-2]!= null&& grid_pane[i-3][j-3] != null)
					{
						diL1 = grid_pane[i][j].toString();
						 diL2 = grid_pane[i-1][j-1].toString();
						 diL3 = grid_pane[i-2][j-2].toString();
						 diL4 = grid_pane[i-3][j-3].toString();
						if(diL1.equals(diL2) && diL2.equals(diL3) && diL3.equals(diL4))
						{
							//System.out...println("Dia Left 1 Hit Found");
							this.valid = "dl";
							return valid;
						}
					}
				}
				catch(Exception e) {}
				finally {
					if(grid_pane[5][0] != null && grid_pane[4][1] != null 
							&& grid_pane[3][2]!= null&& grid_pane[2][3] != null)
					{
						diL1 = grid_pane[5][0].toString();
						 diL2 = grid_pane[4][1].toString();
						 diL3 = grid_pane[3][2].toString();
						 diL4 = grid_pane[2][3].toString();
						if(diL1.equals(diL2) && diL2.equals(diL3) && diL3.equals(diL4))
						{
							//System.out...println("Dia Left 1 Hit Found");
							this.valid = "dl";
							return valid;
						}
					}
					
					if(grid_pane[4][0] != null && grid_pane[3][1] != null 
							&& grid_pane[2][2]!= null&& grid_pane[1][3] != null)
					{
						diL1 = grid_pane[4][0].toString();
						 diL2 = grid_pane[3][1].toString();
						 diL3 = grid_pane[2][2].toString();
						 diL4 = grid_pane[1][3].toString();
						if(diL1.equals(diL2) && diL2.equals(diL3) && diL3.equals(diL4))
						{
							//System.out...println("Dia Left 1 Hit Found");
							this.valid = "dl";
							return valid;
						}
					}
					
					if(grid_pane[3][0] != null && grid_pane[2][1] != null 
							&& grid_pane[1][2]!= null&& grid_pane[0][3] != null)
					{
						diL1 = grid_pane[3][0].toString();
						 diL2 = grid_pane[2][1].toString();
						 diL3 = grid_pane[1][2].toString();
						 diL4 = grid_pane[0][3].toString();
						if(diL1.equals(diL2) && diL2.equals(diL3) && diL3.equals(diL4))
						{
							//System.out...println("Dia Left 1 Hit Found");
							this.valid = "dl";
							return valid;
						}
					}
				}
			}
		}
		this.valid = "";
		return valid;
	}

	
	/**
	 * Method validate draw condition
	 * @return boolean return true if game is determine as draw
	 * 
	 * **/
	public boolean drawValidator()
	{
		if(getPiece1() == 0 || getPiece2() == 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * This method is meant to show after the game is concluded
	 *<br>
	 *Then player can either choose to exit the program or continue with another game
	 * 
	 * 
	 * **/
	public void gameDiaglog()
	{
		this.stage.setTitle("Gameover");
		btnGeneric1 = new Button("Try Again");
		btnGeneric2 = new Button("Exit");
		btnGeneric1.setOnAction(e ->
		{
			/**
			 * This indicate when Try Again button is clicked then
			 * The entire program will reset and run as complete new Stage
			 * **/
		//	lchGame(getStageGame()).close();
			stageGame.close();
			this.stage.close();
			Platform.runLater(()-> new Connect4GUI().setMenu(new Stage()));
		});
		btnGeneric2.setOnAction(e -> {
			/**
			 * This indicates when Exit button is clicked then the program will exit
			 * **/
			System.exit(0);
		});
		VBox pnl_VBox = new VBox(8);
		
		//Following login announce who is the winner or when the match is draw
		//The announcement will placed on VBox pane
		if(getPiece1() == 0 || getPiece2() == 0)
		{
			lblResult = new Label("Match Draw!");
			setPiece1(21);
			setPiece2(21);
			reset = true;
		}		
		else if(player1)
		{
			lblResult = new Label("Red Player Win");
			setPiece1(21);
			setPiece2(21);
			reset = true;
		}
		else
		{
			lblResult = new Label("Green Player Win");
			setPiece1(21);
			setPiece2(21);
			reset = true;
		}
		
		pnl_VBox.getChildren().addAll(lblResult, btnGeneric1, btnGeneric2);
		pnl_VBox.setAlignment(Pos.CENTER);
		this.scene = new Scene(pnl_VBox, 400,400);
		this.stage.setScene(this.scene);
		this.stage.show();
	}
	/**
	 * This method will handle close() operation
	 * <br>
	 * It intended to tell user whether if the user want to close the application
	 * **/
	public void close()
	{
		/**
		 * This event occur when user click on close symbol on window frame
		 * 
		 * **/
		Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("Would you like to exit program now?");
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        a.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> opt = a.showAndWait();
        if (opt.get() == yes) 
        {
            Platform.exit();
            System.exit(0);
        } else if (opt.get() == no) 
        {
            a.close();
        }
	}
	
	
	/**
	 * Method give an alert when player enter into a column that does have enough room
	 * **/
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
	 * application start method
	 * 
	 * @param arg0 indicate stage
	 * 
	 * **/
	@Override
	public void start(Stage arg0){
		// TODO Auto-generated method stub
			setMenu(arg0);
	}
	/**
	 * application main start method
	 * 
	 * 
	 * @param args this is main method, where everything launch
	 * **/
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * System generate method getter and setter
	 * **/
	/**
	 * this is a getRow method
	 * @return row
	 * **/
	public int getRow() {
		return row;
	}
	/**
	 * this is a getCol method
	 * @return col
	 * **/
	public int getCol() {
		return col;
	}
	/**
	 * this is a getSize method
	 * @return size
	 * **/
	public int getSize() {
		return size;
	}
	/**
	 * this is a getPiece 1method
	 * @return piece1
	 * **/
	public int getPiece1() {
		return piece1;
	}
	/**
	 * this is a getPiece2 method
	 * @return piece2
	 * **/
	public int getPiece2() {
		return piece2;
	}

	/**
	 * this is a setPiece1 method
	 * @param piece1 indicate piece
	 * **/
	public void setPiece1(int piece1) {
		this.piece1 = piece1;
	}
	/**
	 * this is a setPiece2 method
	 * 
	 * @param piece2 indicates piece
	 * **/
	public void setPiece2(int piece2) {
		this.piece2 = piece2;
	}

	/**
	 * this is a boolean reset method
	 * <br>
	 * if reset is true then everything is suppose to reset
	 * @return boolean reset
	 * **/
	public boolean isReset() {
		return reset;
	}

	/**
	 * this is boolean computer player method
	 * <br>
	 * computer is true then computer player will be activated
	 * 
	 * @return boolean computer
	 * **/
	public boolean isComputer() {
		return computer;
	}

	/**
	 * this is setComputer method
	 * @param computer indicate whether if game in player vs computer mode
	 * **/
	public void setComputer(boolean computer) {
		this.computer = computer;
	}
	/**
	 * Method is intended to return valid
	 * <br>
	 * Intended to support the validator method
	 * 
	 * @return string valid value
	 * **/
	public String getValid()
	{
		return valid;
	}

	/**
	 * Method return stage of current, intended to help close game pane when try again
	 * @return stageGame
	 * **/
	public Stage getStageGame() {
		return stageGame;
	}
	
	
	
}
