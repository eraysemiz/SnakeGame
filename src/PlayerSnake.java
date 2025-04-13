public class PlayerSnake extends SnakeBase
{
	public PlayerSnake(String direction) {
		super(direction);
	}

	@Override
	public void move() {
		for (int i = bodyParts; i > 0; i--)
		{
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		switch (direction)
		{
			case "UP":
				y[0] = y[0] - GamePanel.UNIT_SIZE;
				break;
			case "DOWN":
				y[0] = y[0] + GamePanel.UNIT_SIZE;
				break;
			case "LEFT":
				x[0] = x[0] - GamePanel.UNIT_SIZE;
				break;
			case "RIGHT":
				x[0] = x[0] + GamePanel.UNIT_SIZE;
				break;
		}
	}
}