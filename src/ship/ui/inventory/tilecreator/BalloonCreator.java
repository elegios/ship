package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.vehicle.tile.Balloon;
import ship.world.vehicle.tile.Tile;

public class BalloonCreator extends TileCreator {
    public static final String NAME     = "Balloon";
    public static final int    BASETILE = Balloon.BASETILE;
    public static final Tag[]  TAGS     = {Tags.COLLIDING};

    public BalloonCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Tile create(int sub, int x, int y) {
        return new Balloon(x, y);
    }

    public int numSubs()        { return 1;         }
    public int subTile(int sub) { return getIcon(); }

}
