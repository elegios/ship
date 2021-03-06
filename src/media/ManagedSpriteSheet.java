/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package media;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author elegios
 */
public class ManagedSpriteSheet {

    private SpriteSheet spriteSheet;

    private short useCount;
    private MediaLoader loader;

    private int tw;
    private int th;

    private String identifier;

    public ManagedSpriteSheet(String identifier, SpriteSheet spriteSheet, MediaLoader loader) {
        useCount = 1;

//        System.out.println(identifier + "++ => " + useCount); //REMOVE

        this.loader     = loader;
        this.identifier = identifier;

        this.spriteSheet = spriteSheet;

        tw = spriteSheet.getWidth()  / spriteSheet.getHorizontalCount();
        th = spriteSheet.getHeight() / spriteSheet.getVerticalCount  ();
    }

    /**
     * Increases the number of users of this sprite sheet.
     * @return this, for chaining method calls
     */
    public ManagedSpriteSheet use() {
        useCount++;

//        System.out.println(identifier + "++ => " + useCount); //REMOVE

        return this;
    }

    /**
     * Decreases the number of users of this sprite sheet. If no
     * users remain, the SpriteSheet is destroyed and released from
     * the parent MediaLoader
     * @throws SlickException
     */
    public void release() throws SlickException {
        useCount--;

//        System.out.println(identifier + "-- => " + useCount); //REMOVE

        if(useCount == 0) {
            loader.release(this);
            spriteSheet.destroy();
        }
    }

    /**
     * Change the SpriteSheet this ManagedSpriteSheet represents
     * @param spriteSheet the new SpriteSheet
     */
    public void setSpriteSheet(SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
    }
    public SpriteSheet getSpriteSheet() { return spriteSheet; }
    public String      identifier()     { return identifier; }

    /**
     * Get the width of one tile in this SpriteSheet
     * @return width of a tile
     */
    public int tw() { return tw; }
    /**
     * Get the height of one tile in this SpriteSheet
     * @return height of a tile
     */
    public int th() { return th; }

}
