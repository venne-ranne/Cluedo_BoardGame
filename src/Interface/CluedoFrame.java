package Interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;

import Environment.Board.Direction;
import Environment.RoomSquare;
import Environment.Square;
import Game.Game;
import Game.Game.InvalidAction;
import Objects.Player;
import Objects.Room;
import Objects.Weapon;
import Objects.Card;
import Objects.Character;

/**
 * The Cluedo Frame class is used to setup the graphical user interface and handle different events caused by the
 * players actions through out the game.
 * The Cluedo interacts with mostly the game, board and canvas classes.
 * */
public class CluedoFrame extends JFrame implements ActionListener, java.awt.event.KeyListener, MouseMotionListener {

	private Game game;
	private JMenuBar menuBar;
	private JMenu File, Options, Help;
	private JMenuItem fileNewGame, optionsControls, optionsInstructions, helpAbout;
	private CluedoBoardCanvas canvas;
	private JPanel boardPanel;
	private JTabbedPane controls;
	private Player currentPlayer;
	private JButton endTurn;

	private JPanel main, handAndNotes;

	// Data that needs to be updated
	private JLabel mainInfo = new JLabel();
	private JTextArea errorsAndInstructions;
	private JTextArea notes;
	private boolean usedPassage = false;
	private boolean hasSuggested = false;
	private boolean moveTurn;
	private boolean endTurnStatus;

	//for suggestions/accusations
	private List<Card> suggestions;
	private Card refutedCard = null;
	private List<Card> accusation;
	private Card characChosen = null;
	private Card weapChosen = null;
	private Card roomChosen = null;

