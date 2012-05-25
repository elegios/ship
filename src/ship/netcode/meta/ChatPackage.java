package ship.netcode.meta;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * playerId | message
 */

public class ChatPackage extends BasicPackage {

    private int playerId;
    private String message;

    public ChatPackage(int playerId, String message) {
        this(playerId +""+ DELIM +""+ message);
    }

    public ChatPackage(String message) {
        super(message);

        this.playerId = getNextInt();
        this.message  = getNextString();
    }

    public ChatPackage() { super(); }

    public Package receivePackage(String message) { return new ChatPackage(message); }

    public int    getPlayerId() { return playerId; }
    public String getMessage () { return  message; }

}
