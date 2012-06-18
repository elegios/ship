package ship.ui.chat;

import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.ui.Box;

public class ChatWindow extends Box implements Renderable, Updatable, KeyReceiver {
    public static final int WIDTH = 12;
    public static final int X = 20;
    public static final int Y = 20;

    public static final int TEXT_X_OFF = 15;
    public static final int TEXT_Y_OFF = 15;

    public static final int DEFAULT_TIME_UNTIL_HIDE = 3000;

    private View view;

    private int     timeUntilHide;
    private boolean visible;

    private boolean acceptingInput;

    private String text;
    private InputBox input;

    public ChatWindow(View view) throws SlickException {
        super(null, view.loader(), X, Y, WIDTH, (View.window().getHeight() / Box.TH) - 2);
        setX(View.window().getWidth() - X - getWidth());
        this.view = view;

        text  = "";
        input = new InputBox(this);

        acceptingInput = false;
        timeUntilHide = 0;
        visible = false;
    }

    public void appendText(String text) {
        this.text += text + "\n";

        if (timeUntilHide != -1)
            timeUntilHide = DEFAULT_TIME_UNTIL_HIDE;
        visible = true;
    }

    @Override
    public void update(GameContainer gc, int diff) {
        if (timeUntilHide > 0) {
            timeUntilHide -= diff;

            if (timeUntilHide <= 0) {
                timeUntilHide = 0;
                visible = false;
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        if (visible) {
            super.render(gc, g);
            view.fonts().chat().drawString(ix() + TEXT_X_OFF, iy() + TEXT_Y_OFF, text);

            if (acceptingInput)
                input.render(gc, g);
        }
    }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (view.net().isOnline()) {
            if (key == Input.KEY_ENTER) {

                visible = true;
                acceptingInput = !acceptingInput;

                if (acceptingInput)
                    timeUntilHide = -1;
                else {
                    timeUntilHide = DEFAULT_TIME_UNTIL_HIDE;
                    input.sendMessage();
                }

                return true;

            } else if (acceptingInput) {
                if (key == Input.KEY_ESCAPE) {
                    cancelMessage();
                    return true;
                }

                return input.keyPressed(keys, key, c);
            }
        }

        return false;
    }

    public void cancelMessage() {
        acceptingInput = false;
        timeUntilHide = DEFAULT_TIME_UNTIL_HIDE;
        input.cancelMessage();
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) {
        // TODO Auto-generated method stub
        return false;
    }

    public View view() { return view; }

}
