/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.modes;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ListIterator;

import freemind.extensions.HookFactory;
import freemind.main.XMLParseException;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActionRegistry;
import freemind.modes.mindmapmode.actions.xml.actors.XmlActorFactory;
import freemind.modes.mindmapmode.hooks.MindMapHookFactory;

/**
 * @author foltin
 * @date 16.03.2014
 */
public abstract class ExtendedMapFeedbackAdapter extends MapFeedbackAdapter
		implements ExtendedMapFeedback {


	protected ActionRegistry mActionRegistry;
	private MindMapNode mSelectedNode;
	protected XmlActorFactory mActorFactory;
	private MindMapHookFactory mNodeHookFactory;

	/**
	 * 
	 */
	public ExtendedMapFeedbackAdapter() {
		super();
		mActionRegistry = new ActionRegistry();
		mActorFactory = new XmlActorFactory(this);

	}

	/**
	 * @throws {@link IllegalArgumentException} when node isn't found.
	 */
	@Override
	public NodeAdapter getNodeFromID(String nodeID) {
		NodeAdapter node = (NodeAdapter) getMap().getLinkRegistry()
				.getTargetForId(nodeID);
		if (node == null) {
			throw new IllegalArgumentException("Node belonging to the node id "
					+ nodeID + " not found in map " + getMap().getFile());
		}
		return node;
	}

	@Override
	public String getNodeID(MindMapNode selected) {
		return getMap().getLinkRegistry().registerLinkTarget(selected);
	}

	@Override
	public void insertNodeInto(MindMapNode pNewNode, MindMapNode pParent,
			int pIndex) {
		getMap().insertNodeInto(pNewNode, pParent, pIndex);
	}

	@Override
	public MindMapNode newNode(Object pUserObject, MindMap pMap) {
		return null;
	}

	@Override
	public void removeNodeFromParent(MindMapNode pSelectedNode) {
		getMap().removeNodeFromParent(pSelectedNode);
	}

	/* (non-Javadoc)
	 * @see freemind.modes.ExtendedMapFeedback#setWaitingCursor(boolean)
	 */
	@Override
	public void setWaitingCursor(boolean pWaiting) {
	}
	
	@Override
	public void nodeStyleChanged(MindMapNode node) {
		nodeChanged(node);
		final ListIterator childrenFolded = node.childrenFolded();
		while (childrenFolded.hasNext()) {
			MindMapNode child = (MindMapNode) childrenFolded.next();
			if (!(child.hasStyle() && child.getEdge().hasStyle())) {
				nodeStyleChanged(child);
			}
		}
	}

	@Override
	public ActionRegistry getActionRegistry() {
		return mActionRegistry;
	}

	@Override
	public boolean doTransaction(String pName, ActionPair pPair) {
		return mActionRegistry.doTransaction(pName, pPair);
	}

	@Override
	public MindMapNode getSelected() {
		return mSelectedNode;
	}

	@Override
	public XmlActorFactory getActorFactory() {
		return mActorFactory;
	}

	public Transferable copy(MindMapNode node, boolean saveInvisible) {
		return new Transferable() {
			
			@Override
			public boolean isDataFlavorSupported(DataFlavor pFlavor) {
				return false;
			}
			
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] {};
			}
			
			@Override
			public Object getTransferData(DataFlavor pFlavor)
					throws UnsupportedFlavorException, IOException {
				throw new UnsupportedFlavorException(pFlavor);
			}
		};
	}

	@Override
	public HookFactory getHookFactory() {
		// lazy creation.
		if (mNodeHookFactory == null) {
			mNodeHookFactory = new MindMapHookFactory();
			// initialization
			mNodeHookFactory.getPossibleNodeHooks();
		}
		return mNodeHookFactory;
	}

	@Override
	public void select(MindMapNode pFocussed, List<MindMapNode> pSelecteds) {
		mSelectedNode = pFocussed;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.ExtendedMapFeedback#load(java.io.File)
	 */
	@Override
	public MapFeedback load(File pFile) throws FileNotFoundException,
			IOException, XMLParseException, URISyntaxException {
		return this;
	}

	@Override
	public void close(boolean pForce) {
		
	}
}
