public abstract class SnakeBase {
	// Private fields to encapsulate the internal state
	private int gameUnits;
	protected int unitSize;
	protected int bodyParts = 4;
	protected int applesEaten = 0;
	protected String direction;
	protected final int[] x;
	protected final int[] y;

	// Constructor to initialize the snake
	public SnakeBase(int gameUnits, int unitSize, String direction) {
		this.gameUnits = gameUnits;
		this.unitSize = unitSize;
		this.direction = direction;
		x = new int[gameUnits];
		y = new int[gameUnits];
	}

	// Abstract method to be implemented by subclasses
	public abstract void move();

	public void moveAI(int appleX, int appleY)
	{

	}

	// Getter and setter methods for bodyParts
	public int getBodyParts() {
		return bodyParts;
	}

	public void setBodyParts(int bodyParts) {
		if (bodyParts >= 0) {
			this.bodyParts = bodyParts;
		}
	}

	// Getter and setter methods for applesEaten
	public int getApplesEaten() {
		return applesEaten;
	}

	public void setApplesEaten(int applesEaten) {
		if (applesEaten >= 0) {
			this.applesEaten = applesEaten;
		}
	}

	// Getter for direction (assuming only reading direction is needed)
	public String getDirection() {
		return direction;
	}

	// Setter for direction
	public void setDirection(String direction) {
		if (direction != null && (direction.equals("UP") || direction.equals("DOWN") || direction.equals("LEFT") || direction.equals("RIGHT"))) {
			this.direction = direction;
		}
	}

	// Getter for x-coordinate
	public int getX(int index) {
		if (index >= 0 && index < gameUnits) {
			return x[index];
		}
		return -1;  // Invalid index
	}

	// Setter for x-coordinate
	public void setX(int index, int value) {
		if (index >= 0 && index < gameUnits) {
			x[index] = value;
		}
	}

	// Getter for y-coordinate
	public int getY(int index) {
		if (index >= 0 && index < gameUnits) {
			return y[index];
		}
		return -1;  // Invalid index
	}

	// Setter for y-coordinate
	public void setY(int index, int value) {
		if (index >= 0 && index < gameUnits) {
			y[index] = value;
		}
	}

	// Methods for checking apple and collision logic
	public boolean checkApple(int appleX, int appleY) {
		if (x[0] == appleX && y[0] == appleY) {
			bodyParts++;
			applesEaten++;
			return true;
		}
		return false;
	}

	public int checkCollisions(int screenWidth, int screenHeight, SnakeBase otherSnake) {
		// Yılan kendine çarpıyor mu?
		for (int i = 1; i < bodyParts; i++)
		{
			if (x[0] == x[i] && y[0] == y[i])
			{
				System.out.println("Self-collision detected at: (" + x[0] + "," + y[0] + ")");
				return 1;
			}
		}

		// Yılan diğer yılana çarpıyor mu?
		if (otherSnake != null) {
			for (int i = 0; i < otherSnake.bodyParts; i++) {
				if (x[0] == otherSnake.x[i] && y[0] == otherSnake.y[i])
				{
					System.out.println("Collision detected between snakes at: " + x[0] + "," + y[0]);
					return 2;
				}
			}
		}

		// Yılan köşelere çarparsa öteki tarafa ışınla
		if (x[0] < 0) {
			x[0] = screenWidth - unitSize;
		} else if (x[0] >= screenWidth) {
			x[0] = 0;
		}

		if (y[0] < 0) {
			y[0] = screenHeight - unitSize;
		} else if (y[0] >= screenHeight) {
			y[0] = 0;
		}

		return 0;
	}
}
