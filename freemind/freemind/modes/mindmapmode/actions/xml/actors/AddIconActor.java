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

import freemind.controller.actions.generated.instance.AddIconAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedback;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * @date 25.03.2014
 */
public class AddIconActor extends XmlActorAdapter {

	/**
	 * @param pMapFeedback
	 */
	public AddIconActor(ExtendedMapFeedback pMapFeedback) {
		super(pMapFeedback);
	}
	
	public void addIcon(MindMapNode node, MindIcon icon) {
		execute(getAddLastIconActionPair(node, icon));
	}

	public void act(XmlAction action) {
		if (action instanceof AddIconAction) {
			AddIconAction iconAction = (AddIconAction) action;
			MindMapNode node = getNodeFromID(iconAction
					.getNode());
			String iconName = iconAction.getIconName();
			int position = iconAction.getIconPosition();
			MindIcon icon = MindIcon.factory(iconName);
			node.addIcon(icon, position);
			getExMapFeedback().nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return AddIconAction.class;
	}

	public AddIconAction createAddIconAction(MindMapNode node, MindIcon icon,
			int iconPosition) {
		AddIconAction action = new AddIconAction();
		action.setNode(getNodeID(node));
		action.setIconName(icon.getName());
		action.setIconPosition(iconPosition);
		return action;
	}
	
	/**
     */
	private ActionPair getAddLastIconActionPair(MindMapNode node, MindIcon icon) {
		int iconIndex = MindIcon.LAST;
		return getAddIconActionPair(node, icon, iconIndex);
	}

	private ActionPair getAddIconActionPair(MindMapNode node, MindIcon icon,
			int iconIndex) {
		AddIconAction doAction = createAddIconAction(node, icon, iconIndex);
		XmlAction undoAction = getXmlActorFactory().getRemoveIconActor().createRemoveIconXmlAction(
				node, iconIndex);
		return new ActionPair(doAction, undoAction);
	}

	/**
     */
	private ActionPair getToggleIconActionPair(MindMapNode node, MindIcon icon) {
		int iconIndex = Tools.iconFirstIndex(node,
				icon.getName());
		if (iconIndex == -1) {
			return getAddLastIconActionPair(node, icon);
		} else {
			return getRemoveIconActionPair(node, icon, iconIndex);
		}
	}

	/**
	 * @param removeFirst
	 */
	private ActionPair getRemoveIconActionPair(MindMapNode node, MindIcon icon,
			boolean removeFirst) {
		int iconIndex = removeFirst ? Tools.iconFirstIndex(
				node, icon.getName()) : Tools.iconLastIndex(
				node, icon.getName());
		return iconIndex >= 0 ? getRemoveIconActionPair(node, icon, iconIndex)
				: null;
	}

	private ActionPair getRemoveIconActionPair(MindMapNode node, MindIcon icon,
			int iconIndex) {
		XmlAction doAction = getXmlActorFactory().getRemoveIconActor().createRemoveIconXmlAction(
				node, iconIndex);
		XmlAction undoAction = createAddIconAction(node, icon, iconIndex);
		return new ActionPair(doAction, undoAction);
	}

	public void toggleIcon(MindMapNode node, MindIcon icon) {
		getExMapFeedback().doTransaction(
				this.getClass().getName()+"/toggle", getToggleIconActionPair(node, icon));
	}

	public void removeIcon(MindMapNode node, MindIcon icon, boolean removeFirst) {
		final ActionPair removeIconActionPair = getRemoveIconActionPair(node,
				icon, removeFirst);
		if (removeIconActionPair == null) {
			return;
		}
		getExMapFeedback().doTransaction(
				this.getClass().getName()+"/remove", removeIconActionPair);
	}



}
