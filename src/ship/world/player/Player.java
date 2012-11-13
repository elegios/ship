/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world.player;

import media.ManagedImage;
import media.MediaLoader;
import media.Renderable;

import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.netcode.ShipProtocol;
import ship.netcode.interaction.PlayerMovementPackage;
import ship.netcode.movement.PlayerPositionPackage;
import ship.netcode.movement.RelativePlayerPositionPackage;
import ship.world.Position;
import ship.world.PositionMemory;
import ship.world.PositionMemoryBank;
import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.World;
import ship.world.vehicle.ImmobileVehicle;
import ship.world.vehicle.Vehicle;
import ship.world.vehicle.VehicleHolder;
import ship.world.vehicle.VehiclePiece;

/**
 *
 * @author elegios
 */
public class Player implements Position, Renderable, Updatable, RelativeMovable, Rectangle, KeyReceiver {
    static final float JUMP_SPEED = -320;
    static final float MOVE_SPEED =  160; // 5 squ/igs
    static final float MOVE_ACCEL =  32;
    static final float BASE_MASS  =  20;

    static final float AIR_RESIST_RANGE = 200; //The range of vehicle airresist in pixels

    static final int NAME_HEIGHT = 18;

    private boolean collidedWithImobileX;
    private boolean collidedWithImobileY;
    private float   collisionLockX;
    private float   collisionLockY;

    private float mass;

    private int id;

    protected float x;
    protected float y;

    private float xSpeed;
    private float ySpeed;

    private PlayerPositionPackage toUpdatePos;
    private RelativePlayerPositionPackage toUpdateRel;
    private PositionMemoryBank posBank;

    private boolean moveRight;
    private boolean moveLeft;
    private boolean jump;

    private boolean       downMotion;
    private VehicleHolder collidedY;
    private VehicleHolder lastVehicle;
    private boolean       airResistX;

    private int width;
    private int height;

    private World world;

    private MediaLoader loader;

    private ManagedImage player;
    private String name;

    public Player(World world, int id, int x, int y) throws SlickException {
        this.world      = world;
        this.id         = id;

        if (world.view().playerId() != id)
            name = world.view().net().getPlayerName(id);
        else
            name = null;

        loader = world.view().loader();

        player = loader.loadManagedImage("player");
        width  = player.getImage().getWidth();
        height = player.getImage().getHeight();

        posBank = new PositionMemoryBank(World.POS_MEMORY_COUNT);

        mass = BASE_MASS;
        this.x = x;
        this.y = y;
        xSpeed = 0.0f;
        ySpeed = 0.0f;
    }

