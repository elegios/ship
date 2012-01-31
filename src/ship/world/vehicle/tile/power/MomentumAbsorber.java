package ship.world.vehicle.tile.power;

import org.newdawn.slick.GameContainer;

import ship.world.vehicle.tile.Tile;

public class MomentumAbsorber extends Tile {
    public static final int BASETILE = AirPowerTransport.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 12;

    public static final float ABSORB_AMOUNT = 20 * 5;

    private int direction;

    private boolean[] poweredFrom;
    private int powers;

    private boolean renderPowered;
    private boolean renderPowered2;

    /**
     * Creates a new MomentumAbsorber. It will absorb up to a maximum value of
     * momentum, assuming it is correctly assembled by the players. The direction
     * can, besides the normal directions, be 4, which means that it is the
     * centre point of a completed MomentumAbsorber.
     * @param x
     * @param y
     * @param direction
     */
    public MomentumAbsorber(int x, int y, int direction) {
        super(x, y, BASETILE + direction, STDMASS, true);

        this.direction = direction;

        poweredFrom = new boolean[4];
        powers      = 0;

        renderPowered  = false;
        renderPowered2 = false;
    }

    public void updateEarly(GameContainer gc, int diff) {
        powers = 0;

        if (direction == 4) {
            if (poweredFrom[UP] && poweredFrom[DOWN]) {
                renderPowered = true;

                float momentum = parent.getAbsYSpeed() * parent.getMass();

                if (momentum < 0)
                    momentum = Math.max(momentum, -ABSORB_AMOUNT);

                else if (momentum > 0)
                    momentum = Math.min(momentum, ABSORB_AMOUNT);

                parent.pushY(-momentum * parent.world().gravity() * parent.world().actionsPerTick() * parent.world().view().diff());
            }

            if (poweredFrom[LEFT] && poweredFrom[RIGHT]) {
                renderPowered2 = true;

                float momentum = parent.getAbsXSpeed() * parent.getMass();

                if (momentum < 0)
                    momentum = Math.max(momentum, -ABSORB_AMOUNT);

                else if (momentum > 0)
                    momentum = Math.min(momentum, ABSORB_AMOUNT);

                parent.pushX(-momentum * parent.world().gravity() * parent.world().actionsPerTick() * parent.world().view().diff());

            }

        }

        for (int i = 0; i < 4; i++)
            poweredFrom[i] = false;
    }

    public boolean powerFrom(int direction) {
        if (this.direction != 4 && direction != this.direction && !poweredFrom[direction]) {
            poweredFrom[direction] = true;
            powers++;

            if (powers == 3) {
                Tile out = getFrom(this.direction);
                renderPowered = true;
                if (out instanceof MomentumAbsorber)
                    ((MomentumAbsorber) out).momentumAbsorberCompleteFrom((this.direction + 2) % 4);
            }

            return true;
        }

        return false;
    }

    protected void momentumAbsorberCompleteFrom(int direction) {
        if (this.direction == 4)
            poweredFrom[direction] = true;
    }

    public int tile() {
        if (direction == 4 && renderPowered2) {
            renderPowered2 = false;

            if (renderPowered) {
                renderPowered = false;
                return super.tile() + 7;
            }

            return super.tile() + 6;
        }

        if (renderPowered) {
            renderPowered = false;
            return super.tile() + 5;
        }

        return super.tile();
    }

}
