/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world.collisiongrid.vehicle.block;

import org.newdawn.slick.GameContainer;

import ship.Updatable;
import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.collisiongrid.CollisionGrid;
import ship.world.collisiongrid.vehicle.Vehicle;

/**
 *
 * @author elegios
 */
public class Block implements Updatable, Rectangle, RelativeMovable {
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
    private boolean render;

    private float mass;

    private boolean powered;
    private boolean fueled;

    public Block(int x, int y, int tile, float mass, boolean collide, boolean render) {
        this.x = x;
        this.y = y;

        this.tile = tile;

        this.mass = mass;

        this.collide = collide;
        this.render  = render;
    }

    public Block setParent(Vehicle parent) { this.parent = parent; return this; }

    public int     x()       { return x; }
    public int     y()       { return y; }
    public int     tile()    { return tile; }
    public boolean collide() { return collide; }
    public boolean render()  { return render; }
    public float   mass()    { return mass; }
    public boolean powered() { return powered; }
    public boolean fueled()  { return fueled; }

    public void power(boolean power) { powered = power; }
    public void fuel (boolean fuel)  { fueled  = fuel;  }

    public boolean powerFrom(int direction) { return false; }
    public boolean fuelFrom (int direction) { return false; }

    public void activate() {}

    public final int ix() { return Math.round(getX()); }
    public final int iy() { return Math.round(getY()); }

    public final float getX() { return parent.getX() + x*CollisionGrid.TW; }
    public final float getY() { return parent.getY() + y*CollisionGrid.TH; }

    public final int  getWidth() { return CollisionGrid.TW; }
    public final int getHeight() { return CollisionGrid.TH; }

    public final float getX2() { return getX() +  getWidth() - 1; }
    public final float getY2() { return getY() + getHeight() - 1; }

    public void update(GameContainer gc, int diff) {}

    public void moveX(int diff) {}
    public void moveY(int diff) {}

    public float getAbsXSpeed() { return parent.getAbsXSpeed(); }
    public float getAbsYSpeed() { return parent.getAbsYSpeed(); }

    public boolean collidedWithImmobileX() { return parent.collidedWithImmobileX(); }
    public boolean collidedWithImmobileY() { return parent.collidedWithImmobileY(); }

    public void collidedWithImmobileX(boolean val) { parent.collidedWithImmobileX(val); }
    public void collidedWithImmobileY(boolean val) { parent.collidedWithImmobileY(val); }

    public float getAbsXMove(int diff) { return parent.getAbsXMove(diff); }
    public float getAbsYMove(int diff) { return parent.getAbsYMove(diff); }

    public float getMass() { return parent.getMass(); }

    public void pushBackX(float momentum) { parent.pushBackX(momentum); }
    public void pushBackY(float momentum) { parent.pushBackY(momentum); }

    public void c(String id, Object data) { parent.c("tile." +x+ "." +y+ "." +id, data); }

    public void updateData   (String id, String  data) {}
    public void updateInt    (String id, int     data) {}
    public void updateBoolean(String id, boolean data) {}
    public void updateFloat  (String id, float   data) {}

}
