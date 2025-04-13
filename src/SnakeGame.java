public class SnakeGame {
	public SnakeGame(int gameMode, String player1, String player2) {
		GameFrame gameFrame = new GameFrame(gameMode, player1, player2);
	}

	public static void main(String[] args) {
		new GameMenu(); // Start with the menu
	}
}
