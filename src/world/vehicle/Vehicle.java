/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world.vehicle;

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
import world.island.CollisionGrid;
import world.vehicle.block.Block;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.EasyNode;

/**
 *
 * @author elegios
 */
public class Vehicle implements Position, Renderable, Updatable, RelativeMovable, Rectangle, ChangeListener, CollisionGrid {
    private int id;

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
    private Block[][] tiles;

    public Vehicle(World world, int id, int x, int y) throws SlickException {
        this.world = world;
        this.id    = id;

        node   = world.view().node();

        tileset = world.view().loader().loadManagedSpriteSheet("tiles");
        tiles = new Block[WIDTH][HEIGHT];

        node.addChangeListener(this);

        c("x", (float) (x - getWidth()/2));
        c("y", (float) (y - getHeight()/2));
        c("xSpeed", 0.0f);
        c("ySpeed", 0.0f);
        c("mass", 0.0f);

        addTile(new Block(WIDTH/2, HEIGHT/2, 1, 100, true).setParent(this));
        addTile(new Block(WIDTH/2 - 1, HEIGHT/2, 1, 100, true).setParent(this));
    }

    public final void addTile(Block tile) {
        c("mass", mass + tile.mass());
        tile(tile.x(), tile.y(), tile);
    }

    public final void remTile(Block tile) {
        c("mass", mass - tile.mass());
        tile(tile.x(), tile.y(), null);
    }

    public void collideWithCollisionGridX(CollisionGrid other) {
        if (other.overlaps(this) || this.overlaps(other))
            for (Block[] blocks : tiles)
                for (Block block : blocks)
                    if (block != null && block.collide()) {
                        float fixMove = other.collideRectangleX(block, getAbsXSpeed() - other.getAbsXSpeed());
                        if (fixMove != 0)
                            c("x", x + fixMove);
                    }
    }

    public void collideWithCollisionGridY(CollisionGrid other) {
        if (other.overlaps(this) || this.overlaps(other))
            for (Block[] blocks : tiles)
                for (Block block : blocks)
                    if (block != null && block.collide()) {
                        float fixMove = other.collideRectangleY(block, getAbsYSpeed() - other.getAbsYSpeed());
                        if (fixMove != 0)
                            c("y", y + fixMove);
                    }
    }

    public float collideRectangleX(Rectangle rect, float xSpeed) { return collideRectangleX(rect, xSpeed, 0); }
    public float collideRectangleX(Rectangle rect, float xSpeed, float xMod) {
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
    public float collideRectangleY(Rectangle rect, float ySpeed, float yMod) {
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
            tile(x, y) != null &&
            tile(x, y).collide())
            return true;
        return false;
    }

    public void pushBackX(float momentum) {
        c("xSpeed", xSpeed + momentum / mass);
    }

    public void pushBackY(float momentum) {
        c("ySpeed", ySpeed + momentum / mass);
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        float xMax = (View.window().getWidth()  - getX() - world.getX())/32;
        float xMin = (                  - 32  - getX() - world.getX())/32;
        float yMax = (View.window().getHeight() - getY() - world.getY())/32;
        float yMin = (                   - 32 - getY() - world.getY())/32;
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                if (tile(i, j)     != null &&
                    i <= xMax && i >= xMin &&
                    j <= yMax && j >= yMin)
                    tileset.getSpriteSheet().renderInUse(ix() + i*32, iy() + j*32, tile(i, j).tile()%32, tile(i, j).tile()/32);

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

    private Block tile(int x, int y) {
        return tiles[x][y];
    }
    private void tile(int x, int y, Block val) {
        tiles[x][y] = val;
    }

    public void moveX(int diff) { c("x", x + getAbsXMove(diff)); }
    public void moveY(int diff) { c("y", y + getAbsYMove(diff)); }

    @Override
    public void update(GameContainer gc, int diff) {
        for (Block[] blocks : tiles)
            for (Block block : blocks)
                if (block != null)
                    block.update(gc, diff);

        c("ySpeed", ySpeed + world.actionsPerTick() * diff * world.gravity() * mass);
        //c("ySpeed", 160.0f);
    }

    public final void c(String id, Object data) { node.c("vehicle." +this.id+ "." +id, data); }

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

    public void dataChanged(String id, String data) {}
    public void intChanged(String id, int data) {}
    public void booleanChanged(String id, boolean data) {}
    public void floatChanged(String id, float data) {
        if (id.startsWith("vehicle." +this.id+ ".")) {
            String var = id.substring(("vehicle." +this.id+ ".").length());
            switch (var) {
                case "mass":
                    mass = data;
                    break;
                case "x":
                    x = data;
                    break;
                case "y":
                    y = data;
                    break;
                case "xSpeed":
                    xSpeed = data;
                    break;
                case "ySpeed":
                    ySpeed = data;
                    break;
            }
        }
    }

}
