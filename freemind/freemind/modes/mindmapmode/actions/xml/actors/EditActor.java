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

import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * @date 01.04.2014
 */
public class EditActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public EditActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.actions.ActorXml#act(freemind.controller.actions.
	 * generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		EditNodeAction editAction = (EditNodeAction) action;
		NodeAdapter node = this.getNodeFromID(editAction
				.getNode());
		if (!node.toString().equals(editAction.getText())) {
			node.setUserObject(editAction.getText());
			getExMapFeedback().nodeChanged(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.actions.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return EditNodeAction.class;
	}
	public void setNodeText(MindMapNode selected, String newText) {
		String oldText = selected.toString();

		EditNodeAction EditAction = new EditNodeAction();
		String nodeID = getNodeID(selected);
		EditAction.setNode(nodeID);
		EditAction.setText(newText);

		EditNodeAction undoEditAction = new EditNodeAction();
		undoEditAction.setNode(nodeID);
		undoEditAction.setText(oldText);

		execute(new ActionPair(EditAction, undoEditAction));
	}

	

}
