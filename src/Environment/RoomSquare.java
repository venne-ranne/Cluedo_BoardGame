package Environment;

import Objects.Player;
import Objects.Room;

/**
 * The room class extends square and represents a square in a room area of a board.
 * It holds information on who is in the room and what corridors and rooms they connect
 * to via secret passages and doorways.
 * */
public class RoomSquare extends Square{

	private Corridor linkedCorridor = null;
	private final Room room;


	/**
	 * Constructs a new RoomSquare object by specified the position and room.
	 * @param description the name of room
	 * @param pos the position on the board
	 * @param room the room that contains this square
	 */
	public RoomSquare(String description, Position pos, Room room) {
		super(description, pos);
		this.room = room;
	}

	/**
	 * Set the corridor square that linked this to represent a doorway.
	 * @param corridor the linked corridor that represents a doorway
	 */
	public void setDoor(Corridor corridor) {
		this.linkedCorridor = corridor;
	}

	/**
	 * Set the secret passage that linked with this RoomSquare.
	 * @param secretRoom the other RoomSquare in the other Room
	 */
	public void setSecretPassage(RoomSquare secretRoom) {
		room.setSecretPassage(secretRoom);
	}

	/**
	 * Returns the Room object that this RoomSquare belongs to.
	 * @return the room of this RoomSquare belongs to
	 */
	public Room getRoom() {
		return room;
	}


	/**
	 * Determines if this room has secret passage or not.
	 * @return true if there is one, otherwise false
	 */
	public boolean hasSecretPassage(){
		return room.hasSecretPassage();
	}

	/**
	 * Determines if this RoomSquare has a door access or not.
	 * @return true if there is one, otherwise false
	 */
	public boolean hasDoor(){
		if (linkedCorridor != null) return true;
		return false;
	}

	/**
	 * Returns the code that represents on the board, this method mainly
	 * for map drawing and board layout.
	 * @return the character that represents this square
	 */
	@Override
	public char getCode() {
		if (room.hasSecretPassage()){
			if (room.getSecretPassageSquare().equals(this)){
				return '/';
			}
		}
		if (linkedCorridor != null) return '#';
		if (name.equals("Kitchen")){
			return 'K';
		} else if (name.equals("Dining Room")){
			return 'D';
		} else if (name.equals("Lounge")){
			return 'O';
		} else if (name.equals("Ballroom")){
			return 'B';
		} else if (name.equals("Hall")){
			return 'H';
		} else if (name.equals("Conservatory")){
			return 'C';
		} else if (name.equals("Billiard Room")){
			return 'P';
		} else if (name.equals("Library")){
			return 'L';
		} else return 'S';
	}
	
	
	/**
	 * Return the name of the room that linked to this square as secret passage
	 * @return the name of the other room that linked the secret passage
	 */
	public String getSecretPassage(){
		return room.getSecretPassage().getName();
	}

	/**
	 * Set the player to this square if he/she enters a room.
	 * @param player the player who has entered this room
	 */
	@Override
	public void setPlayer(Player player) {
		room.setPlayer(player);
	}

	/**
	 * Removes a player when he/she exits the room.
	 * @param player the player who is exiting the room
	 */
	@Override
	public void removePlayer(Player player) {
		room.removePlayer(player);
	}

}
