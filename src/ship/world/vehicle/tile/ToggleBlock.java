package ship.world.vehicle.tile;

import org.newdawn.slick.GameContainer;

public class ToggleBlock extends Tile {
    public static final int BASETILE = Thruster.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 2;

    private boolean renderPowered;

    public ToggleBlock(int x, int y) {
        super(x, y, BASETILE, STDMASS, true);

        renderPowered = false;
    }

    public void updateEarly(GameContainer gc, int diff) {
        if (powered()) {
            power(false);
            parent.setCollidesAt(x(), y(), true);
        }
    }

    public boolean powerFrom(int direction) {
        if (!powered()) {
            power(true);
            renderPowered = true;

            parent.setCollidesAt(x(), y(), false);

            return true;
        }

        return false;
    }

    public int tile() {
        if (renderPowered) {
            renderPowered = false;
            return super.tile() + 1;
        }

        return super.tile();
    }

}
