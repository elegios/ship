package ship.netcode.movement;

public class VehiclePositionPackage extends PlayerPositionPackage {

    public VehiclePositionPackage(int id, float x, float y, float xSpeed, float ySpeed) { super(id, x, y, xSpeed, ySpeed); }
    public VehiclePositionPackage(String message) { super(message); }
    public VehiclePositionPackage() { super(); }

}
