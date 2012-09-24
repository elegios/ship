package ship.world;

import ship.world.vehicle.VehicleHolder;

public class PositionMemory {

    int time;

    VehicleHolder veh;

    float x;
    float y;
    float xSpeed;
    float ySpeed;

    PositionMemory(int time, float x, float y, float xSpeed, float ySpeed, VehicleHolder veh) {
        this.time = time;

        this.veh = veh;

        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public VehicleHolder getVehicle() { return veh; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getXSpeed() { return xSpeed; }
    public float getYSpeed() { return ySpeed; }

}