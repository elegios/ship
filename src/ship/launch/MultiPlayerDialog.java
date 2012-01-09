package ship.launch;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.newdawn.slick.SlickException;

import ship.View;
import dataverse.datanode.ChangeListener;
import dataverse.datanode.easy.EasyNode;
import dataverse.datanode.network.ConnectListener;
import dataverse.datanode.network.NetworkNode;

@SuppressWarnings("serial")
public class MultiPlayerDialog extends JFrame implements ConnectListener, ChangeListener {

    private final JPanel contentPanel = new JPanel();

    private JTextField addressField;
    private JTextField portField;
    private JTextArea textArea;

    private JButton btnHost;
    private JButton btnConnect;
    private JButton btnStart;

    private NetworkNode netNode;

    private EasyNode node;

    private int id;
    private int numPlayers;

    private void netNodeInit(String pattern) {
        addressField.setEnabled(false);
        portField.setEnabled(false);
        btnHost.setEnabled(false);
        btnConnect.setEnabled(false);

        netNode = new NetworkNode(node.getNode(), pattern, Controller.UPDATE_INTERVAL_NETWORK);
        netNode.addConnectListener(this);
        netNode.addChangeListener(this);

    }

    private void host() {
        netNodeInit("^((?!player)).+|player\\.0\\..+");
        id = 0;
        numPlayers = 1;

        try {
            netNode.hostServer(Integer.parseInt(portField.getText()));
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        textArea.append("Server started, press the \"Start\" button when all players have joined.\n");

        btnStart.setEnabled(true);
    }

    private void connect() {
        netNodeInit("player.id.request");

        try {
            netNode.connectToServer(addressField.getText(), Integer.parseInt(portField.getText()));
            id = new Random().nextInt();
            node.c("player.id.request", id);
        } catch (NumberFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void start() {
        node.c("game.seed", new Random().nextInt());
    }

    private void startGame(int seed) {
        netNode.remChangeListener (this);
        netNode.remConnectListener(this);

        setVisible(false);
        dispose();

        try {
            View.create(1500, 800, node, id, numPlayers);
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public MultiPlayerDialog(EasyNode node) {
        this.node = node;

        setBounds(100, 100, 450, 300);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        contentPanel.add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{220, 220, 0};
        gbl_panel.rowHeights = new int[]{49, 0};
        gbl_panel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

                        JPanel panel_1 = new JPanel();
                        panel_1.setBorder(new TitledBorder(null, "Adress", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
                        gbc_panel_1.anchor = GridBagConstraints.NORTH;
                        gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
                        gbc_panel_1.insets = new Insets(0, 0, 0, 5);
                        gbc_panel_1.gridx = 0;
                        gbc_panel_1.gridy = 0;
                        panel.add(panel_1, gbc_panel_1);
                        panel_1.setLayout(new GridLayout(0, 1, 0, 0));

                                addressField = new JTextField();
                                panel_1.add(addressField);
                                addressField.setColumns(10);

                JPanel panel_2 = new JPanel();
                panel_2.setBorder(new TitledBorder(null, "Port", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_panel_2 = new GridBagConstraints();
                gbc_panel_2.anchor = GridBagConstraints.NORTH;
                gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
                gbc_panel_2.gridx = 1;
                gbc_panel_2.gridy = 0;
                panel.add(panel_2, gbc_panel_2);
                panel_2.setLayout(new GridLayout(0, 1, 0, 0));

                        portField = new JTextField();
                        panel_2.add(portField);
                        portField.setColumns(10);
                        panel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{addressField, portField}));

                        textArea = new JTextArea();
                        GridBagConstraints gbc_textArea = new GridBagConstraints();
                        gbc_textArea.insets = new Insets(0, 0, 5, 0);
                        gbc_textArea.fill = GridBagConstraints.BOTH;
                        gbc_textArea.gridx = 0;
                        gbc_textArea.gridy = 1;
                        contentPanel.add(textArea, gbc_textArea);

                        JPanel panel_3 = new JPanel();
                        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
                        gbc_panel_3.fill = GridBagConstraints.BOTH;
                        gbc_panel_3.gridx = 0;
                        gbc_panel_3.gridy = 2;
                        contentPanel.add(panel_3, gbc_panel_3);

                        btnHost = new JButton("Host");
                        btnHost.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                host();
                            }
                        });
                        panel_3.add(btnHost);

                        btnConnect = new JButton("Connect");
                        btnConnect.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                connect();
                            }
                        });
                        panel_3.add(btnConnect);

                        btnStart = new JButton("Start");
                        btnStart.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                start();
                            }
                        });
                        btnStart.setEnabled(false);
                        panel_3.add(btnStart);
    }

    @Override
    public void clientConnected(Socket sock) {
        textArea.append("Got a connection from "+ sock.getInetAddress() +"\n");
    }

    @Override
    public void connected() {
        textArea.append("Connection established, now all we have to do is wait for the server to start the game.\n");
    }

    public void dataChanged   (String id, String  data) {}
    public void booleanChanged(String id, boolean data) {}
    public void floatChanged  (String id, float   data) {}
    public void intChanged    (String id, int     data) {
        if (id.equals("game.seed"))
            new Thread(new GameRunnable(data)).start();

        synchronized (this) {
            if (id.equals("player.id.request") && this.id == 0) {
                node.c("request.granted." +data, numPlayers++);
                node.c("game.numPlayers", numPlayers);
            }
        }

        if (id.equals("request.granted." +this.id)) {
            this.id = data;
            netNode.setPattern("player\\." +this.id+ "\\..+");
            textArea.append("Got the id " +this.id+ " from the server. And set pattern to \"" +netNode.getPattern()+ "\"\n");
        }

        if (id.equals("game.numPlayers")) {
            numPlayers = data;
            textArea.append(numPlayers+" players have connected to the server.\n");
        }

    }

    class GameRunnable implements Runnable {

        private int seed;

        public GameRunnable(int seed) {
            this.seed = seed;
        }

        @Override
        public void run() {
            startGame(seed);
        }

    }

}
