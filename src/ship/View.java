/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship;

import java.io.File;
import java.nio.file.FileSystems;

import media.FontHolder;
import media.MediaLoader;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.control.Keys;
import ship.netcode.Network;
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

    private World     world;
    private Inventory inventory;

    private Network net;

    private int playerId;
    private int numPlayers;

    private int diff;

    public View(Network net, int playerId, int numPlayers) {
        super("Game");

        this.net = net;
        net.setView(this);

        this.playerId   = playerId;
        this.numPlayers = numPlayers;
    }

    public static void create(int width, int height, Network net, int playerId, int numPlayers) throws SlickException {
        window = new AppGameContainer(new View(net, playerId, numPlayers));
        window.setDisplayMode(width, height, false);
        window.setTargetFrameRate(60);
        window.setShowFPS(true);
        window.setAlwaysRender(true);
        window.setUpdateOnlyWhenVisible(false);
        window.start();
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        loader = new MediaLoader(new File("gfx"));
        fonts  = new FontHolder(FileSystems.getDefault().getPath("gfx"));

        keys = new Keys();

        inventory = new Inventory(this);

        world  = new World(this);
    }

    @Override
    public void update(GameContainer gc, int diff) throws SlickException {
        this.diff = diff;
        world.update(gc, diff);
        inventory.update(gc, diff);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {
        world.render(gc, g);
        inventory.render(gc, g);
    }

    @Override
    public void keyPressed(int key, char c) {
        if (keys.keyPressed(keys, key, c))
            return;
        if (inventory.keyPressed(keys, key, c))
            return;
        world.keyPressed(keys, key, c);
    }

    @Override
    public void keyReleased(int key, char c) {
        if (inventory.keyReleased(keys, key, c))
            return;
        world.keyReleased(keys, key, c);
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
