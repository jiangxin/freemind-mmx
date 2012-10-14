/*
 * FreeMind - A Program for creating and viewing Mindmaps Copyright (C)
 * 2000-2004 Joerg Mueller, Daniel Polansky, Christian Foltin and others.
 * 
 * See COPYING for Details
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * Created on 25.08.2004
 */

package freemind.modes.mindmapmode.actions;

import freemind.controller.actions.generated.instance.MoveNodeXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class MoveNodeAction extends NodeGeneralAction implements NodeActorXml {
	private final MindMapController modeController;

	/**
     */
	public MoveNodeAction(MindMapController modeController) {
		super(modeController, "reset_node_position", (String) null);
		this.modeController = modeController;
		addActor(this);
	}

	public void act(XmlAction action) {
		MoveNodeXmlAction moveAction = (MoveNodeXmlAction) action;
		NodeAdapter node = getNodeFromID(moveAction.getNode());
		node.setHGap(moveAction.getHGap());
		node.setShiftY(moveAction.getShiftY());
		if (!node.isRoot())
			node.getParentNode().setVGap(moveAction.getVGap());
		this.modeController.nodeChanged(node);
	}

	public Class getDoActionClass() {
		return MoveNodeXmlAction.class;
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		// reset position
		if (selected.isRoot())
			return null;
		return getActionPair(selected, NodeAdapter.VGAP, NodeAdapter.HGAP, 0);
	}

	private ActionPair getActionPair(MindMapNode selected, int parentVGap,
			int hGap, int shiftY) {
		MoveNodeXmlAction moveAction = moveNode(selected, parentVGap, hGap,
				shiftY);
		MoveNodeXmlAction undoAction = moveNode(selected, selected
				.getParentNode().getVGap(), selected.getHGap(),
				selected.getShiftY());
		return new ActionPair(moveAction, undoAction);
	}

	private MoveNodeXmlAction moveNode(MindMapNode selected, int parentVGap,
			int hGap, int shiftY) {
		MoveNodeXmlAction moveNodeAction = new MoveNodeXmlAction();
		moveNodeAction.setNode(getNodeID(selected));
		moveNodeAction.setHGap(hGap);
		moveNodeAction.setVGap(parentVGap);
		moveNodeAction.setShiftY(shiftY);
		return moveNodeAction;
	}

	public void moveNodeTo(MindMapNode node, int parentVGap, int hGap,
			int shiftY) {
		if (parentVGap == node.getParentNode().getVGap()
				&& hGap == node.getHGap() && shiftY == node.getShiftY()) {
			return;
		}
		modeController.doTransaction(
				(String) getValue(NAME),
				getActionPair(node, parentVGap, hGap, shiftY));
	}

}
