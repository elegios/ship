/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package media;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author elegios
 */
public class ManagedImage {

    private Image image;

    private short useCount;
    private MediaLoader loader;

    private String identifier;

    public ManagedImage(String identifier, Image image, MediaLoader loader) {
        useCount = 1;

        this.loader     = loader;
        this.identifier = identifier;

        this.image = image;
    }

    /**
     * Increases the number of users of this image.
     * @return this, for chaining method calls
     */
    public ManagedImage use() {
        useCount++;

        return this;
    }

    /**
     * Decreases the number of users of this image. If no
     * users remain, the image is destroyed and released from
     * the parent MediaLoader
     * @throws SlickException
     */
    public void release() throws SlickException {
        useCount--;

        if(useCount == 0) {
            loader.release(this);
            image.destroy();
        }
    }

    /**
     * Change the Image this ManagedImage represents
     * @param image the new Image
     */
    public void setImage(Image image) {
        this.image = image;
    }
    public Image  getImage()   { return image; }
    public String identifier() { return identifier; }

}
