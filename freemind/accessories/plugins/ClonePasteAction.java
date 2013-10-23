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
import java.util.Properties;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import freemind.common.OptionalDontShowMeAgainDialog;
import freemind.controller.MenuItemEnabledListener;
import freemind.controller.MindMapNodesSelection;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.HookNodeAction;
import freemind.controller.actions.generated.instance.MoveNodeXmlAction;
import freemind.controller.actions.generated.instance.MoveNodesAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.UndoPasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.Tools.MindMapNodePair;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;
import freemind.view.mindmapview.NodeView;

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
		logger.fine("Clones for nodes: " + Tools.listToString(mindMapNodes));
		// now, construct the plugin for those nodes:
		for (Iterator itPastedNodes = mindMapNodes.iterator(); itPastedNodes
				.hasNext();) {
			MindMapNode copiedNode = (MindMapNode) itPastedNodes.next();
			ClonePlugin clonePlugin = ClonePlugin.getHook(copiedNode);
			// first the clone master
			if (clonePlugin == null) {
				int showResult = new OptionalDontShowMeAgainDialog(
						getMindMapController().getFrame().getJFrame(),
						getMindMapController().getSelectedView(),
						"choose_clone_type",
						"clone_type_question",
						getMindMapController(),
						new OptionalDontShowMeAgainDialog.StandardPropertyHandler(
								getMindMapController().getController(),
								FreeMind.RESOURCES_COMPLETE_CLONING),
						OptionalDontShowMeAgainDialog.BOTH_OK_AND_CANCEL_OPTIONS_ARE_STORED)
						.show().getResult();
				Properties properties = new Properties();
				properties
						.setProperty(
								ClonePlugin.XML_STORAGE_CLONE_ITSELF,
								showResult == JOptionPane.OK_OPTION ? ClonePlugin.CLONE_ITSELF_TRUE
										: ClonePlugin.CLONE_ITSELF_FALSE);
				Vector selecteds = Tools.getVectorWithSingleElement(copiedNode);
				getMindMapController().addHook(copiedNode, selecteds,
						ClonePlugin.PLUGIN_LABEL, properties);
			}
			// finally, we construct a new one:
			Transferable copy = getMindMapController().copy(copiedNode, true);
			addNewClone(copiedNode, pNode, copy);
		}
	}

	public void addNewClone(MindMapNode originalNode,
			MindMapNode pDestinationNode, Transferable copy) {
		String originalNodeId = getMindMapController().getNodeID(originalNode);
		logger.fine("Original node " + originalNode + ", id " + originalNodeId);
		if (originalNode.isRoot()) {
			getMindMapController().getController().errorMessage(
					getMindMapController().getText(
							"clone_plugin_no_root_cloning"));
			return;
		}
		// insert clone:
		List listOfChilds = pDestinationNode.getChildren();
		Vector listOfChildIds = new Vector();
		for (Iterator it = listOfChilds.iterator(); it.hasNext();) {
			String nodeID = getMindMapController().getNodeID(
					(MindMapNode) it.next());
			listOfChildIds.add(nodeID);
			logger.fine("Old child id:" + nodeID);
		}
		getMindMapController().paste(copy, pDestinationNode);
	}

	public Vector getMindMapNodes() {
		return getRegistration().getMindMapNodes();
	}

	protected Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

	public interface ClonePropertiesObserver {
		void propertiesChanged(CloneProperties pCloneProperties);
	}

	public static class CloneProperties {
		boolean mCloneItself = false;
		private HashSet mObserverSet = new HashSet();
		protected static java.util.logging.Logger logger = null;

		/**
		 * 
		 */
		public CloneProperties() {
			if (logger == null) {
				logger = freemind.main.Resources.getInstance().getLogger(
						this.getClass().getName());
			}
		}
		
		public boolean isCloneItself() {
			return mCloneItself;
		}

		public void setCloneItself(boolean pCloneItself) {
			logger.finest("Setting mCloneItself to " + pCloneItself);
			boolean fire = false;
			if (pCloneItself != mCloneItself) {
				fire = true;
			}
			mCloneItself = pCloneItself;
			if (fire) {
				firePropertiesChanged();
			}
		}

		public void registerObserver(ClonePropertiesObserver pObserver) {
			mObserverSet.add(pObserver);
		}

		public void deregisterObserver(ClonePropertiesObserver pObserver) {
			mObserverSet.remove(pObserver);
		}

		private void firePropertiesChanged() {
			for (Iterator it = mObserverSet.iterator(); it.hasNext();) {
				ClonePropertiesObserver observer = (ClonePropertiesObserver) it
						.next();
				observer.propertiesChanged(this);
			}
		}

	}

	public static class Registration implements HookRegistration,
			MenuItemEnabledListener, ActionFilter, NodeSelectionListener {

		private static final String PLUGIN_NAME = "accessories/plugins/ClonePasteAction.properties";
		private static ImageIcon sCloneIcon;
		private static ImageIcon sOriginalIcon;
		private static boolean sShowIcon = true;

		/**
		 * Mapping of clone id (String) to a HashSet of {@link MindMapNode}s
		 */
		private HashMap mCloneIdsMap = new HashMap();
		/**
		 * This is the reverse of mCloneIdsMap: {@link MindMapNode} to cloneId.
		 */
		private HashMap mClonesMap = new HashMap();

		/**
		 * This is a storage cloneId to clone properties.
		 */
		private HashMap mClonePropertiesMap = new HashMap();

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;
		private Vector mLastMarkedNodeViews = new Vector();

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		public void register() {
			if (sCloneIcon == null) {
				sCloneIcon = new ImageIcon(
						controller.getResource("images/clone.png"));
				sOriginalIcon = new ImageIcon(
						controller.getResource("images/clone_original.png"));
				sShowIcon = Resources.getInstance().getBoolProperty(
						FreeMind.RESOURCES_DON_T_SHOW_CLONE_ICONS);
			}
			controller.getActionFactory().registerFilter(this);
			controller.registerNodeSelectionListener(this, false);
		}

		public void deRegister() {
			controller.deregisterNodeSelectionListener(this);
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
			selectShadowNode(node, true, node);
			if (!mClonePropertiesMap.containsKey(pCloneId)) {
				mClonePropertiesMap.put(pCloneId, new CloneProperties());
			}
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
				mClonePropertiesMap.remove(pCloneId);
			}
		}

		public CloneProperties getCloneProperties(String pCloneId) {
			if (mClonePropertiesMap.containsKey(pCloneId)) {
				return (CloneProperties) mClonePropertiesMap.get(pCloneId);
			}
			throw new IllegalArgumentException(
					"Clone properties not found for " + pCloneId);
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
			List correspondingNodes = getCorrespondingNodes(nodeAction, node);
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
					changeNodeListMember(correspondingNodePair, moveAction,
							member);
				}
			}
			if (copiedNodeAction instanceof HookNodeAction) {
				HookNodeAction hookAction = (HookNodeAction) copiedNodeAction;
				for (int i = 0; i < hookAction.getListNodeListMemberList()
						.size(); i++) {
					NodeListMember member = hookAction.getNodeListMember(i);
					changeNodeListMember(correspondingNodePair, hookAction,
							member);
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

		public void changeNodeListMember(
				Tools.MindMapNodePair correspondingNodePair,
				NodeAction pAction, NodeListMember member) {
			NodeAdapter memberNode = controller.getNodeFromID(member.getNode());
			List correspondingMoveNodes = getCorrespondingNodes(pAction,
					memberNode);
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

		/**
		 * Method takes into account, that some actions are different.
		 * 
		 * @param nodeAction
		 * @param node
		 * @return
		 */
		public List getCorrespondingNodes(NodeAction nodeAction,
				MindMapNode node) {
			boolean startWithParent = false;
			// Behavior for complete cloning.
			if (mClonesMap.containsKey(node)) {
				String cloneId = (String) mClonesMap.get(node);
				if (getCloneProperties(cloneId).isCloneItself()) {
					// Behavior for complete cloning
					if (nodeAction instanceof MoveNodesAction
							|| nodeAction instanceof MoveNodeXmlAction
							|| nodeAction instanceof DeleteNodeAction
							|| nodeAction instanceof CutNodeAction) {
						// ok, there is an action for a clone itself. be
						// careful:
						// clone only, if parents are clones:
						startWithParent = true;
					} else if (nodeAction instanceof PasteNodeAction) {
						PasteNodeAction pna = (PasteNodeAction) nodeAction;
						if (pna.getAsSibling()) {
							// sibling means, that the paste goes below the
							// clone.
							// skip.
							startWithParent = true;
						} else {
							// here, the action changes the children, thus, they
							// are
							// subject to cloning.
						}
					} else if (nodeAction instanceof UndoPasteNodeAction) {
						UndoPasteNodeAction pna = (UndoPasteNodeAction) nodeAction;
						if (pna.getAsSibling()) {
							// sibling means, that the paste goes below the
							// clone.
							// skip.
							startWithParent = true;
						} else {
							// here, the action changes the children, thus, they
							// are
							// subject to cloning.
						}
					}
				} else {
					// Behavior for children cloning only
					/*
					 * new node action belongs to the children, so clone it,
					 * even, when node is the clone itself.
					 */
					if (nodeAction instanceof NewNodeAction) {
						// here, the action changes the children, thus, they are
						// subject to cloning.
					} else if (nodeAction instanceof PasteNodeAction) {
						PasteNodeAction pna = (PasteNodeAction) nodeAction;
						if (pna.getAsSibling()) {
							// sibling means, that the paste goes below the
							// clone.
							// skip.
							startWithParent = true;
						} else {
							// here, the action changes the children, thus, they
							// are
							// subject to cloning.
						}
					} else if (nodeAction instanceof UndoPasteNodeAction) {
						UndoPasteNodeAction pna = (UndoPasteNodeAction) nodeAction;
						if (pna.getAsSibling()) {
							// sibling means, that the paste goes below the
							// clone.
							// skip.
							startWithParent = true;
						} else {
							// here, the action changes the children, thus, they
							// are
							// subject to cloning.
						}
					} else {
						// ok, there is an action for a clone itself. be
						// careful:
						// clone only, if parents are clones:
						startWithParent = true;
					}
				}
			}
			List/* MindMapNodePair */correspondingNodes = getCorrespondingNodes(
					node, startWithParent);
			return correspondingNodes;
		}

		/**
		 * This is the main method here. It returns to a given node its cloned
		 * nodes on the other side.
		 * 
		 * @param pNode
		 *            is checked to be son of one of the clones/original.
		 * @param pStartWithParent
		 *            Sometimes, it is relevant, if only one of the parents is a
		 *            clone, eg. for all actions, that affect the clone itself,
		 *            thus not need to be cloned, but perhaps the clone is
		 *            itself a node inside of another clone!
		 * @return a list of {@link MindMapNodePair}s where the first is the
		 *         corresponding node and the second is the clone. If the return
		 *         value is empty, the node isn't son of any.
		 */
		public List getCorrespondingNodes(MindMapNode pNode,
				boolean pStartWithParent) {
			// in case, no clones are present, this method returns very fast.
			if (mClonesMap.isEmpty()) {
				return Collections.EMPTY_LIST;
			}
			MindMapNode clone;
			{
				MindMapNode child;
				// code doubling to speed up. First check for a clone on the way
				// to root.
				if (pStartWithParent) {
					child = pNode.getParentNode();
				} else {
					child = pNode;
				}
				while (!mClonesMap.containsKey(child)) {
					if (child.isRoot()) {
						// nothing found!
						return Collections.EMPTY_LIST;
					}
					child = child.getParentNode();
				}
				clone = child;
			}
			MindMapNode child;
			// now, there is a clone on the way. Collect the indices.
			Vector indexVector = new Vector();
			if (pStartWithParent) {
				addNodePosition(indexVector, pNode);
				child = pNode.getParentNode();
			} else {
				child = pNode;
			}
			while (clone != child) {
				addNodePosition(indexVector, child);
				child = child.getParentNode();
			}
			Vector returnValue = new Vector();
			MindMapNode originalNode = child;
			HashSet targets = (HashSet) mCloneIdsMap.get(mClonesMap.get(child));
			CloneLoop: for (Iterator itClone = targets.iterator(); itClone
					.hasNext();) {
				MindMapNode cloneNode = (MindMapNode) itClone.next();
				MindMapNode target = cloneNode;
				if (cloneNode == originalNode)
					continue;
				for (int i = indexVector.size() - 1; i >= 0; --i) {
					int index = ((Integer) indexVector.get(i)).intValue();
					if (target.getChildCount() <= index) {
						logger.warning("Index " + index
								+ " in other tree not found from "
								+ printNodeIds(targets) + " originating from "
								+ printNodeId(cloneNode) + " start at parent "
								+ pStartWithParent);
						// with crossed fingers.
						continue CloneLoop;
					}
					target = (MindMapNode) target.getChildAt(index);
				}
				// logger.fine("Found corresponding node " + printNodeId(target)
				// + " on clone " + printNodeId(cloneNode));
				returnValue.add(new Tools.MindMapNodePair(target, cloneNode));
			}
			return returnValue;
		}

		private void addNodePosition(Vector indexVector, MindMapNode child) {
			indexVector.add(new Integer(child.getParentNode().getChildPosition(
					child)));
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

		/**
		 * Is sent when a node is selected.
		 */
		public void onFocusNode(NodeView node) {
			markShadowNode(node, true);
		}

		/**
		 * Is sent when a node is deselected.
		 */
		public void onLostFocusNode(NodeView node) {
			markShadowNode(node, false);
		}

		public void markShadowNode(NodeView pNode, boolean pEnableShadow) {
			// at startup, the node is null.
			if (pNode == null || pNode.getModel() == null) {
				return;
			}
			if (!pEnableShadow) {
				if (!mLastMarkedNodeViews.isEmpty()) {
					for (Iterator it = mLastMarkedNodeViews.iterator(); it
							.hasNext();) {
						MindMapNode node = (MindMapNode) it.next();
						if (mClonesMap.containsKey(node)) {
							setIcon(node, sOriginalIcon);
						} else {
							setIcon(node, null);
						}
					}
					mLastMarkedNodeViews.clear();
				}
			} else {
				markShadowNode(pNode.getModel(), pEnableShadow);
			}
		}

		public void markShadowNode(MindMapNode model, boolean pEnableShadow) {
			mLastMarkedNodeViews.clear();
			try {
				List/* pair of MindMapNodePair */shadowNodes = getCorrespondingNodes(
						model, false);
				for (Iterator it = shadowNodes.iterator(); it.hasNext();) {
					Tools.MindMapNodePair shadowNode = (Tools.MindMapNodePair) it
							.next();
					MindMapNode correspondingNode = shadowNode
							.getCorresponding();
					mLastMarkedNodeViews.add(correspondingNode);
					selectShadowNode(correspondingNode, pEnableShadow,
							shadowNode.getCloneNode());
				}
			} catch (IllegalArgumentException e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}

		private void selectShadowNode(MindMapNode node, boolean pEnableShadow,
				MindMapNode pCloneNode) {
			if (!sShowIcon) {
				return;
			}
			while (node != null) {
				ImageIcon i = pEnableShadow ? sCloneIcon : null;
				if (node == pCloneNode) {
					i = sOriginalIcon;
				}
				setIcon(node, i);
				if (node == pCloneNode)
					break;
				node = node.getParentNode();
				// comment this out to get a complete marked path to the root of
				// the
				// clones.
				break;
			}
		}

		public void setIcon(MindMapNode node, ImageIcon i) {
			node.setStateIcon(ClonePlugin.PLUGIN_LABEL, i);
			controller.nodeRefresh(node);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.ModeController.NodeSelectionListener#onSelectionChange
		 * (freemind.modes.MindMapNode, boolean)
		 */
		public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
		}

		public void onUpdateNodeHook(MindMapNode pNode) {

		}

		public void onSaveNode(MindMapNode pNode) {

		}

	}

}
