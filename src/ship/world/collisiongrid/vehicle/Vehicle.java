/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world.collisiongrid.vehicle;

import java.util.Scanner;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import ship.ui.inventory.Inventory;
import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.World;
import ship.world.collisiongrid.CollisionGrid;
import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.collisiongrid.vehicle.block.Thruster;
import ship.world.collisiongrid.vehicle.block.fuel.AirFuelTransport;
import ship.world.collisiongrid.vehicle.block.fuel.FuelTank;
import ship.world.collisiongrid.vehicle.block.fuel.FuelTap;
import ship.world.collisiongrid.vehicle.block.fuel.FuelTransport;
import ship.world.player.Player;


/**
 *
 * @author elegios
 */
public class Vehicle extends CollisionGrid {
    private static final int VEHICLE_WIDTH  = 65;
    private static final int VEHICLE_HEIGHT = 65;

    private Block[][] tiles;

    private int leftX;
    private int rightX;
    private int topY;
    private int botY;

    private Inventory inv;

    public Vehicle(World world, int id, int x, int y) throws SlickException {
        super(world, id, x, y, true, "vehicle");
        inv = world.view().inventory();

        tiles = new Block[WIDTH()][HEIGHT()];
    }

    public void generateStandardVehicle() {
        int mx = WIDTH ()/2;
        int my = HEIGHT()/2;

        addTile(new FuelTap(mx - 1, my, Block.RIGHT));

        addTile(new AirFuelTransport(mx - 2, my - 1, false, Block.LEFT));
        addTile(new FuelTank(mx - 2, my));
        addTile(new FuelTransport(mx - 2, my + 1, false, Block.UP));

        addTile(new AirFuelTransport(mx - 3, my - 1, false, Block.DOWN));
        addTile(new Thruster(mx - 3, my, Block.RIGHT));
        addTile(new FuelTransport(mx - 3, my + 1, false, Block.RIGHT));


        addTile(new FuelTap(mx, my, Block.UP));

        addTile(new FuelTransport(mx - 1, my + 1, false, Block.DOWN));
        addTile(new FuelTank(mx, my + 1));
        addTile(new FuelTransport(mx + 1, my + 1, false, Block.LEFT));

        addTile(new AirFuelTransport(mx - 1, my + 2, false, Block.RIGHT));
        addTile(new Thruster(mx, my + 2, Block.UP));
        addTile(new AirFuelTransport(mx + 1, my + 2, false, Block.UP));


        addTile(new FuelTap(mx + 1, my, Block.LEFT));

        addTile(new AirFuelTransport(mx + 2, my - 1, false, Block.DOWN));
        addTile(new FuelTank(mx + 2, my));
        addTile(new FuelTransport(mx + 2, my + 1, false, Block.RIGHT));

        addTile(new AirFuelTransport(mx + 3, my - 1, false, Block.LEFT));
        addTile(new Thruster(mx + 3, my, Block.LEFT));
        addTile(new FuelTransport(mx + 3, my + 1, false, Block.UP));


        addTile(new Block(mx - 4, my + 1, 1, 5, true, true));
        addTile(new Block(mx - 5, my + 2, 1, 5, true, true));

        addTile(new Block(mx + 4, my + 1, 1, 5, true, true));
        addTile(new Block(mx + 5, my + 2, 1, 5, true, true));

        addTile(new Block(mx - 1, my - 2, 1, 5, true, true));
        addTile(new Block(mx    , my - 2, 1, 5, true, true));
        addTile(new Block(mx + 1, my - 2, 1, 5, true, true));
    }

    public final void addTile(Block tile) {
        c("mass", mass + tile.mass());
        tile(tile.x(), tile.y(), tile);
        setCollidesAt(tile.x(), tile.y(), tile.collide());
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
        setCollidesAt(tile.x(), tile.y(), false);
    }

    protected Rectangle getRectAt(int x, int y) { return tile(x, y); }
    protected int       tileAt   (int x, int y) { return tile(x, y).tile(); }
    protected boolean renderAt(int x, int y) {
        return tile(x, y) != null && tile(x, y).render();
    }
    public boolean existsAt(int x, int y) { return tile(x, y) != null; }
    protected void updateAt(int x, int y, GameContainer gc, int diff) {
        if (tile(x, y) != null)
            tile(x, y).update(gc, diff);
    }

    protected int leftX () { return leftX;  }
    protected int rightX() { return rightX; }
    protected int topY  () { return topY;   }
    protected int botY  () { return botY;   }

