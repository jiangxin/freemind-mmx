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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import accessories.plugins.time.TimeManagement;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.CutNodeAction;
import freemind.controller.actions.generated.instance.MoveNodesAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.NodeListMember;
import freemind.controller.actions.generated.instance.PasteNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

public class ClonePlugin extends PermanentMindMapNodeHookAdapter implements
		ActionFilter, NodeLifetimeListener {

	private static final String XML_STORAGE_ORIGINAL = "ORIGINAL_ID";
	private static final String XML_STORAGE_CLONE = "CLONE_ID";
	String mOriginalNodeId;
	String mCloneNodeId;
	private boolean mIsDisabled = false;

	public ClonePlugin() {
	}

	public ActionPair filterAction(ActionPair pair) {
		if(isDisabled()) {
			return pair;
		}
		MindMapNode originalNode = getOriginalNode();
		if(originalNode==null) {
			logger.info("Original node is (currently?) not available (presumably pasting).");
			return pair;
		}
		MindMapNode cloneNode = getCloneNode();
		if(cloneNode==null) {
			logger.info("Clone node is (currently?) not available.");
			return pair;
		}
		XmlAction doAction = pair.getDoAction();
		doAction = cloneAction(doAction, originalNode, cloneNode);
		pair.setDoAction(doAction);
		return pair;
	}

	private XmlAction cloneAction(XmlAction doAction, MindMapNode originalNode, MindMapNode cloneNode) {
		logger.info("Found do action: " + doAction.getClass().getName());
		if (doAction instanceof NodeAction) {
			NodeAction nodeAction = (NodeAction) doAction;
			MindMapNode node = getMindMapController().getNodeFromID(
					nodeAction.getNode());
			if (nodeAction instanceof CutNodeAction) {
				if (originalNode.isChildOfOrEqual(node)) {
					// the complete original is cut.
					logger.warning("Removing complete plugin.");
					return doAction;
				}
				if (cloneNode.isChildOfOrEqual(node)) {
					// the complete clone is cut.
					logger.warning("Clone is removed.");
					return doAction;
				}
			}
			// check for clone or original?
			if (node.isChildOfOrEqual(originalNode)) {
				MindMapNode correspondingNode = getCorrespondingNode(node,
						originalNode, cloneNode);
				doAction = getNewCompoundAction(nodeAction, correspondingNode,
						originalNode, cloneNode);
			}
			if (node.isChildOfOrEqual(cloneNode)) {
				MindMapNode correspondingNode = getCorrespondingNode(node,
						cloneNode, originalNode);
				doAction = getNewCompoundAction(nodeAction, correspondingNode,
						cloneNode, originalNode);
			}
		} else {
			if (doAction instanceof CompoundAction) {
				CompoundAction compoundAction = (CompoundAction) doAction;
				List choiceList = compoundAction.getListChoiceList();
				int index = 0;
				for (Iterator it = choiceList.iterator(); it.hasNext();) {
					XmlAction subAction = (XmlAction) it.next();
					subAction = cloneAction(subAction, originalNode, cloneNode);
					compoundAction.setAtChoice(index, subAction);
					index++;
				}
			}
		}
		return doAction;
	}

	private XmlAction getNewCompoundAction(NodeAction nodeAction,
			MindMapNode correspondingNode, MindMapNode originalNode,
			MindMapNode cloneNode) {
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
				if (memberNode.isChildOfOrEqual(originalNode)) {
					MindMapNode correspondingNode2 = getCorrespondingNode(
							memberNode, originalNode, cloneNode);
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

	private MindMapNode getCorrespondingNode(MindMapNode pNode,
			MindMapNode pOriginalNode, MindMapNode pCloneNode) {
		Vector indexVector = new Vector();
		MindMapNode child = pNode;
		while (child != pOriginalNode) {
			indexVector.add(0, new Integer(child.getParentNode()
					.getChildPosition(child)));
			child = child.getParentNode();
		}
		MindMapNode target = pCloneNode;
		for (Iterator it = indexVector.iterator(); it.hasNext();) {
			int index = ((Integer) it.next()).intValue();
			if (target.getChildCount() <= index) {
				throw new IllegalArgumentException("Index " + index
						+ " in other tree not found from " + target
						+ " originating from " + pCloneNode);
			}
			target = (MindMapNode) target.getChildAt(index);
		}
		return target;
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		if (mOriginalNodeId != null) {
			// the plugin has recently be loaded and the nodes have been filled.
			/* test for error cases:
			 * - orig is child of clone now
			 * - if clone is a child of clone, this is here not reachable, as the 
			 *   plugin remains active and is not newly invoked. Hmm, what to do?
			 */
//			MindMapNode originalNode = getOriginalNode();
//			MindMapNode cloneNode = getCloneNode();
//			logger.info("Invoke with orig: " + originalNode + " and clone " + cloneNode);
//			if(originalNode.isDescendantOf(cloneNode)){
//				disablePlugin();
//				return;
//			}
			registerPlugin();
			return;
		}
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
		registerPlugin();
	}

	private void disablePlugin() {
		// TODO: Abspeichern!
		getMindMapController().getController().errorMessage("This is not possible. Cloning will be disabled.");
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
//		getMindMapController().registerNodeLifetimeListener(this);
		getMindMapController().getActionFactory().registerFilter(this);
	}


	private void deregisterPlugin() {
		getMindMapController().getActionFactory().deregisterFilter(this);
//		getMindMapController().deregisterNodeLifetimeListener(this);
	}

	MindMapNode getOriginalNode() {
		try {
			return getMindMapController().getNodeFromID(mOriginalNodeId);
		} catch (IllegalArgumentException e) {
//			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	MindMapNode getCloneNode() {
		try {
			return getMindMapController().getNodeFromID(mCloneNodeId);
		} catch (IllegalArgumentException e) {
//			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public void onCreateNodeHook(MindMapNode node) {
		if(isDisabled()) {
			return;
		}
		MindMapNode originalNode = getOriginalNode();
		if(originalNode==null) {
			logger.info("Original node is (currently?) not available (presumably pasting).");
			return;
		}
		MindMapNode cloneNode = getCloneNode();
		if(cloneNode==null) {
			logger.info("Clone node is (currently?) not available.");
			return;
		}
		if(cloneNode.isChildOfOrEqual(node) && node.isChildOfOrEqual(originalNode)){
			// orig -> .... -> node -> .. -> clone
			disablePlugin();
		}
		if(originalNode.isChildOfOrEqual(node) && node.isChildOfOrEqual(cloneNode)){
			// clone -> .... -> node -> .. -> original
			disablePlugin();
		}
	}

	public void onPreDeleteNode(MindMapNode node) {
	}

	public void onPostDeleteNode(MindMapNode node, MindMapNode parent) {
	}
}
