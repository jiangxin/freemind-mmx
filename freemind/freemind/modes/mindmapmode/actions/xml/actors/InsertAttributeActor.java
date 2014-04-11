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

import freemind.controller.actions.generated.instance.InsertAttributeAction;
import freemind.controller.actions.generated.instance.RemoveAttributeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class InsertAttributeActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public InsertAttributeActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	public void act(XmlAction action) {
		if (action instanceof InsertAttributeAction) {
			InsertAttributeAction setAttributeAction = (InsertAttributeAction) action;
			NodeAdapter node = getNodeFromID(setAttributeAction.getNode());
			Attribute newAttribute = new Attribute(
					setAttributeAction.getName(), setAttributeAction.getValue());
			int position = setAttributeAction.getPosition();
			node.checkAttributePosition(position);
			node.insertAttribute(position, newAttribute);
			getExMapFeedback().nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return InsertAttributeAction.class;
	}

	public ActionPair getActionPair(MindMapNode selected, int pPosition,
			Attribute pAttribute) {
		InsertAttributeAction insertAttributeAction = getInsertAttributeAction(selected,
				pPosition, pAttribute);
		RemoveAttributeAction undoInsertAttributeAction = getXmlActorFactory().getRemoveAttributeActor().getRemoveAttributeAction(selected, pPosition);
		return new ActionPair(insertAttributeAction, undoInsertAttributeAction);
	}

	/**
	 * @param pSelected
	 * @param pPosition
	 * @param pAttribute
	 * @return
	 */
	public InsertAttributeAction getInsertAttributeAction(MindMapNode pSelected,
			int pPosition, Attribute pAttribute) {
		InsertAttributeAction insertAttributeAction = new InsertAttributeAction();
		insertAttributeAction.setNode(getNodeID(pSelected));
		insertAttributeAction.setName(pAttribute.getName());
		insertAttributeAction.setValue(pAttribute.getValue());
		insertAttributeAction.setPosition(pPosition);
		return insertAttributeAction;
	}
	
	public void insertAttribute(MindMapNode pNode, int pPosition, Attribute pAttribute) {
		ActionPair actionPair = getActionPair(pNode, pPosition, pAttribute);
		execute(actionPair);

	}

	
	
}