package ship.world.collisiongrid.vehicle.block;

public class AirFuelTransport extends FuelTransport {
    public static final int BASETILE = 238;

    public AirFuelTransport(int x, int y, boolean straight, int direction) {
        super(x, y, BASETILE + direction + 2, STDMASS, false, true);

        this.straight  = straight;
        this.direction = direction;
    }

}
