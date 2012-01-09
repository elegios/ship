package ship.ui.inventory;

public class Tag {

    private String name;
    private int    id;

    public Tag(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    public int    getID  () { return id;   }
    public String getName() { return name; }
}