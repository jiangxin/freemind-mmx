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

import freemind.controller.actions.generated.instance.SetAttributeVisibleElementaryAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.AbstractActorXml;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class SetAttributeVisibleActor extends AbstractActorXml {

	public SetAttributeVisibleActor(MindMapController mindMapModeController) {
		super(mindMapModeController);
	}

	public XmlAction createAction(int index, boolean value) {
		SetAttributeVisibleElementaryAction action = new SetAttributeVisibleElementaryAction();
		action.setIndex(index);
		action.setIsVisible(value);
		return action;
	}

	public ActionPair createActionPair(int index, boolean value) {
		final boolean previousValue = getAttributeRegistry().getElement(index)
				.isVisible();
		ActionPair actionPair = new ActionPair(createAction(index, value),
				createAction(index, previousValue));
		return actionPair;
	}

	public void act(XmlAction action) {
		if (action instanceof SetAttributeVisibleElementaryAction) {
			SetAttributeVisibleElementaryAction setAttributeVisibleAction = (SetAttributeVisibleElementaryAction) action;
			act(setAttributeVisibleAction.getIndex(),
					setAttributeVisibleAction.getIsVisible());
		}

	}

	private void act(int index, boolean value) {
		getAttributeRegistry().getElement(index).setVisibility(value);
		getAttributeRegistry().fireStateChanged();
	}

	public Class getDoActionClass() {
		return SetAttributeVisibleElementaryAction.class;
	}

}
