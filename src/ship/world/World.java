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
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.netcode.ShipProtocol;
import ship.netcode.interaction.ActivatePackage;
import ship.netcode.interaction.CreateTilePackage;
import ship.netcode.interaction.DeleteTilePackage;
import ship.world.player.Builder;
import ship.world.player.Player;
import ship.world.vehicle.ImmobileVehicle;
import ship.world.vehicle.Vehicle;
import ship.world.vehicle.VehicleHolder;
import ship.world.vehicle.VehiclePiece;

/**
 *
 * @author elegios
 */
public class World implements Position, Renderable, Updatable, KeyReceiver {
    public static final int SKY_GRADIENT_MINIMUM = 220 * Vehicle.TW;
    public static final int SKY_GRADIENT_LENGTH  = 10;
    public static final int SKY_MAX_R = 62;
    public static final int SKY_MAX_G = 209;
    public static final int SKY_MAX_B = 255;

    public static final int UPDATE_POS_INTERVAL = 200;
    public static final int POS_MEMORY_COUNT = 5;

    public static final int UNPAUSE_COUNTDOWN_START = 3000;

    private View view;

    private int x;
    private int y;

    private ManagedSpriteSheet tileset;
    private static Color[] skyColors;

    private ParallaxBackground paraBack;

    private VehicleHolder island;
    private Player[] players;
    private Player currPlayer;
    private List<VehicleHolder> vehicles;

    private float actionsPerTick;
    private float gravity;
    private float frictionFraction;
    private float airResist;
    private float fuelRate;

    private boolean updatePos;
    private int     time;
    private int     timeTilUpdatePos;

    private String systemMessage;

    private boolean running;
    private int     unpauseTimer;

    public World(View view) throws SlickException {
        if (skyColors == null) {
            skyColors = new Color[256];
            for (int i = 0; i < 256; i++) {
                skyColors[i] = new Color(Math.max(SKY_MAX_R - i, 0), Math.max(SKY_MAX_G - i, 0), Math.max(SKY_MAX_B - i, 0));
            }
        }

        this.view = view;

        paraBack = new ParallaxBackground(this);

        x = 0;
        y = 0;

        actionsPerTick   = 1.0f/1000;
        gravity          = 9.8f * 50;
        frictionFraction = 0.6f/100;
        airResist        = 0.2f;
        fuelRate         = 1.0f/1000;

        tileset = view.loader().loadManagedSpriteSheet("tiles", Vehicle.TW, Vehicle.TH);

        island  = new VehicleHolder(new ImmobileVehicle(this, 0,  0.5f, 0.4f));
        vehicles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Vehicle vehicle = new Vehicle(this, i, island.getVehicle().getWidth()/2 + 128 +i*400, -32);
            vehicle.generateStandardVehicle();
            VehicleHolder holder = new VehicleHolder(vehicle);
            vehicle.setVehicleHolder(holder);
            vehicles.add(holder);
        }

        players = new Player[view.numPlayers()];
        for (int i = 0; i < view.numPlayers(); i++)
            players[i] = new Player(this, i, island.getVehicle().getWidth()/2 + 32, -100 - i*10);
        currPlayer = players[view.playerId()];

        time = Integer.MIN_VALUE;

        updatePos = false;
        timeTilUpdatePos = UPDATE_POS_INTERVAL;

