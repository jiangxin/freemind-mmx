/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: NodeHookAdapter.java,v 1.1.2.1 2004-03-04 20:26:19 christianfoltin Exp $*/
package freemind.extensions;

import javax.swing.JMenu;

import freemind.modes.ControllerAdapter;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * @author christianfoltin
 *
 * @file NodeHookAdapter.java 
 * @package freemind.modes
 * */
public abstract class NodeHookAdapter extends HookAdapter implements NodeHook {

	private boolean selfUpdateExpected;

	private MindMap map;

	private MindMapNode node;

	// Logging: 
	private static java.util.logging.Logger logger;
	
	/**
	 * 
	 */
	public NodeHookAdapter(MindMapNode node, MindMap map, ModeController controller) {
		super(controller);
		this.node = node;
		this.map = map;
		if(logger == null)
			logger = ((ControllerAdapter)getController()).getFrame().getLogger(this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		logger.info("shutdownMapHook");
		node = null;
        map  = null;
		super.shutdownMapHook();
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#invoke()
	 */
	public void invoke() {
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onReceiveFocusHook()
	 */
	public void onReceiveFocusHook() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onMouseOverHook()
	 */
	public void onMouseOverHook() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		logger.info("onUpdateNodeHook");
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateChildrenHook()
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		logger.info("onUpdateChildrenHook");
	}

	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onUpdateAnyNodeHook()
	 */
	public void onUpdateAnyNodeHook(MindMapNode updatedNode) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * @return
	 */
	protected MindMapNode getNode() {
		return node;
	}

	/**
	 * @param node
	 */
	public void setNode(MindMapNode node) {
		this.node = node;
	}

	/**
	 * @return
	 */
	protected MindMap getMap() {
		return map;
	}

	/**
	 * @param node
	 */
	protected void nodeChanged(MindMapNode node) {
//		if(node == getNode()) {
//			setSelfUpdate(true);
//		}
		// fc, 29.2.2004 (yes, this day exists!)
		// this is not nice. The node should know itself, if it is updateable, but...
		if(node.getViewer() != null)
			getMap().nodeChanged(node);
//		setSelfUpdate(false);
	}

	/**
	 * @param b
	 */
	private void setSelfUpdate(boolean b) {
		this.selfUpdateExpected = b;
		
	}

	/**
	 * @return
	 */
	public boolean isSelfUpdateExpected() {
		return selfUpdateExpected;
	}

	/**
	 * @param string
	 */
	protected void setToolTip(String string) {
		getNode().setToolTip(string);
	}


	/* (non-Javadoc)
	 * @see freemind.modes.NodeHook#onAddChild(freemind.modes.MindMapNode)
	 */
	public void onAddChild(MindMapNode newChildNode) {
		logger.info("onAddChild");
	}

	/* (non-Javadoc)
	 * @see freemind.extensions.NodeHook#nodeMenuHook(javax.swing.JMenu)
	 */
	public void nodeMenuHook(JMenu nodeMenu) {
	}

}
