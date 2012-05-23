package ship.netcode.interaction;

public class DeleteTilePackage extends ActivatePackage {

    public DeleteTilePackage(int playerId, int vehicleId, int vehX, int vehY) { super(playerId, vehicleId, vehX, vehY); }
    public DeleteTilePackage(String message) { super(message); }
    public DeleteTilePackage() { super(); }

}
