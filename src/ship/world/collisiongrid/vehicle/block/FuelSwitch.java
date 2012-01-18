package ship.world.collisiongrid.vehicle.block;

import org.newdawn.slick.GameContainer;

public class FuelSwitch extends Block {
    public static final int BASETILE = 224;

    private boolean active;

    public FuelSwitch(int x, int y) {
        super(x, y, BASETILE, 0, false, true);

        active = false;
    }

    public void update(GameContainer gc, int diff) {
        if (active && parent.tile(x(), y() + 1) != null)
            parent.tile(x(), y() + 1).fuelFrom(Block.UP);
    }

    public int tile() {
        if (active)
            return super.tile() + 1;
        else
            return super.tile();
    }

    public void activate() { c("active", !active); }

    @Override
    public void updateBoolean(String id, boolean data) {
        switch (id) {
            case "active":
                active = data;
                break;
        }
    }

}
