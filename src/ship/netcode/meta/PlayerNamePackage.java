package ship.netcode.meta;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * playerId | playerName
 */

public class PlayerNamePackage extends BasicPackage {

    private int    playerId;
    private String playerName;

    public PlayerNamePackage(int playerId, String playerName) {
        this(playerId +""+ DELIM +""+ playerName);
    }

    public PlayerNamePackage(String message) {
        super(message);

        playerId   = getNextInt();
        playerName = getNextString();
    }

    public PlayerNamePackage() { super(); }

    public Package receivePackage(String message) { return new PlayerNamePackage(message); }

    public int    getPlayerId  () { return   playerId; }
    public String getPlayerName() { return playerName; }

}
