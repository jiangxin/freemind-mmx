/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 by Christian Foltin
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


/*
 * Created on 19.02.2006
 *
 */

package accessories.plugins;

import java.awt.datatransfer.Transferable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 */
public class ChangeNodeLevelAction extends MindMapNodeHookAdapter {

	/**
	 *
	 */
	public ChangeNodeLevelAction() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode,
	 * java.util.List)
	 */
	public void invoke(MindMapNode rootNode) {
		// we dont need node.
		MindMapNode selectedNode;
		List selectedNodes;
		{
			MindMapNode focussed = getMindMapController().getSelected();
			List selecteds = getMindMapController().getSelecteds();
			selectedNode = focussed;
			selectedNodes = selecteds;
		}

		// bug fix: sort to make independent by user's selection:
		getMindMapController().sortNodesByDepth(selectedNodes);

		if (selectedNode.isRoot()) {
			getMindMapController().getController().errorMessage(
					getResourceString("cannot_add_parent_to_root"));
			return;
		}

		boolean upwards = Tools.safeEquals("left",
				getResourceString("action_type")) != selectedNode.isLeft();
		// Make sure the selected nodes all have the same parent
		// (this restriction is to simplify the action, and could
		// possibly be removed in the future, when we have undo)
		// Also make sure that none of the selected nodes are the root node
		MindMapNode selectedParent = selectedNode.getParentNode();
		for (Iterator it = selectedNodes.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			if (node.getParentNode() != selectedParent) {
				getMindMapController().getController().errorMessage(
						getResourceString("cannot_add_parent_diff_parents"));
				return;
			}
			if (node == rootNode) {
				getMindMapController().getController().errorMessage(
						getResourceString("cannot_add_parent_to_root"));
				return;
			}
		}

		// collect node ids:
		String selectedNodeId = selectedNode.getObjectId(getController());
		// WORKAROUND: Make target of local hyperlinks for the case, that ids
		// are not stored persistently.
		getMap().getLinkRegistry().registerLocalHyperlinkId(selectedNodeId);
		Vector selectedNodesId = new Vector();
		for (Iterator iter = selectedNodes.iterator(); iter.hasNext();) {
			MindMapNode node = (MindMapNode) iter.next();
			String nodeId = node.getObjectId(getController());
			// WORKAROUND: Make target of local hyperlinks for the case, that
			// ids are not stored persistently.
			getMap().getLinkRegistry().registerLocalHyperlinkId(nodeId);
			selectedNodesId.add(nodeId);
		}

		if (upwards) {
			if (selectedParent.isRoot()) {
				// change side of the items:
				boolean isLeft = selectedNode.isLeft();
				Transferable copy = getMindMapController().cut(selectedNodes);
				getMindMapController().paste(copy, selectedParent, false,
						!isLeft);
				select(selectedNodeId, selectedNodesId);
				return;
			}
			// determine child pos of parent
			MindMapNode grandParent = selectedParent.getParentNode();
			int parentPosition = grandParent.getChildPosition(selectedParent);
			boolean isLeft = selectedParent.isLeft();
			Transferable copy = getMindMapController().cut(selectedNodes);
			if (parentPosition == grandParent.getChildCount() - 1) {
				getMindMapController().paste(copy, grandParent, false, isLeft);
			} else {
				getMindMapController().paste(
						copy,
						(MindMapNode) grandParent
								.getChildAt(parentPosition + 1), true, isLeft);
			}
			select(selectedNodeId, selectedNodesId);

		} else {
			int ownPosition = selectedParent.getChildPosition(selectedNode);
			// find node above the own nodes:
			MindMapNode directSibling = null;
			for (int i = ownPosition - 1; i >= 0; --i) {
				MindMapNode sibling = (MindMapNode) selectedParent
						.getChildAt(i);
				if ((!selectedNodes.contains(sibling))
						&& selectedNode.isLeft() == sibling.isLeft()) {
					directSibling = sibling;
					break;
				}
			}
			if (directSibling == null) {
				// start searching for a sibling after the selected block:
				for (int i = ownPosition + 1; i < selectedParent
						.getChildCount(); ++i) {
					MindMapNode sibling = (MindMapNode) selectedParent
							.getChildAt(i);
					if ((!selectedNodes.contains(sibling))
							&& selectedNode.isLeft() == sibling.isLeft()) {
						directSibling = sibling;
						break;
					}
				}
			}
			if (directSibling != null && directSibling.isWriteable()) {
				// sibling on the same side found:
				Transferable copy = getMindMapController().cut(selectedNodes);
				getMindMapController().paste(copy, directSibling, false,
						directSibling.isLeft());
				select(selectedNodeId, selectedNodesId);
			}
		}
		obtainFocusForSelected();
	}

	private void select(String selectedNodeId, List selectedNodesIds) {
		// get new nodes by object id:
		MindMapNode newInstanceOfSelectedNode = getMindMapController()
				.getNodeFromID(selectedNodeId);
		List newSelecteds = new LinkedList();
		for (Iterator iter = selectedNodesIds.iterator(); iter.hasNext();) {
			String nodeId = (String) iter.next();
			newSelecteds.add(getMindMapController().getNodeFromID(nodeId));
		}
		getMindMapController().select(newInstanceOfSelectedNode, newSelecteds);
	}

}
