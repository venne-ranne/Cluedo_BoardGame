package Interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import Environment.Board;
import Environment.Corridor;
import Environment.Position;
import Environment.RoomSquare;
import Environment.Square;
import Objects.Character;
import Objects.Room;
import Objects.Weapon;

public class CluedoBoardCanvas extends JPanel{

	private Board board;
	private final static int SQSIZE = 28;
	private final int numCols = 25;
	private final int numRows = 25;
	public final static int LEFT = 35;
	public final static int TOP = 25;
	private HashMap<Room, Position> drawingXY;
	protected static boolean startDraw;

	private final char[][] layout;
	private static String popupMsg = "";

	public CluedoBoardCanvas() {
		layout = new char[numRows][numCols];
		drawingXY = new HashMap<Room, Position>();
		startDraw = false;
	}

	/**
	 * Add the current board for GUI display.
	 * @param board the current board of the game
	 * @throws IOException exception for reading from a file
	 */
	public void addBoardCanvas(Board board) throws IOException{
		this.board = board;
		parsingBoardFile();
		setXYForItems();
		// the drawing position of characters in a room
		drawingXY = new HashMap<Room, Position>();
		drawingXY.put(board.getRoom("Kitchen"), new Position(2, 3));
		drawingXY.put(board.getRoom("Dining Room"), new Position(11, 4));
		drawingXY.put(board.getRoom("Lounge"), new Position(20, 4));
		drawingXY.put(board.getRoom("Ballroom"), new Position(3, 13));
		drawingXY.put(board.getRoom("Hall"), new Position(20, 12));
		drawingXY.put(board.getRoom("Conservatory"), new Position(2, 19));
		drawingXY.put(board.getRoom("Billiard Room"), new Position(9, 19));
		drawingXY.put(board.getRoom("Library"), new Position(15, 19));
		drawingXY.put(board.getRoom("Study"), new Position(22, 21));
	}

