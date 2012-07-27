package ship.world.vehicle.tile.fuel;

import org.newdawn.slick.GameContainer;

import ship.world.vehicle.tile.Tile;

public class FuelTransport extends Tile {
    public static final int BASETILE = FuelSwitch.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 12;

    protected boolean straight;
    protected int     direction;

    public FuelTransport(int x, int y, boolean straight, int direction) {
        super(x, y, BASETILE + direction + 2, STDMASS, true);

        this.straight  = straight;
        this.direction = direction;
    }
    protected FuelTransport(int x, int y, int tile, float mass, boolean collide, boolean render) {
        super(x, y, tile, mass, collide);
    }

    public boolean fuelFrom(int direction, float amount) {
        int outDir = 0;

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
            Tile out = getFrom(outDir);

            if (out != null)
                out.fuelFrom((outDir + 2)%4, amount);

            return true;
        }

        return false;
    }

    public void updateEarly(GameContainer gc, int diff) {
        if (fueled())
            fuel(false);

    }

    public int tile() {
        int ret = super.tile();

        if (straight)
            ret -= 2;

        if (fueled())
            ret += 6;

        return ret;
    }

}
