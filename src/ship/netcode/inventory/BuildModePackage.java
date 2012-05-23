package ship.netcode.inventory;

import elegios.netcode.BasicPackage;

public class BuildModePackage extends BasicPackage {

    private int playerId;
    private boolean mode;

    public BuildModePackage(int playerId, boolean mode) {
        this(playerId +""+ DELIM +""+ mode);
    }

    public BuildModePackage(String message) {
        super(message);

        playerId = getNextInt();
        mode     = getNextBoolean();
    }

    public BuildModePackage() { super(); }

    public int getPlayerId() { return playerId; }
    public boolean getMode() { return     mode; }

}
