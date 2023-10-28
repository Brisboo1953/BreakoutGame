import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Board extends JComponent implements Runnable, KeyListener {
    private Dimension preferredSize = new Dimension(800, 600);
    private boolean isPaused;
    private int score;
    private int dx;
    private Ellipse2D.Double ball;
    private Rectangle2D.Double paddle;
    private List<Rectangle2D.Double> bricks;
    private Thread ballAnimator;
    private Thread refresher;
    private long startTime;
    private boolean gameRunning;
    private JFrame scoreFrame;
    private JLabel timeLabel;
    private JLabel scoreLabel;

    public Board() {
        setOpaque(true);
        setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.BLACK));
        setPreferredSize(preferredSize);
        ball = new Ellipse2D.Double(20, 320, 20, 20);
        paddle = new Rectangle2D.Double(300, 450, 100, 10);
        bricks = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 6; j++) {
                bricks.add(new Rectangle2D.Double(i * 60, j * 20 + 50, 50, 15));
            }
        }
        BallRunner ballRunner = new BallRunner(ball, paddle, this, bricks);
        ballAnimator = new Thread(ballRunner, "BounceThread");
        ballAnimator.start();
        refresher = new Thread(this, "RefreshThread");
        refresher.start();
        addKeyListener(this);
        setFocusable(true);

        startTime = System.currentTimeMillis();
        isPaused = false;
        gameRunning = true;

        scoreFrame = new JFrame("Score");
        scoreFrame.setPreferredSize(new Dimension(200, 100));
        timeLabel = new JLabel();
        scoreLabel = new JLabel();
        scoreFrame.getContentPane().setLayout(new FlowLayout());
        scoreFrame.getContentPane().add(timeLabel);
        scoreFrame.getContentPane().add(scoreLabel);
        scoreFrame.pack();
        scoreFrame.setVisible(true);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(getForeground());
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5.0f));
        g2.setColor(Color.RED);
        g2.fill(ball);

        g2.setColor(Color.GREEN);
        g2.fill(paddle);

        for (int i = 0; i < bricks.size(); i++) {
            // Cambia el color de los bloques
            if (i % 2 == 0) {
                g2.setColor(Color.BLUE);
            } else {
                g2.setColor(Color.MAGENTA);
            }
            Rectangle2D.Double brick = bricks.get(i);
            g2.fill(brick);
        }

        g2.setColor(Color.BLACK);

        // Dibuja el cronómetro y la puntuación
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        timeLabel.setText("Tiempo: " + (elapsedTime / 1000) + " segundos");
        scoreLabel.setText("Puntuación: " + score);

        // Si el juego ha terminado, muestra el mensaje de "Game Over"
        if (!gameRunning) {
            g.drawString("Game Over", getWidth() / 2 - 30, getHeight() / 2);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    @Override
    public void run() {
        while (gameRunning) {
            if (!isPaused) {
                repaint();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void gameOver() {
        gameRunning = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println(e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            if (paddle.x > 0) {
                paddle.x -= 10;
            }
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            if (paddle.x + paddle.width < getWidth()) {
                paddle.x += 10;
            }
        } else if (keyCode == KeyEvent.VK_ADD) {
            // Incrementa la velocidad de la pelota
            BallRunner.increaseSpeed();
        } else if (keyCode == KeyEvent.VK_SUBTRACT) {
            // Disminuye la velocidad de la pelota
            BallRunner.decreaseSpeed();
        } else if (keyCode == KeyEvent.VK_P) {
            // Pausa o reanuda el juego
            isPaused = !isPaused;
        } else if (keyCode == KeyEvent.VK_SPACE) {
            // Reinicia el juego
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void restartGame() {
        // Reinicializa la posición de la pelota y la raqueta
        ball.x = 20;
        ball.y = 320;
        paddle.x = 300;

        // Reinicializa los ladrillos
        bricks.clear();
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 5; j++) {
                bricks.add(new Rectangle2D.Double(i * 70, j * 30 + 50, 60, 20));
            }
        }

        // Reinicializa el tiempo
        startTime = System.currentTimeMillis();

        // Reinicializa la puntuación
        score = 0;

        // Vuelve a activar el juego
        gameRunning = true;
    }
}


