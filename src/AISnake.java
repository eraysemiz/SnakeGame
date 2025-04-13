public class AISnake extends SnakeBase {
	private final PlayerSnake playerSnake;
	private int appleX, appleY;

	public AISnake(String direction, PlayerSnake playerSnake) {
		super(direction);
		this.playerSnake = playerSnake;
	}

	public void moveAI(int appleX, int appleY) {
		this.appleX = appleX;
		this.appleY = appleY;
		move();	// hareket et
	}

	@Override
	public void move() {
		int dx = appleX - getX(0); // Elma ile yılanın başı arasındaki X eksenindeki mesafe
		int dy = appleY - getY(0); // Elma ile yılanın başı arasındaki Y eksenindeki mesafe

		String newDirection = direction;

		/*
			elma ile yılanın başı arasındaki mesafenin mutlak değerini alıp, elmanın dikeyde mi
			yoksa yatayda mı daha uzakta olduğunu bul ve uzakta olan yöne hareket et
		 */
		if (Math.abs(dx) > Math.abs(dy)) // Eğer yatay mesafe dikeyden fazlaysa, yatay hareketi tercih et
		{
			if (dx > 0 && !direction.equals("LEFT")) // Elma sağda ise sağa dön
				newDirection = "RIGHT";
			else if (dx < 0 && !direction.equals("RIGHT"))	// Elma solda ise sola dön
				newDirection = "LEFT";
		}
		else	// Eğer dikey mesafe yataydan fazlaysa, dikey hareketi tercih et
		{
			if (dy > 0 && !direction.equals("UP"))	// Elma aşağıda ise aşağı dön
				newDirection = "DOWN";
			else if (dy < 0 && !direction.equals("DOWN"))	// Elma yukarıda ise yukarı dön
				newDirection = "UP";
		}

		// Eğer belirlenen yeni yön çarpışmaya neden olmayacaksa, yönü değiştir
		if (!willCollideWith(newDirection, playerSnake)) {
			setDirection(newDirection);
		} else {
			avoidCollision();	// Aksi halde çarpışmadan kaçmak için alternatif yön belirle
		}

		// Gövde parçalarını bir öncekinin yerine kaydır
		for (int i = bodyParts - 1; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		// Kafayı mevcut yöne göre hareket ettir
		switch (direction) {
			case "UP":
				y[0] -= GamePanel.UNIT_SIZE;
				break;
			case "DOWN":
				y[0] += GamePanel.UNIT_SIZE;
				break;
			case "LEFT":
				x[0] -= GamePanel.UNIT_SIZE;
				break;
			case "RIGHT":
				x[0] += GamePanel.UNIT_SIZE;
				break;
		}
	}

	private boolean willCollideWith(String nextDirection, PlayerSnake other) {
		// Yılanın başının mevcut konumunu al
		int nextX = x[0];
		int nextY = y[0];

		// Belirtilen yön doğrultusunda bir sonraki konumu hesapla
		switch (nextDirection) {
			case "UP":    nextY -= GamePanel.UNIT_SIZE; break;   // Yukarı hareket -> Y azalır
			case "DOWN":  nextY += GamePanel.UNIT_SIZE; break;   // Aşağı hareket -> Y artar
			case "LEFT":  nextX -= GamePanel.UNIT_SIZE; break;   // Sola hareket -> X azalır
			case "RIGHT": nextX += GamePanel.UNIT_SIZE; break;   // Sağa hareket -> X artar
		}

		// Ekran sınırlarına çarpma kontrolü (25 birim kenar boşluğu varsayılmış)
		if (nextX < 25 || nextX >= GamePanel.SCREEN_WIDTH - 25 ||
				nextY < 25 || nextY >= GamePanel.SCREEN_HEIGHT - 25) {
			return true; // Sınırın dışına çıkılıyorsa çarpışma olur
		}

		// Kendi gövdesiyle çarpışma kontrolü (baş kısmı hariç, i = 1'den başlar)
		for (int i = 1; i < bodyParts; i++) {
			if (x[i] == nextX && y[i] == nextY)
				return true; // Kendi gövdesiyle çarpışma varsa true döndür
		}

		// Diğer yılanla çarpışma kontrolü (diğer yılanın tüm parçaları kontrol edilir)
		for (int i = 0; i < other.getBodyParts(); i++) {
			if (other.getX(i) == nextX && other.getY(i) == nextY)
				return true; // Diğer yılanla çarpışma varsa true döndür
		}

		return false;			// Hiçbir çarpışma yoksa false döndür
	}

	private void avoidCollision() {
		String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
		for (String dir : directions) {
			if (!willCollideWith(dir, playerSnake)) {
				setDirection(dir);
				return;
			}
		}
	}
}
