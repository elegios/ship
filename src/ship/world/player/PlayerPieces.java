package ship.world.player;

import media.Renderable;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import ship.Updatable;
import ship.View;
import ship.world.Rectangle;
import ship.world.World;
import ship.world.vehicle.VehicleHolder;
import ship.world.vehicle.VehiclePiece;

public class PlayerPieces implements Rectangle, Renderable, Updatable {
    public static final float AIR_RESIST_RANGE = 320; //The range of vehicle airresist in pixels

    private Player player;

    private Builder builder;

    private int   index;
    private float splittingPoint;
    private float offset;

    private boolean horizontalSplit;
    private boolean verticalSplit;

    public PlayerPieces(PlayerHolder playerHolder) throws SlickException {
        this.player = playerHolder.getPlayer();

        builder = new Builder(playerHolder.world().view().inventory(), playerHolder);

        index          = 0;
        splittingPoint = Float.NaN;
        offset         = Float.NaN;

        verticalSplit   = false;
        horizontalSplit = false;
    }

    public Player getPlayer() { return  player; }
    public Builder  builder() { return builder; }

    public void update(GameContainer gc, int diff) {
        player.airResistX(false);

        if (player.lastVehicle() == null || !doAirResistX(player.lastVehicle())) {
            boolean airResisted = false;
            for (VehicleHolder vehicle : player.world().vehicles())
                if (doAirResistX(vehicle)) {
                    player.lastVehicle(vehicle);
                    airResisted = true;
                    break;
                }

            if (!airResisted)
                doAirResistX();
        }
        if (player.lastVehicle() == null || !doAirResistY(player.lastVehicle())) {
            boolean airResisted = false;
            for (VehicleHolder vehicle : player.world().vehicles())
                if (doAirResistY(vehicle)) {
                    airResisted = true;
                    break;
                }

            if (!airResisted)
                doAirResistY();
        }

        player.update(gc, diff);
    }

    private boolean doAirResistX(VehicleHolder vehicle) { //TODO: take player splitting into account
        VehiclePiece closestPiece = vehicle.findClosestPiece(getX(), getY());

        int playX = closestPiece.getTileXUnderPos(getCenterX());
        int playY = closestPiece.getTileYUnderPos(getCenterY());

        if (getX() >= closestPiece.getBoundX() - AIR_RESIST_RANGE && getX2() <= closestPiece.getBoundX2() + AIR_RESIST_RANGE &&
            getY() >= closestPiece.getBoundY()                    && getY2() <= closestPiece.getBoundY2()) {

            if (player.getAbsXSpeed() > 0) {
                for (int i = Math.max(playX, closestPiece.leftX()); i <= closestPiece.rightX(); i++) {
                    if (vehicle.getVehicle().existsAt(i, playY)) {
                        player.pushX(-(player.getAbsXSpeed() - vehicle.getVehicle().getAbsXSpeed()) * player.world().airResist());
                        player.airResistX(true);
                        return true;
                    }
                }

            } else if (player.getAbsXSpeed() < 0) {
                //The following if statement is to fix a bug when the player is to the right of the VehiclePiece
                //and getTileXUnderPos returns -1
                if (playX == -1)
                    playX = closestPiece.rightX();

                for (int i = playX; i >= closestPiece.leftX(); i--) {
                    if (vehicle.getVehicle().existsAt(i, playY)) {
                        player.pushX(-(player.getAbsXSpeed() - vehicle.getVehicle().getAbsXSpeed()) * player.world().airResist());
                        player.airResistX(true);
                        return true;
                    }
                }

            }
        }

        return false;
    }
    private void doAirResistX() {
        player.pushX(-player.getAbsXSpeed() * player.world().airResist());
        player.airResistX(true);
    }

