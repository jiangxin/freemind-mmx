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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;

import freemind.controller.MenuItemEnabledListener;
import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.MoveNodeXmlAction;
import freemind.controller.actions.generated.instance.MoveNodesAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.Tools.MindMapNodePair;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * This is the "paste node as clone" action from the menu.
 * 
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
			ClonePlugin clonePlugin = ClonePlugin.getHook(copiedNode);
			// first the clone master
			if (clonePlugin == null) {
				Vector selecteds = Tools.getVectorWithSingleElement(copiedNode);
				getMindMapController().addHook(copiedNode, selecteds,
						ClonePlugin.PLUGIN_LABEL, null);
			}
			// finally, we construct a new one:
			Transferable copy = getMindMapController().copy(copiedNode, true);
			addNewClone(copiedNode, pNode, copy);
		}
	}

	public void addNewClone(MindMapNode originalNode,
			MindMapNode pDestinationNode, Transferable copy) {
		String originalNodeId = getMindMapController().getNodeID(originalNode);
		logger.info("Original node " + originalNode + ", id " + originalNodeId);
		if (originalNode.isRoot()) {
			getMindMapController().getController().errorMessage(
					getMindMapController().getText(
							"clone_plugin_no_root_cloning"));
			return;
		}
		// next error case: the original node must not contain other clones!
		Vector childs = new Vector();
		childs.addAll(originalNode.getChildren());
		while (!childs.isEmpty()) {
			MindMapNode node = (MindMapNode) childs.firstElement();
			childs.remove(0);
			childs.addAll(node.getChildren());
			if (ClonePlugin.getHook(node) != null) {
				getMindMapController()
						.getController()
						.errorMessage(
								Resources
										.getInstance()
										.format("clone_plugin_no_stacking",
												new Object[] { node
														.getShortText(getMindMapController()) }));
				return;
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
	}

	public Vector getMindMapNodes() {
		return ((Registration) getPluginBaseClass()).getMindMapNodes();
	}

	public static class Registration implements HookRegistration,
			MenuItemEnabledListener, ActionFilter {

		private static final String PLUGIN_NAME = "accessories/plugins/ClonePasteAction.properties";

		/**
		 * String, HashSet of {@link MindMapNode}s
		 */
		private HashMap mCloneIdsMap = new HashMap();
		/**
		 * This is the reverse of mCloneIdsMap: {@link MindMapNode} to cloneId.
		 */
		private HashMap mClonesMap = new HashMap();

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void register() {
			controller.getActionFactory().registerFilter(this);
		}

		public void deRegister() {
			controller.getActionFactory().deregisterFilter(this);
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
				if (ClonePlugin.getHook(node) != null) {
					return true;
				}
			}
			return false;
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

		public String generateNewCloneId(String pProposedID) {
			return Tools.generateID(pProposedID, mCloneIdsMap, "CLONE_");
		}

		/**
		 * @param pCloneId
		 * @return true, if the pCloneId is new (not already registered)
		 */
		public boolean registerClone(String pCloneId, ClonePlugin pPlugin) {
			boolean vectorPresent = mCloneIdsMap.containsKey(pCloneId);
			HashSet v = getHashSetToCloneId(pCloneId);
			MindMapNode node = pPlugin.getNode();
			for (Iterator it = v.iterator(); it.hasNext();) {
				MindMapNode otherCloneNode = (MindMapNode) it.next();
				ClonePlugin otherClone = ClonePlugin.getHook(otherCloneNode);
				if (otherClone == null) {
					it.remove();
					logger.warning("Found clone node "
							+ controller.getNodeID(otherCloneNode)
							+ " which isn't a clone any more.");
					continue;
				}
				// inform all others
				otherClone.addClone(node);
				// inform this clone about its brothers
				pPlugin.addClone(otherCloneNode);
			}
			v.add(node);
			mClonesMap.put(node, pCloneId);
			return !vectorPresent;
		}

		public void deregisterClone(String pCloneId, ClonePlugin pPlugin) {
			HashSet cloneSet = getHashSetToCloneId(pCloneId);
			MindMapNode node = pPlugin.getNode();
			cloneSet.remove(node);
			mClonesMap.remove(node);
			// inform all others
			for (Iterator it = cloneSet.iterator(); it.hasNext();) {
				MindMapNode otherCloneNode = (MindMapNode) it.next();
				ClonePlugin otherClone = ClonePlugin.getHook(otherCloneNode);
				if (otherClone == null) {
					it.remove();
					logger.warning("Found clone node "
							+ controller.getNodeID(otherCloneNode)
							+ " which isn't a clone any more.");
					continue;
				}
				otherClone.removeClone(node);
			}
			if (cloneSet.isEmpty()) {
				// remove entire clone
				mCloneIdsMap.remove(cloneSet);
			}
		}

		protected HashSet getHashSetToCloneId(String pCloneId) {
			HashSet v = null;
			if (!mCloneIdsMap.containsKey(pCloneId)) {
				v = new HashSet();
				mCloneIdsMap.put(pCloneId, v);
			} else {
				v = (HashSet) mCloneIdsMap.get(pCloneId);
			}
			return v;
		}

		public ActionPair filterAction(ActionPair pair) {
			// shortcut for no clones for speed up.
			if (mCloneIdsMap.isEmpty()) {
				return pair;
			}
			XmlAction doAction = pair.getDoAction();
			doAction = cloneAction(doAction);
			pair.setDoAction(doAction);
			return pair;
		}

		private XmlAction cloneAction(XmlAction doAction) {
			logger.fine("Found do action: " + doAction.getClass().getName());
			if (doAction instanceof NodeAction) {
				NodeAction nodeAction = (NodeAction) doAction;
				MindMapNode node = controller.getNodeFromID(nodeAction
						.getNode());
				// check for clone or original?
				doAction = cloneAction(nodeAction, node);
			} else {
				if (doAction instanceof CompoundAction) {
					CompoundAction compoundAction = (CompoundAction) doAction;
					List choiceList = compoundAction.getListChoiceList();
					int index = 0;
					for (Iterator it = choiceList.iterator(); it.hasNext();) {
						XmlAction subAction = (XmlAction) it.next();
						subAction = cloneAction(subAction);
						compoundAction.setAtChoice(index, subAction);
						index++;
					}
				}
			}
			return doAction;
		}

		private XmlAction cloneAction(NodeAction nodeAction, MindMapNode node) {
			if (mClonesMap.containsKey(node)) {
				// ok, there is an action for a clone itself. be careful:
//				if (nodeAction instanceof MoveNodesAction) {
//					return nodeAction;
//				}
			}
			if (nodeAction instanceof CutNodeAction) {
				for (Iterator i = mClonesMap.keySet().iterator(); i.hasNext();) {
					MindMapNode clone = (MindMapNode) i.next();
					if (clone.isDescendantOfOrEqual(node)) {
						// the complete node is cut.
						logger.fine("Node " + printNodeId(clone) + " is cut.");
						return nodeAction;
					}
				}
			}
			List/* MindMapNodePair */correspondingNodes = getCorrespondingNodes(
					node, false);
			if (correspondingNodes.isEmpty()) {
				return nodeAction;
			}
			// create new action:
			CompoundAction compound = new CompoundAction();
			compound.addChoice(nodeAction);
			for (Iterator it = correspondingNodes.iterator(); it.hasNext();) {
				Tools.MindMapNodePair pair = (Tools.MindMapNodePair) it.next();
				getNewCompoundAction(nodeAction, pair, compound);
			}
			return compound;
		}

		/**
		 * This is the main method here. It returns to a given node its cloned
		 * nodes on the other side.
		 * 
		 * @param pNode
		 *            is checked to be son of one of the clones/original.
		 * @return a list of {@link MindMapNodePair}s where the first is the
		 *         corresponding node and the second is the clone. If the return
		 *         value is empty, the node isn't son of any.
		 */
		private List getCorrespondingNodes(MindMapNode pNode,
				boolean includeNodeItself) {
			Vector indexVector = new Vector();
			MindMapNode child = pNode;
			while (!mClonesMap.containsKey(child)) {
				if (child.isRoot()) {
					// nothing found!
					return Collections.EMPTY_LIST;
				}
				indexVector.add(0, new Integer(child.getParentNode()
						.getChildPosition(child)));
				child = child.getParentNode();
			}
			Vector returnValue = new Vector();
			MindMapNode originalNode = child;
			HashSet targets = (HashSet) mCloneIdsMap.get(mClonesMap.get(child));
			CloneLoop: for (Iterator itClone = targets.iterator(); itClone
					.hasNext();) {
				MindMapNode cloneNode = (MindMapNode) itClone.next();
				MindMapNode target = cloneNode;
				if (!includeNodeItself && cloneNode == originalNode)
					continue;
				for (Iterator it = indexVector.iterator(); it.hasNext();) {
					int index = ((Integer) it.next()).intValue();
					if (target.getChildCount() <= index) {
						logger.warning("Index " + index
								+ " in other tree not found from "
								+ printNodeIds(targets) + " originating from "
								+ printNodeId(cloneNode));
						// with crossed fingers.
						continue CloneLoop;
					}
					target = (MindMapNode) target.getChildAt(index);
				}
				logger.fine("Found corresponding node " + printNodeId(target)
						+ " on clone " + printNodeId(cloneNode));
				returnValue.add(new Tools.MindMapNodePair(target, cloneNode));
			}
			return returnValue;
		}

		private void getNewCompoundAction(NodeAction nodeAction,
				Tools.MindMapNodePair correspondingNodePair,
				CompoundAction compound) {
			// deep copy:
			NodeAction copiedNodeAction = (NodeAction) Tools
					.deepCopy(nodeAction);
			// special cases:
			if (copiedNodeAction instanceof MoveNodesAction) {
				MoveNodesAction moveAction = (MoveNodesAction) copiedNodeAction;
				for (int i = 0; i < moveAction.getListNodeListMemberList()
						.size(); i++) {
					NodeListMember member = moveAction.getNodeListMember(i);
					NodeAdapter memberNode = controller.getNodeFromID(member
							.getNode());
					List correspondingMoveNodes = getCorrespondingNodes(
							memberNode, false);
					if (!correspondingMoveNodes.isEmpty()) {
						// search for this clone:
						for (Iterator it = correspondingMoveNodes.iterator(); it
								.hasNext();) {
							Tools.MindMapNodePair pair = (Tools.MindMapNodePair) it
									.next();
							if (pair.getCloneNode() == correspondingNodePair
									.getCloneNode()) {
								// found:
								member.setNode(controller.getNodeID(pair
										.getCorresponding()));
								break;
							}
						}
					}
				}
			}
			if (copiedNodeAction instanceof NewNodeAction) {
				NewNodeAction newNodeAction = (NewNodeAction) copiedNodeAction;
				String newId = mMap.getLinkRegistry().generateUniqueID(null);
				newNodeAction.setNewId(newId);
			}
			copiedNodeAction.setNode(controller.getNodeID(correspondingNodePair
					.getCorresponding()));
			if (copiedNodeAction instanceof PasteNodeAction) {
				/*
				 * difficult thing here: if something is pasted, the paste
				 * action itself contains the node ids of the paste. The first
				 * pasted action will get that node id. This should be the
				 * corresponding node itself. This presumably corrects a bug
				 * that the selection on move actions is changing.
				 */
				compound.addChoice(copiedNodeAction);
			} else {
				compound.addAtChoice(0, copiedNodeAction);
			}
		}

		/**
		 * @param pCloneNode
		 * @return
		 */
		private String printNodeId(MindMapNode pCloneNode) {
			try {
				return controller.getNodeID(pCloneNode) + ": '"
						+ (pCloneNode.getShortText(controller)) + "'";
			} catch (Exception e) {
				return "NOT FOUND: '" + pCloneNode + "'";
			}
		}

		/**
		 * @param pClones
		 * @return
		 */
		private String printNodeIds(HashSet pClones) {
			Vector strings = new Vector();
			for (Iterator it = pClones.iterator(); it.hasNext();) {
				MindMapNode pluginNode = (MindMapNode) it.next();
				strings.add(printNodeId(pluginNode));
			}
			return Tools.listToString(strings);
		}

	}

}
