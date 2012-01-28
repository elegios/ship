package ship.ui.inventory;

import java.util.ArrayList;
import java.util.List;

import media.FontHolder;
import media.ManagedSpriteSheet;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.View;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.ui.Box;

public class Tags extends Box implements KeyReceiver, Focusable {

    public static final List<Tag> TAGS = new ArrayList<>();

    public static final Tag ALL             = new Tag("All");

    public static final Tag COLLIDING       = new Tag("Colliding");
    public static final Tag NONCOLLIDING    = new Tag("Noncolliding");

    public static final Tag FUELRELATED     = new Tag("Fuel related");
    public static final Tag FUELDRIVEN      = new Tag("Fuel driven");
    public static final Tag FUELSOURCE      = new Tag("Fuel source");
    public static final Tag FUELTRANSPORT   = new Tag("Fuel transport");

    public static final Tag POWERRELATED    = new Tag("Power related");
    public static final Tag POWERDRIVEN     = new Tag("Power driven");
    public static final Tag POWERSOURCE     = new Tag("Power source");
    public static final Tag POWERTRANSPORT  = new Tag("Power transport");

    public static final Tag INTERACTIVE     = new Tag("Interactive");
    public static final Tag CONTAINERS      = new Tag("Containers");


    public static final int WIDTH = 5;
    public static final int TEXT_HEIGHT = 40;
    public static final int X_OFF = 15;
    public static final int Y_OFF = 15;

    public static final int HIGHLIGHT_W = 180;

    private Inventory parent;

    private FontHolder fonts;
    private int selected;

    private boolean focus;

    private ManagedSpriteSheet highlight;

    /**
     * Creates a Tags list for use in <code>parent</code>.
     * @param parent the inventory that will use this Tags list
     * @param x the x coordinate of the new object, relative to <code>parent</code>
     * @param y the y coordinate of the new object, relative to <code>parent</code>
     * @throws SlickException
     */
    public Tags(Inventory parent, int x, int y) throws SlickException {
        super(parent, parent.view().loader(), x, y, WIDTH, (View.window().getHeight() / Box.TH) - 1);
        this.parent = parent;

        fonts     = parent.view().fonts();
        highlight = parent.view().loader().loadManagedSpriteSheet("tag_highlight", HIGHLIGHT_W, TEXT_HEIGHT);

        TAGS.add(ALL);

        TAGS.add(COLLIDING);
        TAGS.add(NONCOLLIDING);

        TAGS.add(FUELRELATED);
        TAGS.add(FUELDRIVEN);
        TAGS.add(FUELSOURCE);
        TAGS.add(FUELTRANSPORT);

        TAGS.add(POWERRELATED);
        TAGS.add(POWERDRIVEN);
        TAGS.add(POWERSOURCE);
        TAGS.add(POWERTRANSPORT);

        TAGS.add(INTERACTIVE);
        TAGS.add(CONTAINERS);

        selected = 0;
    }

    public Tag getSelected() { return TAGS.get(selected); }

    public void render(GameContainer gc, Graphics g) {
        super.render(gc, g);

        for (int i = 0; i < TAGS.size(); i++) {
            Tag tag = TAGS.get(i);
            if (i == selected) {
                if (focus)
                    highlight.getSpriteSheet().getSprite(0, 0).draw(ix() + X_OFF - 5, iy() + i*TEXT_HEIGHT +Y_OFF - 5);
                else
                    highlight.getSpriteSheet().getSprite(1, 0).draw(ix() + X_OFF - 5, iy() + i*TEXT_HEIGHT +Y_OFF - 5);
                fonts.invSelected().drawString(ix() +X_OFF, iy() +i*TEXT_HEIGHT +Y_OFF, tag.getName());
            } else {
                fonts.inv().drawString(ix() +X_OFF, iy() +i*TEXT_HEIGHT +Y_OFF, tag.getName());
            }
        }
    }

    public void setFocus(boolean val) { focus = val; }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (focus) {
            if (key == keys.up() || key == keys.buildUp()) {
                selected--;
                if (selected < 0)
                    selected += TAGS.size();
                parent.updateTagFilter();
                return true;

            } else if (key == keys.down() || key == keys.buildDown()) {
                selected++;
                selected %= TAGS.size();
                parent.updateTagFilter();
                return true;

            } else if (key == keys.left() || key == keys.buildLeft()) {
                selected = 0;
                parent.updateTagFilter();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) {
        return false;
    }

}