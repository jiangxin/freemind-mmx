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
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import freemind.extensions.NodeHook;
import freemind.main.XMLParseException;
import freemind.modes.ModeController.ReaderCreator;
import freemind.modes.mindmapmode.MindMapController.StringReaderCreator;

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
	 * @param pNode
	 */
	void firePreSaveEvent(MindMapNode pNode);
	/**
	 * Invoke this method after you've changed how a node is to be represented
	 * in the tree.
	 */
	void nodeChanged(MindMapNode node);

	void nodeRefresh(MindMapNode node);

	/** @see ModeController#insertNodeInto(MindMapNode, MindMapNode, int)*/
	void insertNodeInto(MindMapNode pNewNode,
			MindMapNode pParent, int pIndex);
	
	/**
	 * @see ModeController#paste(MindMapNode, MindMapNode)
	 */
	void paste(MindMapNode pNode,
			MindMapNode pParent);
	/**
	 * @param pTextId
	 * @return
	 */
	String getResourceString(String pTextId);
	/**
	 * @param pResourceId 
	 * @return the string from Resources_<lang>.properties belonging to the pResourceId.
	 */
	String getProperty(String pResourceId);
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
}