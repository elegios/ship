/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship;

import java.io.File;

import media.MediaLoader;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import world.World;
import dataverse.datanode.EasyNode;

/**
 *
 * @author elegios
 */
public class View extends BasicGame {
    private static AppGameContainer window;

    private EasyNode node;

    private MediaLoader loader;

    private World world;

    public View(EasyNode node) {
        super("Game");

        this.node = node;
    }

    public static void create(int width, int height, EasyNode node) throws SlickException {
        window = new AppGameContainer(new View(node));
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

        world  = new World(this);
    }

    @Override
    public void update(GameContainer gc, int diff) throws SlickException {
        world.update(gc, diff);
    }

    @Override
    public void render(GameContainer gc, Graphics grphcs) throws SlickException {
        world.render(gc, grphcs);
    }

    public EasyNode    node()   { return node; }
    public MediaLoader loader() { return loader; }
    public World       world()  { return world; }

    public static AppGameContainer window() { return window; }

}
