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

import java.util.Iterator;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.RemoveAllIconsXmlAction;
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
public class RemoveAllIconsAction extends NodeGeneralAction implements
		NodeActorXml, IconInformation {

	private final IconAction addIconAction;

	/**
     */
	public RemoveAllIconsAction(MindMapController modeController,
			IconAction addIconAction) {
		super(modeController, "remove_all_icons", "images/edittrash.png");
		this.addIconAction = addIconAction;
		addActor(this);
	}

	public ActionPair apply(MindMap model, MindMapNode selected) {
		CompoundAction undoAction = new CompoundAction();
		for (Iterator i = selected.getIcons().iterator(); i.hasNext();) {
			MindIcon icon = (MindIcon) i.next();
			undoAction.addChoice(addIconAction.createAddIconAction(selected,
					icon, MindIcon.LAST));
		}
		return new ActionPair(createRemoveAllIconsXmlAction(selected),
				undoAction);
	}

	public RemoveAllIconsXmlAction createRemoveAllIconsXmlAction(
			MindMapNode node) {
		RemoveAllIconsXmlAction action = new RemoveAllIconsXmlAction();
		action.setNode(node.getObjectId(modeController));
		return action;
	}

	public void act(XmlAction action) {
		if (action instanceof RemoveAllIconsXmlAction) {
			RemoveAllIconsXmlAction removeAction = (RemoveAllIconsXmlAction) action;
			MindMapNode node = modeController.getNodeFromID(removeAction
					.getNode());
			while (node.getIcons().size() > 0) {
				node.removeIcon(MindIcon.LAST);
			}
			modeController.nodeChanged(node);
		}
	}

	public void removeAllIcons(MindMapNode node) {
		modeController.doTransaction(
				(String) getValue(NAME), apply(modeController.getMap(), node));
	}

	public Class getDoActionClass() {
		return RemoveAllIconsXmlAction.class;
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
		return "keystroke_remove_all_icons";
	}
}
