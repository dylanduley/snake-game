import java.awt.*;
import java.util.Random;

public class Apple {
    private final int boardLength;
    private Point position;

    public Apple(int boardLength, Snake snake) {
        this.boardLength = boardLength;
        newPosition(snake);
    }

    public void newPosition(Snake snake) {
        Random random = new Random();
        do {
            position = new Point(random.nextInt(boardLength), random.nextInt(boardLength));
        } while (snake.getBody().contains(position));
    }

    public Point getPosition() {
        return position;
    }

    public void draw(Graphics graphics) {
        graphics.setColor(Color.RED);
        graphics.fillRect(position.x * Game.SIZE, position.y * Game.SIZE, Game.SIZE, Game.SIZE);
    }
}
