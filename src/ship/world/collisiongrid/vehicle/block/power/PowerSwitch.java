package ship.world.collisiongrid.vehicle.block.power;

import org.newdawn.slick.GameContainer;

import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.player.Player;

public class PowerSwitch extends Block {
    public static final int BASETILE = 288;
    public static final int NEXTTILE = BASETILE + 8;

    private boolean active;

    private int direction;

    public PowerSwitch(int x, int y, int direction) {
        super(x, y, BASETILE + direction*2, 0, false, true);

        this.direction = direction;

        active = false;
    }

    public void update(GameContainer gc, int diff) {
        if (active) {
            Block out = getFrom((direction + 2) % 4);

            if (out != null)
                out.powerFrom(direction);
        }
    }

    public int tile() {
        if (active)
            return super.tile() + 1;

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
