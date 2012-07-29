package ship.netcode.meta;

import elegios.netcode.BasicPackage;
import elegios.netcode.Package;

/*
 * Specification:
 * time | timeTilUpdatePos
 */

public class TimeSyncPackage extends BasicPackage {

    private int time;
    private int timeTilUpdatePos;

    public TimeSyncPackage(int time, int timeTilUpdatePos) {
        append(time             +"");
        append(timeTilUpdatePos +"");
    }

    public TimeSyncPackage(String message) {
        super(message);

        time             = getNextInt();
        timeTilUpdatePos = getNextInt();
    }

    public TimeSyncPackage() { super(); }

    public Package receivePackage(String message) { return new TimeSyncPackage(message); }

    public int getTime            () { return             time; }
    public int getTimeTilUpdatePos() { return timeTilUpdatePos; }

}