    public void moveX(int diff) {
        x += getAbsXMove(diff);

        if (toUpdateRel != null) {
            toUpdateRel.xChecked(true);
            int time = toUpdateRel.getTime();

            PositionMemory closest = posBank.getClosest(time);
            if (closest != null) {

                if (lastVehicle == null || lastVehicle.getID() != toUpdateRel.getVehicleId())
                    lastVehicle = world.findVehicleHolder(toUpdateRel.getVehicleId());

                PositionMemory lastClosest = lastVehicle.getVehicle().getClosest(time);

                if (closest.getVehicle() != null) { //The PositionMemory is relative
                    if (closest.getVehicle() == lastVehicle) { //The Vehicle in the PositionMemory is equal to that of toUpdateRel
                        x += toUpdateRel.getX() - closest.getX();

                    } else { //The Vehicles in the PositionMemory and toUpdateRel are different
                        PositionMemory vehClosest = closest.getVehicle().getVehicle().getClosest(time);
                        if (vehClosest != null && lastClosest != null) {
                            x += toUpdateRel.getX() + lastClosest.getX() -
                                    (closest    .getX() +  vehClosest.getX());
                        }
                    }

                } else if (lastClosest != null){ //The PositionMemory isn't relative
                    x += toUpdateRel.getX() + lastClosest.getX() - closest.getX();
                }

                xSpeed += toUpdateRel.getXSpeed() - closest.getXSpeed();
            } else
                toUpdateRel.xChecked(false);

        } else if (toUpdatePos != null) {
            toUpdatePos.xChecked(true);
            int time = toUpdatePos.getTime();

            PositionMemory closest = posBank.getClosest(time);
            if (closest != null) {
                if (closest.getVehicle() == null) { //The PositionMemory isn't relative
                    x += toUpdatePos.getX() - closest.getX();

                } else //The PositionMemory is relative
                    x += toUpdatePos.getX() - (closest.getX() + closest.getVehicle().getVehicle().getClosest(time).getX());

                xSpeed += toUpdatePos.getXSpeed() - closest.getXSpeed();

            } else
                toUpdatePos.xChecked(false);
        }
    }
    public void moveY(int diff) {
        y += getAbsYMove(diff);

        if (toUpdateRel != null && toUpdateRel.xChecked()) {
            int time = toUpdateRel.getTime();

            if (lastVehicle == null || lastVehicle.getID() != toUpdateRel.getVehicleId())
                lastVehicle = world.findVehicleHolder(toUpdateRel.getVehicleId());

            PositionMemory closest = posBank.getClosest(time); //This will work, because Y is always set after x which won't happen
                                                               //unless there is a PositionMemory
            PositionMemory lastClosest = lastVehicle.getVehicle().getClosest(time);

            if (closest.getVehicle() != null) { //The PositionMemory is relative
                if (closest.getVehicle() == lastVehicle) { //The Vehicle in the PositionMemory is equal to that of toUpdateRel
                    y += toUpdateRel.getY() - closest.getY();

                } else { //The Vehicles in the PositionMemory and toUpdateRel are different
                    PositionMemory vehClosest = closest.getVehicle().getVehicle().getClosest(time);
                    if (vehClosest != null && lastClosest != null) {
                        y += toUpdateRel.getY() + lastClosest.getY() -
                                (closest    .getY() +  vehClosest.getY());
                    }
                }

            } else if (lastClosest != null){ //The PositionMemory isn't relative
                y += toUpdateRel.getY() + lastClosest.getY() - closest.getY();
            }

            ySpeed += toUpdateRel.getYSpeed() - closest.getYSpeed();

            toUpdateRel = null;

        } else if (toUpdatePos != null && toUpdatePos.xChecked()) {
            int time = toUpdatePos.getTime();

            PositionMemory closest = posBank.getClosest(time); //This will work because Y is always set after X which won't happen
                                                               //unless there is a PositionMemory
            if (closest.getVehicle() == null) { //The PositionMemory isn't relative
                y += toUpdatePos.getY() - closest.getY();

            } else //The PositionMemory is relative
                y += toUpdatePos.getY() - (closest.getY() + closest.getVehicle().getVehicle().getClosest(time).getY());

            ySpeed += toUpdatePos.getYSpeed() - closest.getYSpeed();

            toUpdatePos = null;
        }
    }

    public void relMoveX(Vehicle vehicle, float move) {
        if (lastVehicle != null && vehicle == lastVehicle.getVehicle())
            x += move;
    }
    public void relMoveY(Vehicle vehicle, float move) {
        if (lastVehicle != null && vehicle == lastVehicle.getVehicle())
            y += move;
    }


    public void updateEarly(GameContainer gc, int diff) {
        if (lastVehicle != null && collidedY != null) {
            lastVehicle = collidedY; //Fix bug where last vehicle touched is moved towards a player walking towards it
        }

        //move left
        if (moveLeft && !moveRight)
            if (airResistX) {
                if (xSpeed > lastVehicle.getVehicle().getAbsXSpeed() - MOVE_SPEED) {
                    if (!lastVehicle.getVehicle().collidedWithImmobileY())
                        lastVehicle.getVehicle().pushX(mass * MOVE_ACCEL);
                    xSpeed -= MOVE_ACCEL;
                }
            } else
                if (xSpeed > -MOVE_SPEED)
                    xSpeed -= MOVE_ACCEL;

        //move right
        if (!moveLeft && moveRight)
            if (airResistX) {
                if (xSpeed < lastVehicle.getVehicle().getAbsXSpeed() + MOVE_SPEED) {
                    if (!lastVehicle.getVehicle().collidedWithImmobileY())
                        lastVehicle.getVehicle().pushX(mass * -MOVE_ACCEL);
                    xSpeed += MOVE_ACCEL;
                }
            } else
                if (xSpeed <  MOVE_SPEED)
                    xSpeed += MOVE_ACCEL;

        //jump
        if (collidedY != null && downMotion && jump) {
            if (collidedY.getVehicle().collidedWithImmobileY())
                ySpeed = JUMP_SPEED;
            else
                ySpeed = JUMP_SPEED + collidedY.getVehicle().getAbsYSpeed();
            collidedY.getVehicle().pushY(-JUMP_SPEED * mass);
        }

        downMotion = false;
        collidedY = null;
        collidedWithImobileX = false;
        collidedWithImobileY = false;
    }

