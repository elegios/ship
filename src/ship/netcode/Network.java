package ship.netcode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ship.View;
import ship.launch.MultiPlayerDialog;
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
import elegios.netcode.Connection;
import elegios.netcode.Package;
import elegios.netcode.PackageReceiver;
import elegios.netcode.Server;
import elegios.netcode.ServerListener;

public class Network implements ServerListener, PackageReceiver {

    private MultiPlayerDialog dia;

    private View view;

    private Server server;
    private List<Connection> connections;

    private PlayerBank players;

    private int[] collectedLatency;
    private Date  lastSent;

    private Connection connection;

    private int numPlayers;
    private int id;

    public void setServer(Server server) {
        this.server = server;
        id          = 0;
        numPlayers  = 1;

        connections = new ArrayList<>();
        players     = new PlayerBank(ShipProtocol.PING_END - ShipProtocol.PING_START + 1);

        players.addPlayer(id);
        players.setPlayerName(id, dia.getPlayerName());
    }

    public void setConnection(Connection connection) {
        this.connection = connection;

        players          = new PlayerBank(ShipProtocol.PING_END - ShipProtocol.PING_START + 1);
        collectedLatency = new int[ShipProtocol.PING_END - ShipProtocol.PING_START + 1];

        connection.setReceiver(this);
    }

    public void setDialog(MultiPlayerDialog dia) {
        this.dia = dia;
    }

