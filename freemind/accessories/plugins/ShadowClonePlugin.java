/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2011 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package accessories.plugins;

import java.util.HashMap;

import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 29.04.2011
 */
public class ShadowClonePlugin extends PermanentMindMapNodeHookAdapter {
	private static final String XML_STORAGE_ORIGINAL = "ORIGINAL_ID";
	public static final String PLUGIN_LABEL = "accessories/plugins/ShadowClonePlugin.properties";
	private String mOriginalNodeId;

	public void loadFrom(XMLElement child) {
		super.loadFrom(child);
		HashMap values = loadNameValuePairs(child);
		final String xmlId = (String) values.get(XML_STORAGE_ORIGINAL);
		if (xmlId != null) {
			mOriginalNodeId = xmlId;
			logger.finest("Setting mOriginalNodeId to " + mOriginalNodeId);
		} else {
			logger.finest("Leaving mOriginalNodeId to be " + mOriginalNodeId);
		}
	}

	public void save(XMLElement xml) {
		super.save(xml);
		HashMap values = new HashMap();
		values.put(XML_STORAGE_ORIGINAL, mOriginalNodeId);
		saveNameValuePairs(values, xml);
	}

	public void startupMapHook() {
		super.startupMapHook();
		// register at "mother" clone:
		MindMapNode originalNode = getOriginalNode();
		if (originalNode == null)
			return;
		ClonePlugin hook = ClonePasteAction.getHook(originalNode);
		if (hook != null) {
			hook.addClone(getNode());
		}
	}

	MindMapNode getOriginalNode() {
		if (mOriginalNodeId == null)
			return null;
		try {
			return getMindMapController().getNodeFromID(mOriginalNodeId);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return null;
		}
	}

	public void shutdownMapHook() {
		// deregister at "mother" clone:
		ClonePlugin hook = ClonePasteAction.getHook(getOriginalNode());
		if (hook != null) {
			hook.removeClone(getNode());
		}
		super.shutdownMapHook();
	}

	public void setOriginalNodeId(String pOriginalNodeId) {
		mOriginalNodeId = pOriginalNodeId;
		logger.finest("Setting mOriginalNodeId to " + mOriginalNodeId);
		startupMapHook();
	}
}
