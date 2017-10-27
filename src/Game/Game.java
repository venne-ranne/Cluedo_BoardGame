package Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Environment.BlankSquare;
import Environment.Board;
import Environment.Corridor;
import Environment.Board.Direction;
import Environment.Position;
import Environment.RoomSquare;
import Environment.Square;
import Interface.CluedoFrame;
import Objects.Card;
import Objects.Character;
import Objects.Player;
import Objects.Room;
import Objects.Weapon;

/**
 * The Game Class sets up the Cluedo game including the players, the start positions, the board and deals out the cards.
 * All actions that the player makes are then set to the corresponding objects through this class
 * */
public class Game {

	private Board board;
	private ArrayList<Player> players;
	private boolean gameOver = false;
	private Set<Position> visited = new HashSet<Position>();
	private int diceRoll = 0;
	private boolean rollAction = false;
	private Position firstPosition;

	/**
	 * Constructs a new game by constructs a new Board.
	 * @throws InvalidAction if the move/action is invalid or the wrong arguments
	 */
	public Game() throws InvalidAction {
		board = new Board();
		players = new ArrayList<Player>();
		firstPosition = null;
	}

	/**
	 * Move a player from current location to one square at any direction.
	 * @param player the current player to move
	 * @param direct the direction to move
	 * @throws InvalidAction exception for invalid action
	 */
	public void movePlayer(Player player, Direction direct) throws InvalidAction{
		if (player.isEliminated()) throw new InvalidAction("Sorry... You've lost the game.");

		Position newPos = isValidMove(player, direct);

		// remove player from current position
		Square sq = board.getSquareAt(player.getPosition());
		sq.removePlayer(player);

		// update the player position
		sq = board.getSquareAt(newPos);
		sq.setPlayer(player);
		player.setPosition(newPos);
	}

	/**
	 * Move a player from a room to other room through secret passage.
	 * @param player the player who uses the secret passage
	 * @throws InvalidAction if the move is invalid or user input the wrong arguments
	 */
	public void useSecretPassage(Player player) throws InvalidAction {
		// remove player from current position
		Position pos = player.getPosition();
		RoomSquare sq = (RoomSquare) board.getSquareAt(pos);
		board.getSquareAt(pos).removePlayer(player);

		// update the player position near the door
		// so that it is easier to exit room for next round
		Room room = sq.getRoom().getSecretPassage();
		Set<Corridor> doors = room.getEntrances();
		for (Corridor c : doors) {
			RoomSquare secretRoom = c.getLinkedRoomSquare();
			secretRoom.setPlayer(player);
			Position newPos = secretRoom.getPosition();
			player.setPosition(newPos);
		}
	}

	/*
	 * =================================================================
	 * the following methods are used when player is suggesting/accusing
	 * =================================================================
	 */

	/**
	 * Adds suggestion and refuted card to players lists. Checks suggested
	 * characters current position and sets it to the new room
	 * swaps weapons between rooms.
	 * @param player the player who made the suggestion
	 * @param suggestionList the list of suggestions
	 * @param refuted the refuted card from other player
	 * @throws InvalidAction if the move is invalid or user input the wrong arguments
	 */
	public void makeSuggestion(Player player, List<Card> suggestionList, Card refuted) throws InvalidAction {
		if (player == null) {
			throw new IllegalArgumentException("Invalid player");
		}
		if (suggestionList == null || suggestionList.size() != 3) {
			throw new IllegalArgumentException("Invalid suggestion card list");
		} // check if the cards are in the right order (character >> weapon >>
			// room)
		if (this.board.getCharacterCard(suggestionList.get(0).getName()) == null
				|| this.board.getWeaponCard(suggestionList.get(1).getName()) == null
				|| this.board.getRoomCard(suggestionList.get(2).getName()) == null) {
			throw new IllegalArgumentException("Invalid suggestion card list");
		}
		// check valid player ie not eliminated and in the room suggesting
		if (player.isEliminated()) {
			throw new Game.InvalidAction("Eliminated player can't make suggestion");
		}
		if (!(board.getSquareAt(player.getPosition()) instanceof RoomSquare)) {
			throw new Game.InvalidAction("Player not in a room to make suggestion.");
		}
		// add suggestion to players list of suggestions
		// add refuted card to list if not null
		player.addSuggestion(suggestionList.get(0), suggestionList.get(1), suggestionList.get(2));
		if(refuted != null){player.addRefuted(refuted);}

		Character suggestedCharacter = (Character) suggestionList.get(0);
		Weapon weapon = (Weapon) suggestionList.get(1);
		Room room = (Room) suggestionList.get(2);

		room.setWeapon(weapon);   // room will take care the swapping for weapons
		Position newPos = player.getPosition(); // get the current player position
		if (suggestedCharacter.hasPlayer()){
			// remove other player from current position
			// first get other player old position and remove them from there
			Player otherPlayer = suggestedCharacter.getPlayer();
			Square sq = board.getSquareAt(otherPlayer.getPosition());
			sq.removePlayer(otherPlayer);

			// update other player position to the current player's position
			sq = board.getSquareAt(newPos);
			sq.setPlayer(otherPlayer);
			otherPlayer.setPosition(newPos);
		} else {
			// if the no player, then set character to the current player's position
			suggestedCharacter.setPosition(newPos);
			room.setCharacter(suggestedCharacter);
		}
	}

