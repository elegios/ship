package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Thruster;

public class ThrusterCreator extends BlockCreator {
    public static final String NAME     = "Thruster";
    public static final int    BASETILE = Thruster.BASETILE;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.FUELDRIVEN};

    public ThrusterCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public void create(int x, int y) {
        // TODO Auto-generated method stub

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
