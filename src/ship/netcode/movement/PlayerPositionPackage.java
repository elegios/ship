package ship.netcode.movement;

import elegios.netcode.BasicPackage;

/*
 * Specification:
 * id | x | y | xSpeed | ySpeed
 */

public class PlayerPositionPackage extends BasicPackage {

    private int id;
    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;

    private boolean xChecked;

    public PlayerPositionPackage(int id, float x, float y, float xSpeed, float ySpeed) {
        append(id +"");

        appendFloat(x);
        appendFloat(y);

        appendFloat(xSpeed);
        appendFloat(ySpeed);

        this.id = id;

        this.x = x;
        this.y = y;

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public PlayerPositionPackage(String message) {
        super(message);

        id = getNextInt();

        x = getNextFloat();
        y = getNextFloat();

        xSpeed = getNextFloat();
        ySpeed = getNextFloat();

        xChecked = false;
    }

    public PlayerPositionPackage() { super(); }

    public int   getId() { return id; }
    public float getX()  { return  x; }
    public float getY()  { return  y; }
    public float getXSpeed() { return xSpeed; }
    public float getYSpeed() { return ySpeed; }

    public void xChecked(boolean checked) { xChecked = checked; }

    public boolean xChecked() { return xChecked; }

}
