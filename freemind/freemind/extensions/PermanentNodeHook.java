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
 * Created on 06.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package freemind.extensions;

import java.io.IOException;
import java.io.Writer;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.NodeView;

/**
 * @author foltin
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface PermanentNodeHook extends NodeHook {

	void onFocusNode(NodeView nodeView);

	/**
	 * 
	 */
	void onLostFocusNode(NodeView nodeView);

	/**
	 * Fired after node is getting visible (is unfolded after having been folded).
	 */
	void onViewCreatedHook(NodeView nodeView);

	/**
	 * Fired after a node is getting invisible (folded).
	 */
	void onViewRemovedHook(NodeView nodeView);

	/**
	 * If the node I belong to is changed, I get this notification.
	 * */
	void onUpdateNodeHook();

	/**
	 * Is called if the addedChildNode is inserted as a direct child of the
	 * node, this hook is attached to. The cases in which this method is called
	 * contain new nodes, paste, move, etc.
	 * 
	 * Ah, don't call propagate in this method, as paste introduces nodes with
	 * the hook and you'll have them twice, ... see onNewChild
	 */
	void onAddChild(MindMapNode addedChildNode);

	/**
	 * Is only called, if a new nodes is inserted as a child. Remark: In this
	 * case onAddChild is called too and moreover *before* this method. see
	 * onAddChild.
	 */
	void onNewChild(MindMapNode newChildNode);

	/**
	 * This method is called, if a child is added to me or to any of my
	 * children. (See onUpdateChildrenHook)
	 */
	void onAddChildren(MindMapNode addedChild);

	void onRemoveChild(MindMapNode oldChildNode);

	/**
	 * This method is called, if a child is removed to me or to any of my
	 * children. (See onUpdateChildrenHook)
	 * 
	 * @param oldDad
	 *            TODO
	 */
	void onRemoveChildren(MindMapNode oldChildNode, MindMapNode oldDad);

	/**
	 * If any of my children is updated, I get this notification.
	 */
	void onUpdateChildrenHook(MindMapNode updatedNode);

	/**
	 */
	void save(XMLElement hookElement);

	/**
	 */
	void loadFrom(XMLElement child);

	/**
	 * Can be used to adjust some things after a paste action. (Currently it is used for clones).
	 */
	void processUnfinishedLinks();

	/**
	 * Can be used to contribute to the standard html export.
	 * @param pFileout
	 * @throws IOException 
	 */
	void saveHtml(Writer pFileout) throws IOException;
}
