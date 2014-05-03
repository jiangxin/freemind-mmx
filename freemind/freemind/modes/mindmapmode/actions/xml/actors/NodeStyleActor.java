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

import freemind.controller.actions.generated.instance.NodeStyleFormatAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 27.03.2014
 */
public class NodeStyleActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public NodeStyleActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}
	
	public Class getDoActionClass() {
		return NodeStyleFormatAction.class;
	}

	public void setStyle(MindMapNode node, String style) {
		if(style == null) {
			execute(getActionPair(node, null));
			return;
		}
		for (int i = 0; i < MindMapNode.NODE_STYLES.length; i++) {
			String dstyle = MindMapNode.NODE_STYLES[i];
			if(Tools.safeEquals(style, dstyle)) {
				execute(getActionPair(node, style));
				return;
			}
		}
		throw new IllegalArgumentException("Unknown style " + style);
	}

	public ActionPair getActionPair(MindMapNode targetNode, String style) {
		NodeStyleFormatAction styleAction = createNodeStyleFormatAction(
				targetNode, style);
		NodeStyleFormatAction undoStyleAction = createNodeStyleFormatAction(
				targetNode, targetNode.getStyle());
		return new ActionPair(styleAction, undoStyleAction);
	}

	private NodeStyleFormatAction createNodeStyleFormatAction(
			MindMapNode selected, String style) {
		NodeStyleFormatAction nodeStyleAction = new NodeStyleFormatAction();
		nodeStyleAction.setNode(getNodeID(selected));
		nodeStyleAction.setStyle(style);
		return nodeStyleAction;
	}

	public void act(XmlAction action) {
		if (action instanceof NodeStyleFormatAction) {
			NodeStyleFormatAction nodeStyleAction = (NodeStyleFormatAction) action;
			MindMapNode node = getNodeFromID(nodeStyleAction.getNode());
			String style = nodeStyleAction.getStyle();
			if (!Tools.safeEquals(node.hasStyle() ? node.getStyle() : null,
					style)) {
				// logger.info("Setting style of " + node + " to "+ style +
				// " and was " + node.getStyle());
				node.setStyle(style);
				getExMapFeedback().nodeStyleChanged(node);					
			}
		}
	}



}
