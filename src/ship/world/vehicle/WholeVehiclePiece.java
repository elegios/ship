package ship.world.vehicle;

import ship.world.Rectangle;
import ship.world.vehicle.tile.Tile;

public class WholeVehiclePiece implements VehiclePiece {

    private Vehicle vehicle;

    private boolean[][] collidesAt;
    private Tile[][]    tiles;

    public WholeVehiclePiece(Vehicle vehicle) {
        this.vehicle = vehicle;

        collidesAt = vehicle.getCollidesAt();
        tiles      = vehicle.getTiles();
    }

    @Override
    public boolean collideWithVehiclePieceX(VehiclePiece other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        for (int i = vehicle.leftX(); i <= vehicle.rightX(); i++)
            for (int j = vehicle.topY(); j <= vehicle.botY(); j++)
                if (collidesAt[i][j]) {
                    float fixMove = other.collideRectangleX(tiles[i][j], vehicle.getAbsXSpeed() - other.getVehicle().getAbsXSpeed());
                    if (fixMove != 0) {
                        vehicle.x += fixMove;
                        vehicle.collisionLockX(fixMove);
                        hasCollided = true;
                        if (other.collidedWithImmobileX())
                            hasCollidedWithImmobile = true;
                    }
                }

        if (hasCollidedWithImmobile)
            vehicle.collidedWithImmobileX(true);

        return hasCollided;
    }

    @Override
    public boolean collideWithVehiclePieceY(VehiclePiece other) {
        boolean hasCollidedWithImmobile = false;
        boolean hasCollided = false;

        for (int i = vehicle.leftX(); i <= vehicle.rightX(); i++)
            for (int j = vehicle.topY(); j <= vehicle.botY(); j++)
                if (collidesAt[i][j]) {
                    float fixMove = other.collideRectangleY(tiles[i][j], vehicle.getAbsYSpeed() - other.getVehicle().getAbsYSpeed());
                    if (fixMove != 0) {
                        vehicle.y += fixMove;
                        vehicle.collisionLockY(fixMove);
                        hasCollided = true;
                        if (other.collidedWithImmobileY())
                            hasCollidedWithImmobile = true;
                    }
                }

        if (hasCollidedWithImmobile)
            vehicle.collidedWithImmobileY(true);

        return hasCollided;
    }

    @Override
    public float collideRectangleX(Rectangle rect, float relXSpeed) {
        float xMod = 0;
        boolean first = true;

        while (true) {
            int i1 = (int)          (rect.getX()  + xMod - getX())/Vehicle.TW;
            int i2 = (int) Math.ceil(rect.getX2() + xMod - getX())/Vehicle.TW;
            int j1 = (int)          (rect.getY()         - getY())/Vehicle.TH;
            int j2 = (int) Math.ceil(rect.getY2()        - getY())/Vehicle.TH;

            if (collides(i2, j1) || collides(i2, j2)) { //collision to the right of self, move left
                float fixMove = getX() - rect.getX() - xMod - rect.getWidth() + i2*Vehicle.TW - Vehicle.EXTRA_MOVE;  //The weird order fixes a bug, apparently floats lose precision or something otherwise

                //if (fixMove > -Vehicle.EXTRA_MOVE) //If the move is minimal it's probably just a rounding error or something similar
                //    return xMod + fixMove;

                fixMove = vehicle.pushBackAndFixMoveX(rect, relXSpeed, fixMove, first);
                first = false;
                xMod += fixMove;

                continue;

            }

            if (collides(i1, j1) || collides(i1, j2)) { //collision to the left of self, move right
                float fixMove = getX() - rect.getX() - xMod + i1*Vehicle.TW + Vehicle.TW + Vehicle.EXTRA_MOVE;

                //if (fixMove < Vehicle.EXTRA_MOVE)
                //    return xMod + fixMove;

                fixMove = vehicle.pushBackAndFixMoveX(rect, relXSpeed, fixMove, first);
                first = false;
                xMod += fixMove;

                continue;

            }

            return xMod; //No more collisions, should be done now

        }
    }

