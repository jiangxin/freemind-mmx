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
 * Created on 05.05.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.main.FreeMind;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

public class DeleteChildAction extends MindmapAction  {
	private final MindMapController mMindMapController;
	private String text;

	public DeleteChildAction(MindMapController modeController) {
		super("remove_node", "images/editdelete.png", modeController);
		text = modeController.getText("remove_node");
		this.mMindMapController = modeController;
	}

	public void actionPerformed(ActionEvent e) {
		// ask user if not root is selected:
		for (Iterator iterator = mMindMapController.getSelecteds().iterator(); iterator
				.hasNext();) {
			MindMapNode node = (MindMapNode) iterator.next();
			if (node.isRoot()) {
				mMindMapController.getController().errorMessage(
						mMindMapController.getFrame().getResourceString(
								"cannot_delete_root"));
				return;
			}
		}
		int showResult = new OptionalDontShowMeAgainDialog(mMindMapController
				.getFrame().getJFrame(), mMindMapController.getSelectedView(),
				"really_remove_node", "confirmation", mMindMapController,
				new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
						mMindMapController.getController(),
						FreeMind.RESOURCES_DELETE_NODES_WITHOUT_QUESTION),
				OptionalDontShowMeAgainDialog.ONLY_OK_SELECTION_IS_STORED)
				.show().getResult();
		if (showResult != JOptionPane.OK_OPTION) {
			return;
		}
		// because of multiple selection, cut is better.
		mMindMapController.cut();
		// this.c.deleteNode(c.getSelected());
	}

	public Class getDoActionClass() {
		return DeleteNodeAction.class;
	}


}
