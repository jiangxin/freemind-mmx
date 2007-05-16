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
 * Created on 28.03.2004
 *
 */
package accessories.plugins;

import java.awt.EventQueue;
import java.util.Vector;

import freemind.extensions.HookRegistration;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class NodeHistory extends MindMapNodeHookAdapter {

	public static class Registration implements HookRegistration,
			NodeSelectionListener {

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		private Vector mNodeVector = new Vector();

		private int mCurrentPosition = 0;

		private MindMapNode mPreventRegistration = null;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void register() {
			controller.registerNodeSelectionListener(this);
		}

		public void deRegister() {
			controller.deregisterNodeSelectionListener(this);
		}

		public void onLooseFocusHook(MindMapNode pNode) {
		}

		public void onReceiveFocusHook(MindMapNode pNode) {
			/*******************************************************************
			 * don't denote positions, if somebody navigates through them. *
			 */
			if (mPreventRegistration != pNode) {
				if (mCurrentPosition != mNodeVector.size()) {
					/***********************************************************
					 * * we change the selected in the middle of our vector.
					 * Thus we remove all the coming nodes:
					 **********************************************************/
					for (int i = mNodeVector.size() - 1; i >= mCurrentPosition; --i) {
						mNodeVector.removeElementAt(i);
					}
				}
				// no duplicates:
				if (mCurrentPosition > 0
						&& mNodeVector.get(mCurrentPosition - 1) == pNode) {
					logger.info("Avoid duplicate " + pNode);
					return;
				} else {
					logger.info("Adding " + pNode);
					mNodeVector.add(pNode);
					mCurrentPosition++;
					// only the last 100 nodes
					while (mNodeVector.size() > 100) {
						mNodeVector.removeElementAt(0);
						mCurrentPosition--;
					}
				}
			}
		}

		public void onSaveNode(MindMapNode pNode) {
		}

		public void onUpdateNodeHook(MindMapNode pNode) {
		}

	}

	/**
	 * 
	 */
	public NodeHistory() {
		super();
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		final Registration registration = (Registration) getPluginBaseClass();
		final MindMapController modeController = getMindMapController();

		String foldingType = getResourceString("direction");
		if ("back".equals(foldingType)) {
			if (registration.mCurrentPosition > 1) {
				--registration.mCurrentPosition;
			} else {
				return;
			}
		} else {
			if (registration.mCurrentPosition < registration.mNodeVector.size()) {
				++registration.mCurrentPosition;
			} else {
				return;
			}

		}
		if (registration.mCurrentPosition == 0)
			return;
		final MindMapNode toBeSelected = (MindMapNode) registration.mNodeVector
				.get(registration.mCurrentPosition - 1);
		logger.finest("Selecting " + toBeSelected + " at pos "
				+ registration.mCurrentPosition);
		/***********************************************************************
		 * as the selection is restored after invoke, we make this trick to
		 * change it.
		 **********************************************************************/
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				modeController.setFolded(toBeSelected, false);
				NodeView nodeView = modeController.getNodeView(toBeSelected);
				if (nodeView != null) {
					registration.mPreventRegistration = toBeSelected;
					modeController.select(nodeView);
				}
			}
		});
	}

}
