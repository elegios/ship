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

    void pushX(float momentum);
    void pushY(float momentum);

    boolean collidedWithImmobileX();
    boolean collidedWithImmobileY();
    float   collisionLockX       ();
    float   collisionLockY       ();

    void collidedWithImmobileX(boolean val);
    void collidedWithImmobileY(boolean val);
    void collisionLockX       (float   val);
    void collisionLockY       (float   val);

}
