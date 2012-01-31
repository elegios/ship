package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.vehicle.tile.Tile;
import ship.world.vehicle.tile.power.PowerSwitch;

public class PowerSwitchCreator extends TileCreator {
    public static final String NAME     = "Power switch";
    public static final int    BASETILE = PowerSwitch.BASETILE;
    public static final Tag[]  TAGS     = {Tags.NONCOLLIDING, Tags.POWERSOURCE, Tags.INTERACTIVE, Tags.POWERRELATED};

    public PowerSwitchCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Tile create(int sub, int x, int y) { return new PowerSwitch(x, y, sub); }

    @Override
    public int numSubs() { return 4; }

    @Override
    public int subTile(int sub) { return getIcon() + sub*2; }

}
