/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world.vehicle.block;

import org.newdawn.slick.GameContainer;

import ship.Updatable;
import world.Rectangle;
import world.RelativeMovable;
import world.vehicle.Vehicle;

/**
 *
 * @author elegios
 */
public class Block implements Updatable, Rectangle, RelativeMovable {

    private Vehicle parent;

    private int x;
    private int y;

    private byte tile;

    private boolean collide;

    private float mass;

    public Block(int x, int y, byte tile, float mass, boolean collide) {
        this.x = x;
        this.y = y;

        this.tile = tile;

        this.mass = mass;

        this.collide = collide;
    }
    public Block(int x, int y, int tile, float mass, boolean collide) { this(x, y, (byte) tile, mass, collide); }

    public Block setParent(Vehicle parent) { this.parent = parent; return this; }

    public int     x()       { return x; }
    public int     y()       { return y; }
    public byte    tile()    { return tile; }
    public boolean collide() { return collide; }
    public float   mass()    { return mass; }

    public final int ix() { return Math.round(getX()); }
    public final int iy() { return Math.round(getY()); }

    public final float getX() { return parent.getX() + x*32; }
    public final float getY() { return parent.getY() + y*32; }

    public final int  getWidth() { return 32; }
    public final int getHeight() { return 32; }

    public final float getX2() { return getX() +  getWidth() - 1; }
    public final float getY2() { return getY() + getHeight() - 1; }

    public void update(GameContainer gc, int diff) {}

    public void moveX(int diff) {}
    public void moveY(int diff) {}

    public float getAbsXSpeed() { return parent.getAbsXSpeed(); }
    public float getAbsYSpeed() { return parent.getAbsYSpeed(); }

    public float getAbsXMove(int diff) { return parent.getAbsXMove(diff); }
    public float getAbsYMove(int diff) { return parent.getAbsYMove(diff); }

    public float getMass() { return parent.getMass(); }

    public void pushBackX(float momentum) { parent.pushBackX(momentum); }
    public void pushBackY(float momentum) { parent.pushBackY(momentum); }

}
