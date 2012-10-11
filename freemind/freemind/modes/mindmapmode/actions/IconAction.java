/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2004  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 *
 * Created on 29.09.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import freemind.controller.actions.generated.instance.AddIconAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.controller.filter.condition.IconContainedCondition;
import freemind.main.Tools;
import freemind.modes.IconInformation;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;

public class IconAction extends FreemindAction implements ActorXml,
		IconInformation {
	public MindIcon icon;
	private final MindMapController modeController;
	private final RemoveIconAction removeLastIconAction;

	public IconAction(MindMapController controller, MindIcon _icon,
			RemoveIconAction removeLastIconAction) {
		super(_icon.getDescription(), _icon.getIcon(), controller);
		this.modeController = controller;
		this.removeLastIconAction = removeLastIconAction;
		putValue(Action.SHORT_DESCRIPTION, _icon.getDescription());
		this.icon = _icon;
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getID() == ActionEvent.ACTION_FIRST
				&& (e.getModifiers() & ActionEvent.SHIFT_MASK
						& ~ActionEvent.CTRL_MASK & ~ActionEvent.ALT_MASK) != 0) {
			removeAllIcons();
			addLastIcon();
			return;
		}
		if (e == null
				|| (e.getModifiers() & (ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK)) == 0) {
			addLastIcon();
			return;
		}
		// e != null
		if ((e.getModifiers() & ~ActionEvent.SHIFT_MASK
				& ~ActionEvent.CTRL_MASK & ActionEvent.ALT_MASK) != 0) {
			removeIcon(false);
			return;
		}
		if ((e.getModifiers() & ~ActionEvent.SHIFT_MASK & ActionEvent.CTRL_MASK & ~ActionEvent.ALT_MASK) != 0) {
			removeIcon(true);
			return;
		}
	}

	private void addLastIcon() {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			addIcon(selected, icon);
		}
	}

	private void removeIcon(boolean removeFirst) {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			removeIcon(selected, icon, removeFirst);
		}
	}

	private void toggleIcon() {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			toggleIcon(selected, icon);
		}
	}

	private void removeAllIcons() {
		for (ListIterator it = modeController.getSelecteds().listIterator(); it
				.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel) it.next();
			if (selected.getIcons().size() > 0) {
				modeController.removeAllIcons(selected);
			}
		}
	}

	public void addIcon(MindMapNode node, MindIcon icon) {
		modeController.doTransaction(
				(String) getValue(NAME), getAddLastIconActionPair(node, icon));
	}

	private void toggleIcon(MindMapNode node, MindIcon icon) {
		modeController.doTransaction(
				(String) getValue(NAME), getToggleIconActionPair(node, icon));
	}

	private void removeIcon(MindMapNode node, MindIcon icon, boolean removeFirst) {
		final ActionPair removeIconActionPair = getRemoveIconActionPair(node,
				icon, removeFirst);
		if (removeIconActionPair == null) {
			return;
		}
		modeController.doTransaction(
				(String) getValue(NAME), removeIconActionPair);
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
		XmlAction undoAction = removeLastIconAction.createRemoveIconXmlAction(
				node, iconIndex);
		return new ActionPair(doAction, undoAction);
	}

	/**
     */
	private ActionPair getToggleIconActionPair(MindMapNode node, MindIcon icon) {
		int iconIndex = IconContainedCondition.iconFirstIndex(node,
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
		int iconIndex = removeFirst ? IconContainedCondition.iconFirstIndex(
				node, icon.getName()) : IconContainedCondition.iconLastIndex(
				node, icon.getName());
		return iconIndex >= 0 ? getRemoveIconActionPair(node, icon, iconIndex)
				: null;
	}

	private ActionPair getRemoveIconActionPair(MindMapNode node, MindIcon icon,
			int iconIndex) {
		XmlAction doAction = removeLastIconAction.createRemoveIconXmlAction(
				node, iconIndex);
		XmlAction undoAction = createAddIconAction(node, icon, iconIndex);
		return new ActionPair(doAction, undoAction);
	}

	public void act(XmlAction action) {
		if (action instanceof AddIconAction) {
			AddIconAction iconAction = (AddIconAction) action;
			MindMapNode node = modeController.getNodeFromID(iconAction
					.getNode());
			String iconName = iconAction.getIconName();
			int position = iconAction.getIconPosition();
			MindIcon icon = MindIcon.factory(iconName);
			node.addIcon(icon, position);
			modeController.nodeChanged(node);
		}
	}

	public Class getDoActionClass() {
		return AddIconAction.class;
	}

	public AddIconAction createAddIconAction(MindMapNode node, MindIcon icon,
			int iconPosition) {
		AddIconAction action = new AddIconAction();
		action.setNode(node.getObjectId(modeController));
		action.setIconName(icon.getName());
		action.setIconPosition(iconPosition);
		return action;
	}

	public MindIcon getMindIcon() {
		return icon;
	}

	public KeyStroke getKeyStroke() {
		final String keystrokeResourceName = icon.getKeystrokeResourceName();
		final String keyStrokeDescription = getMindMapController().getFrame()
				.getAdjustableProperty(keystrokeResourceName);
		return Tools.getKeyStroke(keyStrokeDescription);
	}

	public String getDescription() {
		return icon.getDescription();
	}

	public ImageIcon getIcon() {
		return icon.getIcon();
	}

	public String getKeystrokeResourceName() {
		return icon.getKeystrokeResourceName();
	}

}
