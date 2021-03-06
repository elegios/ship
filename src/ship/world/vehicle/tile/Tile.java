/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world.vehicle.tile;

import org.newdawn.slick.GameContainer;

import ship.Updatable;
import ship.netcode.ShipProtocol;
import ship.netcode.tile.ContentPackage;
import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.player.Player;
import ship.world.vehicle.Vehicle;

/**
 *
 * @author elegios
 */
public class Tile implements Updatable, Rectangle, RelativeMovable {
    public static final int UP    = 0;
    public static final int RIGHT = 1;
    public static final int DOWN  = 2;
    public static final int LEFT  = 3;

    public static final float STDMASS = 5;

    protected Vehicle parent;

    private int x;
    private int y;

    private int tile;

    private boolean collide;

    private float mass;

    private boolean powered;
    private boolean fueled;

    public Tile(int x, int y, int tile, float mass, boolean collide) {
        this.x = x;
        this.y = y;

        this.tile = tile;

        this.mass = mass;

        this.collide = collide;
    }

    public Tile setParent(Vehicle parent) { this.parent = parent; return this; }

    /**
     * Get the Tile adjacent to the current Tile in the given direction.
     * @param direction the direction in which the Tile is
     * @return an adjacent Tile
     */
    protected Tile getFrom(int direction) {
        switch (direction) {
            case UP:
                return parent.tile(x    , y - 1);

            case RIGHT:
                return parent.tile(x + 1, y    );

            case DOWN:
                return parent.tile(x    , y + 1);

            case LEFT:
                return parent.tile(x - 1, y    );
        }

        return null;
    }

    public int     x()       { return x; }
    public int     y()       { return y; }
    public int     tile()    { return tile; }
    public boolean collide() { return collide; }
    public float   mass()    { return mass; }
    public boolean powered() { return powered; }
    public boolean fueled()  { return fueled; }

    public void power(boolean power) { powered = power; }
    public void fuel (boolean fuel)  { fueled  = fuel;  }

    public boolean powerFrom(int direction              ) { return false; }
    public boolean fuelFrom (int direction, float amount) { return false; }

    public void activate(Player player) {}

    public final int ix() { return Math.round(getX()); }
    public final int iy() { return Math.round(getY()); }

    public final float getX() { return parent.getX() + x*Vehicle.TW; }
    public final float getY() { return parent.getY() + y*Vehicle.TH; }

    public final int getWidth () { return Vehicle.TW; }
    public final int getHeight() { return Vehicle.TH; }

    public final float getX2() { return getX() +  getWidth() - 1; }
    public final float getY2() { return getY() + getHeight() - 1; }

    public void update     (GameContainer gc, int diff) {}
    public void updateEarly(GameContainer gc, int diff) {}

    public void moveX(int diff) {}
    public void moveY(int diff) {}

    public float getAbsXSpeed() { return parent.getAbsXSpeed(); }
    public float getAbsYSpeed() { return parent.getAbsYSpeed(); }

    public boolean collidedWithImmobileX() { return parent.collidedWithImmobileX(); }
    public boolean collidedWithImmobileY() { return parent.collidedWithImmobileY(); }
    public float   collisionLockX()        { return parent.collisionLockX();        }
    public float   collisionLockY()        { return parent.collisionLockY();        }

    public void collidedWithImmobileX(boolean val) { parent.collidedWithImmobileX(val); }
    public void collidedWithImmobileY(boolean val) { parent.collidedWithImmobileY(val); }
    public void collisionLockX       (float   val) { parent.collisionLockX       (val); }
    public void collisionLockY       (float   val) { parent.collisionLockY       (val); }

    public float getAbsXMove(int diff) { return parent.getAbsXMove(diff); }
    public float getAbsYMove(int diff) { return parent.getAbsYMove(diff); }

    public float getMass() { return parent.getMass(); }

    public void pushX(float momentum) { parent.pushX(momentum); }
    public void pushY(float momentum) { parent.pushY(momentum); }

    public void sendContentPackage(float content) {
        if (parent.world().view().net().isServer())
            parent.world().view().net().send(ShipProtocol.CONTENT, new ContentPackage(parent.getID(), x, y, content));
    }

    public void receiveContentPackage(ContentPackage pack) {}

}
