import javax.swing.*;
import java.awt.*;

public class GameMenu extends JFrame {
	private int gameMode;
	private String player1, player2;

	public GameMenu() {
		setTitle("Yılan Oyunu - Oyun Modu Seçiniz");	// Pencere başlığını ayarla
		setSize(400, 300);		// Pencere boyutunu ayarla (genişlik: 400, yükseklik: 300)

		// Pencere kapatıldığında programı tamamen sonlandır
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Bileşenleri dikey olarak 3 satırda yerleştirmek için GridLayout kullan
		setLayout(new GridLayout(3, 1));

		// 3 farklı oyun modu için butonlar oluştur
		JButton singlePlayerBtn = new JButton("Tek Oyunculu");
		JButton twoPlayerBtn = new JButton("İki Oyunculu");
		JButton vsComputerBtn = new JButton("Bilgisayara Karşı");

		// "Tek Oyunculu" butonuna tıklanırsa, 1 parametresiyle giriş ekranını göster
		singlePlayerBtn.addActionListener(e -> showLoginScreen(1));

		// "İki Oyunculu" butonuna tıklanırsa, 2 parametresiyle giriş ekranını göster
		twoPlayerBtn.addActionListener(e -> showLoginScreen(2));

		// "Bilgisayara Karşı" butonuna tıklanırsa, 3 parametresiyle giriş ekranını göster
		vsComputerBtn.addActionListener(e -> showLoginScreen(3));

		// Butonları pencereye ekle
		add(singlePlayerBtn);
		add(twoPlayerBtn);
		add(vsComputerBtn);

		setLocationRelativeTo(null);			// Pencerenin ekranın ortasında açılmasını sağlar
		setVisible(true);		// Pencereyi görünür hale getir
	}

	private void showLoginScreen(int mode) {
		// Seçilen oyun modunu sakla (1: Tek oyunculu, 2: İki oyunculu, 3: Bilgisayara karşı)
		this.gameMode = mode;

		// Yeni bir giriş penceresi oluştur
		JFrame loginFrame = new JFrame("Kullanıcı adınızı giriniz");
		loginFrame.setSize(400, 300);

		// Eğer iki oyunculu mod seçildiyse 3 satırlık, değilse 2 satırlık GridLayout kullan
		loginFrame.setLayout(new GridLayout(mode == 2 ? 3 : 2, 1));

		// Oyuncu 1 için panel ve giriş alanı oluştur
		JPanel player1Panel = new JPanel(new FlowLayout());
		JLabel player1Label = new JLabel("Oyuncu 1:");
		JTextField username1Field = new JTextField(15);
		player1Panel.add(player1Label);
		player1Panel.add(username1Field);

		// Oyuncu 2 için panel ve giriş alanı oluştur (yalnızca iki oyunculu modda gösterilir)
		JPanel player2Panel = new JPanel(new FlowLayout());
		JLabel player2Label = new JLabel("Oyuncu 2:");
		JTextField username2Field = new JTextField(15);
		player2Panel.add(player2Label);
		player2Panel.add(username2Field);

		// Oyunu başlatmak için buton oluştur
		JButton startButton = new JButton("Başlat");

		// Oyuncu 1 panelini ekle
		loginFrame.add(player1Panel);

		// Eğer iki oyunculu moddaysa Oyuncu 2 panelini de ekle
		if (mode == 2) loginFrame.add(player2Panel);

		loginFrame.add(startButton);	// Başlat butonunu ekle

		// Başlat butonuna tıklanma olayı ekle
		startButton.addActionListener(e -> {
			// Oyuncu 1 ismini al ve boşsa varsayılan isim ver
			player1 = username1Field.getText().trim();
			if (player1.isEmpty()) player1 = "1. Oyuncu";

			// Oyuncuların kullanıcı adlarını ayarla.
			if (mode == 2) {
				player2 = username2Field.getText().trim();
				if (player2.isEmpty()) player2 = "2. Oyuncu";
			} else {
				// Diğer modlarda oyuncu 2 bilgisayar olur
				player2 = "Bilgisayar";
			}
			// Giriş penceresini kapat ve oyunu başlat
			loginFrame.dispose();
			startGame();
		});

		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// Pencere kapatıldığında tüm uygulamayı kapat
		loginFrame.setResizable(false);		// Pencerenin boyutunun değiştirilmesini engelle
		loginFrame.setLocationRelativeTo(null);		// Pencerenin ekranın ortasında açılmasını sağlar
		loginFrame.setVisible(true); 	// Pencereyi görünür hale getir
	}

	private void startGame() {
		this.dispose(); // Menüyü kapat
		new SnakeGame(gameMode, player1, player2); // Oyuna kullanıcı adlarını ve oyun modunu gönder
	}

}