    public void dialogRemoved() {
        dia = null;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void send(Package pack) {
        if (server != null)
            for (Connection conn: connections)
                conn.send(pack);

        else
            connection.send(pack);
    }
    public void send(int packType, Package pack) {
        if (server != null)
            for (Connection conn: connections)
                conn.send(packType, pack);

        else
            connection.send(packType, pack);
    }
    public void send(int signal) {
        if (server != null)
            for (Connection conn: connections)
                conn.send(signal);

        else
            connection.send(signal);
    }

    public boolean isServer() { return     server != null; }
    public boolean isClient() { return connection != null; }
    public boolean isOnline() { return isServer() || isClient(); }

    public int playerId  () { return         id; }
    public int numPlayers() { return numPlayers; }

    @Override
    public synchronized void newConnection(Connection conn) {
        connections.add(conn);

        guiMessage("Got a connection from " +conn.getInetAddress());

        ConnectionHolder holder = new ConnectionHolder(conn, this);
        conn.setReceiver(holder);
        conn.listen();

        conn.send(ShipProtocol.PLAYER_ID, new PlayerIdPackage  (  numPlayers));
        send   (ShipProtocol.NUM_PLAYERS, new NumPlayersPackage(++numPlayers));

        for (PlayerData data : players)
            conn.send(ShipProtocol.PLAYER_NAME, new PlayerNamePackage(data.playerId, data.playerName));

        guiMessage("There are now " +numPlayers+ " players connected to the server.");
    }

    /*
     * This method deals with the package types that both clients and servers can receive
     */
    private void passPackage(int type, Package pack) {
        try {
        if (view != null) {
            switch (type) {
                case ShipProtocol.REL_PLAYER_POS: {
                    RelativePlayerPositionPackage p = (RelativePlayerPositionPackage) pack;
                    view.world().findPlayer(p.getPlayerId()).receiveRelativePlayerPositionPackage(p);
                    break;
                }

                case ShipProtocol.PLAYER_MOVE: {
                    PlayerMovementPackage p = (PlayerMovementPackage) pack;
                    view.world().findPlayer(p.getId()).receivePlayerMovementPackage(p);
                    break;
                }

                case ShipProtocol.PLAYER_POS: {
                    PlayerPositionPackage p = (PlayerPositionPackage) pack;
                    view.world().findPlayer(p.getId()).receivePlayerPositionPackage(p);
                    break;
                }

                case ShipProtocol.BUILD_DIR: {
                    BuildDirectionPackage p = (BuildDirectionPackage) pack;
                    view.world().findPlayer(p.getPlayerId()).builder().receiveBuildDirectionPackage(p);
                    break;
                }

                case ShipProtocol.BUILD_MODE: {
                    BuildModePackage p = (BuildModePackage) pack;
                    view.world().findPlayer(p.getPlayerId()).builder().receiveBuildModePackage(p);
                    break;
                }

                case ShipProtocol.ITEM_AND_SUB:
                    if (view != null) {
                        ItemAndSubItemPackage p = (ItemAndSubItemPackage) pack;
                        view.world().findPlayer(p.getPlayerId()).builder().receiveItemAndSubItemPackage(p);
                    }
                    break;

                case ShipProtocol.ACTIVATE: {
                    ActivatePackage p = (ActivatePackage) pack;
                    view.world().findVehicle(p.getVehicleId()).tile(p.getVehX(), p.getVehY()).activate(view.world().findPlayer(p.getPlayerId()));
                    break;
                }

                case ShipProtocol.CREATE_TILE: {
                    CreateTilePackage p = (CreateTilePackage) pack;
                    view.world().findVehicle(p.getVehicleId()).receiveCreateTilePackage(p);
                    break;
                }

                case ShipProtocol.DELETE_TILE: {
                    DeleteTilePackage p = (DeleteTilePackage) pack;
                    view.world().findVehicle(p.getVehicleId()).receiveDeleteTilePackage(p);
                    break;
                }
            }
        }

        if (type == ShipProtocol.CHAT) {
            ChatPackage p = (ChatPackage) pack;
            guiMessage(players.getPlayerName(p.getPlayerId()) +": "+ p.getMessage());

        } else if (type == ShipProtocol.PLAYER_NAME) {
            PlayerNamePackage p = (PlayerNamePackage) pack;
            players.addPlayer(p.getPlayerId());
            players.setPlayerName(p.getPlayerId(), p.getPlayerName());
            guiMessage(p.getPlayerName() +" is here.");

        }
        } catch (Exception e) {
            System.out.println("\nGot an error in a connection listener"); //TODO: make game not start until everyone has finished loading
            e.printStackTrace();
        }
    }

    @Override
    public boolean wantsPackageOfType(int type) { return true; }

    /*
     * This method is only used when this is a server
     */
    public void receivePackage(Connection conn, int playerId, int type, Package pack) {
        if (type >= ShipProtocol.PING_START && type <= ShipProtocol.PING_END){
            conn.send(type);
            players.incrementPingCount(playerId);

            if (type == ShipProtocol.PING_END && players.pingReady()) {
                players.resetAllPings();
                send(ShipProtocol.INIT_GAME_START);
                view.world().initUnpauseTimer(0);
            }

            view.world().systemMessage(players.createPingStatusMessage());

            return;
        }

        if (type == ShipProtocol.PLAYER_MOVE    ||
            type == ShipProtocol.PLAYER_POS     ||
            type == ShipProtocol.ITEM_AND_SUB   ||
            type == ShipProtocol.BUILD_DIR      ||
            type == ShipProtocol.BUILD_MODE     ||
            type == ShipProtocol.REL_PLAYER_POS ||
            type == ShipProtocol.CHAT           ||
            type == ShipProtocol.PLAYER_NAME) { //These packages are distributed to all players but the sender
            for (Connection connection : connections) {
                if (connection != conn)
                    connection.send(type, pack);
            }
        } else if (type == ShipProtocol.CREATE_TILE ||
                   type == ShipProtocol.DELETE_TILE ||
                   type == ShipProtocol.ACTIVATE) { //These packages are bounced to the sender as well as all other players
            send(type, pack);
        }

        passPackage(type, pack);
    }

    /*
     * This method is only used when this is a client
     */
    @Override
    public void receivePackage(int type, Package pack) {
        if (type >= ShipProtocol.PING_START && type <= ShipProtocol.PING_END) { //It's a ping message, do calculations
            collectedLatency[type - ShipProtocol.PING_START] = (int) (new Date().getTime() - lastSent.getTime());

            if (type < ShipProtocol.PING_END) {
                lastSent = new Date();
                send(type + 1);
                view.world().systemMessage("Syncing (" +(type - ShipProtocol.PING_START)+ "/" +(collectedLatency.length) +")");
            } else
                view.world().systemMessage("Done syncing. Waiting for others.");

            return;
        }

        switch (type) {
            case ShipProtocol.VEHICLE_POS: {
                if (view != null) {
                    VehiclePositionPackage p = (VehiclePositionPackage) pack;
                    view.world().findVehicle(p.getId()).receiveVehiclePositionPackage(p);
                }
                break;
            }

            case ShipProtocol.CONTENT: {
                if (view != null) {
                    ContentPackage p = (ContentPackage) pack;
                    view.world().findVehicle(p.getVehicleId()).tile(p.getVehX(), p.getVehY()).receiveContentPackage(p);
                }
                break;
            }

            case ShipProtocol.PLAYER_ID:
                id = ((PlayerIdPackage) pack).getPlayerId();

                guiMessage("Got the id " +id);

                players.addPlayer(id);
                players.setPlayerName(id, dia.getPlayerName());
                send(new PlayerNamePackage(id, dia.getPlayerName()));

                break;

            case ShipProtocol.NUM_PLAYERS:
                numPlayers = ((NumPlayersPackage) pack).getNumPlayers();

                guiMessage("There are now " +numPlayers+ " players connected to the server.");

                break;

            case ShipProtocol.INIT_GAME_START:
                int latency = 0;

                for (int i : collectedLatency) latency += i;
                latency /= collectedLatency.length;

                view.world().initUnpauseTimer(latency);

                for (int i : collectedLatency) System.out.println(i);
                System.out.println("latency calculated to: " +latency); //TODO: REMOVE

                break;

            case ShipProtocol.INIT_GAME_LOAD:
                if (dia != null)
                    dia.start();

                break;

            case ShipProtocol.PAUSE:
                view.world().pause();

                break;

            case ShipProtocol.TIME_SYNC:
                TimeSyncPackage p = (TimeSyncPackage) pack;
                view.world().timeSync(p.getTime(), p.getTimeTilUpdatePos());

                lastSent = new Date();
                send(ShipProtocol.PING_START);

                break;

            default:
                passPackage(type, pack);
        }
    }

    public void initUnpause(int time, int timeTilUpdatePos) {
        send(ShipProtocol.TIME_SYNC, new TimeSyncPackage(time, timeTilUpdatePos));
        view.world().systemMessage(players.createPingStatusMessage());
    }

    public void guiMessage(String message) {
        if (view != null){
            Calendar now = Calendar.getInstance();
            view.appendText("[" +now.get(Calendar.HOUR_OF_DAY)+ ":" +now.get(Calendar.MINUTE)+ ":" +now.get(Calendar.SECOND)+ "] " +message);
        } else if (dia != null) {
            Calendar now = Calendar.getInstance();
            dia.appendText("[" +now.get(Calendar.HOUR_OF_DAY)+ ":" +now.get(Calendar.MINUTE)+ ":" +now.get(Calendar.SECOND)+ "] " +message);
        }
    }

    public void sendChatMessage(String message) {
        send(ShipProtocol.CHAT, new ChatPackage(id, message));
        guiMessage(players.getPlayerName(id) +": "+ message);
    }

    public String getPlayerName(int playerId) {
        return players.getPlayerName(playerId);
    }

}

class ConnectionHolder implements PackageReceiver {

    private Connection connection;
    private Network net;

    private int playerId;

    ConnectionHolder(Connection connection, Network net) {
        this.connection = connection;
        this.net        = net;
    }

    @Override
    public boolean wantsPackageOfType(int type) { return true; }

    @Override
    public void receivePackage(int type, Package pack) {
        net.receivePackage(connection, playerId, type, pack);
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

}
