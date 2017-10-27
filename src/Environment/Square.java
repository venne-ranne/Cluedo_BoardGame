package Environment;

import Objects.Player;

/**
 * Abstract Square Class represents a 'position' on the board that is accessible ie.players can be placed at that square.
 * */
public abstract class Square {

	protected String name;
	private Position pos;

	/**
	 * Constructs a new Square object with specified the position.
	 * @param name the name of the square
	 * @param pos the position on the board
	 */
	public Square(String name, Position pos) {
		this.name = name;
		this.pos = pos;
	}

	/**
	 * Returns the name of this Square object.
	 * @return the String name of this Square object 
	 */
	public String getName(){
		return name;
	}

	public Position getPosition(){
		return pos;
	}

	/**
	 * Set the player on this square.
	 * @param player the current player
	 */
	public abstract void setPlayer(Player player);

	/**
	 * Remove the player from this square.
	 * @param player the current player
	 */
	public abstract void removePlayer(Player player);

	/**
	 * Check whether this space has a door or not.
	 * @return true if there is a door, otherwise false
	 */
	public abstract boolean hasDoor();

	/**
	 * Returns the code that represents on the board, this method mainly
	 * for map drawing and board layout.
	 * @return the character that represents this square
	 */
	public abstract char getCode();

}
