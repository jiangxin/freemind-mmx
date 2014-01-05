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

import freemind.controller.actions.generated.instance.AddAttributeAction;
import freemind.controller.actions.generated.instance.RemoveAttributeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class AddAttributeActor implements ActorXml {
	/**
	 * 
	 */
	private final MindMapController mMindMapController;
	private RemoveAttributeActor mRemoveAttributeActor;

	/**
	 * @param pMindMapController
	 */
	public AddAttributeActor(MindMapController pMindMapController) {
		mMindMapController = pMindMapController;
	}

	public void act(XmlAction action) {
		if (action instanceof AddAttributeAction) {
			AddAttributeAction addAttributeAction = (AddAttributeAction) action;
			NodeAdapter node = mMindMapController
					.getNodeFromID(addAttributeAction.getNode());
			Attribute newAttribute = new Attribute(
					addAttributeAction.getName(), addAttributeAction.getValue());
			node.addAttribute(newAttribute);
			mMindMapController.nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return AddAttributeAction.class;
	}

	public ActionPair getActionPair(MindMapNode selected, Attribute pAttribute) {
		AddAttributeAction setAttributeAction = addAttribute(selected,
				pAttribute);
		RemoveAttributeAction undoAddAttributeAction = mRemoveAttributeActor
				.removeAttribute(selected, selected.getAttributeTableLength());
		return new ActionPair(setAttributeAction, undoAddAttributeAction);
	}

	/**
	 * @param pSelected
	 * @param pAttribute
	 * @return
	 */
	public AddAttributeAction addAttribute(MindMapNode pSelected,
			Attribute pAttribute) {
		AddAttributeAction addAttributeAction = new AddAttributeAction();
		addAttributeAction.setNode(mMindMapController.getNodeID(pSelected));
		addAttributeAction.setName(pAttribute.getName());
		addAttributeAction.setValue(pAttribute.getValue());
		return addAttributeAction;
	}

	/**
	 * @param pRemoveAttributeActor
	 */
	public void setRemoveAttributeActor(
			RemoveAttributeActor pRemoveAttributeActor) {
		mRemoveAttributeActor = pRemoveAttributeActor;
	}

}