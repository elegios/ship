package ship.netcode.interaction;

import elegios.netcode.BasicPackage;

/*
 * Specification:
 * playerId | vehicleId | vehX | vehY | item | subItem
 */

public class CreateTilePackage extends BasicPackage {

    private int playerId;
    private int vehicleId;
    private int vehX;
    private int vehY;
    private int item;
    private int subItem;

    public CreateTilePackage(int playerId, int vehicleId, int vehX, int vehY, int item, int subItem) {
        append(playerId +"");
        append(vehicleId +"");
        append(vehX +"");
        append(vehY +"");
        append(item +"");
        append(subItem +"");

        this.playerId   = playerId;
        this.vehicleId  = vehicleId;
        this.vehX       = vehX;
        this.vehY       = vehY;
        this.item       = item;
        this.subItem    = subItem;
    }

    public CreateTilePackage(String message) {
        super(message);


    }

    public CreateTilePackage() { super(); }

    public int getPlayerId () { return  playerId; }
    public int getVehicleId() { return vehicleId; }
    public int getVehX     () { return      vehX; }
    public int getVehY     () { return      vehY; }
    public int getItem     () { return      item; }
    public int getSubItem  () { return   subItem; }

}
