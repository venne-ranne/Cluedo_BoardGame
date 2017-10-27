package Environment;

import Objects.Player;

/**
 * Class Blank Square sets up a 'blank position' on the board where there are no objects (players, weapons etc)
 * This is helpful for telling the game and board what is and what isn't at each coordinates of the board.
 * */
public class BlankSquare extends Square{

	/**
	 * Constructs a new BlankSquare with specified the position.
	 * BlankSquare uses to represent a wall on the board.
	 * @param pos the position on the board
	 */
	public BlankSquare(Position pos){
		super("Blank Space", pos);
	}

	/**
	 * Returns the name of this BlankSquare.
	 * @return the name of this BlankSquare
	 */
	@Override
	public String getName() {
		return "Blank Space";
	}

	/**
	 * Returns the code that represents on the board, this method mainly
	 * for map drawing and board layout.
	 * @return the character that represents this square
	 */
	@Override
	public char getCode() {
		return '*';
	}

	/**
	 * This method does not applied for BlankSquare.
	 */
	@Override
	public boolean hasDoor() {
		throw new RuntimeException(
				"Should not call setPlayer() on a BlankSquare!");
	}

	/**
	 * This method does not applied for BlankSquare.
	 */
	@Override
	public void setPlayer(Player player) {
		throw new RuntimeException(
				"Should not call setPlayer() on a BlankSquare!");
	}

	/**
	 * This method does not applied for BlankSquare.
	 */
	@Override
	public void removePlayer(Player player) {
		throw new RuntimeException(
				"Should not call setPlayer() on a BlankSquare!");
	}

}
