package Objects;

public interface Card {
	
	/**
	 * Determines if some other Card object is equals to this object.
	 * @param other the other card that uses to compare with
	 * @return true if the card has the same, otherwise false
	 */
	public boolean equals(Card other);
	
	/**
	 * Returns the name of this card.
	 * @return the name of this card
	 */
	public String getName();
}
