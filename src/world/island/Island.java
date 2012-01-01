/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world.island;

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

/**
 *
 * @author elegios
 */
public class Island implements Position, Renderable, Updatable, RelativeMovable, Rectangle, CollisionGrid {  //too slow to use normal node to store data
    private int id; //will be used whenever syncing across instances is implemented

    public static final int WIDTH  = 1024;
    public static final int HEIGHT = 1024;

    private int x;
    private int y;

    private World world;

    private ManagedSpriteSheet tileset;
    private byte[][] tiles;

    public Island(World world, int id, int x, int y) throws SlickException {
        this.world = world;
        this.id    = id;

        tileset = world.view().loader().loadManagedSpriteSheet("tiles");
        tiles = new byte[WIDTH][HEIGHT];

        this.x = x;
        this.y = y;

        //temp island creation
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                tile(i, j, 0);
        tile(0, 1, 1);
        tile(2, 4, 1);
        for (int i = 0; i < WIDTH; i++)
            tile(i, 5, 1);
    }

    public float collideRectangleX(Rectangle rect, float xSpeed) { return collideRectangleX(rect, xSpeed, 0); }
    public float collideRectangleX(Rectangle rect, float xSpeed, float xMod) {
        if (xSpeed > 0) {
            int i  = (int) Math.ceil(rect.getX2() + xMod - getX())/32;
            int j1 = (int)          (rect.getY()         - getY())/32;
            int j2 = (int) Math.ceil(rect.getY2()        - getY())/32;
            if (collides(i, j1) || collides(i, j2)) {
                if (rect instanceof RelativeMovable)
                    ((RelativeMovable) rect).pushBackX(-((RelativeMovable) rect).getMass() * xSpeed);
                float fixMove = getX() + i*32 - rect.getX() - xMod - rect.getWidth();
                if (fixMove == 0)
                    return 0;
                return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod);
            }
        } else if (xSpeed < 0) {
            int i  = (int)          (rect.getX() + xMod - getX())/32;
            int j1 = (int)          (rect.getY()        - getY())/32;
            int j2 = (int) Math.ceil(rect.getY2()       - getY())/32;
            if (collides(i, j1) || collides(i, j2)) {
                if (rect instanceof RelativeMovable)
                    ((RelativeMovable) rect).pushBackX(-((RelativeMovable) rect).getMass() * xSpeed);
                float fixMove = getX() + i*32 + 32 - rect.getX() - xMod;
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
                if (rect instanceof RelativeMovable)
                    ((RelativeMovable) rect).pushBackY(-((RelativeMovable) rect).getMass() * ySpeed);
                float fixMove = getY() + j*32 - rect.getY() - yMod - rect.getHeight();
                if (fixMove == 0)
                    return 0;
                return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod);
            }
        } else if (ySpeed < 0) {
            int i1 = (int)          (rect.getX()        - getX())/32;
            int i2 = (int) Math.ceil(rect.getX2()       - getX())/32;
            int j  = (int)          (rect.getY() - yMod - getY())/32;
            if (collides(i1, j) || collides(i2, j)) {
                if (rect instanceof RelativeMovable)
                    ((RelativeMovable) rect).pushBackY(-((RelativeMovable) rect).getMass() * ySpeed);
                float fixMove = getY() + j*32 + 32 - rect.getY() - yMod;
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
            tile(x, y) != 0)
            return true;
        return false;
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        float xMax = (View.window().getWidth() - getX() - world.getX())/32;
        float xMin = (                   -32 - getX() - world.getX())/32;
        float yMax = (View.window().getHeight() - getY() - world.getY())/32;
        float yMin = (                    -32 - getY() - world.getY())/32;
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                if (i <= xMax && i >= xMin &&
                    j <= yMax && j >= yMin)
                    tileset.getSpriteSheet().renderInUse(ix() + i*32, iy() + j*32, tile(i, j)%32, tile(i, j)/32);

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

    private byte tile(int x, int y) {
        return tiles[x][y];
    }
    private void tile(int x, int y, int val) {
        tiles[x][y] = (byte) val;
    }

    @Override
    public void update(GameContainer gc, int diff) {

    }

    public void moveX(int diff) {}
    public void moveY(int diff) {}

    public void pushBackX(float momentum) {}
    public void pushBackY(float momentum) {}

    public float getAbsXSpeed() { return 0; }
    public float getAbsYSpeed() { return 0; }

    public float getAbsXMove(int diff) { return 0; }
    public float getAbsYMove(int diff) { return 0; }

    public float getMass() { return 0; }

    public int getWidth()  { return  WIDTH*32; }
    public int getHeight() { return HEIGHT*32; }

    public float getX2() { return getX() +  getWidth() - 1; }
    public float getY2() { return getY() + getHeight() - 1; }

    public float getX() { return x; }
    public float getY() { return y; }

    public int ix() { return Math.round(world.getX() + getX()); }
    public int iy() { return Math.round(world.getY() + getY()); }

}
