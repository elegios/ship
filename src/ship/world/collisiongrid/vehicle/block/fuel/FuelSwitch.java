package ship.world.collisiongrid.vehicle.block.fuel;

import org.newdawn.slick.GameContainer;

import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.player.Player;

public class FuelSwitch extends Block {
    public static final int BASETILE = 224;

    private boolean active;

    public FuelSwitch(int x, int y) {
        super(x, y, BASETILE, 0, false, true);

        active = false;
    }

    public void update(GameContainer gc, int diff) {
        if (active && getFrom(DOWN) != null)
            getFrom(DOWN).fuelFrom(Block.UP, 0);
    }

    public int tile() {
        if (active)
            return super.tile() + 1;
        else
            return super.tile();
    }

    public void activate(Player player) { c("active", !active); }

    @Override
    public void updateBoolean(String id, boolean data) {
        switch (id) {
            case "active":
                active = data;
                break;
        }
    }

}
