/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2005  Joerg Mueller, Daniel Polansky, Christian Foltin and others.
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
 * Created on 09.11.2005
 */

package freemind.modes.viewmodes;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class CommonToggleChildrenFoldedAction extends AbstractAction {

	private ViewControllerAdapter modeController;

	private Logger logger;

	public CommonToggleChildrenFoldedAction(ViewControllerAdapter controller) {
		super(controller.getText("toggle_children_folded"));
		this.modeController = controller;
		logger = modeController.getFrame().getLogger(this.getClass().getName());
	}

	public void actionPerformed(ActionEvent e) {
		NodeView selected = modeController.getSelectedView();
		modeController.toggleFolded.toggleFolded(selected.getModel()
				.childrenUnfolded());
		modeController.getView().selectAsTheOnlyOneSelected(selected);
		modeController.getController().obtainFocusForSelected();
	}

}
