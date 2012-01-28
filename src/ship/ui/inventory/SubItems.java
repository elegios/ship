package ship.ui.inventory;

import media.ManagedSpriteSheet;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.View;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.ui.Box;
import ship.ui.inventory.tilecreator.TileCreator;
import ship.world.collisiongrid.CollisionGrid;
import dataverse.datanode.easy.EasyNode;

public class SubItems extends Box implements KeyReceiver, Focusable {
    public static final int WIDTH = 2;
    public static final int X_OFF = 24;
    public static final int Y_OFF = 24;
    public static final int ITEM_HEIGHT = 40;

    public static final int HIGHLIGHT_W = 60;

    private Inventory parent;

    private EasyNode node;
    private int playerID;

    private ManagedSpriteSheet tiles;
    private ManagedSpriteSheet highlight;

    private boolean focus;

    private int selected;

    private TileCreator block;

    public SubItems(Inventory parent, int x, int y) throws SlickException {
        super(parent, parent.view().loader(), x, y, WIDTH, (View.window().getHeight() / Box.TH) - 1);
        this.parent = parent;

        node     = parent.view().node();
        playerID = parent.view().playerId();

        tiles = parent.view().loader().loadManagedSpriteSheet("tiles", CollisionGrid.TW, CollisionGrid.TH);
        highlight = parent.view().loader().loadManagedSpriteSheet("sub_item_highlight", HIGHLIGHT_W, ITEM_HEIGHT);
    }

    public void updateSubs(TileCreator block) {
        this.block = block;

        updateSubs();
    }
    private void updateSubs() {
        if (block != null) {
            selected = selected % block.numSubs();
            if (selected == -1)
                selected = block.numSubs() - 1;
        }

        node.c("player." +playerID+ ".selectedSubItem", selected);
    }

    public void render(GameContainer gc, Graphics g) {
        if (block != null) {
            super.render(gc, g);

            for (int i = 0; i < block.numSubs(); i++) {
                if (selected == i)
                    if (focus)
                        highlight.getSpriteSheet().getSprite(0, 0).draw(ix() + getWidth()/2 - HIGHLIGHT_W/2, iy() + i*ITEM_HEIGHT + Y_OFF - 4);
                    else
                        highlight.getSpriteSheet().getSprite(1, 0).draw(ix() + getWidth()/2 - HIGHLIGHT_W/2, iy() + i*ITEM_HEIGHT + Y_OFF - 4);
                tiles
                .getSpriteSheet()
                .getSprite(block.subTile(i) % tiles.getSpriteSheet().getHorizontalCount(),
                           block.subTile(i) / tiles.getSpriteSheet().getHorizontalCount())
                  .draw(ix() + X_OFF, iy() + i*ITEM_HEIGHT + Y_OFF);
            }
        }
    }

    public void setFocus(boolean val) {
        focus = val;
        if (focus && block == null)
            parent.moveLeft();
    }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (focus) {
            if (key == keys.up() || key == keys.buildUp()) {
                selected--;
                updateSubs();
                return true;

            } else if (key == keys.down() || key == keys.buildDown()) {
                selected++;
                updateSubs();
                return true;
            }
        }

        return false;
    }

    public boolean keyReleased(Keys keys, int key, char c) { return false; }

}
