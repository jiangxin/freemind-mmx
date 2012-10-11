/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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


package freemind.modes.filemode;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import freemind.controller.MenuBar;
import freemind.controller.StructuredMenuHolder;
import freemind.extensions.HookFactory;
import freemind.modes.MapAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.common.actions.NewMapAction;
import freemind.modes.viewmodes.ViewControllerAdapter;
import freemind.view.mindmapview.MainView;

public class FileController extends ViewControllerAdapter {

	Action newMap = new NewMapAction(this);
	Action center = new CenterAction();
	Action openPath = new OpenPathAction();

	private JPopupMenu popupmenu = new FilePopupMenu(this);

	public FileController(Mode mode) {
		super(mode);
	}

	public JToolBar getModeToolBar() {
		return ((FileMode) getMode()).getToolbar();
	}

	public MapAdapter newModel(ModeController modeController) {
		return new FileMapModel(getFrame(), modeController);
	}

	public MindMapNode newNode(Object userObject, MindMap map) {
		return new FileNodeModel((File) userObject, getFrame(), map);
	}

	public JPopupMenu getPopupMenu() {
		return this.popupmenu;
	}

	// -----------------------------------------------------------------------------------

	// Private
	//

	private class CenterAction extends AbstractAction {
		CenterAction() {
			super(getController().getResourceString("center"));
		}

		public void actionPerformed(ActionEvent e) {
			if (getSelected() != null) {
				MindMap map = new FileMapModel(
						((FileNodeModel) getSelected()).getFile(), getFrame(),
						/*
						 * DON'T COPY THIS, AS THIS IS A BAD HACK! The
						 * Constructor needs a new instance of a modecontroller.
						 */
						FileController.this);
				newMap(map);
			}
		}
	}

	private class OpenPathAction extends AbstractAction {
		OpenPathAction() {
			super(getController().getResourceString("open"));
		}

		public void actionPerformed(ActionEvent e) {
			String inputValue = JOptionPane.showInputDialog(getController()
					.getView().getSelected(), getText("open"), "");
			if (inputValue != null) {
				File newCenter = new File(inputValue);
				if (newCenter.exists()) { // and is a folder
					MindMap map = new FileMapModel(newCenter, getFrame(),
					/*
					 * DON'T COPY THIS, AS THIS IS A BAD HACK! The Constructor
					 * needs a new instance of a modecontroller.
					 */
					FileController.this);
					newMap(map);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ModeController#updateMenus(freemind.controller.
	 * StructuredMenuHolder)
	 */
	public void updateMenus(StructuredMenuHolder holder) {
		add(holder, MenuBar.EDIT_MENU + "/find", find, "keystroke_find");
		add(holder, MenuBar.EDIT_MENU + "/findNext", findNext,
				"keystroke_find_next");
		add(holder, MenuBar.EDIT_MENU + "/openPath", openPath, null);
	}

	public HookFactory getHookFactory() {
		throw new IllegalArgumentException("Not implemented yet.");
	}

	public void plainClick(MouseEvent e) {
		/* perform action only if one selected node. */
		if (getSelecteds().size() != 1)
			return;
		final MainView component = (MainView) e.getComponent();
		if (component.isInFollowLinkRegion(e.getX())) {
			loadURL();
		} else {
			MindMapNode node = (component).getNodeView().getModel();
			toggleFolded(node);
		}
	}

	private void toggleFolded(MindMapNode node) {
		if (node.hasChildren() && !node.isRoot()) {
			setFolded(node, !node.isFolded());
		}
	}

}
