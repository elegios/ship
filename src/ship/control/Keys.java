package ship.control;

import org.newdawn.slick.Input;

public class Keys implements KeyReceiver {

    private int up;
    private int right;
    private int down;
    private int left;

    private int activateDevice;

    private int inventory;

    private int build;
    private int destroy;
    private int buildCancel;


    private int buildUp;
    private int buildRight;
    private int buildDown;
    private int buildLeft;

    public Keys() { //default is a somewhat neutral control scheme, except it requires numpad
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

        setDvorak(); //temporary
    }

    public void setDvorak() {
        up    = Input.KEY_COMMA;
        right = Input.KEY_E;
        down  = Input.KEY_O;
        left  = Input.KEY_A;

        activateDevice = Input.KEY_SPACE;

        inventory = Input.KEY_TAB;

        build       = Input.KEY_G;
        destroy     = Input.KEY_R;
        buildCancel = Input.KEY_S;

        buildUp    = Input.KEY_C;
        buildRight = Input.KEY_N;
        buildDown  = Input.KEY_T;
        buildLeft  = Input.KEY_H;
    }

    public void setQwerty() {
        up    = Input.KEY_W;
        right = Input.KEY_D;
        down  = Input.KEY_S;
        left  = Input.KEY_A;

        activateDevice = Input.KEY_SPACE;

        inventory = Input.KEY_TAB;

        build       = Input.KEY_U;
        destroy     = Input.KEY_O;
        buildCancel = Input.KEY_SEMICOLON;

        buildUp    = Input.KEY_I;
        buildRight = Input.KEY_L;
        buildDown  = Input.KEY_K;
        buildLeft  = Input.KEY_J;
    }

    public void setSweQwerty() {
        setQwerty();

        buildCancel = Input.KEY_EQUALS; //For some reason Ö is reported as 13, which is EQUALS according to Slick.
                                        //(Å and Ä are also reported as 13, so here's a potential oddity)
    }

    public void setSweQwerty2() {
        setQwerty();

        buildCancel = Input.KEY_GRAVE; //Same as above, only some computers/layouts report the value of grave
    }

    public int up   () { return up; }
    public int right() { return right; }
    public int down () { return down; }
    public int left () { return left; }

    public int activateDevice() { return activateDevice; }

    public int inventory() { return inventory; }

    public int build      () { return build;       }
    public int destroy    () { return destroy;     }
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
    public void destroy    (int key) { destroy     = key; }
    public void buildCancel(int key) { buildCancel = key; }

    public void buildUp   (int key) { buildUp    = key; }
    public void buildRight(int key) { buildRight = key; }
    public void buildDown (int key) { buildDown  = key; }
    public void buildLeft (int key) { buildLeft  = key; }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        switch (key) {
            case Input.KEY_F1:
                setQwerty();
                return true;

            case Input.KEY_F2:
                setSweQwerty();
                return true;

            case Input.KEY_F3:
                setSweQwerty2();
                return true;

            case Input.KEY_F4:
                setDvorak();
                return true;
        }

        return false;
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) { return false; }

}
