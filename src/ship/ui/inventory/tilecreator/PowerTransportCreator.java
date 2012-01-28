package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.tile.Tile;
import ship.world.collisiongrid.vehicle.tile.power.PowerTransport;

public class PowerTransportCreator extends TileCreator {
    public static final String NAME     = "Power Pipe (block)";
    public static final int    BASETILE = PowerTransport.BASETILE + 1;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.POWERTRANSPORT, Tags.POWERRELATED};

    public PowerTransportCreator() {
        super(NAME, BASETILE, TAGS);
    }

    public Tile create(int sub, int x, int y) {
        return new PowerTransport(x, y, sub);
    }

    public int numSubs()        { return 11; }
    public int subTile(int sub) { return getIcon() + sub - 1; }

}
