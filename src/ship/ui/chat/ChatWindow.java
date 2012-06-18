package ship.ui.chat;

import media.AnimateFloat;
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

    private int timeUntilHide;

    private AnimateFloat xPos;

    private boolean acceptingInput;

    private String text;
    private InputBox input;

    public ChatWindow(View view) throws SlickException {
        super(null, view.loader(), View.window().getWidth(), Y, WIDTH, (View.window().getHeight() / Box.TH) - 2);
        this.view = view;

        xPos = new AnimateFloat();
        xPos.force(View.window().getWidth());

        text  = "";
        input = new InputBox(this);

        acceptingInput = false;
        timeUntilHide = 0;
    }

    private void show() {
        xPos.set(View.window().getWidth() - X - getWidth());
    }
    private void hide() {
        xPos.set(View.window().getWidth());
    }

    public void appendText(String text) {
        this.text += text + "\n";

        if (timeUntilHide != -1)
            timeUntilHide = DEFAULT_TIME_UNTIL_HIDE;
        show();
    }

    @Override
    public void update(GameContainer gc, int diff) {
        xPos.update(diff);
        setX(Math.round(xPos.get()));

        if (timeUntilHide > 0) {
            timeUntilHide -= diff;

            if (timeUntilHide <= 0) {
                timeUntilHide = 0;
                hide();
            }
        }
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        super.render(gc, g);
        view.fonts().chat().drawString(ix() + TEXT_X_OFF, iy() + TEXT_Y_OFF, text);

        if (acceptingInput)
            input.render(gc, g);
    }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (view.net().isOnline()) {
            if (key == Input.KEY_ENTER) {

                acceptingInput = !acceptingInput;

                if (acceptingInput)
                    timeUntilHide = -1;
                else {
                    timeUntilHide = DEFAULT_TIME_UNTIL_HIDE;
                    input.sendMessage();
                }

                show();

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
        return false;
    }

    public View view() { return view; }

}
