package ship.world;

import java.util.Random;

import media.ManagedImage;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.View;

public class ParallaxBackground implements Renderable {
    public static final int NUM_PARTICLES_PER_LAYER = 20;
    public static final int NUM_LAYERS              = 3;
    public static final float LAYER_FACTOR          = 0.7f;

    private World world;

    private int[][][] coords;

    private ManagedImage sprite;

    /**
     * Creates a new ParallaxBackground for use in the supplied <code>world</code>.
     * The particles will scroll relative to the return of world.ix() and world.iy()
     * methods, at different rate depending on the layer.
     * @param world
     * @throws SlickException
     */
    public ParallaxBackground(World world) throws SlickException {
        this.world = world;

        Random r = new Random();

        coords = new int[NUM_LAYERS][NUM_PARTICLES_PER_LAYER][2];
        for (int l = 0; l < NUM_LAYERS; l++)
            for (int i = 0; i < NUM_PARTICLES_PER_LAYER; i++) {
                coords[l][i][0] = r.nextInt((int) Math.round(View.window().getWidth () * Math.pow(1/LAYER_FACTOR, l)));
                coords[l][i][1] = r.nextInt((int) Math.round(View.window().getHeight() * Math.pow(1/LAYER_FACTOR, l)));
            }

        sprite = world.view().loader().loadManagedImage("parallax_background");
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        for (int i = 0; i < coords.length; i++)
            g.scale(LAYER_FACTOR, LAYER_FACTOR);

        for (int l = NUM_LAYERS - 1; l >= 0; l--) {
            g.scale(1/LAYER_FACTOR, 1/LAYER_FACTOR);

            int width  = (int) (View.window().getWidth () / Math.pow(LAYER_FACTOR, l)) + sprite.getImage().getWidth ();
            int height = (int) (View.window().getHeight() / Math.pow(LAYER_FACTOR, l)) + sprite.getImage().getHeight();
            for (int[] point : coords[l]) {
                int x = ((point[0] + world.ix()) % width);
                if (x < 0)
                    x += width;
                int y = (point[1] + world.iy()) % height;
                if (y < 0)
                    y += height;

                sprite.getImage().draw(x - sprite.getImage().getWidth(), y - sprite.getImage().getHeight());
            }
        }

        g.resetTransform();
    }

}
