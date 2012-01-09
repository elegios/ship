/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world;

import media.ManagedImage;
import media.MediaLoader;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import ship.KeyReceiver;
import ship.Updatable;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.easy.EasyNode;

/**
 *
 * @author elegios
 */
public class Player implements Position, Renderable, Updatable, ChangeListener, RelativeMovable, Rectangle, KeyReceiver {
    static final float JUMP_SPEED = -320;
    static final float MOVE_SPEED =  160; // 5 squ/igs
    static final float BASE_MASS  =  20;

    private boolean collidedWithImobileX;
    private boolean collidedWithImobileY;

    private float mass;

    private int id;

    private float x;
    private float y;
    private float toSetX;
    private boolean toSetXb;
    private float toSetY;
    private boolean toSetYb;

    private float xSpeed;
    private float ySpeed;
    private float toSetXSpeed;
    private boolean toSetXSpe;
    private float toSetYSpeed;
    private boolean toSetYSpe;

    private boolean moveRight;
    private boolean moveLeft;
    private boolean jump;

    private boolean downMotion;
    private RelativeMovable collided;

    private int width;
    private int height;

    private World world;

    private MediaLoader loader;
    private EasyNode    node;

    private ManagedImage player;

    public Player(World world, int id, int x, int y) throws SlickException {
        this.world      = world;
        this.id         = id;

        loader = world.view().loader();
        node   = world.view().node();

        player = loader.loadManagedImage("player");
        width  = player.getImage().getWidth();
        height = player.getImage().getHeight();

        node.addChangeListener(this);

        c("mass",  BASE_MASS);
        c("x", (float) x);
        c("y", (float) y);
        c("xSpeed", 0.0f);
        c("ySpeed", 0.0f);
    }

    public void moveX(int diff) {
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

    public void update(GameContainer gc, int diff) {
        if ( moveLeft && !moveRight)
            if (collided != null)
                xSpeed = collided.getAbsXSpeed() - MOVE_SPEED;
            else
                xSpeed = -MOVE_SPEED;
        if (!moveLeft &&  moveRight )
            if (collided != null)
                xSpeed = collided.getAbsXSpeed() + MOVE_SPEED;
            else
                xSpeed = MOVE_SPEED;
        if (collided != null && downMotion && jump) {
            collided.pushBackY(-JUMP_SPEED * mass);
            ySpeed = JUMP_SPEED;
        }

        pushBackX(-xSpeed * world.airResist());
        pushBackY(-ySpeed * world.airResist());

        ySpeed += world.actionsPerTick() * diff * world.gravity();

        downMotion = false;
        collided = null;
        collidedWithImobileX = false;
        collidedWithImobileY = false;

        if (world.updatePos() && world.currPlayer() == this) {
            c("x", x);
            c("y", y);
            c("xSpeed", xSpeed);
            c("ySpeed", ySpeed);
        }

    }

    public void collisionFixPosX(float xMove, RelativeMovable collisionOrigin) {
        x += xMove;

        if (collisionOrigin.collidedWithImmobileX())
            collidedWithImobileX = true;
    }

    public void collisionFixPosY(float yMove, RelativeMovable collisionOrigin) {
        y += yMove;
        if (yMove < 0)
            downMotion = true;
        collided = collisionOrigin;

        if (collisionOrigin.collidedWithImmobileY())
            collidedWithImobileY = true;
    }

    public void render(GameContainer gc, Graphics g) {
        player.getImage().draw(ix(), iy());
    }

    public void pushBackX(float momentum) { xSpeed += momentum / mass; }
    public void pushBackY(float momentum) { ySpeed += momentum / mass; }

    public float getAbsXSpeed() { return xSpeed; }
    public float getAbsYSpeed() { return ySpeed; }

    public float getAbsXMove(int diff) { return getAbsXSpeed() * diff * world.actionsPerTick(); }
    public float getAbsYMove(int diff) { return getAbsYSpeed() * diff * world.actionsPerTick(); }

    public float getMass() { return mass; }

    public int getHeight() { return height; }
    public int getWidth()  { return  width; }

    public float getX2() { return getX() +  getWidth() - 1; }
    public float getY2() { return getY() + getHeight() - 1; }

    public float getX() { return x; }
    public float getY() { return y; }

    public int ix() { return Math.round(world.getX() + getX()); }
    public int iy() { return Math.round(world.getY() + getY()); }

    public boolean collidedWithImmobileX() { return collidedWithImobileX; }
    public boolean collidedWithImmobileY() { return collidedWithImobileY; }

    public void collidedWithImmobileX(boolean val) { collidedWithImobileX = val; }
    public void collidedWithImmobileY(boolean val) { collidedWithImobileY = val; }

    public final void c(String id, Object data) { node.c("player." +this.id+ "." +id, data); }

    public void dataChanged(String id, String data) {
        if (id.equals("player." +this.id+ ".activate"))
            node.c(data, true);
    }
    public void intChanged(String id, int data) {}
    public void booleanChanged(String id, boolean data) {
        if (id.startsWith("player." +this.id+ ".")) {
            String var = id.substring(("player." +this.id+ ".").length());
            switch (var) {
                case "moveRight":
                    moveRight = data;
                    break;

                case "moveLeft":
                    moveLeft = data;
                    break;

                case "jump":
                    jump = data;
                    break;
            }
        }
    }
    public void floatChanged(String id, float data) {
        if (id.startsWith("player." +this.id+ ".")) {
            String var = id.substring(("player." +this.id+ ".").length());
            switch (var) {
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
                case "mass":
                    mass = data;
                    break;
                default:
                    System.out.println("Unsupported variable in Player: " +id);
            }
        }
    }

    public boolean keyPressed(int key, char c) {
        switch (key) {
            case Input.KEY_RIGHT:
                c("moveRight", true);
                return true;

            case Input.KEY_LEFT:
                c("moveLeft", true);
                return true;

            case Input.KEY_UP:
                c("jump", true);
                return true;

            case Input.KEY_SPACE:
                world.activateUnderPlayer(this);
                return true;
        }

        return false;
    }

    public boolean keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_RIGHT:
                c("moveRight", false);
                return true;

            case Input.KEY_LEFT:
                c("moveLeft", false);
                return true;

            case Input.KEY_UP:
                c("jump", false);
                return true;
        }

        return false;
    }

}
