package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.vehicle.tile.fuel.FuelTransport;

public class FuelTransportCreator extends TileCreator {
    public static final String NAME     = "Fuel Pipe (block)";
    public static final int    BASETILE = FuelTransport.BASETILE + 1;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.FUELTRANSPORT, Tags.FUELRELATED};

    public FuelTransportCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public FuelTransport create(int sub, int x, int y) {
        if (sub <= 1)
            return new FuelTransport(x, y, true, sub);
        else
            return new FuelTransport(x, y, false, sub - 2);

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
