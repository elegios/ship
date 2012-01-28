package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.collisiongrid.vehicle.block.ToggleBlock;

public class ToggleBlockCreator extends BlockCreator {
    public static final String NAME     = "Door";
    public static final int    BASETILE = ToggleBlock.BASETILE;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.POWERDRIVEN, Tags.POWERRELATED, Tags.NONCOLLIDING};

    public ToggleBlockCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Block create(int sub, int x, int y) {
        return new ToggleBlock(x, y);
    }

    @Override
    public int numSubs() { return 1; }

    @Override
    public int subTile(int sub) { return getIcon(); }

}
