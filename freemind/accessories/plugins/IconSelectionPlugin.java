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
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package accessories.plugins;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Action;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.common.dialogs.IconSelectionPopupDialog;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.IconAction;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;

/**
 * @author adapted to the plugin mechanism by ganzer
 *
 */
public class IconSelectionPlugin extends MindMapNodeHookAdapter {

	private MindIcon icon;

	/**
	 */
	public IconSelectionPlugin() {
		super();
	}
	
	public void invoke(MindMapNode rootNode) {
		// we dont need node. 
		NodeView focussed = getController().getSelectedView();
		Vector actions = new Vector();
		Vector items = new Vector();
		Vector itemdescriptions = new Vector();
		MindMapController controller = getMindMapController();
		Vector iconActions = controller.iconActions;
		for (Enumeration e = iconActions.elements(); e.hasMoreElements();) {
			IconAction action =
				((IconAction) e.nextElement());
			addActionToActionVector(actions, items, itemdescriptions, action);
		}
		// And add the remove action, too:
		addActionToActionVector(actions, items, itemdescriptions, controller.removeLastIconAction);
		addActionToActionVector(actions, items, itemdescriptions, controller.removeAllIconsAction);

		FreeMind frame = (FreeMind) getController().getFrame();
		IconSelectionPopupDialog selectionDialog =
			new IconSelectionPopupDialog(
				frame.getJFrame(),
				items,
				itemdescriptions,
				frame);

		final MapView mapView = controller.getView();
        mapView.scrollNodeToVisible(focussed, 0);
		Tools.moveDialogToPosition(frame, selectionDialog, focussed.getLocationOnScreen());
		selectionDialog.setModal(true);
		selectionDialog.show();
		// process result:
		int result = selectionDialog.getResult();
        if (result >= 0) {
			Action action = (Action) actions.get(result);
			action.actionPerformed(new ActionEvent(action, 0, "icon", selectionDialog.getModifiers()));
		}
	}

	/**
     */
    private void addActionToActionVector(Vector actions, Vector items, Vector itemdescriptions, Action action) {
        actions.add(action);
        items.add(action.getValue(Action.SMALL_ICON));
        itemdescriptions.add(action.getValue(Action.SHORT_DESCRIPTION));
    }

//	/* (non-Javadoc)
//	 * @see freemind.extensions.NodeHook#invoke()
//	 */
//	public void invoke(MindMapNode node) {
//		setNode(node);
//		node.addIcon(icon);
//		nodeChanged(node);
//	}


}
