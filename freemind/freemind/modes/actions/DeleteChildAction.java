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
/*$Id: DeleteChildAction.java,v 1.1.2.1 2004-05-06 05:08:26 christianfoltin Exp $*/

package freemind.modes.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ControllerAdapter;
import freemind.modes.NodeAdapter;


public class DeleteChildAction extends AbstractAction implements ActorXml {
    private final ControllerAdapter modeController;
    public DeleteChildAction(ControllerAdapter modeController) {
        super(modeController.getText("delete_child"));
        this.modeController = modeController;
		this.modeController.getActionFactory().registerActor(this, getDoActionClass());
    }

    public void actionPerformed(ActionEvent e) {
       this.modeController.addNew(modeController.getView().getSelected(), modeController.NEW_CHILD, null);
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
		System.out.println("NewNodeAction");
		DeleteNodeAction addNodeAction = (DeleteNodeAction) action;
		NodeAdapter node = this.modeController.getNodeFromID(addNodeAction.getNode());
		modeController.getModel().getLinkRegistry().deregisterLinkTarget(node);
		// URGENT: Deletion of hooks, links, etc.
		modeController.getModel().removeNodeFromParent((MutableTreeNode) node);
		// deregister node:
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return DeleteNodeAction.class;
    }
}
