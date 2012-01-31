package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.vehicle.tile.Thruster;

public class ThrusterCreator extends TileCreator {
    public static final String NAME     = "Thruster";
    public static final int    BASETILE = Thruster.BASETILE;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.FUELDRIVEN, Tags.FUELRELATED};

    public ThrusterCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Thruster create(int sub, int x, int y) {
        return new Thruster(x, y, sub);
    }

    @Override
    public int numSubs() {
        return 4;
    }

    @Override
    public int subTile(int sub) {
        return getIcon() + sub;
    }

}
