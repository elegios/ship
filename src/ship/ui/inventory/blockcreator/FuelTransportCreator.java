package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.FuelTransport;

public class FuelTransportCreator extends BlockCreator {
    public static final String NAME     = "Fuel Pipe (block)";
    public static final int    BASETILE = FuelTransport.BASETILE + 1;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.FUELTRANSPORT};

    public FuelTransportCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public void create(int x, int y) {
        // TODO Auto-generated method stub

    }

    @Override
    public int numSubs() {
        return 6;
    }

    @Override
    public int subTile(int sub) {
        return getIcon() + sub - 1;
    }

}
