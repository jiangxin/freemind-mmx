/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.RemoveAttributeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class RemoveAttributeActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public RemoveAttributeActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void act(XmlAction action) {
		if (action instanceof RemoveAttributeAction) {
			RemoveAttributeAction removeAttributeAction = (RemoveAttributeAction) action;
			NodeAdapter node = getNodeFromID(removeAttributeAction.getNode());
			int position = removeAttributeAction.getPosition();
			node.checkAttributePosition(position);
			node.removeAttribute(position);
			getExMapFeedback().nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return RemoveAttributeAction.class;
	}

	public ActionPair getActionPair(MindMapNode selected, int pPosition) {
		RemoveAttributeAction setAttributeAction = getRemoveAttributeAction(selected,
				pPosition);
		NodeAction undoRemoveAttributeAction;
		if (pPosition == selected.getAttributeTableLength() - 1) {
			undoRemoveAttributeAction = getXmlActorFactory().getAddAttributeActor().getAddAttributeAction(
					selected, selected.getAttribute(pPosition));
		} else {
			undoRemoveAttributeAction = getXmlActorFactory().getInsertAttributeActor().getInsertAttributeAction(
					selected, pPosition, selected.getAttribute(pPosition));
		}
		return new ActionPair(setAttributeAction, undoRemoveAttributeAction);
	}

	/**
	 * @param pSelected
	 * @param pPosition
	 *            TODO
	 * @return
	 */
	public RemoveAttributeAction getRemoveAttributeAction(MindMapNode pSelected,
			int pPosition) {
		RemoveAttributeAction removeAttributeAction = new RemoveAttributeAction();
		removeAttributeAction.setNode(getNodeID(pSelected));
		removeAttributeAction.setPosition(pPosition);
		return removeAttributeAction;
	}
	
	public void removeAttribute(MindMapNode pNode, int pPosition) {
		ActionPair actionPair = getActionPair(pNode, pPosition);
		execute(actionPair);
	}
}