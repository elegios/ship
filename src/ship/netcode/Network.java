package ship.netcode;

import java.util.ArrayList;
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
import ship.netcode.meta.NumPlayersPackage;
import ship.netcode.meta.PlayerIdPackage;
import ship.netcode.movement.PlayerPositionPackage;
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

    private Connection connection;

    private int numPlayers;
    private int id;

    public void setServer(Server server) {
        this.server = server;
        id          = 0;
        numPlayers  = 1;

        connections = new ArrayList<>();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;

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

        guiMessage("There are now " +numPlayers+ " players connected to the server.");
    }

    /*
     * This method deals with the package types that both clients and servers can receive
     */
    private void passPackage(int type, Package pack) {
        //TODO: pass packages to the intended receiver
        try {
            switch (type) {
                case ShipProtocol.PLAYER_POS: {
                    PlayerPositionPackage p = (PlayerPositionPackage) pack;
                    view.world().findPlayer(p.getId()).receivePlayerPositionPackage(p);
                    break;
                }

                case ShipProtocol.PLAYER_MOVE: {
                    PlayerMovementPackage p = (PlayerMovementPackage) pack;
                    view.world().findPlayer(p.getId()).receivePlayerMovementPackage(p);
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

                case ShipProtocol.ITEM_AND_SUB: {
                    ItemAndSubItemPackage p = (ItemAndSubItemPackage) pack;
                    view.world().findPlayer(p.getPlayerId()).builder().receiveItemAndSubItemPackage(p);
                    break;
                }

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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean wantsPackageOfType(int type) { return true; }

    /*
     * This method is only used when this is a server
     */
    public void receivePackage(Connection conn, int type, Package pack) {
        if (type == ShipProtocol.PLAYER_MOVE  ||
            type == ShipProtocol.PLAYER_POS   ||
            type == ShipProtocol.ITEM_AND_SUB ||
            type == ShipProtocol.BUILD_DIR    ||
            type == ShipProtocol.BUILD_MODE) {
            for (Connection connection : connections) {
                if (connection != conn)
                    connection.send(type, pack);
            }
        } else if (type == ShipProtocol.CREATE_TILE ||
                   type == ShipProtocol.DELETE_TILE ||
                   type == ShipProtocol.ACTIVATE) {
            send(type, pack);
        }

        passPackage(type, pack);
    }

    /*
     * This method is only used when this is a client
     */
    @Override
    public void receivePackage(int type, Package pack) {
        switch (type) {
            case ShipProtocol.VEHICLE_POS: {
                VehiclePositionPackage p = (VehiclePositionPackage) pack;
                view.world().findVehicle(p.getId()).receiveVehiclePositionPackage(p);
                break;
            }

            case ShipProtocol.CONTENT: {
                ContentPackage p = (ContentPackage) pack;
                view.world().findVehicle(p.getVehicleId()).tile(p.getVehX(), p.getVehY()).receiveContentPackage(p);
                break;
            }

            case ShipProtocol.PLAYER_ID:
                id = ((PlayerIdPackage) pack).getPlayerId();

                guiMessage("Got the id " +id);

                break;

            case ShipProtocol.NUM_PLAYERS:
                numPlayers = ((NumPlayersPackage) pack).getNumPlayers();

                guiMessage("There are now " +numPlayers+ " connected to the server.");

                break;

            case ShipProtocol.GAME_START:
                if (dia != null)
                    dia.start();

                break;

            default:
                passPackage(type, pack);
        }
    }

    private void guiMessage(String message) {
        if (dia != null)
            dia.appendText(message);
        else {
            //TODO: add method to write guiMessages to the in game client
        }
    }

}

class ConnectionHolder implements PackageReceiver {

    private Connection connection;
    private Network net;

    ConnectionHolder(Connection connection, Network net) {
        this.connection = connection;
        this.net        = net;
    }

    @Override
    public boolean wantsPackageOfType(int type) { return true; }

    @Override
    public void receivePackage(int type, Package pack) {
        net.receivePackage(connection, type, pack);
    }

}
