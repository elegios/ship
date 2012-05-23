package ship.ui.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import media.AnimateFloat;
import media.Renderable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import ship.control.KeyReceiver;
import ship.control.Keys;
import ship.netcode.ShipProtocol;
import ship.netcode.inventory.ItemAndSubItemPackage;
import ship.ui.inventory.tilecreator.AirFuelTransportCreator;
import ship.ui.inventory.tilecreator.AirPowerTransportCreator;
import ship.ui.inventory.tilecreator.BalloonCreator;
import ship.ui.inventory.tilecreator.FuelTankCreator;
import ship.ui.inventory.tilecreator.FuelTapCreator;
import ship.ui.inventory.tilecreator.FuelTransportCreator;
import ship.ui.inventory.tilecreator.MomentumAbsorberCreator;
import ship.ui.inventory.tilecreator.PowerSwitchCreator;
import ship.ui.inventory.tilecreator.PowerTransportCreator;
import ship.ui.inventory.tilecreator.ThrusterCreator;
import ship.ui.inventory.tilecreator.TileCreator;
import ship.ui.inventory.tilecreator.ToggleBlockCreator;
import ship.world.Position;

public class Inventory implements Renderable, Updatable, Position, KeyReceiver {
    public static final int X_ORIGIN = 20;
    public static final int Y_ORIGIN = 20;

    private View     view;

    private AnimateFloat x;
    private AnimateFloat y;

    private boolean visible;
    private Focusable currentFocus;

    private Tags     tags;
    private Items    items;
    private SubItems subItems;

    private List<TileCreator> blockCreators;

    public Inventory(View view) throws SlickException {
        this.view = view;

        x = new AnimateFloat();
        y = new AnimateFloat();

        visible = false;

        blockCreators = new ArrayList<>();
        addTileCreators();
        Collections.sort(blockCreators);

        tags = new Tags(this, X_ORIGIN, Y_ORIGIN);

        items = new Items(this, X_ORIGIN + (int) tags.getX2(), Y_ORIGIN, blockCreators);
        items.setFocus(true);
        currentFocus = items;

        subItems = new SubItems(this, X_ORIGIN + (int) items.getX2(), Y_ORIGIN);
        updateSubMenu();

        x.force(-subItems.getX2() - 1);
    }
    private void addTileCreators() {
        blockCreators.add(new          ThrusterCreator());
        blockCreators.add(new     FuelTransportCreator());
        blockCreators.add(new  AirFuelTransportCreator());
        blockCreators.add(new       PowerSwitchCreator());
        blockCreators.add(new    PowerTransportCreator());
        blockCreators.add(new AirPowerTransportCreator());
        blockCreators.add(new           FuelTapCreator());
        blockCreators.add(new          FuelTankCreator());
        blockCreators.add(new       ToggleBlockCreator());
        blockCreators.add(new           BalloonCreator());
        blockCreators.add(new  MomentumAbsorberCreator());
    }

    /**
     * Returns the BlockCreator at point <code>index</code> in the cache.
     * Since this list is never resorted or altered the same index will
     * always return the same BlockCreator.
     * @param index the index from which the BlockCreator should be taken
     * @return the BlockCreator at <code>index</code>
     */
    public TileCreator getBlockAt(int index) { return blockCreators.get(index); }

    /**
     * Makes sure items displays only the items that match the
     * currently selected tag.
     */
    public void updateTagFilter() {
        items.rebuildItemList(blockCreators, tags.getSelected());
    }

    /**
     * Makes sure subItems displays variants of the correct item.
     */
    public void updateSubMenu() {
        subItems.updateSubs(items.getSelected());
    }

    /**
     * Shifts focus one step to the right, unless currently
     * focusing on subItems.
     */
    public void moveRight() {
        if (currentFocus == tags)
            setFocus(items);
        else if (currentFocus == items)
            setFocus(subItems);
    }

    /**
     * Shifts focus one step to the left, unless currently
     * focusing on tags.
     */
    public void moveLeft() {
        if (currentFocus == items)
            setFocus(tags);
        else if (currentFocus == subItems)
            setFocus(items);
    }

    /**
     * Shifts the focus to <code>focus</code> making sure to
     * first remove it from the currently focused Focusable
     * @param focus
     */
    private void setFocus(Focusable focus) {
        currentFocus.setFocus(false);
        currentFocus = focus;
        currentFocus.setFocus(true);
    }

    /**
     * Checks whether the inventory is open or closed and then initiates
     * a move towards on- or off-screen, respectively
     */
    private void setXPos() {
        if (visible)
            x.set(0);
        else
            x.set(-subItems.getX2() - 1);
    }

    public void sendSelectedItemAndSubTile() {
        if (view.net().isOnline())
            view.net().send(ShipProtocol.ITEM_AND_SUB, new ItemAndSubItemPackage(view.playerId(),
                                                                                 getSelectedItem(),
                                                                                 getSelectedSubItem()));
    }

    /**
     * Returns the index of the currently selected subItem
     * @return
     */
    public int getSelectedSubItem() {
        return subItems.getSelectedSubItem();
    }

    public int getSelectedSubTile() {
        return getSelectedTile().subTile(getSelectedSubItem());
    }

    public int getSelectedItem() {
        return getIndexOf(getSelectedTile());
    }

    public TileCreator getSelectedTile() {
        return items.getSelected();
    }

    @Override
    public void update(GameContainer gc, int diff) {
        x.update(diff);
        y.update(diff);
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        tags    .render(gc, g);
        items   .render(gc, g);
        subItems.render(gc, g);
    }

    @Override
    public boolean keyPressed(Keys keys, int key, char c) {
        if (key == keys.inventory()) {
            visible = !visible;
            setXPos();

            return true;

        } else if (visible) {
            if (((KeyReceiver) currentFocus).keyPressed(keys, key, c))
                return true;

            if (key == keys.buildLeft()) {
                moveLeft();
                return true;

            } else if (key == keys.buildRight()) {
                moveRight();
                return true;

            } else if (key == keys.build() || key == keys.buildCancel()) {
                visible = false;
                setXPos();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) { return false; }

    public View view() { return view; }

    public float getX() { return x.get(); }
    public float getY() { return y.get(); }

    public int ix() { return Math.round(getX()); }
    public int iy() { return Math.round(getY()); }

    public int getIndexOf(TileCreator selected) { return blockCreators.indexOf(selected); }

}
