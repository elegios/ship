/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world;

import java.util.ArrayList;
import java.util.List;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.KeyReceiver;
import ship.Updatable;
import ship.View;
import ship.world.collisiongrid.CollisionGrid;
import ship.world.collisiongrid.island.Island;
import ship.world.collisiongrid.vehicle.Vehicle;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.easy.EasyNode;

/**
 *
 * @author elegios
 */
public class World implements Position, Renderable, Updatable, ChangeListener, KeyReceiver {

    private View view;

    private EasyNode    node;

    private int x;
    private int y;

    private Island island;
    private ManagedSpriteSheet tileset;
    private Player[] players;
    private Player currPlayer;
    private List<Vehicle> vehicles;

    private float actionsPerTick;
    private float gravity;
    private float frictionFraction;
    private float airResist;

    private boolean updatePos;
    private int     timeTilUpdatePos;
    private int     updatePosInterval;

    public World(View view) throws SlickException {
        this.view = view;

        node = view.node();

        node.addChangeListener(this);

        x = 0;
        y = 0;

        c("actionsPerTick",   1.0f/1000);
        c("gravity",          9.8f * 50);
        c("frictionFraction",  0.3f);
        c("airResist",         0.3f);

        island  = new Island(this, 0,  0, 0);
        tileset = view.loader().loadManagedSpriteSheet("tiles", CollisionGrid.TW, CollisionGrid.TH);
        vehicles = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            vehicles.add(new Vehicle(this, i, island.getWidth()/2 + 128 +i*350, -32));

        players = new Player[view.numPlayers()];
        for (int i = 0; i < view.numPlayers(); i++)
            players[i] = new Player(this, i, island.getWidth()/2 + 32, 0);
        currPlayer = players[view.playerId()];

        updatePos = false;
        updatePosInterval = 300;
        timeTilUpdatePos = updatePosInterval;
    }

    @Override
    public void update(GameContainer gc, int diff) {
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
            if (vehicles.get(i).getID() != vehicle.getID())
                if (vehicles.get(i).getID() < vehicle.getID())
                    collideVehicleX(vehicles.get(i));
                else if (vehicle.collideWithCollisionGridX(vehicles.get(i)))
                    i = -1;
    }

    public void collideVehicleY(Vehicle vehicle) {
        for (int i = (vehicles.indexOf(vehicle) + 1); i < vehicles.size(); i++)
            if (vehicles.get(i).getID() != vehicle.getID())
                if (vehicles.get(i).getID() < vehicle.getID())
                    collideVehicleY(vehicles.get(i));
                else if (vehicle.collideWithCollisionGridY(vehicles.get(i)))
                    i = -1;
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
                    vehY >= 0 && vehY < vehicle.HEIGHT())
                    player.c("o.activate", "vehicle." +vehicle.getID()+ ".tile."
                             + vehX +"."
                             + vehY +".activate");
            }

    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        g.setAntiAlias(false);

        tileset.getSpriteSheet().startUse();
        island.render(gc, g);
        for (Vehicle vehicle : vehicles)
            vehicle.render(gc, g);
        tileset.getSpriteSheet().endUse();

        for (Player player : players)
            player.render(gc, g);

        g.drawString("player  x: " +currPlayer.getX()+ "\n" +
        		     "        y: " +currPlayer.getY()+ "\n" +
        		     "absxSpeed: " +currPlayer.getAbsXSpeed()+ "\n" +
                     "absySpeed: " +currPlayer.getAbsYSpeed()+ "\n" +
        		     "vehicle x: " +vehicles.get(0).getX() +"\n" +
        		     "        y: " +vehicles.get(0).getY() +"\n" +
        		     "absxSpeed: " +vehicles.get(0).getAbsXSpeed()+ "\n" +
        		     "absySpeed: " +vehicles.get(0).getAbsYSpeed(), 10, 100);
    }

    public boolean updatePos() { return updatePos; }

    public View view() { return view; }

    public float  actionsPerTick()   { return actionsPerTick; }
    public float  gravity()          { return gravity; }
    public float  frictionFraction() { return frictionFraction; }
    public float  airResist()        { return airResist; }
    public Player currPlayer()       { return currPlayer; }

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
        }
    }

    public boolean keyReleased(int key, char c) {
        return currPlayer.keyReleased(key, c);
    }

    public boolean keyPressed(int key, char c) {
        return currPlayer.keyPressed(key, c);
    }

}