    @Override
    public float collideRectangleY(Rectangle rect, float relYSpeed) {
        float yMod = 0;
        boolean first = true;

        while (true) {
            int i1 = (int)          (rect.getX()         - getX())/Vehicle.TW;
            int i2 = (int) Math.ceil(rect.getX2()        - getX())/Vehicle.TW;
            int j1 = (int)          (rect.getY()  + yMod - getY())/Vehicle.TH;
            int j2 = (int) Math.ceil(rect.getY2() + yMod - getY())/Vehicle.TH;

            if (collides(i1, j2) || collides(i2, j2)) { //collision below rect, move it up
                float fixMove = getY() - rect.getY() - rect.getHeight() - yMod + j2*Vehicle.TH - Vehicle.EXTRA_MOVE;  //The weird order fixes a bug, apparently floats lose precision or something otherwise

                fixMove = vehicle.pushBackAndFixMoveY(rect, relYSpeed, fixMove, first);

                //if (fixMove > -Vehicle.EXTRA_MOVE) //If the move is minimal it's probably just a rounding error or something similar
                //    return yMod + fixMove; //These are removed as it seems to work anyway. if a bug with an infinite loop occurs this might be the solution

                first = false;
                yMod += fixMove;

                continue;

            }

            if (collides(i1, j1) || collides(i2, j1)) { //collision above rect, move it down
                float fixMove = getY() - rect.getY() - yMod + j1*Vehicle.TH + Vehicle.TH + Vehicle.EXTRA_MOVE;

                //if (fixMove < Vehicle.EXTRA_MOVE)
                //    return yMod + fixMove;

                fixMove = vehicle.pushBackAndFixMoveY(rect, relYSpeed, fixMove, first);
                first = false;
                yMod += fixMove;

                continue;

            }

            return yMod; //No more collisions, should be done now

        }
    }

    /**
     * Checks if the tile at (x, y) collides. This is merely a wrapper of
     * collidesAt(x, y), with the exception that this method will return
     * false if (x, y) is outside the CollisionGrid, instead of throwing an
     * exception
     * @param x the x coordinate of the tile to be checked
     * @param y the y coordinate of the tile to be checked
     * @return true if (x, y) is within the CollisionGrid and collides, false otherwise
     */
    private boolean collides(int x, int y) {
        if (0 <= x && x < vehicle.WIDTH() &&
            0 <= y && y < vehicle.HEIGHT())
            return collidesAt[x][y];
        return false;
    }

    private float getX() { return vehicle.getX(); }
    private float getY() { return vehicle.getY(); }

    @Override
    public boolean overlaps(VehiclePiece other) {
        if (((other.getBoundX()     >= getBoundX() && other.getBoundX()     <= getBoundX2()) ||
             (other.getBoundX2() +1 >= getBoundX() && other.getBoundX2() +1 <= getBoundX2()))
            &&
            ((other.getBoundY()     >= getBoundY() && other.getBoundY()     <= getBoundY2()) ||
             (other.getBoundY2() +1 >= getBoundY() && other.getBoundY2() +1 <= getBoundY2())))
            return true;
        return false;
    }

    @Override
    public boolean overlaps(Rectangle other) {
        if (((other.getX()     >= getBoundX() && other.getX()     <= getBoundX2()) ||
             (other.getX2() +1 >= getBoundX() && other.getX2() +1 <= getBoundX2()))
            &&
            ((other.getY()  >= getBoundY() && other.getY()  <= getBoundY2()) ||
             (other.getY2() +1 >= getBoundY() && other.getY2() +1 <= getBoundY2())))
            return true;
        return false;
    }

    @Override
    public int getTileXUnderPos(float x) {
        int tx = vehicle.getTileXUnderPos(x);
        if (tx >= 1 && tx < vehicle.WIDTH())
            return tx;

        return -1;
    }

    @Override
    public int getTileYUnderPos(float y) {
        int ty = vehicle.getTileYUnderPos(y);
        if (ty >= 1 && ty < vehicle.HEIGHT())
            return ty;

        return -1;
    }

    @Override
    public float getBoundX() { return vehicle.getX() + vehicle.leftX()*Vehicle.TW; }
    @Override
    public float getBoundY() { return vehicle.getY() + vehicle.topY ()*Vehicle.TH; }

    @Override
    public float getBoundX2() { return vehicle.getX() + vehicle.rightX()*Vehicle.TW + Vehicle.TW - 1; }
    @Override
    public float getBoundY2() { return vehicle.getY() + vehicle.botY  ()*Vehicle.TH + Vehicle.TH - 1; }

    @Override
    public Vehicle getVehicle() { return vehicle; }

    @Override
    public float getAbsXSpeed() { return vehicle.getAbsXSpeed(); }

    @Override
    public float getAbsYSpeed() { return vehicle.getAbsYSpeed(); }

    @Override
    public boolean collidedWithImmobileX() { return vehicle.collidedWithImmobileX(); }

    @Override
    public boolean collidedWithImmobileY() { return vehicle.collidedWithImmobileY(); }

    @Override
    public int ix() { return vehicle.ix(); }
    @Override
    public int iy() { return vehicle.iy(); }

    @Override
    public int leftX() { return vehicle.leftX(); }
    @Override
    public int rightX() { return vehicle.rightX(); }

    @Override
    public int topY() { return vehicle.topY(); }
    @Override
    public int botY() { return vehicle.botY(); }

}
