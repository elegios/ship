/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world.collisiongrid.island;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import world.Rectangle;
import world.RelativeMovable;
import world.World;
import world.collisiongrid.CollisionGrid;

/**
 *
 * @author elegios
 */
public class Island extends CollisionGrid {  //too slow to use normal node to store data

    private byte[][] tiles;

    public Island(World world, int id, int x, int y) throws SlickException {
        super(world, id, x, y, false, "island");

        tiles = new byte[WIDTH][HEIGHT];

        //temp island creation
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                tile(i, j, 0, false);

        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < 3; j++)
                tile(i, 20 + j, 1, false);
    }

    protected Rectangle getRectAt (int x, int y) { return null; }
    protected boolean   collidesAt(int x, int y) { return tile(x, y) != 0; }
    protected boolean   renderAt  (int x, int y) { return true; }
    protected byte      tileAt    (int x, int y) { return tile(x, y); }
    protected void      updateAt  (int x, int y, GameContainer gc, int diff) {}

    protected float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = ((RelativeMovable) rect);
            rel.pushBackX(-rel.getMass() * xSpeed);
            rel.pushBackY(-rel.getMass() * (rel.getAbsYSpeed() - getAbsYSpeed()) * world.frictionFraction());
        }
        return fixMove;
    }
    protected float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = ((RelativeMovable) rect);
            rel.pushBackY(-rel.getMass() * ySpeed);
            rel.pushBackX(-rel.getMass() * (rel.getAbsXSpeed() - getAbsXSpeed()) * world.frictionFraction());
        }
        return fixMove;
    }

    private byte tile(int x, int y) {
        return tiles[x][y];
    }
    private void tile(int x, int y, int val) { tile(x, y, val, true); } //For later, might be best to make public though
    private void tile(int x, int y, int val, boolean change) {
        if (change)
            c(x+ "." +y, val);
        else
            tiles[x][y] = (byte) val;
    }

    @Override
    public void update(GameContainer gc, int diff) {} //Nothing is updated yet on an Island, at least not every frame

    public void moveX(int diff) {} //Slight speedup, islands are never moved
    public void moveY(int diff) {}

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
