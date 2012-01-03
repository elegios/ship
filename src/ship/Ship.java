/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ship;

import org.newdawn.slick.SlickException;

import dataverse.cliaccess.CLIAccess;
import dataverse.datanode.easy.EasyBlockingNode;
import dataverse.datanode.easy.EasyNode;
import dataverse.datanode.flat.FlatNode;

/**
 *
 * @author elegios
 */
class Ship {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SlickException {
        EasyNode node = new EasyBlockingNode(new FlatNode(null, 1000));
        CLIAccess.startCLIAccess(System.in, System.out, node.getNode(), null);

        View.create(1900, 1080, node);
    }
}
