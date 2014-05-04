/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
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
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import freemind.controller.MindMapNodesSelection;
import freemind.main.Tools;
import freemind.modes.mindmapmode.MindMapNodeModel;

/**
 * @author foltin
 * @date 21.02.2014
 */
public final class ExtendedMapFeedbackImpl extends ExtendedMapFeedbackAdapter {
	private MindMap mMap;

	@Override
	public MindMap getMap() {
		return mMap;
	}

	@Override
	public MindMapNode newNode(Object pUserObject, MindMap pMap) {
		return new MindMapNodeModel(pUserObject, pMap);
	}

	@Override
	public Font getDefaultFont() {
		int fontSize = 12;
		int fontStyle = 0;
		String fontFamily = "SansSerif";

		return getFontThroughMap(new Font(fontFamily, fontStyle, fontSize));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ExtendedMapFeedbackAdapter#copy(freemind.modes.MindMapNode
	 * , boolean)
	 */
	@Override
	public Transferable copy(MindMapNode pNode, boolean pSaveInvisible) {
		StringWriter stringWriter = new StringWriter();
		try {
			((MindMapNodeModel) pNode).save(stringWriter, getMap()
					.getLinkRegistry(), pSaveInvisible, true);
		} catch (IOException e) {
		}
		Vector nodeList = Tools
				.getVectorWithSingleElement(getNodeID(pNode));
		return new MindMapNodesSelection(stringWriter.toString(), null,
				null, null, null, null, null, nodeList);
	}

	public void setMap(MindMap pMap) {
		mMap = pMap;
	}
}