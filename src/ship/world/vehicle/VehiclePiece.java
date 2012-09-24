package ship.world.vehicle;

import ship.world.Rectangle;


public interface VehiclePiece {

    /**
     * Checks for collision with other and moves the
     * Vehicles behind the VehiclePieces as necessary.
     * The Vehicle behind the other VehiclePiece will
     * usually not be moved, with a few special cases.
     *
     * This method should not be called unless this and
     * other are overlapping, that would result in
     * decreased performance as a much more costly check
     * for collision will take place.
     * @param other the VehiclePiece for which collision should be tested
     * @return true if a collision is detected
     */
    boolean collideWithVehiclePieceX(VehiclePiece other);

    /**
     * Checks for collision with other and moves the
     * Vehicles behind the VehiclePieces as necessary.
     * The Vehicle behind the other VehiclePiece will
     * usually not be moved, with a few special cases.
     *
     * This method should not be called unless this and
     * other are overlapping, that would result in
     * decreased performance as a much more costly check
     * for collision will take place.
     * @param other the VehiclePiece for which collision should be tested
     * @return true if a collision is detected
     */
    boolean collideWithVehiclePieceY(VehiclePiece other);

    /**
     * Checks for collision between this VehiclePiece and <code>rect</code>.
     * Returns the amount of pixels that <code>rect</code> needs to move to
     * be outside the Vehicle.
     *
     * This method will call Vehicle.pushBackAndFixMoveX at least once for every collision.
     * This is to fix edge-cases, when <code>rect</code> cannot be moved for one
     * reason or another. If that is the case, pushBackAndFixMoveX will probably move
     * the Vehicle behind this VehiclePiece.
     * @param rect the rectangle to be checked for collision
     * @param relXSpeed the speed of <code>rect</code> relative to the VehiclePiece
     * @return the number of pixels <code>rect</code> needs to be moved.
     */
    float collideRectangleX(Rectangle rect, float relXSpeed);

    /**
     * Checks for collision between this VehiclePiece and <code>rect</code>.
     * Returns the amount of pixels that <code>rect</code> needs to move to
     * be outside the Vehicle.
     *
     * This method will call Vehicle.pushBackAndFixMoveY at least once for every collision.
     * This is to fix edge-cases, when <code>rect</code> cannot be moved for one
     * reason or another. If that is the case, pushBackAndFixMoveX will probably move
     * the Vehicle behind this VehiclePiece.
     * @param rect the rectangle to be checked for collision
     * @param relYSpeed the speed of <code>rect</code> relative to the VehiclePiece
     * @return the number of pixels <code>rect</code> needs to be moved.
     */
    float collideRectangleY(Rectangle rect, float relYSpeed);

    /**
     * Checks whether the small bounding box of this
     * overlaps the small bounding box of other. Note
     * that this is not the max bounding box defined
     * by getX(), getX2() and similar.
     * @param other the VehiclePiece that this might overlap
     * @return true if one corner of other is within this
     */
    boolean overlaps(VehiclePiece other);

    /**
     * Checks whether any of the corners of rect are within
     * the small bounding box of this.
     * @param rect the rect that might overlap
     * @return true if one corner or more are within this
     */
    boolean overlaps(Rectangle rect);

    /**
     * Returns the internal x coordinate of the tile that would be underneath the
     * given global x coordinate. Will return an answer even if the tile would
     * be outside the Vehicle, so be careful with the values returned from here.
     *
     * In addition, if the given internal coordinate is entirely outside of the
     * current VehiclePiece -1 will be returned
     * @param x a global x coordinate
     * @return the internal x coordinate or -1 if outside this VehiclePiece
     */
    int getTileXUnderPos(float x);

    /**
     * Returns the internal y coordinate of the tile that would be underneath the
     * given global y coordinate. Will return an answer even if the tile would
     * be outside the Vehicle, so be careful with the values returned from here.
     *
     * In addition, if the given internal coordinate is entirely outside of the
     * current VehiclePiece -1 will be returned
     * @param y a global y coordinate
     * @return the internal y coordinate or -1 if outside this VehiclePiece
     */
    int getTileYUnderPos(float y);

    float getAbsXSpeed();
    float getAbsYSpeed();

    boolean collidedWithImmobileX();
    boolean collidedWithImmobileY();

    float getBoundX();
    float getBoundY();

    float getBoundX2();
    float getBoundY2();

    /**
     * Returns the internal x-coordinate of the left-most tile that is part of
     * this VehiclePiece. This should include half-tiles.
     * @return the left-most internal x-coordinate in this VehiclePiece
     */
    int leftX();
    /**
     * Returns the internal x-coordinate of the right-most tile that is part of
     * this VehiclePiece. This should include half-tiles.
     * @return the right-most internal x-coordinate in this VehiclePiece
     */
    int rightX();
    /**
     * Returns the internal y-coordinate of the top-most tile that is part of
     * this VehiclePiece. This should include half-tiles.
     * @return the top-most internal y-coordinate in this VehiclePiece
     */
    int topY();
    /**
     * Returns the internal y-coordinate of the bottom-most tile that is part of
     * this VehiclePiece. This should include half-tiles.
     * @return the bottom-most internal y-coordinate in this VehiclePiece
     */
    int botY();

    int ix();
    int iy();

    Vehicle getVehicle();

}
