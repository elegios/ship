package ship.world.vehicle.tile.fuel;

import org.newdawn.slick.GameContainer;

import ship.netcode.tile.ContentPackage;
import ship.world.player.Player;
import ship.world.vehicle.tile.Tile;

public class FuelTap extends Tile {
    public static final int BASETILE = AirFuelTransport.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 16;

    public static final int FUELRATE_FACTOR = 25;

    public static final float MAX_AMOUNT  = FuelTank.MAX_CONTENT/2;
    public static final float FILL_AMOUNT = MAX_AMOUNT;

    public static final float STAGE_ONE = MAX_AMOUNT / 3;
    public static final float STAGE_TWO = 2 * STAGE_ONE;

    private int direction;

    private float content;
    private float lastSentContent;

    private int renderLevel;

    public FuelTap(int x, int y, int direction) {
        super(x, y, BASETILE + direction*4, 0, false);
        this.direction = direction;

        content = 0;
        lastSentContent = 0;
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

            Tile out = getFrom((direction + 2) % 4);

            float amount = parent.world().fuelRate() * FUELRATE_FACTOR * diff;
            content -= amount;
            if (content < 0) {
                amount += content;
                content = 0;
            }

            if (out != null)
                out.fuelFrom(direction, amount);

        } else
            renderLevel = 0;

        if (parent.world().updatePos() && lastSentContent != content) {
            sendContentPackage(content);
            lastSentContent = content;
        }
    }

    public int tile() {
        return super.tile() + renderLevel;
    }

    public void activate(Player player) { //TODO: add checking for whether player has enough fuel in inventory
        if (content + FILL_AMOUNT <= MAX_AMOUNT)
            content += FILL_AMOUNT;
    }

    public void receiveContentPackage(ContentPackage pack) {
        content = pack.getContent();
    }

}
