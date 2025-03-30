import javax.swing.*;
import java.awt.*;

public class GameMenu extends JFrame {
	private int gameMode;
	private String player1, player2;

	public GameMenu() {
		setTitle("Yılan Oyunu - Oyun Modu Seçiniz");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(3, 1));

		JButton singlePlayerBtn = new JButton("Tek Oyunculu");
		JButton twoPlayerBtn = new JButton("İki Oyunculu");
		JButton vsComputerBtn = new JButton("Bilgisayara Karşı");

		singlePlayerBtn.addActionListener(e -> showLoginScreen(1));
		twoPlayerBtn.addActionListener(e -> showLoginScreen(2));
		vsComputerBtn.addActionListener(e -> showLoginScreen(3));

		add(singlePlayerBtn);
		add(twoPlayerBtn);
		add(vsComputerBtn);

		setLocationRelativeTo(null); // Centers the frame on the screen
		setVisible(true);
	}

	private void showLoginScreen(int mode) {
		this.gameMode = mode;
		JFrame loginFrame = new JFrame("Kullanıcı adınızı giriniz");
		loginFrame.setSize(400, 300);
		loginFrame.setLayout(new GridLayout(mode == 2 ? 3 : 2, 1));

		JTextField username1Field = new JTextField("Oyuncu 1: ");
		JTextField username2Field = new JTextField("Oyuncu 2: ");
		JButton startButton = new JButton("Başlat");

		loginFrame.add(username1Field);
		if (mode == 2) loginFrame.add(username2Field);
		loginFrame.add(startButton);

		startButton.addActionListener(e -> {
			player1 = username1Field.getText().replace("Oyuncu 1: ", "").trim();
			if (mode == 2)
			{
				player2 = username2Field.getText().replace("Oyuncu 2: ", "").trim();
				if (player2.isEmpty() || player2.isBlank())
					player2 = "2. Oyuncu";
			}
			else
			{
				player2 = "Bilgisayar";
			}
			loginFrame.dispose();

			if (player1.isEmpty() || player1.isBlank())
				player1 = "1. Oyuncu";

			startGame();
		});

		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setResizable(false);
		loginFrame.setLocationRelativeTo(null); // Centers the window
		loginFrame.setVisible(true); // Makes it visible
	}

	private void startGame() {
		this.dispose(); // Close menu
		new SnakeGame(gameMode, player1, player2); // Pass usernames and mode
	}

	public static void main(String[] args) {
		new GameMenu();
	}
}
