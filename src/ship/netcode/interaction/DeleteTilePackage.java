package ship.netcode.interaction;

import elegios.netcode.Package;

public class DeleteTilePackage extends ActivatePackage {

    public DeleteTilePackage(int playerId, int vehicleId, int vehX, int vehY) { super(playerId, vehicleId, vehX, vehY); }
    public DeleteTilePackage(String message) { super(message); }
    public DeleteTilePackage() { super(); }

    public Package receivePackage(String message) { return new DeleteTilePackage(message); }

}
