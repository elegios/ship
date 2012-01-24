package ship.world.collisiongrid.vehicle.block.fuel;

import org.newdawn.slick.GameContainer;

import ship.world.collisiongrid.vehicle.block.Block;
import ship.world.player.Player;

public class FuelTap extends Block {
    public static final int BASETILE = AirFuelTransport.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 16;

    public static final int FUELRATE_FACTOR = 25;

    public static final float MAX_AMOUNT  = FuelTank.MAX_CONTENT/2;
    public static final float FILL_AMOUNT = MAX_AMOUNT;

    public static final float STAGE_ONE = MAX_AMOUNT / 3;
    public static final float STAGE_TWO = 2 * STAGE_ONE;

    private int direction;

    private float content;

    private int renderLevel;

    public FuelTap(int x, int y, int direction) {
        super(x, y, BASETILE + direction*4, 0, false, true);
        this.direction = direction;

        content = 0;
        renderLevel = 0;
    }

    public void update(GameContainer gc, int diff) {
        if (content > 0) {
            if (content >= STAGE_ONE) {
                if (content >= STAGE_TWO)
                    renderLevel = 3;
                else
                    renderLevel = 2;
            } else
                renderLevel = 1;

            Block out = getFrom((direction + 2) % 4);

            float amount = parent.world().fuelRate() * FUELRATE_FACTOR * diff;
            content -= amount;
            if (content < 0) {
                amount += content;
                content = 0;
            }
            c("content", content);

            if (out != null)
                out.fuelFrom(direction, amount);

        } else
            renderLevel = 0;
    }

    public int tile() {
        return super.tile() + renderLevel;
    }

    public void activate(Player player) { //TODO: add checking for whether player has enough fuel in inventory
        if (content + FILL_AMOUNT <= MAX_AMOUNT)
            c("content", content + FILL_AMOUNT);
    }

    @Override
    public void updateFloat(String id, float data) {
        if (id.equals("content"))
            content = data;
    }

}
