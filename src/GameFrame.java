import javax.swing.JFrame;

public class GameFrame extends JFrame {
	// Constructor - Oyun penceresini başlatır
	public GameFrame(int gameMode, String player1, String player2) {
		// Yeni bir panel oluştur ve bunu GameFrame nesnesine ekle
		this.add(new GamePanel(gameMode, player1, player2));

		// Pencere başlığını ayarla
		this.setTitle("Snake Game");

		// Pencereyi kapattığında uygulamanın sonlanmasını sağla
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Pencerenin boyutunun değiştirilememesini sağla
		this.setResizable(false);

		// Pencereyi içeriklerine göre uygun şekilde boyutlandır
		this.pack();

		// Pencereyi görünür hale getir
		this.setVisible(true);

		// Pencerenin ekranın ortasında açılmasını sağlar
		this.setLocationRelativeTo(null);
	}
}
