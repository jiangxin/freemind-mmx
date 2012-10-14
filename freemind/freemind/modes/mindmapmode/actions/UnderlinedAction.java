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

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemSelectedListener;
import freemind.controller.actions.generated.instance.UnderlinedNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class UnderlinedAction extends NodeGeneralAction implements
		NodeActorXml, MenuItemSelectedListener {
	private final MindMapController modeController;

	/**
	 */
	public UnderlinedAction(MindMapController modeController) {
		super(modeController, "underlined", "images/Underline24.gif");
		this.modeController = modeController;
		addActor(this);
	}

	public void act(XmlAction action) {
		UnderlinedNodeAction underlinedact = (UnderlinedNodeAction) action;
		NodeAdapter node = getNodeFromID(underlinedact.getNode());
		if (node.isUnderlined() != underlinedact.getUnderlined()) {
			node.setUnderlined(underlinedact.getUnderlined());
			this.modeController.nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return UnderlinedNodeAction.class;
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		// every node is set to the inverse of the focussed node.
		boolean underlined = modeController.getSelected().isUnderlined();
		return getActionPair(selected, underlined);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean underlined) {
		UnderlinedNodeAction underlinedAction = toggleUnderlined(selected,
				!underlined);
		UnderlinedNodeAction undoUnderlinedAction = toggleUnderlined(selected,
				underlined);
		return new ActionPair(underlinedAction, undoUnderlinedAction);
	}

	private UnderlinedNodeAction toggleUnderlined(MindMapNode selected,
			boolean underlined) {
		UnderlinedNodeAction underlinedAction = new UnderlinedNodeAction();
		underlinedAction.setNode(getNodeID(selected));
		underlinedAction.setUnderlined(underlined);
		return underlinedAction;
	}

	public void setUnderlined(MindMapNode node, boolean underlined) {
		execute(getActionPair(node, underlined));
	}

	public boolean isSelected(JMenuItem item, Action action) {
		return modeController.getSelected().isUnderlined();
	}
}
