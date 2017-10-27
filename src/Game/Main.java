package Game;

import java.io.IOException;
import java.util.ArrayList;

import Environment.Board;
import Environment.RoomSquare;
import Environment.Square;
import Game.Game.InvalidAction;
import Interface.CluedoFrame;
import Objects.Player;

public class Main {
	
	public static void main(String[] args) throws InvalidAction, IOException, InterruptedException {
		CluedoFrame frame = new CluedoFrame();
		while (1 == 1){	
				if (frame.game() != null && !frame.game().isGameOver() && frame.getCurrentPlayer() == null){
					Game game = frame.game();
					ArrayList<Player> players = game.getAllPlayers();
					Board board = game.getBoard();
					if (players.isEmpty()){
						System.out.println();
						players = game.getAllPlayers();
					}
					
					while (!game.isGameOver() && !players.isEmpty()){
							for (Player player : players){
								boolean firstTime = true;
									if (!player.isEliminated()) {
										frame.setCurrentPlayer(player);
										while (frame.isEndTurn() == false){
											if (frame.isMoveTurn() == true && !game.isDiceRoll() && firstTime){
												//if in room
												Square sq = board.getSquareAt(player.getPosition());
												if (sq instanceof RoomSquare) {
													RoomSquare room = (RoomSquare) sq;				
													//if room has secret passage
													if (room.hasSecretPassage()) {
														frame.promptForPassage(room.getSecretPassage());	//ask player if they wish to use passage													
														if (frame.hasUsedPassage() == true) 	//if they DID use secret passage then its NOT their first time							
															 frame.setMessage("Please suggest, make an accusation or finish your turn."); //update message
													} 	
													firstTime = false;
												} 			
											}
											Thread.sleep(1000);
											if (game.isGameOver()) break;
									    }
										
									}
									if (game.isGameOver()) break;
						    }
							
					}
					
				
				} 
		}
	
		
	}
}
