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

    /**
     * Returns the current xSpeed, relative to the world
     * @return the current xSpeed
     */
    float getAbsXSpeed();
    /**
     * Returns the current ySpeed, relative to the world
     * @return the current ySpeed
     */
    float getAbsYSpeed();

    /**
     * Gets the number of pixels to move during <code>diff</code>
     * milliseconds
     * @param diff number of milliseconds since last frame
     * @return number of pixels to move
     */
    float getAbsXMove(int diff);
    /**
     * Gets the number of pixels to move during <code>diff</code>
     * milliseconds
     * @param diff number of milliseconds since last frame
     * @return number of pixels to move
     */
    float getAbsYMove(int diff);

    /**
     * Returns the mass of the RelativeMovable
     * @return current mass
     */
    float getMass();

    /**
     * Alter the x momentum of the RelativeMovable
     * @param momentum the amount by which x momentum should be changed
     */
    void pushX(float momentum);
    /**
     * Alter the y momentum of the RelativeMovable
     * @param momentum the amount by which y momentum should be changed
     */
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
