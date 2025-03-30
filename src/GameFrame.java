import javax.swing.JFrame;
import java.awt.*;

public class GameFrame extends JFrame {

	public GameFrame(int gameMode, String player1, String player2) {
		this.add(new GamePanel(gameMode, player1, player2)); // Pass to GamePanel
		this.setTitle("Snake Game");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
}
