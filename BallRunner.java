import java.util.List;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class BallRunner implements Runnable {
    private static final int MAX_X = 640;
    private static final int MAX_Y = 480;
    private static final int SIGN = -1;
    private static int DX = 10;
    private static int DY = 10;
    private Ellipse2D.Double ball;
    private int ballX;
    private int ballY;
    private Rectangle2D.Double paddle;
    private Board board;
    private List<Rectangle2D.Double> bricks;

    public BallRunner(Ellipse2D.Double ball, Rectangle2D.Double paddle, Board board, List<Rectangle2D.Double> bricks) {
        this.ball = ball;
        ballX = 320;
        ballY = 240;
        ball.x = ballX;
        ball.y = ballY;

        this.paddle = paddle;
        this.board = board;
        this.bricks = bricks;
    }

    @Override
    public void run() {
        int sY = 1;
        int sX = 1;

        while (true) {
            if (!board.isPaused()) {
                if (ballY < 0) {
                    sY = -sY;
                }
                if (ballX < 0 || ballX > MAX_X - 20) {
                    sX = -sX;
                }

                if (ball.getBounds2D().intersects(paddle.getBounds2D())) {
                    sY = -sY;
                }

                ballX = ballX + (DX * sX);
                ballY = ballY + (DY * sY);
                ball.x = ballX;
                ball.y = ballY;

                for (int i = 0; i < bricks.size(); i++) {
                    Rectangle2D.Double brick = bricks.get(i);
                    if (ball.getBounds2D().intersects(brick)) {
                        bricks.remove(i);
                        board.incrementScore();
                        sY = -sY;
                        break;
                    }
                }

                try {
                    Thread.sleep(150L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void increaseSpeed() {
        DX += 2;
        DY += 2;
    }

    public static void decreaseSpeed() {
        DX -= 2;
        DY -= 2;
    }
}
