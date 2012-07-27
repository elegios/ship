package ship.netcode.movement;

import elegios.netcode.Package;

public class VehiclePositionPackage extends PlayerPositionPackage {

    public VehiclePositionPackage(int id, int time, float x, float y, float xSpeed, float ySpeed) { super(id, time, x, y, xSpeed, ySpeed); }
    public VehiclePositionPackage(String message) { super(message); }
    public VehiclePositionPackage() { super(); }

    public Package receivePackage(String message) { return new VehiclePositionPackage(message); }

}
