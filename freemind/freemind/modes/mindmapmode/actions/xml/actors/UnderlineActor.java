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

import freemind.controller.actions.generated.instance.UnderlinedNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 27.03.2014
 */
public class UnderlineActor extends NodeXmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public UnderlineActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}
	
	public void act(XmlAction action) {
		UnderlinedNodeAction underlinedact = (UnderlinedNodeAction) action;
		NodeAdapter node = getNodeFromID(underlinedact.getNode());
		if (node.isUnderlined() != underlinedact.getUnderlined()) {
			node.setUnderlined(underlinedact.getUnderlined());
			getExMapFeedback().nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return UnderlinedNodeAction.class;
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		// every node is set to the inverse of the focussed node.
		boolean underlined = getExMapFeedback().getSelected().isUnderlined();
		return getActionPair(selected, !underlined);
	}

	private ActionPair getActionPair(MindMapNode selected, boolean underlined) {
		UnderlinedNodeAction underlinedAction = toggleUnderlined(selected,
				underlined);
		UnderlinedNodeAction undoUnderlinedAction = toggleUnderlined(selected,
				!underlined);
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



}
