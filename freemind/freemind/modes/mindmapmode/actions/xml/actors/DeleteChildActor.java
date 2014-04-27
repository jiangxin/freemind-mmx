/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
*/

package freemind.modes.mindmapmode.actions.xml.actors;

import java.awt.datatransfer.Transferable;
import java.util.Iterator;

import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.UndoPasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.PermanentNodeHook;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ViewAbstraction;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.actors.PasteActor.NodeCoordinate;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * @date 18.03.2014
 */
public class DeleteChildActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public DeleteChildActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
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
		MindMapNode selectedNode = getNodeFromID(deleteNodeAction.getNode());
		deleteWithoutUndo(selectedNode);
	}

	/**
     */
	public void deleteWithoutUndo(MindMapNode selectedNode) {
		if(selectedNode.isRoot()) {
			throw new IllegalArgumentException("Root node can't be deleted");
		}
		// remove hooks:
		removeHooks(selectedNode);
		MindMapNode parent = selectedNode.getParentNode();
		getExMapFeedback().fireNodePreDeleteEvent(selectedNode);
		// deregister node:
		MindMap map = getExMapFeedback().getMap();
		map.getLinkRegistry()
				.deregisterLinkTarget(selectedNode);
		// deselect
		ViewAbstraction view = getExMapFeedback().getViewAbstraction();
		if(view != null) {
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
				view.select(newSelectedView);
			}
		}
		getExMapFeedback().removeNodeFromParent(selectedNode);
		// post event
		getExMapFeedback().fireNodePostDeleteEvent(selectedNode, parent);
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
		if(selectedNode.isRoot()) {
			throw new IllegalArgumentException("Root node can't be deleted");
		}
		String newId = getNodeID(selectedNode);

		Transferable copy = getExMapFeedback().copy(selectedNode, true);
		NodeCoordinate coord = new NodeCoordinate(selectedNode,
				selectedNode.isLeft());
		// Undo-action
		PasteNodeAction pasteNodeAction = null;
		pasteNodeAction = getExMapFeedback().getActorFactory().
				getPasteActor().getPasteNodeAction(copy,
				coord, (UndoPasteNodeAction) null);

		DeleteNodeAction deleteAction = getDeleteNodeAction(newId);
		getExMapFeedback().doTransaction(getDoActionClass().getName(),
				new ActionPair(deleteAction, pasteNodeAction));
	}

	public DeleteNodeAction getDeleteNodeAction(String newId) {
		DeleteNodeAction deleteAction = new DeleteNodeAction();
		deleteAction.setNode(newId);
		return deleteAction;
	}

}
