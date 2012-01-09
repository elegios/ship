package ship.world.collisiongrid.vehicle.block;

public class FuelTransport extends Block {
    public static final int BASETILE = 226;

    protected boolean straight;
    protected int     direction;

    public FuelTransport(int x, int y, boolean straight, int direction) {
        super(x, y, BASETILE + direction + 2, STDMASS, true, true);

        this.straight  = straight;
        this.direction = direction;
    }
    protected FuelTransport(int x, int y, int tile, float mass, boolean collide, boolean render) {
        super(x, y, tile, mass, collide, render);
    }

    public boolean fuelFrom(int direction) {
        int     outDir = 0;
        fuel(false);

        if (straight) {
            if (direction == this.direction) {
                outDir = this.direction + 2;
                fuel(true);

            } else if (direction - 2 == this.direction) {
                outDir = this.direction;
                fuel(true);
            }

        } else if (direction == this.direction) {
            outDir = (direction + 3) % 4;
            fuel(true);

        } else if ((direction + 1) % 4 == this.direction) {
            outDir = this.direction;
            fuel(true);
        }

        if (fueled()) {
            switch (outDir) {
                case UP:
                    parent.tile(x(),     y() - 1).fuelFrom(DOWN);
                    break;

                case RIGHT:
                    parent.tile(x() + 1, y()    ).fuelFrom(LEFT);
                    break;

                case DOWN:
                    parent.tile(x(),     y() + 1).fuelFrom(UP);
                    break;

                case LEFT:
                    parent.tile(x() - 1, y()    ).fuelFrom(RIGHT);
                    break;
            }

            return true;
        }

        return false;
    }

    public int tile() {
        int ret = super.tile();
        if (straight)
            ret -= 2;
        if (fueled()) {
            ret += 6;
            fuel(false);
        }
        return ret;
    }

}
