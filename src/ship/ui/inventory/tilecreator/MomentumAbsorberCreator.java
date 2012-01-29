package ship.ui.inventory.tilecreator;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.tile.Tile;
import ship.world.collisiongrid.vehicle.tile.power.MomentumAbsorber;

public class MomentumAbsorberCreator extends TileCreator {
    public static final String NAME     = "Momentum Absorber";
    public static final int    BASETILE = MomentumAbsorber.BASETILE + 4;
    public static final Tag[]  TAGS     = {Tags.COLLIDING, Tags.POWERDRIVEN, Tags.POWERRELATED};

    public MomentumAbsorberCreator() {
        super(NAME, BASETILE, TAGS);
    }

    @Override
    public Tile create(int sub, int x, int y) {
        return new MomentumAbsorber(x, y, sub);
    }

    @Override
    public int numSubs() {
        return 5;
    }

    @Override
    public int subTile(int sub) {
        return getIcon() - 4 + sub;
    }

}
