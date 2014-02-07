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

package freemind.view.mindmapview;

import java.awt.Font;

import freemind.controller.MapMouseMotionListener;
import freemind.controller.MapMouseWheelListener;
import freemind.controller.NodeDragListener;
import freemind.controller.NodeDropListener;
import freemind.controller.NodeKeyListener;
import freemind.controller.NodeMotionListener;
import freemind.controller.NodeMouseMotionListener;
import freemind.modes.MindMapNode;

public interface ViewFeedback {

	/**
	 * @param pNode
	 * @param pIsSelected
	 */
	void changeSelection(NodeView pNode, boolean pIsSelected);

	/**
	 * @param pNode
	 */
	void onLostFocusNode(NodeView pNode);

	/**
	 * @param pNode
	 */
	void onFocusNode(NodeView pNode);

	/**
	 * @param pModel
	 * @param pB
	 */
	void setFolded(MindMapNode pModel, boolean pB);

	/**
	 * @param pNewView
	 */
	void onViewCreatedHook(NodeView pNewView);

	/**
	 * @param pNodeView
	 */
	void onViewRemovedHook(NodeView pNodeView);

	/**
	 * @param pResourceId 
	 * @return the setting of freemind.properties resp. auto.properties.
	 */
	String getProperty(String pResourceId);

	/**
	 * @return
	 */
	Font getDefaultFont();

	/**
	 * @return
	 */
	NodeMouseMotionListener getNodeMouseMotionListener();

	/**
	 * @return
	 */
	NodeMotionListener getNodeMotionListener();

	/**
	 * @return
	 */
	NodeKeyListener getNodeKeyListener();

	/**
	 * @return
	 */
	NodeDragListener getNodeDragListener();

	/**
	 * @return
	 */
	NodeDropListener getNodeDropListener();

	/**
	 * @return
	 */
	MapMouseMotionListener getMapMouseMotionListener();

	/**
	 * @return
	 */
	MapMouseWheelListener getMapMouseWheelListener();


}