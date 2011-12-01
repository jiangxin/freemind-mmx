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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

import freemind.extensions.PermanentNodeHook;
import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 27.10.2011
 */
public class MapNodePositionHolder extends PermanentMindMapNodeHookAdapter {
	public final static String NODE_MAP_HOOK_NAME = "plugins/map/MapNodePositionHolder.properties";
	public final static String NODE_MAP_LOCATION_ICON = "node_map_location_icon";

	private static final String XML_STORAGE_POS_LON = "XML_STORAGE_POS_LON";
	private static final String XML_STORAGE_POS_LAT = "XML_STORAGE_POS_LAT";
	private static final String XML_STORAGE_MAP_LON = "XML_STORAGE_MAP_LON";
	private static final String XML_STORAGE_MAP_LAT = "XML_STORAGE_MAP_LAT";
	private static final String XML_STORAGE_ZOOM = "XML_STORAGE_ZOOM";
	private static final String XML_STORAGE_TILE_SOURCE = "XML_STORAGE_TILE_SOURCE";
	private static final String XML_STORAGE_MAP_TOOLTIP = "XML_STORAGE_MAP_TOOLTIP";
	private static final String NODE_MAP_STORE_TOOLTIP = "node_map_store_tooltip";
	private static final String NODE_MAP_SHOW_TOOLTIP = "node_map_show_tooltip";

	private Coordinate mPosition = new Coordinate(0, 0);
	private Coordinate mMapCenter = new Coordinate(0, 0);
	private String mTileSource = null;
	private int mZoom = 1;
	private static ImageIcon sMapLocationIcon;
	private TileImage mImage;
	private String mBase64Image = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.NodeHookAdapter#invoke(freemind.modes.MindMapNode)
	 */
	public void invoke(MindMapNode pNode) {
		super.invoke(pNode);
		((Registration) getPluginBaseClass()).registerMapNode(this);
		setStateIcon(pNode, true);
		showTooltip();

	}

