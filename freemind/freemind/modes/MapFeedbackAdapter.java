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
import java.util.HashMap;

import freemind.controller.MapMouseMotionListener;
import freemind.controller.MapMouseWheelListener;
import freemind.controller.NodeDragListener;
import freemind.controller.NodeDropListener;
import freemind.controller.NodeKeyListener;
import freemind.controller.NodeMotionListener;
import freemind.controller.NodeMouseMotionListener;
import freemind.extensions.NodeHook;
import freemind.main.Resources;
import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.ViewFeedback;

/**
 * @author foltin
 * @date 07.02.2014
 */
public abstract class MapFeedbackAdapter implements MapFeedback, ViewFeedback {

	private HashMap<String, Font> fontMap = new HashMap<String, Font>();

	/**
	 * 
	 */
	public MapFeedbackAdapter() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.MapFeedback#fireNodePreDeleteEvent(freemind.modes.MindMapNode
	 * )
	 */
	@Override
	public void fireNodePreDeleteEvent(MindMapNode pNode) {

	}

	/* (non-Javadoc)
	 * @see freemind.modes.MapFeedback#fireRecursiveNodeCreateEvent(freemind.modes.MindMapNode)
	 */
	@Override
	public void fireRecursiveNodeCreateEvent(MindMapNode pNode) {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.MapFeedback#firePreSaveEvent(freemind.modes.MindMapNode)
	 */
	@Override
	public void firePreSaveEvent(MindMapNode pNode) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MapFeedback#nodeChanged(freemind.modes.MindMapNode)
	 */
	@Override
	public void nodeChanged(MindMapNode pNode) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MapFeedback#nodeRefresh(freemind.modes.MindMapNode)
	 */
	@Override
	public void nodeRefresh(MindMapNode pNode) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MapFeedback#paste(freemind.modes.MindMapNode,
	 * freemind.modes.MindMapNode)
	 */
	@Override
	public void paste(MindMapNode pNode, MindMapNode pParent) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MapFeedback#getResourceString(java.lang.String)
	 */
	@Override
	public String getResourceString(String pTextId) {
		return Resources.getInstance().getResourceString(pTextId);
	}


	/* (non-Javadoc)
	 * @see freemind.modes.MindMap.MapFeedback#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String pResourceId) {
		return Resources.getInstance().getProperty(pResourceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MapFeedback#out(java.lang.String)
	 */
	@Override
	public void out(String pFormat) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MapFeedback#getDefaultFont()
	 */
	@Override
	public Font getDefaultFont() {

		return null;
	}

	@Override
	public Font getFontThroughMap(Font font) {
		if (!fontMap.containsKey(font.toString())) {
			fontMap.put(font.toString(), font);
		}
		return (Font) fontMap.get(font.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.modes.MapFeedback#createNodeHook(java.lang.String,
	 * freemind.modes.MindMapNode)
	 */
	@Override
	public NodeHook createNodeHook(String pLoadName, MindMapNode pNode) {

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.MapFeedback#invokeHooksRecursively(freemind.modes.MindMapNode
	 * , freemind.modes.MindMap)
	 */
	@Override
	public void invokeHooksRecursively(MindMapNode pNode, MindMap pModel) {

	}

	@Override
	public void changeSelection(NodeView pNode, boolean pIsSelected) {
		
		
	}

	@Override
	public void onLostFocusNode(NodeView pNode) {
		
		
	}

	@Override
	public void onFocusNode(NodeView pNode) {
		
		
	}

	@Override
	public void setFolded(MindMapNode pModel, boolean pB) {
		
		
	}

	@Override
	public void onViewCreatedHook(NodeView pNewView) {
		
		
	}

	@Override
	public void onViewRemovedHook(NodeView pNodeView) {
		
		
	}

	@Override
	public NodeMouseMotionListener getNodeMouseMotionListener() {
		
		return null;
	}

	@Override
	public NodeMotionListener getNodeMotionListener() {
		
		return null;
	}

	@Override
	public NodeKeyListener getNodeKeyListener() {
		
		return null;
	}

	@Override
	public NodeDragListener getNodeDragListener() {
		
		return null;
	}

	@Override
	public NodeDropListener getNodeDropListener() {
		
		return null;
	}

	@Override
	public MapMouseMotionListener getMapMouseMotionListener() {
		
		return null;
	}

	@Override
	public MapMouseWheelListener getMapMouseWheelListener() {
		
		return null;
	}

}
