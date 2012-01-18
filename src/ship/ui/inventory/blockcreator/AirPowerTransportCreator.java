package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.AirPowerTransport;
import ship.world.collisiongrid.vehicle.block.Block;

public class AirPowerTransportCreator extends BlockCreator {
    public static final String NAME     = "Power Pipe (air)";
    public static final int    BASETILE = AirPowerTransport.BASETILE + 1;
    public static final Tag[]  TAGS     = {Tags.NONCOLLIDING, Tags.POWERTRANSPORT, Tags.POWERRELATED};

    public AirPowerTransportCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Block create(int sub, int x, int y) {
        return new AirPowerTransport(x, y, sub);
    }

    @Override
    public int numSubs() { return 11; }

    @Override
    public int subTile(int sub) { return getIcon() + sub - 1;}

}
