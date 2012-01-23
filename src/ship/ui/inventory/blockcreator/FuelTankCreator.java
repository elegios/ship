package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.collisiongrid.vehicle.block.fuel.FuelTank;

public class FuelTankCreator extends BlockCreator {
    public static final String NAME     = "Fuel Tank";
    public static final int    BASETILE = FuelTank.BASETILE;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.FUELSOURCE, Tags.FUELDRIVEN, Tags.FUELRELATED, Tags.CONTAINERS, Tags.POWERDRIVEN, Tags.POWERRELATED};

    public FuelTankCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Block create(int sub, int x, int y) {
        return new FuelTank(x, y);
    }

    public int numSubs()        { return 1;         }
    public int subTile(int sub) { return getIcon(); }

}