    private boolean doAirResistY(VehicleHolder vehicle) {
        VehiclePiece closestPiece = vehicle.findClosestPiece(getX(), getY());

        int playX = closestPiece.getTileXUnderPos(getCenterX());
        int playY = closestPiece.getTileYUnderPos(getCenterY());

        if (getX() >= closestPiece.getBoundX()                    && getX2() <= closestPiece.getBoundX2() &&
            getY() >= closestPiece.getBoundY() - AIR_RESIST_RANGE && getY2() <= closestPiece.getBoundY2() + AIR_RESIST_RANGE) {

            if (player.getAbsYSpeed() > 0) {
                for (int j = Math.max(playY, closestPiece.topY()); j <= closestPiece.botY(); j++) {
                    if (vehicle.getVehicle().existsAt(playX, j)) {
                        player.pushY(-(player.getAbsYSpeed() - vehicle.getVehicle().getAbsYSpeed()) * player.world().airResist());
                        return true;
                    }
                }

            } else if (player.getAbsYSpeed() < 0) {
                //The following if statement is to fix a bug when the player is to the right of the VehiclePiece
                //and getTileXUnderPos returns -1
                if (playY == -1)
                    playY = closestPiece.botY();

                for (int j = playY; j >= closestPiece.topY(); j--) {
                    if (vehicle.getVehicle().existsAt(playX, j)) {
                        player.pushY(-(player.getAbsYSpeed() - vehicle.getVehicle().getAbsYSpeed()) * player.world().airResist());
                        return true;
                    }
                }

            }
        }

        return false;
    }
    private void doAirResistY() {
        player.pushY(-player.getAbsYSpeed() * player.world().airResist());
    }

    /**
     * Removes the vertical splitting point, if any, and sets
     * the horizontal one
     * @param splitPoint the global x coordinate of the splitting point
     * @param enteredFromLeft should be true if the player entered the splitting point from the left
     * @param outputMod the distance between the splitting point and exit point
     */
    public void setHorizontalSplit(float splitPoint, boolean enteredFromLeft, float outputMod) {
        removeSplits();

        if (enteredFromLeft) {
            offset         = outputMod;
            splittingPoint = splitPoint;

        } else {
            player.x += outputMod;
            offset   = -outputMod;
            splittingPoint = splitPoint + outputMod;
        }

        horizontalSplit = true;
    }

    /**
     * Removes the horizontal splitting point, if any, and sets
     * the vertical one
     * @param splitPoint the global y coordinate of the splitting point
     * @param enteredFromTop should be true if the player entered the splitting point from above
     * @param outputMod the distance between the splitting point and exit point
     */
    public void setVerticalSplit(float splitPoint, boolean enteredFromTop, float outputMod) {
        removeSplits();

        if (enteredFromTop) {
            offset         = outputMod;
            splittingPoint = splitPoint;

        } else {
            player.y += outputMod;
            offset   = -outputMod;
            splittingPoint = splitPoint + outputMod;
        }

        verticalSplit = true;
    }

    /**
     * Removes all splitting points. After this method has finished the entirety of
     * the player will be at the position of the current player piece (regardless of
     * whether the player piece would have been rendered there, or if it's collision-
     * box would have been there)
     */
    public void removeSplits() {
        if (completePiece() == 1)
            if (horizontalSplit)
                player.x += offset;
            else if (verticalSplit)
                player.y += offset;

        index          = 0;
        splittingPoint = Float.NaN;
        offset         = Float.NaN;

        verticalSplit   = false;
        horizontalSplit = false;
    }

    /**
     * Returns true if a splitting point is present and it
     * actually splits the player. If, for example the splitting point
     * is at x coordinate 0 and the players x coordinate is 10 this
     * method returns false, even though a splitting point exists
     * @return true if the player is split, false otherwise
     */
    public boolean isSplit() {
        return completePiece() == -1;
    }

    /**
     * Checks if the player is split or which side of the split the player is on.
     * @return 0 if index 0 is the entire player, 1 if index 1 is the entire player, otherwise -1
     */
    private int completePiece() {
        if (horizontalSplit) {
            if (player.getX2() < splittingPoint)
                return 0;

            if (player.getX() > splittingPoint)
                return 1;

            return -1;

        } else if (verticalSplit) {
            if (player.getY2() < splittingPoint)
                return 0;

            if (player.getY() > splittingPoint)
                return 1;

            return -1;
        }

        return 0;
    }

