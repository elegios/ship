package ship.netcode;

import java.util.ArrayList;
import java.util.List;

import ship.launch.MultiPlayerDialog;
import ship.netcode.meta.NumPlayersPackage;
import ship.netcode.meta.PlayerIdPackage;
import elegios.netcode.Connection;
import elegios.netcode.Package;
import elegios.netcode.PackageReceiver;
import elegios.netcode.Server;
import elegios.netcode.ServerListener;

public class Network implements ServerListener, PackageReceiver {

    private MultiPlayerDialog dia;

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

        conn.send(ShipProtocol.PLAYER_ID, new PlayerIdPackage  (  numPlayers));
        send   (ShipProtocol.NUM_PLAYERS, new NumPlayersPackage(++numPlayers));

        guiMessage("There are now " +numPlayers+ " players connected to the server.");
    }

    /*
     * This method is only used when this is a client
     */
    @Override
    public boolean wantsPackageOfType(int type) {
        return type == ShipProtocol.PLAYER_ID ||
               type == ShipProtocol.NUM_PLAYERS;
    }

    /*
     * This method is only used when this is a client
     */
    @Override
    public void receivePackage(int type, Package pack) {
        switch (type) {
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
