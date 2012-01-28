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

    /**
     * Creates a new MediaLoader, that will load all its files
     * relative to <code>artDir</code>
     * @param artDir the directory in which all graphics are located
     */
    public MediaLoader(File artDir) {
        this.artDir = artDir;

        images       = new HashMap<>();
        spriteSheets = new HashMap<>();
    }

    /**
     * Loads and returns a ManagedImage. If a ManagedImage with the same <code>identifier</code>
     * has been loaded before, it is returned instead, after calling its use() method.
     *
     * The Image is loaded from "{artDir}/{identifier}.png"
     * @param identifier a string identifying the Image
     * @return the ManagedImage represented by <code>identifier</code>
     * @throws SlickException
     */
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

    /**
     * Loads and returns a ManagedSpriteSheet. If a ManagedSpriteSheet with the same <code>identifier</code>
     * has been loaded before, it is returned instead, after calling its use() method.
     *
     * The SpriteSheet is loaded from "{artDir}/{identifier}.png" with the tile size (tw, th)
     * @param identifier a string identifying the SpriteSheet
     * @param tw the width of a tile
     * @param th the height of a tile
     * @return the ManagedSpriteSheet represented by <code>identifier</code>
     * @throws SlickException
     */
    public ManagedSpriteSheet loadManagedSpriteSheet(String identifier, int tw, int th) throws SlickException {
        if (spriteSheets.containsKey(identifier))
            return spriteSheets.get(identifier).use();
        else {
            ManagedSpriteSheet spriteSheet = new ManagedSpriteSheet(identifier,
                                                                    new SpriteSheet(artDir.getPath() + "/" + identifier + EXT, tw, th), this);
            spriteSheets.put(identifier, spriteSheet);
            return spriteSheet;
        }
    }

    public DirAnimation loadDirAnimation(String identifier, int tw, int th,
                                         int frameDuration, int xOffset, int yOffset, boolean dirs) throws SlickException {
        return new DirAnimation(loadSpriteSheet(identifier, tw, th), frameDuration, xOffset, yOffset, dirs);
    }

    /**
     * Loads an Image, without storing it for later use through loadManagedImage.
     *
     * The Image is loaded from "{artDir}/{identifier}.png"
     * @param identifier a string representing the image
     * @return a newly loaded Image
     * @throws SlickException
     */
    public Image loadImage(String identifier) throws SlickException {
        return new Image(artDir.getPath() + "/" + identifier + EXT);
    }

    /**
     * Loads a SpriteSheet, without storing it for later use through loadManagedSpriteSheet.
     *
     * The SpriteSheet is loader from "{artDir}/{identifier}.png"
     * @param identifier a string representing the sprite sheet
     * @param tw the width of a tile
     * @param th the height of a tile
     * @return a newly loaded SpriteSheet
     * @throws SlickException
     */
    public SpriteSheet loadSpriteSheet(String identifier, int tw, int th) throws SlickException {
        return new SpriteSheet(loadImage(identifier), tw, th);
    }

    /**
     * Removes a ManagedSpriteSheet from cache. Intended only to be
     * used by the ManagedSpriteSheet.release() method.
     * @param spriteSheet the ManagedSpriteSheet to be removed
     */
    public void release(ManagedSpriteSheet spriteSheet) {
        spriteSheets.remove(spriteSheet.identifier());
    }

    /**
     * Removes a ManagedImage from cache. Intended only to be used
     * by the ManagedImage.release() method.
     * @param image the ManagedImage to be removed
     */
    public void release(ManagedImage image) {
        images.remove(image.identifier());
    }

    /**
     * Reloads all ManagedImages and ManagedSpriteSheets, making sure that
     * their graphics are up-to-date with the files from which they were loaded.
     * @throws SlickException
     */
    public void reloadAll() throws SlickException {
        for (String identifier : spriteSheets.keySet()) {
            spriteSheets.get(identifier).getSpriteSheet().destroy();
            spriteSheets.get(identifier).setSpriteSheet(new SpriteSheet(artDir.getPath() + "/" + identifier + EXT,
                                                                        spriteSheets.get(identifier).tw(),
                                                                        spriteSheets.get(identifier).th()));
        }
        for (String identifier : images.keySet()) {
            images.get(identifier).getImage().destroy();
            images.get(identifier).setImage(new Image(artDir.getPath() + "/" + identifier + EXT));
        }
    }
}
