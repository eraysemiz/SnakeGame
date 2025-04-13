public abstract class SnakeBase {
	// Oyun birimleri, her bir yılan parçası ve yön gibi temel özellikleri saklayan özel alanlar
	protected int bodyParts = 4; // Yılanın başlangıç uzunluğu
	protected int applesEaten = 0; // Yılanın yediği elma sayısı
	protected String direction; // Yılanın yönü (UP, DOWN, LEFT, RIGHT)
	protected final int[] x; // Yılanın x koordinatları (başlangıç noktası ve diğer parçalar)
	protected final int[] y; // Yılanın y koordinatları (başlangıç noktası ve diğer parçalar)

	// Constructor: Yılanın temel özelliklerini başlatan yapıcı metod
	public SnakeBase(String direction) {
		this.direction = direction;
		x = new int[GamePanel.GAME_UNITS]; // Yılanın x koordinatlarını saklayan dizi
		y = new int[GamePanel.GAME_UNITS]; // Yılanın y koordinatlarını saklayan dizi
	}

	// Alt sınıflar tarafından implemente edilecek soyut metod
	public abstract void move();

	// Body parts (yılanın uzunluğu) için getter ve setter metodları
	public int getBodyParts() {
		return bodyParts;
	}

	// Yılanın yediği elma sayısı için getter ve setter metodları
	public int getApplesEaten() {
		return applesEaten;
	}

	public void setDirection(String direction) {
		if (direction != null && (direction.equals("UP") || direction.equals("DOWN") || direction.equals("LEFT") || direction.equals("RIGHT"))) {
			this.direction = direction.toUpperCase(); // Yönü geçerli bir değerle ayarla
		}
	}

	// x koordinatını almak için getter metod
	public int getX(int index) {
		if (index >= 0 && index < GamePanel.GAME_UNITS) {
			return x[index]; // Geçerli bir dizinle x koordinatını döndür
		}
		return -1;  // Geçersiz indeks
	}


	// y koordinatını almak için getter metod
	public int getY(int index) {
		if (index >= 0 && index < GamePanel.GAME_UNITS) {
			return y[index]; // Geçerli bir dizinle y koordinatını döndür
		}
		return -1;  // Geçersiz indeks
	}

	// Elma kontrolü ve yılanın büyümesi
	public boolean checkApple(int appleX, int appleY) {
		if (x[0] == appleX && y[0] == appleY) {  // Eğer baş elmayla çarpıştıysa
			bodyParts++; // Yılanın uzunluğunu bir artır
			applesEaten++; // Yılanın yediği elma sayısını artır
			return true;  // Elma yendi
		}
		return false;  // Elma yenmedi
	}

	// Çarpışma kontrolü (yılan kendine mi çarpıyor, diğer yılanla mı çarpışıyor veya sınırda mı)
	public int checkCollisions(int screenWidth, int screenHeight, SnakeBase otherSnake) {
		// Yılan kendine çarpıyor mu?
		for (int i = 1; i < bodyParts; i++) {
			if (x[0] == x[i] && y[0] == y[i]) {
				System.out.println("Self-collision detected at: (" + x[0] + "," + y[0] + ")");
				return 1; // Kendine çarptı
			}
		}

		// Yılan diğer yılana çarpıyor mu?
		if (otherSnake != null) {
			for (int i = 0; i < otherSnake.bodyParts; i++) {
				if (x[0] == otherSnake.x[i] && y[0] == otherSnake.y[i]) {
					System.out.println("Collision detected between snakes at: " + x[0] + "," + y[0]);
					return 2; // Diğer yılanla çarpıştı
				}
			}
		}

		// Sınırlara çarpma kontrolü
		if (x[0] < 25 || x[0] >= screenWidth - 25 || y[0] < 25 || y[0] >= screenHeight - 25) {
			System.out.println("Wall collision detected at: (" + x[0] + "," + y[0] + ")");
			return 3; // Sınıra çarptı
		}

		return 0; // Çarpışma yok
	}

}
