package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.vehicle.tile.Tile;
import ship.world.vehicle.tile.fuel.FuelTank;

public class FuelTankCreator extends TileCreator {
    public static final String NAME     = "Fuel Tank";
    public static final int    BASETILE = FuelTank.BASETILE;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.FUELSOURCE, Tags.FUELDRIVEN, Tags.FUELRELATED, Tags.CONTAINERS, Tags.POWERDRIVEN, Tags.POWERRELATED};

    public FuelTankCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Tile create(int sub, int x, int y) {
        return new FuelTank(x, y);
    }

    public int numSubs()        { return 1;         }
    public int subTile(int sub) { return getIcon(); }

}
