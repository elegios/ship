package ship.ui;

import media.ManagedSpriteSheet;
import media.MediaLoader;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.world.Position;
import ship.world.Rectangle;

public class Box implements Renderable, Position, Rectangle {
    public static final int TW = 40;
    public static final int TH = 40;

    private int x;
    private int y;

    private int width;
    private int height;

    private Position parent;

    private ManagedSpriteSheet spriteSheet;

    /**
     * Creates a new Box relative to <code>parent</code>
     * @param parent the Box will be draw relative to this Position
     * @param loader the MediaLoader responsible for the graphics of the Box
     * @param x the x coordinate of the Box, relative to <code>parent</code>
     * @param y the y coordinate of the Box, relative to <code>parent</code>
     * @param width the number of tiles this box consists of, in width
     * @param height the number of tiles this box consists of, in height
     * @throws SlickException
     */
    public Box(Position parent, MediaLoader loader, int x, int y, int width, int height) throws SlickException {
        this.parent = parent;

        this.x = x;
        this.y = y;

        this.width  = width;
        this.height = height;

        spriteSheet = loader.loadManagedSpriteSheet("ui_box", TW, TH);
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        spriteSheet.getSpriteSheet().startUse();

        //corner pieces
        spriteSheet.getSpriteSheet().renderInUse(ix()                , iy()                 , 0, 0);
        spriteSheet.getSpriteSheet().renderInUse(ix() + width*TW - TW, iy()                 , 2, 0);
        spriteSheet.getSpriteSheet().renderInUse(ix()                , iy() + height*TH - TH, 0, 2);
        spriteSheet.getSpriteSheet().renderInUse(ix() + width*TW - TW, iy() + height*TH - TH, 2, 2);

        //top
        for (int i = 1; i < width - 1; i++)
            spriteSheet.getSpriteSheet().renderInUse(ix() + i*TW, iy(), 1, 0);

        //bottom
        for (int i = 1; i < width - 1; i++)
            spriteSheet.getSpriteSheet().renderInUse(ix() + i*TW, iy() + height*TH - TH, 1, 2);

        //left
        for (int j = 1; j < height - 1; j++)
            spriteSheet.getSpriteSheet().renderInUse(ix(), iy() + j*TH, 0, 1);

        //right
        for (int j = 1; j < height - 1; j++)
            spriteSheet.getSpriteSheet().renderInUse(ix() + width*TW - TW, iy() + j*TH, 2, 1);

        //center pieces
        for (int i = 1; i < width - 1; i++)
            for (int j = 1; j < height - 1; j++)
                spriteSheet.getSpriteSheet().renderInUse(ix() + i*TW, iy() + j*TH, 1, 1);

        spriteSheet.getSpriteSheet().endUse();
    }

    public void setWidth (int width ) { this.width  = width;  }
    public void setHeight(int height) { this.height = height; }

    public float getX() { return parent.getX() + x; }
    public float getY() { return parent.getY() + y; }

    public float getX2() { return getX() + getWidth () - 1; }
    public float getY2() { return getY() + getHeight() - 1; }

    public int getWidth () { return width  * TW; }
    public int getHeight() { return height * TH; }

    public int ix() { return Math.round(getX()); }
    public int iy() { return Math.round(getY()); }

}
