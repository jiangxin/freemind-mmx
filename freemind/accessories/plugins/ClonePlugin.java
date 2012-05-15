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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import accessories.plugins.ClonePasteAction.Registration;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.MoveNodesAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import freemind.view.mindmapview.NodeView;

public class ClonePlugin extends PermanentMindMapNodeHookAdapter implements
		ActionFilter, NodeSelectionListener, NodeLifetimeListener {

	public static final String PLUGIN_LABEL = "accessories/plugins/ClonePlugin.properties";

	private String mOriginalNodeId;
	/**
	 * This is the master list. {@link ClonePlugin#mCloneNodes mCloneNodes}
	 */
	private HashSet mCloneNodeIds;
	/**
	 * Includes the original node. This is a cached list with the MindMapNodes
	 * belonging to the {@link ClonePlugin#mCloneNodeIds mCloneNodeIds}.
	 */
	private Vector mCloneNodes;

	private boolean mIsDisabled = false;
	private NodeAdapter mOriginalNode;

	private static ImageIcon sCloneIcon;
	private static ImageIcon sOriginalIcon;
	private static Boolean sShowIcon = null;

	public ClonePlugin() {
	}

	public ActionPair filterAction(ActionPair pair) {
		if (isDisabled()) {
			return pair;
		}
		XmlAction doAction = pair.getDoAction();
		doAction = cloneAction(doAction);
		// logger.warning("Result: " + Tools.marshall(doAction).replace(">",
		// ">\n"));
		pair.setDoAction(doAction);
		return pair;
	}

	private XmlAction cloneAction(XmlAction doAction) {
		logger.fine("Found do action: " + doAction.getClass().getName());
		if (doAction instanceof NodeAction) {
			NodeAction nodeAction = (NodeAction) doAction;
			MindMapNode node = getMindMapController().getNodeFromID(
					nodeAction.getNode());
			// check for clone or original?
			doAction = cloneAction(doAction, nodeAction, node);
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

	public void invoke(MindMapNode node) {
		super.invoke(node);
		if (mOriginalNodeId != null) {
			// the plugin has recently be loaded and the nodes have been filled
			// already.
			registerPlugin();
		} else {
			mOriginalNodeId = getMindMapController().getNodeID(node);
			mCloneNodeIds = new HashSet();
		}
	}

	public void addClone(MindMapNode cloneNode) {
		mCloneNodeIds.add(getMindMapController().getNodeID(cloneNode));
		clearCloneCache();
		registerPlugin();
	}

	public void clearCloneCache() {
		mCloneNodes = new Vector();
	}

	private void disablePlugin() {
		// TODO: Abspeichern!
		getMindMapController().getController().errorMessage(
				"This is not possible. Cloning will be disabled.");
		mIsDisabled = true;
	}

	private boolean isDisabled() {
		return mIsDisabled;
	}

	public void save(XMLElement xml) {
		super.save(xml);
		logger.fine("Saved clone plugin");
	}

	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		mOriginalNode = null;
		mCloneNodes = null;
	}

	public void shutdownMapHook() {
		logger.fine("Shutdown of clones");
		deregisterPlugin();
		super.shutdownMapHook();
	}

	private void registerPlugin() {
		if (sCloneIcon == null) {
			sCloneIcon = new ImageIcon(getMindMapController().getResource(
					"images/clone.png"));
		}
		if (sOriginalIcon == null) {
			sOriginalIcon = new ImageIcon(getMindMapController().getResource(
					"images/clone_original.png"));
		}
		if (sShowIcon == null) {
			sShowIcon = Boolean
					.valueOf(Resources.getInstance().getBoolProperty(
							FreeMind.RESOURCES_DON_T_SHOW_CLONE_ICONS));
		}
		/*
		 * test for error cases: - orig is child of clone now - if clone is a
		 * child of clone, this is here not reachable, as the plugin remains
		 * active and is not newly invoked. Hmm, what to do?
		 */
		MindMapNode originalNode = getOriginalNode();
		List/* MindMapNode */cloneNodes = getCloneNodes();
		logger.fine("Invoke shadow class with orig: "
				+ printNodeId(originalNode) + " and clones "
				+ printNodeIds(cloneNodes));
		for (Iterator it = cloneNodes.iterator(); it.hasNext();) {
			MindMapNode cloneNode = (MindMapNode) it.next();
			if (originalNode != null && originalNode.isChildOf(cloneNode)) {
				disablePlugin();
				return;
			}
		}
		getMindMapController().registerNodeSelectionListener(this, false);
		getMindMapController().registerNodeLifetimeListener(this);
		for (Iterator it = cloneNodes.iterator(); it.hasNext();) {
			MindMapNode cloneNode = (MindMapNode) it.next();
			selectShadowNode(cloneNode, true, cloneNode);
		}
		getMindMapController().getActionFactory().registerFilter(this);
		((Registration) getPluginBaseClass()).registerOriginal(mOriginalNodeId);
	}

	private void deregisterPlugin() {
		((Registration) getPluginBaseClass())
				.deregisterOriginal(mOriginalNodeId);
		getMindMapController().getActionFactory().deregisterFilter(this);
		for (Iterator it = getCloneNodes().iterator(); it.hasNext();) {
			MindMapNode cloneNode = (MindMapNode) it.next();
			selectShadowNode(cloneNode, false, cloneNode);
		}
		getMindMapController().deregisterNodeSelectionListener(this);
		getMindMapController().deregisterNodeLifetimeListener(this);
	}

	public void onCreateNodeHook(MindMapNode node) {
		if (isDisabled()) {
			return;
		}
		List cloneNodes = getCloneNodes();
		for (Iterator it = cloneNodes.iterator(); it.hasNext();) {
			MindMapNode clone = (MindMapNode) it.next();
			for (Iterator it2 = cloneNodes.iterator(); it2.hasNext();) {
				MindMapNode clone2 = (MindMapNode) it2.next();
				if (clone != clone2) {
					checkForChainError(clone, node, clone2);
				}
			}
		}
	}

	public void onPreDeleteNode(MindMapNode node) {
	}

	public void onPostDeleteNode(MindMapNode node, MindMapNode parent) {
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

	private void markShadowNode(NodeView node, boolean pEnableShadow) {
		try {
			MindMapNode model = node.getModel();
			List/* pair of MindMapNodePair */shadowNodes = getCorrespondingNodes(
					model, false);
			for (Iterator it = shadowNodes.iterator(); it.hasNext();) {
				Tools.MindMapNodePair shadowNode = (Tools.MindMapNodePair) it
						.next();
				selectShadowNode(shadowNode.getCorresponding(), pEnableShadow,
						shadowNode.getCloneNode());
			}
		} catch (IllegalArgumentException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
	}

	public void onUpdateNodeHook(MindMapNode pNode) {

	}

	public void onSaveNode(MindMapNode pNode) {

	}

	MindMapNode getOriginalNode() {
		try {
			// check for uptodateness:
			if (mOriginalNode != null && mOriginalNode.getParentNode() == null)
				mOriginalNode = null;
			if (mOriginalNode == null)
				mOriginalNode = getMindMapController().getNodeFromID(
						mOriginalNodeId);
		} catch (IllegalArgumentException e) {
			// freemind.main.Resources.getInstance().logException(e);
		}
		return mOriginalNode;
	}

	/**
	 * @return a list of {@link MindMapNode}s including the original node!
	 */
	List/* MindMapNode */getCloneNodes() {
		try {
			// is list up to date?
			if (mCloneNodes != null) {
				for (Iterator it = mCloneNodes.iterator(); it.hasNext();) {
					MindMapNode cloneNode = (MindMapNode) it.next();
					if (cloneNode.getParentNode() == null) {
						clearCloneCache();
					}
				}
			} else {
				clearCloneCache();
			}
			if (mCloneNodes.isEmpty()) {
				mCloneNodes.add(getOriginalNode());
				for (Iterator it = mCloneNodeIds.iterator(); it.hasNext();) {
					String cloneId = (String) it.next();
					mCloneNodes.add(getMindMapController().getNodeFromID(
							cloneId));
				}
			}
		} catch (IllegalArgumentException e) {
			// freemind.main.Resources.getInstance().logException(e);
		}
		return mCloneNodes;
	}

	/**
	 * This is the main method here. It returns to a given node its cloned nodes
	 * on the other side.
	 * 
	 * @param pNode
	 *            is checked to be son of one of the clones/original.
	 * @return a list of MindMapNodePair s where the first is the corresponding
	 *         node and the second is the clone. If the return value is empty,
	 *         the node isn't son of any.
	 */
	private List/* MindMapNodePair */getCorrespondingNodes(MindMapNode pNode,
			boolean includeNodeItself) {
		Vector returnValue = new Vector();
		// build list of indices up to a clone/original is found.
		Vector indexVector = new Vector();
		MindMapNode child = pNode;
		List cloneNodes = getCloneNodes();
		logger.fine("Searching for corresponding for " + printNodeId(pNode)
				+ " in " + printNodeIds(cloneNodes));
		/*
		 * FIXME: Design flaw here: the index based correspondence is more than
		 * week. Imagine moving nodes up/down or inserting nodes with many
		 * children. One the clones, the index way may leed into an asylum....
		 */
		while (!cloneNodes.contains(child)) {
			if (child.isRoot()) {
				// nothing found!
				return returnValue;
			}
			indexVector.add(0, new Integer(child.getParentNode()
					.getChildPosition(child)));
			child = child.getParentNode();
		}
		MindMapNode originalNode = child;
		List/* MindMapNode */targets = cloneNodes;
		CloneLoop: for (Iterator itClone = targets.iterator(); itClone
				.hasNext();) {
			MindMapNode target = (MindMapNode) itClone.next();
			MindMapNode cloneNode = target;
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

	/**
	 * @param pCloneNode
	 * @return
	 */
	private String printNodeId(MindMapNode pCloneNode) {
		try {
			return getMindMapController().getNodeID(pCloneNode) + ": '"
					+ (pCloneNode.getShortText(getMindMapController())) + "'";
		} catch (Exception e) {
			return "NOT FOUND: '" + pCloneNode + "'";
		}
	}

	/**
	 * @param pTargets
	 * @return
	 */
	private String printNodeIds(List pTargets) {
		Vector strings = new Vector();
		for (Iterator it = pTargets.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			strings.add(printNodeId(node));
		}
		return "" + strings;
	}

	private XmlAction cloneAction(XmlAction doAction, NodeAction nodeAction,
			MindMapNode node) {
		if (nodeAction instanceof CutNodeAction) {
			for (Iterator it = getCloneNodes().iterator(); it.hasNext();) {
				MindMapNode clone = (MindMapNode) it.next();
				if (clone.isChildOfOrEqual(node)) {
					// the complete node is cut.
					logger.fine("Node " + printNodeId(clone) + " is cut.");
					return doAction;
				}
			}
		}
		// create new action:
		CompoundAction compound = new CompoundAction();
		compound.addChoice(nodeAction);
		List/* MindMapNodePair */correspondingNodes = getCorrespondingNodes(
				node, false);
		for (Iterator it = correspondingNodes.iterator(); it.hasNext();) {
			Tools.MindMapNodePair pair = (Tools.MindMapNodePair) it.next();
			getNewCompoundAction(nodeAction, pair, compound);
		}
		return compound;
	}

	private void getNewCompoundAction(NodeAction nodeAction,
			Tools.MindMapNodePair correspondingNodePair, CompoundAction compound) {
		// deep copy:
		NodeAction copiedNodeAction = (NodeAction) getMindMapController()
				.unMarshall(getMindMapController().marshall(nodeAction));
		// special cases:
		if (copiedNodeAction instanceof MoveNodesAction) {
			MoveNodesAction moveAction = (MoveNodesAction) copiedNodeAction;
			for (int i = 0; i < moveAction.getListNodeListMemberList().size(); i++) {
				NodeListMember member = moveAction.getNodeListMember(i);
				NodeAdapter memberNode = getMindMapController().getNodeFromID(
						member.getNode());
				List correspondingMoveNodes = getCorrespondingNodes(memberNode,
						false);
				if (!correspondingMoveNodes.isEmpty()) {
					// search for this clone:
					for (Iterator it = correspondingMoveNodes.iterator(); it
							.hasNext();) {
						Tools.MindMapNodePair pair = (Tools.MindMapNodePair) it
								.next();
						if (pair.getCloneNode() == correspondingNodePair
								.getCloneNode()) {
							// found:
							member.setNode(getMindMapController().getNodeID(
									pair.getCorresponding()));
							break;
						}
					}
				}
			}
		}
		if (copiedNodeAction instanceof NewNodeAction) {
			NewNodeAction newNodeAction = (NewNodeAction) copiedNodeAction;
			String newId = getMap().getLinkRegistry().generateUniqueID(null);
			newNodeAction.setNewId(newId);
		}
		copiedNodeAction.setNode(getMindMapController().getNodeID(
				correspondingNodePair.getCorresponding()));
		if (copiedNodeAction instanceof PasteNodeAction) {
			/*
			 * difficult thing here: if something is pasted, the paste action
			 * itself contains the node ids of the paste. The first pasted
			 * action will get that node id. This should be the corresponding
			 * node itself. This presumably corrects a bug that the selection on
			 * move actions is changing.
			 */
			compound.addChoice(copiedNodeAction);
		} else {
			compound.addAtChoice(0, copiedNodeAction);
		}
	}

	private void selectShadowNode(MindMapNode node, boolean pEnableShadow,
			MindMapNode pCloneNode) {
		if (!sShowIcon.booleanValue()) {
			return;
		}
		while (node != null) {
			ImageIcon i = pEnableShadow ? sCloneIcon : null;
			if (node == pCloneNode) {
				i = sOriginalIcon;
			}
			node.setStateIcon(getName(), i);
			getMindMapController().nodeRefresh(node);
			if (node == pCloneNode)
				break;
			node = node.getParentNode();
			// comment this out to get a complete marked path to the root of the
			// clones.
			break;
		}
	}

	private void checkForChainError(MindMapNode originalNode, MindMapNode node,
			MindMapNode cloneNode) {
		if (cloneNode.isChildOfOrEqual(node)
				&& node.isChildOfOrEqual(originalNode)) {
			// orig -> .... -> node -> .. -> clone
			disablePlugin();
		}
	}

	public void removeClone(MindMapNode pCloneNode) {
		mCloneNodeIds.remove(getMindMapController().getNodeID(pCloneNode));
		clearCloneCache();
		registerPlugin();
		if(mCloneNodeIds.isEmpty()) {
			// remove icon
			getNode().setStateIcon(getName(), null);
			getMindMapController().nodeRefresh(getNode());
			// TODO: remove myself
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController.NodeSelectionListener#onSelectionChange
	 * (freemind.modes.MindMapNode, boolean)
	 */
	public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
		// TODO Auto-generated method stub

	}

}
