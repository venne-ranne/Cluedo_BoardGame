package Objects;

import java.util.ArrayList;
import java.util.List;

import Environment.Position;
import Game.Game;
import Game.Game.InvalidAction;

/**
 * The Player class is used to represent a player playing the Cluedo game. It is
 * responsible for holding data of the players characteristics, choices,
 * movement and also methods to let them execute their moves in the game.
 */
public class Player {
	
	private String name;
	private Character character;
	private Position pos;
	private boolean eliminated;
	private ArrayList <Card> handCards;
	private ArrayList <Card> refuted = new ArrayList <Card>();
	private ArrayList<String> suggestions = new ArrayList<String>();

	/**
	 * Constructs a new Player object with specified Character
	 * @param name the name of the player
	 * @param character the Character object that played by this player
	 * @throws InvalidAction if the move/action is invalid or user input the wrong arguments
	 */
	public Player(String name, Character character)throws InvalidAction{
		if (name == null || character == null){ new IllegalArgumentException("Invalid arguments");}
		this.name = name;
		this.character = character;
		this.character.setPlayer(this);
		eliminated = false;
		this.pos = character.getPosition();  // should be start position
	}

	/**
	 * Returns the current position of this player.
	 * @return the current position of this player
	 */
	public Position getPosition() {
		return character.getPosition();
	}

	/**
	 * Set the position of this player on the board.
	 * @param pos the new position
	 * @throws InvalidAction if the move/action is invalid or user input the wrong arguments
	 */
	public void setPosition(Position pos) throws InvalidAction{
		//if (pos == null) { new IllegalArgumentException("Invalid pos");}
		//this.pos = pos;
		character.setPosition(pos);
	}

	/**
	 * Returns the code that represents on the board, this method mainly
	 * for map drawing and board layout.
	 * @return the character that represents this square
	 */
	public char getCode() {
		return character.getCode();
	}

	/**
	 * Set a set of cards to this player's hand.
	 * @param cards - a set of card
	 * @throws InvalidAction if the move/action is invalid or user input the wrong arguments
	 */
	public void setCards(ArrayList<Card> cards) throws InvalidAction{
		if (cards == null){throw new Game.InvalidAction("Invalid cards");}
		this.handCards = cards;
	}

	/**
	 * Returns the String of the detective notes which included cards on hand, refuted card and suggestion list.
	 * @return - the String of the detective note
	 */
	public String displayNotes() {		
		String notes = "\n------------------------------------";	//---------
		notes += "\n           *****  N O T E S  *****           \n";
		notes += "MY CHARACTER: " + this.character.getName() + "\n\n";

		if (this.handCards.isEmpty()) {
			notes += "CARDS IN HAND: \nNONE\n\n";
		} else {
			notes += "CARDS IN HAND: \n";
			for (int i = 0; i < this.handCards.size(); i++) {
				notes += (i + 1) + ". " + this.handCards.get(i).getName() + "\n";
			}
			notes += "\n\n";
		}

		if (this.refuted.isEmpty()) {
			notes += "REFUTED CARDS I'VE SEEN: \nNONE\n\n";
		} else {
			notes += "REFUTED CARDS I'VE SEEN: \n";
			for (int i = 0; i < this.refuted.size(); i++) {
				notes += (i + 1) + ". " + this.refuted.get(i).getName() + "\n";
			}
			notes += "\n\n";
		}

		if (this.suggestions.isEmpty()) {
			notes += "SUGGESTIONS I'VE MADE SO FAR: \nNONE\n\n";
		} else {
			notes += "SUGGESTIONS I'VE MADE SO FAR: \n";
			for (int i = 0; i < this.suggestions.size(); i++) {
				notes += (i + 1) + ". " + this.suggestions.get(i) + "\n";
			}
			notes += "\n\n";
		}
		//notes += "------------------------------------";	//---------

		return notes;
	}

	/**
	 * Add to list of refuted cards player has seen.
	 * @param refutedCard - card refuted
	 * @throws InvalidAction if the move/action is invalid or user input the wrong arguments
	 */
	public void addRefuted(Card refutedCard) throws InvalidAction{
		if (refuted == null){ new IllegalArgumentException("Invalid refuted card"); }
		if (!refuted.contains(refutedCard)) {
			this.refuted.add(refutedCard);
		}
	}


	/**
	 * Add the suggestion list made by this player.
	 * @param character - the suggested Character card
	 * @param weapon - the suggested Weapon card
	 * @param room - the suggested Room card
	 */
	public void addSuggestion(Card character, Card weapon, Card room) {
		if (character == null || weapon == null || room == null) {
			throw new IllegalArgumentException("Invalid room");
		}
		if (!suggestions.contains(character.getName() + ", " + weapon.getName() + ", " + room.getName())) {
			this.suggestions.add(character.getName() + ", " + weapon.getName() + ", " + room.getName());
		}
	}

	/**
	 * Return the Character object that played by this player.
	 * @return - the Character of this player
	 */
	public Character getCharacter() {
		return this.character;
	}

	/**
	 * Returns the name of this player.
	 * @return - the name of this player
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the list of cards on player's hands
	 * @return - the list of hand cards
	 */
	public ArrayList<Card> getHand() {
		return this.handCards;
	}

	/**
	 * Update the status to eliminate after he/she lost the game.
	 * @param result - true if he/she is eliminated
	 */
	public void eliminated(boolean result) {
		this.eliminated = result;
	}

	/**
	 * Checks if this player is eliminated or not.
	 * @return - true if he is eliminated, otherwise false
	 */
	public boolean isEliminated() {
		return this.eliminated;
	}

	/**
	 * Returns the list of suggestions that made by this player.
	 * @return - the list of suggestions
	 */
	public List<String> getSuggestions() {
		return this.suggestions;
	}

	/**
	 * Returns the list of refuted cards that seen by this player.
	 * @return - the list of refuted cards
	 */
	public List<Card> getRefuted() {
		return this.refuted;
	}
	
	/**
	 * Returns the String of information about this player
	 * @return - the String of information
	 */
	public String toString() {
		return "Player " + this.name + " with character " + this.character.getName() + " at square "
				+ this.character.getPosition().toString();
	}

}
