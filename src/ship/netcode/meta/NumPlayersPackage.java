package ship.netcode.meta;

import elegios.netcode.BasicPackage;

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

    public int getNumPlayers() { return numPlayers; }

}
