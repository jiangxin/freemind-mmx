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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.controller.actions.generated.instance.NodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.actions.xml.ActionFilter;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

public class ClonePlugin extends PermanentMindMapNodeHookAdapter implements
		ActionFilter {

	private static final String XML_STORAGE_ORIGINAL = "ORIGINAL_ID";
	private static final String XML_STORAGE_CLONE = "CLONE_ID";
	String mOriginalNodeId;
	String mCloneNodeId;

	public ClonePlugin() {
	}

	public ActionPair filterAction(ActionPair pair) {
		XmlAction doAction = pair.getDoAction();
		doAction = cloneAction(doAction);
		pair.setDoAction(doAction);
		return pair;
	}

	private XmlAction cloneAction(XmlAction doAction) {
		logger.info("Found do action: " + doAction.getClass().getName());
		if (doAction instanceof NodeAction) {
			NodeAction nodeAction = (NodeAction) doAction;
			// check for clone or original?
			MindMapNode node = getMindMapController().getNodeFromID(
					nodeAction.getNode());
			MindMapNode originalNode = getOriginalNode();
			MindMapNode cloneNode = getCloneNode();
			if (isNodeChildOf(node, originalNode)) {
				MindMapNode correspondingNode = getCorrespondingNode(node,
						originalNode, cloneNode);
				doAction = getNewCompoundAction(nodeAction, correspondingNode);
			}
			if (isNodeChildOf(node, cloneNode)) {
				MindMapNode correspondingNode = getCorrespondingNode(node,
						cloneNode, originalNode);
				doAction = getNewCompoundAction(nodeAction, correspondingNode);
			}
		} else {
			if (doAction instanceof CompoundAction) {
				CompoundAction compoundAction = (CompoundAction) doAction;
				List choiceList = compoundAction.getListChoiceList();
				int index = 0;
				for(Iterator it = choiceList.iterator(); it.hasNext(); ){
					XmlAction subAction = (XmlAction) it.next();
					subAction = cloneAction(subAction);
					compoundAction.setAtChoice(index, subAction);
					index ++;
				}
			}
		}
		return doAction;
	}

	private XmlAction getNewCompoundAction(NodeAction nodeAction, MindMapNode correspondingNode) {
		CompoundAction compound = new CompoundAction();
		compound.addChoice(nodeAction);
		// deep copy:
		NodeAction copiedNodeAction = (NodeAction) getMindMapController()
				.unMarshall(getMindMapController().marshall(nodeAction));
		if (copiedNodeAction instanceof NewNodeAction) {
			NewNodeAction newNodeAction = (NewNodeAction) copiedNodeAction;
			String newId = getMap().getLinkRegistry().generateUniqueID(null);
			newNodeAction.setNewId(newId);
		}
		copiedNodeAction.setNode(getMindMapController().getNodeID(
				correspondingNode));
		compound.addChoice(copiedNodeAction);
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

	private boolean isNodeChildOf(MindMapNode pNode, MindMapNode pParentNode) {
		if (pNode == pParentNode) {
			return true;
		}
		if (pNode.isRoot()) {
			return false;
		}
		return isNodeChildOf(pNode.getParentNode(), pParentNode);
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		if(mOriginalNodeId != null) {
			// the plugin has recently be loaded and the nodes have been filled.
			getMindMapController().getActionFactory().registerFilter(this);
			return;
		}
		MindMapNode originalNode = getMindMapController().getSelected();
		mOriginalNodeId = getMindMapController().getNodeID(originalNode);
		logger.info("Original node " + originalNode + ", id "
				+ mOriginalNodeId);
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
		getMindMapController().getActionFactory().registerFilter(this);
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
		getMindMapController().getActionFactory().deregisterFilter(this);
		super.shutdownMapHook();
	}

	MindMapNode getOriginalNode() {
		return getMindMapController().getNodeFromID(mOriginalNodeId);
	}

	MindMapNode getCloneNode() {
		return getMindMapController().getNodeFromID(mCloneNodeId);
	}
}
