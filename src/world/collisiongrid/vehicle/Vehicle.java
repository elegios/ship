/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world.collisiongrid.vehicle;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import world.Rectangle;
import world.RelativeMovable;
import world.World;
import world.collisiongrid.CollisionGrid;
import world.collisiongrid.vehicle.block.Block;
import world.collisiongrid.vehicle.block.KeyThruster;

/**
 *
 * @author elegios
 */
public class Vehicle extends CollisionGrid {

    private Block[][] tiles;

    private int leftX;
    private int rightX;
    private int topY;
    private int botY;

    public Vehicle(World world, int id, int x, int y) throws SlickException {
        super(world, id, x, y, true, "vehicle");

        tiles = new Block[WIDTH][HEIGHT];

        for (int i = 0; i < 17; i++)
            addTile(new Block(WIDTH/2 + i, HEIGHT/2, 1, 5, true, true));
        addTile(new KeyThruster(WIDTH/2 + 17, HEIGHT / 2, Input.KEY_SPACE, 20));
        addTile(new KeyThruster(WIDTH/2 + 18, HEIGHT / 2, Input.KEY_SPACE, 100));
        addTile(new KeyThruster(WIDTH/2 - 1, HEIGHT / 2, Input.KEY_R, 100));
    }

    public final void addTile(Block tile) {
        c("mass", mass + tile.mass());
        tile(tile.x(), tile.y(), tile);
        tile.setParent(this);

        if (tile.x() < leftX)
            leftX = tile.x();
        else if (tile.x() > rightX)
            rightX = tile.x();
        if (tile.y() < topY)
            topY = tile.y();
        else if (tile.y() > botY)
            botY = tile.y();
    }

    public final void remTile(Block tile) {
        c("mass", mass - tile.mass());
        tile(tile.x(), tile.y(), null);
        tile.setParent(this);
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
            RelativeMovable rel = (RelativeMovable) rect;
            rel.pushBackY(-rel.getMass() * (rel.getAbsYSpeed() - getAbsYSpeed()) * world.frictionFraction());
            float momentum = rel.getMass() * xSpeed;
            pushBackX(momentum);
            rel.pushBackX(-momentum);
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
            RelativeMovable rel = (RelativeMovable) rect;
            rel.pushBackX(-rel.getMass() * (rel.getAbsXSpeed() - getAbsXSpeed()) * world.frictionFraction());
            float momentum = rel.getMass() * ySpeed;
            pushBackY(momentum);
            rel.pushBackY(-momentum);
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

    public void update(GameContainer gc, int diff) {
        super.update(gc, diff);

        pushBackX(-xSpeed/mass * world.airResist() * (botY   - topY));
        pushBackY(-ySpeed/mass * world.airResist() * (rightX - leftX));
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
