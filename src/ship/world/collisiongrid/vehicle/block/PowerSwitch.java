package ship.world.collisiongrid.vehicle.block;

import org.newdawn.slick.GameContainer;

public class PowerSwitch extends Block {
    public static final int BASETILE = 256;

    private boolean active;

    private int direction;

    public PowerSwitch(int x, int y, int direction) {
        super(x, y, BASETILE + direction*2, 0, false, true);

        this.direction = direction;

        active = false;
    }

    public void update(GameContainer gc, int diff) {
        if (active) {
            Block out = null;
            switch (direction) {
                case Block.UP:
                    out = parent.tile(x()    , y() + 1);
                    break;

                case Block.RIGHT:
                    out = parent.tile(x() - 1, y()    );
                    break;

                case Block.DOWN:
                    out = parent.tile(x()    , y() - 1);
                    break;

                case Block.LEFT:
                    out = parent.tile(x() + 1, y()    );
                    break;
            }

            if (out != null)
                out.powerFrom(direction);
            parent.tile(x(), y() + 1).fuelFrom(Block.UP);
        }
    }

    public int tile() {
        if (active)
            return super.tile() + 1;

        return super.tile();
    }

    public void activate() { c("active", !active); }

    @Override
    public void updateBoolean(String id, boolean data) {
        switch (id) {
            case "active":
                active = data;
                break;
        }
    }

}
