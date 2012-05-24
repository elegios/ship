package ship.netcode.movement;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * playerId | vehicleId | x | y | xSpeed | ySpeed
 */

public class RelativePlayerPositionPackage extends BasicPackage {

    private int playerId;
    private int vehicleId;
    private float x;
    private float y;
    private float xSpeed;
    private float ySpeed;

    private boolean xChecked;

    public RelativePlayerPositionPackage(int playerId, int vehicleId, float x, float y, float xSpeed, float ySpeed) {
        append(playerId +"");
        append(vehicleId +"");

        appendFloat(x);
        appendFloat(y);

        appendFloat(xSpeed);
        appendFloat(ySpeed);

        this.playerId  = playerId;
        this.vehicleId = vehicleId;

        this.x = x;
        this.y = y;

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public RelativePlayerPositionPackage(String message) {
        super(message);

        playerId  = getNextInt();
        vehicleId = getNextInt();

        x = getNextFloat();
        y = getNextFloat();

        xSpeed = getNextFloat();
        ySpeed = getNextFloat();

        xChecked = false;
    }

    public RelativePlayerPositionPackage() { super(); }

    public Package receivePackage(String message) { return new RelativePlayerPositionPackage(message); }

    public int   getPlayerId () { return  playerId; }
    public int   getVehicleId() { return vehicleId; }
    public float getX()  { return  x; }
    public float getY()  { return  y; }
    public float getXSpeed() { return xSpeed; }
    public float getYSpeed() { return ySpeed; }

    public void xChecked(boolean checked) { xChecked = checked; }

    public boolean xChecked() { return xChecked; }

}
