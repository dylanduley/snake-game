import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Game extends Canvas implements ActionListener {
    // Number of cells
    public static final int BOARD_LENGTH = 20;
    // Size of each cell
    public static final int SIZE = 30;
    // Width of the board
    public static final int WIDTH = BOARD_LENGTH * SIZE;
    // Height of the board plus space for the score
    public static final int HEIGHT = (BOARD_LENGTH * SIZE) + 50;
    public static final String NAME = "Snake Game";

    private boolean running = false;
    private final Snake snake;
    private final Apple apple;
    private int score;
    private Direction direction = Direction.UP;
    private boolean gameOver = false;
    private final JButton restartButton;

    public Game() {
        JFrame frame = new JFrame(NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the icon image
        try {
            Image icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/snake_icon.png")));
            frame.setIconImage(icon);
            Taskbar.getTaskbar().setIconImage(icon);
        } catch (IOException e) {
            System.err.println("Icon image not found.");
        }

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);

        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 18));
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> restartGame());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.add(restartButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.snake = new Snake(BOARD_LENGTH);
        this.apple = new Apple(BOARD_LENGTH, snake);
        this.score = 0;

        Timer timer = new Timer(120, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent pressedKey) {
                handleKeyPress(pressedKey);
            }
        });
        setFocusable(true);
        requestFocus();
    }

    public synchronized void start() {
        running = true;
    }

    public synchronized void stop() {
        running = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !gameOver) {
            // Update the game state
            if (!snake.move(direction)) {
                gameOver = true;
                restartButton.setVisible(true);
            } else {
                if (snake.isCollision(apple)) {
                    score++;
                    apple.newPosition(snake);
                    snake.extend();
                }
                snake.moveTail();
            }
            repaint();
        }
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.clearRect(0, 0, getWidth(), getHeight());
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, HEIGHT - 50, WIDTH, 50);

        if (!gameOver) {
            showScoreAndTime(graphics);
            apple.draw(graphics);
            snake.draw(graphics);
        }

        if (gameOver) {
            showGameOver(graphics);
        }
    }

    private void showScoreAndTime(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.PLAIN, 18));
        graphics.drawString("Score: " + score, 10, 20);
        String timeString = "Time: " + getCurrentTime();
        int timeStringWidth = graphics.getFontMetrics().stringWidth(timeString);
        graphics.drawString(timeString, WIDTH - timeStringWidth - 10, 20);
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date());
    }

    private void showGameOver(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 30));
        String gameOverMessage = "Game Over";
        int gameOverMessageWidth = graphics.getFontMetrics().stringWidth(gameOverMessage);
        graphics.drawString(gameOverMessage, (WIDTH - gameOverMessageWidth) / 2, HEIGHT / 2 - 10);

        String scoreMessage = "Score: " + score;
        graphics.setFont(new Font("Arial", Font.PLAIN, 18));
        int scoreMessageWidth = graphics.getFontMetrics().stringWidth(scoreMessage);
        graphics.drawString(scoreMessage, (WIDTH - scoreMessageWidth) / 2, HEIGHT / 2 + 20);

        String restartMessage = "Press 'R' to Restart";
        int restartMessageWidth = graphics.getFontMetrics().stringWidth(restartMessage);
        graphics.drawString(restartMessage, (WIDTH - restartMessageWidth) / 2, HEIGHT / 2 + 50);
        direction = Direction.UP;
    }

    private void restartGame() {
        snake.reset();
        apple.newPosition(snake);
        score = 0;
        gameOver = false;
        restartButton.setVisible(false);
        start();
    }

    private void handleKeyPress(KeyEvent pressedKey) {
        if (!gameOver) {
            Direction newDirection = direction;

            switch (pressedKey.getKeyCode()) {
                case KeyEvent.VK_W -> newDirection = Direction.UP;
                case KeyEvent.VK_S -> newDirection = Direction.DOWN;
                case KeyEvent.VK_A -> newDirection = Direction.LEFT;
                case KeyEvent.VK_D -> newDirection = Direction.RIGHT;
                case KeyEvent.VK_P -> {
                    // Pause or unpause game
                    if (running) {
                        stop();
                    } else {
                        start();
                    }
                }
                default -> {}
            }

            // Update direction if it's not the opposite direction
            if (!isOppositeDirection(direction, newDirection)) {
                direction = newDirection;
            }
        } else if (pressedKey.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
    }

    private boolean isOppositeDirection(Direction current, Direction newDirection) {
        return (current == Direction.UP && newDirection == Direction.DOWN) ||
                (current == Direction.DOWN && newDirection == Direction.UP) ||
                (current == Direction.LEFT && newDirection == Direction.RIGHT) ||
                (current == Direction.RIGHT && newDirection == Direction.LEFT);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
