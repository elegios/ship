package ship.launch;

import javax.swing.JOptionPane;

import org.newdawn.slick.SlickException;

import ship.View;
import ship.netcode.Network;

public class Controller {

    /**
     * Asks whether the game should be played in single player or multiplayer.
     * In the former case launches the game, in the latter case a new MultiPlayerDialog
     * is created.
     * @throws SlickException
     */
    public static void main(String[] args) throws SlickException {
        int opt = JOptionPane.showOptionDialog(null,
                                               "How do you want to play?",
                                               "Single or Multi",
                                               JOptionPane.DEFAULT_OPTION,
                                               JOptionPane.QUESTION_MESSAGE,
                                               null,
                                               new String[] {"Single Player", "Multi Player"},
                                               "Single Player");

        if (opt == 0) //Single player
            View.create(1920, 1080, new Network(), 0, 1);

        else if (opt == 1) //Multi player
            new MultiPlayerDialog(new Network()).setVisible(true);

        else
            System.exit(0);
    }

}
