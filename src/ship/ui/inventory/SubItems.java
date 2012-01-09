package ship.ui.inventory;

import media.ManagedSpriteSheet;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import ship.KeyReceiver;
import ship.View;
import ship.ui.Box;
import ship.ui.inventory.blockcreator.BlockCreator;
import ship.world.collisiongrid.CollisionGrid;

public class SubItems extends Box implements KeyReceiver, Focusable {
    public static final int WIDTH = 2;
    public static final int X_OFF = 24;
    public static final int Y_OFF = 24;
    public static final int ITEM_HEIGHT = 40;

    public static final int HIGHLIGHT_W = 60;

    private Inventory parent;

    private ManagedSpriteSheet tiles;
    private ManagedSpriteSheet highlight;

    private boolean focus;

    private int selected;

    private BlockCreator block;

    public SubItems(Inventory parent, int x, int y) throws SlickException {
        super(parent, parent.view().loader(), x, y, WIDTH, (View.window().getHeight() / Box.TH) - 1);
        this.parent = parent;

        tiles = parent.view().loader().loadManagedSpriteSheet("tiles", CollisionGrid.TW, CollisionGrid.TH);
        highlight = parent.view().loader().loadManagedSpriteSheet("sub_item_highlight", HIGHLIGHT_W, ITEM_HEIGHT);
    }

    public void updateSubs(BlockCreator block) {
        this.block = block;

        if (block != null)
            selected = Math.max(0, Math.min(selected, block.numSubs() - 1));
    }

    public void render(GameContainer gc, Graphics g) {
        if (block != null && block.numSubs() > 1) {
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
        if (focus && (block == null || block.numSubs() <= 1))
            parent.moveLeft();
    }

    @Override
    public boolean keyPressed(int key, char c) {
        if (focus) {
            switch (key) {
                case Input.KEY_UP:
                    selected--;
                    if (selected < 0)
                        selected += block.numSubs();
                    return true;

                case Input.KEY_DOWN:
                    selected++;
                    selected %= block.numSubs();
                    return true;
            }
        }

        return false;
    }

    public boolean keyReleased(int key, char c) { return false; }

}
