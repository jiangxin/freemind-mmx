/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import freemind.modes.ControllerAdapter;


////////////
//  Actions
///////////

public class NewMapAction extends AbstractAction {
	private final ControllerAdapter modeController;
    ControllerAdapter c;
    public NewMapAction(ControllerAdapter modeController, ControllerAdapter controller) {
        super(modeController.getText("new"), new ImageIcon(modeController.getResource("images/filenew.png")));
		this.modeController = modeController;
        c = controller;
        //Workaround to get the images loaded in jar file.
        //they have to be added to jar manually with full path from root
        //I really don't like this, but it's a bug of java
    }
    public void actionPerformed(ActionEvent e) {
        c.newMap();
    }
}