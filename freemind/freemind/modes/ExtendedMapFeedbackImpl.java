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
import java.util.List;

import freemind.extensions.HookFactory;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActionRegistry;
import freemind.modes.mindmapmode.actions.xml.actors.XmlActorFactory;
import freemind.modes.mindmapmode.hooks.MindMapHookFactory;

/**
 * @author foltin
 * @date 16.03.2014
 */
public abstract class ExtendedMapFeedbackImpl extends ExtendedMapFeedbackAdapter {

	private ActionRegistry mActionRegistry;
	private MindMapNode mSelectedNode;
	private XmlActorFactory mActorFactory;
	private MindMapHookFactory mNodeHookFactory;

	/**
	 * 
	 */
	public ExtendedMapFeedbackImpl() {
		super();
		mActionRegistry = new ActionRegistry();
		mActorFactory = new XmlActorFactory(this);
		
	}

	@Override
	public ActionRegistry getActionRegistry() {
		return mActionRegistry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.ExtendedMapFeedback#doTransaction(java.lang.String,
	 * freemind.modes.mindmapmode.actions.xml.ActionPair)
	 */
	@Override
	public boolean doTransaction(String pName, ActionPair pPair) {
		return mActionRegistry.doTransaction(pName, pPair);
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

	/* (non-Javadoc)
	 * @see freemind.modes.ExtendedMapFeedback#select(freemind.modes.MindMapNode, java.util.List)
	 */
	@Override
	public void select(MindMapNode pFocussed, List<MindMapNode> pSelecteds) {
		mSelectedNode = pFocussed;
	}

}
