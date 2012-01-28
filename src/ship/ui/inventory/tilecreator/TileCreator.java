package ship.ui.inventory.tilecreator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.tile.Tile;

public abstract class TileCreator implements Comparable<TileCreator> {

    private String name;
    private int baseTile;

    private Set<Tag> tags;

    public TileCreator(String name, int baseTile, Tag[] tags) {
        this.name     = name;
        this.baseTile = baseTile;

        this.tags = new HashSet<>(Arrays.asList(tags));
        this.tags.add(Tags.ALL);
    }

    /**
     * Should create a new Tile of one specific type, taking <code>sub</code>
     * into account if there are more than one variant of the given type.
     * @param sub the variant to be used
     * @param x the internal x coordinate of the resulting tile
     * @param y the internal y coordinate of the resulting tile
     * @return a newly created Tile
     */
    public abstract Tile create (int sub, int x, int y);
    /**
     * Should return the number of variants a given Tile type has.
     * @return number of types
     */
    public abstract int numSubs();
    /**
     * Return an int representing a sprite in the CollisionGrid sprite sheet
     * for the given variant.
     * @param sub
     * @return an int representing a sprite from the CollisionGrid SpriteSheet
     */
    public abstract int subTile(int sub);

    /**
     * Checks whether the current Tile type matches the given type.
     * @param tag
     * @return true if tags contains <code>tag</code>
     */
    public boolean matches(Tag tag) { return tags.contains(tag); }

    public String getName() { return name; }
    /**
     * Return an int representing a sprite in the CollisionGrid sprite sheet
     * that will represent the Tile type in the items list.
     * @return an int representing a sprite from the CollisionGrid SpriteSheet
     */
    public int getIcon() { return baseTile; }

    public int compareTo(TileCreator other) { return name.compareTo(other.getName()); }

}
