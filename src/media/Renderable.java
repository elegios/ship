/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package media;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 *
 * @author elegios
 */
public interface Renderable {

    /**
     * Renders the Renderable.
     * @param gc the GameContainer in which the current game exists
     * @param g the Graphics object the handles drawing of the game
     */
    void render(GameContainer gc, Graphics g);

}
