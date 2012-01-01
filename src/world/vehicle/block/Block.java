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
import collisiongrid.CollisionGrid;

/**
 *
 * @author elegios
 */
public class Block implements Updatable, Rectangle, RelativeMovable {

    protected Vehicle parent;

    private int x;
    private int y;

    protected byte tile;

    protected boolean collide;
    protected boolean render;

    protected float mass;

    public Block(int x, int y, byte tile, float mass, boolean collide, boolean render) {
        this.x = x;
        this.y = y;

        this.tile = tile;

        this.mass = mass;

        this.collide = collide;
        this.render  = render;
    }
    public Block(int x, int y, int tile, float mass, boolean collide, boolean render) { this(x, y, (byte) tile, mass, collide, render); }

    public Block setParent(Vehicle parent) { this.parent = parent; return this; }

    public int     x()       { return x; }
    public int     y()       { return y; }
    public byte    tile()    { return tile; }
    public boolean collide() { return collide; }
    public boolean render()  { return render; }
    public float   mass()    { return mass; }

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

    public float getAbsXMove(int diff) { return parent.getAbsXMove(diff); }
    public float getAbsYMove(int diff) { return parent.getAbsYMove(diff); }

    public float getMass() { return parent.getMass(); }

    public void pushBackX(float momentum) { parent.pushBackX(momentum); }
    public void pushBackY(float momentum) { parent.pushBackY(momentum); }

}