        running = true;
        unpauseTimer = -1;
        togglePause();
    }

    @Override
    public void update(GameContainer gc, int diff) {
        if (running) {
            for (Player player : players)
                player.updateEarly(gc, diff);

            moveX(diff);
            collideX();
            moveY(diff);
            collideY();

            time += diff;

            timeTilUpdatePos -= diff;
            if (timeTilUpdatePos < 0) {
                updatePos = true;
                timeTilUpdatePos += UPDATE_POS_INTERVAL;
            } else
                updatePos = false;

            for (Player player : players)
                player.update(gc, diff);
            for (VehicleHolder vehicle : vehicles)
                vehicle.update(gc, diff);

            x = Math.round(currPlayer.getX()) + currPlayer.getWidth()/2  - View.window().getWidth()/2;
            y = Math.round(currPlayer.getY()) + currPlayer.getHeight()/2 - View.window().getHeight()/2;

        } else {
            if (unpauseTimer >= 0) {
                unpauseTimer -= diff;
                if (unpauseTimer > 0)
                    systemMessage("Resuming in " +Math.ceil(((float) unpauseTimer) / 1000)+ " seconds.");
                else {
                    time             -= unpauseTimer;
                    timeTilUpdatePos += unpauseTimer;
                    unpauseTimer      = -1;
                    togglePause();
                }
            }
        }
    }

    /**
     * Moves all movable objects horizontally.
     *
     * This method should be called exactly once per frame,
     * with one call to collideY following shortly thereafter.
     * @param diff the time since the last frame
     */
    private void moveX(int diff) {
        for (VehicleHolder vehicle : vehicles)
            vehicle.moveX(diff);
        for (Player player : players)
            player.moveX(diff);
    }
    /**
     * When a vehicle gets an updated position from the server
     * it calls this method to make sure that all players currently
     * standing on the vehicle are moved with it. This is done
     * by letting all players know that the vehicle has moved
     * and then letting them decide whether to do anything about it.
     * @param vehicle the Vehicle that has moved
     * @param move the distance it has moved
     */
    public void relMoveX(Vehicle vehicle, float move) {
        for (Player player : players)
            player.relMoveX(vehicle, move);
    }
    /**
     * Checks for horizontal collision between all objects, moving them
     * should a collision be detected.
     */
    private void collideX() {
        for (VehicleHolder vehicle : vehicles)
            vehicle.collideWithVehicleHolderX(island);
        for (VehicleHolder vehicle : vehicles)
            collideVehicleX(vehicle);

        for (Player player : players)
            collidePlayerX(player);
    }
    /**
     * Moves all movable objects vertically.
     *
     * This method should be called exactly once per frame,
     * with one call to collideY following shortly thereafter.
     * @param diff the time since the last frame
     */
    private void moveY(int diff) {
        for (VehicleHolder vehicle : vehicles)
            vehicle.moveY(diff);
        for (Player player : players)
            player.moveY(diff);
    }
    /**
     * When a vehicle gets an updated position from the server
     * it calls this method to make sure that all players currently
     * standing on the vehicle are moved with it. This is done
     * by letting all players know that the vehicle has moved
     * and then letting them decide whether to do anything about it.
     * @param vehicle the Vehicle that has moved
     * @param move the distance it has moved
     */
    public void relMoveY(Vehicle vehicle, float move) {
        for (Player player : players)
            player.relMoveY(vehicle, move);
    }
    /**
     * Checks for vertical collision between all objects, moving them
     * should a collision be detected.
     */
    private void collideY() {
        for (VehicleHolder vehicle : vehicles)
            vehicle.collideWithVehicleHolderY(island);
        for (VehicleHolder vehicle : vehicles)
            collideVehicleY(vehicle);

        for (Player player : players)
            collidePlayerY(player);
    }

    /**
     * First collides <code>vehicle</code> with all vehicle with a higher ID,
     * redoing collision with all vehicles, including those of lower ID, should
     * a collision be detected. Those of lower ID will result in a call to
     * collideVehicleX(other), while those with a higher ID will give a
     * vehicle.collideWithVehicleX(other). This makes collisions a little
     * bit more deterministic, since lower IDs will move out of the way of
     * higher IDs if that is possible.
     * @param vehicle the Vehicle which should be tested
     */
    public void collideVehicleX(VehicleHolder vehicle) {
        for (int i = (vehicles.indexOf(vehicle) + 1); i < vehicles.size(); i++)
            if (vehicles.get(i).getID() != vehicle.getID()) {
                if (vehicles.get(i).getID() < vehicle.getID())
                    collideVehicleX(vehicles.get(i));
                else if (vehicle.collideWithVehicleHolderX(vehicles.get(i)))
                    i = -1;
                if (i == vehicles.size() - 1 && vehicle.collideWithVehicleHolderX(island))
                    i = -1;
            }
    }

    /**
     * First collides <code>vehicle</code> with all vehicles with a higher ID,
     * redoing collision with all vehicles, including those of lower ID, should
     * a collision be detected. Those of lower ID will result in a call to
     * collideVehicleY(other), while those with a higher ID will give a
     * vehicle.collideWithVehicleY(other). This makes collisions a little
     * bit more deterministic, since lower IDs will move out of the way of
     * higher IDs if that is possible.
     *
     * After that is complete collision is tested against all islands, once again
     * restarting from the lowest ID vehicle should a collision be detected.
     * @param vehicle the Vehicle which should be tested
     */
    public void collideVehicleY(VehicleHolder vehicle) {
        for (int i = (vehicles.indexOf(vehicle) + 1); i < vehicles.size(); i++) {
            if (vehicles.get(i).getID() != vehicle.getID()) {
                if (vehicles.get(i).getID() < vehicle.getID())
                    collideVehicleY(vehicles.get(i));
                else if (vehicle.collideWithVehicleHolderY(vehicles.get(i))) {
                    i = -1;
                } if (i == vehicles.size() - 1 && vehicle.collideWithVehicleHolderY(island)) {
                    i = -1;
                }
            }
        }
    }

    /**
     * Collides the <code>player</code> first with all vehicles, then with all islands,
     * moving the <code>player</code> should it be necessary.
     * @param player the Player to be checked
     */
    public void collidePlayerX(Player player) {
        for (VehicleHolder vehicle : vehicles)
            vehicle.collideWithPlayerX(player);

        island.collideWithPlayerY(player);
    }

    /**
     * Collides the <code>player</code> first with all vehicles, then with all islands,
     * moving the <code>player</code> should it be necessary.
     * @param player the Player to be checked
     */
    public void collidePlayerY(Player player) {
        for (VehicleHolder vehicle : vehicles)
            vehicle.collideWithPlayerY(player);

        island.collideWithPlayerY(player);
    }

    /**
     * Changes the player.ID.activate value to trigger an activate command
     * on the tile that <code>player<code> is overlapping in a Vehicle. Will
     * do nothing if there is no overlapping Vehicle or the overlapping
     * tile is empty.
     * @param player the player that is activating
     */
    public void activateUnderPlayer(Player player) {
        for (VehicleHolder vehicle : vehicles) {
            VehiclePiece closestPiece = vehicle.findClosestPiece(player.getX() + player.getWidth()/2, player.getY() + player.getHeight()/2);

            int vehX = closestPiece.getTileXUnderPos(player.getX() + player.getWidth ()/2);
            int vehY = closestPiece.getTileYUnderPos(player.getY() + player.getHeight()/2);

            if (vehX != -1 && vehY != -1 && vehicle.getVehicle().tile(vehX, vehY) != null) {
                if (view.net().isOnline())
                    view.net().send(ShipProtocol.ACTIVATE, new ActivatePackage(player.getID(), vehicle.getVehicle().getID(), vehX, vehY));
                if (view.net().isServer() || !view.net().isClient())
                    vehicle.getVehicle().tile(vehX, vehY).activate(player);
            }
        }

    }

    /**
     * Find and return the Vehicle with the given ID, or null if none can be found.
     * @param vehicleID the ID of the Vehicle to be returned
     * @return the Vehicle with the ID <code>vehicleID</code>
     */
    public Vehicle findVehicle(int vehicleID) {
        VehicleHolder ret = findVehicleHolder(vehicleID);
        if (ret != null)
            return ret.getVehicle();

        return null;
    }

    public VehicleHolder findVehicleHolder(int vehicleID) {
        for (VehicleHolder vehicle : vehicles)
            if (vehicle.getID() == vehicleID)
                return vehicle;

        return null;

    }

    public Player findPlayer(int playerID) {
        for (Player player : players)
            if (player.getID() == playerID)
                return player;

        return null;
    }

    /**
     * Changes the player.ID.makeTile value that will trigger the creation
     * of a tile at the point underneath the player builder. Will do nothing
     * if there is no overlapping vehicle, no block to support the construction,
     * or already a block at the given point.
     * @param player the player doing the building
     */
    public void buildUnderPlayerBuilder(Player player) {
        for (VehicleHolder vehicle : vehicles) {
            VehiclePiece closestPiece = vehicle.findClosestPiece(player.getX(), player.getY());

            int tx = closestPiece.getTileXUnderPos(player.builder().getX() + player.builder().getWidth ()/2);
            int ty = closestPiece.getTileYUnderPos(player.builder().getY() + player.builder().getHeight()/2);
            if (tx != -1 && ty != -1)
                if (!vehicle.getVehicle().existsAt(tx, ty) &&
                        (vehicle.getVehicle().existsAt(tx    , ty - 1) ||
                         vehicle.getVehicle().existsAt(tx + 1, ty    ) ||
                         vehicle.getVehicle().existsAt(tx    , ty + 1) ||
                         vehicle.getVehicle().existsAt(tx - 1, ty    ))) {
                    if (view.net().isOnline())
                        view.net().send(ShipProtocol.CREATE_TILE,
                                        new CreateTilePackage(player.getID(),
                                                              vehicle.getID(),
                                                              tx, ty,
                                                              view.inventory().getSelectedItem(),
                                                              view.inventory().getSelectedSubItem()));
                    if (view.net().isServer() || !view.net().isClient())
                        vehicle.getVehicle().addTile(view.inventory().getSelectedTile().create(view.inventory().getSelectedSubItem(), tx, ty));
                    break;
                }
        }

    }

    /**
     * Changes the player.ID.deleTile value that will trigger the
     * destruction of the tile underneath the player builder. Does
     * nothing if there is no vehicle overlapping or no tile in
     * the given position
     * @param player the player doing the destroying
     */
    public void destroyUnderPlayerBuilder(Player player) {
        for (VehicleHolder vehicle : vehicles) {
            VehiclePiece closestPiece = vehicle.findClosestPiece(player.getX(), player.getY());

            int tx = closestPiece.getTileXUnderPos(player.builder().getX() + player.builder().getWidth ()/2);
            int ty = closestPiece.getTileYUnderPos(player.builder().getY() + player.builder().getHeight()/2);
            if (tx != -1 && ty != -1)
                if (vehicle.getVehicle().existsAt(tx, ty)) {
                    if (view.net().isOnline())
                        view.net().send(ShipProtocol.DELETE_TILE, new DeleteTilePackage(player.getID(), vehicle.getID(), tx, ty));
                    if (view.net().isServer() || !view.net().isClient())
                        vehicle.getVehicle().remTile(vehicle.getVehicle().tile(tx, ty));
                    break;
                }
        }

    }

    /**
     * Removes a given Vehicle from the internal list, effectively
     * destroying it.
     * @param vehicle the Vehicle to be removed
     */
    public void removeVehicleFromList(VehicleHolder vehicle) {
        vehicles.remove(vehicle);
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        renderBackgroundGradient();

        paraBack.render(gc, g);

        tileset.getSpriteSheet().startUse();
        island.render(gc, g);
        for (VehicleHolder vehicle : vehicles)
            vehicle.render(gc, g);
        tileset.getSpriteSheet().endUse();

        for (Player player : players)
            player.render(gc, g);

        if (currPlayer.builder().buildMode())
            renderBuilder(currPlayer.builder(), gc, g);
        g.drawString("time: " +time+ "\n" +
                     "xSpeed: " +Math.round(currPlayer.getAbsXSpeed()/32)+ " squ/igs\n" +
        		     "ySpeed: " +Math.round(currPlayer.getAbsYSpeed()/32)+ " squ/igs\n\nPlayer " +view.playerId(), 10, 100);

        if (systemMessage != null)
            view.fonts().message().drawString(View.window().getWidth()/2 - view.fonts().message().getWidth(systemMessage)/2,
                                              View.window().getHeight()/4 - view.fonts().message().getHeight(systemMessage)/4,
                                              systemMessage);
    }
    /**
     * Renders the highlight of the player builder, if in overlaps a position
     * where either building or destruction can be done.
     * @param builder the Builder whose highlight will be rendered.
     * @param gc the GameContainer in which the current game exists
     * @param g the Graphics object that draws everything
     */
    private void renderBuilder(Builder builder, GameContainer gc, Graphics g) {
        for (VehicleHolder vehicle : vehicles) {
            VehiclePiece closestPiece = vehicle.findClosestPiece(builder.getX(), builder.getY());

            int tx = closestPiece.getTileXUnderPos(builder.getX() + builder.getWidth ()/2);
            int ty = closestPiece.getTileYUnderPos(builder.getY() + builder.getHeight()/2);
            if (tx != -1 && ty != -1)
                if (!vehicle.getVehicle().existsAt(tx, ty) &&
                        (vehicle.getVehicle().existsAt(tx    , ty - 1) ||
                         vehicle.getVehicle().existsAt(tx + 1, ty    ) ||
                         vehicle.getVehicle().existsAt(tx    , ty + 1) ||
                         vehicle.getVehicle().existsAt(tx - 1, ty    ))) {
                    builder.renderHighlight(gc, g, closestPiece.ix() + tx*Vehicle.TW, closestPiece.iy() + ty*Vehicle.TH, true);
                    break;
                } else if (vehicle.getVehicle().existsAt(tx, ty)) {
                    builder.renderHighlight(gc, g, closestPiece.ix() + tx*Vehicle.TW, closestPiece.iy() + ty*Vehicle.TH, false);
                    break;
                }
        }

    }
    /**
     * Renders the Background. Is no longer a gradient as that was to computationally
     * expensive, instead renders one color that is darker the further away from
     * the nearest island the player is.
     */
    private void renderBackgroundGradient() {
        long deltaX = View.window().getWidth ()/2 - island.getVehicle().ix() - island.getVehicle().getWidth ()/2;
        long deltaY = View.window().getHeight()/2 - island.getVehicle().iy() - island.getVehicle().getHeight()/2;
        float dist = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY) - SKY_GRADIENT_MINIMUM;

        View.window().getGraphics().drawString("dist: " + dist, 10, 200);

        int color = 0;
        if (dist > 0)
            color = Math.min(Math.round(dist/SKY_GRADIENT_LENGTH), 255);

        View.window().getGraphics().setBackground(skyColors[color]);
        View.window().getGraphics().flush();
    }

    private void togglePause() {
        running = !running;
        if (running) {
            systemMessage(null);
        } else
            if (view.net().isClient())
                systemMessage("Game is paused. Please wait for the server player to resume.");

            else
                systemMessage("Game is paused. Resume by pressing " +Input.getKeyName(view.keys().pause()));

    }

    /**
     * Pauses the game. This is only used for when player is a client.
     */
    public void pause() {
        running = true;
        togglePause();
    }

    public void timeSync(int time, int timeTilUpdatePos) {
        this.time             = time;
        this.timeTilUpdatePos = timeTilUpdatePos;

        systemMessage("Syncing (0/" +(ShipProtocol.PING_END - ShipProtocol.PING_START + 1));
    }

    /**
     * Starts the unpause timer. This method is used both in clients and
     * servers, but in the server the latency is always 0. Clients get
     * a calculated latency after a series of pings to make sure everyone's
     * in sync.
     * @param latency the latency between a client and the server
     */
    public void initUnpauseTimer(int latency) {
        unpauseTimer = UNPAUSE_COUNTDOWN_START - latency;
    }

    /**
     * Sets the current systemMessage. Null means that no message
     * should be shown.
     * @param message the message to be shown
     */
    public synchronized void systemMessage(String message) {
        systemMessage = message;
    }

    /**
     * Check whether objects should update their values in DataVerse.
     * This will periodically be true.
     * @return true if values should be updated, false otherwise
     */
    public boolean updatePos() { return updatePos; }

    public View view() { return view; }

    public int    time()             { return             time; }
    public float  actionsPerTick()   { return   actionsPerTick; }
    public float  gravity()          { return          gravity; }
    public float  frictionFraction() { return frictionFraction; }
    public float  airResist()        { return        airResist; }
    public Player currPlayer()       { return       currPlayer; }
    public float  fuelRate()         { return         fuelRate; }

    public List<VehicleHolder> vehicles() { return vehicles; }

    public float getX() { return -x; }
    public float getY() { return -y; }

    public int ix() { return Math.round(getX()); }
    public int iy() { return Math.round(getY()); }

    public boolean keyReleased(Keys keys, int key, char c) {
        return currPlayer.keyReleased(keys, key, c);
    }

    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == keys.pause()) {
            if (!view.net().isOnline())
                togglePause();

            else if (view.net().isServer()) {
                if (running) {
                    togglePause();
                    view.net().send(ShipProtocol.PAUSE);
                } else
                    view.net().initUnpause(time, timeTilUpdatePos);
            }

            return true;
        }

        if (running)
            return currPlayer.keyPressed(keys, key, c);
        else
            return false;
    }

}
