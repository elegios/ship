/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world.player;

import java.util.Scanner;

import media.ManagedImage;
import media.MediaLoader;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.world.Position;
import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.World;
import ship.world.collisiongrid.vehicle.Vehicle;
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
    private float   collisionLockX;
    private float   collisionLockY;

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
    private Vehicle lastVehicle;
    private boolean airResistX;

    private Builder builder;

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

        builder = new Builder(world.view().inventory(), this);
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

    public void update(GameContainer gc, int diff) { //TODO: fix player movement, not set speed, push in a direction. Also, profile entire thing.
        if ( moveLeft && !moveRight)
            if (collided != null || airResistX)
                if (airResistX)
                    xSpeed = lastVehicle.getAbsXSpeed() - MOVE_SPEED;
                else
                    xSpeed = collided.getAbsXSpeed() - MOVE_SPEED;
            else
                xSpeed = -MOVE_SPEED;
        if (!moveLeft &&  moveRight )
            if (collided != null || airResistX)
                if (airResistX)
                    xSpeed = lastVehicle.getAbsXSpeed() + MOVE_SPEED;
                else
                    xSpeed = collided.getAbsXSpeed() + MOVE_SPEED;
            else
                xSpeed = MOVE_SPEED;
        if (collided != null && downMotion && jump) {
            collided.pushBackY(-JUMP_SPEED * mass);
            ySpeed = JUMP_SPEED;
        }

        if (collided != null)
            if (collided instanceof Vehicle)
                lastVehicle = (Vehicle) collided;
            else
                lastVehicle = null;

        doAirResistX();
        doAirResistY();

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

    private void doAirResistX() {
        int playX = 0;
        int playY = 0;
        if (lastVehicle != null) {
            playX = lastVehicle.getTileXUnderPos(getX() + getWidth ()/2);
            playY = lastVehicle.getTileYUnderPos(getY() + getHeight()/2);
        }

        if (lastVehicle != null &&
            playX >= 0 && playX < lastVehicle.WIDTH() &&
            playY >= 0 && playY < lastVehicle.HEIGHT()) {
            if (xSpeed > 0) {
                for (int i = playX; i < lastVehicle.WIDTH(); i++) {
                    if (lastVehicle.existsAt(i, playY)) {
                        pushBackX(-(xSpeed - lastVehicle.getAbsXSpeed()) * world.airResist());
                        airResistX = true;
                        return;
                    }
                }

                airResistX = false;
                pushBackX(-xSpeed * world.airResist());
            } else if (xSpeed < 0) {
                for (int i = playX; i > 0; i--) {
                    if (lastVehicle.existsAt(i, playY)) {
                        pushBackX(-(xSpeed - lastVehicle.getAbsXSpeed()) * world.airResist());
                        airResistX = true;
                        return;
                    }
                }

                airResistX = false;
                pushBackX(-xSpeed * world.airResist());
            }
        } else {
            airResistX = false;
            pushBackX(-xSpeed * world.airResist());
        }
    }

    private void doAirResistY() {
        int playX = 0;
        int playY = 0;
        if (lastVehicle != null) {
            playX = lastVehicle.getTileXUnderPos(getX() + getWidth ()/2);
            playY = lastVehicle.getTileYUnderPos(getY() + getHeight()/2);
        }

        if (lastVehicle != null &&
            playX >= 0 && playX < lastVehicle.WIDTH() &&
            playY >= 0 && playY < lastVehicle.HEIGHT()) {
            if (ySpeed > 0) {
                for (int j = playY; j < lastVehicle.WIDTH(); j++) {
                    if (lastVehicle.existsAt(playX, j)) {
                        pushBackY(-(ySpeed - lastVehicle.getAbsYSpeed()) * world.airResist());
                        return;
                    }
                }

                pushBackY(-ySpeed * world.airResist());
            } else if (ySpeed < 0) {
                for (int j = playY; j > 0; j--) {
                    if (lastVehicle.existsAt(playX, j)) {
                        pushBackY(-(ySpeed - lastVehicle.getAbsYSpeed()) * world.airResist());
                        return;
                    }
                }

                pushBackY(-ySpeed * world.airResist());
            }
        } else {
            pushBackY(-ySpeed * world.airResist());
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
        builder.render(gc, g);
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
    public float   collisionLockX()        { return collisionLockX;       }
    public float   collisionLockY()        { return collisionLockY;       }

    public void collidedWithImmobileX(boolean val) { collidedWithImobileX = val; }
    public void collidedWithImmobileY(boolean val) { collidedWithImobileY = val; }
    public void collisionLockX       (float   val) { collisionLockX       = val; }
    public void collisionLockY       (float   val) { collisionLockY       = val; }

    public Builder builder() { return builder; }

    public World world() { return world; }

    public final void c(String id, Object data) { node.c("player." +this.id+ "." +id, data); }

    public void dataChanged(String id, String data) {
        if (id.equals("player." +this.id+ ".activate")) {
            Scanner s = new Scanner(data);
            s.useDelimiter("\\.");
            world.activateOnVehicle(s.nextInt(), s.nextInt(), s.nextInt());

        } else if (id.equals("player." +this.id+ ".makeTile"))
            node.c(data, true);
        else if (id.equals("player." +this.id+ ".deleTile"))
            node.c(data, true);
    }
    public void intChanged(String id, int data) {
        if (id.startsWith("player." +this.id+ "."))
            builder.updateInt(id.substring(("player." +this.id+ ".").length()), data);
    }
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

                default:
                    builder.updateBoolean(var, data);
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

    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == keys.right()) {
            c("moveRight", true);
            return true;

        } if (key == keys.left()) {
            c("moveLeft", true);
            return true;

        } if (key == keys.up()) {
            c("jump", true);
            return true;

        } if (key == keys.activateDevice()) {
            world.activateUnderPlayer(this);
            return true;
        }

        return builder.keyPressed(keys, key, c);
    }

    public boolean keyReleased(Keys keys, int key, char c) {
        if (key == keys.right()) {
            c("moveRight", false);
            return true;

        } else if (key == keys.left()) {
            c("moveLeft", false);
            return true;

        } else if (key == keys.up()) {
            c("jump", false);
            return true;
        }

        return builder.keyReleased(keys, key, c);
    }

}