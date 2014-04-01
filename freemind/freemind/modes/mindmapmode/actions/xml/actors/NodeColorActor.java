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

import freemind.controller.actions.generated.instance.NodeColorFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class NodeColorActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public NodeColorActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void setNodeColor(MindMapNode node, Color color) {
		if (Tools.safeEquals(color, node.getColor())) {
			return;
		}
		NodeColorFormatAction doAction = createNodeColorFormatAction(node,
				color);
		NodeColorFormatAction undoAction = createNodeColorFormatAction(node,
				node.getColor());
		execute(new ActionPair(doAction, undoAction));
	}

	public NodeColorFormatAction createNodeColorFormatAction(MindMapNode node,
			Color color) {
		NodeColorFormatAction nodeAction = new NodeColorFormatAction();
		nodeAction.setNode(getNodeID(node));
		nodeAction.setColor(Tools.colorToXml(color));
		return nodeAction;
	}

	public void act(XmlAction action) {
		if (action instanceof NodeColorFormatAction) {
			NodeColorFormatAction nodeColorAction = (NodeColorFormatAction) action;
			Color color = Tools.xmlToColor(nodeColorAction.getColor());
			MindMapNode node = getNodeFromID(nodeColorAction
					.getNode());
			Color oldColor = node.getColor();
			if (!Tools.safeEquals(color, oldColor)) {
				node.setColor(color); // null
				getExMapFeedback().nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return NodeColorFormatAction.class;
	}

}