    protected float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = (RelativeMovable) rect;
            if (first) {
                float frictionMomentum = rel.getMass() * (rel.getAbsYSpeed() - getAbsYSpeed()) * world.frictionFraction() * world.view().diff();
                rel.pushBackY(-frictionMomentum);
                pushBackY(frictionMomentum);
                float momentum = rel.getMass() * xSpeed;
                pushBackX(momentum);
                rel.pushBackX(-momentum);
            }
            if (rel.collidedWithImmobileX()) {
                collidedWithImmobileX(true);
                collisionLockX(-fixMove);
                x -= fixMove;
                return 0;
            }
            if ((rel.collisionLockX() < 0 && fixMove > 0) ||
                (rel.collisionLockX() > 0 && fixMove < 0)) {
                collisionLockX(-fixMove);
                x -= fixMove;
                return 0;
            }
        }

        return fixMove;
    }

    protected float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = (RelativeMovable) rect;
            if (first) {
                float frictionMomentum = rel.getMass() * (rel.getAbsXSpeed() - getAbsXSpeed()) * world.frictionFraction() * world.view().diff();
                rel.pushBackX(-frictionMomentum);
                if (!(rect instanceof Player) || !collidedWithImmobileY())
                    pushBackX(frictionMomentum);
                float momentum = rel.getMass() * ySpeed;
                pushBackY(momentum);
                rel.pushBackY(-momentum);
            }
            if (rel.collidedWithImmobileY()) {
                collidedWithImmobileY(true);
                collisionLockY(-fixMove);
                y -= fixMove;
                return 0;
            }
            if ((rel.collisionLockY() < 0 && fixMove > 0) ||
                (rel.collisionLockY() > 0 && fixMove < 0)) {
                    collisionLockY(-fixMove);
                    y -= fixMove;
                    return 0;
                }
        }

        return fixMove;
    }

    public void pushBackX(float momentum) {
        xSpeed += momentum / mass;
    }

    public void pushBackY(float momentum) {
        ySpeed += momentum / mass;
    }

    public void update(GameContainer gc, int diff) {
        super.update(gc, diff);

        pushBackX((float) (-xSpeed * world.airResist() * Math.pow(botY   - topY,  0.5)));
        pushBackY((float) (-ySpeed * world.airResist() * Math.pow(rightX - leftX, 0.5)));
    }

    public Block tile(int x, int y) {
        return tiles[x][y];
    }
    private void tile(int x, int y, Block val) {
        tiles[x][y] = val;
    }

    public int WIDTH () { return VEHICLE_WIDTH;  }
    public int HEIGHT() { return VEHICLE_HEIGHT; }

    protected void updateData(String id, String  data) {
        if (id.startsWith("tile.")) {
            Scanner s = new Scanner(id.substring(5));
            s.useDelimiter("\\.");
            Block tile = tile(s.nextInt(), s.nextInt());
            if (tile != null)
                tile.updateData(s.nextLine().substring(1), data);
        }
    }
    protected void updateBoolean(String id, boolean data) {
        if (id.startsWith("tile.") || id.startsWith("make.") || id.startsWith("dele.")) {
            Scanner s = new Scanner(id.substring(5));
            s.useDelimiter("\\.");
            if (id.startsWith("tile.")) {
                Block tile = tile(s.nextInt(), s.nextInt());
                if (tile != null)
                    tile.updateBoolean(s.nextLine().substring(1), data);
            } else if (id.startsWith("make.")){
                this.addTile(inv.getBlockAt(s.nextInt()).create(s.nextInt(), s.nextInt(), s.nextInt()));
            } else {
                Block tile = tile(s.nextInt(), s.nextInt());
                if (tile != null)
                    this.remTile(tile);
            }
        }
    }
    protected void updateInt(String id, int data) {
        if (id.startsWith("tile.")) {
            Scanner s = new Scanner(id.substring(5));
            s.useDelimiter("\\.");
            Block tile = tile(s.nextInt(), s.nextInt());
            if (tile != null)
                tile.updateInt(s.nextLine().substring(1), data);
        }
    }
    protected void updateFloat(String id, float data) {
        switch (id) {
            case "mass":
                mass = data;
                break;
            case "x":
                toSetX = data;
                toSetXb = true;
                break;
            case "y":
                toSetY = data;
                toSetYb = true;
                break;
            case "xSpeed":
                toSetXSpeed = data;
                toSetXSpe = true;
                break;
            case "ySpeed":
                toSetYSpeed = data;
                toSetYSpe = true;
                break;
            default:
                if (id.startsWith("tile.")) {
                    Scanner s = new Scanner(id.substring(5));
                    s.useDelimiter("\\.");
                    Block tile = tile(s.nextInt(), s.nextInt());
                    if (tile != null)
                        tile.updateFloat(s.nextLine().substring(1), data);
                } else
                    System.out.println("Unsupported variable in Vehicle: " +id);
        }
    }

}
