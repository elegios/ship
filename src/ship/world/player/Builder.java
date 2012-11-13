package ship.world.player;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.netcode.ShipProtocol;
import ship.netcode.inventory.BuildDirectionPackage;
import ship.netcode.inventory.BuildModePackage;
import ship.netcode.inventory.ItemAndSubItemPackage;
import ship.ui.inventory.Inventory;
import ship.world.vehicle.Vehicle;
import ship.world.vehicle.tile.Tile;

public class Builder implements Renderable, KeyReceiver {

    private Inventory inv;

    private PlayerHolder player;

    private float   renderX;
    private float   renderY;
    private boolean renderHighlight;

    private ManagedSpriteSheet tiles;
    private boolean buildMode;
    private int item;
    private int subItem;
    private int direction;

    private ManagedSpriteSheet highlight;

    public Builder(Inventory inv, PlayerHolder player) throws SlickException {
        this.inv = inv;

        this.player = player;

        tiles     = player.world().view().loader().loadManagedSpriteSheet("tiles", Vehicle.TW, Vehicle.TH);
        highlight = player.world().view().loader().loadManagedSpriteSheet("builder_highlight", Vehicle.TW, Vehicle.TH);
    }

    public void setRenderPos(Player player) {
        setRenderPos(player, 0, 0);
    }
    public void setRenderPos(Player player, float offsetX, float offsetY) {
        renderX = player.getX() + getModX(false) + offsetX;
        renderY = player.getY() + getModY(false) + offsetY;
    }

    public void dontRenderHighlight() { renderHighlight = false; }

    @Override
    public void render(GameContainer gc, Graphics g) {
        if (buildMode) {
            if (player.world().currPlayerHolder() == player) {
                if (inv.getSelectedTile() != null) {
                    int tileX = inv.getSelectedSubTile()%tiles.getSpriteSheet().getHorizontalCount();
                    int tileY = inv.getSelectedSubTile()/tiles.getSpriteSheet().getHorizontalCount();

                    tiles.getSpriteSheet().getSprite(tileX, tileY).draw(ix(renderX), iy(renderY));
                }

                if (renderHighlight)
                    player.world().renderBuilder(player, gc, g);
                renderHighlight = true;

            } else if (item != -1) {
                int tileX = inv.getBlockAt(item).subTile(subItem)%tiles.getSpriteSheet().getHorizontalCount();
                int tileY = inv.getBlockAt(item).subTile(subItem)/tiles.getSpriteSheet().getHorizontalCount();

                tiles.getSpriteSheet().getSprite(tileX, tileY).draw(ix(renderX), iy(renderY));
            }
        }
    }

    public void renderHighlight(GameContainer gc, Graphics g, int x, int y, boolean create) {
        g.clearClip();

        if (create)
            highlight.getSpriteSheet().getSprite(0, 0).draw(x, y);
        else
            highlight.getSpriteSheet().getSprite(1, 0).draw(x, y);
    }

    public boolean buildMode() { return buildMode; }

    public float getModX() { return getModX(true); }
    public float getModX(boolean buildPos) {
        switch (direction) {
            case Tile.UP:
            case Tile.DOWN:
                return - 1;

            case Tile.RIGHT:
                if (buildPos)
                    return player.getPlayer().getWidth() + Vehicle.TW/2;
                else
                    return player.getPlayer().getWidth();

            case Tile.LEFT:
                if (buildPos)
                    return -Vehicle.TW - Vehicle.TW/2;
                else
                    return -Vehicle.TW;

            default:
                return Float.NaN;
        }
    }
    public float getModY() { return getModY(true); }
    public float getModY(boolean buildPos) {
        switch (direction) {
            case Tile.UP:
                if (buildPos)
                    return -Vehicle.TH - Vehicle.TH/2;
                else
                    return -Vehicle.TH;

            case Tile.RIGHT:
            case Tile.LEFT:
                return - 1;

            case Tile.DOWN:
                if (buildPos)
                    return player.getPlayer().getHeight() + Vehicle.TH/2;
                else
                    return player.getPlayer().getHeight();

            default:
                return Float.NaN;
        }
    }

    public int ix(float x) { return Math.round(player.world().getX() + x); }
    public int iy(float y) { return Math.round(player.world().getY() + y); }

    public int getWidth()  { return Vehicle.TW; }
    public int getHeight() { return Vehicle.TH; }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == keys.buildUp()) {
            ensureBuildMode();
            direction = Tile.UP;

            sendDirection();

            return true;

        } if (key == keys.buildRight()) {
            ensureBuildMode();
            direction = Tile.RIGHT;

            sendDirection();

            return true;

        } if (key == keys.buildDown()) {
            ensureBuildMode();
            direction = Tile.DOWN;

            sendDirection();

            return true;

        } if (key == keys.buildLeft()) {
            ensureBuildMode();
            direction = Tile.LEFT;

            sendDirection();

            return true;

        } if (key == keys.build()) {
            if (ensureBuildMode())
                player.world().buildUnderPlayerBuilder(player);
            return true;

        } if (key == keys.destroy()) {
            if (ensureBuildMode())
                player.world().destroyUnderPlayerBuilder(player);
            return true;

        } if (key == keys.buildCancel()) {
            if (buildMode)
                setBuildMode(false);
            return true;
        }

        return false;
    }

    private void sendDirection() {
        if (player.world().view().net().isOnline())
            player.world().view().net().send(ShipProtocol.BUILD_DIR, new BuildDirectionPackage(player.getID(), direction));
    }

    public boolean ensureBuildMode() {
        if (!buildMode) {
            setBuildMode(true);
            return false;
        }

        return true;
    }

    public void setBuildMode(boolean mode) {
        buildMode = mode;

        if (player.world().view().net().isOnline())
            player.world().view().net().send(ShipProtocol.BUILD_MODE, new BuildModePackage(player.getID(), buildMode));
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) { return false; }

    public void receiveBuildDirectionPackage(BuildDirectionPackage pack) {
        direction = pack.getDirection();
    }

    public void receiveBuildModePackage(BuildModePackage pack) {
        buildMode = pack.getMode();
    }

    public void receiveItemAndSubItemPackage(ItemAndSubItemPackage pack) {
        item    = pack.getItem();
        subItem = pack.getSubItem();
    }

}
