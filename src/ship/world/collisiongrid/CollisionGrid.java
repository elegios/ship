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

    protected abstract Rectangle getRectAt (int x, int y);
    public    abstract boolean   collidesAt(int x, int y);
    protected abstract boolean   renderAt  (int x, int y);
    public    abstract boolean   existsAt  (int x, int y);
    protected abstract int       tileAt    (int x, int y);
    protected abstract void      updateAt  (int x, int y, GameContainer gc, int diff);

    protected abstract void updateData   (String id, String  data);
    protected abstract void updateInt    (String id, int     data);
    protected abstract void updateBoolean(String id, boolean data);
    protected abstract void updateFloat  (String id, float   data);

    public abstract int WIDTH();
    public abstract int HEIGHT();

    public abstract void pushBackX(float momentum);
    public abstract void pushBackY(float momentum);

    protected abstract float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove, boolean first);
    protected abstract float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove, boolean first);

    public World world() { return world; }

    public boolean collideWithCollisionGridX(CollisionGrid other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        if (other.overlaps(this) || this.overlaps(other))
            for (int i = 0; i < WIDTH(); i++)
                for (int j = 0; j < HEIGHT(); j++)
                    if (collidesAt(i, j)) {
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

    public boolean collideWithCollisionGridY(CollisionGrid other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        if (other.overlaps(this) || this.overlaps(other))
            for (int i = 0; i < WIDTH(); i++)
                for (int j = 0; j < HEIGHT(); j++)
                    if (collidesAt(i, j)) {
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

    public float collideRectangleX(Rectangle rect, float xSpeed) { return collideRectangleX(rect, xSpeed, 0); }
    public float collideRectangleX(Rectangle rect, float xSpeed, float xMod) {
            int i2 = (int) Math.ceil(rect.getX2() + xMod - getX())/TW;
            int j1 = (int)          (rect.getY()         - getY())/TH;
            int j2 = (int) Math.ceil(rect.getY2()        - getY())/TH;
            if (collides(i2, j1) || collides(i2, j2)) {
                float fixMove = getX() + i2*TW - rect.getX() - xMod - rect.getWidth() - 0.002f;
                if (fixMove == 0)
                    return 0;
                fixMove = pushBackAndFixMoveX(rect, xSpeed, fixMove, xMod == 0);
                return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod);
            }
            int i1  = (int) (rect.getX() + xMod - getX())/TW;
            if (collides(i1, j1) || collides(i1, j2)) {
                float fixMove = getX() + i1*TW + TW - rect.getX() - xMod + 0.002f;
                if (fixMove == 0)
                    return 0;
                fixMove = pushBackAndFixMoveX(rect, xSpeed, fixMove, xMod == 0);
                return fixMove + collideRectangleX(rect, xSpeed, fixMove + xMod);
            }
        return 0;
    }

    public float collideRectangleY(Rectangle rect, float ySpeed) { return collideRectangleY(rect, ySpeed, 0); }
    public float collideRectangleY(Rectangle rect, float ySpeed, float yMod) {
            int i1 = (int)          (rect.getX()         - getX())/TW;
            int i2 = (int) Math.ceil(rect.getX2()        - getX())/TW;
            int j2 = (int) Math.ceil(rect.getY2() + yMod - getY())/TH;
            if (collides(i1, j2) || collides(i2, j2)) {
                float fixMove = getY() + j2*TH - rect.getY() - yMod - rect.getHeight() - 0.002f;
                if (fixMove == 0)
                    return 0;
                fixMove = pushBackAndFixMoveY(rect, ySpeed, fixMove, yMod == 0);
                return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod);
            }
            int j1 = (int) (rect.getY() + yMod - getY())/TH;
            if (collides(i1, j1) || collides(i2, j1)) {
                float fixMove = getY() + j1*TH + TH - rect.getY() - yMod + 0.002f;
                if (fixMove == 0)
                    return 0;
                fixMove = pushBackAndFixMoveY(rect, ySpeed, fixMove, yMod == 0);
                return fixMove + collideRectangleY(rect, ySpeed, fixMove + yMod);
            }
        return 0;
    }

    private boolean collides(int x, int y) {
        if (0 <= x && x < WIDTH() &&
            0 <= y && y < HEIGHT() &&
            collidesAt(x, y))
            return true;
        return false;
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        float xMax = (View.window().getWidth()  - getX() - world.getX())/TW;
        float xMin = (                    - TW  - getX() - world.getX())/TW;
        float yMax = (View.window().getHeight() - getY() - world.getY())/TH;
        float yMin = (                     - TH - getY() - world.getY())/TH;
        for (int i = 0; i < WIDTH(); i++)
            for (int j = 0; j < HEIGHT(); j++)
                if (renderAt(i, j) &&
                    i <= xMax && i >= xMin &&
                    j <= yMax && j >= yMin)
                    tileset.getSpriteSheet().renderInUse(ix() + i*TW,
                                                         iy() + j*TH,
                                                         tileAt(i, j)%tileset.getSpriteSheet().getHorizontalCount(),
                                                         tileAt(i, j)/tileset.getSpriteSheet().getHorizontalCount());

    }

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

    public int getTileXUnderPos(float x) { return (int) (x - getX())/TW; }
    public int getTileYUnderPos(float y) { return (int) (y - getY())/TH; }

    @Override
    public void update(GameContainer gc, int diff) {
        for (int i = 0; i < WIDTH(); i++)
            for (int j = 0; j < HEIGHT(); j++)
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