	/**
	 * Read information of the board from a file.
	 */
	public void parsingBoardFile(){
		try {
			String filename = "./src/board.txt";
			BufferedReader br = new BufferedReader(new FileReader(filename));
			ArrayList<String> lines = new ArrayList<String>();
			int width = -1;
			String line;

			while((line = br.readLine()) != null) {
				lines.add(line);

				// now sanity check
				if (width == -1) {
					width = line.length();
				} else if (width != line.length()) {
					throw new IllegalArgumentException("Input file \"" + filename + "\" is malformed; line " + lines.size() + " incorrect width.");
				}
			}
			br.close();
			for (int i = 0; i < lines.size(); i++){
				String str = lines.get(i);
				for (int j = 0; j < str.length(); j++){
					layout[i][j] = str.charAt(j);
				}
			}
		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Draw the current board.
	 * @param g Graphic object that uses for drawing
	 */
	public void paint(Graphics g) {
		if (!startDraw) return;
		g.setColor(Color.white);
		g.fillRect(0, 0, 800, 800);
		drawBoardGrid(g);

		// draw the rooms
		for (Room room : board.getAllRooms())
			room.draw(g, this);

		// draw the weapons
		for (Weapon weapon : board.getAllWeapons())
			weapon.draw(g, this);

		// draw logo
		Image logo = (new ImageIcon("src/images/logo.png")).getImage();
		g.drawImage(logo, LEFT+SQSIZE*10, TOP+10*SQSIZE, this);

		// print the index number for the board
		drawIndexGrid(g);

		// draw the black outline on the board
		g.setColor(Color.BLACK);
		((Graphics2D) g).setStroke(new BasicStroke(8));
		g.drawRect(LEFT, TOP, 700, 700);

		drawCharacters(g);
	}

	/**
	 * Draw the board square grid and the wall boarder around the rooms.
	 * @param g
	 */
	private void drawBoardGrid(Graphics g) {
		// draw the grid on the board
		for (int row = 0; row < numRows; row++){
			// print the index number for board
			g.setFont(new Font("Arial", Font.PLAIN, 9));
			String index = Integer.toString(row);
			int x = g.getFontMetrics().stringWidth(index);
			if (x == 5) x += 10;        // to align the index number
			g.setColor(Color.BLACK);
			g.drawString(index, x+2, TOP+18+row*SQSIZE);

			// draw the square for grass, corridors, rooms background
			for (int col = 0; col < numCols; col++){
				((Graphics2D) g).setStroke(new BasicStroke(1));
				if (layout[row][col] == 'C'){
					g.setColor(Color.orange);
					g.fillRect(LEFT+col*SQSIZE, TOP+row*SQSIZE, SQSIZE, SQSIZE);
					g.setColor(Color.BLACK);
					g.drawRect(LEFT+col*SQSIZE, TOP+row*SQSIZE, SQSIZE, SQSIZE);
				} else if (layout[row][col] == 'X'){
					g.drawImage((new ImageIcon("src/images/grass.png")).getImage(), LEFT+col*SQSIZE, TOP+row*SQSIZE, this);
				} else {
					g.setColor(new Color(255,250,240));
					g.fillRect(LEFT+col*SQSIZE, TOP+row*SQSIZE, SQSIZE, SQSIZE);
				}

				// draw border outlines for ROOM
				g.setColor(new Color(146, 27, 21));
				((Graphics2D) g).setStroke(new BasicStroke(5));
				if (layout[row][col] == '0' || layout[row][col] == '1' || layout[row][col] == '3')
					g.drawLine(LEFT+col*SQSIZE+2, TOP+row*SQSIZE, LEFT+col*SQSIZE+2, TOP+(row+1)*SQSIZE);              // LEFT side border
				if (layout[row][col] == '0' || layout[row][col] == '6' || layout[row][col] == '7')
					g.drawLine(LEFT+col*SQSIZE+2, TOP+(row+1)*SQSIZE-2, LEFT+(col+1)*SQSIZE-2, TOP+(row+1)*SQSIZE-2);  // bottom side border
				if (layout[row][col] == '2' || layout[row][col] == '3' || layout[row][col] == '4')
					g.drawLine(LEFT+col*SQSIZE+2, TOP+row*SQSIZE, LEFT+(col+1)*SQSIZE-2, TOP+row*SQSIZE);             // TOP border
				if (layout[row][col] == '4' || layout[row][col] == '6' || layout[row][col] == '5')
					g.drawLine(LEFT+(col+1)*SQSIZE-3, TOP+row*SQSIZE, LEFT+(col+1)*SQSIZE-3, TOP+(row+1)*SQSIZE);     // right side border
			}
		}
	}

	/**
	 * Draw the index number of column and rows on the board.
	 * @param g Graphic object that uses for drawing
	 */
	public void drawIndexGrid(Graphics g){
		// print the index number for the board
		for (int col = 0; col < numCols; col++){
			String index = Integer.toString(col);
			int x = g.getFontMetrics().stringWidth(index);
			if (x == 5) x += 10;        // to align the index number
			g.setColor(Color.BLACK);
			g.drawString(index, LEFT+col*SQSIZE+x, 15);
			g.drawString(index, LEFT+col*SQSIZE+x, TOP+18+25*SQSIZE);
			g.drawString(index, LEFT+25*SQSIZE+x, TOP+col*SQSIZE+18);
		}
	}

	/**
	 * Set the positions of room label, secret passage(if there is one) and weapon in a room.
	 */
	public void setXYForItems(){
		// rooms, secret passages and weapon position respectively
		board.getRoom("Lounge").setXY(LEFT+SQSIZE-15, TOP+SQSIZE*21, LEFT, TOP+24*SQSIZE, LEFT+SQSIZE, TOP+23*SQSIZE-3);
		board.getRoom("Kitchen").setXY(LEFT, TOP+SQSIZE*3, LEFT, TOP+SQSIZE, LEFT+SQSIZE, TOP+SQSIZE);
		board.getRoom("Dining Room").setXY(LEFT+SQSIZE, TOP+SQSIZE*12, 0, 0, LEFT, TOP+10*SQSIZE);
		board.getRoom("Ballroom").setXY(LEFT+SQSIZE*9, TOP+SQSIZE*4, 0, 0, LEFT+10*SQSIZE, TOP+2*SQSIZE);
		board.getRoom("Conservatory").setXY(LEFT+SQSIZE*18+15, TOP+SQSIZE*3-5, LEFT+SQSIZE*24, TOP+SQSIZE,LEFT+21*SQSIZE, TOP+2*SQSIZE);
		board.getRoom("Billiard Room").setXY(LEFT+SQSIZE*18+15, TOP+SQSIZE*10, 0, 0, LEFT+22*SQSIZE, TOP+8*SQSIZE+3);
		board.getRoom("Library").setXY(LEFT+SQSIZE*18, TOP+SQSIZE*16-10, 0, 0, LEFT+22*SQSIZE, TOP+14*SQSIZE+3);
		board.getRoom("Study").setXY(LEFT+SQSIZE*18, TOP+SQSIZE*22, LEFT+SQSIZE*24, TOP+24*SQSIZE, LEFT+18*SQSIZE, TOP+22*SQSIZE);
		board.getRoom("Hall").setXY(LEFT+SQSIZE*9, TOP+SQSIZE*21, 0, 0,  LEFT+9*SQSIZE, TOP+22*SQSIZE);
	}

	/**
	 * Draw the character on the current board.
	 * @param g Graphic object that uses for drawing
	 */
	public void drawCharacters(Graphics g){
		for (Character c : board.getAllCharacters()){
			Position pos = c.getPosition();
			Square sq = board.getSquareAt(pos);
			g.setColor(getCharacterColor(c));
			int x = 4;
			int y = 4;
			if (sq instanceof Corridor){
				x += LEFT + (pos.column()*SQSIZE);
				y += TOP + (pos.row()*SQSIZE);
			} else {
				Room room = board.getRoom(sq.getName());
				Position drawXY = drawingXY.get(room);
				if (c.getName().equals("Miss Scarlett")){
					x += LEFT + SQSIZE * drawXY.column();
					y += TOP + SQSIZE * drawXY.row();
				} else if (c.getName().equals("Mrs Peacock")){
					x += LEFT + SQSIZE * (drawXY.column()+1);
					y += TOP + SQSIZE * drawXY.row();
				} else if (c.getName().equals("Mrs White")){
					x += LEFT + SQSIZE * (drawXY.column());
					y += TOP + SQSIZE * (drawXY.row()+1);
				} else if (c.getName().equals("Colonel Mustard")){
					x += LEFT + SQSIZE * (drawXY.column()+1);
					y += TOP + SQSIZE * (drawXY.row()+1);
				} else if (c.getName().equals("Reverend Green")){
					x += LEFT + SQSIZE * (drawXY.column());
					y += TOP + SQSIZE * (drawXY.row()+2);
				} else{
					x += LEFT + SQSIZE * (drawXY.column()+1);
					y += TOP + SQSIZE * (drawXY.row()+2);
				}
			}
			g.fillOval(x, y, SQSIZE-8, SQSIZE-8);
		}
	}

	/**
	 * Returns the color that represents the character.
	 * @param c the Character object
	 * @return color that represents the given character
	 */
	public Color getCharacterColor(Character c){
		if (c.getName().equals("Miss Scarlett"))
			return Color.RED;
		else if (c.getName().equals("Mrs Peacock"))
			return Color.BLUE;
		else if (c.getName().equals("Mrs White"))
			return Color.WHITE;
		else if (c.getName().equals("Colonel Mustard"))
			return Color.yellow;
		else if (c.getName().equals("Reverend Green"))
			return new Color(0, 128, 0);
		else return new Color(75,0,130);
	}

	/**
	 * Display pop-up message when the mouse is hover over the board area.
	 * @param x the position of x on the board
	 * @param y the position of y on the board
	 */
	public void displayPopupMessage(int x, int y){

		// find the col and row on the board based on the given position of mouse
		int col = (x - LEFT)/ SQSIZE;
		int row = (y - TOP) / SQSIZE;

		// if the position is outside the board area
		if (col < 0 || row < 0 || col >= numCols || row >= numRows || x < LEFT || y < TOP) return;

		Square square = board.getSquareAt(new Position(row, col));
		if (square instanceof Corridor){
			Corridor c = (Corridor) square;
			if (c.hasPlayer())
				popupMsg = c.getCharacter().getName();
			else popupMsg = row + ", " + col;
		} else if (square instanceof RoomSquare){
			// if the hover area is in a room
			// display the room name, characters in the room
			// and the secret passage info if there is one
			RoomSquare sq = (RoomSquare) square;
			Room room = sq.getRoom();
			popupMsg = "<html>Room: " + room.getName() +"<br><br>Character(s):<br>";
			if (!room.getCharacters().isEmpty()){
				Set<Character> characters = room.getCharacters();
				for (Character c : characters)
					popupMsg += c.getName() +"<br>";
			} else popupMsg += "<br>";

			if (room.hasSecretPassage())
				popupMsg += "<br>This room has a secret passage to " + room.getSecretPassage().getName();
			popupMsg += "<br></html>";
		}
		this.setToolTipText(popupMsg);
	}

	/**
	 * To set the status of the current board whether to draw or not draw.
	 * @param turnOff draw or not draw
	 */
	public static void startDrawOn(Boolean turnOff){
		startDraw = turnOff;
	}
}
