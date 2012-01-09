/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship.world;

/**
 *
 * @author elegios
 */
public interface RelativeMovable {

    void moveX(int diff);
    void moveY(int diff);

    float getAbsXSpeed();
    float getAbsYSpeed();

    float getAbsXMove(int diff);
    float getAbsYMove(int diff);

    float getMass();

    void pushBackX(float momentum);
    void pushBackY(float momentum);

    boolean collidedWithImmobileX();
    boolean collidedWithImmobileY();

    void collidedWithImmobileX(boolean val);
    void collidedWithImmobileY(boolean val);

}
