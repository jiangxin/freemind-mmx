/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
/*
 * Created on 10.03.2004
 *
 */
package accessories.plugins;

import java.text.MessageFormat;
import java.util.Iterator;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

/**
 * @author foltin
 * 
 */
public class CreationModificationPlugin extends PermanentMindMapNodeHookAdapter {

	private String tooltipFormat;

	/**
	 *  
	 */
	public CreationModificationPlugin() {
		super();
	}

	private void setStyle(MindMapNode node) {
		Object[] messageArguments = {
				node.getHistoryInformation().getCreatedAt(),
				node.getHistoryInformation().getLastModifiedAt() };
		if (tooltipFormat == null) {
			tooltipFormat = getResourceString("tooltip_format");
		}
		MessageFormat formatter = new MessageFormat(tooltipFormat);
		String message = formatter.format(messageArguments);
		setToolTip(node, getName(), message);
		logger.finest(this + "Tooltip for " + node + " with parent "
				+ node.getParentNode() + " is " + message);
	}

	public void shutdownMapHook() {
		removeToolTipRecursively(getNode());
		super.shutdownMapHook();
	}

	/**
	 *  
	 */
	private void removeToolTipRecursively(MindMapNode node) {
		setToolTip(node, getName(), null);
		for (Iterator i = node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			removeToolTipRecursively(child);
		}
	}

	// private long getCreated() {
	// return getNode().getHistoryInformation().getCreatedAt().getTime();
	// }
	//
	// private long getModified() {
	// return getNode().getHistoryInformation().getLastModifiedAt().getTime();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHook#onUpdateChildrenHook(freemind.modes
	 * .MindMapNode)
	 */
	public void onUpdateChildrenHook(MindMapNode updatedNode) {
		super.onUpdateChildrenHook(updatedNode);
		setStyleRecursive(updatedNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHook#onUpdateNodeHook()
	 */
	public void onUpdateNodeHook() {
		super.onUpdateNodeHook();
		setStyle(getNode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.NodeHook#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode node) {
		super.invoke(node);
		setStyleRecursive(node);
	}

	/**
	 */
	private void setStyleRecursive(MindMapNode node) {
		logger.finest("setStyle " + node);
		setStyle(node);
		// recurse:
		for (Iterator i = node.childrenFolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
			setStyleRecursive(child);
		}
	}

	public void onAddChildren(MindMapNode pAddedChild) {
		setStyleRecursive(pAddedChild);
	}

	public void onNewChild(MindMapNode pNewChildNode) {
		setStyleRecursive(pNewChildNode);
	}

}
