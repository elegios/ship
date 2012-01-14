package ship.ui.inventory.blockcreator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ship.ui.inventory.Tag;
import ship.ui.inventory.Tags;
import ship.world.collisiongrid.vehicle.block.Block;

public abstract class BlockCreator implements Comparable<BlockCreator> {

    private String name;
    private int baseTile;

    private Set<Tag> tags;

    public BlockCreator(String name, int baseTile, Tag[] tags) {
        this.name     = name;
        this.baseTile = baseTile;

        this.tags = new HashSet<>(Arrays.asList(tags));
        this.tags.add(Tags.ALL);
    }

    public abstract Block create (int sub, int x, int y);
    public abstract int   numSubs();
    public abstract int   subTile(int sub);

    public boolean matches(Tag tag) { return tags.contains(tag); }

    public String getName() { return     name; }
    public int    getIcon() { return baseTile; }

    public int compareTo(BlockCreator other) { return name.compareTo(other.getName()); }

}
