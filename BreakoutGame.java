import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class BreakoutGame extends JFrame implements KeyListener, ActionListener {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int PADDLE_WIDTH = 100;
    private final int PADDLE_HEIGHT = 10;
    private final int BALL_DIAMETER = 20;
    private final int BRICK_WIDTH = 50;
    private final int BRICK_HEIGHT = 15;
    private final int BRICK_ROWS = 6;
    private final int BRICK_COLS = 10;
    private final int BALL_SPEED = 5;
    private final int PADDLE_SPEED = 10;

    private Ellipse2D.Double ball;
    private Rectangle2D.Double paddle;
    private List<Rectangle2D.Double> bricks;
    private int paddleX;
    private int ballX;
    private int ballY;
    private int ballSpeedX;
    private int ballSpeedY;
    private int score;
    private int timeSeconds;
    private int timeMinutes;
    private boolean isPaused;
    private Timer timer;

    public BreakoutGame() {
        setTitle("Breakout Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        initGame();
        timer = new Timer(1000, this);
        timer.start();
    }

    public void initGame() {
        ball = new Ellipse2D.Double(WIDTH / 2 - BALL_DIAMETER / 2, HEIGHT / 2 - BALL_DIAMETER / 2, BALL_DIAMETER, BALL_DIAMETER);
        paddle = new Rectangle2D.Double(WIDTH / 2 - PADDLE_WIDTH / 2, HEIGHT - PADDLE_HEIGHT - 10, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddleX = (int) paddle.getX();
        createBricks();

        score = 0;
        timeSeconds = 0;
        timeMinutes = 0;
        isPaused = false;
        ballSpeedX = BALL_SPEED;
        ballSpeedY = BALL_SPEED;

        ActionListener ballMovement = e -> moveBall();
        Timer ballTimer = new Timer(20, ballMovement);
        ballTimer.start();
    }

    public void createBricks() {
        bricks = new ArrayList<>();
        for (int row = 0; row < BRICK_ROWS; row++) {
            for (int col = 0; col < BRICK_COLS; col++) {
                double x = col * (BRICK_WIDTH + 2) + 30;
                double y = row * (BRICK_HEIGHT + 2) + 50;
                Rectangle2D.Double brick = new Rectangle2D.Double(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                bricks.add(brick);
            }
        }
    }

    public void moveBall() {
        if (!isPaused) {
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            if (ballX <= 0 || ballX >= WIDTH - BALL_DIAMETER) {
                ballSpeedX = -ballSpeedX;
            }

            if (ballY <= 0) {
                ballSpeedY = -ballSpeedY;
            }

            if (ball.intersects(paddle)) {
                ballSpeedY = -Math.abs(ballSpeedY);
                incrementScore(); // Incrementa la puntuación al tocar la pelota
            }

            for (int i = 0; i < bricks.size(); i++) {
                if (bricks.get(i) != null && ball.intersects(bricks.get(i))) {
                    ballSpeedY = Math.abs(ballSpeedY);
                    bricks.set(i, null);
                    incrementScore(); // Incrementa la puntuación por cada ladrillo roto
                }
            }

            if (ballY >= HEIGHT - BALL_DIAMETER) {
                gameOver();
            }

            if (bricks.stream().allMatch(brick -> brick == null)) {
                gameWon();
            }

            ball.x = ballX;
            ball.y = ballY;
            repaint();
        }
    }

    public void gameOver() {
        JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        initGame();
    }

    public void gameWon() {
        JOptionPane.showMessageDialog(this, "Congratulations! You won! Your Score: " + score, "You Won", JOptionPane.INFORMATION_MESSAGE);
        initGame();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(Color.RED);
        g2d.fill(ball);

        g2d.setPaint(Color.GREEN);
        g2d.fill(paddle);

        g2d.setPaint(Color.BLACK);
        bricks.forEach(brick -> {
            if (brick != null) {
                g2d.fill(brick);
            }
        });

        g2d.setPaint(Color.BLACK);
        g2d.drawString("Score: " + score, 10, 20);
        g2d.drawString("Time: " + timeMinutes + "m " + timeSeconds + "s", WIDTH - 100, 20);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BreakoutGame game = new BreakoutGame();
            game.setVisible(true);
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT && paddleX > 0) {
            paddleX -= PADDLE_SPEED;
        }

        if (keyCode == KeyEvent.VK_RIGHT && paddleX < WIDTH - PADDLE_WIDTH) {
            paddleX += PADDLE_SPEED;
        }

        if (keyCode == KeyEvent.VK_P) {
            isPaused = !isPaused;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused) {
            timeSeconds++;
            if (timeSeconds >= 60) {
                timeSeconds = 0;
                timeMinutes++;
            }
        }
    }

    public void incrementScore() {
        score++;
    }
}


