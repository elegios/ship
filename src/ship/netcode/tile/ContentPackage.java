package ship.netcode.tile;

import elegios.netcode.BasicPackage;

/*
 * Specification
 * vehicleId | vehX | vehY | content
 */

public class ContentPackage extends BasicPackage {

    private int vehicleId;
    private int vehX;
    private int vehY;
    private float content;

    public ContentPackage(int vehicleId, int vehX, int vehY, float content) {
        append(vehicleId +"");
        append(vehX +"");
        append(vehY +"");
        appendFloat(content);

        this.vehicleId = vehicleId;
        this.vehX      = vehX;
        this.vehY      = vehY;
        this.content   = content;
    }

    public ContentPackage(String message) {
        super(message);

        vehicleId = getNextInt();
        vehX      = getNextInt();
        vehY      = getNextInt();
        content   = getNextFloat();
    }

    public ContentPackage() { super(); }

    public int   getVehicleId() { return vehicleId; }
    public int   getVehX     () { return      vehX; }
    public int   getVehY     () { return      vehY; }
    public float getContent  () { return   content; }


}
