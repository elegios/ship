package ship.world.vehicle;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import ship.netcode.ShipProtocol;
import ship.netcode.interaction.CreateTilePackage;
import ship.netcode.interaction.DeleteTilePackage;
import ship.netcode.movement.VehiclePositionPackage;
import ship.world.Position;
import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.World;
import ship.world.player.Player;
import ship.world.vehicle.tile.Thruster;
import ship.world.vehicle.tile.Tile;
import ship.world.vehicle.tile.fuel.AirFuelTransport;
import ship.world.vehicle.tile.fuel.FuelTank;
import ship.world.vehicle.tile.fuel.FuelTransport;
import ship.world.vehicle.tile.power.AirPowerTransport;
import ship.world.vehicle.tile.power.MomentumAbsorber;
import ship.world.vehicle.tile.power.PowerSwitch;
import ship.world.vehicle.tile.power.PowerTransport;

/**
 *
 * @author elegios
 */
public class Vehicle implements Position, Renderable, Updatable, RelativeMovable, Rectangle {
    public static final float EXTRA_MOVE = 0.0025f;

    public static final float SPEED_THRESHOLD = 1f;

    public static final int VEH_WIDTH  = 65;
    public static final int VEH_HEIGHT = 65;

    private int id;

    public static final int TW = 32;
    public static final int TH = 32;

    protected float mass;

    protected float x;
    protected float y;

    protected float xSpeed;
    protected float ySpeed;

    private VehiclePositionPackage toUpdatePos;
    private CreateTilePackage toCreate;
    private DeleteTilePackage toDelete;

    private boolean collidedWithImmobileX;
    private boolean collidedWithImmobileY;
    private float   collisionLockX;
    private float   collisionLockY;

    private boolean[][] collidesAt;

    protected World world;

    protected ManagedSpriteSheet tileset;

    private Tile[][] tiles;

    private int leftX;
    private int rightX;
    private int topY;
    private int botY;

    public Vehicle(World world, int id, int x, int y) throws SlickException {
        this(world, id, x, y, true);
    }
    public Vehicle(World world, int id, int x, int y, boolean centerInit) throws SlickException {
        this.world = world;
        this.id    = id;

        tileset = world.view().loader().loadManagedSpriteSheet("tiles", TW, TH);

        tiles = new Tile[WIDTH()][HEIGHT()];

        leftX  = WIDTH()/2;
        rightX = leftX;

        topY   = HEIGHT()/2;
        botY   = topY;

        collidesAt = new boolean[WIDTH()][HEIGHT()];
        for (int i = 0; i < collidesAt.length; i++)
            for (int j = 0; j < collidesAt[0].length; j++)
                collidesAt[i][j] = false;

        if (centerInit) {
            this.x = x - getWidth()/2;
            this.y = y - getHeight()/2;
        } else {
            this.x = x;
            this.y = y;
        }
        xSpeed = 0.0f;
        ySpeed = 0.0f;
        mass   = 0.0f;
    }

    protected boolean updateMass() { return true; }

