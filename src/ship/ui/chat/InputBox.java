package ship.ui.chat;

import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.world.Position;

public class InputBox implements Position, Renderable, KeyReceiver {

    public static final int PADDING = 5;

    public static final int TEXT_X_OFFSET = 5;
    public static final int TEXT_Y_OFFSET = 5;

    private ChatWindow chat;

    private String text;

    public InputBox(ChatWindow chat) {
        this.chat = chat;

        text = "";
    }

    public void sendMessage() {
        if (!text.isEmpty()) {
            chat.view().net().sendChatMessage(text);
            text = "";
        }
    }

    public void cancelMessage() {
        text = "";
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        chat.view().fonts().chat().drawString(ix() + TEXT_X_OFFSET,
                                              iy() + TEXT_Y_OFFSET,
                                              text);
    }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == Input.KEY_BACK) {
            if (!text.isEmpty())
                text = text.substring(0, text.length() - 1);

            if (text.length() == 0)
                chat.cancelMessage();

        } else if (Character.getType(c) != 15) {
            text += c;
        }

        return true;
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) {
        return false;
    }

    @Override
    public float getX() {
        return chat.getX();
    }

    @Override
    public float getY() {
        return chat.getY2() + PADDING;
    }

    @Override
    public int ix() {
        return Math.round(getX());
    }

    @Override
    public int iy() {
        return Math.round(getY());
    }

}
