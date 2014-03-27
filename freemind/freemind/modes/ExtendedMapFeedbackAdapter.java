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
import java.io.IOException;
import java.util.ListIterator;

import freemind.modes.mindmapmode.MindMapNodeModel;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActionRegistry;
import freemind.modes.mindmapmode.actions.xml.actors.XmlActorFactory;

/**
 * @author foltin
 * @date 16.03.2014
 */
public abstract class ExtendedMapFeedbackAdapter extends MapFeedbackAdapter
		implements ExtendedMapFeedback {

	private ActionRegistry mActionFactory;
	private MindMapNode mSelectedNode;
	private XmlActorFactory mActorFactory;

	/**
	 * 
	 */
	public ExtendedMapFeedbackAdapter() {
		super();
		mActionFactory = new ActionRegistry();
		mActorFactory = new XmlActorFactory(this);
		
	}

	@Override
	public ActionRegistry getActionRegistry() {
		return mActionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ExtendedMapFeedback#doTransaction(java.lang.String,
	 * freemind.modes.mindmapmode.actions.xml.ActionPair)
	 */
	@Override
	public boolean doTransaction(String pName, ActionPair pPair) {
		return mActionFactory.doTransaction(pName, pPair);
	}

	/**
	 * @throws {@link IllegalArgumentException} when node isn't found.
	 */
	@Override
	public NodeAdapter getNodeFromID(String nodeID) {
		// FIXME: Duplicated code with ControllerAdapter
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
		// FIXME: Duplicated code with ControllerAdapter
		return getMap().getLinkRegistry().registerLinkTarget(selected);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ExtendedMapFeedback#getSelected()
	 */
	@Override
	public MindMapNode getSelected() {
		return mSelectedNode;
	}

	@Override
	public void insertNodeInto(MindMapNode pNewNode, MindMapNode pParent,
			int pIndex) {
		getMap().insertNodeInto(pNewNode, pParent, pIndex);
	}

	@Override
	public MindMapNode newNode(Object pUserObject, MindMap pMap) {
		return new MindMapNodeModel(pUserObject, pMap);
	}

	@Override
	public void removeNodeFromParent(MindMapNode pSelectedNode) {
		getMap().removeNodeFromParent(pSelectedNode);
	}

	@Override
	public XmlActorFactory getActorFactory() {
		return mActorFactory;
	}

	/* (non-Javadoc)
	 * @see freemind.modes.ExtendedMapFeedback#copy(freemind.modes.MindMapNode, boolean)
	 */
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
}
