package ship.netcode.interaction;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification
 * id | type | boolean value
 */

public class PlayerMovementPackage extends BasicPackage {

    public static final int MOVE_RIGHT = 0;
    public static final int MOVE_LEFT  = 1;
    public static final int JUMP       = 2;

    private int     id;
    private int     type;
    private boolean value;

    public PlayerMovementPackage(int id, int type, boolean value) {
        this(id +""+ DELIM +""+ type +""+ DELIM +""+ value);
    }

    public PlayerMovementPackage(String message) {
        super(message);

        id    = getNextInt();
        type  = getNextInt();
        value = getNextBoolean();
    }

    public PlayerMovementPackage() { super(); }

    public Package receivePackage(String message) { return new PlayerMovementPackage(message); }

    public int     getId   () { return id;    }
    public int     getType () { return type;  }
    public boolean getValue() { return value; }
}
