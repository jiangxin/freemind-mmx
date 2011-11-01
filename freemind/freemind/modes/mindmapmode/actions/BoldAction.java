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
 * Created on 05.05.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.modes.mindmapmode.actions;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemSelectedListener;
import freemind.controller.actions.generated.instance.BoldNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class BoldAction extends NodeGeneralAction implements NodeActorXml,
		MenuItemSelectedListener {
	/**
	 */
	public BoldAction(MindMapController modeController) {
		super(modeController, "bold", "images/Bold16.gif");
		addActor(this);
	}

	public void act(XmlAction action) {
		if (action instanceof BoldNodeAction) {
			BoldNodeAction boldact = (BoldNodeAction) action;
			NodeAdapter node = getNodeFromID(boldact.getNode());
			if (node.isBold() != boldact.getBold()) {
				node.setBold(boldact.getBold());
				modeController.nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return BoldNodeAction.class;
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		// every node is set to the inverse of the focussed node.
		boolean bold = modeController.getSelected().isBold();
		return getActionPair(selected, !bold);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean bold) {
		BoldNodeAction boldAction = toggleBold(selected, bold);
		BoldNodeAction undoBoldAction = toggleBold(selected, selected.isBold());
		return new ActionPair(boldAction, undoBoldAction);
	}

	private BoldNodeAction toggleBold(MindMapNode selected, boolean bold) {
		BoldNodeAction boldAction = new BoldNodeAction();
		boldAction.setNode(getNodeID(selected));
		boldAction.setBold(bold);
		return boldAction;
	}

	public void setBold(MindMapNode node, boolean bold) {
		execute(getActionPair(node, bold));
	}

	public boolean isSelected(JMenuItem item, Action action) {
		return modeController.getSelected().isBold();
	}

}
