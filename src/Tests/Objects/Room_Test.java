package Tests.Objects;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.junit.Test;

import Environment.Board;
import Environment.Board.Direction;
import Environment.Corridor;
import Environment.Position;
import Environment.RoomSquare;
import Game.Game;
import Game.Game.InvalidAction;
import Objects.Card;
import Objects.Character;
import Objects.Player;
import Objects.Room;
import Objects.Weapon;

public class Room_Test {
	
	/**
	 * Test if the room has a secret passage or not
	 *
	 */
	@Test
	public void testRoomsForSecretPassage() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		// rooms that contain secret passage
		Room lounge = board.getRoom("Lounge");
		Room kitchen = board.getRoom("kitchen");
		Room study = board.getRoom("study");
		Room conservatory = board.getRoom("conservatory");
		assertTrue(lounge.hasSecretPassage());
		assertTrue(kitchen.hasSecretPassage());
		assertTrue(conservatory.hasSecretPassage());
		assertTrue(study.hasSecretPassage());
		assertEquals(study.getSecretPassage(), kitchen);
		assertEquals(kitchen.getSecretPassage(), study);
		assertEquals(lounge.getSecretPassage(), conservatory);
		assertEquals(conservatory.getSecretPassage(), lounge);
		
		// rooms that do not have secret passage
		Room hall = board.getRoom("hall");
		Room library = board.getRoom("library");
		Room ballroom = board.getRoom("ballroom");
		Room billiard = board.getRoom("billiard room");
		Room dining = board.getRoom("dining room");
		
		assertFalse(hall.hasSecretPassage());
		assertFalse(library.hasSecretPassage());
		assertFalse(ballroom.hasSecretPassage());
		assertFalse(billiard.hasSecretPassage());
		assertFalse(dining.hasSecretPassage());
		assertEquals(dining.getName(), "Dining Room");
		
		// the RoomSquare that use to move player through secret passage
		assertEquals(conservatory.getSecretPassageSquare().getRoom(), conservatory);
		assertEquals(conservatory.getSecretPassageSquare(), board.getSquareAt(new Position(1, 23)));
	}
	

	/**
	 * Test removes a player from a room
	 *
	 */
	@Test
	public void testRemovePlayerFromRoom() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		ArrayList<Player> players = mockUpThreePlayers(game);
		Room lounge = board.getRoom("Lounge");
		Room dining = board.getRoom("Dining Room");
		for (Corridor c : lounge.getEntrances()){
			assertTrue(c.hasDoor());
		}
		
		// move a player to a room, then check the number persons in the room
		assertEquals(lounge.getCharacters().size(), 0);
		assertFalse(lounge.hasPlayers());
		lounge.setPlayer(players.get(1));
		assertEquals(lounge.getCharacters().size(), 1);
		assertTrue(lounge.hasPlayers());
		assertEquals(players.get(1).getCharacter().getPosition(), players.get(1).getPosition());
		
		// remove a player from previous room, then enter other room
		assertFalse(dining.hasPlayers());
		assertFalse(dining.hasPlayers());
		lounge.removePlayer(players.get(1));
		dining.setPlayer(players.get(1));
		assertEquals(lounge.getCharacters().size(), 0);
		assertEquals(dining.getCharacters().size(), 1);
		assertFalse(lounge.hasPlayers());
		assertTrue(dining.hasPlayers());
	}
	
	/**
	 * Test removes a character with no player from a room
	 *
	 */
	@Test
	public void testRemoveCharacterFromRoom() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		Room lounge = board.getRoom("Lounge");
		// move a character to a room, then check the number persons in the room
		assertEquals(lounge.getCharacters().size(), 0);
		lounge.setCharacter((Character) board.getCharacterCard("Reverend Green"));
		assertEquals(lounge.getCharacters().size(), 1);
		assertTrue(lounge.hasPlayers());
		lounge.removeCharacter((Character) board.getCharacterCard("Reverend Green"));
		assertEquals(lounge.getCharacters().size(), 0);
		assertFalse(lounge.hasPlayers());
	}
	
	/**
	 * Test if some other object is equals to a Room card object.
	 *
	 */
	@Test
	public void testEqualsRoomObject() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		Room hall = board.getRoom("hall");
		assertTrue(hall.equals(board.getRoomCard("HALL")));
		assertTrue(hall.equals((Card) board.getRoom("hall")));
		assertFalse(hall.equals(board.getRoomCard("kitchen")));
		assertTrue(board.getRoomCard("kitchen") instanceof Room);
		assertFalse(board.getRoomCard("dagger") instanceof Room);
	}
	
	/**
	 * Initializes three mock players.
	 * */
	private ArrayList<Player> mockUpThreePlayers(Game game) throws InvalidAction { 
		Board board = game.getBoard();
		ArrayList<Player> players =  new ArrayList<Player>();
		HashMap<String, Character> characters = board.getAllCharactersWithName();
		players.add(new Player("Brian", characters.get("Colonel Mustard")));
		players.add(new Player("Vivienne", characters.get("Miss Scarlett")));
		players.add(new Player("Shaika", characters.get("Mrs White")));
		board.loadCards(players);
		return players;
	}

}
