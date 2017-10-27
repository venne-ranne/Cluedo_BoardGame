package Objects;

import java.awt.Graphics;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

import Environment.Corridor;
import Environment.RoomSquare;
import Game.Game.InvalidAction;
import Interface.CluedoBoardCanvas;

/**
 * RoomArea represents a room on the board. It is made up of room squares on the
 * which correlate to those on the board, players in the room and its entrances.
 */
public class Room implements Card {

	private String name;
	private Set<Corridor> entrances;
	private final boolean hasSecretPassage;
	private Room secretRoom = null;
	private RoomSquare secretRoomSquare = null;
	private Weapon weapon;
	private Set<Character> allCharacters;
	private ImageIcon secretPassageImage = null;
	private ImageIcon roomImage;
	private int x1, y1, x2, y2, x3, y3;

	/**
	 * Constructs a new Room object with specified whether this Room object has secret passage or not.
	 * @param name the name of the room
	 * @param hasSecretPassage true if there is a secret passage, otherwise false
	 * @param image the image label for this room
	 */
	public Room(String name, boolean hasSecretPassage, ImageIcon image) {
		this.name = name;
		entrances = new HashSet<Corridor>();
		allCharacters = new HashSet<Character>();
		this.hasSecretPassage = hasSecretPassage;
		this.roomImage = image;
		this.secretPassageImage = null;
		weapon = null;
	}

	/**
	 * Returns the name of this Room object.
	 * @return the name of this room
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the weapon in the room, if there is a existing weapon in this room, then do the swapping.
	 * @param other the weapon to place in this Room
	 * @throws InvalidAction - if the item is null or invalid user input
	 */
	public void setWeapon(Weapon other) throws InvalidAction {
		// this room has a weapon,
		// then exchange this weapon to the other room
		if (other != null) {
			if (this.weapon != null) {
				Room r = other.getRoom();
				this.weapon.setRoom(r);
			}
			Room r = other.getRoom();
			r.setWeapon(null);
			other.setRoom(this);
			this.weapon = other;
		}
	}

	/**
	 * Remove this object from this room.
	 */
	public void removeWeapon() {
		this.weapon = null;
	}

	/**
	 * Return the secret passage room that linked with this room.
	 * @return the secret passage room if there is one, otherwise null
	 */
	public Room getSecretPassage() {
		return secretRoom;
	}

	/**
	 * Checks if there is a secret passage in the room.
	 * @return true if there is a secret passage, otherwise false.
	 */
	public boolean hasSecretPassage() {
		return hasSecretPassage;
	}

	/**
	 * Checks if there is at least one player in the room.
	 * @return true if there is one, otherwise false
	 */
	public boolean hasPlayers() {
		if (!allCharacters.isEmpty()) return true;
		return false;
	}
	/**
	 * Set the secret passage that linked to this room by specified the other Room object and RoomSquare
	 * that using to represent on the board for map displaying.
	 * @param other the other room that linked to this room
	 * @param square the RoomSquare to use for map displaying
	 * @param image the image use to draw on the board
	 */
	public void setSecretPassage(Room other, RoomSquare square, ImageIcon image) {
		secretRoom = other;
		secretRoomSquare = square;
		secretPassageImage = image;
	}

	/**
	 * Set the other RoomSquare that linked to this room as secret passage,
	 * this mainly uses to move/set player's position.
	 * @param other the other RoomSquare that use to remove/set player's position
	 */
	public void setSecretPassage(RoomSquare other) {
		secretRoomSquare = other;
	}

	/**
	 * Return the RoomSquare object that uses as secret passage
	 * @return the RoomSquare of the secret passage
	 */
	public RoomSquare getSecretPassageSquare() {
		return secretRoomSquare;
	}

	/**
	 * Add an entrance to this room.
	 * @param corridor the Corridor object that linked to a RoomSquare as door
	 */
	public void addDoor(Corridor corridor) {
		entrances.add(corridor);
	}

	/**
	 * Get all the entrances of this room.
	 * @return all the entrances of this room
	 */
	public Set<Corridor> getEntrances() {
		return entrances;
	}

	/**
	 * Set the player to this room.
	 * @param player the player that enters the room
	 */
	public void setPlayer(Player player) {
		allCharacters.add(player.getCharacter());
	}

	/**
	 * Removes the player who exits the room.
	 * @param player the player that being removed
	 */
	public void removePlayer(Player player) {
		allCharacters.remove(player.getCharacter());
	}

	/**
	 * Returns all the players in the room.
	 * @return all the players in the room.
	 */
	//public Set<Player> getPlayers() {
		//return this.allPlayers;
	//}

	/**
	 * Set the Character who just entered/moved to the room.
	 * @param character the character of the player or no player
	 */
	public void setCharacter(Character character) {
		allCharacters.add(character);
	}

	/**
	 * Remove the Character from this room.
	 * @param character the character that being removed
	 */
	public void removeCharacter(Character character) {
		allCharacters.remove(character);
	}

	/**
	 * Returns all the characters in this room.
	 * @return a set of Character objects
	 */
	public Set<Character> getCharacters() {
		return this.allCharacters;
	}

	/**
	 * Determines whether some other card object is equals to this card object.
	 * @param other the other card to compare with.
	 * @return true if the card has the same name, otherwise false
	 */
	@Override
	public boolean equals(Card other) {
		if (other.getClass() == getClass() && other.getName().equalsIgnoreCase(name))
			return true;
		return false;
	}

	public void draw(Graphics g, CluedoBoardCanvas canvas){
		g.drawImage(roomImage.getImage(), x1, y1, canvas);
		if (hasSecretPassage) g.drawImage(secretPassageImage.getImage(), x2, y2, canvas);
	}

	public Point getWeaponXY(){
		return new Point(x3, y3);
	}

	/**
	 *
	 * @param x1 the X position for room label
	 * @param y1 the Y position for room label
	 * @param x2 the X position for secret passage
	 * @param y2 the Y position for secret passage
	 * @param x3 the X position for weapon
	 * @param y3 the Y position for weapon
	 */
	public void setXY(int x1, int y1, int x2, int y2, int x3, int y3){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.x3 = x3;
		this.y3 = y3;
	}

	public ImageIcon getRoomImage(){
		return roomImage;
	}

}