    public void update(GameContainer gc, int diff) {
        if (lastVehicle == null || !doAirResistX(lastVehicle)) {
            boolean airResisted = false;
            for (VehicleHolder vehicle : world.vehicles())
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
            for (VehicleHolder vehicle : world.vehicles())
                if (doAirResistY(vehicle)) {
                    airResisted = true;
                    break;
                }
            if (!airResisted)
                doAirResistY();
        }

        if (collidedY != null)
            lastVehicle = collidedY;

        ySpeed += world.actionsPerTick() * diff * world.gravity();

        if (world.updatePos() && world.view().net().isOnline()) {
            if (world.currPlayerHolder().getPlayer() == this) {
                if (lastVehicle != null && !(lastVehicle.getVehicle() instanceof ImmobileVehicle))
                    world.view().net().send(ShipProtocol.REL_PLAYER_POS, new RelativePlayerPositionPackage(id, lastVehicle.getID(),
                                                                                                           world.time(),
                                                                                                           x - lastVehicle.getVehicle().getX(),
                                                                                                           y - lastVehicle.getVehicle().getY(),
                                                                                                           xSpeed, ySpeed));

                else
                    world.view().net().send(ShipProtocol.PLAYER_POS, new PlayerPositionPackage(id, world.time(), x, y, xSpeed, ySpeed));
            } else {
                if (lastVehicle != null && !(lastVehicle.getVehicle() instanceof ImmobileVehicle)) {
                    posBank.store(world.time(), x - lastVehicle.getVehicle().getX(), y - lastVehicle.getVehicle().getY(), xSpeed, ySpeed, lastVehicle);
                } else {
                    posBank.store(world.time(), x, y, xSpeed, ySpeed, null);
                }
            }
        }
    }

