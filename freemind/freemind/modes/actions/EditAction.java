/*
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;


//
// Node editing
//

public class EditAction extends AbstractAction implements ActorXml {
	private final ControllerAdapter modeController;
    public EditAction(ControllerAdapter modeController) {
        super(modeController.getText("edit"));
		this.modeController = modeController;
		this.modeController.getActionFactory().registerActor(this, getDoActionClass());
    }
	public void actionPerformed(ActionEvent arg0) {
		MindMapNode selected = this.modeController.getSelected();
		this.modeController.edit(null, false, false);
	}
	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		System.out.println("EditNodeAction");
		EditNodeAction editAction = (EditNodeAction) action;
		NodeAdapter node = this.modeController.getNodeFromID(editAction.getNode());
		if(!node.toString().equals(editAction.getText())) {
			node.setUserObject(editAction.getText());
			this.modeController.nodeChanged(node);
		}
	}
	/* (non-Javadoc)
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return EditNodeAction.class;
	}
	
	public void setNodeText(MindMapNode node, String text) {
		modeController.changeNodeText(node, text);
	}
}