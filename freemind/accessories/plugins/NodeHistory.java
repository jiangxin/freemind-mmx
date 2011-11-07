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
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.Controller;
import freemind.controller.MenuItemEnabledListener;
import freemind.extensions.HookRegistration;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.MapModule;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 */
public class NodeHistory extends MindMapNodeHookAdapter {

	/** Of NodeHolder */
	private static Vector sNodeVector = new Vector();

	private static int sCurrentPosition = 0;

	private static boolean sPreventRegistration = false;

	private static class NodeHolder {
		public String mNodeId;

		public String mMapModuleName;

		public NodeHolder(MindMapNode pNode,
				MindMapController pMindMapController) {
			mNodeId = pNode.getObjectId(pMindMapController);
			MapModule mapModule = pMindMapController.getMapModule();
			if (mapModule == null) {
				throw new IllegalArgumentException(
						"MapModule not present to controller "
								+ pMindMapController);
			}
			mMapModuleName = mapModule.toString();
		}

		/** @return null, if node not found. */
		public MindMapNode getNode(Controller pController) {
			ModeController modeController = getModeController(pController);
			if (modeController != null) {
				return modeController.getNodeFromID(mNodeId);
			}
			return null;
		}

		private ModeController getModeController(Controller pController) {
			ModeController modeController = null;
			MapModule mapModule = getMapModule(pController);
			if (mapModule != null) {
				modeController = mapModule.getModeController();
			}
			return modeController;
		}

		private MapModule getMapModule(Controller pController) {
			MapModule mapModule = null;
			Map mapModules = pController.getMapModuleManager().getMapModules();
			for (Iterator iter = mapModules.keySet().iterator(); iter.hasNext();) {
				String mapModuleName = (String) iter.next();
				if (mapModuleName != null
						&& mapModuleName.equals(mMapModuleName)) {
					mapModule = (MapModule) mapModules.get(mapModuleName);
					break;
				}
			}
			return mapModule;
		}

		public boolean isIdentical(MindMapNode pNode,
				MindMapController pMindMapController) {
			String id = pNode.getObjectId(pMindMapController);
			MapModule mapModule = pMindMapController.getMapModule();
			if (mapModule != null) {
				return id.equals(mNodeId);
			}
			return false;
		}

	}

	public static class Registration implements HookRegistration,
			NodeSelectionListener, MenuItemEnabledListener {

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void register() {
			controller.registerNodeSelectionListener(this, false);
		}

		public void deRegister() {
			controller.deregisterNodeSelectionListener(this);
		}

		public void onLostFocusNode(NodeView pNode) {
		}

		public void onFocusNode(NodeView pNode) {
			/*******************************************************************
			 * don't denote positions, if somebody navigates through them. *
			 */
			if (!NodeHistory.sPreventRegistration) {
				// no duplicates:
				if (sCurrentPosition > 0
						&& ((NodeHolder) sNodeVector.get(sCurrentPosition - 1))
								.isIdentical(pNode.getModel(), controller)) {
					// logger.info("Avoid duplicate " + pNode + " at " +
					// sCurrentPosition);
					return;
				}
				if (sCurrentPosition != sNodeVector.size()) {
					/***********************************************************
					 * * we change the selected in the middle of our vector.
					 * Thus we remove all the coming nodes:
					 **********************************************************/
					for (int i = sNodeVector.size() - 1; i >= sCurrentPosition; --i) {
						sNodeVector.removeElementAt(i);
					}
				}
				// logger.info("Adding " + pNode + " at " + sCurrentPosition);
				sNodeVector.add(new NodeHolder(pNode.getModel(), controller));
				sCurrentPosition++;
				// only the last 100 nodes
				while (sNodeVector.size() > 100) {
					sNodeVector.removeElementAt(0);
					sCurrentPosition--;
				}
			}
		}

		public void onSaveNode(MindMapNode pNode) {
		}

		public void onUpdateNodeHook(MindMapNode pNode) {
		}

		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			String hookName = ((NodeHookAction) pAction).getHookName();
			if ("accessories/plugins/NodeHistoryBack.properties"
					.equals(hookName)) {
				// back is only enabled if there are already some nodes to go
				// back ;-)
				return sCurrentPosition > 1;
			} else {
				return sCurrentPosition < sNodeVector.size();
			}
		}

		/* (non-Javadoc)
		 * @see freemind.modes.ModeController.NodeSelectionListener#onSelectionChange(freemind.modes.MindMapNode, boolean)
		 */
		public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
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
		String direction = getResourceString("direction");
		// logger.info("Direction: " + direction);
		if ("back".equals(direction)) {
			if (sCurrentPosition > 1) {
				--sCurrentPosition;
			} else {
				return;
			}
		} else {
			if (sCurrentPosition < sNodeVector.size()) {
				++sCurrentPosition;
			} else {
				return;
			}

		}
		if (sCurrentPosition == 0)
			return;
		// printVector();
		NodeHolder nodeHolder = (NodeHolder) sNodeVector
				.get(sCurrentPosition - 1);
		final Controller mainController = getController().getController();
		final MindMapNode toBeSelected = (nodeHolder).getNode(mainController);
		boolean changeModule = false;
		MapModule newModule = null;
		if (nodeHolder.getModeController(mainController) != getMindMapController()) {
			changeModule = true;
			newModule = nodeHolder.getMapModule(mainController);
			if (newModule == null) {
				// the map was apparently closed, we try with the next node.
				invoke(node);
				return;
			}
		}
		final boolean fChangeModule = changeModule;
		final MapModule fNewModule = newModule;
		logger.finest("Selecting " + toBeSelected + " at pos "
				+ sCurrentPosition);
		sPreventRegistration = true;
		/***********************************************************************
		 * as the selection is restored after invoke, we make this trick to
		 * change it.
		 **********************************************************************/
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ModeController c = modeController;
				if (fChangeModule) {
					boolean res = mainController.getMapModuleManager()
							.changeToMapModule(fNewModule.toString());
					if (!res) {
						logger.warning("Can't change to map module "
								+ fNewModule);
						sPreventRegistration = false;
						return;
					}
					c = fNewModule.getModeController();
				}
				if (!toBeSelected.isRoot()) {
					c.setFolded(toBeSelected.getParentNode(), false);
				}
				NodeView nodeView = c.getNodeView(toBeSelected);
				if (nodeView != null) {
					c.select(nodeView);
					sPreventRegistration = false;
				}
			}
		});
	}

	private void printVector() {
		StringBuffer sb = new StringBuffer("\n");
		int i = 0;
		for (Iterator iter = sNodeVector.iterator(); iter.hasNext();) {
			NodeHolder holder = (NodeHolder) iter.next();
			sb.append(((sCurrentPosition - 1 == i) ? "==>" : "   ")
					+ "Node pos " + i + " is "
					+ holder.getNode(getMindMapController().getController()));
			sb.append("\n");
			i++;
		}
		logger.info(sb.toString() + "\n");
	}

}