    private int completeBuilderPiece() {
        int ret = 0;

        if (horizontalSplit) {
            if (player.getX() + builder.getModX(false) + builder.getWidth() < splittingPoint)
                return 0;

            if (player.getX() + builder.getModX(false) > splittingPoint)
                return 1;

            return -1;

        } else if (verticalSplit) {
            if (player.getY() + builder.getModY(false) + builder.getHeight() < splittingPoint)
                return 0;

            if (player.getY() + builder.getModY(false) > splittingPoint)
                return 1;

            return -1;
        }

        return ret;
    }

    /**
     * Sets which piece should be used for collision detection. 0 is the top
     * or left piece, depending on the orientation of the current split, 1 is
     * the bottom or right piece
     * @param index
     */
    public void setCurrentPiece(int index) {
        if (isSplit())
            this.index = index;
        else
            this.index = completePiece();
    }

    @Override
    public float getX() {
        if (index == 1 && horizontalSplit)
            if (completePiece() == -1)
                return splittingPoint + offset;
            else
                return player.getX() + offset;

        return player.getX();
    }

    @Override
    public float getY() {
        if (index == 1 && verticalSplit)
            if (completePiece() == -1)
                return splittingPoint + offset;
            else
                return player.getY() + offset;

        return player.getY();
    }

    @Override
    public float getX2() {
        if (horizontalSplit) {
            if (index == 1)
                return player.getX2() + offset;
            else if (isSplit())
                return splittingPoint;
        }

        return player.getX2();
    }

    @Override
    public float getY2() {
        if (verticalSplit) {
            if (index == 1)
                return player.getY2() + offset;
            else if (isSplit())
                return splittingPoint;
        }

        return player.getY2();
    }

    @Override
    public int ix() {
        if (index == 1)
            return Math.round(player.getX() + offset);

        return Math.round(player.getX());
    }
    @Override
    public int iy() {
        if (index == 1)
            return Math.round(player.getY() + offset);

        return Math.round(player.getY());
    }

    @Override
    public void render(GameContainer gc, Graphics g) { //TODO: render as if split when just the builder is past the split
        setCurrentPiece(0);

        switch(completePiece()) {
            case -1:
                if (horizontalSplit)
                    renderHorizontallySplit(player, gc, g);
                else
                    renderVerticallySplit(player, gc, g);
                break;

            case 1:
                if (horizontalSplit) {
                    float oldX = player.x;
                    player.x += offset;
                    player.render(gc, g);
                    player.x = oldX;
                } else {
                    float oldY = player.y;
                    player.y += offset;
                    player.render(gc, g);
                    player.y = oldY;
                }
                break;

            default:
                player.render(gc, g);
                break;
        }

        switch (completeBuilderPiece()) {
            case -1:
                if (horizontalSplit)
                    renderHorizontallySplit(builder, gc, g);
                else
                    renderVerticallySplit(builder, gc, g);
                break;

            case 1:
                if (horizontalSplit)
                    builder.setRenderPos(player, offset, 0);
                else
                    builder.setRenderPos(player, 0, offset);
                builder.render(gc, g);
                break;

            default:
                builder.setRenderPos(player);
                builder.render(gc, g);
                break;
        }
    }

