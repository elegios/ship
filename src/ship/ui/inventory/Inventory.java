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
import ship.ui.inventory.blockcreator.AirFuelTransportCreator;
import ship.ui.inventory.blockcreator.AirPowerTransportCreator;
import ship.ui.inventory.blockcreator.BlockCreator;
import ship.ui.inventory.blockcreator.FuelTransportCreator;
import ship.ui.inventory.blockcreator.PowerSwitchCreator;
import ship.ui.inventory.blockcreator.PowerTransportCreator;
import ship.ui.inventory.blockcreator.ThrusterCreator;
import ship.world.Position;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.easy.EasyNode;

public class Inventory implements Renderable, Updatable, ChangeListener, Position, KeyReceiver {
    public static final int X_ORIGIN = 20;
    public static final int Y_ORIGIN = 20;

    private View     view;
    private EasyNode node;

    private AnimateFloat x;
    private AnimateFloat y;

    private boolean visible;
    private Focusable currentFocus;

    private Tags     tags;
    private Items    items;
    private SubItems subItems;

    private int playerID;

    private List<BlockCreator> blockCreators;

    public Inventory(View view) throws SlickException {
        this.view = view;

        playerID = view.playerId();
        node     = view.node();
        node.addChangeListener(this);

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
    }

    public BlockCreator getBlockAt(int index) { return blockCreators.get(index); }

    public void updateTagFilter() {
        items.rebuildItemList(blockCreators, tags.getSelected());
    }

    public void updateSubMenu() {
        subItems.updateSubs(items.getSelected());
    }

    public void moveRight() {
        if (currentFocus == tags)
            setFocus(items);
        else if (currentFocus == items)
            setFocus(subItems);
    }

    public void moveLeft() {
        if (currentFocus == items)
            setFocus(tags);
        else if (currentFocus == subItems)
            setFocus(items);
    }

    private void setFocus(Focusable focus) {
        currentFocus.setFocus(false);
        currentFocus = focus;
        currentFocus.setFocus(true);
    }

    private void setXPos() {
        if (visible)
            x.set(0);
        else
            x.set(-subItems.getX2() - 1);
    }

    @Override
    public void update(GameContainer gc, int diff) {
        x.update(diff);
        y.update(diff);
    }

    @Override
    public void render(GameContainer gc, Graphics g) {
        //if (visible) {
            tags    .render(gc, g);
            items   .render(gc, g);
            subItems.render(gc, g);
        //}
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

            if (key == keys.left() || key == keys.buildLeft())
                moveLeft();

            else if (key == keys.right() || key == keys.buildRight())
                moveRight();

            else if (key == keys.build() || key == keys.buildCancel()) {
                visible = false;
                setXPos();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean keyReleased(Keys keys, int key, char c) {
        return false;
    }

    public View view() { return view; }

    public float getX() { return x.get(); }
    public float getY() { return y.get(); }

    public int ix() { return Math.round(getX()); }
    public int iy() { return Math.round(getY()); }

    public void c(String id, Object data) { node.c("player." +playerID+ ".inventory." +id, data); }

    public void dataChanged   (String id, String  data) {}
    public void intChanged    (String id, int     data) {}
    public void booleanChanged(String id, boolean data) {}
    public void floatChanged  (String id, float   data) {}

    public int getIndexOf(BlockCreator selected) { return blockCreators.indexOf(selected); }

}
