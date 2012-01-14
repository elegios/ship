package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.AirFuelTransport;

public class AirFuelTransportCreator extends BlockCreator {
    public static final String NAME     = "Fuel Pipe (air)";
    public static final int    BASETILE = AirFuelTransport.BASETILE + 1;
    public static final Tag[]  TAGS     = {Tags.NONCOLLIDING, Tags.FUELTRANSPORT};

    public AirFuelTransportCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public AirFuelTransport create(int sub, int x, int y) {
        if (sub <= 1)
            return new AirFuelTransport(x, y, true, sub);
        else
            return new AirFuelTransport(x, y, false, sub - 2);
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
