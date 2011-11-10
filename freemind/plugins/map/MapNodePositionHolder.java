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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileController;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource.Mapnik;

import freemind.controller.actions.generated.instance.PlaceNodeXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.extensions.PermanentNodeHook;
import freemind.main.Resources;
import freemind.main.XMLElement;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionFactory;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;

/**
 * @author foltin
 * @date 27.10.2011
 */
public class MapNodePositionHolder extends PermanentMindMapNodeHookAdapter
		implements TileLoaderListener {
	public final static String NODE_MAP_HOOK_NAME = "plugins/map/MapNodePositionHolder.properties";
	public final static String NODE_MAP_LOCATION_ICON = "node_map_location_icon";

	private static final String XML_STORAGE_POS_LON = "XML_STORAGE_POS_LON";
	private static final String XML_STORAGE_POS_LAT = "XML_STORAGE_POS_LAT";
	private static final String XML_STORAGE_MAP_LON = "XML_STORAGE_MAP_LON";
	private static final String XML_STORAGE_MAP_LAT = "XML_STORAGE_MAP_LAT";
	private static final String XML_STORAGE_ZOOM = "XML_STORAGE_ZOOM";

	private Coordinate mPosition = new Coordinate(0, 0);
	private Coordinate mMapCenter = new Coordinate(0, 0);
	private int mZoom = 1;
	private static ImageIcon sMapLocationIcon;
	private static int mTemporaryFileCounter = 1;
	private static String mTemporaryFileCounterLock = "";
	private TileImage mImage;

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
		mImage = ((Registration) getPluginBaseClass()).getImage(mPosition,
				mZoom, this);

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
	 */
	public void changePosition(MapNodePositionHolder pHolder,
			Coordinate pPosition, Coordinate pMapCenter, int pZoom) {
		((Registration) getPluginBaseClass()).changePosition(pHolder,
				pPosition, pMapCenter, pZoom);
	}

	public static interface MapNodePositionListener {
		void registerMapNode(MapNodePositionHolder pMapNodePositionHolder);

		void deregisterMapNode(MapNodePositionHolder pMapNodePositionHolder);
	}

	public static class TileImage implements ImageObserver {

		private Tile[][] mTiles = null;
		private boolean mTilesPresent = false;
		private boolean mImageCreated = false;
		private BufferedImage mImage;
		private int mWaitingForCallbacks = 0;
		private int mDx;
		private int mDy;

		public TileImage() {

		}

		public boolean isLoaded() {
			if (!mTilesPresent)
				return false;
			for (int i = 0; i < mTiles.length; i++) {
				Tile[] tiles = mTiles[i];
				for (int j = 0; j < tiles.length; j++) {
					Tile tile = tiles[j];
					if (!tile.isLoaded() && !tile.hasError()) {
						System.out.println("Tile " + tile + " is not loaded:" + tile.getStatus());
						return false;
					}
				}
			}
			if(!mImageCreated) {
				createImage();
				mImageCreated = true;
			}
			return isDrawingDone();
		}

		/**
		 * Is called when all tiles are loaded and creates the common picture.
		 */
		private void createImage() {
			BufferedImage tileImage00 = mTiles[0][0].getImage();
			int height = tileImage00.getHeight();
			int width = tileImage00.getWidth();
			mImage = new BufferedImage(height * mTiles[0].length, width
					* mTiles.length, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) mImage.getGraphics();
			for (int i = 0; i < mTiles.length; i++) {
				Tile[] tiles = mTiles[i];
				for (int j = 0; j < tiles.length; j++) {
					Tile tile = tiles[j];
					boolean done = graphics.drawImage(tile.getImage(), i*height, j*width, this);
					if(!done) {
						mWaitingForCallbacks++;
					}
				}
			}
			if(isDrawingDone()) {
				drawCross();
			}
		}

		public boolean isDrawingDone() {
			return mWaitingForCallbacks<=0;
		}

		public void drawCross() {
			System.out.println("Drawing cross");
			Graphics2D graphics = (Graphics2D) mImage.getGraphics();
			graphics.setColor(Color.RED);
			graphics.setStroke(new BasicStroke(4));
			int size = 10;
			graphics.drawLine(mDx-size, mDy, mDx+size, mDy);
			graphics.drawLine(mDx, mDy-size, mDx, mDy+size);
		}

		/**
		 * @return
		 */
		public RenderedImage getImage() {
			return mImage;
		}

		/**
		 * @param pDimension
		 * @param pX
		 * @param pY
		 * @param pZoom
		 * @param mTileController
		 * @param pLogger
		 * @param pDy 
		 * @param pDx 
		 */
		public void setTiles(int pDimension, int pX, int pY, int pZoom,
				TileController mTileController, Logger pLogger, int pDx, int pDy) {
			mDx = pDx;
			mDy = pDy;
			mTiles = new Tile[pDimension][pDimension];
			for (int i = 0; i < pDimension; ++i) {
				for (int j = 0; j < pDimension; ++j) {
					pLogger.info("Trying to load tile to x=" + (pX+i) + ", y=" + (pY+j)
							+ ", zoom=" + pZoom);
					mTiles[i][j] = mTileController.getTile(pX + i, pY + j,
							pZoom);
				}
			}
			mTilesPresent = true;
		}

		/* (non-Javadoc)
		 * @see java.awt.image.ImageObserver#imageUpdate(java.awt.Image, int, int, int, int, int)
		 */
		public boolean imageUpdate(Image pImg, int pInfoflags, int pX, int pY,
				int pWidth, int pHeight) {
			mWaitingForCallbacks--;
			if(isDrawingDone()) {
				drawCross();
			}
			return isDrawingDone();
		}

	}

	public static class Registration extends Thread implements
			HookRegistration, ActorXml, TileLoaderListener {

		private static final String PLUGINS_MAP_NODE_POSITION = MapNodePositionHolder.class
				.getName();

		/*
		 * Collects MapNodePositionHolder. This is necessary to be able to
		 * display them all efficiently.
		 */
		private HashSet/* MapNodePositionHolder s */mMapNodePositionHolders = new HashSet();

		private HashSet mMapNodePositionListeners = new HashSet();

		private HashMap mTileLoaderListeners = new HashMap();

		private final MindMapController controller;

		private final MindMap mMap;

		private final java.util.logging.Logger logger;

		private Mapnik mTileSource;

		private TileController mTileController;

		private FileTileCache mTileCache;

		private boolean mStopMe = false;

		public Registration(ModeController controller, MindMap map) {
			this.controller = (MindMapController) controller;
			mMap = map;
			logger = controller.getFrame().getLogger(this.getClass().getName());
		}

		/**
		 * @param pPosition
		 */
		public TileImage getImage(Coordinate pPosition, int pZoom,
				TileLoaderListener pTileListener) {
			int tileSize = mTileSource.getTileSize();
			int exactx = OsmMercator.LonToX(pPosition.getLon(), pZoom);
			int exacty = OsmMercator.LatToY(pPosition.getLat(), pZoom);
			int x = exactx / tileSize;
			int y = exacty / tileSize;
			// determine other surrounding tiles that are close to the exact
			// point.
			int dx = exactx % tileSize;
			int dy = exacty % tileSize;
			// determine quadrant of cursor in tile:
			if (dx < tileSize / 2) {
				x -= 1;
				dx += tileSize;
			}
			if (dy < tileSize / 2) {
				y -= 1;
				dy += tileSize;
			}
			TileImage tileImage = new TileImage();
			synchronized (mTileLoaderListeners) {
				mTileLoaderListeners.put(tileImage, pTileListener);
			}
			tileImage.setTiles(2, x, y, pZoom, mTileController, logger, dx, dy);
			return tileImage;
		}

		public void deRegister() {
			controller.getActionFactory().deregisterActor(getDoActionClass());
			this.mStopMe = true;
		}

		public void register() {
			controller.getActionFactory().registerActor(this,
					getDoActionClass());
			mTileSource = new OsmTileSource.Mapnik();
			mTileCache = new FileTileCache();
			mTileController = new TileController(mTileSource, mTileCache, this);
			this.start();
		}

		public void registerMapNode(MapNodePositionHolder pMapNodePositionHolder) {
			mMapNodePositionHolders.add(pMapNodePositionHolder);
			for (Iterator it = mMapNodePositionListeners.iterator(); it
					.hasNext();) {
				MapNodePositionListener listener = (MapNodePositionListener) it
						.next();
				try {
					listener.registerMapNode(pMapNodePositionHolder);
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		}

		public HashSet getMapNodePositionHolders() {
			return mMapNodePositionHolders;
		}

		public void deregisterMapNode(
				MapNodePositionHolder pMapNodePositionHolder) {
			mMapNodePositionHolders.remove(pMapNodePositionHolder);
			for (Iterator it = mMapNodePositionListeners.iterator(); it
					.hasNext();) {
				MapNodePositionListener listener = (MapNodePositionListener) it
						.next();
				try {
					listener.deregisterMapNode(pMapNodePositionHolder);
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		}

		public void registerMapNodePositionListener(
				MapNodePositionListener pMapNodePositionListener) {
			mMapNodePositionListeners.add(pMapNodePositionListener);
		}

		public void deregisterMapNodePositionListener(
				MapNodePositionListener pMapNodePositionListener) {
			mMapNodePositionListeners.remove(pMapNodePositionListener);
		}

		/**
		 * Set map position. Is undoable.
		 * 
		 */
		public void changePosition(MapNodePositionHolder pHolder,
				Coordinate pPosition, Coordinate pMapCenter, int pZoom) {
			MindMapNode node = pHolder.getNode();
			PlaceNodeXmlAction doAction = createPlaceNodeXmlActionAction(node,
					pPosition, pMapCenter, pZoom);
			PlaceNodeXmlAction undoAction = createPlaceNodeXmlActionAction(
					node, pHolder.getPosition(), pHolder.getMapCenter(),
					pHolder.getZoom());
			ActionFactory actionFactory = controller.getActionFactory();
			actionFactory.startTransaction(PLUGINS_MAP_NODE_POSITION);
			actionFactory.executeAction(new ActionPair(doAction, undoAction));
			actionFactory.endTransaction(PLUGINS_MAP_NODE_POSITION);
		}

		/**
		 * @param pNode
		 * @param pPosition
		 * @param pMapCenter
		 * @param pZoom
		 * @return
		 */
		private PlaceNodeXmlAction createPlaceNodeXmlActionAction(
				MindMapNode pNode, Coordinate pPosition, Coordinate pMapCenter,
				int pZoom) {
			logger.info("Setting position of node " + pNode);
			PlaceNodeXmlAction action = new PlaceNodeXmlAction();
			action.setNode(controller.getNodeID(pNode));
			action.setCursorLatitude(pPosition.getLat());
			action.setCursorLongitude(pPosition.getLon());
			action.setMapCenterLatitude(pMapCenter.getLat());
			action.setMapCenterLongitude(pMapCenter.getLon());
			action.setZoom(pZoom);
			return action;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.mindmapmode.actions.xml.ActorXml#act(freemind.controller
		 * .actions.generated.instance.XmlAction)
		 */
		public void act(XmlAction pAction) {
			if (pAction instanceof PlaceNodeXmlAction) {
				PlaceNodeXmlAction placeAction = (PlaceNodeXmlAction) pAction;
				MindMapNode node = controller.getNodeFromID(placeAction
						.getNode());
				MapNodePositionHolder hook = getHook(node);
				if (hook != null) {
					hook.setMapCenter(new Coordinate(placeAction
							.getMapCenterLatitude(), placeAction
							.getMapCenterLongitude()));
					hook.setPosition(new Coordinate(placeAction
							.getCursorLatitude(), placeAction
							.getCursorLongitude()));
					hook.setZoom(placeAction.getZoom());
					// TODO: Only, if values really changed.
					controller.nodeChanged(node);
				} else {
					throw new IllegalArgumentException(
							"MapNodePositionHolder to node id "
									+ placeAction.getNode() + " not found.");
				}

			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.modes.mindmapmode.actions.xml.ActorXml#getDoActionClass()
		 */
		public Class getDoActionClass() {
			return PlaceNodeXmlAction.class;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener#
		 * tileLoadingFinished(org.openstreetmap.gui.jmapviewer.Tile, boolean)
		 */
		public void run() {
			while (!mStopMe) {
				logger.info("Looking for tiles "  + mTileLoaderListeners.size());
				synchronized (mTileLoaderListeners) {
					for (Iterator it = mTileLoaderListeners.entrySet()
							.iterator(); it.hasNext();) {
						Entry entry = (Entry) it.next();
						TileImage tileImage = (TileImage) entry.getKey();
						logger.info("TileImage " + tileImage + " is loaded " + tileImage.isLoaded());
						if (tileImage.isLoaded()) {
							((TileLoaderListener) entry.getValue())
									.tileLoadingFinished(null, true);
							it.remove();
						}
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					freemind.main.Resources.getInstance().logException(e);
					
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener#
		 * getTileCache()
		 */
		public TileCache getTileCache() {
			return mTileCache;
		}

		/* (non-Javadoc)
		 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener#tileLoadingFinished(org.openstreetmap.gui.jmapviewer.Tile, boolean)
		 */
		public void tileLoadingFinished(Tile pTile, boolean pSuccess) {
			// TODO Auto-generated method stub
			
		}

	}

	public Coordinate getPosition() {
		return mPosition;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener#
	 * tileLoadingFinished(org.openstreetmap.gui.jmapviewer.Tile, boolean)
	 */
	public void tileLoadingFinished(Tile pTile, boolean pSuccess) {
		logger.info("Creating tooltip for " + getNode());
		// save image to disk:
		
		String filePath;
		synchronized (mTemporaryFileCounterLock) {
			filePath = "/tmp/myfile" + mTemporaryFileCounter + ".png";
			mTemporaryFileCounter++;
		}
		try {
			ImageIO.write(mImage.getImage(), "png", new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			freemind.main.Resources.getInstance().logException(e);

		}

		String imageTag = "<img src=\"file://" + filePath + "\"/>";
		setToolTip(NODE_MAP_HOOK_NAME, "<html>" + imageTag + "</html>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener#getTileCache
	 * ()
	 */
	public TileCache getTileCache() {
		// TODO Auto-generated method stub
		return null;
	}

}
