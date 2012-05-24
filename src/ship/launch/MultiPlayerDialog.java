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
import ship.netcode.Network;
import ship.netcode.ShipProtocol;
import elegios.netcode.Connection;
import elegios.netcode.Server;

@SuppressWarnings("serial")
public class MultiPlayerDialog extends JFrame {

    private final JPanel contentPanel = new JPanel();

    private JTextField addressField;
    private JTextField portField;
    private JTextArea textArea;

    private JButton btnHost;
    private JButton btnConnect;
    private JButton btnStart;

    private Network net;
    private ShipProtocol protocol;

    /**
     * Calls netNodeInit and hosts a server on the selected port. Also
     * enables the Start button.
     */
    private void host() {
        btnHost.setEnabled(false);
        btnConnect.setEnabled(false);

        try {
            net.setServer(Server.host(protocol, net, Integer.parseInt(portField.getText())));
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }

        textArea.append("Server started, press the \"Start\" button when all players have joined.\n");

        btnStart.setEnabled(true);
    }

    /**
     * Calls netNodeInit and connects to the selected address on the
     * selected port. Will not enable anything ui related.
     */
    private void connect() {
        btnHost.setEnabled(false);
        btnConnect.setEnabled(false);

        try {
            Connection conn = new Connection(protocol, addressField.getText(), Integer.parseInt(portField.getText()));
            conn.connect();
            net.setConnection(conn);
            conn.listen();

        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If this is a server, sends a GAME_START signal, then starts
     * the game regardless of this being a server or not.
     */
    public void start() {
        if (net.isServer())
            net.send(ShipProtocol.GAME_START);

        new Thread(new Runnable() {

            @Override
            public void run() {
                startGame();
            }

        }, "main game thread").start();
    }

    /**
     * Called when game.seed has been set. Disposes of the current window
     * and starts the game client window.
     * @param seed
     */
    private void startGame() {
        setVisible(false);
        dispose();

        try {
            View.create(1500, 800, net, net.playerId(), net.numPlayers());
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the graphical components of the dialog.
     */
    public MultiPlayerDialog(Network net) {
        this.net = net;
        net.setDialog(this);
        protocol = new ShipProtocol();

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
                                //addressField.setText("90.130.22.183"); //TODO: enable
                                addressField.setText("localhost");
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
                        //portField.setText("80"); //TODO: enable
                        portField.setText("7780");
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

    public void appendText(String text) {
        textArea.append(text + "\n");
    }

}
