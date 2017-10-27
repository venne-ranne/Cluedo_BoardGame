package Environment;

import Objects.Player;
import Objects.Character;

/**
 * The Corridor class is a square on the board which represents the corridor around the
 * house. It can be blank, have a player and/or connect to a room via doorway direction.
 * */
public class Corridor extends Square {

	private RoomSquare linkedRoom = null;   // this represents the doorway that linked to this corridor
	private Character character;

	/**
	 * Constructor a new Corridor object by specified the position on the board.
	 * @param pos the pos on the board
	 */
	public Corridor(Position pos){
		super("Corridor", pos);
		//player = null;
	}

	/**
	 * Check whether this space is occupied or not.
	 * @return true if the player who occupied this square, otherwise false.
	 */
	public boolean hasPlayer() {
		if (character == null) return false;
		return true;
	}

	/**
	 * Set player who occupied this space
	 * @param player the player who occupied this square
	 */
	public void setPlayer(Player player){
		//this.player = player;
		character = player.getCharacter();
	}

	/**
	 * Remove player from this corridor space.
	 * @param player the player that being removed
	 */
	@Override
	public void removePlayer(Player player) {
		//this.player = null;
		this.character = null;
	}

	/**
	 * Set the Character token that occupied this square.
	 * @param character the character that occupied this square
	 */
	public void setCharacter(Character character){
		this.character = character;
	}

	/**
	 * Remove the Character token that occupied this space.
	 */
	public void removeCharacter(){
		this.character = null;
	}

	/**
	 * Check whether this space has a doorway to a room.
	 * @return true if there is one, otherwise false
	 */
	@Override
	public boolean hasDoor() {
		if (linkedRoom != null) return true;
		return false;
	}

	/**
	 * Set the door direction and the space that linked to it.
	 * @param room the RoomSquare object that linked to this Corridor as door
	 */
	public void setDoor(RoomSquare room) {
		linkedRoom = room;
	}

	/**
	 * Returns the RoomSquare object that linked to this Corridor object
	 * that represents the doorway between them.
	 * @return the RoomSquare that linked to this Corridor as door
	 */
	public RoomSquare getLinkedRoomSquare() {
		return linkedRoom;
	}

	/**
	 * Returns the code that represents on the board, this method mainly
	 * for map drawing and board layout.
	 * @return the character that represents this square
	 */
	@Override
	public char getCode() {
		if (character != null)
			return character.getCode();
		return ' ';
	}

	/**
	 * Return the character on this space.
	 * @return the character at this square
	 */
	public Character getCharacter(){
		return character;
	}

}
