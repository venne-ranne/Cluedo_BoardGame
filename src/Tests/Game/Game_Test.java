package Tests.Game;

import org.junit.*;

import Environment.Board;
import Environment.Position;
import Environment.RoomSquare;
import Environment.Board.Direction;
import Game.Game;
import Game.Game.InvalidAction;
import Game.Game.InvalidAction;
import Objects.Player;
import Objects.Room;
import Objects.Weapon;
import Objects.Card;
import Objects.Character;

import static org.junit.Assert.*;
import java.util.*;

public class Game_Test {
	Game game;
	ArrayList<Player> players = new ArrayList<Player>();
	Player p1;
	Player p2;
	Player p3;

	/**
	 * Test to see if suggestion method correctly rejects null arguments
	 */
	@Test
	public void nullArgumentsSuggestion() throws InvalidAction {
		Board board = setupMockGame();
		String input = "wwwww";
		game.movePlayer(p1, Direction.NORTH);
		try {
			game.makeSuggestion(p1, null, null);
			fail("Invalid arguments");
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Checks to see if suggestion method throws exception if player not in a
	 * room
	 */
	@Test
	public void invalidSuggestion_notInRoom() throws InvalidAction {
		setupMockGame();
		game.movePlayer(p1, Direction.NORTH);
		List<Card> suggestions = new ArrayList<Card>();
		suggestions.add(game.getBoard().getCharacterCard("Mrs White"));
		suggestions.add(game.getBoard().getWeaponCard("Dagger"));
		suggestions.add(game.getBoard().getRoomCard("Lounge"));
		try {
			game.makeSuggestion(p1, suggestions, game.getBoard().getCharacterCard("Mrs White"));
			fail("Player p1 isn't in the room yet!");
		} catch (Game.InvalidAction e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * checks if the character the player suggested has correctly moved to the
	 * suggested room
	 */
	@Test
	public void validSuggestion_moveCharacter() throws InvalidAction {
		setupMockGame();
		Room lounge = game.getBoard().getRoom("Lounge");
		lounge.setPlayer(p1);
		List<Card> suggestions = new ArrayList<Card>();
		suggestions.add(game.getBoard().getCharacterCard("Reverend Green"));
		suggestions.add(game.getBoard().getWeaponCard("Dagger"));
		suggestions.add(game.getBoard().getRoomCard("Lounge"));
		p1.setPosition(new Position(19, 6));
		game.makeSuggestion(p1, suggestions, game.getBoard().getCharacterCard("Reverend Green"));

		Character RGreen = game.getBoard().getAllCharactersWithName().get("Reverend Green");
		assertEquals(RGreen.getPosition(), p1.getPosition());
		assertTrue(p1.getSuggestions().contains("Reverend Green, Dagger, Lounge"));
		assertTrue(p1.getRefuted().contains(game.getBoard().getCharacterCard("Reverend Green")));
	}

	/**
	 * checks if the player connected to the character the suggesting player
	 * suggested has correctly moved to the suggested room
	 */
	@Test
	public void validSuggestion_movePlayer() throws InvalidAction {
		setupMockGame();
		Room lounge = game.getBoard().getRoom("Lounge");
		lounge.setPlayer(p1);
		List<Card> suggestions = new ArrayList<Card>();
		suggestions.add(game.getBoard().getCharacterCard("Mrs White"));
		suggestions.add(game.getBoard().getWeaponCard("Dagger"));
		suggestions.add(game.getBoard().getRoomCard("Lounge"));
		p1.setPosition(new Position(19, 6));
		game.makeSuggestion(p1, suggestions, game.getBoard().getWeaponCard("Dagger"));

		assertEquals(p2.getPosition(), p1.getPosition());
		assertTrue(p1.getSuggestions().contains("Mrs White, Dagger, Lounge"));
		assertTrue(p1.getRefuted().contains(game.getBoard().getWeaponCard("Dagger")));
	}

	/**
	 * checks if the checkAccusation() correctly identifies the solution
	 */
	@Test
	public void solution() throws InvalidAction {
		setupMockGame();
		ArrayList<Card> accusation = new ArrayList<Card>();
		for (Card card : this.game.getBoard().getSolution()) {
			accusation.add(card);
		}
		assertTrue(this.game.checkAccusation(accusation));
		try {
			accusation.remove(accusation.get(0));
			boolean result = this.game.checkAccusation(accusation);
			fail("Invalid accusation list");
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * checks if game correctly chooses the next refutable player going
	 * clockwise
	 */
	@Test
	public void checkRefutablePlayer() throws InvalidAction {
		setupMockGame();
		String input = "wwwww";
		Room lounge = game.getBoard().getRoom("Lounge");
		lounge.setPlayer(p3);
		List<Card> suggestions = new ArrayList<Card>();
		suggestions.add(game.getBoard().getCharacterCard("Mrs White"));
		suggestions.add(game.getBoard().getWeaponCard("Dagger"));
		suggestions.add(game.getBoard().getRoomCard("Lounge"));
		
		Player refutingPlayer = game.findNextRefutablePlayer(p1, suggestions);
		//since colonel mustard is the next player going clockwise.
		//if colonel mustard isn't the refuting player, make sure he doesn't have any refuting cards
		//so the game correctly passed him
		if(!refutingPlayer.equals(p3)){
			assertFalse(p3.getHand().contains(suggestions.get(0))
					|| p3.getHand().contains(suggestions.get(1))
					|| p3.getHand().contains(suggestions.get(2)));
		}
	}

	/**
	 * checks if game correctly chooses the refutable cards that match
	 * suggestion and are from refutable players hand
	 */
	@Test
	public void checkRefutableCards() throws InvalidAction {
		setupMockGame();
		String input = "wwwww";
		game.movePlayer(p1, Direction.NORTH);
		game.movePlayer(p1, Direction.NORTH);
		game.movePlayer(p1, Direction.NORTH);
		game.movePlayer(p1, Direction.NORTH);
		game.movePlayer(p1, Direction.NORTH);
		List<Card> suggestions = new ArrayList<Card>();
		suggestions.add(game.getBoard().getCharacterCard("Mrs White"));
		suggestions.add(game.getBoard().getWeaponCard("Dagger"));
		suggestions.add(game.getBoard().getRoomCard("Lounge"));
		
		Player refutingPlayer = game.findNextRefutablePlayer(p1, suggestions);
		List <Card> refutableCards = game.getRefutableCards(suggestions, refutingPlayer);
		
		//check refutable cards are from refuting player AND match suggestion
		for(Card card : refutableCards){
			assertTrue(refutingPlayer.getHand().contains(card));
			assertTrue(suggestions.contains(card));
		}		
	}
	
	/**
	 * checks if player correctly enters a room is they are in a room square (door)
	 * and is at a corridor otherwise
	 */
	@Test
	public void test1_playerEnterRoom() throws InvalidAction {
		Game game = new Game();
		Board board = game.getBoard();
		HashMap<String, Character> characs = board.getAllCharactersWithName();
		Character scarlett = characs.get("Miss Scarlett");
		Player player = new Player("Miss scarlet", scarlett);

		// test moving on corridor
		game.setDiceRoll(5);
		game.movePlayer(player, Direction.NORTH);
		game.movePlayer(player, Direction.NORTH);
		game.movePlayer(player, Direction.NORTH);
		Position newPos = new Position(21, 7);
		assertTrue(player.getPosition().equals(newPos));
		assertTrue(scarlett.getPosition().equals(newPos));

	}

	/**
	 * Test invalid move when player move into a wall
	 * @throws InvalidMove
	 */
	@Test
	public void test1_invalidMove() throws InvalidAction{
		Game game = new Game();
		try {
			Player player = setupMockPlayer(game);
			game.movePlayer(player, Direction.WEST);
			fail("There's a wall there, player should't be allow to move there");
		} catch (InvalidAction e){
			e.getMessage();
		}
	}

	/**
	 * Test invalid move after player lost the game.
	 * @throws InvalidMove
	 */
	@Test
	public void test2_invalidMove() throws InvalidAction {
		Game game = new Game();
		try {
			Player player = setupMockPlayer(game);
			player.eliminated(true);
			game.movePlayer(player, Direction.EAST);
			fail("Player should't be allow to move once lost the game.");
		} catch (InvalidAction e){
			e.getMessage();
		}
	}

	/**
	 * Test invalid move when player try to move on a same tile twice.
	 * @throws InvalidMove
	 */
	@Test
	public void test3_invalidMove() throws InvalidAction {
		Game game = new Game();
		try {
			Player player = setupMockPlayer(game);
			game.movePlayer(player, Direction.WEST);
			game.movePlayer(player, Direction.WEST);
			game.movePlayer(player, Direction.SOUTH);
			fail("Player should't be allow to move on same square twice");
		} catch (InvalidAction e){
			e.getMessage();
		}
	}
	
	/**
	 * Test invalid move when player try to enter invalid key input.
	 * @throws InvalidMove
	 */
	@Test
	public void test4_invalidMove() throws InvalidAction {
		Game game = new Game();
		try {
			Player player = setupMockPlayer(game);
			game.movePlayer(player, Direction.WEST);
			game.movePlayer(player, Direction.SOUTH);
			fail("Player shouldn't be allow to move on same square twice");
		} catch (InvalidAction e){
			e.getMessage();
		}
	}
	
	/**
	 * Test invalid move when player try to enter invalid key input.
	 * @throws InvalidMove
	 */
	@Test
	public void test5_invalidMove() throws InvalidAction {
		Game game = new Game();
		try {
			Player player = setupMockPlayer(game);
			game.movePlayer(player, Direction.EAST);
			fail("There's a wall there, player shouldn't be allow to move there.");
		} catch (InvalidAction e){
			e.getMessage();
		}
	}
	
	/**
	 * Test invalid move when player try to exit a room.
	 * @throws InvalidMove
	 */
	@Test
	public void test6_invalidMove() throws InvalidAction {
		Game game = new Game();
		try {
			Player player = setupMockPlayer(game);
			Room lounge = game.getBoard().getRoom("Lounge");
			lounge.setPlayer(player);
			game.movePlayer(player, Direction.EAST);
			//game.movePlayer(player, input.length(), input);
			//game.movePlayer(player, 2, "aa");
			fail("Invalid move. Player should enter the right direction for exiting a room");
		} catch (InvalidAction e){
			e.getMessage();
		}
	}
	
	/**
	 * Test when a player enter a room using secretPassage
	 * @throws InvalidAction 
	 */
	@Test
	public void test1_playerEnterSecretPassage() throws InvalidAction {
		Game game = new Game();
		Player p = setupMockPlayer(game);
		Room lounge = game.getBoard().getRoom("Lounge");
		lounge.setPlayer(p);
		assertTrue(lounge.hasPlayers());
		assertTrue(lounge.hasSecretPassage());
	}

	private Player setupMockPlayer(Game game) throws InvalidAction {
		Board board = game.getBoard();
		HashMap<String, Character> characs = board.getAllCharactersWithName();
		Character scarlett = characs.get("Miss Scarlett");
		return new Player("Miss scarlet", scarlett);
	}

	/**
	 * Initializes a mock game with the minimum ammount of players
	 */
	private Board setupMockGame() throws InvalidAction {
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
		return board;
	}

}
