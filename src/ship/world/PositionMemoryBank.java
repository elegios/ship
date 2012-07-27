package ship.world;

import ship.world.vehicle.Vehicle;

public class PositionMemoryBank {

    private PositionMemory[] memories;
    private int    lastItem;

    public PositionMemoryBank(int numMemories) {
        memories = new PositionMemory[numMemories];
        lastItem = -1;
    }

    public void store(int time, float x, float y, float xSpeed, float ySpeed, Vehicle veh) {
        lastItem = (lastItem + 1) % memories.length;
        memories[lastItem] = new PositionMemory(time, x, y, xSpeed, ySpeed, veh);
    }

    public PositionMemory getClosest(int time) {
        int diff = World.UPDATE_POS_INTERVAL * 5;
        PositionMemory closestMemory = null;

        for (PositionMemory i : memories) {
            if (i != null) {
                int locDiff = Math.abs(time - i.time);

                if (locDiff < diff) {
                    diff = locDiff;
                    closestMemory = i;
                }
            }
        }

        return closestMemory;
    }

}
