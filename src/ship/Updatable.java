/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship;

import org.newdawn.slick.GameContainer;

/**
 *
 * @author elegios
 */
public interface Updatable {

    /**
     * Update the Updatable. Expected to be run once per frame
     * @param gc the GameContainer in which the current game exists
     * @param diff the number of milliseconds since the last frame
     */
    void update(GameContainer gc, int diff);

}
