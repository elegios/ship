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

//        System.out.println(identifier + "++ => " + useCount); //REMOVE

        this.loader     = loader;
        this.identifier = identifier;

        this.image = image;
    }

    public ManagedImage use() {
        useCount++;

//        System.out.println(identifier + "++ => " + useCount); //REMOVE

        return this;
    }

    public void release() throws SlickException {
        useCount--;

//        System.out.println(identifier + "-- => " + useCount); //REMOVE

        if(useCount == 0) {
            loader.release(this);
            image.destroy();
        }
    }

    public void setImage(Image image) {
        this.image = image;
    }
    public Image  getImage()   { return image; }
    public String identifier() { return identifier; }

}
