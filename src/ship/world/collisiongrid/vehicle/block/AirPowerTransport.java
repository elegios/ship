package ship.world.collisiongrid.vehicle.block;

public class AirPowerTransport extends PowerTransport {
    public static final int BASETILE = 286;

    public AirPowerTransport(int x, int y, int type) {
        super(x, y, BASETILE + type, Block.STDMASS, false, true);

        setDirections(type);
    }

}
