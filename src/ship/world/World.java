/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world;

import java.util.ArrayList;
import java.util.List;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.world.collisiongrid.CollisionGrid;
import ship.world.collisiongrid.island.Island;
import ship.world.collisiongrid.vehicle.Vehicle;
import ship.world.player.Builder;
import ship.world.player.Player;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.easy.EasyNode;

/**
 *
 * @author elegios
 */
public class World implements Position, Renderable, Updatable, ChangeListener, KeyReceiver {
    public static final int SKY_GRADIENT_MINIMUM = 220 * CollisionGrid.TW;
    public static final int SKY_GRADIENT_LENGTH  = 10;
    public static final int SKY_MAX_R = 62;
    public static final int SKY_MAX_G = 209;
    public static final int SKY_MAX_B = 255;

    private View view;

    private EasyNode    node;

    private int x;
    private int y;

    private ManagedSpriteSheet tileset;
    private static Color[] skyColors;

    private ParallaxBackground paraBack;

    private Island island;
    private Player[] players;
    private Player currPlayer;
    private List<Vehicle> vehicles;

    private float actionsPerTick;
    private float gravity;
    private float frictionFraction;
    private float airResist;
    private float fuelRate;

    private boolean updatePos;
    private int     timeTilUpdatePos;
    private int     updatePosInterval;

    public World(View view) throws SlickException {
        if (skyColors == null) {
            skyColors = new Color[256];
            for (int i = 0; i < 256; i++) {
                skyColors[i] = new Color(Math.max(SKY_MAX_R - i, 0), Math.max(SKY_MAX_G - i, 0), Math.max(SKY_MAX_B - i, 0));
            }
        }

        this.view = view;

        paraBack = new ParallaxBackground(this);

        node = view.node();

        node.addChangeListener(this);

        x = 0;
        y = 0;

        c("actionsPerTick",    1.0f/1000);
        c("gravity",           9.8f * 50);
        c("frictionFraction",  0.6f/100);
        c("airResist",         0.2f);
        c("fuelRate",          1.0f/1000);

        tileset = view.loader().loadManagedSpriteSheet("tiles", CollisionGrid.TW, CollisionGrid.TH);

        island  = new Island(this, 0,  0, 0);
        vehicles = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            vehicles.add(new Vehicle(this, i, island.getWidth()/2 + 128 +i*400, -32));
        for (Vehicle vehicle : vehicles)
            vehicle.generateStandardVehicle();

        players = new Player[view.numPlayers()];
        for (int i = 0; i < view.numPlayers(); i++)
            players[i] = new Player(this, i, island.getWidth()/2 + 32, -100);
        currPlayer = players[view.playerId()];

        updatePos = false;
        updatePosInterval = 300;
        timeTilUpdatePos = updatePosInterval;
    }

    @Override
    public void update(GameContainer gc, int diff) {
        for (Player player : players)
            player.controlUpdate(gc, diff);

        moveX(diff);
        collideX();
        moveY(diff);
        collideY();

        timeTilUpdatePos -= diff;
        if (timeTilUpdatePos < 0) {
            updatePos = true;
            timeTilUpdatePos += updatePosInterval;
        } else
            updatePos = false;

        for (Player player : players)
            player.update(gc, diff);
        for (Vehicle vehicle : vehicles)
            vehicle.update(gc, diff);

        x = Math.round(currPlayer.getX()) + currPlayer.getWidth()/2  - View.window().getWidth()/2;
        y = Math.round(currPlayer.getY()) + currPlayer.getHeight()/2 - View.window().getHeight()/2;
    }

    private void moveX(int diff) {
        for (Vehicle vehicle : vehicles)
            vehicle.moveX(diff);
        for (Player player : players)
            player.moveX(diff);
    }
    private void collideX() {
        for (Vehicle vehicle : vehicles)
            vehicle.collideWithCollisionGridX(island);
        for (Vehicle vehicle : vehicles)
            collideVehicleX(vehicle);

        for (Player player : players)
            collidePlayerX(player);
    }
    private void moveY(int diff) {
        for (Vehicle vehicle : vehicles)
            vehicle.moveY(diff);
        for (Player player : players)
            player.moveY(diff);
    }
    private void collideY() {
        for (Vehicle vehicle : vehicles)
            vehicle.collideWithCollisionGridY(island);
        for (Vehicle vehicle : vehicles)
            collideVehicleY(vehicle);

        for (Player player : players)
            collidePlayerY(player);
    }

