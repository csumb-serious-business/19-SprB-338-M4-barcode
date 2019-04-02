package common;

/**
 * todo: add desc
 *
 * @author todo
 */
public class Coordinate {
    int x;
    int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return x of the Coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @return y of the Coordinate
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "{ x: " + x + ", y: " + y + "}";
    }
}
