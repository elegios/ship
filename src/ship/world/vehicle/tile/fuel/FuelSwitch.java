package ship.world.vehicle.tile.fuel;

import org.newdawn.slick.GameContainer;

import ship.world.player.Player;
import ship.world.vehicle.tile.Tile;

public class FuelSwitch extends Tile {
    public static final int BASETILE = 224;
    public static final int NEXTTILE = BASETILE + 2;

    private boolean active;

    public FuelSwitch(int x, int y) {
        super(x, y, BASETILE, 0, false);

        active = false;
    }

    public void update(GameContainer gc, int diff) {
        if (active && getFrom(DOWN) != null)
            getFrom(DOWN).fuelFrom(Tile.UP, 0);
    }

    public int tile() {
        if (active)
            return super.tile() + 1;
        else
            return super.tile();
    }

    public void activate(Player player) { active = !active; }

}
