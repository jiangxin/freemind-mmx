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
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
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
		for (Iterator itPastedNodes = mindMapNodes.iterator(); itPastedNodes.hasNext();) {
			MindMapNode copiedNode = (MindMapNode) itPastedNodes.next();
			// now, we need to look if it is a clone of somebody (chain: source -> clone -> clone)
			HashSet originalNodeIds = ((Registration) getPluginBaseClass()).getOriginalNodeIds();
			for (Iterator originalIdsIt = originalNodeIds.iterator(); originalIdsIt
					.hasNext();) {
				String originalId = (String) originalIdsIt.next();
				try {
					MindMapNode original = getMindMapController().getNodeFromID(originalId);
					ClonePlugin clonePlugin = getHook(original);
					if(clonePlugin != null) {
						List cloneNodes = clonePlugin.getCloneNodes();
						if(cloneNodes.contains(copiedNode)){
							// found!
							copiedNode = original;
							// get at least one clone (such that the plugin itself is not copied!)
							MindMapNode cloneNode = null;
							for (Iterator it = cloneNodes.iterator(); it
							.hasNext();) {
								MindMapNode node = (MindMapNode) it.next();
								if(node != copiedNode) {
									cloneNode = node;
								}
							}
							if(cloneNode == null){
								throw new IllegalArgumentException("Clone plugin but no additional clone found.");
							}
							Transferable copy = getMindMapController().copy(cloneNode, true);
							addNewClone(copiedNode, pNode, copy);
							return;
						}
					}
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
			// finally, we construct a new one:
			logger.info("Create new clone plugin");
			// first copy, as the hook shouldn't be copied....
			Transferable copy = getMindMapController().copy(copiedNode, true);
			Vector selecteds = Tools.getVectorWithSingleElement(copiedNode);
			getMindMapController().addHook(copiedNode,
					selecteds,
					ClonePlugin.PLUGIN_NAME);
			addNewClone(copiedNode, pNode, copy);
		}
	}

	public void addNewClone(MindMapNode originalNode, MindMapNode pDestinationNode, Transferable copy) {
		String originalNodeId = getMindMapController().getNodeID(originalNode);
		logger.info("Original node " + originalNode + ", id "
				+ originalNodeId);
		if (originalNode.isRoot()) {
			throw new IllegalArgumentException("Root can't be cloned");
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
		for (Iterator it = pDestinationNode.getChildren().iterator(); it.hasNext();) {
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
		ClonePlugin clonePlugin = getHook(originalNode);
		clonePlugin.addClone(cloneNode);
	}

	private ClonePlugin getHook(MindMapNode originalNode) {
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
	
	public Vector getMindMapNodes() {
		return ((Registration) getPluginBaseClass()).getMindMapNodes();
	}

	public static class Registration implements HookRegistration,
			MenuItemEnabledListener {

		private HashSet/* String */ mOriginalNodeIds = new HashSet();
		
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
			Vector mindMapNodes = getMindMapNodes();
//			logger.warning("Nodes " + Tools.listToString(mindMapNodes));
			return !mindMapNodes.isEmpty();
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
//					freemind.main.Resources.getInstance().logException(e);
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
