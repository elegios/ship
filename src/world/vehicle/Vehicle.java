/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world.vehicle;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import world.Rectangle;
import world.RelativeMovable;
import world.World;
import world.vehicle.block.Block;
import collisiongrid.CollisionGrid;

/**
 *
 * @author elegios
 */
public class Vehicle extends CollisionGrid {

    private Block[][] tiles;

    public Vehicle(World world, int id, int x, int y) throws SlickException {
        super(world, id, x, y, true, "vehicle");

        tiles = new Block[WIDTH][HEIGHT];

        addTile(new Block(WIDTH/2,     HEIGHT/2, 1, 100, true, true).setParent(this));
        addTile(new Block(WIDTH/2 - 1, HEIGHT/2, 1, 100, true, true).setParent(this));
    }

    public final void addTile(Block tile) {
        c("mass", mass + tile.mass());
        tile(tile.x(), tile.y(), tile);
    }

    public final void remTile(Block tile) {
        c("mass", mass - tile.mass());
        tile(tile.x(), tile.y(), null);
    }

    protected Rectangle getRectAt(int x, int y) { return tile(x, y); }
    protected byte      tileAt   (int x, int y) { return tile(x, y).tile(); }
    protected boolean   collidesAt(int x, int y) {
        return tile(x, y) != null && tile(x, y).collide();
    }
    protected boolean renderAt(int x, int y) {
        return tile(x, y) != null && tile(x, y).render();
    }
    protected void updateAt(int x, int y, GameContainer gc, int diff) {
        if (tile(x, y) != null)
            tile(x, y).update(gc, diff);
    }

    protected float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove) {
        if (rect instanceof RelativeMovable) {
            float momentum = ((RelativeMovable) rect).getMass() * xSpeed;
            pushBackX(momentum);
            ((RelativeMovable) rect).pushBackX(-momentum);
        }
        if (rect instanceof Block) {
            float ret = fixMove / 2;
            c("x", x - fixMove);
            return ret;
        }

        return fixMove;
    }

    protected float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove) {
        if (rect instanceof RelativeMovable) {
            float momentum = ((RelativeMovable) rect).getMass() * ySpeed;
            pushBackY(momentum);
            ((RelativeMovable) rect).pushBackY(-momentum);
        }
        if (rect instanceof Block) {
            float ret = fixMove / 2;
            c("y", y - fixMove);
            return ret;
        }

        return fixMove;
    }

    public void pushBackX(float momentum) {
        c("xSpeed", xSpeed + momentum / mass);
    }

    public void pushBackY(float momentum) {
        c("ySpeed", ySpeed + momentum / mass);
    }

    private Block tile(int x, int y) {
        return tiles[x][y];
    }
    private void tile(int x, int y, Block val) {
        tiles[x][y] = val;
    }

    protected void updateData   (String id, String  data) {}
    protected void updateBoolean(String id, boolean data) {}
    protected void updateInt    (String id, int     data) {}
    protected void updateFloat  (String id, float   data) {
        switch (id) {
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
            default:
                System.out.println("Unsupported variable in Vehicle: " +id);
        }
    }

}
