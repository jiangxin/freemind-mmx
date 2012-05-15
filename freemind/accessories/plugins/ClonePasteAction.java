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

import java.awt.datatransfer.Transferable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.MindMapNodesSelection;
import freemind.extensions.HookRegistration;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 25.4.2011
 * 
 */
public class ClonePasteAction extends MindMapNodeHookAdapter {

	/**
	 * 
	 */
	public ClonePasteAction() {
	}

	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		Vector mindMapNodes = getMindMapNodes();
		logger.info("Clones for nodes: " + Tools.listToString(mindMapNodes));
		// now, construct the plugin for those nodes:
		for (Iterator itPastedNodes = mindMapNodes.iterator(); itPastedNodes
				.hasNext();) {
			MindMapNode copiedNode = (MindMapNode) itPastedNodes.next();
			ClonePlugin clonePlugin = null;
			// now, we need to look if it is a clone of somebody (chain: source
			// -> clone -> clone)
			ShadowClonePlugin shadowHook = getShadowHook(copiedNode);
			if (shadowHook != null) {
				// found. get the original
				copiedNode = shadowHook.getOriginalNode();
			}
			clonePlugin = getHook(copiedNode);
			if (clonePlugin != null) {
				// get one clone node as it has the correct plugin already
				// attached.
				// get at least one clone (such that the plugin itself is not
				// copied!)
				List cloneNodes = clonePlugin.getCloneNodes();
				MindMapNode cloneNode = null;
				for (Iterator it = cloneNodes.iterator(); it.hasNext();) {
					MindMapNode node = (MindMapNode) it.next();
					if (node != copiedNode) {
						cloneNode = node;
					}
				}
				if (cloneNode != null) {
					Transferable copy = getMindMapController().copy(cloneNode,
							true);
					addNewClone(copiedNode, pNode, copy);
					return;
				} else {
					// ok, we have a clone without shadow nodes. We remove the
					// old hook:
					addHook(copiedNode);
					// and create a new fresh one.
				}
			}
			// finally, we construct a new one:
			logger.info("Create new clone plugin");
			Transferable copy = addHook(copiedNode);
			addNewClone(copiedNode, pNode, copy);
		}
	}

	public Transferable addHook(MindMapNode pOriginalNode) {
		// first copy, as the hook shouldn't be copied....
		Transferable copy = getMindMapController().copy(pOriginalNode, true);
		Vector selecteds = Tools.getVectorWithSingleElement(pOriginalNode);
		getMindMapController().addHook(pOriginalNode, selecteds,
				ClonePlugin.PLUGIN_LABEL);
		return copy;
	}

	public void addNewClone(MindMapNode originalNode,
			MindMapNode pDestinationNode, Transferable copy) {
		String originalNodeId = getMindMapController().getNodeID(originalNode);
		logger.info("Original node " + originalNode + ", id " + originalNodeId);
		if (originalNode.isRoot()) {
			throw new IllegalArgumentException("Root can't be cloned");
		}
		// next error case: the original node must not contain other clones!
		Vector childs = new Vector();
		childs.addAll(originalNode.getChildren());
		while(!childs.isEmpty()) {
			MindMapNode node = (MindMapNode) childs.firstElement();
			childs.remove(0);
			childs.addAll(node.getChildren());
			if(getHook(node)!= null || getShadowHook(node) != null) {
				throw new IllegalArgumentException("There is already the clone '" + node.getShortText(getMindMapController()) + "' inside. Clones can't be stacked.");
			}
		}
		// insert clone:
		List listOfChilds = pDestinationNode.getChildren();
		Vector listOfChildIds = new Vector();
		for (Iterator it = listOfChilds.iterator(); it.hasNext();) {
			String nodeID = getMindMapController().getNodeID(
					(MindMapNode) it.next());
			listOfChildIds.add(nodeID);
			logger.info("Old child id:" + nodeID);
		}
		getMindMapController().paste(copy, pDestinationNode);
		// how to get the clone node now?
		String cloneNodeId = null;
		MindMapNode cloneNode = null;
		for (Iterator it = pDestinationNode.getChildren().iterator(); it
				.hasNext();) {
			MindMapNode child = (MindMapNode) it.next();
			String childId = getMindMapController().getNodeID(child);
			logger.info("Checking " + child + " to be the new node, id "
					+ childId);
			if (!listOfChildIds.contains(childId)) {
				// clone found:
				cloneNodeId = childId;
				cloneNode = child;
				break;
			}
		}
		if (cloneNodeId == null || cloneNode == null) {
			throw new IllegalArgumentException("Clone node not found.");
		}
		ShadowClonePlugin shadowHook = getShadowHook(cloneNode);
		if (shadowHook == null) {
			Vector selecteds = Tools.getVectorWithSingleElement(cloneNode);
			getMindMapController().addHook(cloneNode, selecteds,
					ShadowClonePlugin.PLUGIN_LABEL);
		}
		shadowHook = getShadowHook(cloneNode);
		if (shadowHook == null) {
			throw new IllegalArgumentException(
					"Clone hook not found although created...");
		}
		shadowHook.setOriginalNodeId(originalNodeId);
		ClonePlugin clonePlugin = getHook(originalNode);
		clonePlugin.addClone(cloneNode);
	}

	public static ClonePlugin getHook(MindMapNode originalNode) {
		if(originalNode == null) {
			return null;
		}
		for (Iterator it2 = originalNode.getActivatedHooks().iterator(); it2
				.hasNext();) {
			PermanentNodeHook hook = (PermanentNodeHook) it2.next();
			if (hook instanceof ClonePlugin) {
				ClonePlugin cloneHook = (ClonePlugin) hook;
				return cloneHook;
			}
		}
		return null;
	}

	public static ShadowClonePlugin getShadowHook(MindMapNode cloneNode) {
		for (Iterator it2 = cloneNode.getActivatedHooks().iterator(); it2
				.hasNext();) {
			PermanentNodeHook hook = (PermanentNodeHook) it2.next();
			if (hook instanceof ShadowClonePlugin) {
				ShadowClonePlugin cloneHook = (ShadowClonePlugin) hook;
				return cloneHook;
			}
		}
		return null;
	}

	public Vector getMindMapNodes() {
		return ((Registration) getPluginBaseClass()).getMindMapNodes();
	}

	public static class Registration implements HookRegistration,
			MenuItemEnabledListener {

		private static final String PLUGIN_NAME = "accessories/plugins/ClonePasteAction.properties";

		private HashSet/* String */mOriginalNodeIds = new HashSet();

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			if (controller == null)
				return false;
			String hookName = ((NodeHookAction) pAction).getHookName();
			if (PLUGIN_NAME.equals(hookName)) {
				// only enabled, if nodes have been copied before.
				Vector mindMapNodes = getMindMapNodes();
				// logger.warning("Nodes " + Tools.listToString(mindMapNodes));
				return !mindMapNodes.isEmpty();
			}
			List selecteds = controller.getSelecteds();
			for (Iterator it = selecteds.iterator(); it.hasNext();) {
				MindMapNode node = (MindMapNode) it.next();
				if(getHook(node)!= null || getShadowHook(node) != null) {
					return true;
				}
			}
			return false;
		}

		public void deRegister() {
		}

		public void register() {
		}

		public Vector getMindMapNodes() {
			Vector mindMapNodes = new Vector();
			Transferable clipboardContents = controller.getClipboardContents();
			if (clipboardContents != null) {
				try {
					List transferData = (List) clipboardContents
							.getTransferData(MindMapNodesSelection.copyNodeIdsFlavor);
					for (Iterator it = transferData.iterator(); it.hasNext();) {
						String nodeId = (String) it.next();
						MindMapNode node = controller.getNodeFromID(nodeId);
						mindMapNodes.add(node);
					}
				} catch (Exception e) {
					// e.printStackTrace();
					// freemind.main.Resources.getInstance().logException(e);
				}
			}
			return mindMapNodes;
		}

		public void registerOriginal(String pOriginalNodeId) {
			mOriginalNodeIds.add(pOriginalNodeId);
		}

		public HashSet getOriginalNodeIds() {
			return mOriginalNodeIds;
		}

		public void deregisterOriginal(String pOriginalNodeId) {
			mOriginalNodeIds.remove(pOriginalNodeId);
		}
	}

}
