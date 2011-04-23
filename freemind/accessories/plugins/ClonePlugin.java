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

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.MoveNodesAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import freemind.view.mindmapview.NodeView;

public class ClonePlugin extends PermanentMindMapNodeHookAdapter implements ActionFilter {

	private static final String XML_STORAGE_ORIGINAL = "ORIGINAL_ID";
	private static final String XML_STORAGE_CLONE = "CLONE_ID";
	String mOriginalNodeId;
	String mCloneNodeId;
	private boolean mIsDisabled = false;
	private ImageIcon sCloneIcon;
	private static Boolean sShowIcon = null;
	private CloneShadowClass mShadowClass1;
	private CloneShadowClass mShadowClass2;

	public ClonePlugin() {
	}

	public ActionPair filterAction(ActionPair pair) {
		if (isDisabled()) {
			return pair;
		}
		XmlAction doAction = pair.getDoAction();
		doAction = cloneAction(doAction);
		pair.setDoAction(doAction);
		return pair;
	}

	private XmlAction cloneAction(XmlAction doAction) {
		logger.info("Found do action: " + doAction.getClass().getName());
		if (doAction instanceof NodeAction) {
			NodeAction nodeAction = (NodeAction) doAction;
			MindMapNode node = getMindMapController().getNodeFromID(
					nodeAction.getNode());
			// check for clone or original?
			doAction = mShadowClass1.cloneAction(doAction, nodeAction, node);
			doAction = mShadowClass2.cloneAction(doAction, nodeAction, node);
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
			// the plugin has recently be loaded and the nodes have been filled already.
		} else {
			MindMapNode originalNode = getMindMapController().getSelected();
			mOriginalNodeId = getMindMapController().getNodeID(originalNode);
			logger.info("Original node " + originalNode + ", id " + mOriginalNodeId);
			if (originalNode.isRoot()) {
				throw new IllegalArgumentException("Root can't be cloned");
			}
			// insert clone:
			Transferable copy = getMindMapController().copy(originalNode, true);
			MindMapNode parent = originalNode.getParentNode();
			List listOfChilds = parent.getChildren();
			Vector listOfChildIds = new Vector();
			for (Iterator it = listOfChilds.iterator(); it.hasNext();) {
				String nodeID = getMindMapController().getNodeID(
						(MindMapNode) it.next());
				listOfChildIds.add(nodeID);
				logger.info("Old child id:" + nodeID);
			}
			getMindMapController().paste(copy, parent);
			// how to get the clone node now?
			for (Iterator it = parent.getChildren().iterator(); it.hasNext();) {
				MindMapNode child = (MindMapNode) it.next();
				String childId = getMindMapController().getNodeID(child);
				logger.info("Checking " + child + " to be the new node, id "
						+ childId);
				if (!listOfChildIds.contains(childId)) {
					// clone found:
					mCloneNodeId = childId;
					break;
				}
			}
			if (mCloneNodeId == null) {
				throw new IllegalArgumentException("Clone node not found.");
			}
		}
		registerPlugin();
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
		HashMap values = new HashMap();
		values.put(XML_STORAGE_ORIGINAL, mOriginalNodeId);
		values.put(XML_STORAGE_CLONE, mCloneNodeId);
		saveNameValuePairs(values, xml);
		logger.info("Saved clone plugin");
	}

	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		HashMap values = loadNameValuePairs(child);
		mOriginalNodeId = (String) values.get(XML_STORAGE_ORIGINAL);
		mCloneNodeId = (String) values.get(XML_STORAGE_CLONE);
	}

	public void shutdownMapHook() {
		logger.info("Shutdown of clones");
		deregisterPlugin();
		super.shutdownMapHook();
	}

	private void registerPlugin() {
		if (sCloneIcon == null) {
			sCloneIcon = new ImageIcon(getMindMapController().getResource(
			"images/clone.png"));
		}
		if (sShowIcon == null) {
			sShowIcon = Boolean
			.valueOf(Resources.getInstance().getBoolProperty(
					FreeMind.RESOURCES_DON_T_SHOW_CLONE_ICONS));
		}
		mShadowClass1 = new CloneShadowClass(mOriginalNodeId, mCloneNodeId).register();
		mShadowClass2 = new CloneShadowClass(mCloneNodeId, mOriginalNodeId).register();
		getMindMapController().getActionFactory().registerFilter(this);

	}

	private void deregisterPlugin() {
		getMindMapController().getActionFactory().deregisterFilter(this);
		mShadowClass1.deregister();
		mShadowClass2.deregister();
		mShadowClass1 = null;
		mShadowClass2 = null;
	}


		
	private class CloneShadowClass implements NodeSelectionListener, NodeLifetimeListener {
		private final String mOriginalNodeId;
		private final String mCloneNodeId;
		private MindMapNode mOriginalNode;
		private MindMapNode mCloneNode;

		public CloneShadowClass(String pOriginalNodeId, String pCloneNodeId) {
			mOriginalNodeId = pOriginalNodeId;
			mCloneNodeId = pCloneNodeId;
		}

		public void deregister() {
//			selectShadowNode(getOriginalNode(), false);
			getMindMapController().deregisterNodeSelectionListener(this);
			getMindMapController().deregisterNodeLifetimeListener(this);
		}

		public CloneShadowClass register() {
			/*
			 * test for error cases: - orig is child of clone now - if clone is
			 * a child of clone, this is here not reachable, as the plugin
			 * remains active and is not newly invoked. Hmm, what to do?
			 */
			MindMapNode originalNode = getOriginalNode();
			MindMapNode cloneNode = getCloneNode();
			logger.info("Invoke shadow class with orig: " + originalNode
					+ " and clone " + cloneNode);
			if (originalNode != null && originalNode.isChildOf(cloneNode)) {
				disablePlugin();
				return this;
			}
			getMindMapController().registerNodeSelectionListener(this);
			getMindMapController().registerNodeLifetimeListener(this);
//			selectShadowNode(originalNode, true);
			return this;
		}

		public void onCreateNodeHook(MindMapNode node) {
			if (isDisabled()) {
				return;
			}
			checkForChainError(getOriginalNode(), node, getCloneNode());
		}
		
		public void onPreDeleteNode(MindMapNode node) {
		}
		
		public void onPostDeleteNode(MindMapNode node, MindMapNode parent) {
		}

		/**
		 * Is sent when a node is selected.
		 */
		public void onSelectHook(NodeView node) {
			markShadowNode(node, true);
		}

		/**
		 * Is sent when a node is deselected.
		 */
		public void onDeselectHook(NodeView node) {
			markShadowNode(node, false);
		}
		
		private void markShadowNode(NodeView node, boolean pEnableShadow) {
			MindMapNode model = node.getModel();
			if (model.isChildOfOrEqual(getOriginalNode())) {
				MindMapNode shadowNode = getCorrespondingNode(model);
				selectShadowNode(shadowNode, pEnableShadow);
			}
		}
		
		public void onUpdateNodeHook(MindMapNode pNode) {
			
		}
		
		public void onSaveNode(MindMapNode pNode) {
			
		}

		MindMapNode getOriginalNode() {
			try {
				// check for uptodateness:
				if(mOriginalNode != null && mOriginalNode.getParentNode() == null) 
					mOriginalNode = null;
				if(mOriginalNode==null)
					mOriginalNode = getMindMapController().getNodeFromID(mOriginalNodeId);
			} catch (IllegalArgumentException e) {
				// freemind.main.Resources.getInstance().logException(e);
			}
			return mOriginalNode;
		}

		MindMapNode getCloneNode() {
			try {
				// check for uptodateness:
				if(mCloneNode != null && mCloneNode.getParentNode() == null) 
					mCloneNode = null;
				if(mCloneNode==null)
					mCloneNode = getMindMapController().getNodeFromID(mCloneNodeId);
			} catch (IllegalArgumentException e) {
				// freemind.main.Resources.getInstance().logException(e);
			}
			return mCloneNode;
		}
		
		/**
		 * This is the main method here. It returns to a given node its
		 * cloned node on the other side.
		 * @param pNode
		 * @return 
		 */
		private MindMapNode getCorrespondingNode(MindMapNode pNode) {
			Vector indexVector = new Vector();
			MindMapNode child = pNode;
			while (child != getOriginalNode()) {
				indexVector.add(0, new Integer(child.getParentNode()
						.getChildPosition(child)));
				child = child.getParentNode();
			}
			MindMapNode target = getCloneNode();
			for (Iterator it = indexVector.iterator(); it.hasNext();) {
				int index = ((Integer) it.next()).intValue();
				if (target.getChildCount() <= index) {
					throw new IllegalArgumentException("Index " + index
							+ " in other tree not found from " + target
							+ " originating from " + getCloneNode());
				}
				target = (MindMapNode) target.getChildAt(index);
			}
			return target;
		}

		private XmlAction cloneAction(XmlAction doAction, NodeAction nodeAction, MindMapNode node) {
			if (nodeAction instanceof CutNodeAction) {
				if (getOriginalNode().isChildOfOrEqual(node)) {
					// the complete original is cut.
					logger.warning("Node " + getOriginalNode() + " is cut.");
					return doAction;
				}
			}
			if (node.isChildOfOrEqual(getOriginalNode())) {
				MindMapNode correspondingNode = getCorrespondingNode(node);
				doAction = getNewCompoundAction(nodeAction, correspondingNode);
			}
			return doAction;
		}

		private XmlAction getNewCompoundAction(NodeAction nodeAction,
				MindMapNode correspondingNode) {
			CompoundAction compound = new CompoundAction();
			compound.addChoice(nodeAction);
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
					if (memberNode.isChildOfOrEqual(getOriginalNode())) {
						MindMapNode correspondingNode2 = getCorrespondingNode(
								memberNode);
						member.setNode(getMindMapController().getNodeID(
								correspondingNode2));
					}
				}
			}
			if (copiedNodeAction instanceof NewNodeAction) {
				NewNodeAction newNodeAction = (NewNodeAction) copiedNodeAction;
				String newId = getMap().getLinkRegistry().generateUniqueID(null);
				newNodeAction.setNewId(newId);
			}
			copiedNodeAction.setNode(getMindMapController().getNodeID(
					correspondingNode));
			compound.addAtChoice(0, copiedNodeAction);
			return compound;
		}

		private void selectShadowNode(MindMapNode node, boolean pEnableShadow) {
			if(!sShowIcon.booleanValue()){
				return;
			}
			while (node != null) {
//				node.setBackgroundColor(pEnableShadow?Color.YELLOW:Color.WHITE);
				ImageIcon i = pEnableShadow?sCloneIcon:null;
				node.setStateIcon(getName(), i);
				getMindMapController().nodeRefresh(node);
				if(node == getCloneNode())
					break;
				node = node.getParentNode();
			}
		}

		private void checkForChainError(MindMapNode originalNode, MindMapNode node, MindMapNode cloneNode) {
			if (cloneNode.isChildOfOrEqual(node)
					&& node.isChildOfOrEqual(originalNode)) {
				// orig -> .... -> node -> .. -> clone
				disablePlugin();
			}
		}

	}
}
