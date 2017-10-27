package Objects;

import java.awt.Image;

import javax.swing.ImageIcon;

import Environment.Position;
import Game.Game;
import Game.Game.InvalidAction;

/**
 * The character class represents the characters involved in the game.
 * It initializes holds their positions, name and code which is useful for getting and setting
 * them in different rooms to suggest a murder scenario
 * */
public class Character implements Card {

	private final String name;
	private Position pos;
	private Player player = null;
	private ImageIcon characImage = null;

	/**
	 * Constructs a new Character object with initialized the start position on the board.
	 * @param name the name of the character
	 * @param pos the start position
	 * @param img the image of this character
	 */
	public Character(String name, Position pos, ImageIcon img){
		this.name = name;
		this.pos = pos;
		this.characImage = img;
	}

	/**
	 * Set the position of this character to the given position on the board.
	 * @param pos the new position 
	 * @throws InvalidAction when the given position is NULL
	 */
	public void setPosition(Position pos) throws InvalidAction{
		if (pos == null){ throw new Game.InvalidAction("Invalid position"); }
		this.pos = pos;
	}

	 /**
	 * Determines whether some other card object is equals to this card object.
	 * @param other the other card to compare with.
	 * @return true if the card has the same name, otherwise false
	 */
	@Override
	public boolean equals(Card other) {
		if (other != null && other.getClass() == getClass() && other.getName().equalsIgnoreCase(name))
			return true;
		return false;
	}
	
	/**
	 * Set a player for this character.
	 * @param player the player that belongs to this Character
	 */
	public void setPlayer(Player player){
		this.player = player;
	}
		
	/**
	 * Returns the player who owns this character.
	 * @return the player who owns this character, otherwise null.
	 */
	public Player getPlayer(){
		if (this.player != null) return this.player;
		return null;
	}
	
	/**
	 * Determines if this character belongs to a player
	 * @return true if this character has a player, otherwise false.
	 */
	public boolean hasPlayer(){
		if (this.player != null) return true;
			return false;
	}
	
	/**
	 * Returns the code that represents on the board, this method mainly
	 * for map drawing and board layout.
	 * @return the character that represents this square
	 */
	public char getCode(){
		if (name.equalsIgnoreCase("Miss Scarlett")) return 's';
		else if (name.equalsIgnoreCase("Colonel Mustard")) return 'm';
		else if (name.equalsIgnoreCase("Mrs White")) return 'w';
		else if (name.equalsIgnoreCase("Reverend Green")) return 'g';
		else if (name.equalsIgnoreCase("Mrs Peacock")) return 'c';
		return 'p';  //"Professor Plum"
	}
	
	/**
	 * Returns the name of the character, mainly use for TextClient class.
	 * @return the name of the character
	 */
	public String toString(){
		return "Character: " + name;
	}
	
	/**
	 * Returns the name of this character.
	 * @return the String of this character's name
	 */
	public String getName(){
		return name;
	}

	/**
	 * Returns the current position of this character on the board.
	 * @return the position of this character
	 */
	public Position getPosition(){
		return pos;
	}

	/**
	 * Return the name of the player.
	 * @return the name of the player
	 */
	public String getPlayerName() {
		if (player != null) return player.getName();
		return "NO PLAYER";
	}
	
	/**
	 * Return the image of the player.
	 * @return the name of the player
	 */
	public ImageIcon getImage() {
		return this.characImage;
	}

}
