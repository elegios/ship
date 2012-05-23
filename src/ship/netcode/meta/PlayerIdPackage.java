package ship.netcode.meta;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * playerId
 */

public class PlayerIdPackage extends BasicPackage {

    private int playerId;

    public PlayerIdPackage(int playerId) {
        this(playerId +"");
    }

    public PlayerIdPackage(String message) {
        super(message);

        playerId   = getNextInt();
    }

    public PlayerIdPackage() { super(); }

    public Package receivePackage(String message) { return new PlayerIdPackage(message); }

    public int getPlayerId  () { return playerId; }

}
