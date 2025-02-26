import javax.naming.ldap.UnsolicitedNotification;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;	// item boyutu, itemların boyutunu ayarlar
	static final int GAME_UNITS = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE;	// MAX UNIT SAYISI
	static final int DELAY = 100;		// oyun hızı
	final int[] x = new int[GAME_UNITS]; // yılanın gövdesinin x koordinatları
	final int[] y = new int[GAME_UNITS]; // yılanın gövdesinin y koordinatları
	private int bodyParts = 6;
	private int applesEaten = 0;
	private int appleX;		// elma konumu
	private int appleY;
	private String direction = "RIGHT";
	private boolean gameState = false;
	Timer timer;
	Random random;


	public GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}

	private void startGame()
	{
		spawnApple();
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
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++)
			{
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0,i * UNIT_SIZE, SCREEN_WIDTH , i * UNIT_SIZE);
			}
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			for (int i = 0; i < bodyParts; i++)
			{
				if (i == 0) 	// yılanın kafası
				{
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else		// yılanın gövdesi
				{
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, 35);
		}
		else
		{
			gameOver(g);
		}

	}

	private void spawnApple()
	{
		appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
	}

	private void move()
	{
		for (int i = bodyParts; i > 0; i--)
		{
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		switch (direction)
		{
			case "UP":
				y[0] = y[0] - UNIT_SIZE;
				break;
			case "DOWN":
				y[0] = y[0] + UNIT_SIZE;
				break;
			case "LEFT":
				x[0] = x[0] - UNIT_SIZE;
				break;
			case "RIGHT":
				x[0] = x[0] + UNIT_SIZE;
				break;
		}
	}

	private void checkApple()
	{
		if (x[0] == appleX && y[0] == appleY)
		{
			bodyParts++;
			applesEaten++;
			spawnApple();
		}
	}

	private void checkCollisions()
	{
		for (int i = bodyParts; i > 0; i--)
		{
			if (x[0] == x[i] && y[0] == y[i]) // yılan kendine çarparsa
			{
				gameState = false;
				timer.stop();
			}

			// Wrap the snake to the opposite side when it crosses the border
			if (x[0] < 0) {
				x[0] = SCREEN_WIDTH - UNIT_SIZE;  // Move to the right edge
			} else if (x[0] >= SCREEN_WIDTH) {
				x[0] = 0;  // Move to the left edge
			}

			if (y[0] < 0) {
				y[0] = SCREEN_HEIGHT - UNIT_SIZE;  // Move to the bottom edge
			} else if (y[0] >= SCREEN_HEIGHT) {
				y[0] = 0;  // Move to the top edge
			}
		}
	}

	private void gameOver(Graphics g)
	{
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Game Over!", (SCREEN_WIDTH - metrics.stringWidth("Game Over!")) / 2, SCREEN_HEIGHT / 2);

		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		metrics = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 - 100);

		JButton newGameButton = new JButton("New Game");
		newGameButton.setFont(new Font("Ink Free", Font.BOLD, 20));
		newGameButton.setBounds((SCREEN_WIDTH / 2) - 75, SCREEN_HEIGHT / 2 + 50, 150, 50);

		this.setLayout(null);
		this.add(newGameButton);
		this.repaint();

		newGameButton.addActionListener(e -> restartGame());
	}

	private void restartGame()
	{
		bodyParts = 6;
		applesEaten = 0;
		direction = "RIGHT";
		gameState = true;

		// Clear the snake's position
		for (int i = 0; i < bodyParts; i++) {
			x[i] = 0;
			y[i] = 0;
		}

		spawnApple(); // Spawn a new apple
		this.removeAll(); // Remove all components (including button)
		this.revalidate();
		this.repaint();

		// Restart the game loop
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (gameState)
		{
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_LEFT:
					if (!direction.equals("RIGHT"))
						direction = "LEFT";
					break;
				case KeyEvent.VK_RIGHT:
					if (!direction.equals("LEFT"))
						direction = "RIGHT";
					break;
				case KeyEvent.VK_UP:
					if (!direction.equals("DOWN"))
						direction = "UP";
					break;
				case KeyEvent.VK_DOWN:
					if (!direction.equals("UP"))
						direction = "DOWN";
					break;
			}
		}

	}
}