    /**
     * Adds a given Block to the Vehicle, adding its mass to the total and checking whether
     * the Vehicle has grown bigger in any direction.
     * @param tile the Block
     */
    public final void addTile(Tile tile) {
        if (updateMass())
            mass += tile.mass();

        tiles[tile.x()][tile.y()] = tile;
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

    public final void remTile(Tile tile) {
        if (tile != null) {
            if (updateMass())
                mass -= tile.mass();

            tiles[tile.x()][tile.y()] = null;
            setCollidesAt(tile.x(), tile.y(), false);

            if (tile.x() == leftX) {
                for (int i = leftX; i <= rightX; i++)
                    for (int j = topY; j <= botY; j++)
                        if (tiles[i][j] != null) {
                            leftX = i;
                            i = rightX + 1; //To make the outer loop break;
                            break;
                        }
            } else if (tile.x() == rightX) {
                for (int i = rightX; i >= leftX; i--)
                    for (int j = topY; j <= botY; j++)
                        if (tiles[i][j] != null) {
                            rightX = i;
                            i = leftX - 1; //To make the outer loop break;
                            break;
                        }
            }

            if (tile.y() == topY) {
                for (int j = topY; j <= botY; j++)
                    for (int i = leftX; i <= rightX; i++)
                        if (tiles[i][j] != null) {
                            topY = j;
                            j = botY + 1; //To make the outer loop break;
                            break;
                        }
            } else if (tile.y() == botY) {
                for (int j = botY; j >= topY; j--)
                    for (int i = leftX; i <= rightX; i++)
                        if (tiles[i][j] != null) {
                            botY = j;
                            j = topY - 1; //To make the outer loop break;
                            break;
                        }
            }

            if (leftX == rightX && topY == botY && tiles[leftX][topY] == null)
                world.removeVehicleFromList(this);
        } else {
            System.out.println("Tried to remove a null tile, not sure why");
        }
    }

    /**
     * Returns the internal x coordinate of the left-most tile in the current
     * CollisionGrid.
     * @return left-most internal coordinate in use
     */
    public int leftX() { return leftX; }
    /**
     * Returns the internal x coordinate of the right-most tile in the current
     * CollisionGrid.
     * @return right-most internal coordinate in use
     */
    public int rightX() { return rightX; }
    /**
     * Returns the internal y coordinate of the top-most tile in the current
     * CollisionGrid.
     * @return top-most internal coordinate in use
     */
    public int topY() { return topY; }
    /**
     * Returns the internal y coordinate of the bottom-most tile in the current
     * CollisionGrid.
     * @return bottom-most internal coordinate in use
     */
    public int botY() { return botY; }

    /**
     * Returns the maximum number of tiles the current CollisionGrid can
     * handle, horizontally.
     * @return width in tiles
     */
    public int WIDTH() { return VEH_WIDTH; }
    /**
     * Returns the maximum number of tiles the current CollisionGrid can
     * handle, vertically.
     * @return height in tiles
     */
    public int HEIGHT() { return VEH_HEIGHT; }

    /**
     * Checks whether there is a tile at (x, y)
     * @param x the internal x coordinate to be checked
     * @param y the internal y coordinate to be checked
     * @return true if (x, y) contains a tile
     */
    public boolean existsAt(int x, int y) { return tiles[x][y] != null; }

    /**
     * Returns the tile at (x, y), or null if none exists
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @return the tile at (x, y), or null
     */
    public Tile tile(int x, int y) { return tiles[x][y]; }

    public void pushX(float momentum) {
        xSpeed += momentum / mass;
    }
    public void pushY(float momentum) {
        ySpeed += momentum / mass;
    }

    /**
     * Method that is called at least once during every actual collision with another
     * Rectangle. The method receives various data, acts on it, and then returns a new
     * fixMove value, should the old one need to be changed. This is to enable the current
     * CollisionGrid to move even if it is the other one that normally should move, to
     * fix edge cases.
     * @param rect the Rectangle with which there has been a collision
     * @param xSpeed the relative speed at which the Rectangle travels
     * @param fixMove the previous fixMove value
     * @param first true if this is the first call to this method for the current collision
     * @return a new fixMove value, or the old one if it does not need to change
     */
    protected float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = (RelativeMovable) rect;
            if (first) {
                float frictionMomentum = rel.getMass() * (rel.getAbsYSpeed() - getAbsYSpeed()) * world.frictionFraction() * world.view().diff();
                rel.pushY(-frictionMomentum);
                pushY(frictionMomentum);
                float minMomentum = Math.min(rel.getMass(), getMass()) * xSpeed;
                pushX(minMomentum);
                rel.pushX(-minMomentum);
            }
            if (rel.collidedWithImmobileX()) {
                if ((rel.collisionLockX() < 0 && fixMove > 0) ||
                    (rel.collisionLockX() > 0 && fixMove < 0)) {
                    collidedWithImmobileX(true);
                    collisionLockX(-fixMove);
                    x -= fixMove;
                    return 0;
                }
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
    /**
     * Method that is called at least once during every actual collision with another
     * Rectangle. The method receives various data, acts on it, and then returns a new
     * fixMove value, should the old one need to be changed. This is to enable the current
     * CollisionGrid to move even if it is the other one that normally should move, to
     * fix edge cases.
     * @param rect the Rectangle with which there has been a collision
     * @param ySpeed the relative speed at which the Rectangle travels
     * @param fixMove the previous fixMove value
     * @param first true if this is the first call to this method for the current collision
     * @return a new fixMove value, or the old one if it does not need to change
     */
    protected float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable) {
            RelativeMovable rel = (RelativeMovable) rect;
            if (first) {
                float frictionMomentum = rel.getMass() * (rel.getAbsXSpeed() - getAbsXSpeed()) * world.frictionFraction() * world.view().diff();
                rel.pushX(-frictionMomentum);
                if (!(rect instanceof Player) || !collidedWithImmobileY())
                    pushX(frictionMomentum);
                float minMomentum = Math.min(rel.getMass(), getMass()) * ySpeed;
                pushY(minMomentum);
                rel.pushY(-minMomentum);
            }
            if (rel.collidedWithImmobileY()) {
                if ((rel.collisionLockY() < 0 && fixMove > 0) ||
                    (rel.collisionLockY() > 0 && fixMove < 0)) {
                    collidedWithImmobileY(true);
                    collisionLockY(-fixMove);
                    y -= fixMove;
                    return 0;
                }
            }
            if (!collidedWithImmobileY() &&
                ((rel.collisionLockY() < 0 && fixMove > 0) ||
                 (rel.collisionLockY() > 0 && fixMove < 0))) {
                collisionLockY(-fixMove);
                y -= fixMove;
                return 0;
            }
        }

        return fixMove;
    }

