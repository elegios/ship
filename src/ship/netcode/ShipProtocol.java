package ship.netcode;

import ship.netcode.interaction.ActivatePackage;
import ship.netcode.interaction.CreateTilePackage;
import ship.netcode.interaction.DeleteTilePackage;
import ship.netcode.interaction.PlayerMovementPackage;
import ship.netcode.inventory.BuildDirectionPackage;
import ship.netcode.inventory.BuildModePackage;
import ship.netcode.inventory.ItemAndSubItemPackage;
import ship.netcode.meta.ChatPackage;
import ship.netcode.meta.NumPlayersPackage;
import ship.netcode.meta.PlayerIdPackage;
import ship.netcode.meta.PlayerNamePackage;
import ship.netcode.meta.TimeSyncPackage;
import ship.netcode.movement.PlayerPositionPackage;
import ship.netcode.movement.RelativePlayerPositionPackage;
import ship.netcode.movement.VehiclePositionPackage;
import ship.netcode.tile.ContentPackage;
import elegios.netcode.BasicProtocol;

public class ShipProtocol extends BasicProtocol {

    public static final int PING_START = -20; //this means there will be ten message-pairs altogether
    public static final int PING_END   = -11;

    public static final int PAUSE           = -3;
    public static final int INIT_GAME_START = -2;
    public static final int INIT_GAME_LOAD  = -1;

    public static final int TIME_SYNC   = 16;
    public static final int PLAYER_ID   = 1;
    public static final int NUM_PLAYERS = 2;
    public static final int CHAT        = 14;
    public static final int PLAYER_NAME = 15;

    public static final int PLAYER_POS     = 3;
    public static final int REL_PLAYER_POS = 13;
    public static final int VEHICLE_POS    = 4;

    public static final int ACTIVATE    = 5;
    public static final int PLAYER_MOVE = 6;
    public static final int CREATE_TILE = 7;
    public static final int DELETE_TILE = 8;

    public static final int ITEM_AND_SUB = 9;
    public static final int BUILD_DIR    = 10;
    public static final int BUILD_MODE   = 11;

    public static final int CONTENT      = 12;

    public ShipProtocol() {
        super();

        addType(new PlayerIdPackage  ());
        addType(new NumPlayersPackage());

        addType(new PlayerPositionPackage ());
        addType(new VehiclePositionPackage());

        addType(new ActivatePackage      ());
        addType(new PlayerMovementPackage());
        addType(new CreateTilePackage    ());
        addType(new DeleteTilePackage    ());

        addType(new ItemAndSubItemPackage());
        addType(new BuildDirectionPackage());
        addType(new BuildModePackage     ());

        addType(new ContentPackage());

        addType(new RelativePlayerPositionPackage());

        addType(new ChatPackage      ());
        addType(new PlayerNamePackage());

        addType(new TimeSyncPackage());
    }

}
