import javax.swing.JFrame;
import java.awt.*;

public class GameFrame extends JFrame {

	public GameFrame() throws HeadlessException {

		GamePanel gamePanel = new GamePanel(false);

		this.add(gamePanel);
		this.setTitle("Snake"); // Açılacak pencerenin ismi
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null); // oyun penceresini ekranın ortasında açar
	}
}
