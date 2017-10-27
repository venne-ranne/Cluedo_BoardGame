package Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.ImageIcon;

import Game.Game.InvalidAction;
import Objects.Card;
import Objects.Character;
import Objects.Player;
import Objects.Room;
import Objects.Weapon;

/**
 * Board class represents the board environment itself. It contains the
 * information of the boundaries, squares, rooms and characters as well as how
 * the move through the board. It initializes the board positions with codes of
 * what each square represents on the board (eg.'D' in the 2D array represents
 * the Dining room, 'X' is wall, '67' is corridor). It initializes the rooms and
 * their positions. The board is drawn for the first time when its created.
 */
public class Board {

	private Square[][] squares = new Square[25][25]; // this is the underlying data structure for a board
	private Room diningRoom, ballroom, kitchen, conservatory, billiardRoom, library, study, hall, lounge;
	private Set< Character> allCharacters;
	private Set< Weapon> allWeapons;
	private ArrayList<Card> dealCards;
	private Set<Card> solution;

	public enum Direction {
		NORTH, EAST, SOUTH, WEST;
	}

	/**
	 * Construct an initial board. Initialize rooms, weapons, characters, positions and 2D array.
	 * Adds the doorway between two squares and selects the solution cards randomly.
	 * @throws InvalidAction if the object is null.
	 */
	public Board() throws InvalidAction {
		dealCards = new ArrayList<Card>();
		solution = new HashSet<Card>();
		allCharacters = new HashSet<Character>();
		allWeapons = new HashSet<Weapon>();

		// constructs the characters
		Character scarlett = new Character("Miss Scarlett", new Position(24, 7), (new ImageIcon("src/images/CharacCard_MS.png")));
		Character mustard = new Character("Colonel Mustard", new Position(17, 0), (new ImageIcon("src/images/CharacCard_CM.png")));
		Character white = new Character("Mrs White", new Position(0, 9), (new ImageIcon("src/images/CharacCard_MW.png")));
		Character green = new Character("Reverend Green", new Position(0, 14), (new ImageIcon("src/images/CharacCard_RG.png")));
		Character peacock = new Character("Mrs Peacock", new Position(6, 24), (new ImageIcon("src/images/CharacCard_MP.png")));
		Character plum = new Character("Professor Plum", new Position(19, 24), (new ImageIcon("src/images/CharacCard_PP.png")));

		// constructs the rooms
		diningRoom = new Room("Dining Room", false, new ImageIcon("src/images/dining.png"));
		ballroom = new Room("Ballroom", false, new ImageIcon("src/images/ballroom.png"));
		kitchen = new Room("Kitchen", true, new ImageIcon("src/images/kitchen.png"));
		conservatory = new Room("Conservatory", true, new ImageIcon("src/images/conservatory1.png"));
		billiardRoom = new Room("Billiard Room", false, new ImageIcon("src/images/billiard.png"));
		library = new Room("Library", false, new ImageIcon("src/images/library.png"));
		study = new Room("Study", true, new ImageIcon("src/images/study.png"));
		hall = new Room("Hall", false, new ImageIcon("src/images/hall.png"));
		lounge = new Room("Lounge", true, new ImageIcon("src/images/lounge.png"));

		// constructs the weapons
		Weapon candleStick = new Weapon("Candlestick", ballroom, new ImageIcon("src/images/candlestick.png"));
		Weapon dagger = new Weapon("Dagger", diningRoom, (new ImageIcon("src/images/dagger.png")));
		Weapon leadPipe = new Weapon("LeadPipe", billiardRoom, (new ImageIcon("src/images/leadpipe.png")));
		Weapon revolver = new Weapon("Revolver", kitchen, (new ImageIcon("src/images/revolver.png")));
		Weapon rope = new Weapon("Rope", lounge, (new ImageIcon("src/images/rope.png")));
		Weapon spanner = new Weapon("Spanner", library, (new ImageIcon("src/images/spanner.png")));

		allCharacters.add(scarlett);
		allCharacters.add(mustard);
		allCharacters.add(white);
		allCharacters.add(green);
		allCharacters.add(peacock);
		allCharacters.add(plum);

		allWeapons.add(candleStick);
		allWeapons.add(dagger);
		allWeapons.add(leadPipe);
		allWeapons.add(revolver);
		allWeapons.add(rope);
		allWeapons.add(spanner);

		char[][] map = {
				{'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X',  67, 'X', 'X', 'X', 'X',  67, 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'},  //0
				{'K', 'K', 'K', 'K', 'K', 'K', 'X',  67,  67,  67, 'B', 'B', 'B', 'B',  67,  67,  67, 'X', 'G', 'G', 'G', 'G', 'G', 'G', 'X'},  //1
				{'K', 'K', 'K', 'K', 'K', 'K',  67,  67, 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B',  67,  67, 'G', 'G', 'G', 'G', 'G', 'G', 'X'},  //2
				{'K', 'K', 'K', 'K', 'K', 'K',  67,  67, 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B',  67,  67, 'G', 'G', 'G', 'G', 'G', 'G', 'X'},  //3
				{'K', 'K', 'K', 'K', 'K', 'K',  67,  67, 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B',  67,  67, 'G', 'G', 'G', 'G', 'G', 'G', 'X'},  //4
				{'K', 'K', 'K', 'K', 'K', 'K',  67,  67, 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B',  67,  67,  67, 'G', 'G', 'G', 'G', 'G', 'X'},  //5
				{'X', 'K', 'K', 'K', 'K', 'K',  67,  67, 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B',  67,  67,  67,  67,  67,  67,  67,  67,  67},  //6
				{ 67,  67,  67,  67,  67,  67,  67,  67, 'B', 'B', 'B', 'B', 'B', 'B', 'B', 'B',  67,  67,  67,  67,  67,  67,  67,  67, 'X'},  //7
				{'X',  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67, 'P', 'P', 'P', 'P', 'P', 'P', 'X'},	//8
				{'D', 'D', 'D', 'D', 'D',  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67, 'P', 'P', 'P', 'P', 'P', 'P', 'X'},	//9
				{'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',  67,  67, 'X', 'X', 'X', 'X', 'X',  67,  67,  67, 'P', 'P', 'P', 'P', 'P', 'P', 'X'},	//10
				{'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',  67,  67, 'X', 'X', 'X', 'X', 'X',  67,  67,  67, 'P', 'P', 'P', 'P', 'P', 'P', 'X'},	//11
				{'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',  67,  67, 'X', 'X', 'X', 'X', 'X',  67,  67,  67, 'P', 'P', 'P', 'P', 'P', 'P', 'X'},	//12
				{'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',  67,  67, 'X', 'X', 'X', 'X', 'X',  67,  67,  67,  67,  67,  67,  67,  67, 'X', 'X'},	//13
				{'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',  67,  67, 'X', 'X', 'X', 'X', 'X',  67,  67,  67, 'L', 'L', 'L', 'L', 'L', 'X', 'X'},	//14
				{'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D',  67,  67, 'X', 'X', 'X', 'X', 'X',  67,  67, 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'X'},  //15
				{'X',  67,  67,  67,  67,  67,  67,  67,  67,  67, 'X', 'X', 'X', 'X', 'X',  67,  67, 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'X'},  //16
				{ 67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67,  67, 'L', 'L', 'L', 'L', 'L', 'L', 'L', 'X'},	//17
				{'X',  67,  67,  67,  67,  67,  67,  67,  67, 'H', 'H', 'H', 'H', 'H', 'H',  67,  67,  67, 'L', 'L', 'L', 'L', 'L', 'X', 'X'},	//18
				{'O', 'O', 'O', 'O', 'O', 'O', 'O',  67,  67, 'H', 'H', 'H', 'H', 'H', 'H',  67,  67,  67,  67,  67,  67,  67,  67,  67,  67},	//19
				{'O', 'O', 'O', 'O', 'O', 'O', 'O',  67,  67, 'H', 'H', 'H', 'H', 'H', 'H',  67,  67,  67,  67,  67,  67,  67,  67,  67, 'X'},	//20
				{'O', 'O', 'O', 'O', 'O', 'O', 'O',  67,  67, 'H', 'H', 'H', 'H', 'H', 'H',  67,  67, 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'X'},	//21
				{'O', 'O', 'O', 'O', 'O', 'O', 'O',  67,  67, 'H', 'H', 'H', 'H', 'H', 'H',  67,  67, 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'X'},	//22
				{'O', 'O', 'O', 'O', 'O', 'O', 'O',  67,  67, 'H', 'H', 'H', 'H', 'H', 'H',  67,  67, 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'X'},	//23
				{'O', 'O', 'O', 'O', 'O', 'O', 'X',  67, 'X', 'H', 'H', 'H', 'H', 'H', 'H', 'X',  67, 'S', 'S', 'S', 'S', 'S', 'S', 'S', 'X'}};	//24


		// parsing the map and constructor squares
		// set up secret passages
		for (int row = 0; row < map.length; row++) {
			for (int col = 0; col < map.length; col++) {
				char code = map[row][col];
				RoomSquare room = null;
				Position pos = new Position(row, col);
				if (code == 'K') {
					squares[row][col] = new RoomSquare("Kitchen", pos, kitchen);
				} else if (code == 'D') {
					squares[row][col] = new RoomSquare("Dining Room", pos, diningRoom);
				} else if (code == 'O') {
					squares[row][col] = new RoomSquare("Lounge", pos, lounge);
				} else if (code == 'B') {
					squares[row][col] = new RoomSquare("Ballroom", pos, ballroom);
				} else if (code == 'H') {
					squares[row][col] = new RoomSquare("Hall", pos, hall);
				} else if (code == 'G') {
					room = new RoomSquare("Conservatory", pos, conservatory);
					squares[row][col] = room;
				} else if (code == 'P') {
					room = new RoomSquare("Billiard Room", pos, billiardRoom);
					squares[row][col] = room;
				} else if (code == 'L') {
					squares[row][col] = new RoomSquare("Library", pos, library);
				} else if (code == 'S') {
					squares[row][col] = new RoomSquare("Study", pos, study);
				} else if (code == 67) {
					Corridor corridor = new Corridor(pos);
					if (col == mustard.getPosition().column() && row == mustard.getPosition().row()) {
						corridor.setCharacter(mustard);
					} else if (col == white.getPosition().column() && row == white.getPosition().row()){
						corridor.setCharacter(white);
					} else if (col == green.getPosition().column() && row == green.getPosition().row()){
						corridor.setCharacter(green);
					} else if (col == peacock.getPosition().column() && row == peacock.getPosition().row()){
						corridor.setCharacter(peacock);
					} else if (col == scarlett.getPosition().column() && row == scarlett.getPosition().row()){
						corridor.setCharacter(scarlett);
					} else if (col == plum.getPosition().column() && row == plum.getPosition().row()){
						corridor.setCharacter(plum);
					}
					squares[row][col] = corridor;
				} else {
					squares[row][col] = new BlankSquare(pos);
				}
			}
		}

	/*	Image secret1 = (new ImageIcon("src/images/secret.png")).getImage();
		Image secret2 = (new ImageIcon("src/images/secretKitchen.png")).getImage();
		Image secret3 = (new ImageIcon("src/images/secretConservatory.png")).getImage();
		Image secret4 = (new ImageIcon("src/images/secretStudy.png")).getImage();*/

		// set up secret passages
		RoomSquare secretRoom = (RoomSquare) squares[1][0];
		kitchen.setSecretPassage(study, secretRoom, new ImageIcon("src/images/secretKitchen.png"));
		secretRoom = (RoomSquare) squares[24][23];
		study.setSecretPassage(kitchen, secretRoom, new ImageIcon("src/images/secretStudy.png"));
		secretRoom = (RoomSquare) squares[1][23];
		conservatory.setSecretPassage(lounge, secretRoom, new ImageIcon("src/images/secretConservatory.png"));
		secretRoom = (RoomSquare) squares[24][0];
		lounge.setSecretPassage(conservatory, secretRoom, new ImageIcon("src/images/secret.png"));

		// set up the doorway
		addDoorway(6, 4, 7, 4); // kitchen
		addDoorway(4, 18, 5, 18); // conservatory
		addDoorway(19, 6, 18, 6); // lounge
		addDoorway(21, 17, 20, 17); // study

		addDoorway(5, 8, 5, 7); // ballroom left door
		addDoorway(5, 15, 5, 16); // ballroom right door
		addDoorway(7, 9, 8, 9); // ballroom left door facing south
		addDoorway(7, 14, 8, 14); // ballroom right door facing south

		addDoorway(9, 18, 9, 17); // billiard room
		addDoorway(12, 22, 13, 22); // billiard room door 2
		addDoorway(16, 17, 16, 16); // library
		addDoorway(14, 20, 13, 20); // library door 2

		addDoorway(20, 14, 20, 15); // hall door on the right
		addDoorway(18, 12, 17, 12); // hall door on the right facing north
		addDoorway(18, 11, 17, 11); // hall door on the left acing north

		addDoorway(15, 6, 16, 6); // dining room door facing south
		addDoorway(12, 7, 12, 8); // dining room door facing west
		Random random = new Random();

		// create a deck of cards
		dealCards.add(scarlett);
		dealCards.add(mustard);
		dealCards.add(white);
		dealCards.add(green);
		dealCards.add(peacock);
		dealCards.add(plum);

		dealCards.add(candleStick);
		dealCards.add(dagger);
		dealCards.add(revolver);
		dealCards.add(leadPipe);
		dealCards.add(rope);
		dealCards.add(spanner);

		dealCards.add(diningRoom);
		dealCards.add(ballroom);
		dealCards.add(kitchen);
		dealCards.add(conservatory);
		dealCards.add(billiardRoom);
		dealCards.add(library);
		dealCards.add(study);
		dealCards.add(hall);
		dealCards.add(lounge);

		// set up the solution case
		solution.add(dealCards.get(random.nextInt(6)));
		solution.add(dealCards.get(random.nextInt(6) + 6));
		solution.add(dealCards.get(random.nextInt(6) + 12 + random.nextInt(4)));
	}

	/**
	 * Prints out the current board.
	 */
	public void printMap() {
		System.out.println(
				"\n--------------------------------------------------------------------------------------------------------");
		for (int row = 0; row < squares.length; row++) {
			System.out.printf("%02d  | ", row);
			for (int col = 0; col < squares.length; col++) {
				System.out.print(squares[row][col].getCode() + " | ");
				if (col == squares.length-1){
					//System.out.printf("%02d\n", col);
					System.out.println("");
				}
			}

		}
		System.out.println(
				"    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
		String column = "    |";
		for (int col = 0; col < 10; col++) {
			column += " "+col+" |";
		}
		for (int col = 10; col < squares.length; col++) {
			column += " "+col+"|";
		}System.out.println(column);
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");
		System.out.println("SECRET PASSAGES: Conservatory <===> Lounge | Kitchen <===> Study\n"
				+ "K:Kitchen | B:Ballroom | C:Conservatory | P:Billiard Room | L:Library | S:Study | H:Hall | O:Lounge"
				+ "\n" + "D:Dining Room | *:No Entry | #:Door | /:Secret Passage"+"\n");
		System.out.println("***************** WEAPON IN THE ROOM *****************");
		for (Weapon w: allWeapons){
			System.out.print(w.getName() + ":" + w.getRoom().getName() +"  *  ");
		}
		System.out.println();
	}

	/**
	 * Prints out the current positions of all the players and characters.
	 */
	public void printPlayersPosition() {
		System.out.println("\n************* THE POSITION OF CHARACTERS *************");
			for (Character ch : allCharacters) {
				Square sq = getSquareAt(ch.getPosition());
				if (sq instanceof RoomSquare)
					System.out.println(ch.getCode() + ": " + ch.getName() + " is in " + sq.getName() + " "+sq.getPosition()+". Player: "+ch.getPlayerName());
				else System.out.println(ch.getCode() + ": " + ch.getName() + " is at " + sq.getPosition() + ". Player: "+ch.getPlayerName());
			}
		System.out.println();
	}

	/**
	 * Set up the doorway from a room to a corridor. Rows and columns from the room and corridor perspectives
	 * @param r1 the row number
	 * @param c1 the column number
	 * @param r2 the row number
	 * @param c2 the column number
	 */
	public void addDoorway(int r1, int c1, int r2, int c2) {
		RoomSquare room = (RoomSquare) getSquareAt(new Position(r1, c1));
		Corridor corridor = (Corridor) getSquareAt(new Position(r2, c2));
		room.setDoor(corridor);
		getRoom(room.getName()).addDoor(corridor);
		corridor.setDoor(room);
	}

	/**
	 * Return a square on the board based on the given position.
	 * @param pos the position on the board
	 * @return the square on the given position
	 */
	public Square getSquareAt(Position pos) {
		return squares[pos.row()][pos.column()];
	}

	/**
	 * Return a map of Characters with the String name as key and Character as value.
	 * @return a map of Characters with their name
	 */
	public HashMap<String, Character> getAllCharactersWithName(){
		HashMap<String, Character> result = new HashMap<String, Character>();
		for (Character c : allCharacters){
			result.put(c.getName(), c);
		}
		return result;
	}

	/**
	 * Distributes the cards evenly to all the players, if there is any extra cards,
	 * added to all the player refuted cards list.
	 * @param players the list of new players
	 * @throws InvalidAction - if the item is null or invalid user input
	 */
	public void loadCards(ArrayList<Player> players) throws InvalidAction{
		int count = 0;
		int numCards = 18/players.size();
		int extra = 18%players.size();
		Set<Card> extraCards = new HashSet<Card>();
		Set<Card> distributed = new HashSet<Card>();
		Random random = new Random();

		// if the number of card distribution is not even
		// the remain cards will add to all player
		// randomly selected
		if (extra != 0){
			while (count < extra){
				Card c = dealCards.get(random.nextInt(21));
				if (!solution.contains(c)){
					extraCards.add(c);
					distributed.add(c);
					count++;
				}
			}
		}

		// distributes the cards evenly to the players
		// keep randomly selected a card if the card already distributed
		for (int i = 0; i < players.size(); i++){
			ArrayList<Card> cards = new ArrayList<Card>();
			for (int j = 0; j < numCards; j++){
				Card card = dealCards.get(random.nextInt(dealCards.size()));
				while (solution.contains(card) || distributed.contains(card) ||
					   (extraCards.contains(card) && extraCards.size() > 0)){
					   card = dealCards.get(random.nextInt(dealCards.size()));
				}
				cards.add(card);
				distributed.add(card);
			}
			players.get(i).setCards(cards);
			// if there is extra cards, add it to all the player's refuted list
			if (!extraCards.isEmpty()){
				for (Card c : extraCards){
					players.get(i).addRefuted(c);
				}
			}
		}



	}

	/**
	 * Return a set of Weapons on the board.
	 * @return a set of Weapons
	 */
	public Set<Weapon> getAllWeapons(){
		return allWeapons;
	}

	/**
	 * Returns a set of all the Rooms on the board.
	 * @return a set of Rooms
	 */
	public Set<Room> getAllRooms(){
		Set <Room> allRooms = new HashSet <Room>();
		allRooms.add(diningRoom);
		allRooms.add(ballroom);
		allRooms.add(kitchen);
		allRooms.add(conservatory);
		allRooms.add(billiardRoom);
		allRooms.add(library);
		allRooms.add(study);
		allRooms.add(hall);
		allRooms.add(lounge);
		return allRooms;
	}

	/**
	 * Returns a set of all the Characters on the board
	 * @return - a set of Characters
	 */
	public Set<Character> getAllCharacters(){
		return allCharacters;
	}

	/**
	 * Returns a Room based on the given name.
	 * @param name the name of the room
	 * @return the Room if there is one, otherwise returns null
	 */
	public Room getRoom(String name) {
		if (name.equalsIgnoreCase("Kitchen")) {
			return kitchen;
		} else if (name.equalsIgnoreCase("Dining Room")) {
			return diningRoom;
		} else if (name.equalsIgnoreCase("Lounge")) {
			return lounge;
		} else if (name.equalsIgnoreCase("Ballroom")) {
			return ballroom;
		} else if (name.equalsIgnoreCase("Hall")) {
			return hall;
		} else if (name.equalsIgnoreCase("Conservatory")) {
			return conservatory;
		} else if (name.equalsIgnoreCase("Billiard Room")) {
			return billiardRoom;
		} else if (name.equalsIgnoreCase("Library")) {
			return library;
		} else if (name.equalsIgnoreCase("Study")) {
			return study;
		}
		return null;
	}

	/**
	 * Returns a card based on the given name.
	 * @param name the name of room
	 * @return a card from the deck cards if there is one, otherwise returns null
	 */
	public Card getRoomCard(String name) {
		for (int i = 12; i < dealCards.size(); i++) {
			Card card = dealCards.get(i);
			if (card.getName().equalsIgnoreCase(name))
				return card;
		}
		return null;
	}

	/**
	 * Returns a Card based on the given name.
	 * @param name the name of the character if there is one, otherwise returns null
	 * @return a card from the deck cards if there is one, otherwise returns null
	 */
	public Card getCharacterCard(String name) {
		for (int i = 0; i < 6; i++) {
			Card card = dealCards.get(i);
			if (card.getName().equalsIgnoreCase(name))
				return card;
		}
		return null;
	}

	/**
	 * Returns a Card based on the given name.
	 * @param name the name of the weapon if there is one, otherwise returns null
	 * @return a card from the deck cards if there is one, otherwise returns null
	 */
	public Card getWeaponCard(String name) {
		for (int i = 6; i < 12; i++) {
			Card card = dealCards.get(i);
			if (card.getName().equalsIgnoreCase(name))
				return card;
		}
		return null;
	}

	/**
	 * Returns the solution cards of a game which are one Character, one Weapon and one Room cards.
	 * @return a set of solution
	 */
	public Set<Card> getSolution() {
		return solution;
	}

	/**
	 * Returns a deck of Cluedo cards.
	 * @return a deck of Cluedo cards in ordered of Characters, Weapons and Rooms
	 */
	public ArrayList<Card> getDealCards() {
		return dealCards;
	}

}
