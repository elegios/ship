package ship.ui.inventory;

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

    public static final Tag[] TAGS = new Tag[] { new Tag(0, "All"),
                                                 new Tag(1, "Colliding"),
                                                 new Tag(2, "Noncolliding"),
                                                 new Tag(3, "Fueldriven"),
                                                 new Tag(4, "Fuelgenerating"),
                                                 new Tag(5, "Fueltransport"),
                                                 new Tag(6, "Powerdriven"),
                                                 new Tag(7, "Powergenerating"),
                                                 new Tag(8, "Powertransport"),
                                                 new Tag(9, "Interactive"),
                                                 new Tag(10, "Containers")
                                               };

    public static final Tag ALL             = TAGS[0];
    public static final Tag COLLIDING       = TAGS[1];
    public static final Tag NONCOLLIDING    = TAGS[2];
    public static final Tag FUELDRIVEN      = TAGS[3];
    public static final Tag FUELGENERATING  = TAGS[4];
    public static final Tag FUELTRANSPORT   = TAGS[5];
    public static final Tag POWERDRIVEN     = TAGS[6];
    public static final Tag POWERGENERATING = TAGS[7];
    public static final Tag POWERTRANSPORT  = TAGS[8];
    public static final Tag INTERACTIVE     = TAGS[9];
    public static final Tag CONTAINERS      = TAGS[10];


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

    public Tags(Inventory parent, int x, int y) throws SlickException {
        super(parent, parent.view().loader(), x, y, WIDTH, (View.window().getHeight() / Box.TH) - 1);
        this.parent = parent;

        fonts     = parent.view().fonts();
        highlight = parent.view().loader().loadManagedSpriteSheet("tag_highlight", HIGHLIGHT_W, TEXT_HEIGHT);

        selected = 0;
    }

    public Tag getSelected() { return TAGS[selected]; }

    public void render(GameContainer gc, Graphics g) {
        super.render(gc, g);

        for (Tag tag : TAGS) {
            if (tag.getID() == selected) {
                if (focus)
                    highlight.getSpriteSheet().getSprite(0, 0).draw(ix() + X_OFF - 5, iy() + tag.getID()*TEXT_HEIGHT +Y_OFF - 5);
                else
                    highlight.getSpriteSheet().getSprite(1, 0).draw(ix() + X_OFF - 5, iy() + tag.getID()*TEXT_HEIGHT +Y_OFF - 5);
                fonts.invSelected().drawString(ix() +X_OFF, iy() +tag.getID()*TEXT_HEIGHT +Y_OFF, tag.getName());
            } else {
                fonts.inv().drawString(ix() +X_OFF, iy() +tag.getID()*TEXT_HEIGHT +Y_OFF, tag.getName());
            }
        }
    }

    public void setFocus(boolean val) { focus = val; }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (focus) {
            if (key == keys.up()) {
                selected--;
                if (selected < 0)
                    selected += TAGS.length;
                parent.updateTagFilter();
                return true;
            } else if (key == keys.down()) {
                selected++;
                selected %= TAGS.length;
                parent.updateTagFilter();
                return true;
            } else if (key == keys.left()) {
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