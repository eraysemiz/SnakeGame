import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

	//  Sabit Değerler (Ekran ve oyun ayarları)
	static final int SCREEN_WIDTH = 1000; // Ekran genişliği (piksel)
	static final int SCREEN_HEIGHT = 750; // Ekran yüksekliği (piksel)
	static final int UNIT_SIZE = 25; // Oyun ızgarasında bir birimin boyutu (piksel)
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; // Toplam birim sayısı
	static final int DELAY = 100; // Oyun döngüsündeki gecikme süresi (ms)

	//  Oyun Durumu
	private boolean gameState = false; // Oyun aktif mi?
	private final int gameMode; // Oyun modu: 1 = Tek oyuncu, 2 = İki oyuncu, 3 = Bilgisayara karşı
	private String winnerMsg; // Oyun sonunda gösterilecek kazanan mesajı
	private JButton newGameButton;

	//  Yılan
	private PlayerSnake snake1; // Oyuncu 1'in yılanı
	private PlayerSnake snake2; // Oyuncu 2'nin yılanı
	private AISnake aiSnake;    // Yapay zekâ yılanı (Bilgisayar)

	//  Elma
	private int apple1X, apple1Y; // Oyuncu 1 için elma
	private int apple2X, apple2Y;  // Oyuncu 2 veya AI için elma
	private int whoAteApple = 0;  // Hangi oyuncunun elmayı yediğini tutar

	//  Zamanlayıcılar
	private Timer gameTimer;        // Oyun döngüsü için zamanlayıcı
	private Timer countdownTimer;   // Geri sayım zamanlayıcısı (1 dakikalık süre)
	private int timeLeft = 60;      // Kalan süre (saniye)

	//  Girdi ve Yardımcı Nesneler
	private MyKeyAdapter keyAdapter; // Klavye girişlerini dinleyen adapter
	private Random random;           // Elma konumlarını rastgele belirlemek için

	//  Oyuncu Kullanıcı Adları
	private final String player1Username; // Oyuncu 1'in kullanıcı adı
	private final String player2Username; // Oyuncu 2'nin kullanıcı adı

	public GamePanel(int gameMode, String player1, String player2)
	{
		this.gameMode = gameMode;  // Seçilen oyun modunu (1 = tek oyuncu, 2 = iki oyuncu, 3 = yapay zeka karşısı) kaydediyoruz
		this.player1Username = player1;  // Oyuncu 1'in kullanıcı adını ayarlıyoruz
		this.player2Username = player2;  // Oyuncu 2'nin kullanıcı adını ayarlıyoruz
		random = new Random();  // Rastgele sayılar üretmek için random nesnesini başlatıyoruz (örneğin, elma konumu için)

		// İlk oyuncunun yılanını başlatıyoruz
		snake1 = new PlayerSnake("RIGHT");  // Oyuncu 1'in yılanını sağa hareket edecek şekilde başlatıyoruz

		// Eğer oyun modu iki oyuncu (gameMode == 2) ise, ikinci oyuncunun yılanını başlatıyoruz
		if (gameMode == 2)
		{
			snake2 = new PlayerSnake("LEFT");  // Oyuncu 2'nin yılanını sola hareket edecek şekilde başlatıyoruz
		}
		// Eğer oyun modu yapay zekaya karşı (gameMode == 3) ise, yapay zeka yılanını başlatıyoruz
		else if (gameMode == 3)
		{
			aiSnake = new AISnake("LEFT", snake1);  // Yapay zeka yılanını sola hareket edecek şekilde başlatıyoruz
		}
		setInitialSnakeBody();

		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));  // Panelin tercih edilen boyutunu ayarlıyoruz
		this.setBackground(Color.black);  // Panelin arka planını siyah olarak ayarlıyoruz
		this.setFocusable(true);  // Paneli odaklanabilir hale getiriyoruz, böylece tuş basımlarını algılayabilir.

		keyAdapter = new MyKeyAdapter();			// Klavye girişlerini işlemek için bir key listener (tuş dinleyici) oluşturuyoruz
		this.addKeyListener(keyAdapter); 			 // listener'ı panele ekliyoruz, böylece tuşları dinleyebiliriz
		startGame();  			// Oyunu başlatmak için startGame() metodunu çağırıyoruz, elma spawn işlemi ve zamanlayıcıları başlatıyoruz
	}

	private void startGame()
	{
		spawnApple(whoAteApple);  		// Elmaları rastgele bir pozisyonda spawn ediyoruz
		gameState = true;  	// Oyunun başladığını belirtiyoruz (gameState = true)

		// 75 ms gecikmeli oyun zamanlayıcısını başlatıyoruz. Böylece oyun paneli her 75 ms bir yeniden çizilecek.
		gameTimer = new Timer(DELAY, this);
		gameTimer.start();  // Zamanlayıcıyı çalıştırıyoruz

		// Eğer oyun modu 2 (iki oyuncu) veya 3 (yapay zeka) ise, bir geri sayım başlatıyoruz
		if (gameMode == 2 || gameMode == 3)
		{
			// Geri sayım zamanlayıcısını 1 saniyelik aralıklarla başlatıyoruz
			countdownTimer = new Timer(1000, e -> {
				timeLeft--;  // Kalan süreyi bir saniye azaltıyoruz

				// Eğer süre bitmişse (timeLeft < 0), geri sayım zamanlayıcısını durduruyoruz
				if (timeLeft < 0) {
					countdownTimer.stop();  // Geri sayım zamanlayıcısını durduruyoruz
					gameTimer.stop();  // Oyunun ana zamanlayıcısını durduruyoruz
					declareWinner(false, false, false, true); // Kazananı belirle
					gameState = false;  // Oyunun bittiğini belirtiyoruz
					repaint();  // Ekranı yeniliyoruz (gameState değiştiği için)
				}
				repaint();  // Her saniyede bir ekranı yeniliyoruz (geri sayım güncellenmesi için)
			});

			countdownTimer.start();  // Oyun sayacını başlatıyoruz
		}
	}


	@Override
	public void paintComponent(Graphics g)
	{
		// paintComponent panelde her bir değişiklik olduğunda otomatik çağırılıyor.
		// JPanel'in paintComponent metodu boş panel çağırıldığında boş görünmez bir panel çizer.
		super.paintComponent(g);

		// Boş panelin üstüne oyunumuzu çiziyoruz
		drawWalls(g);
		drawGame(g);
	}

	private void drawWalls(Graphics g) {
		g.setColor(Color.DARK_GRAY); // Duvar rengi

		// Üst duvar
		g.fillRect(0, 0, SCREEN_WIDTH, UNIT_SIZE);

		// Alt duvar
		g.fillRect(0, SCREEN_HEIGHT - UNIT_SIZE, SCREEN_WIDTH, UNIT_SIZE);

		// Sol duvar
		g.fillRect(0, 0, UNIT_SIZE, SCREEN_HEIGHT);

		// Sağ duvar
		g.fillRect(SCREEN_WIDTH - UNIT_SIZE, 0, UNIT_SIZE, SCREEN_HEIGHT);
	}

	private void drawGame(Graphics g)
	{
		// Eğer oyun devam ediyorsa (gameState = true)
		if (gameState)
		{
			// Oyun modu 2 (iki oyuncu) veya 3 (yapay zeka) ise geri sayımı gösteriyoruz
			if (gameMode == 2 || gameMode == 3)
			{
				// Rengi beyaz yapıyoruz ve yazı tipini ayarlıyoruz
				g.setColor(Color.white);
				g.setFont(new Font("Arial", Font.BOLD, 20));

				// Kalan süreyi (timeLeft) ekranda gösteriyoruz
				g.drawString("Süre: " + timeLeft, SCREEN_WIDTH / 2 - 40, 30);
			}

			// Elmayı çiziyoruz (kırmızı renkte)
			g.setColor(Color.red);
			g.fillOval(apple1X, apple1Y, UNIT_SIZE, UNIT_SIZE);

			// Oyun modu 2 (iki oyuncu) veya 3 (yapay zeka) ise ikinci elmayı çiziyoruz (sarı renkte)
			if (gameMode == 2 || gameMode == 3) {
				g.setColor(Color.YELLOW);
				g.fillOval(apple2X, apple2Y, UNIT_SIZE, UNIT_SIZE);
			}

			// 1. yılanı çiziyoruz
			drawSnake(g, snake1, Color.green, new Color(45, 180, 0));

			// Oyuncu 1'in skorunu gösteriyoruz
			FontMetrics metrics = g.getFontMetrics();
			String player1ScoreText = player1Username + ": " + snake1.getApplesEaten();
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			g.drawString(player1ScoreText, 20, 40);

			// Eğer oyun modu 2 (iki oyuncu) ise ikinci oyuncunun yılanını çiziyoruz
			String player2ScoreText;
			int textWidth;
			if (gameMode == 2) {
				g.setColor(Color.YELLOW);
				player2ScoreText = player2Username + ": " + snake2.getApplesEaten();
				textWidth = metrics.stringWidth(player2ScoreText) * 2;
				g.drawString(player2ScoreText, SCREEN_WIDTH - textWidth, 40);
				drawSnake(g, snake2,new Color(0, 0, 139), Color.blue);
			}
			// Eğer oyun modu 3 (yapay zeka) ise yapay zekanın yılanını çiziyoruz
			else if (gameMode == 3) {
				g.setColor(Color.YELLOW);
				player2ScoreText = "Bilgisayar: " + aiSnake.applesEaten;
				textWidth = metrics.stringWidth(player2ScoreText) * 2;
				g.drawString(player2ScoreText, SCREEN_WIDTH - textWidth, 40);
				drawSnake(g, aiSnake, new Color(139, 0 ,0), Color.RED);
			}
		}
		else {
			// Eğer oyun bitmişse (gameState = false), 'gameOver' fonksiyonunu çağırıyoruz
			gameOver(g);
		}
	}

	private void setInitialSnakeBody()
	{
		snake1.x[0] = SCREEN_WIDTH / 4;
		snake1.y[0] = SCREEN_HEIGHT / 2;

		snake1.x[1] = snake1.x[0] - 1;
		snake1.y[1] = snake1.y[0];
		snake1.x[2] = snake1.x[1] - 1;
		snake1.y[2] = snake1.y[1];
		snake1.x[3] = snake1.x[2] - 1;
		snake1.y[3] = snake1.y[2];

		if (gameMode == 2)
		{
			snake2.x[0] = SCREEN_WIDTH * 3 / 4;
			snake2.y[0] = SCREEN_HEIGHT / 2;

			snake2.x[1] = snake2.x[0] + 1;
			snake2.y[1] = snake2.y[0];
			snake2.x[2] = snake2.x[1] + 1;
			snake2.y[2] = snake2.y[1];
			snake2.x[3] = snake2.x[2] + 1;
			snake2.y[3] = snake2.y[2];
		}
		else if (gameMode == 3)
		{
			aiSnake.x[0] = SCREEN_WIDTH * 3 / 4;
			aiSnake.y[0] = SCREEN_HEIGHT / 2;

			aiSnake.x[1] = aiSnake.x[0] + 1;
			aiSnake.y[1] = aiSnake.y[0];
			aiSnake.x[2] = aiSnake.x[1] + 1;
			aiSnake.y[2] = aiSnake.y[1];
			aiSnake.x[3] = aiSnake.x[2] + 1;
			aiSnake.y[3] = aiSnake.y[2];
		}
	}

	private void drawSnake(Graphics g, SnakeBase snake, Color headColor, Color bodyColor)
	{
		// Yılanın tüm parçalarını çizen bir döngü başlatıyoruz
		for (int i = 0; i < snake.bodyParts; i++)
		{
			// Eğer i sıfırsa, bu yılanın başıdır, baş rengini belirliyoruz
			if (i == 0)
			{
				g.setColor(headColor);
			}
			else
			{
				g.setColor(bodyColor); // Yılanın gövdesinin rengini belirliyoruz
			}
			// Yılanın her bir parçasını ekrana çiziyoruz
			// x[i] ve y[i] yılanın parçasının koordinatlarını, UNIT_SIZE ise parçaların boyutunu belirler
			g.fillRect(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
		}
	}


	private void spawnApple(int whoAteApple)
	{
		/*
			Bu metot elmaların koordinatları değiştirir. Swing delay sonrası yeniden paintComponent
			methodunu çağırdığında elmanın konumu değişmiş olur.
		 */
		int min = 1;
		int maxX = (SCREEN_WIDTH / UNIT_SIZE) - 2;
		int maxY = (SCREEN_HEIGHT / UNIT_SIZE) - 2;

		if (whoAteApple == 0) {
			apple1X = (random.nextInt(maxX - min + 1) + min) * UNIT_SIZE;
			apple1Y = (random.nextInt(maxY - min + 1) + min) * UNIT_SIZE;

			if (gameMode == 2 || gameMode == 3) {
				apple2X = (random.nextInt(maxX - min + 1) + min) * UNIT_SIZE;
				apple2Y = (random.nextInt(maxY - min + 1) + min) * UNIT_SIZE;
			}
		} else if (whoAteApple == 1) {
			apple1X = (random.nextInt(maxX - min + 1) + min) * UNIT_SIZE;
			apple1Y = (random.nextInt(maxY - min + 1) + min) * UNIT_SIZE;
		} else if (whoAteApple == 2) {
			apple2X = (random.nextInt(maxX - min + 1) + min) * UNIT_SIZE;
			apple2Y = (random.nextInt(maxY - min + 1) + min) * UNIT_SIZE;
		}
	}


	private void gameOver(Graphics g)			// Oyun bitim ekranını çizer
	{
		Font titleFont = new Font("Ink Free", Font.BOLD, 75);
		Font scoreFont = new Font("Ink Free", Font.BOLD, 40);
		Font winnerFont = new Font("Ink Free", Font.BOLD, 50);

		// Ekrana ortalanmış "Oyun Bitti" yazısı
		g.setColor(Color.red);
		g.setFont(titleFont);
		String gameOverText = "Oyun Bitti!";
		FontMetrics metrics = getFontMetrics(titleFont);
		int x = (SCREEN_WIDTH - metrics.stringWidth(gameOverText)) / 2; // ekrana ortalama işlemi
		g.drawString(gameOverText, x, 200);

		// Oyuncu 1 uzunluğu çizer
		g.setFont(scoreFont);
		String p1Score = player1Username + " Uzunluk: " + (snake1.applesEaten + 4);
		metrics = getFontMetrics(scoreFont);
		x = (SCREEN_WIDTH - metrics.stringWidth(p1Score)) / 2;
		g.drawString(p1Score, x, 300);

		// Eğer varsa oyuncu 2 veya bilgisayarın skorunu çizer
		if (gameMode == 2 || gameMode == 3) {
			String p2Score;
			if (gameMode == 2)
				p2Score = player2Username + " Uzunluk: " + (snake2.applesEaten + 4);
			else
				p2Score = "Bilgisayar Uzunluk: " + (aiSnake.applesEaten + 4);

			x = (SCREEN_WIDTH - getFontMetrics(scoreFont).stringWidth(p2Score)) / 2;
			g.drawString(p2Score, x, 350);
		}

		// Kazanan mesajını çizer
		g.setFont(winnerFont);
		metrics = getFontMetrics(winnerFont);
		x = (SCREEN_WIDTH - metrics.stringWidth(winnerMsg)) / 2;
		g.drawString(winnerMsg, x, 450);

		// Yeni oyun butonu oluşturur
		if (newGameButton == null) {
			newGameButton = new JButton("Yeni Oyun");
			newGameButton.addActionListener(e -> restartGame());
			this.setLayout(null);
			this.add(newGameButton);
		}
		// butonu ortala
		newGameButton.setBounds((SCREEN_WIDTH - 200) / 2, 520, 200, 50);
	}

	private void restartGame() {
		// Mevcut oyunun penceresini alır ve kapatır
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this); // Mevcut pencereyi al
		if (topFrame != null) {
			topFrame.dispose(); // Şu anki oyunu kapat
		}

		// Yeni oyun menüsünü başlatır
		SwingUtilities.invokeLater(() -> {
			new GameMenu(); // Yeni GameMenu penceresini başlat
		});
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int snake1Collision, snake2Collision, aiSnakeCollision;
		boolean playerHeadCollisionWithAI = false;

		// Eğer oyun aktifse (gameState true ise), yılanların hareketini ve diğer işlemleri başlat
		if (gameState) {
			// Player 1 yılanının hareketi
			snake1.move();

			// Eğer oyun iki oyunculu modda ise, Player 2 yılanının hareketini sağla
			if (gameMode == 2) {
				snake2.move();
			}
			// Eğer oyun yapay zeka modunda ise, AI yılanının hareketini sağla
			else if (gameMode == 3)
			{
				aiSnake.moveAI(apple2X, apple2Y);
			}

			// Yılanların elmaya temas edip etmediğini kontrol et
			if (snake1.checkApple(apple1X, apple1Y)) {
				spawnApple(1); // Elma yendiyse, yeni elma spawn et
			}
			// İki oyunculu modda, Player 2'nin elmaya temas edip etmediğini kontrol et
			if (gameMode == 2 && snake2.checkApple(apple2X, apple2Y)) {
				spawnApple(2); // Player 2'nin elması yediği durum
			}
			// Yapay zeka modunda, AI yılanının elmaya temas edip etmediğini kontrol et
			if (gameMode == 3 && aiSnake.checkApple(apple2X, apple2Y)) {
				spawnApple(2); // Yapay zekanın elması yediği durum
			}

			// Çarpışmaları kontrol et
			snake1Collision = snake1.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake2); // Player 1'in çarpışmalarını kontrol et
			snake2Collision = gameMode == 2 ? snake2.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake1) : 0; // Player 2'nin çarpışmalarını kontrol et
			aiSnakeCollision = gameMode == 3 ? aiSnake.checkCollisions(SCREEN_WIDTH, SCREEN_HEIGHT, snake1) : 0; // Yapay zekanın çarpışmalarını kontrol et

			// Player 1'in kafasının yapay zekayla çarpışıp çarpmadığını kontrol et
			playerHeadCollisionWithAI = gameMode == 3 && checkPlayerHeadCollisionWithAISnake(snake1, aiSnake);
			if (playerHeadCollisionWithAI && snake1Collision == 0)
				snake1Collision = 2; // Eğer çarpışma varsa, Player 1'in kaybettiğini işaretle

			// Eğer herhangi bir yılan çarpışmışsa (kendisiyle, diğer oyuncu ile veya yapay zekayla), oyunu bitir
			if (snake1Collision == 1 || snake2Collision == 1 || aiSnakeCollision == 1)
			{
				gameState = false;  // Oyunu durdur
				gameTimer.stop();  // Zamanlayıcıyı durdur
				declareWinner(snake1Collision == 1, snake2Collision == 1, aiSnakeCollision == 1, false); // Kaybeden oyuncuyu belirt
			}
			// Eğer bir yılan diğerine çarpmışsa (kendisi, diğer oyuncu veya yapay zeka), oyunu bitir
			else if (snake1Collision == 2 || snake2Collision == 2 || aiSnakeCollision == 2)
			{
				gameState = false;  // Oyunu durdur
				gameTimer.stop();  // Zamanlayıcıyı durdur
				declareWinner(snake1Collision == 2, snake2Collision == 2, aiSnakeCollision == 2, false); // Kaybeden oyuncuyu belirt
			}
			else if (snake1Collision == 3 || snake2Collision == 3 || aiSnakeCollision == 3)
			{
				gameState = false;  // Oyunu durdur
				gameTimer.stop();  // Zamanlayıcıyı durdur
				declareWinner(snake1Collision == 3, snake2Collision == 3, aiSnakeCollision == 3, false); // Kaybeden oyuncuyu belirt
			}
			repaint(); // Ekranı yenileyerek durumu güncelle
		}
	}


	private void declareWinner(boolean snake1Lost, boolean snake2Lost, boolean aiSnakeLost, boolean isTimeUp)
	{
		// Eğer zaman dolmadıysa (isTimeUp false ise), kaybedenleri ve kazananı belirle
		if (!isTimeUp)
		{
			if (gameMode == 3)
			{
				// AI Modu: Player vs AI
				if (snake1Lost && aiSnakeLost)
				{
					winnerMsg = "Berabere!"; // Her ikisi de aynı anda kaybetti
				} else if (snake1Lost)
				{
					winnerMsg = "Bilgisayar Kazandı!"; // Player 1 kaybetti, bilgisayar kazandı
				} else
				{
					winnerMsg = player1Username + " Kazandı!"; // Player 1 kazandı
				}
			}
			else if (gameMode == 2)
			{
				// İki oyunculu mod
				if (snake1Lost && snake2Lost)
				{
					winnerMsg = "Berabere!"; // Her iki oyuncu da kaybetti
				}
				else if (snake1Lost)
				{
					winnerMsg = player2Username + " Kazandı!"; // Player 1 kaybetti, Player 2 kazandı
				}
				else if (snake2Lost)
				{
					winnerMsg = player1Username + " Kazandı!"; // Player 2 kaybetti, Player 1 kazandı
				}
				else if (snake1.x[0] == snake2.x[0] && snake1.y[0] == snake2.y[0])
				{
					// Baş-baş çarpışma durumu
					winnerMsg = "Berabere!"; // Yılanlar çarpıştı, berabere oldu
				}
			}
			else if (gameMode == 1) {  // Tek oyunculu modda oyun bittiğinde ekranın görünmesini sağla
				winnerMsg = " "; // Tek oyunculu modda bir kazanan yok
			}
		}
		else
		{
			// Zaman dolmuşsa, elmalar üzerinden kazananı belirle
			if (gameMode == 2)
			{
				// İki oyunculu modda, kim daha fazla elma yediyse o kazanır
				if (snake1.applesEaten > snake2.applesEaten)
				{
					winnerMsg = player1Username + " Kazandı"; // Player 1 daha fazla elma yedi
				}
				else if (snake2.applesEaten > snake1.applesEaten)
				{
					winnerMsg = player2Username + " Kazandı"; // Player 2 daha fazla elma yedi
				}
				else
				{
					winnerMsg = "Berabere"; // Her iki oyuncu da aynı sayıda elma yedi, berabere
				}
			}
			else if (gameMode == 3)
			{
				// AI modunda, kim daha fazla elma yediyse o kazanır
				if (snake1.applesEaten > aiSnake.applesEaten)
				{
					winnerMsg = player1Username + " Kazandı"; // Player 1 daha fazla elma yedi
				}
				else if (aiSnake.applesEaten > snake1.applesEaten)
				{
					winnerMsg = "Bilgisayar Kazandı"; // Yapay zeka daha fazla elma yedi
				}
				else
				{
					winnerMsg = "Berabere"; // Her ikisi de aynı sayıda elma yedi, berabere
				}
			}
		}

		repaint(); // Ekranı yeniden çizerek kazanan mesajını göster
	}



	class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_ESCAPE:
					pauseGame();
					break;
				// 1. oyuncu kontroller
				case KeyEvent.VK_LEFT:
					if (!snake1.direction.equals("RIGHT"))
						snake1.direction = "LEFT";
					break;
				case KeyEvent.VK_RIGHT:
					if (!snake1.direction.equals("LEFT"))
						snake1.direction = "RIGHT";
					break;
				case KeyEvent.VK_UP:
					if (!snake1.direction.equals("DOWN"))
						snake1.direction = "UP";
					break;
				case KeyEvent.VK_DOWN:
					if (!snake1.direction.equals("UP"))
						snake1.direction = "DOWN";
					break;

				// 2. oyuncu kontroller
				case KeyEvent.VK_A:
					if (gameMode == 2 && !snake2.direction.equals("RIGHT"))
						snake2.direction = "LEFT";
					break;
				case KeyEvent.VK_D:
					if (gameMode == 2 && !snake2.direction.equals("LEFT"))
						snake2.direction = "RIGHT";
					break;
				case KeyEvent.VK_W:
					if (gameMode == 2 && !snake2.direction.equals("DOWN"))
						snake2.direction = "UP";
					break;
				case KeyEvent.VK_S:
					if (gameMode == 2 && !snake2.direction.equals("UP"))
						snake2.direction = "DOWN";
					break;
			}
		}
	}

	public void pauseGame() {
		// Timer'ı durdur
		gameTimer.stop(); // Oyun zamanlayıcısını durdurur
		if (countdownTimer != null) {
			countdownTimer.stop(); // Eğer geri sayım zamanlayıcısı varsa, onu da durdurur
		}
		// Duraklatma penceresini göster
		Object[] options = {"Devam Et", "Yeni Oyun", "Oyunu Bitir"};
		int choice = JOptionPane.showOptionDialog(
				this,
				"Oyun duraklatıldı.", // Duraklatma mesajı
				"Duraklatıldı", // Pencere başlığı
				JOptionPane.DEFAULT_OPTION, // Varsayılan seçenek tipi
				JOptionPane.INFORMATION_MESSAGE, // Mesaj türü
				null,
				options, // Seçenekler
				options[0] // Varsayılan seçenek (Devam Et)
		);

		switch (choice) {
			case 0: // Devam Et
				// Oyunu devam ettir
				gameTimer.start(); // Oyun zamanlayıcısını tekrar başlat
				if (countdownTimer != null) {
					countdownTimer.start(); // Eğer geri sayım zamanlayıcısı varsa, onu da tekrar başlat
				}
				break;
			case 1: // Yeni Oyun
				restartGame(); // Yeni oyun başlat
				break;
			case 2: // Oyunu Bitir
				System.exit(0); // Uygulamayı kapat
				break;
			default:
				// Eğer pencereyi kapatırsa, oyunu devam ettir
				gameTimer.start(); // Oyun zamanlayıcısını tekrar başlat
				if (countdownTimer != null) {
					countdownTimer.start(); // Eğer geri sayım zamanlayıcısı varsa, onu da tekrar başlat
				}
				break;
		}
	}

	private boolean checkPlayerHeadCollisionWithAISnake(SnakeBase playerSnake, SnakeBase aiSnake) {
		// Oyuncunun başı ile yapay zekanın başının çarpışıp çarpmadığını kontrol et
		if (playerSnake.x[0] == aiSnake.x[0] && playerSnake.y[0] == aiSnake.y[0]) {
			return true; // Eğer çarpışma varsa, true döndür
		}

		// Oyuncunun başı ile yapay zekanın vücudundaki herhangi bir parçanın çarpışıp çarpmadığını kontrol et
		for (int i = 1; i < aiSnake.bodyParts; i++) { // 1'den başla çünkü baş zaten kontrol edildi
			if (playerSnake.x[0] == aiSnake.x[i] && playerSnake.y[0] == aiSnake.y[i]) {
				return true; // Eğer çarpışma varsa, true döndür
			}
		}
		return false; // Eğer çarpışma yoksa, false döndür
	}

}

