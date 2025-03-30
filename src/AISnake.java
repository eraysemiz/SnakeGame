public class AISnake extends SnakeBase {

	private int moveCounter = 0; // Used to limit the frequency of direction changes
	private static final int MOVE_FREQUENCY = 5; // Number of moves before the AI makes a decision
	private static final double MISTAKE_CHANCE = 0.2; // 20% chance to make a random mistake in direction

	public AISnake(int gameUnits, int unitSize, String direction) {
		super(gameUnits, unitSize, direction);
	}

	@Override
	public void move() {
		// Move the body as usual
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		// Move the snake head in the chosen direction
		switch (direction) {
			case "UP":
				y[0] = y[0] - unitSize;
				break;
			case "DOWN":
				y[0] = y[0] + unitSize;
				break;
			case "LEFT":
				x[0] = x[0] - unitSize;
				break;
			case "RIGHT":
				x[0] = x[0] + unitSize;
				break;
		}
	}

	@Override
	public void moveAI(int appleX, int appleY) {
		// Shift body forward
		for (int i = bodyParts - 1; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		// Increase move counter
		moveCounter++;

		// Change direction every MOVE_FREQUENCY steps, and sometimes make mistakes
		if (moveCounter >= MOVE_FREQUENCY) {
			moveCounter = 0; // Reset counter

			// Random chance for the AI to make a mistake or not follow the apple directly
			if (Math.random() < MISTAKE_CHANCE) {
				randomizeDirection(); // Make a random move instead of going towards the apple
			} else {
				moveTowardsApple(appleX, appleY); // Normal behavior
			}
		}

		// Move the head in the chosen direction
		switch (direction) {
			case "UP":
				y[0] -= unitSize;
				break;
			case "DOWN":
				y[0] += unitSize;
				break;
			case "LEFT":
				x[0] -= unitSize;
				break;
			case "RIGHT":
				x[0] += unitSize;
				break;
		}
	}

	// Method to make the AI choose a random direction (even if it might not be the most optimal)
	private void randomizeDirection() {
		String[] possibleDirections = {"UP", "DOWN", "LEFT", "RIGHT"};
		String randomDirection = possibleDirections[(int) (Math.random() * possibleDirections.length)];

		// Ensure it doesn't reverse the current direction
		if (direction.equals("UP") && !randomDirection.equals("DOWN")) {
			setDirection(randomDirection);
		} else if (direction.equals("DOWN") && !randomDirection.equals("UP")) {
			setDirection(randomDirection);
		} else if (direction.equals("LEFT") && !randomDirection.equals("RIGHT")) {
			setDirection(randomDirection);
		} else if (direction.equals("RIGHT") && !randomDirection.equals("LEFT")) {
			setDirection(randomDirection);
		}
	}

	public void moveTowardsApple(int appleX, int appleY) {
		int dx = appleX - getX(0);
		int dy = appleY - getY(0);

		// Keep the current direction as default
		String newDirection = direction;

		// Prioritize horizontal movement if it's the greater distance
		if (Math.abs(dx) > Math.abs(dy)) {
			if (dx > 0 && !direction.equals("LEFT")) {
				newDirection = "RIGHT";
			} else if (dx < 0 && !direction.equals("RIGHT")) {
				newDirection = "LEFT";
			}
		} else {
			if (dy > 0 && !direction.equals("UP")) {
				newDirection = "DOWN";
			} else if (dy < 0 && !direction.equals("DOWN")) {
				newDirection = "UP";
			}
		}

		// Apply the new safe direction
		if (!willCollideWithSelf(newDirection)) {
			setDirection(newDirection);
		}
	}

	private boolean willCollideWithSelf(String nextDirection) {
		int nextX = x[0];
		int nextY = y[0];

		switch (nextDirection) {
			case "UP": nextY -= unitSize; break;
			case "DOWN": nextY += unitSize; break;
			case "LEFT": nextX -= unitSize; break;
			case "RIGHT": nextX += unitSize; break;
		}

		// Check if new head position overlaps with body
		for (int i = 1; i < bodyParts; i++) {
			if (x[i] == nextX && y[i] == nextY) {
				return true; // Collision detected
			}
		}
		return false; // Safe move
	}
}