    public void collideVehicleX(Vehicle vehicle) {
        for (int i = (vehicles.indexOf(vehicle) + 1); i < vehicles.size(); i++)
            if (vehicles.get(i).getID() != vehicle.getID()) {
                if (vehicles.get(i).getID() < vehicle.getID())
                    collideVehicleX(vehicles.get(i));
                else if (vehicle.collideWithCollisionGridX(vehicles.get(i)))
                    i = -1;
                if (i == vehicles.size() - 1 && vehicle.collideWithCollisionGridX(island))
                    i = -1;
            }
    }

    public void collideVehicleY(Vehicle vehicle) {
        for (int i = (vehicles.indexOf(vehicle) + 1); i < vehicles.size(); i++)
            if (vehicles.get(i).getID() != vehicle.getID()) {
                if (vehicles.get(i).getID() < vehicle.getID())
                    collideVehicleY(vehicles.get(i));
                else if (vehicle.collideWithCollisionGridY(vehicles.get(i)))
                    i = -1;
                if (i == vehicles.size() - 1 && vehicle.collideWithCollisionGridY(island))
                    i = -1;
            }
    }

    public void collidePlayerX(Player player) {
        for (Vehicle vehicle : vehicles)
            if (vehicle.overlaps(player)) {
                float fixMove = vehicle.collideRectangleX(player, player.getAbsXSpeed() - vehicle.getAbsXSpeed());
                if (fixMove != 0)
                    player.collisionFixPosX(fixMove, vehicle);
            }

        if (island.overlaps(player)) {
            float fixMove = island.collideRectangleX(player, player.getAbsXSpeed());
            if (fixMove != 0)
                player.collisionFixPosX(fixMove, island);
        }
    }

    public void collidePlayerY(Player player) {
        for (Vehicle vehicle : vehicles)
            if (vehicle.overlaps(player)) {
                float fixMove = vehicle.collideRectangleY(player, player.getAbsYSpeed() - vehicle.getAbsYSpeed());
                if (fixMove != 0)
                    player.collisionFixPosY(fixMove, vehicle);
            }

        if (island.overlaps(player)) {
            float fixMove = island.collideRectangleY(player, player.getAbsYSpeed());
            if (fixMove != 0)
                player.collisionFixPosY(fixMove, island);
        }
    }

    public void activateUnderPlayer(Player player) {
        for (Vehicle vehicle : vehicles)
            if (vehicle.overlaps(player)) {
                int vehX = vehicle.getTileXUnderPos(player.getX() + player.getWidth ()/2);
                int vehY = vehicle.getTileYUnderPos(player.getY() + player.getHeight()/2);
                if (vehX >= 0 && vehX < vehicle.WIDTH() &&
                    vehY >= 0 && vehY < vehicle.HEIGHT() &&
                    vehicle.tile(vehX, vehY) != null)
                    player.c("activate", vehicle.getID() +"."+ vehX +"."+ vehY);
            }

    }
    public void activateOnVehicle(Player player, int vehicle, int x, int y) { vehicles.get(vehicle).tile(x, y).activate(player); }

    public void buildUnderPlayerBuilder(Player player) {
        for (Vehicle vehicle : vehicles) {
            int tx = vehicle.getTileXUnderPos(player.builder().getX() + player.builder().getWidth ()/2);
            int ty = vehicle.getTileYUnderPos(player.builder().getY() + player.builder().getHeight()/2);
            if (tx >= 1 && tx < vehicle.WIDTH() - 1 &&
                ty >= 1 && ty < vehicle.HEIGHT() - 1)
                if (!vehicle.existsAt(tx, ty) &&
                        (vehicle.existsAt(tx    , ty - 1) ||
                         vehicle.existsAt(tx + 1, ty    ) ||
                         vehicle.existsAt(tx    , ty + 1) ||
                         vehicle.existsAt(tx - 1, ty    ))) {
                    player.c("makeTile", "vehicle." +vehicle.getID()+ ".make." +player.builder().getMakeString()+ "." +tx+ "." +ty);
                    break;
                }
        }

    }

