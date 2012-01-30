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
    private float toSetXRel;
    private float toSetY;
    private float toSetYRel;

    private float xSpeed;
    private float ySpeed;
    private float toSetXSpeed;
    private float toSetYSpeed;

    private boolean moveRight;
    private boolean moveLeft;
    private boolean jump;

    private boolean downMotion;
    private RelativeMovable collidedY;
    private Vehicle lastVehicle;
    private int     toSetLastVehicle;
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

        builder = new Builder(world.view().inventory(), this);

        node.addChangeListener(this);

        c("mass",  BASE_MASS);
        this.x = x;
        this.y = y;
        c("x", x);
        c("y", y);
        c("xSpeed", 0.0f);
        c("ySpeed", 0.0f);

        toSetXRel = Float.NaN;
        toSetYRel = Float.NaN;
        toSetLastVehicle = -1;
    }

    public void moveX(int diff) {
        if (world.currPlayer() != this) {
            if (!Float.isNaN(toSetX)) {
                x = toSetX;
                toSetX = Float.NaN;

            } else if (!Float.isNaN(toSetXRel) && lastVehicle != null) {
                x = lastVehicle.getX() + toSetXRel;
                toSetXRel = Float.NaN;
            }

            if (!Float.isNaN(toSetXSpeed)) {
                xSpeed = toSetXSpeed;
                toSetXSpeed = Float.NaN;
            }
        }

        x += getAbsXMove(diff);
    }
    public void moveY(int diff) {
        if (world.currPlayer() != this) {
            if (!Float.isNaN(toSetY)) {
                y = toSetY;
                toSetY = Float.NaN;

            } else if (!Float.isNaN(toSetYRel) && lastVehicle != null) {
                y = lastVehicle.getY() + toSetYRel;
                toSetYRel = Float.NaN;
            }

            if (!Float.isNaN(toSetYSpeed)) {
                ySpeed = toSetYSpeed;
                toSetYSpeed = Float.NaN;
            }
        }

        y += getAbsYMove(diff);
    }

    public void relMoveX(Vehicle vehicle, float move) {
        if (vehicle == lastVehicle)
            x = move + getX();
    }
    public void relMoveY(Vehicle vehicle, float move) {
        if (vehicle == lastVehicle)
            y = move + getY();
    }

    public void updateEarly(GameContainer gc, int diff) {
        //move left
        if (moveLeft && !moveRight)
            if (airResistX) {
                if (xSpeed > lastVehicle.getAbsXSpeed() - MOVE_SPEED) {
                    if (!lastVehicle.collidedWithImmobileY())
                        lastVehicle.pushX(mass * 32);
                    xSpeed -= 32;
                }
            } else
                if (xSpeed > - MOVE_SPEED)
                    xSpeed -= 32;

        //move right
        if (!moveLeft && moveRight)
            if (airResistX) {
                if (xSpeed < lastVehicle.getAbsXSpeed() + MOVE_SPEED) {
                    if (!lastVehicle.collidedWithImmobileY())
                        lastVehicle.pushX(-mass * 32);
                    xSpeed += 32;
                }
            } else
                if (xSpeed < MOVE_SPEED)
                    xSpeed += 32;

        //jump
        if (collidedY != null && downMotion && jump) {
            collidedY.pushY(-JUMP_SPEED * mass);
            if (collidedY.collidedWithImmobileY())
                ySpeed = JUMP_SPEED;
            else
                ySpeed = JUMP_SPEED + collidedY.getAbsYSpeed();
        }

        downMotion = false;
        collidedY = null;
        collidedWithImobileX = false;
        collidedWithImobileY = false;

        if (toSetLastVehicle >= 0) {
            lastVehicle = world.findVehicle(toSetLastVehicle);
            toSetLastVehicle = -1;
        }
    }

    public void update(GameContainer gc, int diff) {
        if (collidedY != null)
            if (collidedY instanceof Vehicle)
                lastVehicle = (Vehicle) collidedY;
            else
                lastVehicle = null;

        if (lastVehicle == null || !doAirResistX(lastVehicle)) {
            boolean airResisted = false;
            for (Vehicle vehicle : world.vehicles())
                if (doAirResistX(vehicle)) {
                    lastVehicle = vehicle;
                    airResisted = true;
                    break;
                }
            if (!airResisted)
                doAirResistX();
        }
        if (lastVehicle == null || !doAirResistY(lastVehicle)) {
            boolean airResisted = false;
            for (Vehicle vehicle : world.vehicles())
                if (doAirResistY(vehicle)) {
                    airResisted = true;
                    break;
                }
            if (!airResisted)
                doAirResistY();
        }

        ySpeed += world.actionsPerTick() * diff * world.gravity();

        if (world.updatePos() && world.currPlayer() == this) {
            if (lastVehicle != null) {
                c("lastVehicle", lastVehicle.getID());
                c("xRel", x - lastVehicle.getX());
                c("yRel", y - lastVehicle.getY());
            } else {
                c("x", x);
                c("y", y);
            }
            c("xSpeed", xSpeed);
            c("ySpeed", ySpeed);
        }

    }

    private boolean doAirResistX(Vehicle vehicle) {
        int playX = vehicle.getTileXUnderPos(getX() + getWidth ()/2);
        int playY = vehicle.getTileYUnderPos(getY() + getHeight()/2);

        if (playX >= 0               && playX <  vehicle.WIDTH() &&
            playY >= vehicle.topY () && playY <= vehicle.botY ()) {
            if (xSpeed > 0) {
                for (int i = playX; i <= vehicle.rightX(); i++) {
                    if (vehicle.existsAt(i, playY)) {
                        pushX(-(xSpeed - vehicle.getAbsXSpeed()) * world.airResist());
                        airResistX = true;
                        return true;
                    }
                }

            } else if (xSpeed < 0) {
                for (int i = playX; i >= vehicle.leftX(); i--) {
                    if (vehicle.existsAt(i, playY)) {
                        pushX(-(xSpeed - vehicle.getAbsXSpeed()) * world.airResist());
                        airResistX = true;
                        return true;
                    }
                }

            }
        }

        return false;
    }
    private void doAirResistX() {
        pushX(-xSpeed * world.airResist());
        airResistX = false;
    }

    private boolean doAirResistY(Vehicle vehicle) {
        int playX = vehicle.getTileXUnderPos(getX() + getWidth ()/2);
        int playY = vehicle.getTileYUnderPos(getY() + getHeight()/2);

        if (playX >= vehicle.leftX() && playX <= vehicle.rightX() &&
            playY >= 0               && playY <  vehicle.HEIGHT()) {
            if (ySpeed > 0) {
                for (int j = playY; j <= vehicle.botY(); j++) {
                    if (vehicle.existsAt(playX, j)) {
                        pushY(-(ySpeed - vehicle.getAbsYSpeed()) * world.airResist());
                        return true;
                    }
                }

            } else if (ySpeed < 0) {
                for (int j = playY; j >= vehicle.topY(); j--) {
                    if (vehicle.existsAt(playX, j)) {
                        pushY(-(ySpeed - vehicle.getAbsYSpeed()) * world.airResist());
                        return true;
                    }
                }

            }
        }

        return false;
    }
    private void doAirResistY() {
        pushY(-ySpeed * world.airResist());
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
        collidedY = collisionOrigin;

        if (collisionOrigin.collidedWithImmobileY())
            collidedWithImobileY = true;
    }

    public void render(GameContainer gc, Graphics g) {
        player.getImage().draw(ix(), iy());
        builder.render(gc, g);
    }

    public void pushX(float momentum) { xSpeed += momentum / mass; }
    public void pushY(float momentum) { ySpeed += momentum / mass; }

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
            Vehicle vehicle = world.findVehicle(s.nextInt());
            if (vehicle != null)
                vehicle.tile(s.nextInt(), s.nextInt()).activate(this);

        } else if (id.equals("player." +this.id+ ".makeTile")) //TODO: make this action only enter DataVerse once, the player message that triggered this will end up in every client
            node.c(data, true);
        else if (id.equals("player." +this.id+ ".deleTile"))
            node.c(data, true);
    }
    public void intChanged(String id, int data) {
        if (id.startsWith("player." +this.id+ ".")) {
            String var = id.substring(("player." +this.id+ ".").length());
            if (var.startsWith("lastVehicle"))
                toSetLastVehicle = data;
            else
                builder.updateInt(var, data);
        }
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
                case "xRel":
                    toSetXRel = data;
                    break;

                case "yRel":
                    toSetYRel = data;
                    break;

                case "x":
                    toSetX = data;
                    break;

                case "y":
                    toSetY = data;
                    break;

                case "xSpeed":
                    toSetXSpeed = data;
                    break;

                case "ySpeed":
                    toSetYSpeed = data;
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
