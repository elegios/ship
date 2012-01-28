/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world;

/**
 *
 * @author elegios
 */
public interface Rectangle extends Position {

    int getWidth();
    int getHeight();

    /**
     * Get the right-most coordinate of the Rectangle
     * @return the right edge of the Rectangle
     */
    float getX2();
    /**
     * Get the lower-most coordinate of the Rectangle
     * @return the bottom edge of the Rectangle
     */
    float getY2();

}
