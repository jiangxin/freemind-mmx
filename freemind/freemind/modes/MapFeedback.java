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

import java.awt.Font;
import java.util.List;

import freemind.extensions.NodeHook;
import freemind.view.mindmapview.ViewFeedback;

/**
 * This interface describes the services, the {@link ModeController} provides to 
 * a MindMap and its descendants.
 *  
 * @author foltin
 * @date 30.01.2014
 */
public interface MapFeedback {
	/**
	 * Is issued before a node is deleted. It is issued via
	 * NodeLifetimeListener.
	 */
	void fireNodePreDeleteEvent(MindMapNode node);
	
	/**
	 * Is issued after a node is deleted. It is issued via
	 * NodeLifetimeListener.
	 */
	void fireNodePostDeleteEvent(MindMapNode node, MindMapNode parent);
	
	/**
	 * @param pNode
	 */
	void firePreSaveEvent(MindMapNode pNode);
	/**
	 * Invoke this method after you've changed how a node is to be represented
	 * in the tree.
	 */
	void nodeChanged(MindMapNode node);

	void nodeRefresh(MindMapNode node);

	void fireRecursiveNodeCreateEvent(MindMapNode node);
	
	/**
	 * @see ModeController#paste(MindMapNode, MindMapNode)
	 */
	void paste(MindMapNode pNode,
			MindMapNode pParent);
	/**
	 * @param pTextId
	 * @return the string from Resources_<lang>.properties belonging to the pResourceId.
	 */
	String getResourceString(String pTextId);
	/**
	 * @param pResourceId 
	 * @return the setting of freemind.properties resp. auto.properties.
	 */
	String getProperty(String pResourceId);

	int getIntProperty(String key, int defaultValue);

	
	/**
	 * @param pProperty
	 * @param pValue
	 */
	void setProperty(String pProperty, String pValue);

	/**
	 * Show the message to the user.
	 * @param pFormat
	 */
	void out(String pFormat);
	/**
	 * @return
	 */
	Font getDefaultFont();
	/**
	 * @param pFont
	 * @return
	 */
	Font getFontThroughMap(Font pFont);
	
	/**
	 * MapFeedback and MindMap are closely intertwined.
	 */
	MindMap getMap();

	/**
	 * @param pLoadName
	 * @param pNode
	 * @return
	 */
	NodeHook createNodeHook(String pLoadName, MindMapNode pNode);

	/**
	 * @param pNode
	 * @param pModel
	 */
	void invokeHooksRecursively(MindMapNode pNode, MindMap pModel);

	/**
	 * @return a ViewAbstraction, if a view is attached, null otherwise.
	 */
	ViewAbstraction getViewAbstraction();
	
	/**
	 * @return null, if no feedback is available.
	 */
	ViewFeedback getViewFeedback();

	void sortNodesByDepth(List inPlaceList);
	
}

