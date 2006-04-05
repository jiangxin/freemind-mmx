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
/* $Id: DeleteChildAction.java,v 1.1.2.2.2.1 2006-04-05 21:26:28 dpolivaev Exp $ */

package freemind.modes.mindmapmode.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.PermanentNodeHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.PasteAction.NodeCoordinate;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;


public class DeleteChildAction extends AbstractAction implements ActorXml {
    private final MindMapController pMindMapController;
    private String text;
    public DeleteChildAction(MindMapController modeController) {
        super(modeController.getText("remove_node"), new ImageIcon(
                modeController.getResource("images/editdelete.png")));
		text = modeController.getText("remove_node");
        this.pMindMapController = modeController;
		this.pMindMapController.getActionFactory().registerActor(this, getDoActionClass());
    }

    public void actionPerformed(ActionEvent e) {
        // because of multiple selection, cut is better.
    	pMindMapController.cut();
       //this.c.deleteNode(c.getSelected());
    }
    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
     */
    public void act(XmlAction action) {
		DeleteNodeAction deleteNodeAction = (DeleteNodeAction) action;
		MindMapNode selectedNode = this.pMindMapController.getNodeFromID(deleteNodeAction.getNode());
		deleteWithoutUndo(selectedNode);
    }
    /**
     * @param selectedNode
     */
    public void deleteWithoutUndo(MindMapNode selectedNode) {
        // deregister node:
		pMindMapController.getModel().getLinkRegistry().deregisterLinkTarget(selectedNode);
        // remove hooks:
		long currentRun = 0;
		// determine timeout:
		long timeout = selectedNode.getActivatedHooks().size() * 2 + 2;
        while(selectedNode.getActivatedHooks().size() > 0) {
            PermanentNodeHook hook = (PermanentNodeHook) selectedNode.getActivatedHooks().iterator().next();
            selectedNode.removeHook(hook);
            if(currentRun++ > timeout) {
                throw new IllegalStateException("Timeout reached shutting down the hooks.");
            }
        }
		pMindMapController.getModel().removeNodeFromParent( selectedNode);
    }

    /* (non-Javadoc)
     * @see freemind.controller.actions.ActorXml#getDoActionClass()
     */
    public Class getDoActionClass() {
        return DeleteNodeAction.class;
    }

	public void deleteNode(MindMapNode selectedNode){
		String newId = pMindMapController.getNodeID(selectedNode);
        pMindMapController.getActionFactory().startTransaction(text);
        MindMapNode parent = selectedNode.getParentNode();

        Transferable copy = pMindMapController.getModel().copy(selectedNode);
        NodeCoordinate coord = new NodeCoordinate(selectedNode, selectedNode.isLeft().getValue());
        // Undo-action
        PasteNodeAction pasteNodeAction=null;
        pasteNodeAction = pMindMapController.paste.getPasteNodeAction(copy, coord);

        DeleteNodeAction deleteAction = getDeleteNodeAction(newId);
        pMindMapController.getActionFactory().executeAction(new ActionPair(deleteAction, pasteNodeAction));
        pMindMapController.getActionFactory().endTransaction(text);
	}

	public DeleteNodeAction getDeleteNodeAction(String newId)
		 {
		DeleteNodeAction deleteAction = new DeleteNodeAction();
		deleteAction.setNode(newId);
		return deleteAction;
	}


}