	/**
	 * Eliminates the player.
	 * @param player the player who is being eliminated
	 * @throws IllegalArgumentException if the player is null
	 */
	public void eliminated(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("Invalid player");
		}
		player.eliminated(true);
	}

	/**
	 * Checks the accusation cards with the solution.
	 * @param accusation the list of accusation cards
	 * @throws IllegalArgumentException if the accusation is null or the number of cards is not 3
	 * @return true if all the accusation is right, otherwise false
	 */
	public boolean checkAccusation(List<Card> accusation) {
		if (accusation == null || accusation.size() != 3) {
			throw new IllegalArgumentException("Invalid accusation cards");
		} // check if the cards are in the right order (character >> weapon >>
			// room)
			// if the cards match solution then return true
		return this.board.getSolution().contains(accusation.get(0))
				&& this.board.getSolution().contains(accusation.get(1))
				&& this.board.getSolution().contains(accusation.get(2));
	}

	/**
	 * Finds next player going clockwise, who has at least 1 refutable card.
	 * @param player the player to go clockwise from
	 * @param suggestionList the list of suggestions
	 * @return the next refutable player
	 * @throws InvalidAction if the action is invalid or the invalid arguments
	 */
	public Player findNextRefutablePlayer(Player player, List<Card> suggestionList) throws InvalidAction {
		if (player == null) {
			throw new IllegalArgumentException("Invalid player");
		}
		if (suggestionList == null || suggestionList.size() != 3) {
			throw new IllegalArgumentException("Invalid suggestion card list");
		} // check if the cards are in the right order (character >> weapon >>
			// room)
		if (this.board.getCharacterCard(suggestionList.get(0).getName()) == null
				|| this.board.getWeaponCard(suggestionList.get(1).getName()) == null
				|| this.board.getRoomCard(suggestionList.get(2).getName()) == null) {
			throw new IllegalArgumentException("Invalid suggestion card list");
		}
		Player refutingPlayer = null;
		int currentPlayerIndex = players.indexOf(player);
		// searching from player to end of index
		for (int i = currentPlayerIndex + 1; i < players.size(); i++) {
			for (Card card : players.get(i).getHand()) { // if player has at
															// least 1 card then
															// make that player
															// the refuter
				if (card.equals(suggestionList.get(0)) || card.equals(suggestionList.get(1))
						|| card.equals(suggestionList.get(2))) {
					refutingPlayer = players.get(i);
				}
			}
		}
		// searching from beginning of index to player
		if (refutingPlayer == null) {
			if (currentPlayerIndex > 0) {
				for (int i = 0; i < currentPlayerIndex; i++) {
					for (Card card : players.get(i).getHand()) {
						if (card.equals(suggestionList.get(0)) || card.equals(suggestionList.get(1))
								|| card.equals(suggestionList.get(2))) {
							refutingPlayer = players.get(i);
						}
					}
				}
			}
		}
		return refutingPlayer;
		// if returning null this indicates
		// no player had all three cards so if the suggester doesn't have them
		// either then this combo is the solution
	}

	/**
	 * Get the cards from refuting player which matches with the suggestion.
	 * @param suggestionList the list of suggestions
	 * @param refutingPlayer the player who played the character that being moved to the room
	 * @return a list of refutable cards
	 */
	public List<Card> getRefutableCards(List<Card> suggestionList, Player refutingPlayer) {
		if (refutingPlayer == null) {
			throw new IllegalArgumentException("Invalid player");
		}
		if (suggestionList == null || suggestionList.size() != 3) {
			throw new IllegalArgumentException("Invalid suggestion card list");
		}
		if (this.board.getCharacterCard(suggestionList.get(0).getName()) == null
				|| this.board.getWeaponCard(suggestionList.get(1).getName()) == null
				|| this.board.getRoomCard(suggestionList.get(2).getName()) == null) {
			throw new IllegalArgumentException("Invalid suggestion card list");
		}
		List<Card> refutableCards = new ArrayList<Card>();
		for (Card card : refutingPlayer.getHand()) {
			if ((card.equals(suggestionList.get(0)) || card.equals(suggestionList.get(1))
					|| card.equals(suggestionList.get(2))) && !refutableCards.contains(card)) {
				refutableCards.add(card);
			}
		}
		return refutableCards;
	}

	/*
	 * ==============================================================
	 * the following methods are used when moving a player
	 * ==============================================================
	 */

	/**
	 * Determines if the player has given a valid list of directions or not.
	 * @param player the current player
	 * @param direct the direction to move
	 * @throws InvalidAction if the any of the given directions is invalid such as there's a wall
	 * @return the new position where the player will be moving to
	 */
	public Position isValidMove(Player player, Direction direct) throws InvalidAction {
		Square sq, prevSq;
		Position prevPos = player.getPosition();
		Position pos = getPositionFromDirection(prevPos, direct);

			if (visited.isEmpty()) firstPosition = prevPos;
			if (visited.contains(pos) || (firstPosition.equals(pos) && firstPosition != null && !visited.isEmpty()))
				throw new InvalidAction("Invalid move. Cannot move to a space twice.");

			// first, check if the given position is within the board or not
			// then get the square on the board and then check a list of condition
			// if the space is ok to move on or not, ie. check if there's a wall or not
			if (pos.isValid()) {
				sq = board.getSquareAt(pos);
				prevSq = board.getSquareAt(prevPos);
				if (sq instanceof BlankSquare ||
				   (sq instanceof RoomSquare && !sq.hasDoor() && prevSq instanceof Corridor && !prevSq.hasDoor()) ||
				   (sq instanceof RoomSquare && sq.hasDoor() && prevSq instanceof Corridor && !prevSq.hasDoor()))
					throw new InvalidAction("Invalid move. There's a wall.");
				if (sq instanceof Corridor && ((Corridor) sq).hasPlayer())
					throw new InvalidAction("Invalid move. Move included spaces occupied by other player.");

				// return after he/she enters a room
				if (sq instanceof RoomSquare && prevSq instanceof Corridor && !visited.isEmpty()) {
					return pos;
				} else if (sq instanceof RoomSquare && prevSq instanceof Corridor && prevSq.hasDoor()) {
					return pos;
				}

				// when the player tried to exit a room
				if (prevSq instanceof RoomSquare && !sq.hasDoor() && visited.isEmpty()) {
					throw new InvalidAction("Invalid move. You're still in the same room. Choose direction toward a door for exit.");
				}

			} else
				throw new InvalidAction("Move is invalid. There's a wall");

			// added to visited list so that the player don't travel on the same square twice.
			visited.add(pos);
		return pos;  // return the new position
	}


	/**
	 * Get a new position based on the current position and given direction.
	 * @param pos the current position
	 * @param direct the direction to go
	 * @return the new position
	 */
	public Position getPositionFromDirection(Position pos, Direction direct) {
		if (direct.equals(Direction.NORTH))
			return new Position(pos.row() - 1, pos.column());
		else if (direct.equals(Direction.EAST))
			return new Position(pos.row(), pos.column() + 1);
		else if (direct.equals(Direction.SOUTH))
			return new Position(pos.row() + 1, pos.column());
		else
			return new Position(pos.row(), pos.column() - 1);
	}

	/**
	 * Get a list of direction based on the given string from user input.
	 * @param directionList a string that contains all the directions
	 * @return a list of Direction
	 */
	public List<Direction> getAllDirections(String directionList) {
		List<Direction> list = new ArrayList<Direction>();
		char[] split = directionList.toUpperCase().toCharArray();

		for (char ch : split) {
			if (ch == 'W')
				list.add(Direction.NORTH);
			else if (ch == 'A')
				list.add(Direction.WEST);
			else if (ch == 'S')
				list.add(Direction.SOUTH);
			else list.add(Direction.EAST);
		}
		return list;
	}

	/**
	 * Determines if the string is an valid direction list. Only WASD keys input
	 * are allowed. Splits the string to char array then iterates through to
	 * check whether the char is valid or not.
	 * @param directionList the list of direction in a string
	 * @param numRolls the number of dice rolls
	 * @return True if the string is only contains 'WASD' letters, otherwise false.
	 * @throws InvalidAction if the specified initial capacity is not WASD keys
	 */
	public boolean checkForDirections(String directionList, int numRolls) throws InvalidAction{
		if (directionList.length() > numRolls)
			throw new InvalidAction("Invalid move. Only " + numRolls + " moves allowed.");

		if (directionList.length() == 0){
			throw new InvalidAction("Please enter at least one move.");
		}

		char[] split = directionList.toUpperCase().toCharArray();

		for (char ch : split) {
			if (!java.lang.Character.isLetter(ch))
				throw new InvalidAction("Invalid move. Only WASD keys allowed.");
			if (ch == 'W' || ch == 'A' || ch == 'S' || ch == 'D')
				continue;
			else
				throw new InvalidAction("Invalid move. Only WASD keys allowed.");
		}
		return true;
	}

	/*
	 * ==============================================================
	 * the getter and setter methods
	 * ==============================================================
	 */

	/**
	 * Determines if the character is a valid token or not.
	 * @param name the name of the Character
	 * @return true if it is a valid character, otherwise false.
	 */
	public boolean isValidCharacter(String name) {
		for (Character character : board.getAllCharacters()) {
			if (character.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get all the characters on the board.
	 * @return the set of Characters objects
	 */
	public Set<Character> getAllCharacters() {
		return board.getAllCharacters();
	}

	/**
	 * Get the current game board.
	 * @return - the current game board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Get the deck of Cluedo cards
	 * @return the deck of Cluedo cards
	 */
	public ArrayList<Card> getDealCards() {
		return board.getDealCards();
	}

	/**
	 * Set the players.
	 * @param players the list of new players
	 */
	public void setAllPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	/**
	 * Checks if the game is game over or not.
	 * @return true if it is game over, otherwise false
	 */
	public boolean gameOver(){
		return this.gameOver;
	}

	/**
	 * Ends the game after a player wins the game.
	 */
	public void endGame(){
		this.gameOver = true;
	}

	/**
	 * Returns the list of current players.
	 * @return the current players list
	 */
	public ArrayList<Player> getAllPlayers(){
		return players;
	}

	/**
	 * Determines if the game is over or not.
	 * @return the current game status
	 */
	public Boolean isGameOver(){
		return this.gameOver;
	}

	/**
	 * Resets the visited square set.
	 */
	public void resetVisitedSquare(){
		this.visited.clear();
		this.diceRoll = 0;
		this.rollAction = false;
		this.firstPosition = null;
	}

	/**
	 * Returns the number of moves that used by the player
	 * @return the number of moves that used by the player
	 */
	public int numberOfUseMoves(){
		return this.visited.size();
	}

	/**
	 * Set the number of dice roll.
	 * @param numDice the amount of dice roll
	 */
	public void setDiceRoll(int numDice){
		this.diceRoll = numDice;
		this.rollAction = true;
	}

	/**
	 * Returns the number of dice roll.
	 * @return the number of dice roll
	 */
	public int getDiceRoll(){
		return this.diceRoll;
	}

	/**
	 * Determines if the dice is rolled or not.
	 * @return true if rolled, otherwise false
	 */
	public boolean isDiceRoll() {
		return rollAction;
	}

	/**
	 * Returns next player after the player passed in the argument
	 * @param currentPlayer the current player
	 * @return the next player
	 */
	public Player getNextPlayer(Player currentPlayer) {
		int currentPlayerIndex = players.indexOf(currentPlayer);
		Player nextPlayer = null;
		// searching from player to end of index
		for (int i = currentPlayerIndex + 1; i < players.size(); i++) {
			if (!players.get(i).isEliminated()) {
				nextPlayer = players.get(i);
				return nextPlayer;
			}
		}
		// searching from beginning of index to player
		if (nextPlayer == null) {
			if (currentPlayerIndex > 0) {
				for (int i = 0; i < currentPlayerIndex; i++) {
					if (!players.get(i).isEliminated()) {
						nextPlayer = players.get(i);
						return nextPlayer;
					}
				}
			}
		}
		return nextPlayer;
	}

	/**
	 * Indicates an attempt to make an invalid move.
	 */
	public static class InvalidAction extends Exception {
		public InvalidAction(String msg) {
			super(msg);
		}
	}

}