	public CluedoFrame() {
		super("Cluedo");
		currentPlayer = null;
		game = null;
		moveTurn = false;
		endTurnStatus = false;
		setSize(1100, 800);
		setResizable(false); // prevent us from being resizeable
		setVisible(true); // make sure we are visible!
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (this.getWidth() / 2);
		int yPos = (dim.height / 2) - (this.getHeight() / 2);
		this.setLocation(xPos, yPos);
		this.setTitle("Cluedo Board Game");

		// menu bar
		menuBar = loadMenu();
		this.setJMenuBar(menuBar);

		canvas = new CluedoBoardCanvas(); // create canvas
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		boardPanel = new JPanel();
		boardPanel.setPreferredSize(new Dimension(700, 800));
		boardPanel.add(canvas);
		this.add(canvas, BorderLayout.CENTER);
		boardPanel.setVisible(true);

		// controls panel
		controls = new JTabbedPane();
		controls.setForeground(Color.WHITE);
		this.add(controls, BorderLayout.EAST);
		controls.setPreferredSize(new Dimension(300, 700));
		controls.setBackground(new Color(30, 82, 102));
		controls.setVisible(true);
		addControlTabs();
		welcome();
		addKeyListener(this);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
		      public boolean dispatchKeyEvent(KeyEvent e) {
		          if (e.getID() == KeyEvent.KEY_PRESSED) {
		              keyReleased(e);
		          }
		          return false;
		}});

	}

	/**
	 *
	 * Load the components of the menubar such as file, options and help
	 * Adds listeners for short cut keys
	 * @return JMenuBar
	 *
	 * */
	private JMenuBar loadMenu() {
		JMenuBar bar = new JMenuBar();
		this.File = new JMenu("File");
		this.Options = new JMenu("Options");
		this.Help = new JMenu("Help");

		this.fileNewGame = new JMenuItem("New Game");
		this.fileNewGame.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
		this.fileNewGame.addActionListener(this);
		this.File.add(this.fileNewGame);

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		exitItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));
		this.File.add(exitItem);

		this.optionsControls = new JMenuItem("Controls");

		this.optionsControls.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
		this.optionsControls.addActionListener(this);
		this.Options.add(this.optionsControls);

		this.optionsInstructions = new JMenuItem("How to Play");
		this.optionsInstructions.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK));
		this.optionsInstructions.addActionListener(this);
		this.Options.add(optionsInstructions);

		this.helpAbout = new JMenuItem("About");
		this.helpAbout.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK));this.helpAbout.addActionListener(this);
		this.Help.add(helpAbout);

		bar.add(File);
		bar.add(Options);
		bar.add(Help);
		bar.addKeyListener(this);
		return bar;
	}

	/**
	 *
	 * Loads the tabs for the tabbed pane use as teh control panel.
	 *
	 * */
	private void addControlTabs() {
		// Main Tab
		main = new JPanel();
		main.setBackground(new Color(30, 82, 102)); // yellow-beige: new
													// Color(247, 241, 151)
		main.setVisible(true);
		setupMainTab(main);
		// Hand Tab
		handAndNotes = new JPanel();
		handAndNotes.setBackground(new Color(30, 82, 102));
		handAndNotes.setVisible(true);
		handAndNotes.setLayout(new BoxLayout(handAndNotes, BoxLayout.Y_AXIS));
		setupHandAndNotesTab(handAndNotes);

		controls.addTab("Main", main);
		controls.addTab("Dectective Pad", handAndNotes);
	}

	/**
	 *
	 * Set up side control panel - main tab
	 * Main tab hold information about the player and has teh controls to move around the board
	 * There is also a messages bar to give the player helpful instructions.
	 * @param JPanel main to customize and add to
	 *
	 * */
	private void setupMainTab(JPanel main) {
		// info panel
		JPanel info = new JPanel();
		info.setOpaque(false);
		info.setPreferredSize(new Dimension(280, 145));//
		mainInfo.setForeground(Color.WHITE);
		info.add(mainInfo);
		main.add(info);

		// dice panel
		JPanel dice = new JPanel();
		dice.setOpaque(false);
		dice.setPreferredSize(new Dimension(280, 150));
		ImageIcon img2 = new ImageIcon("src/images/dice.png");
		JButton diceButton = new JButton("Roll Dice");
		diceButton.setActionCommand("Roll");
		diceButton.addActionListener(this);
		diceButton.setIcon(img2);
		diceButton.setHorizontalTextPosition(AbstractButton.CENTER);
		diceButton.setVerticalTextPosition(AbstractButton.BOTTOM);
		dice.add(diceButton, BorderLayout.CENTER);
		main.add(dice);

		// move panel
		JPanel movements = movementPanel();
		main.add(movements);

		// message panel
		JPanel message = new JPanel();
		message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
		message.setBackground(Color.WHITE);
		message.setPreferredSize(new Dimension(280, 70));
		this.errorsAndInstructions = new JTextArea("Messages:");
		this.errorsAndInstructions.setEditable(false);
		this.errorsAndInstructions.setBackground(Color.WHITE);
		this.errorsAndInstructions.setPreferredSize(new Dimension(275, 60));
		this.errorsAndInstructions.setLineWrap(true);
		this.errorsAndInstructions.setWrapStyleWord(true);
		message.add(new JScrollPane(this.errorsAndInstructions));
		main.add(message);

		JPanel end = new JPanel();
		endTurn = new JButton("End Turn");
		endTurn.addActionListener(this);
		endTurn.requestFocusInWindow();
		end.setOpaque(false);
		end.add(endTurn, BorderLayout.CENTER);
		main.add(end);
	}

	/**
	 *
	 * Sets up the components of the movement panel ie the buttons
	 * @return JPanel movement
	 *
	 * */
	private JPanel movementPanel() {
		JPanel movement = new JPanel();
		movement.setOpaque(false);
		// movement.setBackground(Color.RED);
		movement.setPreferredSize(new Dimension(280, 285));
		movement.setVisible(true);
		movement.setLayout(new BoxLayout(movement, BoxLayout.Y_AXIS));
		JPanel labelContainer = new JPanel();
		JLabel mLabel = new JLabel("Move Character", JLabel.CENTER);
		mLabel.setForeground(Color.WHITE);
		labelContainer.add(mLabel, BorderLayout.CENTER);
		labelContainer.setOpaque(false);
		 movement.add(labelContainer);
		//JLabel mLabel = new JLabel("                     Move Character", JLabel.CENTER);
		//mLabel.setForeground(Color.WHITE);
		// movement.add(new JLabel(" Move Character"));
		//movement.add(mLabel);

		JPanel leftP = new JPanel();
		leftP.setOpaque(false);
		leftP.setLayout(new BorderLayout());
		BasicArrowButton leftB = new BasicArrowButton(BasicArrowButton.WEST);
		leftB.setActionCommand("move left");
		leftB.addActionListener(this);
		leftB.setBorder(new EmptyBorder(40, 40, 40, 40));
		leftB.setBackground(new Color(204, 160, 229));
		leftP.setPreferredSize(new Dimension(85, 250));
		leftP.add(leftB, BorderLayout.CENTER);

		JPanel middleP = new JPanel();
		middleP.setOpaque(false);
		middleP.setPreferredSize(new Dimension(85, 250));
		middleP.setLayout(new BoxLayout(middleP, BoxLayout.Y_AXIS));
		BasicArrowButton upB = new BasicArrowButton(BasicArrowButton.NORTH);
		upB.setActionCommand("move up");
		upB.addActionListener(this);
		upB.setBackground(new Color(243, 170, 170));
		BasicArrowButton downB = new BasicArrowButton(BasicArrowButton.SOUTH);
		downB.setActionCommand("move down");
		downB.addActionListener(this);
		downB.setBackground(new Color(160, 206, 229));
		upB.setBorder(new EmptyBorder(40, 40, 40, 40));
		downB.setBorder(new EmptyBorder(40, 40, 40, 40));
		middleP.add(upB);
		middleP.add(downB);

		JPanel rightP = new JPanel();
		rightP.setOpaque(false);
		rightP.setLayout(new BorderLayout());
		BasicArrowButton rightB = new BasicArrowButton(BasicArrowButton.EAST);
		rightB.setActionCommand("move right");
		rightB.addActionListener(this);
		rightB.setBorder(new EmptyBorder(40, 40, 40, 40));
		rightB.setBackground(new Color(160, 229, 190));
		rightP.setPreferredSize(new Dimension(85, 250));
		rightP.add(rightB, BorderLayout.CENTER);

		JPanel arrows = new JPanel();
		arrows.setOpaque(false);
		arrows.add(leftP);
		arrows.add(middleP);
		arrows.add(rightP);
		movement.add(arrows);

		return movement;

	}

	/**
	 *
	 * Add the side control panel - hand and notes tab
	 * This tab hold the detective pad for the player which displays their hand, suggestions and refuted cards
	 * It also has the buttons to make suggestions and accusations.
	 * @param JPanel handNotes to customize and add to
	 *
	 * */
	private void setupHandAndNotesTab(JPanel handNotes) {
		handNotes.setLayout(new BorderLayout());
		//Detective pad layout
		this.notes = new JTextArea() {
			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage((new ImageIcon("src/images/notes.png")).getImage(), 0, 0, this);
				super.paintComponent(g);
			}
		};
		this.notes.setOpaque(false);
		this.notes.repaint();
		this.notes.getPreferredSize().width = 280;
		this.notes.setLineWrap(true);
		this.notes.setWrapStyleWord(true);
		this.notes.setEditable(false);
		Font font = new Font("Segoe Print", Font.ITALIC, 12);
		this.notes.setFont(font);
		handNotes.add(new JScrollPane(this.notes), BorderLayout.CENTER);

		//Suggestion button layout
		JButton suggest = new JButton();
		suggest.setActionCommand("Suggest");
		suggest.setPreferredSize(new Dimension(130, 84));
		ImageIcon suggestB = new ImageIcon("src/images/suggest.jpg");
		suggest.setIcon(suggestB);
		suggest.addActionListener(this);

		//accuse button layout
		JButton accuse = new JButton();
		accuse.setActionCommand("Accuse");
		accuse.setPreferredSize(new Dimension(130, 84));
		ImageIcon accuseB = new ImageIcon("src/images/accuse.jpg");
		accuse.setIcon(accuseB);
		accuse.addActionListener(this);

		//panel to hold both buttons to make layout easier
		JPanel suggestAccuse = new JPanel();
		suggestAccuse.setPreferredSize(new Dimension(300, 93));
		suggestAccuse.setBackground(new Color(30, 82, 102));
		suggestAccuse.add(suggest);
		suggestAccuse.add(accuse);
		handNotes.add(suggestAccuse, BorderLayout.SOUTH);
	}

	/**
	 *
	 * Loads the players information such as their character, position, name, notes
	 *
	 * */
	public void loadProfile() {
		//Load player info such and update controls panel
		String pos = game().getBoard().getSquareAt(getCurrentPlayer().getPosition()).getName();
		String labelInput = "<html>Player : " + currentPlayer.getName() + "<br/> Character: "
				+ currentPlayer.getCharacter().getName() + "<br/> Location: " + pos + "</html>";
		mainInfo.setText(labelInput);
		mainInfo.setFont(new Font(mainInfo.getFont().getName(), Font.PLAIN, 11));
		mainInfo.setIcon(currentPlayer.getCharacter().getImage());

		//load players notes and update
		String notes = currentPlayer.displayNotes();
		this.notes.setText(notes);

		//reset the booleans and suggestions and message to display
		setMessage("Please roll the dice and move your token, or finish your turn.");
		this.usedPassage = false;
		this.hasSuggested = false;
		this.suggestions = new ArrayList <Card>();
		this.accusation = new ArrayList <Card>();
		this.characChosen = null;
		this.weapChosen = null;
		this.roomChosen = null;

		this.controls.repaint();
	}

	/**
	 * Move the current player to a desired square.
	 * @param direct the direction to move
	 */
	public void movePlayer(Direction direct){
		try {
			if (game.getDiceRoll() > game.numberOfUseMoves() && moveTurn == true) {
				game.movePlayer(currentPlayer, direct);
				Square sq = game.getBoard().getSquareAt(currentPlayer.getPosition());
				int numMovesLeft = game.getDiceRoll() - game.numberOfUseMoves();
				if (sq instanceof RoomSquare){
					setMessage("You're now in the "+sq.getName() + ".\n");
					moveTurn = false;
				} else {
					setMessage("You're now at the corridor, " + currentPlayer.getPosition() +".\n"+ "You have " +numMovesLeft + " move(s) left.");
				}
				updateInfo();
				canvas.repaint();
				if (numMovesLeft <= 0) moveTurn = false;
			} else if (!moveTurn && game.isDiceRoll()){
				setMessage("Sorry... You've used all your moves.");
			} else if (!game.isDiceRoll() && moveTurn == true){
				setMessage("You haven't rolled the dice yet! Please roll the dice to move.");
			}
		} catch (InvalidAction e1) {
			this.setMessage(e1.getMessage());
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action == "Suggest") {
			if (this.hasSuggested == false) {
				try {
					showSuggestionDialog();
				} catch (InvalidAction e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				String message = "You have already made a suggestion! Please make an accusation or finish turn.";
				setMessage(message);
				controls.setSelectedIndex(0);
			}
		} else if (action == "Accuse") {
			try {
				showAccusationDialog();
			} catch (InvalidAction e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (action == "Roll") {
			if (!this.game.isDiceRoll() && this.moveTurn == true && !this.usedPassage && !this.hasSuggested) showDiceDialog();
			else setMessage("Sorry... You've used all your moves.");
		} else if (action == "New Game") {
			startNewGameDialog();
		} else if (action == "Controls") {
			controls(true);
		} else if (action == "How to Play") {
			howToPlay(true);
		} else if (action == "About") {
			about(true);
		} else if (action == "End Turn") {
			endTurnStatus = true;
		} else if (action == "move left") {
			movePlayer(Direction.WEST);
		} else if (action == "move right") {
			movePlayer(Direction.EAST);
		} else if (action == "move up") {
			movePlayer(Direction.NORTH);
		} else if (action == "move down") {
			movePlayer(Direction.SOUTH);
		} else if(action == "CM"){
			this.characChosen = game().getBoard().getCharacterCard("Colonel Mustard");
		} else if(action == "MW"){
			this.characChosen = game().getBoard().getCharacterCard("Mrs White");
		} else if(action == "MS"){
			this.characChosen = game().getBoard().getCharacterCard("Miss Scarlett");
		} else if(action == "MP"){
			this.characChosen = game().getBoard().getCharacterCard("Mrs Peacock");
		} else if(action == "RG"){
			this.characChosen = game().getBoard().getCharacterCard("Reverend Green");
		} else if(action == "PP"){
			this.characChosen = game().getBoard().getCharacterCard("Professor Plum");
		} else if(action == "Cs"){
			this.weapChosen = game().getBoard().getWeaponCard("Candlestick");
		} else if(action == "Dg"){
			this.weapChosen = game().getBoard().getWeaponCard("Dagger");
		} else if(action == "Lp"){
			this.weapChosen = game().getBoard().getWeaponCard("LeadPipe");
		} else if(action == "Rp"){
			this.weapChosen = game().getBoard().getWeaponCard("Rope");
		} else if(action == "Rv"){
			this.weapChosen = game().getBoard().getWeaponCard("Revolver");
		} else if(action == "Sp"){
			this.weapChosen = game().getBoard().getWeaponCard("Spanner");
		} else if(action == "Br"){
			this.roomChosen = game().getBoard().getRoomCard("Ballroom");
		} else if(action == "Bl"){
			this.roomChosen = game().getBoard().getRoomCard("Billiard Room");
		} else if(action == "Lb"){
			this.roomChosen = game().getBoard().getRoomCard("Library");
		} else if(action == "Cn"){
			this.roomChosen = game().getBoard().getRoomCard("Conservatory");
		} else if(action == "Dn"){
			this.roomChosen = game().getBoard().getRoomCard("Dining Room");
		} else if(action == "Lg"){
			this.roomChosen = game().getBoard().getRoomCard("Lounge");
		} else if(action == "St"){
			this.roomChosen = game().getBoard().getRoomCard("Study");
		} else if(action == "Hl"){
			this.roomChosen = game().getBoard().getRoomCard("Hall");
		} else if(action == "Kt"){
			this.roomChosen = game().getBoard().getRoomCard("Kitchen");
		}

	}


	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (CluedoBoardCanvas.startDraw && x >= 0 && x <= 800 && y >= 0 && y <= 800) {
			canvas.displayPopupMessage(x, y);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP) {
			movePlayer(Direction.NORTH);
		} else if (key == KeyEvent.VK_DOWN) {
			movePlayer(Direction.SOUTH);
		} else if (key == KeyEvent.VK_LEFT) {
			movePlayer(Direction.WEST);
		} else if (key == KeyEvent.VK_RIGHT) {
			movePlayer(Direction.EAST);
		}

	}

	private static JRadioButton jRadio = null;
	private String chosenToken;
	private JLabel characterImage;

	/**
	 * Enter the information for all the players.
	 * @param numPlayers the number of players
	 * @throws InvalidAction throws an exception when the input is invalid
	 */
	public void inputPlayersInfo(int numPlayers) throws InvalidAction {
		// now, input data
		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<String> chosenTokens = new ArrayList<String>();
		JDialog dialog = createTokenInput();
		int count = 1;
		while (count <= numPlayers) {
			String name = createInputNameField(count);
			dialog.pack();
			dialog.setLocationRelativeTo(this);
			dialog.setModal(true);
			dialog.setVisible(true);
			dialog.setAlwaysOnTop(true);
			players.add(new Player(name, (Character)game.getBoard().getCharacterCard(chosenToken)));
			chosenTokens.add(chosenToken);
			count++;
		}

		// load the players and cards
		game.setAllPlayers(players);
		game.getBoard().loadCards(players);
		CluedoBoardCanvas.startDrawOn(true);
		canvas.repaint();
	}

	/**
	 * Creates the pop-up dialog box for the players to enter their names
	 * @param count the index number of the player when entering their details
	 * @return
	 */
	private String createInputNameField(int count){
		JOptionPane namePane = new JOptionPane();
		JDialog nameDialog = namePane.createDialog(this, "Player " + count + " Input");
		namePane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		namePane.setWantsInput(true);
		namePane.setMessage("What's your name?");
		namePane.setIcon(new ImageIcon());
		nameDialog.setLocationRelativeTo(this);
		nameDialog.setSize(new Dimension(300, 150));
		nameDialog.setModal(true);
		nameDialog.setVisible(true);
		String name = (String) namePane.getInputValue();
		if (name.length() <= 0) name = "Player "+ count;
		return name;
	}

	/**
	 * Returns a pop-up dialog for the player to choose a token/character.
	 * @return the pop-up dialog with a list of characters
	 */
	private JDialog createTokenInput(){
		JDialog dialog =new JDialog(this, "Choose a Character...");
		dialog.setSize(new Dimension(500, 200));
		JRadioButton[] buttons = new JRadioButton[6];
		int i = 0;
		boolean first = true;
		ButtonGroup group = new ButtonGroup();
		for (Character w : game.getBoard().getAllCharacters()){
			if (first){
				characterImage = new JLabel(w.getImage());
				chosenToken = w.getName();
				first = false;
			}
			buttons[i++] = new JRadioButton(w.getName());
			buttons[i-1].addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent ae) {
					jRadio = (JRadioButton) ae.getSource();
					chosenToken = ae.getActionCommand();
					Character w = (Character) game.getBoard().getCharacterCard(chosenToken);
					characterImage.setIcon(w.getImage());
				}
			});
			group.add(buttons[i-1]);
		}

		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.setPreferredSize(new Dimension(300, 200));
		radioPanel.setBorder(BorderFactory.createEmptyBorder(15,30,15,30));
		for (JRadioButton rm : buttons)
			radioPanel.add(rm);
		dialog.add(radioPanel,BorderLayout.LINE_START);
		dialog.add(characterImage, BorderLayout.CENTER);
		dialog.add(new JPanel(), BorderLayout.EAST);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	        	 if (jRadio != null){
		        	 jRadio.setEnabled(false);
		        	 jRadio.setSelected(false);
		        	 dialog.dispose();
		        	 group.clearSelection();
		        	 jRadio = null;
	        	 }
	         }
	    });
		dialog.add(okButton, BorderLayout.SOUTH);
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		return dialog;
	}

	/**
	 * Get the number of players.
	 * @throws InvalidAction an exception if it is invalid input
	 */
	public void inputNumberPlayers() throws InvalidAction {
		JOptionPane numPlayers = new JOptionPane();
		String[] choices = {"Choose number of players","3", "4", "5", "6" };
		JDialog dialog = createDialog(numPlayers, "Start New Game", "How many players?", choices, "dropDown");

		// get the number player
		// if user didn't use exit, we ask for players info
		Object result = numPlayers.getInputValue();
		dialog.dispose();
		if (result != null) {
			if (result.equals("uninitializedValue")) return;
			else if (result.equals(choices[0])) inputNumberPlayers();
			else inputPlayersInfo(Integer.parseInt((String) result));;
		}
	}

	/**
	 * Set the current player on the board.
	 * @param player the player who is not eliminated
	 */
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
		this.moveTurn = true;
		this.endTurnStatus = false;
		this.game.resetVisitedSquare();
		setMessage("Please roll the dice and move you token, or finish your turn.");
		loadProfile();
	}

	/**
	 * Start a new game.
	 */
	public void startNewGame() {
		try {
			CluedoBoardCanvas.startDrawOn(false);
			if (this.game != null){
				game.endGame();
				currentPlayer = null;
				moveTurn = false;
				endTurnStatus = true;
			}
			this.game = new Game();
			canvas.addBoardCanvas(game.getBoard());
			inputNumberPlayers();
		} catch (InvalidAction e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pop-up dialog to ask if the user want to start a new game or not.
	 */
	public void startNewGameDialog(){
		JOptionPane newGame = new JOptionPane();
		String[] choices = { "Yes", "No"};
		JDialog dialog;
		dialog = createDialog(newGame, "New Game", "Start a new game?", choices, "noDropDown");

		// get the response from player
		Object response = newGame.getValue();
		dialog.dispose();
		if (response != null){
			if (response.equals(choices[0]) && game != null){
				game.endGame();
				game.getAllPlayers().clear();
				endTurnStatus = true;
				currentPlayer = null;
				startNewGame();
			} else if (response.equals(choices[0]) && game == null) {
				startNewGame();
			} else if ((response.equals(choices[1]) && game != null) || response.equals("uninitializedValue") || (response.equals(choices[1]) && game == null)){
				if (game == null) return;
				return;
			}
		}
	}

	/**
	 * Pop-up dialog to ask if the player want to use the secret passage.
	 * @param roomName the name of room that connected to this secret passage
	 */
	public void promptForPassage(String roomName){
		if (moveTurn == true) {
			JOptionPane useSP = new JOptionPane();
			String[] choices = { "Yes", "No"};
			JDialog dialog = createDialog(useSP, "Secret Passage", "Use secret Passage to "+roomName + " ?", choices, "noDropDown");

			// get the response from player
			Object response = useSP.getValue();
			dialog.dispose();
			if (response.equals(choices[0])) {
				try {
					game.useSecretPassage(currentPlayer);
					this.usedPassage = true;
					moveTurn = false;
				} catch (InvalidAction e1) {
					e1.printStackTrace();
				}
				canvas.repaint();
			}
		}
	}

	/**
	 * Check if the current player has used the secret passage or not.
	 * @return true if the player used the secret passage, otherwise false
	 */
	public boolean hasUsedPassage(){
		return this.usedPassage;
	}

	/**
	 * Update the message box on the bottom right corner.
	 * @param message the message to print out
	 */
	public void setMessage(String message){
		String msg = "Message:\n"+message;
		this.errorsAndInstructions.setText(msg);
		this.controls.repaint();
	}

	/**
	 * Returns the current game.
	 * @return the current game
	 */
	public Game game() {
		return game;
	}

	/**
	 * Pop-up dialog box when the player clicked on the roll dice button.
	 */
	private void showDiceDialog(){
		this.game.setDiceRoll(new Random().nextInt((12 - 2) + 1) + 2);
		String roll = "You have rolled a " + this.game.getDiceRoll();
		JOptionPane.showMessageDialog(this, roll, "Roll Dice", JOptionPane.PLAIN_MESSAGE);
		String message = "Please move token " + this.game.getDiceRoll() + " spaces.";
		setMessage(message);
		this.controls.repaint();
	}

	/**
	 *
	 * initiates the pop ups for making a suggestion such as choosing cards and getting refuted card.
	 * It then gathers the information and passes it to the game
	 *
	 * */
	private void showSuggestionDialog() throws InvalidAction {
		if (this.moveTurn == false) {
			// check if in room
			Square isRoom = game().getBoard().getSquareAt(getCurrentPlayer().getPosition());
			// check if in room
			if (isRoom instanceof RoomSquare) {
				askForCharacter("suggest");
				this.suggestions.add(this.characChosen);
				askForWeapon("suggest");
				this.suggestions.add(this.weapChosen);
				//add room
				String roomName = game().getBoard().getSquareAt(getCurrentPlayer().getPosition()).getName();
				Card room = game().getBoard().getRoomCard(roomName);
				this.suggestions.add(room);
				//check to make sure suggestion is valid
				if(this.suggestions.contains(null)){
					//throw new InvalidAction ("Suggestion inccorect.");
					setMessage("Uh oh! Something went wrong while you were suggesting. Please try again.");
					controls.setSelectedIndex(0);
					return;
				}
				//get refuting card
				setRefutedCard();
				if(this.refutedCard  == null){
					JOptionPane.showMessageDialog(this, "You suggestion has refuted no cards.","Refuted Card", JOptionPane.PLAIN_MESSAGE);
					setMessage("Please make an accusation or finish your turn.");
				}else{
					String msg = "Your suggestion has refuted the following card: \n"+this.refutedCard.getName();
					JOptionPane.showMessageDialog(this, msg,"Refuted Card", JOptionPane.PLAIN_MESSAGE);
					setMessage("Please make an accusation or finish your turn.");
				}
				game().makeSuggestion(getCurrentPlayer(), this.suggestions, this.refutedCard);
				//reset cards chosen for accusation
				this.hasSuggested = true;
				this.characChosen = null;
				this.weapChosen = null;
				//updateNotes
				String notes = currentPlayer.displayNotes();
				this.notes.setText(notes);
				this.controls.repaint();
				this.canvas.repaint();
			} else {
				setMessage("You need to be in a room to make a suggestion.");
				controls.setSelectedIndex(0);
			}
		} else {
			setMessage("You need to move to a room before you can suggest.");
			controls.setSelectedIndex(0);
		}
	}

	/**
	 *
	 * initiates the pop ups for making a accusation such as choosing cards and checking solution.
	 * It then gathers the information and passes it to the game and either eliminates player or makes them the winner
	 *
	 * */
	private void showAccusationDialog() throws InvalidAction {
		askForCharacter("accuse");
		this.accusation.add(this.characChosen);
		askForWeapon("accuse");
		this.accusation.add(this.weapChosen);
		askForRoom();
		this.accusation.add(this.roomChosen);
		//check to make sure suggestion is valid
		if(this.accusation.contains(null)){
			//throw new InvalidAction ("Accusation inccorect.");
			setMessage("Uh oh! Something went wrong while you were accusing. Please try again.");
			controls.setSelectedIndex(0);
			return;
		}
		//check solution
		boolean accusation = game().checkAccusation(this.accusation);
		if (accusation == true) {
			String message = "You have accused the following cards: "+this.accusation.get(0).getName()+", "+this.accusation.get(1).getName()+", "+this.accusation.get(2).getName()+".";
			message += "Your accusation was CORRECT. You have WON the game!";
			JOptionPane.showMessageDialog(this, message,
					"You have Won!", JOptionPane.PLAIN_MESSAGE);
			//end game
			gameOver(true);
		} else {
			String message = "You have accused the following cards: "+this.accusation.get(0).getName()+", "+this.accusation.get(1).getName()+", "+this.accusation.get(2).getName()+".\n";
			message += "Your accusation was INCORRECT. You are hence eliminated from the game.\nYou may, however, continue refuting.";
			JOptionPane.showMessageDialog(this, message,
					"You are Eliminated", JOptionPane.PLAIN_MESSAGE);
			game().eliminated(getCurrentPlayer());
			//get next player
			controls.setSelectedIndex(0);
			Player nextPlayer = game().getNextPlayer(getCurrentPlayer());
			if(nextPlayer != null){
				message = "Please call next player " + nextPlayer.getName()+"\nPlease confirm if you are Player "+nextPlayer.getName();
				boolean confirm = confirm("Please Call Player", message);
				while(confirm == false){
					confirm = confirm("Please Call Player", message);
				}
				endTurnStatus = true;
			}else{//all players eliminated so game over
				gameOver(false);
			}
		}
		this.controls.repaint();
	}

	/**
	 * Pop-up dialog box when the user try to exit the program.
	 */
	public void closeWindow(){
		JOptionPane close = new JOptionPane();
		String[] choices = { "Yes", "No" };
		JDialog dialog = createDialog(close, "Exit Game?", "Are you sure you wish to exit?", choices, "noDropDown");

		// get the response from player
		Object response = close.getValue();
		dialog.dispose();
		if (response.equals(choices[0])) {
			System.exit(0);
		}
	}

	/**
	 * Update the information of the current player.
	 */
	private void updateInfo(){
		String pos = game().getBoard().getSquareAt(currentPlayer.getPosition()).getName();
		String labelInput = "<html>Player : " + currentPlayer.getName() + "<br/> Character: "
				+ currentPlayer.getCharacter().getName() + "<br/> Location: "+pos+"</html>";
		mainInfo.setText(labelInput);
	}

	/**
	 *
	 * Creates the pop up to choose a character to suggest or accuse.
	 * Makes the JRadioButtons and panel to add to the pop up JOption
	 * @param suggestOrAccuse to use for the message/title/label
	 *
	 * */
	public void askForCharacter(String suggestOrAccuse) {
		// set up the characters
		 JRadioButton mrsWhite = new JRadioButton("Mrs White");	//miss white
		 mrsWhite.setIcon(new ImageIcon("src/images/CharacCard_MW.png"));
		 mrsWhite.setActionCommand("MW");
		 mrsWhite.setSelected(true);

		 JRadioButton mrsPk = new JRadioButton("Mrs Peacock");	//mrs Peacock
		 mrsPk.setIcon(new ImageIcon("src/images/CharacCard_MP.png"));
		 mrsPk.setActionCommand("MP");
		 mrsPk.setSelected(true);

		 JRadioButton missS = new JRadioButton("Miss Scarlett");	//miss scarlett
		 missS.setIcon(new ImageIcon("src/images/CharacCard_MS.png"));
		 missS.setActionCommand("MS");
		 missS.setSelected(true);

		 JRadioButton profP = new JRadioButton("Professor Plum");	//prof plum
		 profP.setIcon(new ImageIcon("src/images/CharacCard_PP.png"));
		 profP.setActionCommand("PP");
		 profP.setSelected(true);

		 JRadioButton colM = new JRadioButton("Colonel Mustard");	//colonel mustard
		 colM.setIcon(new ImageIcon("src/images/CharacCard_CM.png"));
		 colM.setActionCommand("CM");
		 colM.setSelected(true);

		 JRadioButton revG = new JRadioButton("Reverend Green");	//reverend green
		 revG.setIcon(new ImageIcon("src/images/CharacCard_RG.png"));
		 revG.setActionCommand("RG");
		 revG.setSelected(true);

		 //Group the radio buttons.
		 ButtonGroup group = new ButtonGroup();
		 group.add(mrsWhite);
		 group.add(mrsPk);
		 group.add(missS);
		 group.add(profP);
		 group.add(colM);
		 group.add(revG);

		 //Register a listener for the radio buttons.
		 mrsWhite.addActionListener(this);
		 mrsPk.addActionListener(this);
		 missS.addActionListener(this);
		 profP.addActionListener(this);
		 colM.addActionListener(this);
		 revG.addActionListener(this);

		 //add to JOptionPane
		 JPanel opt = new JPanel();
		 opt.setPreferredSize(new Dimension(660, 360));
		 opt.setLayout(new BoxLayout(opt, BoxLayout.Y_AXIS));
		 JPanel label = new JPanel();
		 label.add(new JLabel("Choose the character you suspect commited the murder:"), BorderLayout.CENTER);
		 opt.add(label);
		 JPanel radioPanel = new JPanel(new GridLayout(2, 3));
	     radioPanel.add(mrsWhite, 0, 0);
	     radioPanel.add(mrsPk, 0, 1);
	     radioPanel.add(missS, 0, 2);
	     radioPanel.add(profP, 1, 0);
	     radioPanel.add(colM, 1, 1);
	     radioPanel.add(revG, 1, 2);
	     JPanel buttons = new JPanel();
	     buttons.add(radioPanel, BorderLayout.CENTER);
	     opt.add(buttons);
		JOptionPane.showMessageDialog(null,opt,"Which character do you "+suggestOrAccuse+"?", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 *
	 * Creates the pop up to choose a weapon to suggest or accuse.
	 * Makes the JRadioButtons and panel to add to the pop up JOption
	 * @param suggestOrAccuse to use for the message/title/label
	 *
	 * */
	public void askForWeapon(String suggestOrAccuse) {
		// set up the characters
		 JRadioButton dagger = new JRadioButton("Dagger");	//dagger
		 dagger.setIcon(new ImageIcon("src/images/WeapCard_D.png"));
		 dagger.setActionCommand("Dg");
		 dagger.setSelected(true);

		 JRadioButton candle = new JRadioButton("Candlestick");	//candle stick
		 candle.setIcon(new ImageIcon("src/images/WeapCard_CS.png"));
		 candle.setActionCommand("Cs");
		 candle.setSelected(true);

		 JRadioButton lead = new JRadioButton("Lead Pipe");	//lead pipe
		 lead.setIcon(new ImageIcon("src/images/WeapCard_LP.png"));
		 lead.setActionCommand("Lp");
		 lead.setSelected(true);

		 JRadioButton rope = new JRadioButton("Rope");	//rope
		 rope.setIcon(new ImageIcon("src/images/WeapCard_R.png"));
		 rope.setActionCommand("Rp");
		 rope.setSelected(true);

		 JRadioButton revolver = new JRadioButton("Revolver");	//revolver
		 revolver.setIcon(new ImageIcon("src/images/WeapCard_RV.png"));
		 revolver.setActionCommand("Rv");
		 revolver.setSelected(true);

		 JRadioButton spanner = new JRadioButton("Spanner");	//spanner
		 spanner.setIcon(new ImageIcon("src/images/WeapCard_SP.png"));
		 spanner.setActionCommand("Sp");
		 spanner.setSelected(true);

		 //Group the radio buttons.
		 ButtonGroup group = new ButtonGroup();
		 group.add(dagger);
		 group.add(candle);
		 group.add(lead);
		 group.add(rope);
		 group.add(revolver);
		 group.add(spanner);

		 //Register a listener for the radio buttons.
		 dagger.addActionListener(this);
		 candle.addActionListener(this);
		 lead.addActionListener(this);
		 rope.addActionListener(this);
		 revolver.addActionListener(this);
		 spanner.addActionListener(this);

		 //add to JOption
		 JPanel opt = new JPanel();
		 opt.setPreferredSize(new Dimension(560, 320));
		 opt.setLayout(new BoxLayout(opt, BoxLayout.Y_AXIS));
		 JPanel label = new JPanel();
		 label.add(new JLabel("Choose the weapon you suspect was used in the murder:"), BorderLayout.CENTER);
		 opt.add(label);
		 JPanel radioPanel = new JPanel(new GridLayout(2, 3));
	     radioPanel.add(dagger, 0, 0);
	     radioPanel.add(candle, 0, 1);
	     radioPanel.add(lead, 0, 2);
	     radioPanel.add(rope, 1, 0);
	     radioPanel.add(revolver, 1, 1);
	     radioPanel.add(spanner, 1, 2);
	     JPanel buttons = new JPanel();
	     buttons.add(radioPanel, BorderLayout.CENTER);
	     opt.add(buttons);
	     JOptionPane.showMessageDialog(null,opt,"Which weapon do you "+suggestOrAccuse+"?", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 *
	 * Creates the pop up to choose a room to accuse.
	 * Makes the JRadioButtons and panel to add to the pop up JOption
	 *
	 * */
	public void askForRoom() {
		// set up the rooms
		 JRadioButton ball = new JRadioButton("Ballroom");	//ballroom
		 ball.setIcon(new ImageIcon("src/images/RoomCard_BR.png"));
		 ball.setActionCommand("Br");
		 ball.setSelected(true);

		 JRadioButton bill = new JRadioButton("Billiard Room");	//billiard
		 bill.setIcon(new ImageIcon("src/images/RoomCard_BL.png"));
		 bill.setActionCommand("Bl");
		 bill.setSelected(true);

		 JRadioButton lib = new JRadioButton("Library");	//library
		 lib.setIcon(new ImageIcon("src/images/RoomCard_LB.png"));
		 lib.setActionCommand("Lb");
		 lib.setSelected(true);

		 JRadioButton con = new JRadioButton("Conservatory");	//conservatory
		 con.setIcon(new ImageIcon("src/images/RoomCard_C.png"));
		 con.setActionCommand("Cn");
		 con.setSelected(true);

		 JRadioButton study = new JRadioButton("Study");	//Study
		 study.setIcon(new ImageIcon("src/images/RoomCard_S.png"));
		 study.setActionCommand("St");
		 study.setSelected(true);

		 JRadioButton dining = new JRadioButton("Dining Room");	//dining room
		 dining.setIcon(new ImageIcon("src/images/RoomCard_D.png"));
		 dining.setActionCommand("Dn");
		 dining.setSelected(true);

		 JRadioButton kitch = new JRadioButton("Kitchen");	//kitchen
		 kitch.setIcon(new ImageIcon("src/images/RoomCard_K.png"));
		 kitch.setActionCommand("Kt");
		 kitch.setSelected(true);

		 JRadioButton hall = new JRadioButton("Hall");	//hall
		 hall.setIcon(new ImageIcon("src/images/RoomCard_H.png"));
		 hall.setActionCommand("Hl");
		 hall.setSelected(true);

		 JRadioButton lounge = new JRadioButton("Lounge");	//lounge
		 lounge.setIcon(new ImageIcon("src/images/RoomCard_L.png"));
		 lounge.setActionCommand("Lg");
		 lounge.setSelected(true);

		 //Group the radio buttons.
		 ButtonGroup group = new ButtonGroup();
		 group.add(ball);
		 group.add(bill);
		 group.add(lib);
		 group.add(con);
		 group.add(study);
		 group.add(dining);
		 group.add(kitch);
		 group.add(hall);
		 group.add(lounge);

		 //Register a listener for the radio buttons.
		 ball.addActionListener(this);
		 bill.addActionListener(this);
		 con.addActionListener(this);
		 study.addActionListener(this);
		 dining.addActionListener(this);
		 lib.addActionListener(this);
		 hall.addActionListener(this);
		 lounge.addActionListener(this);
		 kitch.addActionListener(this);

		 //add to JOptionPane
		 JPanel opt = new JPanel();
		 opt.setPreferredSize(new Dimension(620, 500));
		 opt.setLayout(new BoxLayout(opt, BoxLayout.Y_AXIS));
		 JPanel label = new JPanel();
		 label.add(new JLabel("Choose the room you suspect is wher the murder occured:"), BorderLayout.CENTER);
		 opt.add(label);
		 JPanel radioPanel = new JPanel(new GridLayout(3, 3));
	     radioPanel.add(ball, 0, 0);
	     radioPanel.add(bill, 0, 1);
	     radioPanel.add(lib, 0, 2);
	     radioPanel.add(kitch, 1, 0);
	     radioPanel.add(hall, 1, 1);
	     radioPanel.add(lounge, 1, 2);
	     radioPanel.add(con, 2, 0);
	     radioPanel.add(study, 2, 1);
	     radioPanel.add(dining, 2, 2);
	     JPanel buttons = new JPanel();
	     buttons.add(radioPanel, BorderLayout.CENTER);
	     opt.add(buttons);
	     JOptionPane.showMessageDialog(null,opt,"Which room do you accuse?", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 *
	 * Creates the pop up to ask refuting player to choose card to refute. It then sets the refuted card field as the card or null
	 * so the suggesting player can see and get an hint to what may be the solution
	 * @throws InvalidAction exception for invalid action
	 * */
	public void setRefutedCard() throws InvalidAction{
		// call next refutable player
		Player nextPlayer = game().findNextRefutablePlayer(getCurrentPlayer(), this.suggestions);
		if (nextPlayer == null) {
			this.refutedCard = null;
		}
		controls.setSelectedIndex(0);
		String message = "You have suggested the following cards: \n\t"+this.suggestions.get(0).getName()+", "+this.suggestions.get(1).getName()+", "+this.suggestions.get(2).getName()+".";
		message += "\nPlayer "+nextPlayer.getName()+" has a refutable card. \nPlease call "+nextPlayer.getName()+"\nPlease confirm if you are Player "+nextPlayer.getName();
		//JOptionPane.showMessageDialog(this, message,"Please Call Player", JOptionPane.PLAIN_MESSAGE);
		boolean confirm = confirm("Please Call Player", message);
		while(confirm == false){
			confirm = confirm("Please Call Player", message);
		}
		List<Card> refutableCards = game().getRefutableCards(this.suggestions, nextPlayer);
		// if only 1 card
		if (refutableCards.size() == 1) {
			message = "<html><body><p style='width: 200px;'>You have the refutable card: " + refutableCards.get(0).getName()
					+ ", which is going to be shown to the suggesting player.</p></body></html>";
			JOptionPane.showMessageDialog(this, message,"Show refuted card?", JOptionPane.PLAIN_MESSAGE);
			// call back current player
			message = "Please call back player " + getCurrentPlayer().getName()+"\nPlease confirm if you are Player "+getCurrentPlayer().getName();
			confirm = confirm("Please Call Player", message);
			while(confirm == false){
				confirm = confirm("Please Call Player", message);
			}
			this.refutedCard = refutableCards.get(0);
		// if more than 1 card
		} else if (refutableCards.size() > 1) {
				this.refutedCard = pickCard(refutableCards);
				// call back current player
				message = "Please call back player " + getCurrentPlayer().getName()+"\nPlease confirm if you are Player "+getCurrentPlayer().getName();
				confirm = confirm("Please Call Player", message);
				while(confirm == false){
					confirm = confirm("Please Call Player", message);
				}
		}
	}

	/**
	 *
	 * Makes a pop up for refuting player to choose the card they wish to let teh suggesting player see.
	 * @return Card refuted card
	 * @param refutableCards the list of refutable cards
	 *
	 * */
	public Card pickCard(List <Card> refutableCards){
		String[] refutableChoices = new String[refutableCards.size()];
		int index = 0;
		for (Card n : refutableCards){
			refutableChoices[index++] = n.getName();
		}
		// Let refuting player select a card to refute
		JOptionPane refutableOption = new JOptionPane();
		JDialog refutableDialog = refutableOption.createDialog(this, "Choose a card to refute");
		String message = "Which of the following cards would you \nlike to show the suggester?";
		refutableOption.setMessage(message);
		refutableOption.setSelectionValues(refutableChoices);
		refutableOption.setInitialSelectionValue(refutableChoices[0]);
		refutableDialog.setLocationRelativeTo(this);
		refutableDialog.setSize(new Dimension(400, 250));
		refutableDialog.setModal(true);
		refutableDialog.setVisible(true);

		// return chosen card
		String cardName = (String)refutableOption.getInputValue();
		Card chosenCard = game.getBoard().getCharacterCard(cardName);		//if chosen card is a character card
		if (chosenCard == null) {
			chosenCard = game.getBoard().getWeaponCard(cardName);		//if chosen card is a weapon card
		}
		if (chosenCard == null) {
			chosenCard = game.getBoard().getRoomCard(cardName);		//if chosen card is a room card
		}
		refutableDialog.dispose();
		return chosenCard;
	}

	/**
	 *
	 * Makes a pop up yes or no confirmation to be used in different methods in this class
	 * @param label the label of the dialog
	 * @param message the message of the dialog
	 * @return yes or no
	 * */
	public boolean confirm(String label, String message){
		JOptionPane confirm = new JOptionPane();
		JDialog confirmDialog = confirm.createDialog(this, label);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String[] choices = { "Yes", "No" };
		confirm.setMessage(message);
		confirm.setInitialValue(choices[0]);
		confirm.setOptions(choices);

		// set the dialog layout settings
		confirmDialog.pack();
		confirmDialog.setLocationRelativeTo(this);
		confirmDialog.setSize(new Dimension(450, 220));
		confirmDialog.setModal(true);
		confirmDialog.setVisible(true);

		// get the response from player
		Object response = confirm.getValue();
		if (response.equals(choices[0])) {
			return true;
		}
		return false;
	}


	/**
	 * Determines if the current player's turn is finished or not.
	 * @return true if the current player clicked the end turn button, otherwise false
	 */
	public boolean isEndTurn(){
		return endTurnStatus;
	}

	/**
	 * Determines if the current player still has any moves left.
	 * @return true if there is no more moves, otherwise false
	 */
	public boolean isMoveTurn(){
		return moveTurn;
	}

	/**
	 * Returns the player who is currently controls the GUI.
	 * @return the current player
	 */
	public Player getCurrentPlayer(){
		return currentPlayer;
	}

	/**
	 * Pop-up welcome dialog.
	 */
	public void welcome(){
		JOptionPane welcome = new JOptionPane();
		JDialog welcomeDialog = welcome.createDialog(this, "Welcome to Cluedo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String[] choices = { "New Game", "How to Play", "Controls", "About" };
		welcome.setMessage("");
		welcome.setInitialValue(choices[0]);
		welcome.setOptions(choices);
		ImageIcon img2 = new ImageIcon("src/images/welcome.png");
		welcome.setIcon(img2);;
		welcome.setBackground(new Color(30, 82, 102));
		// set the dialog layout settings
		welcomeDialog.pack();
		welcomeDialog.setLocationRelativeTo(null);
		welcomeDialog.setSize(new Dimension(440, 383));
		welcomeDialog.setModal(true);
		welcomeDialog.setVisible(true);

		// get the response from player
		Object response = welcome.getValue();
		if (response.equals(choices[0])) {
			startNewGameDialog();
		}else if (response.equals(choices[1])) {
			howToPlay(false);
		}else if (response.equals(choices[2])) {
			controls(false);
		}else if (response.equals(choices[3])) {
			about(false);
		}

	}

	/**
	 * Pop-up dialog of how to play page.
	 * @param fromMenu the status whether if the player clicked on a button or from menu bar
	 */
	public void howToPlay(boolean fromMenu){
		JOptionPane howToPlay = new JOptionPane();
		JDialog howToPlayDialog = howToPlay.createDialog(this, "How to Play");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		howToPlay.setMessage("");
		//if not access from menu then accessed from main so have button to go back
		String[] choices = {"Back to Main Menu"};
		if(!fromMenu){
			howToPlay.setInitialValue(choices[0]);
			howToPlay.setOptions(choices);
		}
		howToPlay.setLayout(new BoxLayout(howToPlay, BoxLayout.Y_AXIS));
		ImageIcon img2 = new ImageIcon("src/images/instructions.png");
		howToPlay.setIcon(img2);;

		// set the dialog layout settings
		howToPlayDialog.pack();
		howToPlayDialog.setLocationRelativeTo(null);
		howToPlayDialog.setSize(new Dimension(420, 670));
		howToPlayDialog.setModal(true);
		howToPlayDialog.setVisible(true);

		// get the response from player
		Object response = howToPlay.getValue();
		if (!fromMenu && response.equals(choices[0])) {
			welcome();
		}

	}

	/**
	 * Pop-up dialog of about page.
	 * @param fromMenu the status whether if the player clicked on a button or from menu bar
	 */
	public void about(boolean fromMenu){
		JOptionPane about = new JOptionPane();
		JDialog aboutDialog = about.createDialog(this, "About");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		about.setMessage("");
		//if not access from menu then accessed from main so have button to go back
		String[] choices = {"Back to Main Menu"};
		if(!fromMenu){
			about.setInitialValue(choices[0]);
			about.setOptions(choices);
		}
		about.setLayout(new BoxLayout(about, BoxLayout.Y_AXIS));
		ImageIcon img2 = new ImageIcon("src/images/about.png");
		about.setIcon(img2);;

		// set the dialog layout settings
		aboutDialog.pack();
		aboutDialog.setLocationRelativeTo(null);
		aboutDialog.setSize(new Dimension(420, 283));
		aboutDialog.setModal(true);
		aboutDialog.setVisible(true);

		// get the response from player
		Object response = about.getValue();
		if (!fromMenu && response.equals(choices[0])) {
			welcome();
		}

	}

	/**
	 * pop up for the controls diagram.
	 * @param fromMenu if accessed from menu or main menu pop up. If main menu then will have button to go back to it.
	 */
	public void controls(boolean fromMenu){
		JOptionPane controls = new JOptionPane();
		JDialog controlsDialog = controls.createDialog(this, "Controls");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		controls.setMessage("");
		//if not access from menu then accessed from main so have button to go back
		String[] choices = {"Back to Main Menu"};
		if(!fromMenu){
			controls.setInitialValue(choices[0]);
			controls.setOptions(choices);
		}
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		ImageIcon img2 = new ImageIcon("src/images/controls.png");
		controls.setIcon(img2);;

		// set the dialog layout settings
		controlsDialog.pack();
		controlsDialog.setLocationRelativeTo(null);
		controlsDialog.setSize(new Dimension(420, 500));
		controlsDialog.setModal(true);
		controlsDialog.setVisible(true);

		// get the response from player
		Object response = controls.getValue();
		if (!fromMenu && response.equals(choices[0])) {
			welcome();
		}
	}

	/**
	 * Create a pop-up dialog.
	 * @param pane the JOptionPane that linked to this dialog
	 * @param title the title of the dialog
	 * @param msg the message to print out
	 * @param choices selection values
	 * @param type dropdown or standard menu
	 * @return the new create dialog
	 */
	public JDialog createDialog(JOptionPane pane, String title, String msg, String[] choices, String type){
		JDialog dialog = pane.createDialog(this, title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane.setMessage(msg);
		if (type.equals("dropDown")){
			pane.setSelectionValues(choices);
			pane.setInitialSelectionValue(choices[0]);
		} else {
			pane.setInitialValue(choices[0]);
			pane.setOptions(choices);
		}

		// set the dialog layout settings
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setSize(new Dimension(300, 150));
		dialog.setModal(true);
		dialog.setVisible(true);
		return dialog;
	}

	/**
	 * Pop up for game over
	 * @param won if the game is over by winning or elimination of all players
	 */
	public void gameOver(boolean won){
		//if the game is won by current player then ask if then say so
		String message = "";
		boolean playAgain = false;
		if(won){
			message = "Congradulations Player "+getCurrentPlayer()+"!\nYou have WON the game!\nThe murder was commited by "+this.accusation.get(0).getName()+", "+this.accusation.get(1).getName()+", "+this.accusation.get(2).getName()+".\nPlay again?";
			playAgain = confirm("GAME OVER", message);
		}//otherwise say no one won
		else{
			List<Card>solution = new ArrayList <Card>();
			for (Card n : game().getBoard().getSolution()){
				solution.add(n);
			}
			String charac = "";
			String weap = "";
			String room = "";
			if(solution.get(0) instanceof Character){
				charac = solution.get(0).getName();
				if(solution.get(1) instanceof Weapon){
					weap = solution.get(1).getName();
					room = solution.get(2).getName();
				}else {
					weap = solution.get(2).getName();
					room = solution.get(1).getName();
				}
			}else if(solution.get(1) instanceof Character){
				charac = solution.get(1).getName();
				if(solution.get(0) instanceof Weapon){
					weap = solution.get(0).getName();
					room = solution.get(2).getName();
				}else {
					weap = solution.get(2).getName();
					room = solution.get(0).getName();
				}
			}else{
				charac = solution.get(2).getName();
				if(solution.get(0) instanceof Weapon){
					weap = solution.get(0).getName();
					room = solution.get(1).getName();
				}else {
					weap = solution.get(1).getName();
					room = solution.get(0).getName();
				}
			}
			message = "All players are eliminated!\nThe murder was commited by: \n"+charac+" in the "+room+" with the "+weap+".\nPlay again?";
			playAgain = confirm("GAME OVER", message);
		}
		if(playAgain){
			game.endGame();
			game.getAllPlayers().clear();
			endTurnStatus = true;
			currentPlayer = null;
			startNewGame();
		} else{
			game().endGame();
		}
	}
}