    /**
     * Check if the tile at (x, y) collides. Will throw an exception if (x, y) is
     * outside the CollisionGrid.
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @return true if the tile collides, false otherwise
     */
    public boolean collidesAt(int x, int y)            { return collidesAt[x][y]; }
    /**
     * Sets whether the tile at (x, y) collides or not. Will throw an exception if (x, y)
     * is outside the CollisionGrid
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @param collides whether the tile should collide or not
     */
    public void setCollidesAt(int x, int y, boolean collides) { collidesAt[x][y] = collides; }

    public World world() { return world; }

    /**
     * Checks for collision with <code>other</code>. If true, it
     * will primarily be this CollisionGrid that moves, with a few exceptions.
     * @param other the CollisionGrid to be checked for collision
     * @return true if collision has been detected, false otherwise
     */
    public boolean collideWithVehicleX(Vehicle other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        if (other.overlaps(this) || this.overlaps(other))
            for (int i = leftX(); i <= rightX(); i++)
                for (int j = topY(); j <= botY(); j++)
                    if (collidesAt[i][j]) {
                        float fixMove = other.collideRectangleX(tiles[i][j], getAbsXSpeed() - other.getAbsXSpeed());
                        if (fixMove != 0) {
                            x += fixMove;
                            collisionLockX = fixMove;
                            hasCollided = true;
                            if (other.collidedWithImmobileX())
                                hasCollidedWithImmobile = true;
                        }
                    }

        if (hasCollidedWithImmobile)
            collidedWithImmobileX = true;

        return hasCollided;
    }

    /**
     * Checks for collision with <code>other</code>. If true, it
     * will primarily be this CollisionGrid that moves, with a few exceptions.
     * @param other the CollisionGrid to be checked for collision
     * @return true if collision has been detected, false otherwise
     */
    public boolean collideWithVehicleY(Vehicle other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        if (other.overlaps(this) || this.overlaps(other))
            for (int i = leftX(); i <= rightX(); i++)
                for (int j = topY(); j <= botY(); j++)
                    if (collidesAt[i][j]) {
                        float fixMove = other.collideRectangleY(tiles[i][j], getAbsYSpeed() - other.getAbsYSpeed());
                        if (Math.abs(fixMove) >= EXTRA_MOVE) {
                            y += fixMove;
                            collisionLockY = fixMove;
                            hasCollided = true;
                            if (other.collidedWithImmobileY())
                                hasCollidedWithImmobile = true;
                        }
                    }

        if (hasCollidedWithImmobile)
            collidedWithImmobileY = true;

        return hasCollided;
    }

