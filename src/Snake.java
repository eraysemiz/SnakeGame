public class Snake {
	int gameUnits;
	int unitSize;
	int bodyParts = 6;
	int applesEaten = 0;
	String direction;

	final int[] x;
	final int[] y;

	public Snake(int gameUnits, int unitSize, String direction)
	{
		this.gameUnits = gameUnits;
		this.direction = direction;
		this.unitSize = unitSize;
		x = new int[gameUnits];
		y = new int[gameUnits];
	}

	public void move()
	{
		for (int i = bodyParts; i > 0; i--)
		{
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		switch (direction)
		{
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

	public boolean checkApple(int appleX, int appleY)
	{
		if (x[0] == appleX && y[0] == appleY)
		{
			bodyParts++;
			applesEaten++;
			return true;
		}
		return false;
	}

	public boolean checkCollisions(int screenWidth, int screenHeight, Snake otherSnake)
	{
		// Yılan kendine çarpıyor mu?
		for (int i = bodyParts; i > 0; i--)
		{
			if (x[0] == x[i] && y[0] == y[i]) {
				return true;
			}
		}

		// Yılan diğer yılana çarpıyor mu?
		if (otherSnake != null)
		{
			for (int i = 0; i < otherSnake.bodyParts; i++)
			{
				if (x[0] == otherSnake.x[i] && y[0] == otherSnake.y[i])
				{
					return true;
				}
			}
		}

		// Yılan köşelere çarparsa öteki tarafa ışınla
		if (x[0] < 0)
		{
			x[0] = screenWidth - unitSize;
		}
		else if (x[0] >= screenWidth)
		{
			x[0] = 0;
		}

		if (y[0] < 0)
		{
			y[0] = screenHeight - unitSize;
		}
		else if (y[0] >= screenHeight)
		{
			y[0] = 0;
		}

		return false;
	}

}
