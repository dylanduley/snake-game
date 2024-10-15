import java.awt.*;
import java.util.ArrayList;

public class Snake {
    private final ArrayList<Point> body;
    private final int boardLength;

    public Snake(int boardLength) {
        this.boardLength = boardLength;
        body = new ArrayList<>();
        reset();
    }

    public void reset() {
        body.clear();
        body.add(new Point(boardLength / 2, boardLength / 2));
        body.add(new Point(boardLength / 2, boardLength / 2 + 1));
        body.add(new Point(boardLength / 2, boardLength / 2 + 2));
    }

    public boolean move(Direction direction) {
        Point head = body.get(0);
        Point newHead = new Point(head);
        switch (direction) {
            case UP -> newHead.translate(0, -1);
            case DOWN -> newHead.translate(0, 1);
            case LEFT -> newHead.translate(-1, 0);
            case RIGHT -> newHead.translate(1, 0);
        }
        if (newHead.x < 0 || newHead.x >= boardLength || newHead.y < 0 || newHead.y >= boardLength || body.contains(newHead)) {
            return false;
        }
        body.add(0, newHead);
        return true;
    }

    public void extend() {
        body.add(null);
    }

    public void moveTail() {
        body.remove(body.size() - 1);
    }

    public boolean isCollision(Apple apple) {
        return body.get(0).equals(apple.getPosition());
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        for (Point segment : body) {
            g.fillRect(segment.x * Game.SIZE, segment.y * Game.SIZE, Game.SIZE, Game.SIZE);
        }
    }

    public ArrayList<Point> getBody() {
        return body;
    }
}
