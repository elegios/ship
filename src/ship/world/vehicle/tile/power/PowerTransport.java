package ship.world.vehicle.tile.power;

import org.newdawn.slick.GameContainer;

import ship.world.vehicle.tile.Tile;

public class PowerTransport extends Tile {
    public static final int BASETILE = PowerSwitch.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 22;

    private boolean[] directions;

    private boolean renderPowered;

    public PowerTransport(int x, int y, int type) {
        super(x, y, BASETILE + type, Tile.STDMASS, true);

        setDirections(type);

        renderPowered = false;
    }
    protected PowerTransport(int x, int y, int tile, float mass, boolean collide, boolean render) {
        super(x, y, tile, mass, collide);
    }

    protected final void setDirections(int type) {
        directions = new boolean[4];

        directions[Tile.UP] = type == 0 ||
                               type == 2 ||
                               type == 3 ||
                               type == 6 ||
                               type == 7 ||
                               type == 9 ||
                               type == 10;

        directions[Tile.RIGHT] = type == 1 ||
                                  type == 3 ||
                                  type == 4 ||
                                  type == 6 ||
                                  type == 7 ||
                                  type == 8 ||
                                  type == 10;

        directions[Tile.DOWN] = type == 0 ||
                                 type == 4 ||
                                 type == 5 ||
                                 type == 7 ||
                                 type == 8 ||
                                 type == 9 ||
                                 type == 10;

        directions[Tile.LEFT] = type == 1 ||
                                 type == 2 ||
                                 type == 5 ||
                                 type == 6 ||
                                 type == 8 ||
                                 type == 9 ||
                                 type == 10;
    }

    public boolean powerFrom(int direction) {
        if (directions[direction] && !powered()) {
            power(true);
            renderPowered = true;

            Tile block;
            if (direction != Tile.UP && directions[Tile.UP] && (block = getFrom(UP)) != null)
                block.powerFrom(Tile.DOWN);

            if (direction != Tile.RIGHT && directions[Tile.RIGHT] && (block = getFrom(RIGHT)) != null)
                block.powerFrom(Tile.LEFT);

            if (direction != Tile.DOWN && directions[Tile.DOWN] && (block = getFrom(DOWN)) != null)
                block.powerFrom(Tile.UP);

            if (direction != Tile.LEFT && directions[Tile.LEFT] && (block = getFrom(LEFT)) != null)
                block.powerFrom(Tile.RIGHT);

            return true;
        }

        return false;
    }

    public void updateEarly(GameContainer gc, int diff) {
        if (powered())
            power(false);
    }

    public int tile() {
        if (renderPowered) {
            renderPowered = false;
            return super.tile() + 11;
        }

        return super.tile();
    }

}
