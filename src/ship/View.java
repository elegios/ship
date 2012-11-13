/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayDeque;
import java.util.Deque;

import media.FontHolder;
import media.MediaLoader;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import ship.control.Keys;
import ship.netcode.Network;
import ship.ui.chat.ChatWindow;
import ship.ui.inventory.Inventory;
import ship.world.World;

/**
 *
 * @author elegios
 */
public class View extends BasicGame {
    private static AppGameContainer window;

    private MediaLoader loader;
    private FontHolder  fonts;

    private Keys keys;

    private World      world;
    private Inventory  inventory;
    private ChatWindow chat;

    private Network net;

    private int playerId;
    private int numPlayers;

    private int diff;

    private GameContainer gc;
    private Deque<Rectangle> clips;

    public View(Network net, int playerId, int numPlayers) {
        super("Game");

        this.net = net;
        net.setView(this);

        this.playerId   = playerId;
        this.numPlayers = numPlayers;

        clips = new ArrayDeque<>();
    }

    public static void create(int width, int height, Network net, int playerId, int numPlayers) throws SlickException {
        window = new AppGameContainer(new View(net, playerId, numPlayers));
        window.setDisplayMode(width, height, false);
        window.setTargetFrameRate(60);
        window.setShowFPS(true);
        window.setAlwaysRender(true);
        window.setClearEachFrame(false);
        window.setUpdateOnlyWhenVisible(false);
        window.start();
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        clips.push(new Rectangle(0, 0, window.getWidth(), window.getHeight()));

        this.gc = gc;

        loader = new MediaLoader(new File("gfx"));
        fonts  = new FontHolder(FileSystems.getDefault().getPath("gfx"));

        keys = new Keys();

        inventory = new Inventory(this);
        chat      = new ChatWindow(this);

        world  = new World(this);
    }

    @Override
    public void update(GameContainer gc, int diff) throws SlickException {
        this.diff = diff;
        world.update(gc, diff);
        inventory.update(gc, diff);
        chat.update(gc, diff);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        world.render(gc, g);
        chat.render(gc, g);
        inventory.render(gc, g);
    }

    @Override
    public void keyPressed(int key, char c) {
        if (     keys.keyPressed(keys, key, c) ||
                 chat.keyPressed(keys, key, c) ||
            inventory.keyPressed(keys, key, c))
            return;
        world.keyPressed(keys, key, c);
    }

    @Override
    public void keyReleased(int key, char c) {
        if (inventory.keyReleased(keys, key, c))
            return;
        world.keyReleased(keys, key, c);
    }

    public void appendText(String text) {
        chat.appendText(text);
    }

    public void pushClip(float x, float y, float width, float height) {
        Rectangle lastClip = clips.peek();

        float newX = Math.max(x, lastClip.getX());
        float newY = Math.max(y, lastClip.getY());
        float newWidth  = Math.min(lastClip.getX() + lastClip.getWidth (), x + width)  - newX;
        float newHeight = Math.min(lastClip.getY() + lastClip.getHeight(), y + height) - newY;

        Rectangle newClip  = new Rectangle(newX, newY, newWidth, newHeight);
        gc.getGraphics().setClip(newClip);
        clips.push(newClip);
    }

    public void popClip() {
        if (clips.size() > 1) {
            clips.pop();
            gc.getGraphics().setClip(clips.peek());
        }
    }

    public Network     net()        { return net;        }
    public MediaLoader loader()     { return loader;     }
    public FontHolder  fonts()      { return fonts;      }
    public Keys        keys()       { return keys;       }
    public Inventory   inventory()  { return inventory;  }
    public World       world()      { return world;      }
    public int         playerId()   { return playerId;   }
    public int         numPlayers() { return numPlayers; }
    public int         diff()       { return diff;       }

    public static AppGameContainer window() { return window; }

}
