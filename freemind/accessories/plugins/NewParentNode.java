/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/*
 * Created on 19.04.2004
 *
 */
package accessories.plugins;

import java.awt.datatransfer.Transferable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.MapView;

/**
 * @author foltin The original version was sent by Stephen Viles (sviles)
 *         https://sourceforge.net/tracker/?func=detail&atid=307118&aid=881217&
 *         group_id=7118
 * 
 *         Initial Comment: The "New Parent Node" action creates a node as a
 *         parent of one or more selected nodes. If more than one node is
 *         selected, the selected nodes must all have the same parent -- this
 *         restriction is imposed to make the action easier to understand and to
 *         undo manually, and could potentially be removed when we get automated
 *         undo.
 * 
 *         The root node must not be one of the selected nodes. I find this
 *         action useful when I need to add an extra level of grouping in the
 *         middle of an existing hierarchy. It is quicker than adding a new node
 *         at the same level and then cutting-and-pasting the child nodes. The
 *         code simply performs these actions in sequence, after validating the
 *         selected nodes.
 */
public class NewParentNode extends MindMapNodeHookAdapter {

	/**
	 * 
	 */
	public NewParentNode() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode,
	 * java.util.List)
	 */
	public void invoke(MindMapNode rootNode) {
		final MapView mapView = getMindMapController().getView();
		// we dont need node.
		MindMapNode focussed = getMindMapController().getSelected();
		List selecteds = getMindMapController().getSelecteds();
		MindMapNode selectedNode = focussed;
		List selectedNodes = selecteds;

		// bug fix: sort to make independent by user's selection:
		getMindMapController().sortNodesByDepth(selectedNodes);

		if (focussed.isRoot()) {
			if (selecteds.size() == 1) {
				// only root is selected. we try to create a new root:
				Vector children = new Vector(rootNode.getChildren());
				// copy only root.
				Transferable rootContent = getMindMapController().copySingle();
				// and paste it directly again.
				getMindMapController().paste(rootContent, rootNode);
				Vector childrenNew = new Vector(rootNode.getChildren());
				/*
				 * look for the new node as the difference between former
				 * children and new children.
				 */
				MindMapNode rootCopy = null;
				boolean found = false;
				for (Iterator it = childrenNew.iterator(); it.hasNext();) {
					rootCopy = (MindMapNode) it.next();
					if (!children.contains(rootCopy)) {
						found = true;
						break;
					}
				}
				if (!found) {
					logger.warning("New node not found in list of all children. Strange...");
					return;
				}
				// delete root node content:
				getMindMapController().clearNodeContents(rootNode);
				// children list must be able to modify, thus we copy it deeply.
				moveToOtherNode(rootNode, children, rootNode, rootCopy);
				return;
			}
			getMindMapController().getController().errorMessage(
					getResourceString("cannot_add_parent_to_root"));
			return;
		}

		MindMapNode newNode = moveToNewParent(rootNode, selectedNode,
				selectedNodes);
		if (newNode == null) {
			return;
		}
		// Start editing new node
		mapView.selectAsTheOnlyOneSelected(mapView.getNodeView(newNode));
		getMindMapController().getFrame().repaint();
		// edit is not necessary, as the new text can directly entered and
		// editing is starting automatically.
		// getMindMapController().edit(newNode.getViewer(),
		// selectedParent.getViewer(), null, false, false, false);
	}

	private MindMapNode moveToNewParent(MindMapNode rootNode,
			MindMapNode selectedNode, List selectedNodes) {
		// Create new node in the position of the selectedNode
		MindMapNode selectedParent = selectedNode.getParentNode();
		int childPosition = selectedParent.getChildPosition(selectedNode);
		MindMapNode newNode = getMindMapController().addNewNode(selectedParent,
				childPosition, selectedNode.isLeft());
		return moveToOtherNode(rootNode, selectedNodes, selectedParent, newNode);
	}

	private MindMapNode moveToOtherNode(MindMapNode rootNode,
			List nodesToBeMoved, MindMapNode selectedParent, MindMapNode newNode) {
		if (nodesToBeMoved.size() == 0) {
			// nothing to do.
			return newNode;
		}
		// Make sure the selected nodes all have the same parent
		// (this restriction is to simplify the action, and could
		// possibly be removed in the future, when we have undo)
		// Also make sure that none of the selected nodes are the root node
		for (Iterator it = nodesToBeMoved.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			if (node.getParentNode() != selectedParent) {
				getMindMapController().getController().errorMessage(
						getResourceString("cannot_add_parent_diff_parents"));
				return null;
			}
			if (node == rootNode) {
				getMindMapController().getController().errorMessage(
						getResourceString("cannot_add_parent_to_root"));
				return null;
			}
		}

		// MindMapNode newNode = getMindMapController().newNode();
		// getMap().insertNodeInto(newNode, selectedParent, childPosition);

		// Move selected nodes to become children of new node
		Transferable copy = getMindMapController().cut(nodesToBeMoved);
		getMindMapController().paste(copy, newNode);
		getMindMapController().select(selectedParent, Tools.getVectorWithSingleElement(selectedParent));
//		getMindMapController().obtainFocusForSelected();
		nodeChanged(selectedParent);
		
		return newNode;
	}

}
