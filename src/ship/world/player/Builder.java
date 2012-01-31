package ship.world.player;

import media.ManagedSpriteSheet;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.ui.inventory.Inventory;
import ship.world.Position;
import ship.world.vehicle.Vehicle;
import ship.world.vehicle.tile.Tile;

public class Builder implements Renderable, KeyReceiver, Position {

    private Inventory inv;

    private Player player;

    private ManagedSpriteSheet tiles;
    private boolean buildMode;
    private int item;
    private int subItem;
    private int direction;

    private ManagedSpriteSheet highlight;

    public Builder(Inventory inv, Player player) throws SlickException {
        this.inv = inv;

        this.player = player;

        tiles     = player.world().view().loader().loadManagedSpriteSheet("tiles", Vehicle.TW, Vehicle.TH);
        highlight = player.world().view().loader().loadManagedSpriteSheet("builder_highlight", Vehicle.TW, Vehicle.TH);
    }

    public String getMakeString() { return item +"."+ subItem; }

    @Override
    public void render(GameContainer gc, Graphics g) {
        if (buildMode && item != -1) {
            int tileX = inv.getBlockAt(item).subTile(subItem)%tiles.getSpriteSheet().getHorizontalCount();
            int tileY = inv.getBlockAt(item).subTile(subItem)/tiles.getSpriteSheet().getHorizontalCount();

            tiles.getSpriteSheet().getSprite(tileX, tileY).draw(ix(), iy());
        }
    }

    public void renderHighlight(GameContainer gc, Graphics g, int x, int y, boolean create) {
        if (create)
            highlight.getSpriteSheet().getSprite(0, 0).draw(x, y);
        else
            highlight.getSpriteSheet().getSprite(1, 0).draw(x, y);
    }

    public boolean buildMode() { return buildMode; }

    public float getX() {
        switch (direction) {
            case Tile.UP:
            case Tile.DOWN:
                return player.getX() - 1;

            case Tile.RIGHT:
                return player.getX2() + 1 + Vehicle.TW/2;

            case Tile.LEFT:
                return player.getX() - Vehicle.TW - Vehicle.TW/2;

            default:
                return Float.NaN;
        }
    }
    public float getY() {
        switch (direction) {
            case Tile.UP:
                return player.getY() - Vehicle.TH - Vehicle.TH/2;

            case Tile.RIGHT:
            case Tile.LEFT:
                return player.getY() - 1;

            case Tile.DOWN:
                return player.getY2() + 1 + Vehicle.TH/2;

            default:
                return Float.NaN;
        }
    }

    public int ix() { return Math.round(player.world().getX() + getX()); }
    public int iy() { return Math.round(player.world().getY() + getY()); }

    public int getWidth()  { return Vehicle.TW; }
    public int getHeight() { return Vehicle.TH; }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == keys.buildUp()) {
            if (!buildMode)
                player.c("buildMode", true);
            player.c("buildDirection", Tile.UP);
            return true;

        } if (key == keys.buildRight()) {
            if (!buildMode)
                player.c("buildMode", true);
            player.c("buildDirection", Tile.RIGHT);
            return true;

        } if (key == keys.buildDown()) {
            if (!buildMode)
                player.c("buildMode", true);
            player.c("buildDirection", Tile.DOWN);
            return true;

        } if (key == keys.buildLeft()) {
            if (!buildMode)
                player.c("buildMode", true);
            player.c("buildDirection", Tile.LEFT);
            return true;

        } if (key == keys.build()) {
            if (!buildMode)
                player.c("buildMode", true);
            else
                player.world().buildUnderPlayerBuilder(player);
            return true;

        } if (key == keys.destroy()) {
            if (!buildMode)
                player.c("buildMode", true);
            else
                player.world().destroyUnderPlayerBuilder(player);
            return true;

        } if (key == keys.buildCancel()) {
            if (buildMode)
                player.c("buildMode", false);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) { return false; }

    public void updateInt(String id, int data) {
        switch (id) {
            case "selectedItem":
                buildMode = true;
                item = data;
                break;

            case "selectedSubItem":
                buildMode = true;
                subItem = data;
                break;

            case "buildDirection":
                direction = data;
                break;
        }
    }

    public void updateBoolean(String id, boolean data) {
        switch (id) {
            case "buildMode":
                buildMode = data;
                break;
        }
    }

}
