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

import freemind.controller.actions.generated.instance.SetAttributeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class SetAttributeActor implements ActorXml {
	/**
	 * 
	 */
	private final MindMapController mMindMapController;

	/**
	 * @param pMindMapController
	 */
	public SetAttributeActor(MindMapController pMindMapController) {
		mMindMapController = pMindMapController;
	}

	public void act(XmlAction action) {
		if (action instanceof SetAttributeAction) {
			SetAttributeAction setAttributeAction = (SetAttributeAction) action;
			NodeAdapter node = mMindMapController.getNodeFromID(setAttributeAction.getNode());
			Attribute newAttribute = new Attribute(setAttributeAction.getName(), setAttributeAction.getValue());
			int position = setAttributeAction.getPosition();
			node.checkAttributePosition(position);
			if (!node.getAttribute(position).equals(newAttribute)) {
				node.setAttribute(position, newAttribute);
				mMindMapController.nodeChanged(node);
			}
		}
	}

	public Class getDoActionClass() {
		return SetAttributeAction.class;
	}

	public ActionPair getActionPair(MindMapNode selected, int pPosition, Attribute pAttribute) {
		SetAttributeAction setAttributeAction = setAttribute(selected, pPosition, pAttribute);
		SetAttributeAction undoSetAttributeAction = setAttribute(selected, pPosition, selected.getAttribute(pPosition));
		return new ActionPair(setAttributeAction, undoSetAttributeAction);
	}

	/**
	 * @param pSelected
	 * @param pPosition
	 * @param pAttribute
	 * @return
	 */
	private SetAttributeAction setAttribute(MindMapNode pSelected,
			int pPosition, Attribute pAttribute) {
		SetAttributeAction setAttributeAction = new SetAttributeAction();
		setAttributeAction.setNode(mMindMapController.getNodeID(pSelected));
		setAttributeAction.setName(pAttribute.getName());
		setAttributeAction.setValue(pAttribute.getValue());
		setAttributeAction.setPosition(pPosition);
		return setAttributeAction;
	}

}