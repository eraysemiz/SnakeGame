import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 1000;
	static final int SCREEN_HEIGHT = 800;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	private boolean gameState = false;

	private SnakeBase snake1;
	private SnakeBase snake2;
	private SnakeBase aiSnake;
	private int gameMode = 1; // 1 = One player, 2 = 2 player, 3 = Against computer

	private int apple1X, apple1Y;
	public int apple2X, apple2Y;
	private int whoAteApple = 0;


	private MyKeyAdapter keyAdapter;

	private Timer timer;
	private Random random;

	private String player1Username;
	private String player2Username;
	String winnerMsg;

	public GamePanel(int gameMode, String player1, String player2)
	{
		this.gameMode = gameMode;
		this.player1Username = player1;
		this.player2Username = player2;
		random = new Random();

		snake1 = new PlayerSnake(GAME_UNITS, UNIT_SIZE, "RIGHT");
		snake1.x[0] = 0;
		snake1.y[0] = 0;

		if (gameMode == 2)
		{
			snake2 = new PlayerSnake(GAME_UNITS, UNIT_SIZE, "LEFT");
		}
		else if (gameMode == 3)
		{
			aiSnake = new AISnake(GAME_UNITS, UNIT_SIZE, "LEFT");
		}

		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		keyAdapter = new MyKeyAdapter();
		this.addKeyListener(keyAdapter);
		startGame();
	}

	private void startGame()
	{
		spawnApple(whoAteApple);
		gameState = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}

	private void draw(Graphics g)
	{
		if (gameState)
		{
			// Draw apples
			g.setColor(Color.red);
			g.fillOval(apple1X, apple1Y, UNIT_SIZE, UNIT_SIZE);

			if (gameMode == 2 || gameMode == 3) {
				g.setColor(Color.orange);
				g.fillOval(apple2X, apple2Y, UNIT_SIZE, UNIT_SIZE);
			}

			// Draw snakes
			drawSnake(g, snake1, Color.green, new Color(45, 180, 0));

			// Display player 1 score
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			g.drawString(player1Username + " Skor: " + snake1.applesEaten, 20, 40);

			// For 2-player mode
			if (gameMode == 2)
			{
				drawSnake(g, snake2, Color.blue, new Color(0, 0, 200));
				g.drawString(player2Username + " Skor: " + snake2.applesEaten, SCREEN_WIDTH - 300, 40);
			}
			// For AI mode
			else if (gameMode == 3)
			{
				drawSnake(g, aiSnake, Color.red, Color.MAGENTA);
				g.drawString("Bilgisayar Skor: " + aiSnake.applesEaten, SCREEN_WIDTH / 2 - 200, 40);
			}
		}
		else {
			gameOver(g);
		}
	}

	private void drawSnake(Graphics g, SnakeBase snake, Color headColor, Color bodyColor)
	{
		for (int i = 0; i < snake.bodyParts; i++)
		{
			if (i == 0)
			{
				g.setColor(headColor);
			}
			else
			{
				g.setColor(bodyColor);
			}
			g.fillRect(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
		}
	}

	private void spawnApple(int whoAteApple)
	{
		if (whoAteApple == 0)
		{
			apple1X = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
			apple1Y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

			if (gameMode == 2 || gameMode == 3)
			{
				apple2X = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
				apple2Y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
			}
		}
		else if (whoAteApple == 1)
		{
			apple1X = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
			apple1Y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
		}
		else if (whoAteApple == 2)
		{
			apple2X = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
			apple2Y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
		}
	}

	private void gameOver(Graphics g)
	{
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		g.drawString("Oyun Bitti!", 250, 325);

		// Displaying scores at the end
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		g.drawString(player1Username + " Skor: " + snake1.applesEaten, 250, 400);

		if (gameMode == 2 || gameMode == 3)
		{
			if (gameMode == 2)
				g.drawString(player2Username + " Skor: " + snake2.applesEaten, 250, 450);
			else
				g.drawString("Bilgisayar Skor: " + aiSnake.applesEaten, 250, 450);
		}

		// Display winner message

		if (gameMode == 3) {
			// Against AI
			if (aiSnake.applesEaten > snake1.applesEaten) {
				winnerMsg = "Bilgisayar Kazandı!";
			} else if (aiSnake.applesEaten < snake1.applesEaten) {
				winnerMsg = player1Username + " Kazandı!";
			} else {
				winnerMsg = "Berabere!";
			}
		} else {
			// Two player mode
			if (snake1.applesEaten > snake2.applesEaten) {
				winnerMsg = player1Username + " Kazandı!";
			} else if (snake2.applesEaten > snake1.applesEaten) {
				winnerMsg = player2Username + " Kazandı!";
			} else {
				winnerMsg = "Berabere!";
			}
		}

		g.setFont(new Font("Ink Free", Font.BOLD, 50));
		g.drawString(winnerMsg, 250, 500);

		// "New Game" button
		JButton newGameButton = new JButton("Yeni Oyun");
		newGameButton.setBounds(300, 550, 200, 50);
		newGameButton.addActionListener(e -> restartGame(newGameButton));

		this.setLayout(null);
		this.add(newGameButton);
	}

	private void restartGame(JButton newGameButton)
	{
		if (newGameButton != null) {
			this.remove(newGameButton);  // Remove from panel
			newGameButton = null;  // Set to null to avoid reuse
		}

		// Reset player snake
		snake1 = new PlayerSnake(GAME_UNITS, UNIT_SIZE, "LEFT");

		// Reset second snake if in 2-player mode
		if (gameMode == 2) {
			snake2 = new PlayerSnake(GAME_UNITS, UNIT_SIZE, "RIGHT");
		} else {
			snake2 = null;
		}

		// Reset AI Snake if in AI mode
		if (gameMode == 3) {
			aiSnake = new AISnake(GAME_UNITS, UNIT_SIZE, "RIGHT");
		} else {
			aiSnake = null;
		}

		// Reset apple position
		spawnApple(0);


		// Reset game state
		gameState = true;

		// Restart timer
		if (timer != null) {
			timer.stop();
		}
		timer = new Timer(DELAY, this);
		timer.start();

		// Make sure key listeners are re-added
		this.removeKeyListener(keyAdapter); // Remove old listener
		this.addKeyListener(keyAdapter);    // Add new listener
		this.setFocusable(true);
		this.requestFocusInWindow();

		// Force repaint
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int snake1Collision = 0;
		int snake2Collision = 0;
		int aiSnakeCollision = 0;
		boolean playerHeadCollisionWithAI = false;

		if (gameState) {
			// Yılanların hareketi
			snake1.move();
			if (gameMode == 2) {
				snake2.move();
			}
			else if (gameMode == 3)
			{
				aiSnake.moveAI(apple2X, apple2Y);
			}

			// yılanlar elmaya temas etti mi?
			if (snake1.checkApple(apple1X, apple1Y)) {
				spawnApple(1);
			}
			if (gameMode == 2 && snake2.checkApple(apple2X, apple2Y)) {
				spawnApple(2);
			}
			if (gameMode == 3 && aiSnake.checkApple(apple2X, apple2Y)) {
				spawnApple(2);
			}

			snake1Collision = snake1.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake2);
			snake2Collision = gameMode == 2 ? snake2.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake1) : 0;
			aiSnakeCollision = gameMode == 3 ? aiSnake.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake1) : 0;

			// Handle self-collision and player-player or player-AI collision
			if (snake1Collision == 1 || snake2Collision == 1 || aiSnakeCollision == 1) {
				gameState = false;
				timer.stop();
				declareWinner(snake1Collision == 1, snake2Collision == 1, aiSnakeCollision == 1);
			} else if (snake1Collision == 2 || snake2Collision == 2 || aiSnakeCollision == 2) {
				gameState = false;
				timer.stop();
				declareWinner(snake1Collision == 2, snake2Collision == 2, aiSnakeCollision == 2);
			}
			repaint();
		}
	}

	private void declareWinner(boolean snake1Lost, boolean snake2Lost, boolean aiSnakeLost) {

		if (snake1Lost) {
			winnerMsg = gameMode == 2 ? player2Username + " Kazandı!" : "AI Kazandı!";
		} else if (snake2Lost) {
			winnerMsg = player1Username + " Kazandı!";
		} else if (aiSnakeLost) {
			winnerMsg = player1Username + " Kazandı!";
		}

	}

	public class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyCode())
			{
				// 1. oyuncu kontroller
				case KeyEvent.VK_LEFT:
					if (!snake1.direction.equals("RIGHT")) snake1.direction = "LEFT";
					break;
				case KeyEvent.VK_RIGHT:
					if (!snake1.direction.equals("LEFT")) snake1.direction = "RIGHT";
					break;
				case KeyEvent.VK_UP:
					if (!snake1.direction.equals("DOWN")) snake1.direction = "UP";
					break;
				case KeyEvent.VK_DOWN:
					if (!snake1.direction.equals("UP")) snake1.direction = "DOWN";
					break;

				// 2. oyuncu kontroller
				case KeyEvent.VK_A:
					if (gameMode == 2 && !snake2.direction.equals("RIGHT")) snake2.direction = "LEFT";
					break;
				case KeyEvent.VK_D:
					if (gameMode == 2 && !snake2.direction.equals("LEFT")) snake2.direction = "RIGHT";
					break;
				case KeyEvent.VK_W:
					if (gameMode == 2 && !snake2.direction.equals("DOWN")) snake2.direction = "UP";
					break;
				case KeyEvent.VK_S:
					if (gameMode == 2 && !snake2.direction.equals("UP")) snake2.direction = "DOWN";
					break;
			}
		}
	}

	private boolean checkSnakeBodyCollision(SnakeBase playerSnake, SnakeBase otherSnake) {
		for (int i = 1; i < otherSnake.bodyParts; i++) {
			if (playerSnake.x[0] == otherSnake.x[i] && playerSnake.y[0] == otherSnake.y[i]) {
				return true; // Collision detected
			}
		}
		return false; // No collision
	}

	private boolean checkPlayerHeadCollisionWithAISnake(SnakeBase playerSnake, SnakeBase aiSnake) {
		// Check if the player's head collides with the AI snake's head
		if (playerSnake.x[0] == aiSnake.x[0] && playerSnake.y[0] == aiSnake.y[0]) {
			return true;
		}

		// Check if the player's head collides with any part of the AI snake's body
		for (int i = 1; i < aiSnake.bodyParts; i++) { // start from 1 because the head is already checked
			if (playerSnake.x[0] == aiSnake.x[i] && playerSnake.y[0] == aiSnake.y[i]) {
				return true;
			}
		}
		return false;
	}


}

