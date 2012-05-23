package ship.netcode.interaction;

import elegios.netcode.BasicPackage;

/*
 * Specification:
 * playerId | vehicleId | vehX | vehY
 */

public class ActivatePackage extends BasicPackage {

    private int playerId;
    private int vehicleId;
    private int vehX;
    private int vehY;

    public ActivatePackage(int playerId, int vehicleId, int vehX, int vehY) {
        this(playerId +""+ DELIM +""+ vehicleId +""+ DELIM +""+ vehX +""+ DELIM +""+ vehY);
    }

    public ActivatePackage(String message) {
        super(message);

        playerId  = getNextInt();
        vehicleId = getNextInt();
        vehX      = getNextInt();
        vehY      = getNextInt();
    }

    public ActivatePackage() { super(); }

    public int getPlayerId () { return  playerId; }
    public int getVehicleId() { return vehicleId; }
    public int getVehX     () { return      vehX; }
    public int getVehY     () { return      vehY; }

}
