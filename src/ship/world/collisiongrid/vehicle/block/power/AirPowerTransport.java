package ship.world.collisiongrid.vehicle.block.power;

import ship.world.collisiongrid.vehicle.block.Block;

public class AirPowerTransport extends PowerTransport {
    public static final int BASETILE = PowerTransport.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 22;

    public AirPowerTransport(int x, int y, int type) {
        super(x, y, BASETILE + type, Block.STDMASS, false, true);

        setDirections(type);
    }

}
