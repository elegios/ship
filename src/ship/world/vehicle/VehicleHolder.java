package ship.world.vehicle;

import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import ship.Updatable;
import ship.world.Rectangle;

public class VehicleHolder implements Updatable, Renderable {

    private Vehicle vehicle;

    private WholeVehiclePiece wholeVehicle;

    public VehicleHolder(Vehicle vehicle) {
        this.vehicle = vehicle;

        wholeVehicle = new WholeVehiclePiece(vehicle);
      //TODO: vertical and horizontal pieces
    }

    public boolean collideWithVehicleHolderX(VehicleHolder other) {
        VehiclePiece collision = other.findOverlappingPiece(wholeVehicle);
        if (collision != null)
            return wholeVehicle.collideWithVehiclePieceX(collision);

        return false;
    }
    public boolean collideWithVehicleHolderY(VehicleHolder other) {
        VehiclePiece collision = other.findOverlappingPiece(wholeVehicle);
        if (collision != null)
            return wholeVehicle.collideWithVehiclePieceY(collision);

        return false;
    }

    public void updateSplits() {
        //TODO: update splitting points, adding and removing as necessary
    }

    public VehiclePiece findOverlappingPiece(VehiclePiece piece) {
        if (wholeVehicle.overlaps(piece) || piece.overlaps(wholeVehicle))
            return wholeVehicle;

        return null;
    }

    public VehiclePiece findOverlappingPiece(Rectangle rect) {
        if (wholeVehicle.overlaps(rect))
            return wholeVehicle;

        return null;
    }

    public VehiclePiece findClosestPiece(float x, float y) {
        return wholeVehicle;

        /*float closestDistSquared = Float.MAX_VALUE;
        VehiclePiece closest     = null;

        if (!horizontalPieces.isEmpty()); //TODO: check for the actual closest

        return closest;*/
    }

    public int getID() { return vehicle.getID(); }
    public Vehicle getVehicle() { return vehicle; }

    public void moveX(int diff) { vehicle.moveX(diff); }
    public void moveY(int diff) { vehicle.moveY(diff); }

    @Override
    public void render(GameContainer gc, Graphics g) { vehicle.render(gc, g); }

    @Override
    public void update(GameContainer gc, int diff) { vehicle.update(gc, diff); }

}
