package ship.world.collisiongrid.vehicle.tile.power;

import ship.world.collisiongrid.vehicle.tile.Tile;

public class AirPowerTransport extends PowerTransport {
    public static final int BASETILE = PowerTransport.NEXTTILE;
    public static final int NEXTTILE = BASETILE + 22;

    public AirPowerTransport(int x, int y, int type) {
        super(x, y, BASETILE + type, Tile.STDMASS, false, true);

        setDirections(type);
    }

}