    /**
     * Checks for collision between this CollisionGrid and <code>rect</code>.
     * Returns the amount of pixels that <code>rect</code> needs to move to
     * be outside the CollisionGrid.
     *
     * This method will call pushBackAndFixMoveX at least once for every collision.
     * This is to fix edge-cases, when <code>rect</code> cannot be moved for one
     * reason or another. If that is the case, pushBackAndFixMoveX will probably move
     * the CollisionGrid.
     * @param rect the rectangle to be checked for collision
     * @param ySpeed the speed of <code>rect</code> relative to the CollisionGrid
     * @return the number of pixels <code>rect</code> needs to be moved.
     */
    public  float collideRectangleX(Rectangle rect, float xSpeed) { return collideRectangleX(rect, xSpeed, 0, true); }
    private float collideRectangleX(Rectangle rect, float xSpeed, float xMod, boolean first) {
        int i2 = (int) Math.ceil(rect.getX2() + xMod - getX())/TW;
        int j1 = (int)          (rect.getY()         - getY())/TH;
        int j2 = (int) Math.ceil(rect.getY2()        - getY())/TH;
        if (collides(i2, j1) || collides(i2, j2)) {
            float fixMove = getX() - rect.getX() - xMod - rect.getWidth() + i2*TW - EXTRA_MOVE;  //The weird order fixes a bug, apparently floats lose precision or something otherwise
            if (fixMove > -EXTRA_MOVE)
                return fixMove;
            fixMove = pushBackAndFixMoveX(rect, xSpeed, fixMove, first);
            return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod, false);
        }
        int i1  = (int) (rect.getX() + xMod - getX())/TW;
        if (collides(i1, j1) || collides(i1, j2)) {
            float fixMove = getX() - rect.getX() - xMod + i1*TW + TW + EXTRA_MOVE;
            if (fixMove < EXTRA_MOVE)
                return fixMove;
            fixMove = pushBackAndFixMoveX(rect, xSpeed, fixMove, first);
            return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod, false);
        }

        return 0;
    }

    /**
     * Checks for collision between this CollisionGrid and <code>rect</code>.
     * Returns the amount of pixels that <code>rect</code> needs to move to
     * be outside the CollisionGrid.
     *
     * This method will call pushBackAndFixMoveY at least once for every collision.
     * This is to fix edge-cases, when <code>rect</code> cannot be moved for one
     * reason or another. If that is the case, pushBackAndFixMoveY will probably move
     * the CollisionGrid.
     * @param rect the rectangle to be checked for collision
     * @param ySpeed the speed of <code>rect</code> relative to the CollisionGrid
     * @return the number of pixels <code>rect</code> needs to be moved.
     */
    public  float collideRectangleY(Rectangle rect, float ySpeed) { return collideRectangleY(rect, ySpeed, 0, true); } //TODO: refactor as loop with max number of loops
    private float collideRectangleY(Rectangle rect, float ySpeed, float yMod, boolean first) {
        int i1 = (int)          (rect.getX()         - getX())/TW;
        int i2 = (int) Math.ceil(rect.getX2()        - getX())/TW;
        int j2 = (int) Math.ceil(rect.getY2() + yMod - getY())/TH;
        if (collides(i1, j2) || collides(i2, j2)) {
            float fixMove = getY() - rect.getY() - yMod - rect.getHeight() + j2*TH - EXTRA_MOVE;
            if (fixMove > -EXTRA_MOVE)
                return fixMove;
            fixMove = pushBackAndFixMoveY(rect, ySpeed, fixMove, first);
            return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod, false);
        }
        int j1 = (int) (rect.getY() + yMod - getY())/TH;
        if (collides(i1, j1) || collides(i2, j1)) {
            float fixMove = getY() - rect.getY() - yMod + j1*TH + TH + EXTRA_MOVE;
            if (fixMove < EXTRA_MOVE)
                return fixMove;
            fixMove = pushBackAndFixMoveY(rect, ySpeed, fixMove, first);
            return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod, false);
        }

        return 0;
    }

    /**
     * Checks if the tile at (x, y) collides. This is merely a wrapper of
     * collidesAt(x, y), with the exception that this method will return
     * false if (x, y) is outside the CollisionGrid, instead of throwing an
     * exception
     * @param x the x coordinate of the tile to be checked
     * @param y the y coordinate of the tile to be checked
     * @return true if (x, y) is within the CollisionGrid and collides, false otherwise
     */
    private boolean collides(int x, int y) {
        if (0 <= x && x < WIDTH() &&
            0 <= y && y < HEIGHT())
            return collidesAt[x][y];
        return false;
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        int xMax = (int) Math.min((View.window().getWidth()  - getX() - world.getX())/TW, WIDTH () - 1);
        int xMin = (int) Math.max((                    - TW  - getX() - world.getX())/TW, 0);
        int yMax = (int) Math.min((View.window().getHeight() - getY() - world.getY())/TH, HEIGHT() - 1);
        int yMin = (int) Math.max((                     - TH - getY() - world.getY())/TH, 0);

        if (xMax < 0 || xMin >= WIDTH() ||
            yMax < 0 || yMin >= HEIGHT())
            return;

        for (int i = xMin; i <= xMax; i++)
            for (int j = yMin; j <= yMax; j++)
                if (tiles[i][j] != null) {
                    int tile = tiles[i][j].tile();
                    tileset.getSpriteSheet().renderInUse(ix() + i*TW,
                                                         iy() + j*TH,
                                                         tile%tileset.getSpriteSheet().getHorizontalCount(),
                                                         tile/tileset.getSpriteSheet().getHorizontalCount());
                }

    }

    /**
     * Checks if <code>rect</code> overlaps the current CollisionGrid.
     * This only checks if any of the four corners of <code>rect</code>
     * is within the CollisionGrid, so if the CollisionGrid is entirely
     * within <code>rect</code> false is returned.
     * @param rect the Rectangle to be checked for overlap
     * @return true if any corner of <code>rect</code> overlaps
     */
    public boolean overlaps(Rectangle rect) {
        if (((rect.getX()  >= getX() && rect.getX()  <= getX2()) ||
             (rect.getX2() >= getX() && rect.getX2() <= getX2()))
            &&
            ((rect.getY()  >= getY() && rect.getY()  <= getY2()) ||
             (rect.getY2() >= getY() && rect.getY2() <= getY2())))
            return true;
        return false;
    }

    public void moveX(int diff) {
        collidedWithImmobileX = false;
        collisionLockX = 0;

        if (toUpdatePos != null) {
            toUpdatePos.xChecked(true);

            world.relMoveX(this, toUpdatePos.getX() - x);

            x      = toUpdatePos.getX();
            xSpeed = toUpdatePos.getXSpeed();
        }

        float absSpeed = getAbsXSpeed();
        if (absSpeed > SPEED_THRESHOLD || absSpeed < -SPEED_THRESHOLD)
            x += getAbsXMove(diff);
    }
    public void moveY(int diff) {
        collidedWithImmobileY = false;
        collisionLockY = 0;

        if (toUpdatePos != null && toUpdatePos.xChecked()) {
            world.relMoveY(this, toUpdatePos.getY() - y);

            y      = toUpdatePos.getY();
            ySpeed = toUpdatePos.getYSpeed();

            toUpdatePos = null;
        }

        float absSpeed = getAbsYSpeed();
        if (absSpeed > SPEED_THRESHOLD || absSpeed < -SPEED_THRESHOLD)
            y += getAbsYMove(diff);
    }

    public boolean collidedWithImmobileX() { return collidedWithImmobileX; }
    public boolean collidedWithImmobileY() { return collidedWithImmobileY; }
    public float   collisionLockX()        { return collisionLockX; }
    public float   collisionLockY()        { return collisionLockY; }

    public void collidedWithImmobileX(boolean val) { collidedWithImmobileX = val; }
    public void collidedWithImmobileY(boolean val) { collidedWithImmobileY = val; }
    public void collisionLockX       (float   val) { collisionLockX        = val; }
    public void collisionLockY       (float   val) { collisionLockY        = val; }

    /**
     * Gets the internal x coordinate of the tile that would be underneath the
     * given global x coordinate. Will return an answer even if the tile would
     * be outside the CollisionGrid, so be careful with the values returned from here
     * @param x a global x-coordinate
     * @return the internal x-coordinate of the tile
     */
    public int getTileXUnderPos(float x) { return (int) (x - getX())/TW; }
    /**
     * Gets the internal y coordinate of the tile that would be underneath the
     * given global y coordinate. Will return an answer even if the tile would
     * be outside the CollisionGrid, so be careful with the values returned from here
     * @param y a global y-coordinate
     * @return the internal y-coordinate of the tile
     */
    public int getTileYUnderPos(float y) { return (int) (y - getY())/TH; }

    @Override
    public void update(GameContainer gc, int diff) {
        if (toCreate != null) {
            addTile(world.view().inventory().getBlockAt(toCreate.getItem()).create(toCreate.getSubItem(), toCreate.getVehX(), toCreate.getVehY()));
            toCreate = null;
        }

        if (toDelete != null) {
            remTile(tiles[toDelete.getVehX()][toDelete.getVehY()]);
            toDelete = null;
        }

        for (int i = leftX; i <= rightX; i++)
            for (int j = topY; j <= botY; j++)
                if (tiles[i][j] != null)
                    tiles[i][j].updateEarly(gc, diff);

        ySpeed += world.actionsPerTick() * diff * world.gravity();

        for (int i = leftX(); i <= rightX(); i++)
            for (int j = topY(); j <= botY(); j++)
                if (tiles[i][j] != null)
                    tiles[i][j].update(gc, diff);

        if (world.updatePos() && world.view().net().isServer()) {
            world.view().net().send(ShipProtocol.VEHICLE_POS, new VehiclePositionPackage(id, x, y, xSpeed, ySpeed));
        }

        pushX((float) (-xSpeed * world.airResist() * Math.pow(botY   - topY,  0.5))); //TODO: airresist from other vehicles
        pushY((float) (-ySpeed * world.airResist() * Math.pow(rightX - leftX, 0.5)));
    }

    public int getID() { return id; }

    public float getAbsXSpeed() { return xSpeed; }
    public float getAbsYSpeed() { return ySpeed; }

    public float getAbsXMove(int diff) { return getAbsXSpeed() * world.actionsPerTick() * diff; }
    public float getAbsYMove(int diff) { return getAbsYSpeed() * world.actionsPerTick() * diff; }

    public float getMass() { return mass; }

    public final int getWidth()  { return  WIDTH()*TW; }
    public final int getHeight() { return HEIGHT()*TH; }

    public float getX2() { return getX() +  getWidth() - 1; }
    public float getY2() { return getY() + getHeight() - 1; }

    public float getX() { return x; }
    public float getY() { return y; }

    public int ix() { return Math.round(world.getX() + getX()); }
    public int iy() { return Math.round(world.getY() + getY()); }

    /*
     * TODO: add receiving mechanisms for the following data:
     * - tile creation and deletion
     * - tile activation
     * - x, y, xSpeed, ySpeed (set toUpdatePos after checking !toUpdatePos.xChecked())
     * - tile data (it might be best to have this in the tiles themselves, we'll see)
     */

    public void receiveVehiclePositionPackage(VehiclePositionPackage pack) {
        if (toUpdatePos == null || !toUpdatePos.xChecked())
            toUpdatePos = pack;
    }

    public void receiveCreateTilePackage(CreateTilePackage pack) { //TODO: fix bug when several delete/create packages are received between one loop
        toCreate = pack;
    }

    public void receiveDeleteTilePackage(DeleteTilePackage pack) {
        toDelete = pack;
    }

    public void generateStandardVehicle() {
        int mx = WIDTH ()/2;
        int my = HEIGHT()/2;

        if (id > 4) {

            addTile(new PowerSwitch(mx - 1, my, Tile.RIGHT));

            addTile(new AirFuelTransport(mx - 2, my - 1, false, Tile.LEFT));
            addTile(new FuelTank(mx - 2, my));
            ((FuelTank) tiles[mx - 2][my]).setContent(FuelTank.MAX_CONTENT);
            addTile(new FuelTransport(mx - 2, my + 1, false, Tile.UP));

            addTile(new AirFuelTransport(mx - 3, my - 1, false, Tile.DOWN));
            addTile(new Thruster(mx - 3, my, Tile.RIGHT));
            addTile(new FuelTransport(mx - 3, my + 1, false, Tile.RIGHT));


            addTile(new PowerSwitch(mx, my, Tile.UP));

            addTile(new FuelTransport(mx - 1, my + 1, false, Tile.DOWN));
            addTile(new FuelTank(mx, my + 1));
            ((FuelTank) tiles[mx][my + 1]).setContent(FuelTank.MAX_CONTENT);
            addTile(new FuelTransport(mx + 1, my + 1, false, Tile.LEFT));

            addTile(new AirFuelTransport(mx - 1, my + 2, false, Tile.RIGHT));
            addTile(new Thruster(mx, my + 2, Tile.UP));
            addTile(new AirFuelTransport(mx + 1, my + 2, false, Tile.UP));


            addTile(new PowerSwitch(mx + 1, my, Tile.LEFT));

            addTile(new AirFuelTransport(mx + 2, my - 1, false, Tile.DOWN));
            addTile(new FuelTank(mx + 2, my));
            ((FuelTank) tiles[mx + 2][my]).setContent(FuelTank.MAX_CONTENT);
            addTile(new FuelTransport(mx + 2, my + 1, false, Tile.RIGHT));

            addTile(new AirFuelTransport(mx + 3, my - 1, false, Tile.LEFT));
            addTile(new Thruster(mx + 3, my, Tile.LEFT));
            addTile(new FuelTransport(mx + 3, my + 1, false, Tile.UP));


            addTile(new Tile(mx - 4, my + 1, 1, 5, true));
            addTile(new Tile(mx - 5, my + 2, 1, 5, true));

            addTile(new Tile(mx + 4, my + 1, 1, 5, true));
            addTile(new Tile(mx + 5, my + 2, 1, 5, true));

            addTile(new Tile(mx - 1, my - 2, 1, 5, true));
            addTile(new Tile(mx    , my - 2, 1, 5, true));
            addTile(new Tile(mx + 1, my - 2, 1, 5, true));

        } else {
            addTile(new PowerSwitch(mx, my, Tile.UP));

            addTile(new FuelTransport(mx - 1, my + 1, false, Tile.DOWN));
            addTile(new FuelTank(mx, my + 1));
            ((FuelTank) tiles[mx][my + 1]).setContent(FuelTank.MAX_CONTENT);

            addTile(new AirFuelTransport(mx - 1, my + 2, false, Tile.RIGHT));
            addTile(new Thruster(mx, my + 2, Tile.UP));

            addTile(new AirPowerTransport(mx - 4, my - 2, Tile.DOWN + 2));
            addTile(new AirPowerTransport(mx - 3, my - 2, Tile.DOWN + 6));
            addTile(new AirPowerTransport(mx - 2, my - 2, Tile.LEFT + 2));

            addTile(new AirPowerTransport(mx - 4, my - 1, Tile.RIGHT + 2));
            addTile(new MomentumAbsorber(mx - 3, my - 1, Tile.DOWN));
            addTile(new AirPowerTransport(mx - 2, my - 1, Tile.LEFT + 6));

            addTile(new MomentumAbsorber(mx - 3, my, Tile.LEFT + 1));
            addTile(new AirPowerTransport(mx - 2, my, Tile.RIGHT + 6));
            addTile(new PowerSwitch(mx - 1, my, Tile.RIGHT));

            addTile(new PowerTransport(mx - 4, my + 1, Tile.DOWN + 2));
            addTile(new MomentumAbsorber(mx - 3, my + 1, Tile.UP));
            addTile(new PowerTransport(mx - 2, my + 1, Tile.LEFT + 6));

            addTile(new PowerTransport(mx - 4, my + 2, Tile.RIGHT + 2));
            addTile(new PowerTransport(mx - 3, my + 2, Tile.UP + 6));
            addTile(new PowerTransport(mx - 2, my + 2, Tile.UP + 2));
        }
    }

}
