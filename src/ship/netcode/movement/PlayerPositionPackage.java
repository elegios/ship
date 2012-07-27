package ship.netcode.movement;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * id | time | x | y | xSpeed | ySpeed
 */

public class PlayerPositionPackage extends BasicPackage {

    private int id;

    private int time;

    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;

    private boolean xChecked;

    public PlayerPositionPackage(int id, int time, float x, float y, float xSpeed, float ySpeed) {
        append(id +"");

        append(time +"");

        appendFloat(x);
        appendFloat(y);

        appendFloat(xSpeed);
        appendFloat(ySpeed);

        this.id = id;

        this.time = time;

        this.x = x;
        this.y = y;

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public PlayerPositionPackage(String message) {
        super(message);

        id = getNextInt();

        time = getNextInt();

        x = getNextFloat();
        y = getNextFloat();

        xSpeed = getNextFloat();
        ySpeed = getNextFloat();

        xChecked = false;
    }

    public PlayerPositionPackage() { super(); }

    public Package receivePackage(String message) { return new PlayerPositionPackage(message); }

    public int   getId()   { return id; }
    public int   getTime() { return time; }
    public float getX()    { return  x; }
    public float getY()    { return  y; }
    public float getXSpeed() { return xSpeed; }
    public float getYSpeed() { return ySpeed; }

    public void xChecked(boolean checked) { xChecked = checked; }

    public boolean xChecked() { return xChecked; }

}
