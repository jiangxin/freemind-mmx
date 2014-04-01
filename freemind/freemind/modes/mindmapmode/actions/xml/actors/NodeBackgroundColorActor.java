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

import java.awt.Color;

import freemind.controller.actions.generated.instance.NodeBackgroundColorFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class NodeBackgroundColorActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public NodeBackgroundColorActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void setNodeBackgroundColor(MindMapNode node, Color color) {
		NodeBackgroundColorFormatAction doAction = createNodeBackgroundColorFormatAction(
				node, color);
		NodeBackgroundColorFormatAction undoAction = createNodeBackgroundColorFormatAction(
				node, node.getBackgroundColor());
		execute(new ActionPair(doAction, undoAction));
	}

	public NodeBackgroundColorFormatAction createNodeBackgroundColorFormatAction(
			MindMapNode node, Color color) {
		NodeBackgroundColorFormatAction nodeAction = new NodeBackgroundColorFormatAction();
		nodeAction.setNode(getNodeID(node));
		nodeAction.setColor(Tools.colorToXml(color));
		return nodeAction;
	}

	public void act(XmlAction action) {
		if (action instanceof NodeBackgroundColorFormatAction) {
			NodeBackgroundColorFormatAction nodeColorAction = (NodeBackgroundColorFormatAction) action;
			Color color = Tools.xmlToColor(nodeColorAction.getColor());
			MindMapNode node = getNodeFromID(nodeColorAction
					.getNode());
			Color oldColor = node.getBackgroundColor();
			if (!Tools.safeEquals(color, oldColor)) {
				node.setBackgroundColor(color); // null
				getExMapFeedback().nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return NodeBackgroundColorFormatAction.class;
	}

}
