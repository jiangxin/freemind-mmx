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

import java.util.Iterator;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.RemoveAllIconsXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 25.03.2014
 */
public class RemoveAllIconsActor extends NodeXmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public RemoveAllIconsActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public RemoveAllIconsXmlAction createRemoveAllIconsXmlAction(
			MindMapNode node) {
		RemoveAllIconsXmlAction action = new RemoveAllIconsXmlAction();
		action.setNode(getNodeID(node));
		return action;
	}

	public void act(XmlAction action) {
		if (action instanceof RemoveAllIconsXmlAction) {
			RemoveAllIconsXmlAction removeAction = (RemoveAllIconsXmlAction) action;
			MindMapNode node = getNodeFromID(removeAction
					.getNode());
			while (node.getIcons().size() > 0) {
				node.removeIcon(MindIcon.LAST);
			}
			getExMapFeedback().nodeChanged(node);
		}
	}

	public void removeAllIcons(MindMapNode node) {
		getExMapFeedback().doTransaction(
				this.getClass().getName(), apply(getExMapFeedback().getMap(), node));
	}

	public Class getDoActionClass() {
		return RemoveAllIconsXmlAction.class;
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		CompoundAction undoAction = new CompoundAction();
		for (Iterator i = selected.getIcons().iterator(); i.hasNext();) {
			MindIcon icon = (MindIcon) i.next();
			undoAction.addChoice(getExMapFeedback().getActorFactory().getAddIconActor().createAddIconAction(selected,
					icon, MindIcon.LAST));
		}
		return new ActionPair(createRemoveAllIconsXmlAction(selected),
				undoAction);
	}


	
}
