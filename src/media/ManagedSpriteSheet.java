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

    private String identifier;

    public ManagedSpriteSheet(String identifier, SpriteSheet spriteSheet, MediaLoader loader) {
        useCount = 1;

//        System.out.println(identifier + "++ => " + useCount); //REMOVE

        this.loader     = loader;
        this.identifier = identifier;

        this.spriteSheet = spriteSheet;
    }

    public ManagedSpriteSheet use() {
        useCount++;

//        System.out.println(identifier + "++ => " + useCount); //REMOVE

        return this;
    }

    public void release() throws SlickException {
        useCount--;

//        System.out.println(identifier + "-- => " + useCount); //REMOVE

        if(useCount == 0) {
            loader.release(this);
            spriteSheet.destroy();
        }
    }

    public void setSpriteSheet(SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
    }
    public SpriteSheet getSpriteSheet() { return spriteSheet; }
    public String      identifier()     { return identifier; }

}
