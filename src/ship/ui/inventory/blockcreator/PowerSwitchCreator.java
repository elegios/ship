package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.collisiongrid.vehicle.block.PowerSwitch;

public class PowerSwitchCreator extends BlockCreator {
    public static final String NAME     = "Power switch";
    public static final int    BASETILE = PowerSwitch.BASETILE;
    public static final Tag[]  TAGS     = {Tags.NONCOLLIDING, Tags.POWERSOURCE, Tags.INTERACTIVE, Tags.POWERRELATED};

    public PowerSwitchCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Block create(int sub, int x, int y) { return new PowerSwitch(x, y, sub); }

    @Override
    public int numSubs() { return 4; }

    @Override
    public int subTile(int sub) { return getIcon() + sub*2; }

}
