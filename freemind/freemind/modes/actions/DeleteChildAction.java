/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 05.05.2004
 */
/*$Id: DeleteChildAction.java,v 1.1.2.3 2004-08-08 13:03:48 christianfoltin Exp $*/

package freemind.modes.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.bind.JAXBException;

import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;


public class DeleteChildAction extends AbstractAction implements ActorXml {
    private final ControllerAdapter c;
    private String text;
    public DeleteChildAction(ControllerAdapter modeController) {
        super(modeController.getText("remove_node"));
		text = modeController.getText("remove_node");
        this.c = modeController;
		this.c.getActionFactory().registerActor(this, getDoActionClass());
    }

    public void actionPerformed(ActionEvent e) {
    	c.cut();
       //this.c.deleteNode(c.getSelected());
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
		System.out.println("NewNodeAction");
		DeleteNodeAction deleteNodeAction = (DeleteNodeAction) action;
		NodeAdapter node = this.c.getNodeFromID(deleteNodeAction.getNode());
		c.select(node.getViewer());
		c.cut();
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return DeleteNodeAction.class;
    }
    
	public void deleteNode(MindMapNode selectedNode){
		try {
			String newId = c.getNodeID(selectedNode);
			c.getActionFactory().startTransaction(text);
			MindMapNode parent = selectedNode.getParentNode();
			//URGENT: this is wrong: cut node would be the right here.
            NewNodeAction newNodeAction =
                c.newChild.getAddNodeAction(
                    parent,
                    parent.getChildPosition(selectedNode),
                    newId,
                    null);
			// Undo-action
			DeleteNodeAction deleteAction = getDeleteNodeAction(newId);
			c.getActionFactory().executeAction(new ActionPair(deleteAction, newNodeAction));
			c.getActionFactory().endTransaction(text);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public DeleteNodeAction getDeleteNodeAction(String newId)
		throws JAXBException {
		DeleteNodeAction deleteAction = c.getActionXmlFactory().createDeleteNodeAction();
		deleteAction.setNode(newId);
		return deleteAction;
	}


}
