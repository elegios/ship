package ship.netcode.inventory;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * playerId | direction
 */

public class BuildDirectionPackage extends BasicPackage {

    private int playerId;
    private int direction;

    public BuildDirectionPackage(int playerId, int direction) {
        this(playerId +""+ DELIM +""+ direction);
    }

    public BuildDirectionPackage(String message) {
        super(message);

        playerId  = getNextInt();
        direction = getNextInt();
    }

    public BuildDirectionPackage() { super(); }

    public Package receivePackage(String message) { return new BuildDirectionPackage(message); }

    public int getPlayerId () { return playerId; }
    public int getDirection() { return direction; }

}
