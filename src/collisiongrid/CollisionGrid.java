/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collisiongrid;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import world.Position;
import world.Rectangle;
import world.RelativeMovable;
import world.World;
import world.vehicle.block.Block;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.EasyNode;

/**
 *
 * @author elegios
 */
public abstract class CollisionGrid implements Position, Renderable, Updatable, RelativeMovable, Rectangle, ChangeListener {
    private int id;

    protected static boolean centerInit;
    protected static String  name;

    public static final int WIDTH  = 1024;
    public static final int HEIGHT = 1024;

    private float mass;

    private float x;
    private float y;

    private float xSpeed;
    private float ySpeed;

    private World world;

    private EasyNode    node;

    private ManagedSpriteSheet tileset;

    public CollisionGrid(World world, int id, int x, int y) throws SlickException {
        this.world = world;
        this.id    = id;

        node = world.view().node();

        tileset = world.view().loader().loadManagedSpriteSheet("tiles");

        node.addChangeListener(this);

        if (centerInit) {
            c("x", (float) (x - getWidth()/2));
            c("y", (float) (y - getHeight()/2));
        } else {
            c("x", (float) x);
            c("y", (float) y);
        }
        c("xSpeed", 0.0f);
        c("ySpeed", 0.0f);
        c("mass", 0.0f);
    }

    protected abstract Rectangle getRectAt (int x, int y);
    protected abstract boolean   collidesAt(int x, int y);
    protected abstract boolean   renderAt  (int x, int y);
    protected abstract byte      tileAt    (int x, int y);
    protected abstract void      updateAt  (int x, int y, GameContainer gc, int diff);

    protected abstract void updateData   (String id, String  data);
    protected abstract void updateInt    (String id, int     data);
    protected abstract void updateBoolean(String id, boolean data);
    protected abstract void updateFloat  (String id, float   data);

    public abstract void pushBackX(float momentum);
    public abstract void pushBackY(float momentum);

    public void collideWithCollisionGridX(CollisionGrid other) {
        if (other.overlaps(this) || this.overlaps(other))
            for (int i = 0; i < WIDTH; i++)
                for (int j = 0; j < HEIGHT; j++)
                    if (collidesAt(i, j)) {
                        float fixMove = other.collideRectangleX(getRectAt(i, j), getAbsXSpeed() - other.getAbsXSpeed());
                        if (fixMove != 0)
                            c("x", x + fixMove);
                    }
    }

    public void collideWithCollisionGridY(CollisionGrid other) {
        if (other.overlaps(this) || this.overlaps(other))
            for (int i = 0; i < WIDTH; i++)
                for (int j = 0; j < HEIGHT; j++)
                    if (collidesAt(i, j)) {
                        float fixMove = other.collideRectangleY(getRectAt(i, j), getAbsYSpeed() - other.getAbsYSpeed());
                        if (fixMove != 0)
                            c("y", y + fixMove);
                    }
    }

