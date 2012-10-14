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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import freemind.main.XMLElement;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.common.CommonNodeKeyListener;
import freemind.modes.common.CommonNodeKeyListener.EditHandler;
import freemind.modes.common.CommonToggleFoldedAction;
import freemind.modes.common.actions.FindAction;
import freemind.modes.common.actions.FindAction.FindNextAction;
import freemind.modes.common.listeners.CommonMouseMotionManager;
import freemind.modes.common.listeners.CommonNodeMouseMotionListener;
import freemind.view.mindmapview.MainView;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public abstract class ViewControllerAdapter extends ControllerAdapter {

	public CommonToggleFoldedAction toggleFolded = null;

	public CommonToggleChildrenFoldedAction toggleChildrenFolded = null;

	public FindAction find = null;

	public FindNextAction findNext = null;

	/**
	 */
	public ViewControllerAdapter(Mode mode) {
		super(mode);
		toggleFolded = new CommonToggleFoldedAction(this);
		toggleChildrenFolded = new CommonToggleChildrenFoldedAction(this);
		find = new FindAction(this);
		findNext = new FindNextAction(this, find);
	}

	public void doubleClick(MouseEvent e) {
	}

	public void plainClick(MouseEvent e) {
	}

	public boolean extendSelection(MouseEvent e) {
		// FIXME: Remove double code
		NodeView newlySelectedNodeView = ((MainView) e.getComponent())
				.getNodeView();
		// MindMapNode newlySelectedNode = newlySelectedNodeView.getModel();
		boolean extend = e.isControlDown();
		boolean range = e.isShiftDown();
		boolean branch = e.isAltGraphDown() || e.isAltDown(); /*
															 * windows alt,
															 * linux altgraph
															 * ....
															 */
		boolean retValue = false;

		if (extend || range || branch
				|| !getView().isSelected(newlySelectedNodeView)) {
			if (!range) {
				if (extend)
					getView().toggleSelected(newlySelectedNodeView);
				else
					select(newlySelectedNodeView);
				retValue = true;
			} else {
				retValue = getView().selectContinuous(newlySelectedNodeView);
				// /* fc, 25.1.2004: replace getView by controller methods.*/
				// if (newlySelectedNodeView != getView().getSelected() &&
				// newlySelectedNodeView.isSiblingOf(getView().getSelected())) {
				// getView().selectContinuous(newlySelectedNodeView);
				// retValue = true;
				// } else {
				// /* if shift was down, but no range can be selected, then the
				// new node is simply selected: */
				// if(!getView().isSelected(newlySelectedNodeView)) {
				// getView().toggleSelected(newlySelectedNodeView);
				// retValue = true;
				// }
			}
			if (branch) {
				getView().selectBranch(newlySelectedNodeView, extend);
				retValue = true;
			}
		}

		if (retValue) {
			e.consume();

			// Display link in status line
			String link = newlySelectedNodeView.getModel().getLink();
			link = (link != null ? link : " ");
			getController().getFrame().out(link);
		}
		return retValue;
	}

	public void setFolded(MindMapNode node, boolean folded) {
		_setFolded(node, folded);
	}

	public void startupController() {
		super.startupController();
		getController().getNodeMouseMotionListener().register(
				new CommonNodeMouseMotionListener(this));
		getController().getMapMouseMotionListener().register(
				new CommonMouseMotionManager(this));
		getController().getNodeKeyListener().register(
				new CommonNodeKeyListener(this, new EditHandler() {

					public void edit(KeyEvent e, boolean addNew,
							boolean editLong) {
						// no edit.
					}
				}));

	}

	public void shutdownController() {
		super.shutdownController();
		getController().getNodeMouseMotionListener().deregister();
		getController().getMapMouseMotionListener().deregister();
		getController().getNodeKeyListener().deregister();
	}

	protected void setAllActions(boolean enabled) {
		super.setAllActions(enabled);
		find.setEnabled(enabled);
		findNext.setEnabled(enabled);
		toggleFolded.setEnabled(enabled);
		toggleChildrenFolded.setEnabled(enabled);
	}

	public XMLElement createXMLElement() {
		throw new IllegalArgumentException(
				"createXMLElement is not defined for "
						+ this.getClass().getName());
	}

}
