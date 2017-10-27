package Environment;

/**
 * Position class represents the coordinates of the board as a 2D array format (row/col)
 * */
public final class Position {

	private int row; // must be between 0 and 24
	private int col; // must be between 0 and 24

	/**
	 * Constructors a new Position object with the given row and column numbers.
	 * @param row the row number
	 * @param col the column number 
	 */
	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Returns the row number of this object.
	 * @return the row number
	 */
	public int row() {
		return row;
	}

	/**
	 * Returns the column number of this object.
	 * @return the column number
	 */
	public int column() {
		return col;
	}

	/**
	 * Checks if the position is valid ie. within the board size. Must be between 0 and 24.
	 * @return true if it is between 0 and 24, otherwise false
	 */
	public boolean isValid() {
		return col >= 0 && col <= 24 && row >= 0 && row <= 24;
	}
	
	/**
	 * Checks if this object is equals to some other object.
	 * @param o object the object to compare with
	 * @return true if the object has the same row and column numbers, otherwise false
	 */
	public boolean equals(Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;
			return row == p.row && col == p.col;
		}
		return false;
	}

	/**
	 * Returns hashCode of this object.
	 * @return hashCode
	 */
	public int hashCode() {
		return row ^ col;
	}

	/**
	 * Returns the String of information about this Position object.
	 * @return the String with the column and row numbers
	 */
	public String toString() {
		return "[r = " + row + ", c = " + col+"]";
	}
}
