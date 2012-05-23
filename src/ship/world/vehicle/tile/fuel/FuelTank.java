package ship.world.vehicle.tile.fuel;

import org.newdawn.slick.GameContainer;

import ship.netcode.tile.ContentPackage;
import ship.world.vehicle.tile.Tile;

public class FuelTank extends Tile {
    public static final int BASETILE = FuelTap.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 10;

    public static final float MAX_CONTENT = 600;
    public static final float STAGE_SIZE  = MAX_CONTENT / 4;

    private boolean renderPowered;

    private float content;

    private float lastSentContent;

    public FuelTank(int x, int y) {
        super(x, y, BASETILE, STDMASS, true);

        content = 0;
        lastSentContent = 0;

        renderPowered = false;
    }

    public boolean powerFrom(int direction) {
        if (!powered()) {
            power(true);
            renderPowered = true;

            float amount = Math.min(parent.world().fuelRate() * parent.world().view().diff(), content);
            for (int i = 0; i < 4 && amount > 0; i++) {
                Tile out = getFrom(i);
                if (out != null && out.fuelFrom((i + 2) % 4, amount)) {
                    content -= amount;
                    amount = Math.min(amount, content);
                }
            }

            return true;
        }

        return false;
    }

    public void updateEarly(GameContainer gc, int diff) {
        if (powered())
            power(false);
    }

    public void update(GameContainer gc, int diff) {
        if (parent.world().updatePos() && lastSentContent != content) {
            sendContentPackage(content);
            lastSentContent = content;
        }
    }

    public void setContent(float content) {
        this.content = content;
    }

    public boolean fuelFrom(int direction, float amount) {
        if (content + amount <= MAX_CONTENT) {
            content += amount;

            return true;
        }

        return false;
    }

    public int tile() {
        if (renderPowered) {
            renderPowered = false;
            return super.tile() + Math.round(content/STAGE_SIZE) + 5;
        } else
            return super.tile() + Math.round(content/STAGE_SIZE);
    }

    public void receiveContentPackage(ContentPackage pack) {
        content = pack.getContent();
    }

}