	public void showTooltip() {
		if (Resources.getInstance().getBoolProperty(
						NODE_MAP_SHOW_TOOLTIP)) {
			if (mBase64Image != null) {
				mImage = new TileImage();
				mImage.load(mBase64Image);
			} else {
				// order tooltip to be created.
				mImage = ((Registration) getPluginBaseClass())
						.getImageForTooltip(mPosition, mZoom, mTileSource);
			}
			if(!mImage.hasErrors()) {
				createTooltip();
			} else {
				mImage = null;
				logger.warning("Tooltip for node '" + getNode() + "' has errors on creation.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHookAdapter#shutdownMapHook()
	 */
	public void shutdownMapHook() {
		setStateIcon(getNode(), false);
		((Registration) getPluginBaseClass()).deregisterMapNode(this);
		super.shutdownMapHook();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.extensions.PermanentNodeHookAdapter#save(freemind.main.XMLElement
	 * )
	 */
	public void save(XMLElement xml) {
		super.save(xml);
		HashMap values = new HashMap();
		values.put(XML_STORAGE_POS_LON, toString(mPosition.getLon()));
		values.put(XML_STORAGE_POS_LAT, toString(mPosition.getLat()));
		values.put(XML_STORAGE_MAP_LON, toString(mMapCenter.getLon()));
		values.put(XML_STORAGE_MAP_LAT, toString(mMapCenter.getLat()));
		values.put(XML_STORAGE_ZOOM, toString(mZoom));
		if (mTileSource != null) {
			values.put(XML_STORAGE_TILE_SOURCE, mTileSource);
		}
		if (Resources.getInstance().getBoolProperty(NODE_MAP_STORE_TOOLTIP)
				&& mImage != null) {
			values.put(XML_STORAGE_MAP_TOOLTIP, mImage.save());
		}
		saveNameValuePairs(values, xml);
	}

	protected void setStateIcon(MindMapNode node, boolean enabled) {
		node.setStateIcon(NODE_MAP_LOCATION_ICON,
				(enabled) ? getMapLocationIcon() : null);
	}

	/**
	 * @param pDouble
	 * @return
	 */
	private String toString(double pDouble) {
		return "" + pDouble;
	}

	/**
	 * @param pInt
	 * @return
	 */
	private String toString(int pInt) {
		return "" + pInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.PermanentNodeHookAdapter#loadFrom(freemind.main.
	 * XMLElement)
	 */
	public void loadFrom(XMLElement pChild) {
		super.loadFrom(pChild);
		HashMap values = loadNameValuePairs(pChild);
		mPosition.setLat(fromString(values.get(XML_STORAGE_POS_LAT)));
		mPosition.setLon(fromString(values.get(XML_STORAGE_POS_LON)));
		mMapCenter.setLat(fromString(values.get(XML_STORAGE_MAP_LAT)));
		mMapCenter.setLon(fromString(values.get(XML_STORAGE_MAP_LON)));
		mZoom = intFromString(values.get(XML_STORAGE_ZOOM));
		// if no value stored, the get method returns null.
		mBase64Image = (String) values.get(XML_STORAGE_MAP_TOOLTIP);
		mTileSource = (String) values.get(XML_STORAGE_TILE_SOURCE);
	}

	/**
	 * @param pObject
	 * @return
	 */
	private double fromString(Object pObject) {
		try {
			return Double.parseDouble((String) pObject);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return 0.0;
		}
	}

	/**
	 * @param pObject
	 * @return
	 */
	private int intFromString(Object pObject) {
		try {
			return Integer.parseInt((String) pObject);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			return 1;
		}
	}

	/**
	 * Set map position. Is undoable.
	 * 
	 * @param pTileSource
	 * 
	 */
	public void changePosition(MapNodePositionHolder pHolder,
			Coordinate pPosition, Coordinate pMapCenter, int pZoom,
			String pTileSource) {
		((Registration) getPluginBaseClass()).changePosition(pHolder,
				pPosition, pMapCenter, pZoom, pTileSource);
	}

	public static interface MapNodePositionListener {
		void registerMapNode(MapNodePositionHolder pMapNodePositionHolder);

		void deregisterMapNode(MapNodePositionHolder pMapNodePositionHolder);
	}

	public Coordinate getPosition() {
		return mPosition;
	}

	/**
	 * @param pTileSource
	 */
	public void setTileSource(String pTileSource) {
		mTileSource = pTileSource;
	}

	/**
	 * @return
	 */
	public String getTileSource() {
		return mTileSource;
	}

	public void setPosition(Coordinate pPosition) {
		mPosition = pPosition;
	}

	public Coordinate getMapCenter() {
		return mMapCenter;
	}

	public void setMapCenter(Coordinate pMapCenter) {
		mMapCenter = pMapCenter;
	}

	public int getZoom() {
		return mZoom;
	}

	public void setZoom(int pZoom) {
		mZoom = pZoom;
	}

	public MindMapNode getNode() {
		return super.getNode();
	}

	/**
	 */
	public static MapNodePositionHolder getHook(MindMapNode node) {
		for (Iterator j = node.getActivatedHooks().iterator(); j.hasNext();) {
			PermanentNodeHook element = (PermanentNodeHook) j.next();
			if (element instanceof MapNodePositionHolder) {
				return (MapNodePositionHolder) element;
			}
		}
		return null;
	}

	public static ImageIcon getMapLocationIcon() {
		// icon
		if (sMapLocationIcon == null) {
			sMapLocationIcon = new ImageIcon(Resources.getInstance()
					.getResource("images/map_location.png"));
		}
		return sMapLocationIcon;
	}

	public void createTooltip() {
		logger.info("Creating tooltip for " + getNode());
		// save image to disk:
		try {
			File tempFile = File
					.createTempFile("node_map_tooltip_", ".png", new File(
							getController().getFrame().getFreemindDirectory()));
			tempFile.deleteOnExit();
			ImageIO.write(mImage.getImage(), "png", tempFile);
			String imageTag = "<img src=\"file://" + tempFile.getAbsolutePath()
					+ "\"/>";
			setToolTip(NODE_MAP_HOOK_NAME, "<html>" + imageTag + "</html>");
		} catch (IOException e) {
			freemind.main.Resources.getInstance().logException(e);

		}
	}

	/**
	 * 
	 */
	public void recreateTooltip() {
		mImage = null;
		mBase64Image = null;
		showTooltip();
	}

}
