package ship.netcode.meta;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * numPlayers
 */

public class NumPlayersPackage extends BasicPackage {

    private int numPlayers;

    public NumPlayersPackage(int numPlayers) {
        this(numPlayers +"");
    }

    public NumPlayersPackage(String message) {
        super(message);

        numPlayers = getNextInt();
    }

    public NumPlayersPackage() { super(); }

    public Package receivePackage(String message) { return new NumPlayersPackage(message); }

    public int getNumPlayers() { return numPlayers; }

}
