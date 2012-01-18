package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.collisiongrid.vehicle.block.PowerTransport;

public class PowerTransportCreator extends BlockCreator {
    public static final String NAME     = "Power Pipe (block)";
    public static final int    BASETILE = PowerTransport.BASETILE + 1;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.POWERTRANSPORT, Tags.POWERRELATED};

    public PowerTransportCreator() {
        super(NAME, BASETILE, TAGS);
    }

    public Block create(int sub, int x, int y) {
        return new PowerTransport(x, y, sub);
    }

    public int numSubs()        { return 11; }
    public int subTile(int sub) { return getIcon() + sub - 1; }

}