    private boolean doAirResistX(VehicleHolder vehicle) {
        VehiclePiece closestPiece = vehicle.findClosestPiece(getX(), getY());

        int playX = closestPiece.getTileXUnderPos(getX() + getWidth ()/2);
        int playY = closestPiece.getTileYUnderPos(getY() + getHeight()/2);

        if (getX() >= closestPiece.getBoundX() - AIR_RESIST_RANGE && getX() < closestPiece.getBoundX2() + AIR_RESIST_RANGE &&
            getY() >= closestPiece.getBoundY()                    && getY() <= closestPiece.getBoundY2()) {

            if (xSpeed - vehicle.getVehicle().getAbsXSpeed() > 0) {
                for (int i = Math.max(playX, closestPiece.leftX()); i <= closestPiece.rightX(); i++) {
                    if (vehicle.getVehicle().existsAt(i, playY)) {
                        pushX(-(xSpeed - vehicle.getVehicle().getAbsXSpeed()) * world.airResist());
                        airResistX = true;
                        return true;
                    }
                }

            } else if (xSpeed - vehicle.getVehicle().getAbsXSpeed() < 0) {
                //The following if statement is to fix a bug when the player is to the right of the VehiclePiece
                //and getTileXUnderPos returns -1
                if (playX == -1)
                    playX = closestPiece.rightX();

                for (int i = playX; i >= closestPiece.leftX(); i--) {
                    if (vehicle.getVehicle().existsAt(i, playY)) {
                        pushX(-(xSpeed - vehicle.getVehicle().getAbsXSpeed()) * world.airResist());
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

    private boolean doAirResistY(VehicleHolder vehicle) {
        VehiclePiece closestPiece = vehicle.findClosestPiece(getX(), getY());

        int playX = closestPiece.getTileXUnderPos(getX() + getWidth ()/2);
        int playY = closestPiece.getTileYUnderPos(getY() + getHeight()/2);

        if (getX() >= closestPiece.getBoundX()                    && getX() <  closestPiece.getBoundX2() &&
            getY() >= closestPiece.getBoundY() - AIR_RESIST_RANGE && getY() <= closestPiece.getBoundY2() + AIR_RESIST_RANGE) {

            if (ySpeed - vehicle.getVehicle().getAbsYSpeed() > 0) {
                for (int j = Math.max(playY, closestPiece.topY()); j <= closestPiece.botY(); j++) {
                    if (vehicle.getVehicle().existsAt(playX, j)) {
                        pushY(-(ySpeed - vehicle.getVehicle().getAbsYSpeed()) * world.airResist());
                        return true;
                    }
                }

            } else if (ySpeed - vehicle.getVehicle().getAbsYSpeed() < 0) {
                //The following if statement is to fix a bug when the player is to the right of the VehiclePiece
                //and getTileXUnderPos returns -1
                if (playY == -1)
                    playY = closestPiece.botY();

                for (int j = playY; j >= closestPiece.topY(); j--) {
                    if (vehicle.getVehicle().existsAt(playX, j)) {
                        pushY(-(ySpeed - vehicle.getVehicle().getAbsYSpeed()) * world.airResist());
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

    public void collisionFixPosX(float xMove, VehicleHolder collisionOrigin) {
        x += xMove;

        if (collisionOrigin.getVehicle().collidedWithImmobileX())
            collidedWithImobileX = true;
    }

    public void collisionFixPosY(float yMove, VehicleHolder collisionOrigin) {
        y += yMove;
        if (yMove < 0)
            downMotion = true;
        collidedY = collisionOrigin;

        if (collisionOrigin.getVehicle().collidedWithImmobileY())
            collidedWithImobileY = true;
    }

    public void render(GameContainer gc, Graphics g) {
        player.getImage().draw(ix(), iy());

        if (name != null) {
            Font font = world.view().fonts().name();
            font.drawString(ix() + width/2 - font.getWidth(name)/2, iy() - NAME_HEIGHT , name);
        }
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

    public World world() { return world; }

    public void receivePlayerPositionPackage(PlayerPositionPackage pack) {
        if ((toUpdatePos == null || !toUpdatePos.xChecked()) && (toUpdateRel == null || !toUpdateRel.xChecked()))
            toUpdatePos = pack;
    }

    public void receiveRelativePlayerPositionPackage(RelativePlayerPositionPackage pack) {
        if ((toUpdatePos == null || !toUpdatePos.xChecked()) && (toUpdateRel == null || !toUpdateRel.xChecked()))
            toUpdateRel = pack;
    }

    public void receivePlayerMovementPackage(PlayerMovementPackage pack) {
        switch (pack.getType()) {
            case PlayerMovementPackage.MOVE_LEFT:
                moveLeft = pack.getValue();
                break;

            case PlayerMovementPackage.MOVE_RIGHT:
                moveRight = pack.getValue();
                break;

            case PlayerMovementPackage.JUMP:
                jump = pack.getValue();
                break;
        }
    }

    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == keys.right()) {
            moveRight = true;
            if (world.view().net().isOnline())
                world.view().net().send(ShipProtocol.PLAYER_MOVE, new PlayerMovementPackage(id, PlayerMovementPackage.MOVE_RIGHT, true));
            return true;

        } if (key == keys.left()) {
            moveLeft = true;
            if (world.view().net().isOnline())
                world.view().net().send(ShipProtocol.PLAYER_MOVE, new PlayerMovementPackage(id, PlayerMovementPackage.MOVE_LEFT, true));
            return true;

        } if (key == keys.up()) {
            jump = true;
            if (world.view().net().isOnline())
                world.view().net().send(ShipProtocol.PLAYER_MOVE, new PlayerMovementPackage(id, PlayerMovementPackage.JUMP, true));
            return true;

        }

        return false;
    }

    public boolean keyReleased(Keys keys, int key, char c) {
        if (key == keys.right()) {
            moveRight = false;
            if (world.view().net().isOnline())
                world.view().net().send(ShipProtocol.PLAYER_MOVE, new PlayerMovementPackage(id, PlayerMovementPackage.MOVE_RIGHT, false));
            return true;

        } else if (key == keys.left()) {
            moveLeft = false;
            if (world.view().net().isOnline())
                world.view().net().send(ShipProtocol.PLAYER_MOVE, new PlayerMovementPackage(id, PlayerMovementPackage.MOVE_LEFT, false));
            return true;

        } else if (key == keys.up()) {
            jump = false;
            if (world.view().net().isOnline())
                world.view().net().send(ShipProtocol.PLAYER_MOVE, new PlayerMovementPackage(id, PlayerMovementPackage.JUMP, false));
            return true;
        }

        return false;
    }

    public int getID() {
        return id;
    }

}
