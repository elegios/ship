/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world.collisiongrid;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import ship.world.Position;
import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.World;
import ship.world.collisiongrid.island.Island;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.easy.EasyNode;

/**
 *
 * @author elegios
 */
public abstract class CollisionGrid implements Position, Renderable, Updatable, RelativeMovable, Rectangle, ChangeListener {
    private int id;

    private String name;

    public static final int TW = 32;
    public static final int TH = 32;

    protected float mass;

    protected float x;
    protected float y;
    protected float toSetX;
    protected boolean toSetXb;
    protected float toSetY;
    protected boolean toSetYb;

    protected float xSpeed;
    protected float ySpeed;
    protected float toSetXSpeed;
    protected boolean toSetXSpe;
    protected float toSetYSpeed;
    protected boolean toSetYSpe;

    private boolean collidedWithImmobileX;
    private boolean collidedWithImmobileY;
    private float   collisionLockX;
    private float   collisionLockY;

    private boolean[][] collidesAt;

    protected World world;

    protected EasyNode    node;

    protected ManagedSpriteSheet tileset;

    public CollisionGrid(World world, int id, int x, int y, boolean centerInit, String name) throws SlickException {
        this.world = world;
        this.id    = id;
        this.name  = name;

        node = world.view().node();

        tileset = world.view().loader().loadManagedSpriteSheet("tiles", TW, TH);

        node.addChangeListener(this);

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
        c("xSpeed", 0.0f);
        c("ySpeed", 0.0f);
        c("mass", 0.0f);
    }

    /**
     * For collision detection, return the tile at (x, y), as a Rectangle
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @return the tile as a Rectangle
     */
    protected abstract Rectangle getRectAt    (int x, int y);
    /**
     * Check whether the tile at (x, y) wants to be rendered
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @return true if the tile should be rendered.
     */
    protected abstract boolean   renderAt     (int x, int y);
    /**
     * Checks if the tile at (x, y) exists
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @return true if the tile exists
     */
    public    abstract boolean   existsAt     (int x, int y);
    /**
     * Get a number representing a tile from the CollisionGrid SpriteSheet, that will be used
     * to render the tile at (x, y)
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @return an int representing a sprite from the CollisionGrid SpriteSheet
     */
    protected abstract int       tileAt       (int x, int y);
    /**
     * Call the update method of the tile at (x, y)
     * @param x the internal x coordinate of the tile
     * @param y the internal y coordinate of the tile
     * @param gc the GameContainer in which the current game exists
     * @param diff the number of milliseconds since the last frame
     */
    protected abstract void      updateAt     (int x, int y, GameContainer gc, int diff);

    /**
     * Returns the internal x coordinate of the left-most tile in the current
     * CollisionGrid.
     * @return left-most internal coordinate in use
     */
    protected abstract int leftX();
    /**
     * Returns the internal x coordinate of the right-most tile in the current
     * CollisionGrid.
     * @return right-most internal coordinate in use
     */
    protected abstract int rightX();
    /**
     * Returns the internal y coordinate of the top-most tile in the current
     * CollisionGrid.
     * @return top-most internal coordinate in use
     */
    protected abstract int topY();
    /**
     * Returns the internal y coordinate of the bottom-most tile in the current
     * CollisionGrid.
     * @return bottom-most internal coordinate in use
     */
    protected abstract int botY();

    protected abstract void updateData   (String id, String  data);
    protected abstract void updateInt    (String id, int     data);
    protected abstract void updateBoolean(String id, boolean data);
    protected abstract void updateFloat  (String id, float   data);

    /**
     * Returns the maximum number of tiles the current CollisionGrid can
     * handle, horizontally.
     * @return width in tiles
     */
    public abstract int WIDTH();
    /**
     * Returns the maximum number of tiles the current CollisionGrid can
     * handle, vertically.
     * @return height in tiles
     */
    public abstract int HEIGHT();

