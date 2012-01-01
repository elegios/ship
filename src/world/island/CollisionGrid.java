/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package world.island;

import world.Rectangle;
import world.RelativeMovable;

/**
 *
 * @author elegios
 */
public interface CollisionGrid extends RelativeMovable, Rectangle {

    float collideRectangleX(Rectangle rect, float xSpeed);
    float collideRectangleY(Rectangle rect, float ySpeed);

    boolean overlaps(Rectangle other);

}
