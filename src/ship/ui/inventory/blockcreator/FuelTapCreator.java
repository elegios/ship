package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.collisiongrid.vehicle.block.fuel.FuelTap;

public class FuelTapCreator extends BlockCreator {
    public static final String NAME     = "Fuel Tap";
    public static final int    BASETILE = FuelTap.BASETILE;
    public static final Tag[]  TAGS     = {Tags.NONCOLLIDING, Tags.FUELSOURCE, Tags.INTERACTIVE, Tags.FUELRELATED, Tags.CONTAINERS};

    public FuelTapCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Block create(int sub, int x, int y) { return new FuelTap(x, y, sub); }

    @Override
    public int numSubs() { return 4; }

    @Override
    public int subTile(int sub) { return getIcon() + sub*4; }

}