    public abstract void pushX(float momentum);
    public abstract void pushY(float momentum);

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
    protected abstract float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove, boolean first);
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
    protected abstract float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove, boolean first);

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
    public boolean collideWithCollisionGridX(CollisionGrid other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        if (other.overlaps(this) || this.overlaps(other))
            for (int i = leftX(); i <= rightX(); i++)
                for (int j = topY(); j <= botY(); j++)
                    if (collidesAt[i][j]) {
                        float fixMove = other.collideRectangleX(getRectAt(i, j), getAbsXSpeed() - other.getAbsXSpeed());
                        if (fixMove != 0) {
                            if ((!collidedWithImmobileX &&
                                 ((fixMove < 0 && collisionLockX < 0) ||
                                  (fixMove > 0 && collisionLockX > 0) ||
                                  collisionLockX == 0)) ||
                                  other instanceof Island) {
                                x += fixMove;
                                collisionLockX = fixMove;
                                hasCollided = true;
                            }
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
    public boolean collideWithCollisionGridY(CollisionGrid other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        if (other.overlaps(this) || this.overlaps(other))
            for (int i = leftX(); i <= rightX(); i++)
                for (int j = topY(); j <= botY(); j++)
                    if (collidesAt[i][j]) {
                        float fixMove = other.collideRectangleY(getRectAt(i, j), getAbsYSpeed() - other.getAbsYSpeed());
                        if (fixMove != 0) {
                            if ((!collidedWithImmobileY &&
                                 ((fixMove < 0 && collisionLockY < 0) ||
                                  (fixMove > 0 && collisionLockY > 0) ||
                                  collisionLockY == 0)) ||
                                  other instanceof Island) {
                                y += fixMove;
                                collisionLockY = fixMove;
                                hasCollided = true;
                            }
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
            float fixMove = getX() - rect.getX() - xMod - rect.getWidth() + i2*TW - 0.002f;  //The weird order fixes a bug, apparently floats lose precision or something otherwise
            if (fixMove > 0.002f)
                return fixMove;
            fixMove = pushBackAndFixMoveX(rect, xSpeed, fixMove, first);
            return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod, false);
        }
        int i1  = (int) (rect.getX() + xMod - getX())/TW;
        if (collides(i1, j1) || collides(i1, j2)) {
            float fixMove = getX() - rect.getX() - xMod + i1*TW + TW + 0.002f;
            if (fixMove < 0.002f)
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
    public  float collideRectangleY(Rectangle rect, float ySpeed) { return collideRectangleY(rect, ySpeed, 0, true); }
    private float collideRectangleY(Rectangle rect, float ySpeed, float yMod, boolean first) {
        int i1 = (int)          (rect.getX()         - getX())/TW;
        int i2 = (int) Math.ceil(rect.getX2()        - getX())/TW;
        int j2 = (int) Math.ceil(rect.getY2() + yMod - getY())/TH;
        if (collides(i1, j2) || collides(i2, j2)) {
            float fixMove = getY() - rect.getY() - yMod - rect.getHeight() + j2*TH - 0.002f;
            if (fixMove > 0.002f)
                return fixMove;
            fixMove = pushBackAndFixMoveY(rect, ySpeed, fixMove, first);
            return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod, false);
        }
        int j1 = (int) (rect.getY() + yMod - getY())/TH;
        if (collides(i1, j1) || collides(i2, j1)) {
            float fixMove = getY() - rect.getY() - yMod + j1*TH + TH + 0.002f;
            if (fixMove < 0.002f)
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
                if (renderAt(i, j))
                    tileset.getSpriteSheet().renderInUse(ix() + i*TW,
                                                         iy() + j*TH,
                                                         tileAt(i, j)%tileset.getSpriteSheet().getHorizontalCount(),
                                                         tileAt(i, j)/tileset.getSpriteSheet().getHorizontalCount());

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

        if (toSetXb) {
            x = toSetX;
            toSetXb = false;
        }
        if (toSetXSpe) {
            xSpeed = toSetXSpeed;
            toSetXSpe = false;
        }

        x += getAbsXMove(diff);
    }
    public void moveY(int diff) {
        collidedWithImmobileY = false;
        collisionLockY = 0;

        if (toSetYb) {
            y = toSetY;
            toSetYb = false;
        }
        if (toSetYSpe) {
            ySpeed = toSetYSpeed;
            toSetYSpe = false;
        }

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
        for (int i = leftX(); i <= rightX(); i++)
            for (int j = topY(); j <= botY(); j++)
                updateAt(i, j, gc, diff);

        ySpeed += world.actionsPerTick() * diff * world.gravity();

        if (world.updatePos() && world.view().playerId() == 0) {
            c("x", x);
            c("y", y);
            c("xSpeed", xSpeed);
            c("ySpeed", ySpeed);
        }
    }

    public int getID() { return id; }

    public final void c(String id, Object data) { node.c(name+ "." +this.id+ "." +id, data); }

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
