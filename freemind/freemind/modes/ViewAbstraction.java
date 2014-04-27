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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import freemind.view.mindmapview.NodeView;

/**
 * Abstraction for the MapView
 * Handles selections, focus, etc.
 * 
 * @author foltin
 * @date 18.03.2014
 */
public interface ViewAbstraction {

	/**
	 * Makes the node the only selected, scrolls it to the visible rect of the screen.
	 */
	void select(NodeView node);

	/**
	 * Removes the selection for this node. Take care, that at least one selected node
	 * is remaining.
	 * 
	 * @param node
	 */
	void deselect(NodeView node);

	NodeView getNodeView(MindMapNode node);

	boolean isSelected(NodeView n);

	LinkedList<NodeView> getSelecteds();

	NodeView getSelected();
	
	/**
	 * @return an ArrayList of MindMapNode objects. If both ancestor and
	 *         descendant node are selected, only the ancestor is returned
	 */
	ArrayList<MindMapNode> getSelectedNodesSortedByY();

}
