package Tests.Objects;

import org.junit.*;

import Environment.Board;
import Environment.Position;
import Game.Game;
import Game.Game.InvalidAction;
import Objects.Player;
import Objects.Weapon;
import Objects.Card;
import Objects.Character;

import static org.junit.Assert.*;
import java.util.*;

import javax.swing.ImageIcon;

public class Player_Test {
	private Game game;
	private Player player = null;	
	ArrayList<Player> players = new ArrayList<Player>();
	Player p1;
	Player p2;
	Player p3;

	// check constructor + code
	/**
	 * Check that class correctly passes valid arguments for constructors
	 * @throws InvalidAction 
	 */
	@Test
	public void testValidConstructor() throws InvalidAction {
		this.player = new Player("Bob", new Character("Mrs White", new Position(0, 9), (new ImageIcon("src/images/CharacCard_MW.png"))));
		assertEquals(this.player.getName(), "Bob");
		assertFalse(this.player.isEliminated());
		assertEquals(this.player.getCharacter().getName(), "Mrs White");
		assertEquals(this.player.getCode(), this.player.getCharacter().getCode());
	}


	/**
	 * @throws InvalidAction 
	 */
	@Test
	public void testPositions() throws InvalidAction {
		this.player = new Player("Bob", new Character("Mrs White", new Position(0, 9), (new ImageIcon("src/images/CharacCard_MW.png"))));
		try {
			this.player.setPosition(null);
			fail("Player shouldn't be set to invalid position");
		} catch (InvalidAction e) {
			System.out.println(e.getMessage());
		}
	}

	// check hand + suggested + refuted
	/**
	 * checks if a players have the right amount of cards distributed 
	 * Checks if a suggestion and refuted card is correctly added to players lists
	 * @throws InvalidAction 
	 */
	@Test
	public void testCards() throws InvalidAction {
		setupMockGame();
		assertEquals(p1.getHand().size(), 6);
		assertEquals(p2.getHand().size(), 6);
		assertEquals(p3.getHand().size(), 6);
		
		String input = "wwwwwa";
		p1.setPosition(new Position(19, 6));
		List<Card> suggestions = new ArrayList<Card>();
		suggestions.add(game.getBoard().getCharacterCard("Reverend Green"));
		suggestions.add(game.getBoard().getWeaponCard("Dagger"));
		suggestions.add(game.getBoard().getRoomCard("Lounge"));
		game.makeSuggestion(p1, suggestions, game.getBoard().getCharacterCard("Reverend Green"));
		
		assertTrue(p1.getSuggestions().contains("Reverend Green, Dagger, Lounge"));
		assertTrue(p1.getRefuted().contains(game.getBoard().getCharacterCard("Reverend Green")));
	}
	
	//check eliminated
	/**
	 * checks if a player is successfully eliminated
	 * @throws InvalidAction 
	 */
	@Test
	public void testEliminated() throws InvalidAction {
		setupMockGame();
		assertFalse(p1.isEliminated());
		this.game.eliminated(p1);
		assertTrue(p1.isEliminated());
	}
	
	
	/**
	 * Initializes a mock game with the minimum ammount of players
	 */
	private void setupMockGame() throws InvalidAction {
		this.game = new Game();
		Board board = this.game.getBoard();
		this.players = new ArrayList<Player>();
		this.p1 = new Player("Bob", board.getAllCharactersWithName().get("Miss Scarlett"));
		this.p2 = new Player("Sally", board.getAllCharactersWithName().get("Mrs White"));
		this.p3 = new Player("Paul", board.getAllCharactersWithName().get("Colonel Mustard"));
		this.players.add(p1);
		this.players.add(p2);
		this.players.add(p3);
		this.game.setAllPlayers(this.players);
		game.getBoard().loadCards(this.players);
	}
}
