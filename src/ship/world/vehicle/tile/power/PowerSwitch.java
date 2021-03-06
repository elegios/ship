package ship.world.vehicle.tile.power;

import org.newdawn.slick.GameContainer;

import ship.world.player.Player;
import ship.world.vehicle.tile.Tile;

public class PowerSwitch extends Tile {
    public static final int BASETILE = 288;
    public static final int NEXTTILE = BASETILE + 8;

    private boolean active;

    private int direction;

    public PowerSwitch(int x, int y, int direction) {
        super(x, y, BASETILE + direction*2, 0, false);

        this.direction = direction;

        active = false;
    }

    public void update(GameContainer gc, int diff) {
        if (active) {
            Tile out = getFrom((direction + 2) % 4);

            if (out != null)
                out.powerFrom(direction);
        }
    }

    public int tile() {
        if (active)
            return super.tile() + 1;

        return super.tile();
    }

    public void activate(Player player) { active = !active; }

}
