package ship.world.vehicle;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import ship.world.Rectangle;
import ship.world.RelativeMovable;
import ship.world.World;
import ship.world.player.PlayerPieces;
import ship.world.vehicle.tile.Tile;

public class ImmobileVehicle extends Vehicle {
    public static final int IMMOBILE_VEH_WIDTH  = 512;
    public static final int IMMOBILE_VEH_HEIGHT = 512;

    public ImmobileVehicle(World world, int id, float x, float y) throws SlickException {
        super(world, id, x, y, false);

        for (int i = 1; i < WIDTH(); i++)
            for (int j = 0; j < 5; j++)
                addTile(new Tile(i, 20 + j, 0, Tile.STDMASS, true));
    }

    protected boolean updateMass() { return false; }

    public int WIDTH () { return IMMOBILE_VEH_WIDTH;  }
    public int HEIGHT() { return IMMOBILE_VEH_HEIGHT; }

    protected float pushBackAndFixMoveX(Rectangle rect, float xSpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable || rect instanceof PlayerPieces) {
            RelativeMovable rel = null;

            if (rect instanceof PlayerPieces)
                rel = ((PlayerPieces) rect).getPlayer();
            else
                rel = (RelativeMovable) rect;

            if (first) {
                rel.pushY(-rel.getMass() * (rel.getAbsYSpeed() - getAbsYSpeed()) * world.frictionFraction() * world.view().diff());
                rel.pushX(-rel.getMass() * xSpeed);
            }
        }

        return fixMove;
    }
    protected float pushBackAndFixMoveY(Rectangle rect, float ySpeed, float fixMove, boolean first) {
        if (rect instanceof RelativeMovable || rect instanceof PlayerPieces) {
            RelativeMovable rel = null;

            if (rect instanceof PlayerPieces)
                rel = ((PlayerPieces) rect).getPlayer();
            else
                rel = (RelativeMovable) rect;

            if (first) {
                rel.pushX(-rel.getMass() * (rel.getAbsXSpeed() - getAbsXSpeed()) * world.frictionFraction() * world.view().diff());
                rel.pushY(-rel.getMass() * ySpeed);
            }
        }

        return fixMove;
    }

    public boolean collidedWithImmobileX() { return true; }
    public boolean collidedWithImmobileY() { return true; }

    public void pushX(float momentum) {}
    public void pushY(float momentum) {}

    public void update(GameContainer gc, int diff) {}

}
