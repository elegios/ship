/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world.collisiongrid.island;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.World;
import ship.world.collisiongrid.CollisionGrid;


/**
 *
 * @author elegios
 */
public class Island extends CollisionGrid {
    private static final int ISLAND_WIDTH  = 512;
    private static final int ISLAND_HEIGHT = 512;

    private byte[][] tiles;

    public Island(World world, int id, int x, int y) throws SlickException {
        super(world, id, x, y, false, "island");

        tiles = new byte[WIDTH()][HEIGHT()];

        //temp island creation
        for (int i = 0; i < WIDTH(); i++)
            for (int j = 0; j < HEIGHT(); j++)
                tile(i, j, 0, false);

        for (int i = 0; i < WIDTH(); i++)
            for (int j = 0; j < 3; j++)
                tile(i, HEIGHT()/8 + j, 1, false);
    }

    protected Rectangle getRectAt (int x, int y) { return null; }
    public    boolean   collidesAt(int x, int y) { return tile(x, y) != 0; }
    protected boolean   renderAt  (int x, int y) { return tile(x, y) != 0; }
    public    boolean   existsAt  (int x, int y) { return tile(x, y) != 0; }
    protected int       tileAt    (int x, int y) { return tile(x, y); }
    protected void      updateAt  (int x, int y, GameContainer gc, int diff) {}

    protected int leftX () { return            0; }
    protected int rightX() { return WIDTH () - 1; }
    protected int topY  () { return            0; }
    protected int botY  () { return HEIGHT() - 1; }

    protected float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = ((RelativeMovable) rect);
            if (first) {
                rel.pushBackY(-rel.getMass() * (rel.getAbsYSpeed() - getAbsYSpeed()) * world.frictionFraction());
                rel.pushBackX(-rel.getMass() * xSpeed);
            }
        }

        return fixMove;
    }
    protected float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = ((RelativeMovable) rect);
            if (first) {
                rel.pushBackX(-rel.getMass() * (rel.getAbsXSpeed() - getAbsXSpeed()) * world.frictionFraction());
                rel.pushBackY(-rel.getMass() * ySpeed);
            }
        }

        return fixMove;
    }

    private byte tile(int x, int y) {
        return tiles[x][y];
    }
    private void tile(int x, int y, int val, boolean change) {
        if (change)
            c(x+ "." +y, val); //TODO: make this actually do something
        else {
            tiles[x][y] = (byte) val;
            setCollidesAt(x, y, val != 0);
        }
    }

    public int WIDTH () { return ISLAND_WIDTH;  }
    public int HEIGHT() { return ISLAND_HEIGHT; }

    @Override
    public void update(GameContainer gc, int diff) {} //Nothing is updated yet on an Island, at least not every frame

    public void moveX(int diff) {} //Slight speedup, islands are never moved
    public void moveY(int diff) {}

    public boolean collidedWithImmobileX() { return true; }
    public boolean collidedWithImmobileY() { return true; }

    public void pushBackX(float momentum) {}
    public void pushBackY(float momentum) {}

    protected void updateData   (String id, String  data) {}
    protected void updateInt    (String id, int     data) {}
    protected void updateBoolean(String id, boolean data) {}
    protected void updateFloat  (String id, float   data) {
        switch (id) {
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
            case "mass":
                mass = data;
                break;
            default:
                System.out.println("Unsupported variable in Island: " +id);
        }
    }

}
