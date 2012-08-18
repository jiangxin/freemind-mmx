/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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

package freemind.modes.common.plugins;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;

import freemind.extensions.PermanentNodeHook;
import freemind.extensions.PermanentNodeHookAdapter;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;

/**
 * @author foltin
 * @date 16.08.2012
 */
public class MapNodePositionHolderBase extends PermanentNodeHookAdapter {

	public static final String NODE_MAP_HOOK_NAME = "plugins/map/MapNodePositionHolder.properties";
	public static final String NODE_MAP_LOCATION_ICON = "node_map_location_icon";
	protected static final String XML_STORAGE_POS_LON = "XML_STORAGE_POS_LON";
	protected static final String XML_STORAGE_POS_LAT = "XML_STORAGE_POS_LAT";
	protected static final String XML_STORAGE_MAP_LON = "XML_STORAGE_MAP_LON";
	protected static final String XML_STORAGE_MAP_LAT = "XML_STORAGE_MAP_LAT";
	protected static final String XML_STORAGE_ZOOM = "XML_STORAGE_ZOOM";
	protected static final String XML_STORAGE_TILE_SOURCE = "XML_STORAGE_TILE_SOURCE";
	protected static final String XML_STORAGE_MAP_TOOLTIP_LOCATION = "XML_STORAGE_MAP_TOOLTIP_LOCATION";
	protected static final String NODE_MAP_STORE_TOOLTIP = "node_map_store_tooltip";
	protected static final String NODE_MAP_SHOW_TOOLTIP = "node_map_show_tooltip";
	public static final String TILE_SOURCE_MAP_QUEST_OPEN_MAP = "plugins.map.FreeMindMapController.MapQuestOpenMap";
	public static final String TILE_SOURCE_TRANSPORT_MAP = "plugins.map.FreeMindMapController.TransportMap";
	public static final String TILE_SOURCE_CYCLE_MAP = "org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource$CycleMap";
	public static final String TILE_SOURCE_MAPNIK = "org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource$Mapnik";
	public static final String SHORT_MAP_QUEST_OPEN_MAP = "Q";
	public static final String SHORT_TRANSPORT_MAP = "T";
	public static final String SHORT_CYCLE_MAP = "C";
	public static final String SHORT_MAPNIK = "M";
	public static ImageIcon sMapLocationIcon;
	protected String mTooltipLocation = null;
	protected File mTooltipFile = null;
	private HashMap mValues;

	public static ImageIcon getMapLocationIcon() {
		// icon
		if (sMapLocationIcon == null) {
			sMapLocationIcon = new ImageIcon(Resources.getInstance()
					.getResource("images/map_location.png"));
		}
		return sMapLocationIcon;
	}

	protected void setStateIcon(MindMapNode node, boolean enabled) {
		node.setStateIcon(NODE_MAP_LOCATION_ICON,
				(enabled) ? getMapLocationIcon() : null);
	}

	/**
	 * 
	 */
	public MapNodePositionHolderBase() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHookAdapter#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		setStateIcon(getNode(), false);
		hideTooltip();
		super.shutdownMapHook();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		setStateIcon(pNode, true);
		showTooltip();
	}
	
	public void showTooltip() {
		if (isTooltipDesired()) {
			if (mTooltipLocation != null) {
				addTooltip();
			}
		}
	}

	protected boolean isTooltipDesired() {
		return Resources.getInstance().getBoolProperty(NODE_MAP_SHOW_TOOLTIP) && 
				!Tools.safeEquals(mTooltipLocation, "false");
	}


	public void loadFrom(XMLElement pChild) {
		super.loadFrom(pChild);
		mValues = loadNameValuePairs(pChild);
		// if no value stored, the get method returns null.
		mTooltipLocation = (String) mValues
				.get(XML_STORAGE_MAP_TOOLTIP_LOCATION);
	}

	public void addTooltip() {
		String imageHtml = getImageHtml();
		setToolTip(NODE_MAP_HOOK_NAME, imageHtml);
	}

	public String getImageHtml() {
		String imageTag = "<img src=\"file:./" + mTooltipLocation + "\"/>";
		String imageHtml = "<html><body>" + imageTag + "</body></html>";
		logger.fine("Tooltip at " + imageTag);
		return imageHtml;
	}

	/**
	 * 
	 */
	protected void hideTooltip() {
		setToolTip(NODE_MAP_HOOK_NAME, null);		
	}

	public static MapNodePositionHolderBase getBaseHook(MindMapNode node) {
		for (Iterator j = node.getActivatedHooks().iterator(); j.hasNext();) {
			PermanentNodeHook element = (PermanentNodeHook) j.next();
			if (element instanceof MapNodePositionHolderBase) {
				return (MapNodePositionHolderBase) element;
			}
		}
		return null;
	}

	public String[] getBarePosition() {
		return new String[] {(String) mValues.get(XML_STORAGE_POS_LAT), 
				(String) mValues.get(XML_STORAGE_POS_LON), 
				(String) mValues.get(XML_STORAGE_MAP_LAT),
				(String) mValues.get(XML_STORAGE_MAP_LON),
				(String) mValues.get(XML_STORAGE_ZOOM),
				(String) mValues.get(XML_STORAGE_TILE_SOURCE)};
	}

}

