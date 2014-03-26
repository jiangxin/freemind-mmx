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

import freemind.controller.actions.generated.instance.EdgeStyleFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.EdgeAdapter;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapEdge;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 26.03.2014
 */
public class EdgeStyleActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public EdgeStyleActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}
	
	public Class getDoActionClass() {
		return EdgeStyleFormatAction.class;
	}

	/**
	 * @param node
	 * @param style use null to remove the style
	 */
	public void setEdgeStyle(MindMapNode node, String style) {
		if (Tools.safeEquals(style, getStyle(node))) {
			return;
		}
		if (style != null) {
			boolean found = false;
			// check style:
			for (int i = 0; i < EdgeAdapter.EDGESTYLES.length; i++) {
				String possibleStyle = EdgeAdapter.EDGESTYLES[i];
				if (Tools.safeEquals(style, possibleStyle)) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new IllegalArgumentException("Style " + style
						+ " is not known");
			}
		}
		execute(getActionPair(node, style));
	}

	public ActionPair getActionPair(MindMapNode selected, String style) {
		EdgeStyleFormatAction styleAction = createNodeStyleFormatAction(
				selected, style);
		EdgeStyleFormatAction undoStyleAction = createNodeStyleFormatAction(
				selected, getStyle(selected));
		return new ActionPair(styleAction, undoStyleAction);
	}

	public String getStyle(MindMapNode selected) {
		String oldStyle = selected.getEdge().getStyle();
		if (!selected.getEdge().hasStyle()) {
			oldStyle = null;
		}
		return oldStyle;
	}

	private EdgeStyleFormatAction createNodeStyleFormatAction(
			MindMapNode selected, String style) {
		EdgeStyleFormatAction edgeStyleAction = new EdgeStyleFormatAction();
		edgeStyleAction.setNode(getNodeID(selected));
		edgeStyleAction.setStyle(style);
		return edgeStyleAction;
	}

	public void act(XmlAction action) {
		if (action instanceof EdgeStyleFormatAction) {
			EdgeStyleFormatAction edgeStyleAction = (EdgeStyleFormatAction) action;
			MindMapNode node = getNodeFromID(edgeStyleAction.getNode());
			String newStyle = edgeStyleAction.getStyle();
			MindMapEdge edge = node.getEdge();
			if (!Tools.safeEquals(edge.hasStyle() ? edge.getStyle() : null,
					newStyle)) {
				((EdgeAdapter) edge).setStyle(newStyle);
				getExMapFeedback().nodeChanged(node);
			}
		}
	}


}
