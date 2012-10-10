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
 * Created on 05.10.2004
 */
/* $Id: EdgeWidthAction.java,v 1.1.2.2.2.2 2007/08/17 20:41:57 christianfoltin Exp $ */

package freemind.modes.mindmapmode.actions;

import freemind.controller.actions.generated.instance.EdgeWidthFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.EdgeAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class EdgeWidthAction extends NodeGeneralAction implements NodeActorXml {
	private int mWidth;

	public EdgeWidthAction(MindMapController controller, int width) {
		super(controller, null, null);
		this.mWidth = width;
		setName(getWidthTitle(controller, width));
		addActor(this);
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		return getActionPair(selected, mWidth);
	}

	public Class getDoActionClass() {
		return EdgeWidthFormatAction.class;
	}

	public void setEdgeWidth(MindMapNode node, int width) {
		if (width == getWidth(node)) {
			return;
		}
		modeController.doTransaction(
				(String) getValue(NAME), getActionPair(node, width));

	}

	private ActionPair getActionPair(MindMapNode selected, int width) {
		EdgeWidthFormatAction styleAction = createEdgeWidthFormatAction(
				selected, width);
		EdgeWidthFormatAction undoStyleAction = createEdgeWidthFormatAction(
				selected, getWidth(selected));
		return new ActionPair(styleAction, undoStyleAction);
	}

	public int getWidth(MindMapNode selected) {
		return ((EdgeAdapter) selected.getEdge()).getRealWidth();
	}

	private EdgeWidthFormatAction createEdgeWidthFormatAction(
			MindMapNode selected, int width) {
		EdgeWidthFormatAction edgeWidthAction = new EdgeWidthFormatAction();
		edgeWidthAction.setNode(getNodeID(selected));
		edgeWidthAction.setWidth(width);
		return edgeWidthAction;
	}

	public void act(XmlAction action) {
		if (action instanceof EdgeWidthFormatAction) {
			EdgeWidthFormatAction edgeWithAction = (EdgeWidthFormatAction) action;
			MindMapNode node = getNodeFromID(edgeWithAction.getNode());
			int width = edgeWithAction.getWidth();
			EdgeAdapter edge = (EdgeAdapter) node.getEdge();
			if (edge.getRealWidth() != width) {
				edge.setWidth(width);
				modeController.nodeChanged(node);
			}
		}
	}

	private static String getWidthTitle(MindMapController controller, int width) {
		String returnValue;
		if (width == EdgeAdapter.WIDTH_PARENT) {
			returnValue = controller.getText("edge_width_parent");
		} else if (width == EdgeAdapter.WIDTH_THIN) {
			returnValue = controller.getText("edge_width_thin");
		} else {
			returnValue = Integer.toString(width);
		}
		return /* controller.getText("edge_width") + */returnValue;
	}

}