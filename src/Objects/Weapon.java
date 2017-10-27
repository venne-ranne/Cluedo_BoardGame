package Objects;

import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;
import Game.Game;
import Game.Game.InvalidAction;
import Interface.CluedoBoardCanvas;


public class Weapon implements Card{

	private String name;
	private Room room;
	private ImageIcon image;

	/**
	 * Constructs a new Weapon by specified the room on the board.
	 * @param name the name of the weapon
	 * @param room the Room object where this weapon located
	 * @param image the image of this weapon
	 * @throws InvalidAction - if the item is null or invalid user input
	 */
	public Weapon(String name, Room room, ImageIcon image) throws InvalidAction{
		this.name = name;
		this.room = room;
		this.room.setWeapon(this);
		this.image = image;
	}

	/**
	 * Set the room where the weapon is moved or swapped.
	 * @param room the Room object that this weapon located
	 * @throws InvalidAction - if the item is null
	 */
	public void setRoom(Room room) throws InvalidAction{
		if (room == null) { throw new Game.InvalidAction("Invalid room"); }
		this.room = room;
	}

	/**
	 * Returns the Room object where the weapon is located.
	 * @return the room where the weapon is located
	 */
	public Room getRoom(){
		return room;
	}

	/**
	 * Returns the name of this weapon.
	 * @return the String name of this weapon
	 */
	public String getName(){
		return name;
	}

	/**
	 * Returns the image of this weapon.
	 * @return the String name of this weapon
	 */
	public ImageIcon getImage(){
		return image;
	}

	/**
	 * Determines whether some other card object is equals to this card object.
	 * @param other  the other card to compare with.
	 * @return true if the card has the same name, otherwise false
	 */
	@Override
	public boolean equals(Card other) {
		if (other.getClass() == getClass() && other.getName().equalsIgnoreCase(name))
			return true;
		return false;
	}

	/**
	 * Returns the String of information about this weapon.
	 * @return the String of information
	 */
	public String toString(){
		return name + "is currently in " +room;
	}

	/**
	 * Draw the weapon on the current board.
	 * @param g Graphic object that uses for drawing
	 * @param canvas the current board canvas
	 */
	public void draw(Graphics g, CluedoBoardCanvas canvas){
		Point point = room.getWeaponXY();
		g.drawImage(image.getImage(), point.x, point.y, canvas);
	}

}
