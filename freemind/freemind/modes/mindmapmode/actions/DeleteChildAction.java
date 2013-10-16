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


package freemind.modes.mindmapmode.actions;

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.UndoPasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.PermanentNodeHook;
import freemind.main.FreeMind;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.PasteAction.NodeCoordinate;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

public class DeleteChildAction extends AbstractAction implements ActorXml {
	private final MindMapController mMindMapController;
	private String text;

	public DeleteChildAction(MindMapController modeController) {
		super(modeController.getText("remove_node"), new ImageIcon(
				modeController.getResource("images/editdelete.png")));
		text = modeController.getText("remove_node");
		this.mMindMapController = modeController;
		this.mMindMapController.getActionFactory().registerActor(this,
				getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		// ask user if not root is selected:
		for (Iterator iterator = mMindMapController.getSelecteds().iterator(); iterator
				.hasNext();) {
			MindMapNode node = (MindMapNode) iterator.next();
			if (node.isRoot()) {
				mMindMapController.getController().errorMessage(
						mMindMapController.getFrame().getResourceString(
								"cannot_delete_root"));
				return;
			}
		}
		int showResult = new OptionalDontShowMeAgainDialog(mMindMapController
				.getFrame().getJFrame(), mMindMapController.getSelectedView(),
				"really_remove_node", "confirmation", mMindMapController,
				new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
						mMindMapController.getController(),
						FreeMind.RESOURCES_DELETE_NODES_WITHOUT_QUESTION),
				OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED)
				.show().getResult();
		if (showResult != JOptionPane.OK_OPTION) {
			return;
		}
		// because of multiple selection, cut is better.
		mMindMapController.cut();
		// this.c.deleteNode(c.getSelected());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		DeleteNodeAction deleteNodeAction = (DeleteNodeAction) action;
		MindMapNode selectedNode = this.mMindMapController
				.getNodeFromID(deleteNodeAction.getNode());
		deleteWithoutUndo(selectedNode);
	}

	/**
     */
	public void deleteWithoutUndo(MindMapNode selectedNode) {
		// remove hooks:
		removeHooks(selectedNode);
		MindMapNode parent = selectedNode.getParentNode();
		mMindMapController.fireNodePreDeleteEvent(selectedNode);
		// deregister node:
		mMindMapController.getModel().getLinkRegistry()
				.deregisterLinkTarget(selectedNode);
		// deselect
		MapView view = mMindMapController.getView();
		NodeView nodeView = view.getNodeView(selectedNode);
		view.deselect(nodeView);
		if(view.getSelecteds().size() == 0) {
			NodeView newSelectedView;
			int childIndex = parent.getChildPosition(selectedNode);
			if(parent.getChildCount() > childIndex+1) {
				// the next node
				newSelectedView = view.getNodeView((MindMapNode) parent.getChildAt(childIndex+1));
			} else if(childIndex > 0) {
				// the node before:
				newSelectedView = view.getNodeView((MindMapNode) parent.getChildAt(childIndex-1));
			} else {
				// no other node on same level. take the parent.
				newSelectedView = view.getNodeView(parent);
			}
			view.selectAsTheOnlyOneSelected(newSelectedView);
		}
		mMindMapController.removeNodeFromParent(selectedNode);
		// post event
		mMindMapController.fireNodePostDeleteEvent(selectedNode, parent);
	}

	private void removeHooks(MindMapNode selectedNode) {
		for (Iterator it = selectedNode.childrenUnfolded(); it.hasNext();) {
			MindMapNode child = (MindMapNode) it.next();
			removeHooks(child);
		}
		long currentRun = 0;
		// determine timeout:
		long timeout = selectedNode.getActivatedHooks().size() * 2 + 2;
		while (selectedNode.getActivatedHooks().size() > 0) {
			PermanentNodeHook hook = (PermanentNodeHook) selectedNode
					.getActivatedHooks().iterator().next();
			selectedNode.removeHook(hook);
			if (currentRun++ > timeout) {
				throw new IllegalStateException(
						"Timeout reached shutting down the hooks.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return DeleteNodeAction.class;
	}

	public void deleteNode(MindMapNode selectedNode) {
		String newId = mMindMapController.getNodeID(selectedNode);

		Transferable copy = mMindMapController.copy(selectedNode, true);
		NodeCoordinate coord = new NodeCoordinate(selectedNode,
				selectedNode.isLeft());
		// Undo-action
		PasteNodeAction pasteNodeAction = null;
		pasteNodeAction = mMindMapController.paste.getPasteNodeAction(copy,
				coord, (UndoPasteNodeAction) null);

		DeleteNodeAction deleteAction = getDeleteNodeAction(newId);
		mMindMapController.doTransaction(text,
				new ActionPair(deleteAction, pasteNodeAction));
	}

	public DeleteNodeAction getDeleteNodeAction(String newId) {
		DeleteNodeAction deleteAction = new DeleteNodeAction();
		deleteAction.setNode(newId);
		return deleteAction;
	}

}
