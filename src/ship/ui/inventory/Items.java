package ship.ui.inventory;

import java.util.ArrayList;
import java.util.List;

import media.FontHolder;
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

public class Items extends Box implements KeyReceiver, Focusable {
    public static final int WIDTH = 8;
    public static final int X_OFF = 15;
    public static final int TEXT_X_OFF = 42;
    public static final int Y_OFF = 15;
    public static final int TEXT_Y_OFF = 5;
    public static final int ITEM_HEIGHT = 40;

    private Inventory parent;

    private FontHolder         fonts;
    private ManagedSpriteSheet tiles;

    private List<BlockCreator> items;
    private int selected;

    private boolean focus;

    public Items(Inventory parent, int x, int y, List<BlockCreator> items) throws SlickException {
        super(parent, parent.view().loader(), x, y, WIDTH, (View.window().getHeight() / Box.TH) - 1);
        this.parent = parent;

        fonts = parent.view().fonts();
        tiles = parent.view().loader().loadManagedSpriteSheet("tiles", CollisionGrid.TW, CollisionGrid.TH);

        this.items = new ArrayList<BlockCreator>(items);
    }

    public void setFocus(boolean val) { focus = val; }

    @Override
    public boolean keyPressed(int key, char c) {
        if (focus) {
            switch (key) {
                case Input.KEY_UP:
                    selected--;
                    if (selected < 0)
                        selected += items.size();
                    parent.updateSubMenu();
                    return true;

                case Input.KEY_DOWN:
                    selected++;
                    selected %= items.size();
                    parent.updateSubMenu();
                    return true;
            }
        }

        return false;
    }

    public void rebuildItemList(List<BlockCreator> itemList, Tag tag) {
        items.clear();
        for (BlockCreator tile : itemList)
            if (tile.matches(tag))
                items.add(tile);

        selected = Math.max(Math.min(items.size() - 1, selected), 0);
        parent.updateSubMenu();
    }

    public BlockCreator getSelected() {
        if (items.size() > selected)
            return items.get(selected);
        return null;
    }

    public void render(GameContainer gc, Graphics g) {
        super.render(gc, g);


        for (int i = 0; i < items.size(); i++) {
            tiles
              .getSpriteSheet()
              .getSprite(items.get(i).getIcon() % tiles.getSpriteSheet().getHorizontalCount(),
                         items.get(i).getIcon() / tiles.getSpriteSheet().getHorizontalCount())
                .draw(ix() + X_OFF, iy() + i*ITEM_HEIGHT + Y_OFF);

            if (focus && i == selected)
                fonts.invSelected().drawString(ix() +X_OFF +TEXT_X_OFF, iy() +i*ITEM_HEIGHT +Y_OFF +TEXT_Y_OFF, items.get(i).getName());
            else if (!focus && i == selected)
                fonts.invHighlight().drawString(ix() +X_OFF +TEXT_X_OFF, iy() +i*ITEM_HEIGHT +Y_OFF +TEXT_Y_OFF, items.get(i).getName());
            else
                fonts.inv().drawString(ix() +X_OFF +TEXT_X_OFF, iy() +i*ITEM_HEIGHT +Y_OFF +TEXT_Y_OFF, items.get(i).getName());
        }
    }

    @Override
    public boolean keyReleased(int key, char c) {
        return false;
    }

}
