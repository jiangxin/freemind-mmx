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

import freemind.controller.actions.generated.instance.CloudColorXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.LineAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class CloudColorActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public CloudColorActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void setCloudColor(MindMapNode node, Color color) {
		CloudColorXmlAction doAction = createCloudColorXmlAction(node, color);
		CloudColorXmlAction undoAction = createCloudColorXmlAction(node,
				(node.getCloud() == null) ? null : node.getCloud().getColor());
		execute(new ActionPair(doAction, undoAction));
	}

	public CloudColorXmlAction createCloudColorXmlAction(MindMapNode node,
			Color color) {
		CloudColorXmlAction nodeAction = new CloudColorXmlAction();
		nodeAction.setNode(getNodeID(node));
		nodeAction.setColor(Tools.colorToXml(color));
		return nodeAction;
	}

	public void act(XmlAction action) {
		if (action instanceof CloudColorXmlAction) {
			CloudColorXmlAction nodeColorAction = (CloudColorXmlAction) action;
			Color color = Tools.xmlToColor(nodeColorAction.getColor());
			MindMapNode node = getNodeFromID(nodeColorAction
					.getNode());
			// this is not necessary, as this action is not enabled if there is
			// no cloud.
			if (node.getCloud() == null) {
				getExMapFeedback().getActorFactory().getCloudActor().setCloud(node, true);
			}
			Color selectedColor = null;
			if (node.getCloud() != null) {
				selectedColor = node.getCloud().getColor();
			}
			if (!Tools.safeEquals(color, selectedColor)) {
				((LineAdapter) node.getCloud()).setColor(color); // null
				getExMapFeedback().nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return CloudColorXmlAction.class;
	}

}
