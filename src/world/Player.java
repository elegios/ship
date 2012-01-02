/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import media.ManagedImage;
import media.MediaLoader;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.EasyNode;

/**
 *
 * @author elegios
 */
public class Player implements Position, Renderable, Updatable, ChangeListener, RelativeMovable, Rectangle {
    static final float JUMP_SPEED = -320;
    static final float MOVE_SPEED =  160; // 5 squ/igs

    private float mass;

    private int id;

    private float x;
    private float y;

    private float xSpeed;
    private float ySpeed;

    private boolean downMotion;
    private boolean collided;

    //these are the absolute speeds of the object the player last stood on
    private float relativeXSpeed;
    private float relativeYSpeed;

    private int width;
    private int height;

    private World world;

    private MediaLoader loader;
    private EasyNode    node;

    private ManagedImage player;

    public Player(World world, int id, int x, int y) throws SlickException {
        this.world = world;
        this.id = id;

        loader = world.view().loader();
        node   = world.view().node();

        player = loader.loadManagedImage("player");
        width  = player.getImage().getWidth();
        height = player.getImage().getHeight();

        node.addChangeListener(this);

        c("mass",  70.0f);
        c("x", (float) x);
        c("y", (float) y);
        c("xSpeed", 0.0f);
        c("ySpeed", 0.0f);
        c("relativeXSpeed", 0.0f);
        c("relativeYSpeed", 0.0f);
    }

    public void moveX(int diff) { c("x", x + getAbsXMove(diff)); }
    public void moveY(int diff) { c("y", y + getAbsYMove(diff)); }

    public void update(GameContainer gc, int diff) {
        Input input = gc.getInput();
        if ( input.isKeyDown(Input.KEY_LEFT) && !input.isKeyDown(Input.KEY_RIGHT))
            c("xSpeed", -MOVE_SPEED);
        if (!input.isKeyDown(Input.KEY_LEFT) &&  input.isKeyDown(Input.KEY_RIGHT))
            c("xSpeed", MOVE_SPEED);
        if (collided && downMotion && input.isKeyDown(Input.KEY_UP))
                c("ySpeed", JUMP_SPEED);

        c("ySpeed", ySpeed + world.actionsPerTick() * diff * mass * world.gravity());
        //c("ySpeed", 160.0f);

        downMotion = ySpeed > 0;
        collided = false;
    }

    public void collisionFixPosX(float xMove, RelativeMovable collisionOrigin) {
        c("x", x + xMove);
        collided = true;
    }

    public void collisionFixPosY(float yMove, RelativeMovable collisionOrigin) {
        c("y", y + yMove);
        collided = true;
    }

    private void updateCollision(RelativeMovable collisionOrigin) {
        c("relativeXSpeed", collisionOrigin.getAbsXSpeed());
        c("relativeYSpeed", collisionOrigin.getAbsYSpeed());

        collided = true;
    }

    public void render(GameContainer gc, Graphics g) {
        player.getImage().draw(ix(), iy());
    }

    public void pushBackX(float momentum) { c("xSpeed", xSpeed + momentum / mass); }
    public void pushBackY(float momentum) { c("ySpeed", ySpeed + momentum / mass); }

    public float getAbsXSpeed() { return xSpeed + relativeXSpeed; }
    public float getAbsYSpeed() { return ySpeed + relativeYSpeed; }

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

    public final void c(String id, Object data) { node.c("player." +this.id+ "." +id, data); }

    public void dataChanged(String id, String data) {}
    public void intChanged(String id, int data) {}
    public void booleanChanged(String id, boolean data) {}
    public void floatChanged(String id, float data) {
        if (id.startsWith("player." +this.id+ ".")) {
            String var = id.substring(("player." +this.id+ ".").length());
            switch (var) {
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
                case "relativeXSpeed":
                    relativeXSpeed = data;
                    break;
                case "relativeYSpeed":
                    relativeYSpeed = data;
                    break;
                case "mass":
                    mass = data;
                    break;
                default:
                    System.out.println("Unsupported variable in Player: " +id);
            }
        }
    }

}
