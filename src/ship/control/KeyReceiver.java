package ship.control;

public interface KeyReceiver {

    boolean keyPressed (Keys keys, int key, char c);
    boolean keyReleased(Keys keys, int key, char c);

}
