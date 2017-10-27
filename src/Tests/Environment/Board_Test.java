package Tests.Environment;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import java.util.Set;

import org.junit.*;

import Environment.Board;
import Environment.Position;
import Game.Game;
import Game.Game.InvalidAction;
import Objects.*;
import Objects.Character;

public class Board_Test {

	/**
	 * Test to see if the given room name is equals to the Room objects
	 * that available on the board. It should be ignore uppercase and lowercase.
	 * */
	@Test
	public void testRoomOnBoard() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		assertEquals(board.getRoom("Dining Room").getName(), "Dining Room");
		assertEquals(board.getRoom("KITCHEN").getName(), "Kitchen");
		assertEquals(board.getRoom("Billiard Room").getName(), "Billiard Room");
		assertEquals(board.getRoom("Dining Room").getName(), "Dining Room");
		assertEquals(board.getRoom("Hall").getName(), "Hall");
		assertEquals(board.getRoom("Study").getName(), "Study");
		assertEquals(board.getRoom("Ballroom").getName(), "Ballroom");
		assertEquals(board.getRoom("Lounge").getName(), "Lounge");
		assertEquals(board.getRoom("Conservatory").getName(), "Conservatory");
		assertEquals(board.getRoom("garden"), null);
	}

	/**
	 * Test if the number of items on board is correct.
	 * 6 characters, 6 weapons, 9 rooms and 21 Cluedo cards.
	 * */
	@Test
	public void testNumberOfItems() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		assertEquals(board.getAllCharacters().size(), 6);
		assertEquals(board.getAllRooms().size(), 9);
		assertEquals(board.getAllWeapons().size(), 6);
		assertEquals(board.getDealCards().size(), 21);
		assertEquals(board.getSolution().size(), 3);
	}

	/**
	 * Test if the board returns a valid card of Cluedo
	 * */
	@Test
	public void testGetCardFromBoard() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		Character character =  new Character("Miss Scarlett", new Position(0, 0), (new ImageIcon("src/images/CharacCard_MS.png")));
		Room room = new Room("Dining Room", false, new ImageIcon("src/images/dining.png"));
		Weapon weapon = new Weapon("Dagger", room, (new ImageIcon("src/images/dagger.png")));
		assertEquals(board.getCharacterCard("miss scarlett").getName(), character.getName());
		assertEquals(board.getWeaponCard("dagger").getName(), weapon.getName());
		assertEquals(board.getRoomCard("Dining ROOM").getName(), room.getName());
		assertEquals(board.getRoomCard("toilet"), null);
		assertEquals(board.getRoomCard("Dagger"), null);
		assertEquals(board.getCharacterCard("Dagger"), null);
		assertEquals(board.getWeaponCard("knife"), null);
	}

	/**
	 * Test if the card is distributed evenly with 3 players without no refuted card
	 */
	@Test
	public void test1_DealCards() throws InvalidAction {
		Game game = new Game();
		ArrayList<Player> players = mockUpThreePlayers(game);
		for (Player p: players){
			assertEquals(p.getHand().size(), 6);
			assertEquals(p.getRefuted().size(), 0);
		}
	}

	/**
	 * Test if the card is distributed evenly with 4 players with 2 refuted cards.
	 * And the refuted cards added to all the player refuted's list.
	 *
	 */
	@Test
	public void test2_DealCards() throws InvalidAction {
		Game game = new Game();
		ArrayList<Player> players = mockUpFourPlayers(game);
		List<Card> extraCards = players.get(0).getRefuted();
		for (Player p: players){
			assertEquals(p.getHand().size(), 4);
			assertEquals(p.getRefuted().size(), 2);
			for (int i = 0; i < extraCards.size(); i++){
				assertEquals(p.getRefuted().get(i), extraCards.get(i));
			}
		}
	}

	/**
	 * Test if the four players' hand cards and refuted cards does not contain the solution cards.
	 *
	 */
	@Test
	public void test3_DealCards() throws InvalidAction {
		Game game = new Game();
		ArrayList<Player> players = mockUpFourPlayers(game);
		Set<Card> solutions = game.getBoard().getSolution();
		for (Player p: players){
			for (Card c: solutions){
				assertFalse(p.getHand().contains(c));
				assertFalse(p.getRefuted().contains(c));
			}

		}
	}

	/**
	 * Test if the four players' hand cards and refuted cards does not contain the solution cards.
	 *
	 */
	@Test
	public void testAllValidCharacters() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		HashMap<String, Character> characters = board.getAllCharactersWithName();
		assertTrue(characters.containsKey("Miss Scarlett"));
		assertTrue(characters.containsKey("Colonel Mustard"));
		assertTrue(characters.containsKey("Professor Plum"));
		assertTrue(characters.containsKey("Mrs White"));
		assertTrue(characters.containsKey("Reverend Green"));
		assertTrue(characters.containsKey("Mrs Peacock"));
		assertEquals(characters.get("Mrs Peacock").getPosition().column(), 24);
		assertEquals(characters.get("Mrs Peacock").getPosition(), new Position(6, 24));
		assertEquals(characters.get("Professor Plum").getPosition(), new Position(19, 24));
	}

	/**
	 * Test if the three players' hand cards and refuted cards does not contain the solution cards.
	 *
	 */
	@Test
	public void test4_DealCards() throws InvalidAction {
		Game game = new Game();
		ArrayList<Player> players = mockUpThreePlayers(game);
		Set<Card> solutions = game.getBoard().getSolution();
		for (Player p: players){
			for (Card c: solutions){
				assertFalse(p.getHand().contains(c));
				assertFalse(p.getRefuted().contains(c));
			}

		}
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

	/**
	 * Initializes three mock players.
	 * */
	private ArrayList<Player> mockUpFourPlayers(Game game) throws InvalidAction {
		Board board = game.getBoard();
		ArrayList<Player> players =  new ArrayList<Player>();
		HashMap<String, Character> characters = board.getAllCharactersWithName();
		players.add(new Player("Brian", characters.get("Colonel Mustard")));
		players.add(new Player("Vivienne", characters.get("Miss Scarlett")));
		players.add(new Player("Shaika", characters.get("Mrs White")));
		players.add(new Player("Angelica", characters.get("Professor Plum")));
		board.loadCards(players);
		return players;
	}




}