    private void renderHorizontallySplit(Renderable rend, GameContainer gc, Graphics g) { //TODO: figure out what happens to the builder getX and stuff
        View view = player.world().view();
        setCurrentPiece(0);

        float screenY  = player.world().getY();
        float screenY2 = player.world().getY();

        if (rend instanceof Player) {
            screenY  += player.getY();
            screenY2 += player.getY2();
        } else { //We're rendering a Builder
            screenY  += player.getY() + builder.getModY(false);
            screenY2 += player.getY() + builder.getModY(false) + builder.getHeight() - 1;
        }

        view.pushClip(0, 0, (int) (player.world().getX() + splittingPoint), View.window().getHeight());
        if (rend instanceof Builder)
            builder.setRenderPos(player);
        rend.render(gc, g);
        view.popClip();

        Color c = g.getColor();
        g.setColor(World.SPLIT_COLOR);
        if (!(rend instanceof Builder) || builder.buildMode())
            g.drawLine(player.world().getX() + splittingPoint, screenY,
                       player.world().getX() + splittingPoint, screenY2);

        view.pushClip((int) (player.world().getX() + splittingPoint + offset), 0, View.window().getWidth(), View.window().getHeight());

        if (rend instanceof Player) {
            float oldX = player.x;
            player.x += offset;
            player.render(gc, g);
            player.x = oldX;
        } else {
            builder.dontRenderHighlight();
            builder.setRenderPos(player, offset, 0);
            builder.render(gc, g);
        }

        view.popClip();

        setCurrentPiece(1);
        if (!(rend instanceof Builder) || ((Builder) rend).buildMode())
            g.drawLine(player.world().getX() + splittingPoint + offset, screenY,
                       player.world().getX() + splittingPoint + offset, screenY2);

        g.setColor(c);
    }
    private void renderVerticallySplit(Renderable rend, GameContainer gc, Graphics g) { //TODO: figure out what happens to the builder getX and stuff
        View view = player.world().view();
        setCurrentPiece(0);

        float screenX  = player.world().getX();
        float screenX2 = screenX;

        if (rend instanceof Player) {
            screenX  += player.getX();
            screenX2 += player.getX2();
        } else {
            screenX  += player.getX() + builder.getModX(false);
            screenX2 += player.getX() + builder.getModX(false) + builder.getWidth() - 1;
        }

        view.pushClip(0, 0, View.window().getWidth(), (int) (player.world().getY() + splittingPoint));
        if (rend instanceof Builder)
            builder.setRenderPos(player);
        rend.render(gc, g);
        view.popClip();

        Color c = g.getColor();
        g.setColor(World.SPLIT_COLOR);
        if (!(rend instanceof Builder) || builder.buildMode())
            g.drawLine(screenX,  player.world().getY() + splittingPoint,
                       screenX2, player.world().getY() + splittingPoint);

        view.pushClip(0, (int) (player.world().getY() + splittingPoint + offset), View.window().getWidth(), View.window().getHeight());

        if (rend instanceof Player) {
            float oldY = player.y;
            player.y += offset;
            player.render(gc, g);
            player.y = oldY;
        } else {
            builder.dontRenderHighlight();
            builder.setRenderPos(player, 0, offset);
            builder.render(gc, g);
        }

        view.popClip();

        setCurrentPiece(1);
        if (!(rend instanceof Builder) || ((Builder) rend).buildMode())
            if (!(rend instanceof Builder) || builder.buildMode())
                g.drawLine(screenX,  player.world().getY() + splittingPoint + offset,
                           screenX2, player.world().getY() + splittingPoint + offset);

        g.setColor(c);
    }

    @Override
    public int getWidth() {
        if (horizontalSplit && isSplit()) {
            if (index == 1)
                return player.getWidth() - (int) (splittingPoint - player.getX());

            return (int) (splittingPoint - player.getX());
        }

        return player.getWidth();
    }

    @Override
    public int getHeight() {
        if (verticalSplit && isSplit()) {
            if (index == 1)
                return player.getHeight() - (int) (splittingPoint - player.getY());

            return (int) (splittingPoint - player.getY());
        }

        return player.getHeight();
    }

    public int getCenterX() {
        float centerX = player.getX() + player.getWidth()/2;

        if (horizontalSplit && centerX > splittingPoint) {
            return Math.round(centerX + offset);
        }

        return Math.round(centerX);
    }

    public int getCenterY() {
        float centerY = player.getY() + player.getHeight()/2;

        if (verticalSplit && centerY > splittingPoint) {
            return Math.round(centerY + offset);
        }

        return Math.round(centerY);
    }

    public float getBuilderCenterX() {
        float centerX = player.getX() + builder.getModX() + builder.getWidth()/2;

        if (horizontalSplit && centerX > splittingPoint)
            return centerX + offset;

        return centerX;
    }

    public float getBuilderCenterY() {
        float centerY = player.getY() + builder.getModY() + builder.getHeight()/2;

        if (verticalSplit && centerY > splittingPoint)
            return centerY + offset;

        return centerY;
    }

    public boolean horizontalSplit() { return horizontalSplit; }
    public boolean verticalSplit  () { return   verticalSplit; }

    public float splittingPoint() { return splittingPoint; }
    public float offset        () { return         offset; }

}
