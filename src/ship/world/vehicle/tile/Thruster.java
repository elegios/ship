package ship.world.vehicle.tile;

import org.newdawn.slick.GameContainer;

public class Thruster extends Tile {
    public static final int BASETILE = 192;
    public static final int NEXTTILE = BASETILE + 20;

    public static final float STR1 = 20;
    public static final float STR2 = 60;
    public static final float STR3 = 180;
    public static final float STR4 = 540;

    private int direction;

    private int fuelLevel;

    private int renderLevel;

    public Thruster(int x, int y, int direction) {
        super(x, y, BASETILE + direction, STDMASS, true);

        this.direction = direction;
    }

    public void update(GameContainer gc, int diff) {
        if (fuelLevel > 0) {
            float strength;
            switch (fuelLevel) {
                case 1:
                    strength = STR1;
                    break;

                case 2:
                    strength = STR2;
                    break;

                case 3:
                    strength = STR3;
                    break;

                default:
                    strength = STR4;
                    break;
            }

            switch (direction) {
                case UP:
                    parent.pushY(-strength * parent.world().gravity() * parent.world().actionsPerTick() * diff);
                    break;

                case RIGHT:
                    parent.pushX( strength * parent.world().gravity() * parent.world().actionsPerTick() * diff);
                    break;

                case DOWN:
                    parent.pushY( strength * parent.world().gravity() * parent.world().actionsPerTick() * diff);
                    break;

                case LEFT:
                    parent.pushX(-strength * parent.world().gravity() * parent.world().actionsPerTick() * diff);
                    break;
            }

            renderLevel = fuelLevel;

            fuelLevel = 0;
        } else
            renderLevel = 0;
    }

    public int tile() {
        return super.tile() + renderLevel*4;
    }

    public boolean fuelFrom(int direction, float amount) { fuelLevel++; return true; }

}
