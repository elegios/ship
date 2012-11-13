package ship.world.player;

import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.world.World;
import ship.world.vehicle.VehicleHolder;
import ship.world.vehicle.VehiclePiece;

public class PlayerHolder implements Updatable, Renderable, KeyReceiver {

    private Player player;
    private PlayerPieces pieces;

    private World world;

    public PlayerHolder(Player player) throws SlickException {
        this.player = player;
        this.world  = player.world();

        pieces = new PlayerPieces(this);
    }

    public Player getPlayer() { return player; }

    public PlayerPieces getPieces() { return pieces; }

    public void collideWithVehicleHolderX(VehicleHolder vehicle) {
        if (pieces.isSplit()) {
            pieces.setCurrentPiece(0);
            collideWithVehicleHolderXWorker(vehicle);

            pieces.setCurrentPiece(1);
            collideWithVehicleHolderXWorker(vehicle);

        } else {
            pieces.setCurrentPiece(0);
            collideWithVehicleHolderXWorker(vehicle);
        }
    }

    private void collideWithVehicleHolderXWorker(VehicleHolder vehicle) {
        VehiclePiece collision = vehicle.findOverlappingPiece(pieces);

        if (collision != null) {
            float fixMove = collision.collideRectangleX(pieces, player.getAbsXSpeed() - vehicle.getVehicle().getAbsXSpeed());
            if (fixMove != 0)
                player.collisionFixPosX(fixMove, vehicle);
        }
    }

    public void collideWithVehicleHolderY(VehicleHolder vehicle) {
        if (pieces.isSplit()) {
            pieces.setCurrentPiece(0);
            collideWithVehicleHolderYWorker(vehicle);

            pieces.setCurrentPiece(1);
            collideWithVehicleHolderYWorker(vehicle);

        } else {
            pieces.setCurrentPiece(0);
            collideWithVehicleHolderYWorker(vehicle);
        }
    }

    private void collideWithVehicleHolderYWorker(VehicleHolder vehicle) {
        VehiclePiece collision = vehicle.findOverlappingPiece(pieces);

        if (collision != null) {
            float fixMove = collision.collideRectangleY(pieces, player.getAbsYSpeed() - vehicle.getVehicle().getAbsYSpeed());
            if (fixMove != 0)
                player.collisionFixPosY(fixMove, vehicle);
        }
    }

    public int getCenterX() {
        return pieces.getCenterX();
    }

    public int getCenterY() {
        return pieces.getCenterY();
    }

    @Override
    public void render(GameContainer gc, Graphics g) { pieces.render(gc, g); }

    public void updateEarly(GameContainer gc, int diff) { player.updateEarly(gc, diff); }
    @Override
    public void update(GameContainer gc, int diff) { pieces.update(gc, diff); }

    public int getID() { return player.getID(); }

    public World world() { return world; }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == keys.activateDevice()) {
            world.activateUnderPlayer(this);
            return true;
        }

        return player.keyPressed(keys, key, c) || pieces.builder().keyPressed(keys, key, c);
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) {
        return player.keyReleased(keys, key, c) || pieces.builder().keyReleased(keys, key, c);
    }

}
