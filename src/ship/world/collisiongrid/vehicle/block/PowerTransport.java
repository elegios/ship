package ship.world.collisiongrid.vehicle.block;

public class PowerTransport extends Block {
    public static final int BASETILE = 264;

    private boolean[] directions;

    private boolean powered;

    public PowerTransport(int x, int y, int type) {
        super(x, y, BASETILE + type, Block.STDMASS, true, true);

        setDirections(type);
    }
    protected PowerTransport(int x, int y, int tile, float mass, boolean collide, boolean render) {
        super(x, y, tile, mass, collide, render);
    }

    protected final void setDirections(int type) {
        directions = new boolean[4];

        directions[Block.UP] = type == 0 ||
                               type == 2 ||
                               type == 3 ||
                               type == 6 ||
                               type == 7 ||
                               type == 9 ||
                               type == 10;

        directions[Block.RIGHT] = type == 1 ||
                                  type == 3 ||
                                  type == 4 ||
                                  type == 6 ||
                                  type == 7 ||
                                  type == 8 ||
                                  type == 10;

        directions[Block.DOWN] = type == 0 ||
                                 type == 4 ||
                                 type == 5 ||
                                 type == 7 ||
                                 type == 8 ||
                                 type == 9 ||
                                 type == 10;

        directions[Block.LEFT] = type == 1 ||
                                 type == 2 ||
                                 type == 5 ||
                                 type == 6 ||
                                 type == 8 ||
                                 type == 9 ||
                                 type == 10;
    }

    public boolean powerFrom(int direction) {
        if (directions[direction] && !powered) {
            powered = true;

            Block block;
            if (direction != Block.UP && directions[Block.UP] && (block = parent.tile(x(), y() - 1)) != null)
                block.powerFrom(Block.DOWN);

            if (direction != Block.RIGHT && directions[Block.RIGHT] && (block = parent.tile(x() + 1, y())) != null)
                block.powerFrom(Block.LEFT);

            if (direction != Block.DOWN && directions[Block.DOWN] && (block = parent.tile(x(), y() + 1)) != null)
                block.powerFrom(Block.UP);

            if (direction != Block.LEFT && directions[Block.LEFT] && (block = parent.tile(x() - 1, y())) != null)
                block.powerFrom(Block.RIGHT);

            return true;
        }

        return false;
    }

    public int tile() {
        if (powered) {
            powered = false;
            return super.tile() + 11;
        }

        return super.tile();
    }

}
