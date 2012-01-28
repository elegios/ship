package ship.world.collisiongrid.vehicle.tile;

import org.newdawn.slick.GameContainer;

public class Balloon extends Tile { //TODO: make these blow up when destroyed (by force)
    public static final int BASETILE = ToggleBlock.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 1;

    public static final float LIFT_STR = STDMASS + 20;

    public Balloon(int x, int y) {
        super(x, y, BASETILE, STDMASS, true, true);
    }

    public void update(GameContainer gc, int diff) {
        parent.pushY(-LIFT_STR * parent.world().gravity() * parent.world().actionsPerTick() * diff);
    }

}
