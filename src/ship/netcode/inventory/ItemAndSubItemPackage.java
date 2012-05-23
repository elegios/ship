package ship.netcode.inventory;

import elegios.netcode.BasicPackage;

/*
 * Specification:
 * playerId | item | subItem
 */

public class ItemAndSubItemPackage extends BasicPackage {

    private int playerId;
    private int item;
    private int subItem;

    public ItemAndSubItemPackage(int playerId, int item, int subItem) {
        this(playerId +""+ DELIM +""+ item +""+ DELIM +""+ subItem);
    }

    public ItemAndSubItemPackage(String message) {
        super(message);

        playerId = getNextInt();
        item     = getNextInt();
        subItem  = getNextInt();
    }

    public ItemAndSubItemPackage() { super(); }

    public int getPlayerId() { return playerId; }
    public int getItem    () { return     item; }
    public int getSubItem () { return  subItem; }

}
