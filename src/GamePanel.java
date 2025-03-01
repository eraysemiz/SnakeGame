import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

	static final int SCREEN_WIDTH = 800;
	static final int SCREEN_HEIGHT = 800;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;

	private Snake snake1;
	private Snake snake2;
	private boolean isTwoPlayerMode;

	private int apple1X, apple1Y;
	private int apple2X, apple2Y;
	private boolean gameState = false;

	private Timer timer;
	private Random random;

	public GamePanel(boolean isTwoPlayerMode)
	{
		this.isTwoPlayerMode = isTwoPlayerMode;
		random = new Random();

		snake1 = new Snake(GAME_UNITS, UNIT_SIZE, "RIGHT");
		if (isTwoPlayerMode)
		{
			snake2 = new Snake(GAME_UNITS, UNIT_SIZE, "LEFT");
		}

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
			// 1. Elmayı çiz
			g.setColor(Color.red);
			g.fillOval(apple1X, apple1Y, UNIT_SIZE, UNIT_SIZE);

			// 2. elmayı çiz
			if (isTwoPlayerMode)
			{
				g.setColor(Color.orange);
				g.fillOval(apple2X, apple2Y, UNIT_SIZE, UNIT_SIZE);
			}

			// 1. Yılanı çiz
			drawSnake(g, snake1, Color.green, new Color(45, 180, 0));

			// 2. yılanı çiz
			if (isTwoPlayerMode)
			{
				drawSnake(g, snake2, Color.blue, new Color(0, 0, 200));
			}

			// Skoru göster
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			g.drawString("1. Oyuncu Skor: " + snake1.applesEaten, 20, 40);

			if (isTwoPlayerMode)
				g.drawString("2. Oyuncu Skor: " + snake2.applesEaten, 600, 40);
		}
		else
			gameOver(g);
	}

	private void drawSnake(Graphics g, Snake snake, Color headColor, Color bodyColor)
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

	private void spawnApple()
	{
		apple1X = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		apple1Y = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

		if (isTwoPlayerMode)
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

		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		g.drawString("Oyuncu1 Skor: " + snake1.applesEaten, 250, 400);
		if (isTwoPlayerMode)
		{
			g.drawString("Oyuncu2 Skor: " + snake2.applesEaten, 250, 450);
		}

		if (isTwoPlayerMode)
		{

			String msg;
			if (snake1.applesEaten > snake2.applesEaten)
			{
				msg = "1. Oyuncu Kazandı!";
			}
			else if (snake2.applesEaten > snake1.applesEaten)
			{
				msg = "2. Oyuncu Kazandı!";
			}
			else
			{
				msg = "Berabere!";
			}

			g.setFont(new Font("Ink Free", Font.BOLD, 50));
			g.drawString(msg, 250, 500);
		}

		JButton newGameButton = new JButton("Yeni Oyun");
		newGameButton.setBounds(300, 550, 200, 50);
		newGameButton.addActionListener(e -> restartGame());

		this.setLayout(null);
		this.add(newGameButton);
	}

	private void restartGame()
	{
		snake1 = new Snake(GAME_UNITS, UNIT_SIZE, "RIGHT");
		if (isTwoPlayerMode)
			snake2 = new Snake(GAME_UNITS, UNIT_SIZE, "LEFT");
		else
			snake2 = null;

		gameState = true;
		this.removeAll();
		this.revalidate();
		this.repaint();
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (gameState) {
			// Yılanların hareketi
			snake1.move();
			if (isTwoPlayerMode) {
				snake2.move();
			}

			// yılanlar elmaya temas etti mi?
			if (snake1.checkApple(apple1X, apple1Y)) {
				spawnApple();
			}
			if (isTwoPlayerMode && snake2.checkApple(apple1X, apple1Y)) {
				spawnApple();
			}

			// yılanlar çarpışma kontrol
			boolean snake1Collision = snake1.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake2);
			boolean snake2Collision = isTwoPlayerMode && snake2.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake1);

			if (snake1Collision || snake2Collision)
			{
				gameState = false;
				timer.stop();
			}
			repaint();
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
					if (isTwoPlayerMode && !snake2.direction.equals("RIGHT")) snake2.direction = "LEFT";
					break;
				case KeyEvent.VK_D:
					if (isTwoPlayerMode && !snake2.direction.equals("LEFT")) snake2.direction = "RIGHT";
					break;
				case KeyEvent.VK_W:
					if (isTwoPlayerMode && !snake2.direction.equals("DOWN")) snake2.direction = "UP";
					break;
				case KeyEvent.VK_S:
					if (isTwoPlayerMode && !snake2.direction.equals("UP")) snake2.direction = "DOWN";
					break;
			}
		}
	}
}

