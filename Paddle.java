import java.awt.geom.Rectangle2D;

public class Paddle {
    private Rectangle2D.Double paddle;
    private int x;
    private int y;

    public Paddle(int x, int y, int width, int height) {
        paddle = new Rectangle2D.Double(x, y, width, height);
        this.x = x;
        this.y = y;
    }

    public Rectangle2D.Double getBounds() {
        return paddle;
    }

    public void move(int dx) {
        x += dx;
        paddle.x = x;
    }
}