    public float collideRectangleX(Rectangle rect, float xSpeed) { return collideRectangleX(rect, xSpeed, 0); }
    public float collideRectangleX(Rectangle rect, float xSpeed, float xMod) { //TODO
        if (xSpeed > 0) {
            int i  = (int) Math.ceil(rect.getX2() + xMod - getX())/32;
            int j1 = (int)          (rect.getY()         - getY())/32;
            int j2 = (int) Math.ceil(rect.getY2()        - getY())/32;
            if (collides(i, j1) || collides(i, j2)) {
                if (rect instanceof RelativeMovable) {
                    float momentum = ((RelativeMovable) rect).getMass() * xSpeed;
                    pushBackX(momentum);
                    ((RelativeMovable) rect).pushBackX(-momentum);
                }
                float fixMove = getX() + i*32 - rect.getX() - xMod - rect.getWidth();
                if (rect instanceof Block) {
                    fixMove /= 2;
                    c("x", x - fixMove);
                }
                if (fixMove == 0)
                    return 0;
                return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod);
            }
        } else if (xSpeed < 0) {
            int i  = (int)          (rect.getX() + xMod - getX())/32;
            int j1 = (int)          (rect.getY()        - getY())/32;
            int j2 = (int) Math.ceil(rect.getY2()       - getY())/32;
            if (collides(i, j1) || collides(i, j2)) {
                if (rect instanceof RelativeMovable) {
                    float momentum = ((RelativeMovable) rect).getMass() * xSpeed;
                    pushBackX(momentum);
                    ((RelativeMovable) rect).pushBackX(-momentum);
                }
                float fixMove = getX() + i*32 + 32 - rect.getX() - xMod;
                if (rect instanceof Block) {
                    fixMove /= 2;
                    c("x", x - fixMove);
                }
                if (fixMove == 0)
                    return 0;
                return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod);
            }
        }

        return 0;
    }

    public float collideRectangleY(Rectangle rect, float ySpeed) { return collideRectangleY(rect, ySpeed, 0); }
    public float collideRectangleY(Rectangle rect, float ySpeed, float yMod) { //TODO
        if (ySpeed > 0) {
            int i1 = (int)          (rect.getX()         - getX())/32;
            int i2 = (int) Math.ceil(rect.getX2()        - getX())/32;
            int j  = (int) Math.ceil(rect.getY2() + yMod - getY())/32;
            if (collides(i1, j) || collides(i2, j)) {
                if (rect instanceof RelativeMovable) {
                    float momentum = ((RelativeMovable) rect).getMass() * ySpeed;
                    pushBackY(momentum);
                    ((RelativeMovable) rect).pushBackY(-momentum);
                }
                float fixMove = getY() + j*32 - rect.getY() - yMod - rect.getHeight();
                if (rect instanceof Block) {
                    fixMove /= 2;
                    c("y", y - fixMove);
                }
                if (fixMove == 0)
                    return 0;
                return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod);
            }
        } else if (ySpeed < 0) {
            int i1 = (int)          (rect.getX()        - getX())/32;
            int i2 = (int) Math.ceil(rect.getX2()       - getX())/32;
            int j  = (int)          (rect.getY() + yMod - getY())/32;
            if (collides(i1, j) || collides(i2, j)) {
                if (rect instanceof RelativeMovable) {
                    float momentum = ((RelativeMovable) rect).getMass() * ySpeed;
                    pushBackY(momentum);
                    ((RelativeMovable) rect).pushBackY(-momentum);
                }
                float fixMove = getY() + j*32 + 32 - rect.getY() - yMod;
                if (rect instanceof Block) {
                    fixMove /= 2;
                    c("y", y - fixMove);
                }
                if (fixMove == 0)
                    return 0;
                return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod);
            }
        }

        return 0;
    }

    private boolean collides(int x, int y) {
        if (0 <= x && x < WIDTH &&
            0 <= y && y < HEIGHT &&
            collidesAt(x, y))
            return true;
        return false;
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        float xMax = (View.window().getWidth()  - getX() - world.getX())/32;
        float xMin = (                    - 32  - getX() - world.getX())/32;
        float yMax = (View.window().getHeight() - getY() - world.getY())/32;
        float yMin = (                     - 32 - getY() - world.getY())/32;
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                if (renderAt(i, j) &&
                    i <= xMax && i >= xMin &&
                    j <= yMax && j >= yMin)
                    tileset.getSpriteSheet().renderInUse(ix() + i*32, iy() + j*32, tileAt(i, j)%32, tileAt(i, j)/32);

    }

    public boolean overlaps(Rectangle rect) {
        if (((rect.getX()  >= getX() && rect.getX()  < getX2()) ||
             (rect.getX2() >= getX() && rect.getX2() < getX2()))
            &&
            ((rect.getY()  >= getY() && rect.getY()  < getY2()) ||
             (rect.getY2() >= getY() && rect.getY2() < getY2())))
            return true;
        return false;
    }

    public void moveX(int diff) { c("x", x + getAbsXMove(diff)); }
    public void moveY(int diff) { c("y", y + getAbsYMove(diff)); }

    @Override
    public void update(GameContainer gc, int diff) {
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                updateAt(i, j, gc, diff);

        c("ySpeed", ySpeed + world.actionsPerTick() * diff * world.gravity() * mass);
    }

    public final void c(String id, Object data) { node.c(name+ "." +this.id+ "." +id, data); }

    public float getAbsXSpeed() { return xSpeed; }
    public float getAbsYSpeed() { return ySpeed; }

    public float getAbsXMove(int diff) { return getAbsXSpeed() * world.actionsPerTick() * diff; }
    public float getAbsYMove(int diff) { return getAbsYSpeed() * world.actionsPerTick() * diff; }

    public float getMass() { return mass; }

    public final int getWidth()  { return  WIDTH*32; }
    public final int getHeight() { return HEIGHT*32; }

    public float getX2() { return getX() +  getWidth() - 1; }
    public float getY2() { return getY() + getHeight() - 1; }

    public float getX() { return x; }
    public float getY() { return y; }

    public int ix() { return Math.round(world.getX() + getX()); }
    public int iy() { return Math.round(world.getY() + getY()); }

    public void dataChanged(String id, String data) {
        if (id.startsWith(name+ "." +this.id+ "."))
            updateData(id.substring((name+ "." +this.id+ ".").length()), data);
        }
    public void intChanged(String id, int data) {
        if (id.startsWith(name+ "." +this.id+ "."))
            updateInt(id.substring((name+ "." +this.id+ ".").length()), data);
        }
    public void booleanChanged(String id, boolean data) {
        if (id.startsWith(name+ "." +this.id+ "."))
            updateBoolean(id.substring((name+ "." +this.id+ ".").length()), data);
    }
    public void floatChanged(String id, float data) {
        if (id.startsWith(name+ "." +this.id+ "."))
            updateFloat(id.substring((name+ "." +this.id+ ".").length()), data);
    }

}
