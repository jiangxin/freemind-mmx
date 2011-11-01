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

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.UnregistryAttributeElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.controller.filter.util.SortedComboBoxModel;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class UnregistryAttributeActor extends AbstractActorXml {

	public UnregistryAttributeActor(MindMapController mindMapModeController) {
		super(mindMapModeController);
	}

	public XmlAction createAction(String name) {
		UnregistryAttributeElementaryAction action = new UnregistryAttributeElementaryAction();
		action.setName(name);
		return action;
	}

	public ActionPair createActionPair(String name) {
		ActionPair actionPair = new ActionPair(createAction(name),
				createUndoAction(name));
		return actionPair;
	}

	private XmlAction createUndoAction(String name) {
		final CompoundAction compoundAction = createCompoundAction();
		final SortedComboBoxModel values = getAttributeRegistry().getElement(
				name).getValues();
		final XmlAction firstAction = ((MindMapModeAttributeController) getAttributeController()).registryAttributeActor
				.createAction(name);
		compoundAction.addChoice(firstAction);
		for (int i = 0; i < values.getSize(); i++) {
			String value = values.getElementAt(i).toString();
			final XmlAction nextAction = ((MindMapModeAttributeController) getAttributeController()).registryAttributeValueActor
					.createAction(name, value);
			compoundAction.addChoice(nextAction);
		}
		return compoundAction;
	}

	public void act(XmlAction action) {
		if (action instanceof UnregistryAttributeElementaryAction) {
			UnregistryAttributeElementaryAction unregistryAttributeElementaryAction = (UnregistryAttributeElementaryAction) action;
			act(unregistryAttributeElementaryAction.getName());
		}

	}

	private void act(String name) {
		final AttributeRegistry registry = getAttributeRegistry();
		registry.unregistry(name);
	}

	public Class getDoActionClass() {
		return UnregistryAttributeElementaryAction.class;
	}

}
