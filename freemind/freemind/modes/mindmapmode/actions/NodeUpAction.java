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
 * Created on 21.08.2004
 */

package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import freemind.controller.actions.generated.instance.MoveNodesAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MapAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class NodeUpAction extends AbstractAction implements ActorXml {
	private final MindMapController modeController;
	private static Logger logger;

	public NodeUpAction(MindMapController modeController) {
		super(modeController.getText("node_up"));
		this.modeController = modeController;
		modeController.getActionFactory().registerActor(this,
				getDoActionClass());
		if (logger == null) {
			logger = modeController.getFrame().getLogger(
					this.getClass().getName());
		}
	}

	public void actionPerformed(ActionEvent e) {
		MindMapNode selected = modeController.getSelected();
		List selecteds = modeController.getSelecteds();
		moveNodes(selected, selecteds, -1);
		modeController.select(selected, selecteds);
	}

	/**
     */
	public void moveNodes(MindMapNode selected, List selecteds, int direction) {
		MoveNodesAction doAction = createMoveNodesAction(selected, selecteds,
				direction);
		MoveNodesAction undoAction = createMoveNodesAction(selected, selecteds,
				-direction);
		modeController.doTransaction((String) getValue(NAME), new ActionPair(
				doAction, undoAction));
	}

	public void _moveNodes(MindMapNode selected, List selecteds, int direction) {
		Comparator comparator = (direction == -1) ? null : new Comparator() {

			public int compare(Object o1, Object o2) {
				int i1 = ((Integer) o1).intValue();
				int i2 = ((Integer) o2).intValue();
				return i2 - i1;
			}
		};
		if (!selected.isRoot()) {
			MindMapNode parent = selected.getParentNode();
			// multiple move:
			Vector sortedChildren = getSortedSiblings(parent);
			TreeSet range = new TreeSet(comparator);
			for (Iterator i = selecteds.iterator(); i.hasNext();) {
				MindMapNode node = (MindMapNode) i.next();
				if (node.getParent() != parent) {
					logger.warning("Not all selected nodes (here: "
							+ node.getText() + ") have the same parent "
							+ parent.getText() + ".");
					return;
				}
				range.add(new Integer(sortedChildren.indexOf(node)));
			}
			// test range for adjacent nodes:
			Integer last = (Integer) range.iterator().next();
			for (Iterator i = range.iterator(); i.hasNext();) {
				Integer newInt = (Integer) i.next();
				if (Math.abs(newInt.intValue() - last.intValue()) > 1) {
					logger.warning("Not adjacent nodes. Skipped. ");
					return;
				}
				last = newInt;
			}
			for (Iterator i = range.iterator(); i.hasNext();) {
				Integer position = (Integer) i.next();
				// from above:
				MindMapNode node = (MindMapNode) sortedChildren.get(position
						.intValue());
				moveNodeTo(node, parent, direction);
			}
		}
	}

	/**
	 * The direction is used if side left and right are present. then the next
	 * suitable place on the same side# is searched. if there is no such place,
	 * then the side is changed.
	 * 
	 * @return returns the new index.
	 */
	public int moveNodeTo(MindMapNode newChild, MindMapNode parent,
			int direction) {
		MapAdapter model = modeController.getModel();
		int index = model.getIndexOfChild(parent, newChild);
		int newIndex = index;
		int maxIndex = parent.getChildCount();
		Vector sortedNodesIndices = getSortedSiblings(parent);
		int newPositionInVector = sortedNodesIndices.indexOf(newChild)
				+ direction;
		if (newPositionInVector < 0) {
			newPositionInVector = maxIndex - 1;
		}
		if (newPositionInVector >= maxIndex) {
			newPositionInVector = 0;
		}
		MindMapNode destinationNode = (MindMapNode) sortedNodesIndices
				.get(newPositionInVector);
		newIndex = model.getIndexOfChild(parent, destinationNode);
		modeController.removeNodeFromParent(newChild);
		modeController.insertNodeInto(newChild, parent, newIndex);
		modeController.nodeChanged(newChild);
		return newIndex;
	}

	/**
	 * Sorts nodes by their left/right status. The left are first.
	 */
	private Vector getSortedSiblings(MindMapNode node) {
		Vector nodes = new Vector();
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			nodes.add(i.next());
		}
		Collections.sort(nodes, new Comparator() {

			public int compare(Object o1, Object o2) {
				if (o1 instanceof MindMapNode) {
					MindMapNode n1 = (MindMapNode) o1;
					if (o2 instanceof MindMapNode) {
						MindMapNode n2 = (MindMapNode) o2;
						// left is less than right
						int b1 = n1.isLeft() ? 0 : 1;
						int b2 = n2.isLeft() ? 0 : 1;
						return b1 - b2;
					}
				}
				throw new IllegalArgumentException(
						"Elements in LeftRightComparator are not comparable.");
			}
		});
		// logger.finest("Sorted nodes "+ nodes);
		return nodes;
	}

	public void act(XmlAction action) {
		if (action instanceof MoveNodesAction) {
			MoveNodesAction moveAction = (MoveNodesAction) action;
			MindMapNode selected = modeController.getNodeFromID(moveAction
					.getNode());
			Vector selecteds = new Vector();
			for (Iterator i = moveAction.getListNodeListMemberList().iterator(); i
					.hasNext();) {
				NodeListMember node = (NodeListMember) i.next();
				selecteds.add(modeController.getNodeFromID(node.getNode()));
			}
			_moveNodes(selected, selecteds, moveAction.getDirection());
		}
	}

	public Class getDoActionClass() {
		return MoveNodesAction.class;
	}

	private MoveNodesAction createMoveNodesAction(MindMapNode selected,
			List selecteds, int direction) {
		MoveNodesAction moveAction = new MoveNodesAction();
		moveAction.setDirection(direction);
		moveAction.setNode(selected.getObjectId(modeController));
		// selectedNodes list
		for (Iterator i = selecteds.iterator(); i.hasNext();) {
			MindMapNode node = (MindMapNode) i.next();
			NodeListMember nodeListMember = new NodeListMember();
			nodeListMember.setNode(node.getObjectId(modeController));
			moveAction.addNodeListMember(nodeListMember);
		}
		return moveAction;

	}
}
