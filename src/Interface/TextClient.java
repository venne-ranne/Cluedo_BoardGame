/*package Interface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Environment.Board;
import Environment.Position;
import Environment.RoomSquare;
import Environment.Square;
import Game.Game;
import Game.Game.InvalidAction;
import Objects.Card;
import Objects.Character;
import Objects.Player;
import Objects.Room;
import Objects.Weapon;

*//**
 * Text_Client is the main class that the Cluedo program is executed in. 
 * It sets up a new game object for 3-6 players to play on a text interface. * 
 * It contains the methods that involve communication with the player ie, asking for actions, cards, direction to move
 * 
 * @author Vivienne Yapp and Shaika Khan
 * @version 1.0
 * *//*
public class Text_Client {

	*//**
	 * Get string from System.in
	 * @param msg the message to print out
	 * @return the user input
	 *//*
	private static String inputString(String msg) {
		System.out.print(msg + " ");
		while (true) {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			try {
				return input.readLine();
			} catch (IOException e) {
				System.out.println("I/O Error ... please try again!");
			}
		}
	}

	*//**
	 * Get char from System.in
	 * @param msg the message to print out
	 * @return an int of the Character
	 *//*
	private static int inputChar(String msg) {
		System.out.print(msg + " ");
		while (true) {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			try {
				return input.read();
			} catch (IOException e) {
				System.out.println("I/O Error ... please try again!");
			}
		}
	}

	*//**
	 * Get number from System.in
	 * @param msg the message to print out
	 * @return an integer number
	 *//*
	private static int inputNumber(String msg) {
		System.out.print(msg + " ");
		while (true) {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			try {
				String v = input.readLine();
				if (v == null) {
					throw new IOException();
				}
				while (!v.matches("[0-9]")) {
					System.out.print("Invalid input! Please enter digits only!");
					v = input.readLine();
				}
				return Integer.parseInt(v);
			} catch (IOException e) {
				System.out.println("Please enter a number!");
			}
		}
	}

	*//**
	 * Input player details from System.in and setup the players and characters.
	 * @param nplayer number of players
	 * @param game the current game
	 * @return a list of new players
	 * @throws InvalidAction if the user input the wrong arguments
	 *//*
	private static ArrayList<Player> inputPlayers(int nplayers, Game game) throws InvalidAction {
		// set up the characters
		HashMap<String, Character> characters = game.getBoard().getAllCharactersWithName();

		// now, input data
		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<String> chosen = new ArrayList<String>();

		for (int i = 0; i != nplayers; ++i) {
			String name = inputString("Player #" + (i + 1) + " name?");
			String tokenName = inputString("Player #" + (i + 1) + " character?").toLowerCase();
			while (!game.isValidCharacter(tokenName) || chosen.contains(tokenName)) {
				System.out.print("Invalid character!  Must be one of: ");
				for (String t : characters.keySet()) {
					if (!chosen.contains(t.toLowerCase())) {
						System.out.print(t + ", ");
					}
				}
				System.out.println();
				tokenName = inputString("Player #" + (i + 1) + " character?").toLowerCase();
			}
			players.add(new Player(name, (Character) game.getBoard().getCharacterCard(tokenName)));
			chosen.add(tokenName);
		}
		return players;
	}

	*//**
	 * Moves the player according to the directions and he/she must only entered (WASD)
	 * @param player the current player
	 * @param numRolls the number of dice rolls
	 * @param game the current game
	 * @return the square based on the new position of the player
	 * @throws InvalidAction if the user input the wrong arguments or invalid movement.
	 * *//*
	private static Square movePlayer(Player player, int numRolls, Game game) throws InvalidAction {
		while (true) {
			try {
				String input = inputString("Enter direction to move: (use WASD keys only)");
				//game.movePlayer(player, numRolls, input);
				Position pos = player.getPosition();
				Board board = game.getBoard();
				Square sq = board.getSquareAt(pos);
				if (sq instanceof RoomSquare) {
					System.out.println("\n" + player.getCharacter().getName() + " has entered room : " + sq.getName());
				}
				return sq;
			} catch (Game.InvalidAction e) {
				System.out.println(e.getMessage());
				}
		}
	}

	*//**
	 * Moves player through secret passage between corner rooms
	 * Conservatory <===> Lounge | Kitchen <===> Study
	 * @param player the current player
	 * @param game the current game
	 * *//*
	private static void useSecretPassage(Player player, Game game) throws InvalidAction {
		game.useSecretPassage(player);
		Position pos = player.getPosition();
		Board board = game.getBoard();
		Square sq = board.getSquareAt(pos);
		System.out.println("\n" + player.getCharacter().getName() + " has entered room : " + sq.getName());
	}

	*//**
	 * lets player make a suggestion. 
	 * Checks if player is not eliminated and in a room and hasn't previously made a suggestion the same turn.
	 * Prints to the player their suggestion refuted any card and if so which cards
	 * @param player the current player
	 * @param game the current game
	 * @throws InvalidAction if the given card is not valid
	 *//*
	private static void makeSuggestion(Player player, Game game) throws InvalidAction {
		Board board = game.getBoard();
		List<Card> suggestionList = new ArrayList<Card>();

		if (player == null) {
			throw new IllegalArgumentException("Invalid player");
		}
		if (game == null) {
			throw new IllegalArgumentException("Invalid game");
		}
		if (player.isEliminated()) {
			throw new Game.InvalidAction("Eliminated player can't make suggestion");
		}
		if (!(game.getBoard().getSquareAt(player.getPosition()) instanceof RoomSquare)) {
			throw new Game.InvalidAction("Player not in a room to make suggestion.");
		}
		showNotes(player);

		// add characterCard
		String character = inputString("Which character would you like to suggest: ");
		Card card = board.getCharacterCard(character);
		while (card == null) {
			String printInvalidCharacter = "Invalid character!  Must be one of: ";
			for (String t : board.getAllCharactersWithName().keySet()) {
				printInvalidCharacter += (t + ", ");
			}
			printInvalidCharacter += "\nWhich character would you like to suggest: ";
			character = inputString(printInvalidCharacter);
			card = board.getCharacterCard(character);
		}
		suggestionList.add(card);

		// add weaponCard
		String weapon = inputString("Which weapon would you like to suggest: ");
		card = board.getWeaponCard(weapon);
		while (card == null) {
			String printInvalidWeapon = "Invalid Weapon!  Must be one of: ";
			for (Weapon w : board.getAllWeapons()) {
				printInvalidWeapon += (w.getName() + ", ");
			}
			printInvalidWeapon += "\nWhich weapon would you like to suggest: ";
			weapon = inputString(printInvalidWeapon);
			card = board.getWeaponCard(weapon);
		}
		suggestionList.add(card);

		// add roomCard
		RoomSquare roomSq = (RoomSquare) board.getSquareAt(player.getPosition());
		card = board.getRoomCard(roomSq.getName());
		suggestionList.add(card);

		System.out.println("You have suggested the murder was commited by: " + suggestionList.get(0).getName()
				+ " with the weapon " + suggestionList.get(1).getName() + " in room "
				+ suggestionList.get(2).getName());

		// get refuting card
		Card refuted = getRefutableCard(player, suggestionList, game);

		if (refuted == null) {
			System.out.println("Your suggestion has refuted no cards.");
		} else {
			System.out.println("Your suggestion has refuted the card: " + refuted.getName());
		}
		game.makeSuggestion(player, suggestionList, refuted);
	}

	*//**
	 * Lets player make Accusation in the condition that they aren't eliminated.
	 * Prints to the player if they were correct or not and thus won the game or eliminated.
	 * @param player the current player
	 * @param game the current game
	 * @throws InvalidAction if the player already eliminated
	 * *//*
	private static void makeAccusation(Player player, Game game) throws InvalidAction {
		// check for invalid arguments
		if (player == null) {
			throw new InvalidAction("Invalid player");
		}
		if (game == null) {
			throw new InvalidAction("Invalid game");
		}
		if (player.isEliminated()) {
			throw new InvalidAction("Eliminated player can't make accusation");
		}

		showNotes(player);

		Board board = game.getBoard();
		List<Card> accusationtionList = new ArrayList<Card>();

		// add characterCard
		String character = inputString("Which character would you like to accuse: ");
		Card card = board.getCharacterCard(character);
		while (card == null) {
			String printInvalidCharacter = "Invalid character!  Must be one of: ";
			for (String t : board.getAllCharactersWithName().keySet()) {
				printInvalidCharacter += (t + ", ");
			}
			printInvalidCharacter += "\nWhich character would you like to accuse: ";
			character = inputString(printInvalidCharacter);
			card = board.getCharacterCard(character);
		}
		accusationtionList.add(card);

		// add weaponCard
		String weapon = inputString("Which weapon would you like to accuse: ");
		card = board.getWeaponCard(weapon);
		while (card == null) {
			String printInvalidWeapon = "Invalid Weapon!  Must be one of: ";
			for (Weapon w : board.getAllWeapons()) {
				printInvalidWeapon += (w.getName() + ", ");
			}
			printInvalidWeapon += "\nWhich weapon would you like to accuse: ";
			weapon = inputString(printInvalidWeapon);
			card = board.getWeaponCard(weapon);
		}
		accusationtionList.add(card);

		// add weaponCard
		String room = inputString("Which room would you like to accuse: ");
		card = board.getRoomCard(room);
		while (card == null) {
			String printInvalidRoom = "Invalid room!  Must be one of: ";
			for (Room r : board.getAllRooms()) {
				printInvalidRoom += (r.getName() + ", ");
			}
			printInvalidRoom += "\nWhich room would you like to accuse: ";
			room = inputString(printInvalidRoom);
			card = board.getRoomCard(room);
		}
		accusationtionList.add(card);

		System.out.println("You have accused that the murder was commited by: " + accusationtionList.get(0).getName()
				+ " with the weapon " + accusationtionList.get(1).getName() + " in room "
				+ accusationtionList.get(2).getName());

		boolean accusation = game.checkAccusation(accusationtionList);
		if (accusation == true) {
			System.out.println("Your accusation was CORRECT.\nYou have WON the game!");
			endGame(player, game, accusationtionList);
		} else {
			System.out.println(
					"Your accusation was INCORRECT. You are hence eliminated from the game. You may however continue refuting");
			game.eliminated(player);
		}
		System.out.println("");

	}

	*//**
	 * Confirms if the player is the player game is asking for.
	 * @param player the valid player
	 * @return true if it is right player, otherwise false
	 * @throws InvalidAction if the user input the wrong arguments
	 * *//*
	private static boolean confirmPlayer(Player player) {
		String isPlayer = inputString("Please confirm if you are player " + player.getName() + ". [Y/N]: ");
		while (!isPlayer.equalsIgnoreCase("Y")) {
			if (isPlayer.equalsIgnoreCase("N")) {
				isPlayer = inputString("Please call player " + player.getName() + ".\nPlease confirm if you are player "
						+ player.getName() + ". [Y/N]: ");
			} else {
				isPlayer = inputString(
						"Invalid answer. Please confirm if you are player " + player.getName() + ". [Y/N]: ");
			}
		}
		if (isPlayer.equalsIgnoreCase("Y")) {
			return true;
		}
		return false;
	}

	*//**
	 * Prints message if the Game if over and sets game to be over so it doesn't continue.
	 * @param player the winning player
	 * @param game the current game
	 * @param solution the list of solution
	 * *//*
	private static void endGame(Player player, Game game, List<Card> solution) {
		String toPrint = "\n************************************\n\n";
		toPrint += "G A M E   O V E R!!!";
		toPrint += "\nWinner: " + player.getName() + "\n";
		toPrint += "Murder was commited by: " + solution.get(0).getName() + " with weapon " + solution.get(1).getName()
				+ " in room " + solution.get(2).getName() + "\n\n";
		toPrint += "************************************\n";
		System.out.println(toPrint);
		game.endGame();
	}

	*//**
	 * Returns refutable card if there are any. Checks from player going clockwise from suggester.
	 * @param player the current player
	 * @param suggestedCards the list of suggestion cards
	 * @param game the current game
	 * @return Card refuted card OR null
	 * @throws InvalidAction
	 * *//*
	private static Card getRefutableCard(Player player, List<Card> suggestionList, Game game) throws InvalidAction {
		// call next refutable player
		Player nextPlayer = game.findNextRefutablePlayer(player, suggestionList);
		if (nextPlayer == null) {
			return null;
		}
		clearScreen();
		System.out.println(
				"Player " + nextPlayer.getName() + " has a refutable card. Please call player " + nextPlayer.getName());
		boolean isNextPlayer = confirmPlayer(nextPlayer);
		if (isNextPlayer) {
			System.out.println("Player " + player.getName() + " has suggested the following cards: "
					+ suggestionList.get(0).getName() + ", " + suggestionList.get(1).getName() + ", "
					+ suggestionList.get(2).getName());
			List<Card> refutableCards = game.getRefutableCards(suggestionList, nextPlayer);
			// if only 1 card
			if (refutableCards.size() == 1) {
				clearScreen();
				System.out.println("You have the refutable card: " + refutableCards.get(0).getName()
						+ " which is going to be shown to the suggested player.");
				// call back current player				
				System.out.println("Please call back player " + player.getName());
				boolean isPlayer = confirmPlayer(player);
				if (isPlayer) {
					return refutableCards.get(0);
				}
				// if more than 1 card
			} else if (refutableCards.size() > 1) {
				return pickCard(player, refutableCards, game);
			}
		}
		throw new InvalidAction("Invalid player confirmation.");
	}

	*//**
	 * Allows a refuting player to pick the card they want to show to the suggester, if they have more than 1 refuting card
	 * @param refutingPlayer the player who has a card from the suggestion
	 * @param refutableCards the list of refutable cards
	 * @param game the current game
	 * *//*
	private static Card pickCard(Player player, List<Card> refutableCards, Game game) throws InvalidAction {
		String toPrint = "";
		toPrint += "You have the refutable cards: ";
		for (Card card : refutableCards) {
			toPrint += card.getName() + ", ";
		}
		toPrint += "please choose a card to show the suggester.\n";
		String chosen = inputString(toPrint);
		Card chosenCard = game.getBoard().getCharacterCard(chosen);
		if (chosenCard == null) {
			chosenCard = game.getBoard().getWeaponCard(chosen);
		}
		if (chosenCard == null) {
			chosenCard = game.getBoard().getRoomCard(chosen);
		}
		while (!refutableCards.contains(chosenCard)) {// keep asking for card if
														// they enter an card
														// not in the list
			chosen = inputString("Invalid answer. Please choose a card to how the suggester.\n");
			chosenCard = game.getBoard().getCharacterCard(chosen);
			if (chosenCard == null) {
				chosenCard = game.getBoard().getWeaponCard(chosen);
			}
			if (chosenCard == null) {
				chosenCard = game.getBoard().getRoomCard(chosen);
			}
		}
		if (chosenCard != null) {
			clearScreen();
			System.out.println("You have chosen the card: " + chosenCard.getName()
					+ ", which is going to be shown to the suggested player.");
			// call back current player			
			System.out.println("Please call back player " + player.getName());
			boolean isPlayer = confirmPlayer(player);
			if (isPlayer) {
				return chosenCard;
			}
		}
		throw new InvalidAction("Invalid chosen card");
	}

	*//**
	 * Prints the players notes.
	 * @param player - the current player
	 * *//*
	private static void showNotes(Player player) {
		System.out.println(player.displayNotes());
	}
	
	*//**
	 * Prints blank spaces to clear immediate screen so next player cannot see the previous players information 
	 * so directly
	 * *//*
	public static void clearScreen(){
		for(int i = 0; i<100; i++){
			System.out.println();
		}System.out.println("PLEASE DO NOT SCROLL UP AND PEAK AT PREVIOUS PLAYERS INFORMATION!!! PLAY FAIRLY! :)");
	}

	*//**
	 * Displays players options at their position (suggest/accuse, view notes, view map, end turn)
	 * will continue asking until player gives valid answer.
	 * @param player the current player
	 * @param game the current game
	 * @param isPlayerInRoom true if the player is in a room, otherwise false.
	 * *//*
	private static void playerOptions(Player player, Game game, boolean isPlayerInRoom) {
		System.out.println("Options for " + player.getName() + ":");
		System.out.println("* Making a suggestion or accusation");
		System.out.println("* Detective Notes");
		System.out.println("* Game Map");
		System.out.println("* End turn");
		Boolean suggested = false;
		while (!game.gameOver()) {
			try {
				String cmd = inputString("[suggest/accuse/note/map/end]");
				if (cmd.equalsIgnoreCase("end")) {
					clearScreen();
					return;
				} else if (cmd.equalsIgnoreCase("suggest")) {
					if (suggested)
						throw new InvalidAction(
								"You only allowed to make one suggestion per round." + " Enter 'end' to finish turn.");
					else if (player.isEliminated())
						throw new InvalidAction(
								"Sorry... You've lost the game, therefore you cannot make other suggestions."
										+ " Enter 'end' to finish turn.");
					else if (!isPlayerInRoom)
						throw new InvalidAction("Opps sorry... You're only allowed to make suggestion in a room");
					makeSuggestion(player, game);
					suggested = true;
				} else if (cmd.equalsIgnoreCase("accuse")) {
					if (player.isEliminated())
						throw new InvalidAction(
								"Sorry... You've lost the game, therefore you cannot make other accusation."
										+ " Enter 'end' to finish turn.");
					makeAccusation(player, game);
				} else if (cmd.equalsIgnoreCase("note")) {
					showNotes(player);
				} else if (cmd.equalsIgnoreCase("map")) {
					game.getBoard().printMap();
					game.getBoard().printPlayersPosition();
				} else {
					System.out.println("Invalid command.  Enter 'end' to finish turn.");
				}
			} catch (Game.InvalidAction e) {
				System.out.println(e.getMessage());
			}
		}
	}

	*//**
	 * The main method that starts a new game.
	 * @param args args
	 * @throws InvalidAction if the move/action is invalid or user input the wrong arguments
	 * @throws InterruptedException throws interreptedException
	 *//*
	public static void main(String args[]) throws InvalidAction, InterruptedException {
		Game game = new Game();
		Board board = game.getBoard();

		System.out.println("*************************** Cluedo Version 1.0 ***************************");
		System.out.println("By Vivienne Yapp and Shaika Khan, 2016");

		// input player info
		int nplayers = inputNumber("How many players? At least 3 and MAX 6 players.");

		// if the number player is less than 3 and more than 6
		while (nplayers < 3 || nplayers > 6)
			nplayers = inputNumber("Enter number of players again. 3-6 players.");

		// entering the players information
		// then, load the cards to the players' hand
		ArrayList<Player> players = inputPlayers(nplayers, game);
		game.setAllPlayers(players);
		board.loadCards(players);

		Thread.sleep(1000);
		// now, begin the game!
		int round = 1;
		Random dice = new Random();

		while (!game.gameOver()) { // loop forever
			System.out.println("\n********************************************************************************");
			System.out.println(
					"************************************ ROUND " + round + " ***********************************");
			System.out.println("********************************************************************************");
			int roll = dice.nextInt(10) + 2;
			for (Player player : players) {
				Thread.sleep(200);
				boolean firstTime = true;
				boolean secretPassage = false;

				if (!player.isEliminated()) {
					game.getBoard().printMap();
					board.printPlayersPosition();
					Square sq = board.getSquareAt(player.getPosition());

					// if the player is in a room and the room has a secret
					// passage
					// then give the player an option whether he/she want to use
					// it
					if (sq instanceof RoomSquare) {
						RoomSquare room = (RoomSquare) sq;
						secretPassage = room.hasSecretPassage();
						if (secretPassage) {
							System.out.println(
									"Now its " + player.getName() + " turns. You are currently in " + room.getName());
							char input = 'F';
							while (true) {
								input = (char) inputChar("Do you wish to use the secret passage to "
										+ room.getSecretPassage() + "? [Y/N]");
								if (input == 'Y' || input == 'N' || input == 'y' || input == 'n')
									break;
							}

							// if the player want to use the secret passage
							// then auto go to player options
							if (input == 'Y' || input == 'y') {
								useSecretPassage(player, game);
								playerOptions(player, game, true);
								firstTime = false;
							}
						}
					}

					// if the player in the corridor square
					// then auto roll the dice, if the player enter a room
					// then continue to player options, otherwise next player
					// turn
					if (firstTime) {
						roll = dice.nextInt(10) + 2;
						Character character = player.getCharacter();
						if (secretPassage) {
							System.out.println("You have roll a " + roll + ". " + "Where do you want to move "
									+ character.getName() + " (Code: " + character.getCode() + ") ?");
						} else {
							System.out.println("Now its " + player.getName() + " turns. \nYou have roll a " + roll
									+ ". " + "Where do you want to move " + character.getName() + " (Code: "
									+ character.getCode() + ") ?");
						}
						Square square = movePlayer(player, roll, game);
						firstTime = false;
						if (square instanceof RoomSquare)
							playerOptions(player, game, true);
						else
							playerOptions(player, game, false);
					}

				}

			}
			round++; // the forever loop
		}

	}

}
*/