/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world;

/**
 *
 * @author elegios
 */
public interface Position {

    float getX();
    float getY();

    /**
     * Get top left x coordinate on the screen in integers, for rendering
     * @return coordinate on the screen
     */
    int ix();
    /**
     * Get top left y coordinate on the screen in integers, for rendering
     * @return coordinate on the screen
     */
    int iy();

}
