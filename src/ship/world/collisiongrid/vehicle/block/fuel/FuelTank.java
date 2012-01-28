package ship.world.collisiongrid.vehicle.block.fuel;

import org.newdawn.slick.GameContainer;

import ship.world.collisiongrid.vehicle.block.Block;

public class FuelTank extends Block {
    public static final int BASETILE = FuelTap.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 10;

    public static final float MAX_CONTENT = 600;
    public static final float STAGE_SIZE  = MAX_CONTENT / 4;

    private boolean renderPowered;

    private float content;

    public FuelTank(int x, int y) {
        super(x, y, BASETILE, STDMASS, true, true);

        content = 0;

        renderPowered = false;
    }

    public boolean powerFrom(int direction) {
        if (!powered()) {
            power(true);
            renderPowered = true;

            float amount = Math.min(parent.world().fuelRate() * parent.world().view().diff(), content);
            for (int i = 0; i < 4 && amount > 0; i++) {
                Block out = getFrom(i);
                if (out != null && out.fuelFrom((i + 2) % 4, amount)) {
                    content -= amount;
                    amount = Math.min(amount, content);
                }
            }

            c("content", content);
            return true;
        }

        return false;
    }

    public void updateEarly(GameContainer gc, int diff) {
        if (powered())
            power(false);
    }

    public boolean fuelFrom(int direction, float amount) {
        if (content + amount <= MAX_CONTENT) {
            content += amount;

            c("content", content);
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

    public void updateFloat(String id, float data) {
        if (id.equals("content"))
            content = data;
    }

}
