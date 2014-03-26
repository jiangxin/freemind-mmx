/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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

import freemind.controller.actions.generated.instance.EdgeWidthFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.EdgeAdapter;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 26.03.2014
 */
public class EdgeWidthActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public EdgeWidthActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public Class getDoActionClass() {
		return EdgeWidthFormatAction.class;
	}

	public void setEdgeWidth(MindMapNode node, int width) {
		if (width == getWidth(node)) {
			return;
		}
		execute(getActionPair(node, width));

	}

	public ActionPair getActionPair(MindMapNode selected, int width) {
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
				getExMapFeedback().nodeChanged(node);
			}
		}
	}


}
