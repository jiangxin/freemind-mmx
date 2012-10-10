/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package accessories.plugins;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.actions.generated.instance.ChangeRootNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeMotionListenerView;
import freemind.view.mindmapview.NodeView;

/**
 * Changes the root node to another one What happens with clouds? This is ok, as
 * it can be removed afterwards.
 * 
 * @author foltin
 * @date 01.10.2011
 */
public class ChangeRootNode extends MindMapNodeHookAdapter {
	private static final String TRANSACTION_NAME = "ChangeRootNode";

	public void invoke(MindMapNode node) {
		// we dont need node.
		MindMapNode focussed = getMindMapController().getSelected();
		MindMapNode rootNode = getMindMapController().getRootNode();

		getMindMapController().doTransaction(
				TRANSACTION_NAME,
				new ActionPair(getAction(focussed), getAction(rootNode)));

	};

	/**
	 * @param pNode
	 *            the new root node.
	 * @return the corresponding action.
	 */
	private XmlAction getAction(MindMapNode pNode) {
		ChangeRootNodeAction action = new ChangeRootNodeAction();
		action.setNode(getMindMapController().getNodeID(pNode));
		return action;
	}

	public static class Registration implements HookRegistration,
			MenuItemEnabledListener, ActorXml {

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing
		 * .JMenuItem, javax.swing.Action)
		 */
		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			return controller.getSelecteds().size() == 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.extensions.HookRegistration#register()
		 */
		public void register() {
			controller.getActionFactory().registerActor(this,
					getDoActionClass());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.extensions.HookRegistration#deRegister()
		 */
		public void deRegister() {
			controller.getActionFactory().deregisterActor(getDoActionClass());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.mindmapmode.actions.xml.ActorXml#act(freemind.controller
		 * .actions.generated.instance.XmlAction)
		 */
		public void act(XmlAction pAction) {
			if (pAction instanceof ChangeRootNodeAction) {
				ChangeRootNodeAction rootNodeAction = (ChangeRootNodeAction) pAction;
				NodeAdapter focussed = controller.getNodeFromID(rootNodeAction
						.getNode());
				if (focussed.isRoot()) {
					// node is already root. Everything ok.
					return;
				}
				/*
				 * moving the hooks: 1. new interface method: movehook 2. change
				 * root node from old to new node copying text, decoration, etc.
				 * 3. deactivate all root hooks. this is possibly the best
				 * solution as it is consequent. Method 3 is chosen.
				 */
				MindMapNode oldRoot = mMap.getRootNode();
				oldRoot.removeAllHooks();
				// change the root node:
				mMap.changeRoot(focussed);
				// remove all viewers:
				Vector nodes = new Vector();
				nodes.add(focussed);
				while (!nodes.isEmpty()) {
					MindMapNode child = (MindMapNode) nodes.firstElement();
					logger.fine("Removing viewers for " + child);
					nodes.remove(0);
					nodes.addAll(child.getChildren());
					Collection viewers = new Vector(child.getViewers());
					for (Iterator it = viewers.iterator(); it.hasNext();) {
						NodeView viewer = (NodeView) it.next();
						child.removeViewer(viewer);
					}
				}

				MapView mapView = controller.getView();
				for (int i = mapView.getComponentCount() - 1; i >= 0; i--) {
					Component comp = mapView.getComponent(i);
					if (comp instanceof NodeView
							|| comp instanceof NodeMotionListenerView) {
						mapView.remove(comp);
					}
				}
				mapView.initRoot();
				mapView.add(mapView.getRoot());
				mapView.doLayout();
				controller.nodeChanged(focussed);
				logger.fine("layout done.");
				controller.select(focussed,
						Tools.getVectorWithSingleElement(focussed));
				controller.centerNode(focussed);
				controller.obtainFocusForSelected();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.mindmapmode.actions.xml.ActorXml#getDoActionClass()
		 */
		public Class getDoActionClass() {
			return ChangeRootNodeAction.class;
		}

	}

}
