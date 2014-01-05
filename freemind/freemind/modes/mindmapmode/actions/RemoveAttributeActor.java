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

package freemind.modes.mindmapmode.actions;

import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.RemoveAttributeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class RemoveAttributeActor implements ActorXml {
	/**
	 * 
	 */
	private final MindMapController mMindMapController;
	private InsertAttributeActor mInsertAttributeActor;
	private AddAttributeActor mAddAttributeActor;

	/**
	 * @param pMindMapController
	 */
	public RemoveAttributeActor(MindMapController pMindMapController) {
		mMindMapController = pMindMapController;
	}

	public void act(XmlAction action) {
		if (action instanceof RemoveAttributeAction) {
			RemoveAttributeAction removeAttributeAction = (RemoveAttributeAction) action;
			NodeAdapter node = mMindMapController
					.getNodeFromID(removeAttributeAction.getNode());
			int position = removeAttributeAction.getPosition();
			node.checkAttributePosition(position);
			node.removeAttribute(position);
			mMindMapController.nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return RemoveAttributeAction.class;
	}

	public ActionPair getActionPair(MindMapNode selected, int pPosition) {
		RemoveAttributeAction setAttributeAction = removeAttribute(selected,
				pPosition);
		NodeAction undoRemoveAttributeAction;
		if (pPosition == selected.getAttributeTableLength() - 1) {
			undoRemoveAttributeAction = mAddAttributeActor.addAttribute(
					selected, selected.getAttribute(pPosition));
		} else {
			undoRemoveAttributeAction = mInsertAttributeActor.insertAttribute(
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
	public RemoveAttributeAction removeAttribute(MindMapNode pSelected,
			int pPosition) {
		RemoveAttributeAction removeAttributeAction = new RemoveAttributeAction();
		removeAttributeAction.setNode(mMindMapController.getNodeID(pSelected));
		removeAttributeAction.setPosition(pPosition);
		return removeAttributeAction;
	}

	/**
	 * @param pInsertAttributeActor
	 */
	public void setInsertAttributeActor(
			InsertAttributeActor pInsertAttributeActor) {
		mInsertAttributeActor = pInsertAttributeActor;
	}

	/**
	 * @param pAddAttributeActor
	 */
	public void setAddAttributeActor(AddAttributeActor pAddAttributeActor) {
		mAddAttributeActor = pAddAttributeActor;
	}

}