    public void destroyUnderPlayerBuilder(Player player) {
        for (Vehicle vehicle : vehicles) {
            int tx = vehicle.getTileXUnderPos(player.builder().getX() + player.builder().getWidth ()/2);
            int ty = vehicle.getTileYUnderPos(player.builder().getY() + player.builder().getHeight()/2);
            if (tx >= 1 && tx < vehicle.WIDTH() - 1 &&
                ty >= 1 && ty < vehicle.HEIGHT() - 1)
                if (vehicle.existsAt(tx, ty)) {
                    player.c("deleTile", "vehicle." +vehicle.getID()+ ".dele." +tx+ "." +ty);
                    break;
                }
        }

    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        renderBackgroundGradient();

        paraBack.render(gc, g);

        tileset.getSpriteSheet().startUse();
        island.render(gc, g);
        for (Vehicle vehicle : vehicles)
            vehicle.render(gc, g);
        tileset.getSpriteSheet().endUse();

        for (Player player : players)
            player.render(gc, g);

        if (currPlayer.builder().buildMode())
            renderBuilder(currPlayer.builder(), gc, g);
        g.drawString("xSpeed: " +Math.round(currPlayer.getAbsXSpeed()/32)+ " squ/igs\n" +
        		     "ySpeed: " +Math.round(currPlayer.getAbsYSpeed()/32)+ " squ/igs", 10, 100);
    }
    private void renderBuilder(Builder builder, GameContainer gc, Graphics g) {
        for (Vehicle vehicle : vehicles) {
            int tx = vehicle.getTileXUnderPos(builder.getX() + builder.getWidth ()/2);
            int ty = vehicle.getTileYUnderPos(builder.getY() + builder.getHeight()/2);
            if (tx >= 1 && tx < vehicle.WIDTH() - 1 &&
                ty >= 1 && ty < vehicle.HEIGHT() - 1)
                if (!vehicle.existsAt(tx, ty) &&
                        (vehicle.existsAt(tx    , ty - 1) ||
                         vehicle.existsAt(tx + 1, ty    ) ||
                         vehicle.existsAt(tx    , ty + 1) ||
                         vehicle.existsAt(tx - 1, ty    ))) {
                    builder.renderHighlight(gc, g, vehicle.ix() + tx*CollisionGrid.TW, vehicle.iy() + ty*CollisionGrid.TH, true);
                    break;
                } else if (vehicle.existsAt(tx, ty)) {
                    builder.renderHighlight(gc, g, vehicle.ix() + tx*CollisionGrid.TW, vehicle.iy() + ty*CollisionGrid.TH, false);
                    break;
                }
        }

    }
    private void renderBackgroundGradient() {
        long deltaX = View.window().getWidth ()/2 - island.ix() - island.getWidth ()/2;
        long deltaY = View.window().getHeight()/2 - island.iy() - island.getHeight()/2;
        float dist = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY) - SKY_GRADIENT_MINIMUM;

        View.window().getGraphics().drawString("dist: " + dist, 10, 200);

        int color = 0;
        if (dist > 0)
            color = Math.min(Math.round(dist/SKY_GRADIENT_LENGTH), 255);

        View.window().getGraphics().setBackground(skyColors[color]);
        View.window().getGraphics().flush();
    }

    public boolean updatePos() { return updatePos; }

    public View view() { return view; }

    public float  actionsPerTick()   { return actionsPerTick; }
    public float  gravity()          { return gravity; }
    public float  frictionFraction() { return frictionFraction; }
    public float  airResist()        { return airResist; }
    public Player currPlayer()       { return currPlayer; }
    public float  fuelRate()         { return fuelRate; }

    public float getX() { return -x; }
    public float getY() { return -y; }

    public int ix() { return Math.round(getX()); }
    public int iy() { return Math.round(getY()); }

    public final void c(String id, Object data) { node.c("world." +id, data); }

    public void dataChanged(String id, String data) {}
    public void intChanged(String id, int data) {}
    public void booleanChanged(String id, boolean data) {}
    public void floatChanged(String id, float data) {
        switch (id) {
            case "world.actionsPerTick":
                actionsPerTick = data;
                break;
            case "world.gravity":
                gravity = data;
                break;
            case "world.frictionFraction":
                frictionFraction = data;
                break;
            case "world.airResist":
                airResist = data;
                break;
            case "world.fuelRate":
                fuelRate = data;
                break;
        }
    }

    public boolean keyReleased(Keys keys, int key, char c) {
        return currPlayer.keyReleased(keys, key, c);
    }

    public boolean keyPressed(Keys keys, int key, char c) {
        return currPlayer.keyPressed(keys, key, c);
    }

}
