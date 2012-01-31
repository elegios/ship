package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.vehicle.tile.Tile;
import ship.world.vehicle.tile.power.AirPowerTransport;

public class AirPowerTransportCreator extends TileCreator {
    public static final String NAME     = "Power Pipe (air)";
    public static final int    BASETILE = AirPowerTransport.BASETILE + 1;
    public static final Tag[]  TAGS     = {Tags.NONCOLLIDING, Tags.POWERTRANSPORT, Tags.POWERRELATED};

    public AirPowerTransportCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Tile create(int sub, int x, int y) {
        return new AirPowerTransport(x, y, sub);
    }

    @Override
    public int numSubs() { return 11; }

    @Override
    public int subTile(int sub) { return getIcon() + sub - 1;}

}
