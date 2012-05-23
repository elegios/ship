package ship.netcode.meta;

import elegios.netcode.BasicPackage;

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

    public int getPlayerId  () { return playerId; }

}
