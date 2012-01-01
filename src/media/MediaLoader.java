/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package media;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author elegios
 */
public class MediaLoader {

    private static final String EXT = ".png";

    private File artDir;

    private Map<String, ManagedSpriteSheet> spriteSheets;
    private Map<String, ManagedImage> images;

    private int tw;
    private int th;

    public MediaLoader(File artDir) {
        this.artDir = artDir;

        images       = new HashMap<>();
        spriteSheets = new HashMap<>();
    }

    public ManagedImage loadManagedImage(String identifier) throws SlickException {
        if (images.containsKey(identifier)) {
            return images.get(identifier).use();
        } else {
            ManagedImage image =
                    new ManagedImage(identifier,
                                     new Image(artDir.getPath() + "/" + identifier + EXT),
                                     this);
            images.put(identifier, image);
            return image;
        }
    }

    public void setSpriteSheetSize(int tw, int th) {
        this.tw = tw;
        this.th = th;
    }

    public ManagedSpriteSheet loadManagedSpriteSheet(String identifier) throws SlickException {
        if (spriteSheets.containsKey(identifier))
            return spriteSheets.get(identifier).use();
        else {
            ManagedSpriteSheet spriteSheet = new ManagedSpriteSheet(identifier,
                                                                    new SpriteSheet(artDir.getPath() + "/" + identifier + EXT, tw, th), this);
            spriteSheets.put(identifier, spriteSheet);
            return spriteSheet;
        }
    }

    public SpriteSheet loadSpriteSheet(String identifier, int tw, int th) throws SlickException {
        return new SpriteSheet(loadImage(identifier), tw, th);
    }

    public DirAnimation loadDirAnimation(String identifier, int tw, int th,
                                         int frameDuration, int xOffset, int yOffset, boolean dirs) throws SlickException {
        return new DirAnimation(loadSpriteSheet(identifier, tw, th), frameDuration, xOffset, yOffset, dirs);
    }

    public Image loadImage(String identifier) throws SlickException {
        return new Image(artDir.getPath() + "/" + identifier + EXT);
    }

    public void release(ManagedSpriteSheet spriteSheet) {
        spriteSheets.remove(spriteSheet.identifier());
    }

    public void release(ManagedImage image) {
        images.remove(image.identifier());
    }

    public void reloadAll() throws SlickException {
        for (String identifier : spriteSheets.keySet()) {
            spriteSheets.get(identifier).getSpriteSheet().destroy();
            spriteSheets.get(identifier).setSpriteSheet(new SpriteSheet(artDir.getPath() + "/" + identifier + EXT, 32, 32));
        }
        for (String identifier : images.keySet()) {
            images.get(identifier).getImage().destroy();
            images.get(identifier).setImage(new Image(artDir.getPath() + "/" + identifier + EXT));
        }
    }
}
