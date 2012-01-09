package ship.ui.inventory;

import media.FontHolder;
import media.ManagedImage;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import ship.KeyReceiver;
import ship.View;
import ship.ui.Box;

public class Tags extends Box implements KeyReceiver, Focusable {
    public static final int WIDTH = 6;
    public static final int TEXT_HEIGHT = 40;
    public static final int X_OFF = 15;
    public static final int Y_OFF = 15;

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

    private FontHolder fonts;
    private int selected;

    private boolean focus;

    private ManagedImage tagsTag;

    private Inventory parent;

    public Tags(Inventory parent, int x, int y) throws SlickException {
        super(parent, parent.view().loader(), x, y, WIDTH, (View.window().getHeight() / Box.TH) - 1);
        this.parent = parent;

        fonts = parent.view().fonts();
        tagsTag = parent.view().loader().loadManagedImage("tags_tag");

        selected = 0;
    }

    public Tag getSelected() { return TAGS[selected]; }

    public void render(GameContainer gc, Graphics g) {
        super.render(gc, g);

        for (Tag tag : TAGS) {
            if (tag.getID() == selected)
                fonts.invSelected().drawString(ix() +X_OFF, iy() +tag.getID()*TEXT_HEIGHT +Y_OFF, tag.getName());
            else
                fonts.inv().drawString(ix() +X_OFF, iy() +tag.getID()*TEXT_HEIGHT +Y_OFF, tag.getName());
        }

        tagsTag.getImage().draw(ix() + getWidth() - tagsTag.getImage().getWidth(), iy() + getHeight()/2 - tagsTag.getImage().getHeight()/2);
    }

    public void setFocus(boolean val) { focus = val; }

    @Override
    public boolean keyPressed(int key, char c) {
        if (focus) {
            switch (key) {
                case Input.KEY_UP:
                    selected--;
                    if (selected < 0)
                        selected += TAGS.length;
                    parent.updateTagFilter();
                    return true;

                case Input.KEY_DOWN:
                    selected++;
                    selected %= TAGS.length;
                    parent.updateTagFilter();
                    return true;

                case Input.KEY_ENTER:
                    parent.moveRight();
                    return true;

                case Input.KEY_LEFT:
                    selected = 0;
                    parent.updateTagFilter();
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(int key, char c) {
        return false;
    }

}