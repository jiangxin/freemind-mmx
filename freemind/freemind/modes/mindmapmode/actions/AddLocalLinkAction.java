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
 * Created on 07.10.2004
 */


package freemind.modes.mindmapmode.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * @author foltin
 * 
 */
public class AddLocalLinkAction extends FreemindAction {

	private final MindMapController modeController;

	/**
     */
	public AddLocalLinkAction(MindMapController modeController) {
		super("add_local_link", "images/LinkLocal.png", modeController);
		this.modeController = modeController;
	}

	public void actionPerformed(ActionEvent e) {
		// assert that at least two nodes are selected. draw an arrow link in
		// between.
		List selecteds = modeController.getSelecteds();
		if (selecteds.size() < 2) {
			modeController.getController().errorMessage(
					modeController.getText("less_than_two_selected_nodes"));
			return;
		}
		MindMapNode target = (MindMapNode) selecteds.get(0);
		String targetId = (target).getObjectId(modeController);
		for (int i = 1; i < selecteds.size(); i++) {
			MindMapNode source = (MindMapNode) selecteds.get(i);
			modeController.setLink(source, "#" + targetId);
		}
	}

}
