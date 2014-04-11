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

import freemind.controller.actions.generated.instance.AddAttributeAction;
import freemind.controller.actions.generated.instance.RemoveAttributeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class AddAttributeActor extends XmlActorAdapter {

	public AddAttributeActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void act(XmlAction action) {
		if (action instanceof AddAttributeAction) {
			AddAttributeAction addAttributeAction = (AddAttributeAction) action;
			NodeAdapter node = getNodeFromID(addAttributeAction.getNode());
			Attribute newAttribute = new Attribute(
					addAttributeAction.getName(), addAttributeAction.getValue());
			node.addAttribute(newAttribute);
			getExMapFeedback().nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return AddAttributeAction.class;
	}

	public ActionPair getActionPair(MindMapNode selected, Attribute pAttribute) {
		AddAttributeAction setAttributeAction = getAddAttributeAction(selected,
				pAttribute);
		RemoveAttributeAction undoAddAttributeAction = getXmlActorFactory().getRemoveAttributeActor()
				.getRemoveAttributeAction(selected, selected.getAttributeTableLength());
		return new ActionPair(setAttributeAction, undoAddAttributeAction);
	}

	/**
	 * @param pSelected
	 * @param pAttribute
	 * @return
	 */
	public AddAttributeAction getAddAttributeAction(MindMapNode pSelected,
			Attribute pAttribute) {
		AddAttributeAction addAttributeAction = new AddAttributeAction();
		addAttributeAction.setNode(getNodeID(pSelected));
		addAttributeAction.setName(pAttribute.getName());
		addAttributeAction.setValue(pAttribute.getValue());
		return addAttributeAction;
	}

	public int addAttribute(MindMapNode pNode, Attribute pAttribute) {
		int retValue = pNode.getAttributeTableLength();
		ActionPair actionPair = getActionPair(pNode, pAttribute);
		execute(actionPair);
		return retValue;
	}

}