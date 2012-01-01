/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package media;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author elegios
 */
public class DirAnimation {

    private SpriteSheet ss;
    private int frameDuration;
    private boolean dirs;

    private int xOffset;
    private int yOffset;

    private int timeUntilTick;
    private byte currentDirection;
    private int currentFrame;

    public DirAnimation(SpriteSheet ss, int frameDuration, int xOffset, int yOffset, boolean dirs) {
        this.ss            = ss;
        this.frameDuration = frameDuration;
        this.dirs          = dirs;

        this.xOffset = xOffset;
        this.yOffset = yOffset;

        timeUntilTick    = frameDuration;
        currentDirection = 0;
    }

    public void playFromStart() {
        timeUntilTick = frameDuration;
        currentFrame = 0;
    }

    public void setDirection(byte direction) {
        if (dirs)
            currentDirection = direction;
        playFromStart();
    }

    public void update(int i) {
        timeUntilTick -= i;
        if (timeUntilTick <= 0) {
            timeUntilTick += frameDuration;
            currentFrame = (currentFrame + 1) % ss.getHorizontalCount();
        }
    }

    public void render(int x, int y) {
        ss.getSprite(currentFrame, currentDirection).draw(x + xOffset, y + yOffset);
    }

    public void destroy() throws SlickException {
        ss.destroy();
    }

}
