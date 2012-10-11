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

import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import freemind.controller.actions.generated.instance.AddIconAction;
import freemind.controller.actions.generated.instance.RemoveIconXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.Tools;
import freemind.modes.IconInformation;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * @author foltin
 * 
 */
public class RemoveIconAction extends NodeGeneralAction implements
		NodeActorXml, IconInformation {

	private IconAction iconAction;

	/**
     */
	public RemoveIconAction(MindMapController modeController) {
		super(modeController, "remove_last_icon", "images/remove.png");
		addActor(this);
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		List icons = selected.getIcons();
		if (icons.size() == 0)
			return null;
		AddIconAction undoAction = iconAction.createAddIconAction(selected,
				(MindIcon) icons.get(icons.size() - 1), MindIcon.LAST);
		return new ActionPair(
				createRemoveIconXmlAction(selected, MindIcon.LAST), undoAction);
	}

	public Class getDoActionClass() {
		return RemoveIconXmlAction.class;
	}

	public RemoveIconXmlAction createRemoveIconXmlAction(MindMapNode node,
			int iconPosition) {
		RemoveIconXmlAction action = new RemoveIconXmlAction();
		action.setNode(node.getObjectId(modeController));
		action.setIconPosition(iconPosition);
		return action;
	}

	public int removeLastIcon(MindMapNode node) {
		modeController.doTransaction(
				(String) getValue(NAME), apply(modeController.getMap(), node));
		return node.getIcons().size();
	}

	/**
    *
    */

	public void act(XmlAction action) {
		if (action instanceof freemind.controller.actions.generated.instance.RemoveIconXmlAction) {
			freemind.controller.actions.generated.instance.RemoveIconXmlAction removeAction = (freemind.controller.actions.generated.instance.RemoveIconXmlAction) action;
			MindMapNode node = modeController.getNodeFromID(removeAction
					.getNode());
			int position = removeAction.getIconPosition();
			node.removeIcon(position);
			modeController.nodeChanged(node);
		}
	}

	/**
	 * @param iconAction
	 *            The addIconAction to set.
	 */
	public void setIconAction(IconAction iconAction) {
		this.iconAction = iconAction;
	}

	public String getDescription() {
		return (String) getValue(Action.SHORT_DESCRIPTION);
	}

	public ImageIcon getIcon() {
		return (ImageIcon) getValue(Action.SMALL_ICON);
	}

	public KeyStroke getKeyStroke() {
		return Tools.getKeyStroke(getMindMapController().getFrame()
				.getAdjustableProperty(getKeystrokeResourceName()));
	}

	public String getKeystrokeResourceName() {
		return "keystroke_remove_last_icon";
	}
}
