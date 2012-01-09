package ship.launch;

import javax.swing.JOptionPane;

import org.newdawn.slick.SlickException;

import ship.View;
import dataverse.cliaccess.CLIAccess;
import dataverse.datanode.easy.EasyBlockingNode;
import dataverse.datanode.easy.EasyNode;
import dataverse.datanode.flat.FlatNode;

public class Controller {
    public static final int UPDATE_INTERVAL_NETWORK = 0; //0 means immediate changes

    private EasyNode node;

    public Controller() {
        node = new EasyBlockingNode(new FlatNode(null, 1000));
        CLIAccess.startCLIAccess(System.in, System.out, node.getNode(), null);
    }

    public void start() throws SlickException {
        int opt = JOptionPane.showOptionDialog(null,
                                               "How do you want to play?",
                                               "Single or Multi",
                                               JOptionPane.DEFAULT_OPTION,
                                               JOptionPane.QUESTION_MESSAGE,
                                               null,
                                               new String[] {"Single Player", "Multi Player"},
                                               "Single Player");

        if (opt == 0) { //Single player
            View.create(1900, 1080, node, 0, 1);
        } else if (opt == 1) { //Multi player
            new MultiPlayerDialog(node).setVisible(true);
        }

        //Anything is probably CLOSED_OPTION, in which case the program should close.
    }

    public static void main(String[] args) throws SlickException {
        new Controller().start();
    }

}
