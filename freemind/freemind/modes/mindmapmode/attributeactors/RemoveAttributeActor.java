/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/*
 * Created on 29.01.2006
 * Created by Dimitri Polivaev
 */
package freemind.modes.mindmapmode.attributeactors;

import freemind.controller.actions.generated.instance.DeleteAttributeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class RemoveAttributeActor extends AbstractActorXml {

	public RemoveAttributeActor(MindMapController mindMapModeController) {
		super(mindMapModeController);
	}

	public XmlAction createAction(NodeAttributeTableModel model, int row) {
		DeleteAttributeElementaryAction action = new DeleteAttributeElementaryAction();
		action.setNode(getNodeID(model.getNode()));
		action.setRow(row);
		return action;
	}

	public ActionPair createActionPair(NodeAttributeTableModel model, int row) {
		String name = model.getAttribute(row).getName();
		String value = model.getAttribute(row).getValue();
		ActionPair actionPair = new ActionPair(
				createAction(model, row),
				((MindMapModeAttributeController) getAttributeController()).insertAttributeActor
						.createAction(model, row, name, value));
		return actionPair;
	}

	public void act(XmlAction action) {
		if (action instanceof DeleteAttributeElementaryAction) {
			DeleteAttributeElementaryAction AttributeAction = (DeleteAttributeElementaryAction) action;
			NodeAdapter node = getNode(AttributeAction.getNode());
			node.createAttributeTableModel();
			act(node.getAttributes(),
					AttributeAction.getRow());
		}

	}

	private void act(NodeAttributeTableModel model, int row) {
		model.getAttributes().remove(row);
		model.disableStateIcon();
		model.fireTableRowsDeleted(row, row);
	}

	public Class getDoActionClass() {
		return DeleteAttributeElementaryAction.class;
	}

}
