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

package plugins.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import plugins.map.FreeMindMapController.TileSourceStore;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.MindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 22.12.2011
 */
public class AddLinkToMapAction extends MindMapNodeHookAdapter {

	static final String NODE_CONTEXT_PLUGIN_NAME = "plugins/map/MapDialog_AddLinkToMapAction.properties";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		List selecteds = getMindMapController().getSelecteds();
		for (Iterator it = selecteds.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			MapNodePositionHolder hook = MapNodePositionHolder.getHook(node);
			if(hook != null) {
				if(node.getLink() != null) {
					// TODO: ask user if should overwrite?
					continue;
				}
				String layer = "M";
				TileSourceStore tileSourceByName = FreeMindMapController.getTileSourceByName(hook.getTileSource());
				if(tileSourceByName != null) {
					layer = tileSourceByName.mLayerName;
				}
				String link= "http://www.openstreetmap.org/?" +
						"mlat=" + hook.getPosition().getLat()
						+"&mlon="+ hook.getPosition().getLon()
						+ "&lat=" + hook.getMapCenter().getLat()
						+"&lon="+ hook.getMapCenter().getLon()
						+"&zoom=" + hook.getZoom()
						+ "&layers=" + layer;
				getMindMapController().setLink(node, link);
			}
		}
	}
}
