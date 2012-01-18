package ship.control;

import org.newdawn.slick.Input;

public class Keys {

    private int up;
    private int right;
    private int down;
    private int left;

    private int activateDevice;

    private int inventory;

    private int build;
    private int buildCancel;
    private int buildUp;
    private int buildRight;
    private int buildDown;
    private int buildLeft;

    public Keys() {
        up    = Input.KEY_UP;
        right = Input.KEY_RIGHT;
        down  = Input.KEY_DOWN;
        left  = Input.KEY_LEFT;

        activateDevice = Input.KEY_SPACE;

        inventory = Input.KEY_NUMPAD0;

        build       = Input.KEY_NUMPAD4;
        buildCancel = Input.KEY_NUMPAD6;

        buildUp    = Input.KEY_NUMPAD5;
        buildRight = Input.KEY_NUMPAD3;
        buildDown  = Input.KEY_NUMPAD2;
        buildLeft  = Input.KEY_NUMPAD1;
    }

    public int up   () { return up; }
    public int right() { return right; }
    public int down () { return down; }
    public int left () { return left; }

    public int activateDevice() { return activateDevice; }

    public int inventory() { return inventory; }

    public int build      () { return build;       }
    public int buildCancel() { return buildCancel; }

    public int buildUp   () { return buildUp;    }
    public int buildRight() { return buildRight; }
    public int buildDown () { return buildDown;  }
    public int buildLeft () { return buildLeft;  }


    public void up   (int key) { up    = key; }
    public void right(int key) { right = key; }
    public void down (int key) { down  = key; }
    public void left (int key) { left  = key; }

    public void activateDevice(int key) { activateDevice = key; }

    public void inventory(int key) { inventory = key; }

    public void build      (int key) { build       = key; }
    public void buildCancel(int key) { buildCancel = key; }

    public void buildUp   (int key) { buildUp    = key; }
    public void buildRight(int key) { buildRight = key; }
    public void buildDown (int key) { buildDown  = key; }
    public void buildLeft (int key) { buildLeft  = key; }

}
