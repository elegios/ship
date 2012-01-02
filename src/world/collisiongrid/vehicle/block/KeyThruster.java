package world.collisiongrid.vehicle.block;

import org.newdawn.slick.GameContainer;

public class KeyThruster extends Block {

    private float momentum;
    private int   key;

    public KeyThruster(int x, int y, int key, float strength) {
        super(x, y, 32, 5, true, true);
        this.key = key;

        momentum = strength * 9.8f * 50;
    }

    public void update(GameContainer gc, int diff) {
        if (gc.getInput().isKeyDown(key)) {
            parent.pushBackY(-momentum * parent.world().actionsPerTick() * diff);
        }
    }

}
