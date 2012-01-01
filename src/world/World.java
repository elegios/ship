/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import world.island.Island;
import world.vehicle.Vehicle;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.EasyNode;

/**
 *
 * @author elegios
 */
public class World implements Position, Renderable, Updatable, ChangeListener {

    private View view;

    private EasyNode    node;

    private int x;
    private int y;

    private Island island;
    private ManagedSpriteSheet tileset;
    private Player player;
    private Vehicle vehicle;

    private float actionsPerTick;
    private float gravity;

    public World(View view) throws SlickException {
        this.view = view;

        node   = view.node();

        island = new Island(this, 0,  0, 0);
        tileset = view.loader().loadManagedSpriteSheet("tiles");
        player = new Player(this, 0, 32, 0);
        vehicle = new Vehicle(this, 0, 128, -32);

        node.addChangeListener(this);

        x = 0;
        y = 0;

        c("actionsPerTick", 1.0f/1000);
        c("gravity"       , 9.8f);
    }

    @Override
    public void update(GameContainer gc, int diff) {
        moveX(diff);
        collideX();
        moveY(diff);
        collideY();

        player.update(gc, diff);
        vehicle.update(gc, diff);

        x = Math.round(player.getX()) + player.getWidth()/2  - View.window().getWidth()/2;
        y = Math.round(player.getY()) + player.getHeight()/2 - View.window().getHeight()/2;
    }

    private void moveX(int diff) {
        player.moveX(diff);
        vehicle.moveX(diff);
    }
    private void collideX() {
        collidePlayerX(player);
        vehicle.collideWithCollisionGridX(island);
    }
    private void moveY(int diff) {
        player.moveY(diff);
        vehicle.moveY(diff);
    }
    private void collideY() {
        collidePlayerY(player);
        vehicle.collideWithCollisionGridY(island);
    }

    public void collidePlayerX(Player player) {
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

    @Override
    public void render(GameContainer gc, Graphics g) {
        g.setAntiAlias(false);

        tileset.getSpriteSheet().startUse(); //this block should also contain the vehicle render
        island.render(gc, g);
        vehicle.render(gc, g);
        tileset.getSpriteSheet().endUse();

        player.render(gc, g);

        g.drawString("player    x: " +player.getX()+      "    y: " +player.getY(), 10, 100);
    }

    public View view() { return view; }

    public float actionsPerTick() { return actionsPerTick; }
    public float gravity()        { return gravity; }

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
        }
    }

}
