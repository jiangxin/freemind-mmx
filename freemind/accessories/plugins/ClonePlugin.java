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

import java.awt.EventQueue;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;

import accessories.plugins.ClonePasteAction.Registration;
import freemind.extensions.PermanentNodeHook;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.Tools.MindMapNodePair;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import freemind.view.mindmapview.NodeView;

public class ClonePlugin extends PermanentMindMapNodeHookAdapter implements
		NodeSelectionListener, NodeLifetimeListener {

	public static final String PLUGIN_LABEL = "accessories/plugins/ClonePlugin.properties";
	public static final String XML_STORAGE_CLONES = "CLONE_IDS";
	public static final String XML_STORAGE_CLONE_ID = "CLONE_ID";

	/**
	 * This is the master list. {@link ClonePlugin#mCloneNodes mCloneNodes}
	 */
	private HashSet mCloneNodeIds;
	/**
	 * Includes the original node. This is a cached list with the MindMapNodes
	 * belonging to the {@link ClonePlugin#mCloneNodeIds mCloneNodeIds}.
	 */
	private HashSet mCloneNodes;

	private static ImageIcon sCloneIcon;
	private static ImageIcon sOriginalIcon;
	private static boolean sShowIcon = true;
	private String mCloneId;

	public ClonePlugin() {
	}

	public void invoke(MindMapNode node) {
		super.invoke(node);
		if (sCloneIcon == null) {
			sCloneIcon = new ImageIcon(getMindMapController().getResource(
					"images/clone.png"));
			sOriginalIcon = new ImageIcon(getMindMapController().getResource(
					"images/clone_original.png"));
			sShowIcon = Resources.getInstance().getBoolProperty(
					FreeMind.RESOURCES_DON_T_SHOW_CLONE_ICONS);
		}
		registerPlugin();
	}

	public void addClone(MindMapNode cloneNode) {
		mCloneNodeIds.add(getMindMapController().getNodeID(cloneNode));
		clearCloneCache();
	}

	public void clearCloneCache() {
		mCloneNodes = new HashSet();
	}

	private void disablePlugin() {
		getMindMapController().getController().errorMessage(
				getMindMapController().getText("clone_plugin_impossible"));
		toggleHook();
	}

	/** double add = remove. 
	 * 
	 */
	protected void toggleHook() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Vector selecteds = Tools.getVectorWithSingleElement(getNode());
				getMindMapController().addHook(getNode(), selecteds,
						PLUGIN_LABEL);
			}
		});
	}

	public void save(XMLElement xml) {
		super.save(xml);
		HashMap values = new HashMap();
		StringBuffer cloneIds = new StringBuffer();
		for (Iterator it = mCloneNodeIds.iterator(); it.hasNext();) {
			String cloneId = (String) it.next();
			cloneIds.append(cloneId);
			cloneIds.append(",");
		}
		values.put(XML_STORAGE_CLONES, cloneIds);
		values.put(XML_STORAGE_CLONE_ID, mCloneId);
		saveNameValuePairs(values, xml);
		logger.fine("Saved clone plugin");
	}

	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		mCloneNodes = null;
		mCloneNodeIds = new HashSet();
		HashMap values = loadNameValuePairs(child);
		String cloneIds = (String) values.get(XML_STORAGE_CLONES);
		if (cloneIds != null) {
			StringTokenizer st = new StringTokenizer(cloneIds, ",");
			while (st.hasMoreTokens()) {
				String cloneId = st.nextToken();
				mCloneNodeIds.add(cloneId);
			}
		}
		mCloneId = (String) values.get(XML_STORAGE_CLONE_ID);
	}

	public void shutdownMapHook() {
		logger.fine("Shutdown of clones");
		deregisterPlugin();
		super.shutdownMapHook();
	}

	private void registerPlugin() {
		/*
		 * test for error cases: - orig is child of clone now - if clone is a
		 * child of clone, this is here not reachable, as the plugin remains
		 * active and is not newly invoked. Hmm, what to do?
		 */
		MindMapNode originalNode = getNode();
		HashSet cloneNodes = getCloneNodes();
		logger.fine("Invoke shadow class with orig: "
				+ printNodeId(originalNode) + " and clones "
				+ printNodeIds(cloneNodes));
		// check for error case that clones are descendant of one another.
		for (Iterator it = cloneNodes.iterator(); it.hasNext();) {
			MindMapNode cloneNode = (MindMapNode) it.next();
			if (originalNode != null && originalNode.isDescendantOf(cloneNode)) {
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
		Registration registration = getRegistration();
		if (mCloneId == null) {
			// hmm, it seems, that I am the first. Let's generate an id:
			mCloneId = registration.generateNewCloneId(null);
		}
		registration.registerClone(mCloneId, this);
		// the clone list contains itself, too.
		addClone(getNode());
	}

	protected Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

	private void deregisterPlugin() {
		getRegistration().deregisterClone(mCloneId, this);
		getMindMapController().deregisterNodeSelectionListener(this);
		getMindMapController().deregisterNodeLifetimeListener(this);
	}

	public void onCreateNodeHook(MindMapNode node) {
		HashSet cloneNodes = getCloneNodes();
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

	/**
	 * @return a list of {@link MindMapNode}s including the original node!
	 */
	HashSet getCloneNodes() {
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
				mCloneNodes.add(getNode());
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
	 * @return a list of {@link MindMapNodePair}s where the first is the
	 *         corresponding node and the second is the clone. If the return
	 *         value is empty, the node isn't son of any.
	 */
	private List/* MindMapNodePair */getCorrespondingNodes(MindMapNode pNode,
			boolean includeNodeItself) {
		Vector returnValue = new Vector();
		// build list of indices up to a clone/original is found.
		Vector indexVector = new Vector();
		MindMapNode child = pNode;
		HashSet cloneNodes = getCloneNodes();
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
		HashSet targets = cloneNodes;
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
	private String printNodeIds(Collection pTargets) {
		Vector strings = new Vector();
		for (Iterator it = pTargets.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			strings.add(printNodeId(node));
		}
		return "" + strings;
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
		if (cloneNode.isDescendantOfOrEqual(node)
				&& node.isDescendantOfOrEqual(originalNode)) {
			// orig -> .... -> node -> .. -> clone
			disablePlugin();
		}
	}

	public void removeClone(MindMapNode pCloneNode) {
		mCloneNodeIds.remove(getMindMapController().getNodeID(pCloneNode));
		clearCloneCache();
		registerPlugin();
		if (mCloneNodeIds.isEmpty()) {
			// remove icon
			getNode().setStateIcon(getName(), null);
			getMindMapController().nodeRefresh(getNode());
			// remove myself
			toggleHook();
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

	public static ClonePlugin getHook(MindMapNode originalNode) {
		if (originalNode == null) {
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

}
