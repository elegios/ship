package ship.ui.inventory.blockcreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Balloon;
import ship.world.collisiongrid.vehicle.block.Block;

public class BalloonCreator extends BlockCreator {
    public static final String NAME     = "Balloon";
    public static final int    BASETILE = Balloon.BASETILE;
    public static final Tag[]  TAGS     = {Tags.COLLIDING};

    public BalloonCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Block create(int sub, int x, int y) {
        return new Balloon(x, y);
    }

    public int numSubs()        { return 1;         }
    public int subTile(int sub) { return getIcon(); }

}
