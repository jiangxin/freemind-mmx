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
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.TileController;
import org.openstreetmap.gui.jmapviewer.interfaces.TileCache;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import plugins.map.MapNodePositionHolder.MapNodePositionListener;
import freemind.common.BooleanProperty;
import freemind.common.DontShowNotificationProperty;
import freemind.common.SeparatorProperty;
import freemind.common.TextTranslator;
import freemind.controller.MenuItemEnabledListener;
import freemind.controller.actions.generated.instance.PlaceNodeXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookRegistration;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain.VersionInformation;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.Tools.IntHolder;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.actions.xml.ActionFactory;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import freemind.modes.mindmapmode.actions.xml.ActorXml;
import freemind.preferences.FreemindPropertyContributor;
import freemind.preferences.layout.OptionPanel;

public class Registration implements HookRegistration, ActorXml,
		TileLoaderListener, MenuItemEnabledListener {

	/**
	 * 
	 * Clean the file cache periodically.
	 * 
	 * @author foltin
	 * @date 27.04.2012
	 */
	public class CachePurger extends TimerTask {

		/**
		 * @author foltin
		 * @date 27.04.2012
		 */
		private final class AgeFilter implements FileFilter {
			private final long mYoungestFileToAccept;

			public AgeFilter(long pYoungestFileToAccept) {
				mYoungestFileToAccept = pYoungestFileToAccept;
			}

			public boolean accept(File pPathname) {
				return pPathname.getName().endsWith(".tags")
						&& pPathname.lastModified() <= mYoungestFileToAccept;
			}
		}

		private final File mCacheDirectory;
		private final long mCacheMaxAge;

		/**
		 * @param pCacheDirectory
		 * @param pCacheMaxAge
		 */
		public CachePurger(File pCacheDirectory, long pCacheMaxAge) {
			mCacheDirectory = pCacheDirectory;
			mCacheMaxAge = pCacheMaxAge;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		public void run() {
			// the jobs must not overtake themselves.
			synchronized (mCachePurgerSemaphore) {
				if (mCachePurgerSemaphore.getValue() > 0) {
					return;
				}
				mCachePurgerSemaphore.setValue(1);
			}
			try {
				logger.info("Start purging for " + mCacheDirectory);
				if (mCacheDirectory.exists()) {
					File[] cacheDirectories = mCacheDirectory.listFiles();
					for (int i = 0; i < cacheDirectories.length; i++) {
						File cacheDirectory = cacheDirectories[i];
						purgeDirectory(cacheDirectory);

					}
				}
				logger.info("Finished purging");
			} finally {
				mCachePurgerSemaphore.setValue(0);
			}
		}

		/**
		 * @param pCacheDirectory
		 */
		private void purgeDirectory(File pCacheDirectory) {
			logger.fine("Start purging for subdir " + pCacheDirectory);
			File[] listTagFiles = pCacheDirectory.listFiles(new AgeFilter(
					System.currentTimeMillis() - mCacheMaxAge));
			if (listTagFiles == null) {
				return;
			}
			for (int i = 0; i < listTagFiles.length; i++) {
				File tagFile = listTagFiles[i];
				File imageFile = new File(tagFile.getPath().replace(".tags",
						".png"));
				try {
					logger.finest("Deleting " + tagFile);
					logger.finest("Deleting " + imageFile);
					tagFile.delete();
					imageFile.delete();
				} catch (Exception e) {
					freemind.main.Resources.getInstance().logException(e);
				}
			}
		}

	}

	private static final String PLUGINS_MAP_NODE_POSITION = MapNodePositionHolder.class
			.getName();

	/*
	 * Collects MapNodePositionHolder. This is necessary to be able to display
	 * them all efficiently.
	 */
	private HashSet/* MapNodePositionHolder s */mMapNodePositionHolders = new HashSet();

	private HashSet mMapNodePositionListeners = new HashSet();

	private final MindMapController controller;

	private final MindMap mMap;

	private final java.util.logging.Logger logger;

	private TileSource mTileSource;

	private TileController mTileController;

	private MemoryTileCache mTileCache;

	private MapDialog mMapDialog = null;

	private MapDialogPropertyContributor mOptionContributor;

	private static Timer sTimer;

	private static Boolean sTimerSemaphore = new Boolean(false);

	private IntHolder mCachePurgerSemaphore = new IntHolder(0);

	private static final class MapDialogPropertyContributor implements
			FreemindPropertyContributor {

		private final MindMapController modeController;

		public MapDialogPropertyContributor(MindMapController modeController) {
			this.modeController = modeController;
		}

		public List getControls(TextTranslator pTextTranslator) {
			Vector controls = new Vector();
			controls.add(new OptionPanel.NewTabProperty(
					"plugins/map/MapDialog.properties_MapDialogTabName"));
			controls.add(new SeparatorProperty(
					"plugins/map/MapDialog.properties_PatternSeparatorName"));
			controls.add(new BooleanProperty("node_map_show_tooltip.tooltip",
					"node_map_show_tooltip"));
			controls.add(new DontShowNotificationProperty(
					"resources_search_for_node_text_without_question.tooltip",
					FreeMind.RESOURCES_SEARCH_FOR_NODE_TEXT_WITHOUT_QUESTION));
			return controls;
		}
	}

	public Registration(ModeController controller, MindMap map) {
		this.controller = (MindMapController) controller;
		mMap = map;
		logger = controller.getFrame().getLogger(this.getClass().getName());
		mTileSource = new OsmTileSource.Mapnik();
		mTileCache = new MemoryTileCache();
		mTileController = new TileController(mTileSource, mTileCache, this);
		mTileController.setTileLoader(createTileLoader(this));
		mOptionContributor = new MapDialogPropertyContributor(this.controller);

		synchronized (sTimerSemaphore) {
			if (sTimer == null) {
				// only once in the system
				sTimer = new Timer();
				long purgeTime = Resources.getInstance().getLongProperty(
						MapDialog.TILE_CACHE_PURGE_TIME,
						MapDialog.TILE_CACHE_PURGE_TIME_DEFAULT);
				sTimer.schedule(new CachePurger(getCacheDirectory(),
						getCacheMaxAge()), purgeTime, purgeTime);
			}
		}

	}

	/**
	 * @param pPosition
	 * @param pTileSource
	 */
	public TileImage getImageForTooltip(Coordinate pPosition, int pZoom,
			String pTileSource) {
		TileSource tileSource = FreeMindMapController.changeTileSource(
				pTileSource, null);
		if (tileSource != null) {
			mTileSource = tileSource;
			mTileController.setTileSource(tileSource);
		}
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
		tileImage.setTiles(2, x, y, pZoom, mTileController, logger, dx, dy);
		// wait for tiles:
		int timeout = 60;
		while (timeout-- > 0) {
			try {
				if (tileImage.isLoaded() || tileImage.hasErrors()) {
					break;
				}
				Thread.sleep(100);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
		return tileImage;
	}

	public void deRegister() {
		OptionPanel.removeContributor(mOptionContributor);
		controller.getActionFactory().deregisterActor(getDoActionClass());
	}

	public void register() {
		OptionPanel.addContributor(mOptionContributor);
		controller.getActionFactory().registerActor(this, getDoActionClass());
	}

	public void registerMapNode(MapNodePositionHolder pMapNodePositionHolder) {
		mMapNodePositionHolders.add(pMapNodePositionHolder);
		for (Iterator it = mMapNodePositionListeners.iterator(); it.hasNext();) {
			MapNodePositionListener listener = (MapNodePositionListener) it
					.next();
			try {
				listener.registerMapNode(pMapNodePositionHolder);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}

	public Set getMapNodePositionHolders() {
		return Collections.unmodifiableSet(mMapNodePositionHolders);
	}

	public void deregisterMapNode(MapNodePositionHolder pMapNodePositionHolder) {
		mMapNodePositionHolders.remove(pMapNodePositionHolder);
		for (Iterator it = mMapNodePositionListeners.iterator(); it.hasNext();) {
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

	public OsmTileLoader createTileLoader(TileLoaderListener mMap) {
		OsmTileLoader loader = null;
		String tileCacheClass = Resources.getInstance().getProperty(
				MapDialog.TILE_CACHE_CLASS);
		if (Tools.safeEquals(tileCacheClass, "file")) {
			File cacheDir = getCacheDirectory();
			try {
				OsmFileCacheTileLoader osmFileCacheTileLoader = new OsmFileCacheTileLoader(
						mMap, cacheDir);
				loader = osmFileCacheTileLoader;
				long maxFileAge = getCacheMaxAge();
				logger.info("Setting cache max age to " + maxFileAge
						/ OsmFileCacheTileLoader.FILE_AGE_ONE_DAY + " days.");
				osmFileCacheTileLoader.setCacheMaxFileAge(maxFileAge);
			} catch (Exception e1) {
				freemind.main.Resources.getInstance().logException(e1);
			}
		}
		if (loader == null) {
			logger.info("Using osm tile loader");
			loader = new OsmTileLoader(mMap);
		}
		VersionInformation freemindVersion = controller.getFrame()
				.getFreemindVersion();
		loader.headers.put("User-agent", "FreeMind " + freemindVersion);
		return loader;
	}

	protected long getCacheMaxAge() {
		long maxFileAge = Resources.getInstance().getLongProperty(
				MapDialog.TILE_CACHE_MAX_AGE,
				OsmFileCacheTileLoader.FILE_AGE_ONE_WEEK);
		return maxFileAge;
	}

	protected File getCacheDirectory() {
		String directory = Resources.getInstance().getProperty(
				MapDialog.FILE_TILE_CACHE_DIRECTORY);
		if (directory.startsWith("%/")) {
			directory = Resources.getInstance().getFreemindDirectory()
					+ File.separator + directory.substring(2);
		}
		File cacheDir = new File(directory);
		logger.info("Trying to use file cache tile loader with dir "
				+ directory);
		return cacheDir;
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
		MindMapNode node = pHolder.getNode();
		PlaceNodeXmlAction doAction = createPlaceNodeXmlActionAction(node,
				pPosition, pMapCenter, pZoom, pTileSource);
		PlaceNodeXmlAction undoAction = createPlaceNodeXmlActionAction(node,
				pHolder.getPosition(), pHolder.getMapCenter(),
				pHolder.getZoom(), pHolder.getTileSource());
		ActionFactory actionFactory = controller.getActionFactory();
		actionFactory.doTransaction(PLUGINS_MAP_NODE_POSITION, new ActionPair(
				doAction, undoAction));
	}

	/**
	 * @param pNode
	 * @param pPosition
	 * @param pMapCenter
	 * @param pZoom
	 * @param pTileSource
	 * @return
	 */
	private PlaceNodeXmlAction createPlaceNodeXmlActionAction(
			MindMapNode pNode, Coordinate pPosition, Coordinate pMapCenter,
			int pZoom, String pTileSource) {
		logger.info("Setting position of node " + pNode);
		PlaceNodeXmlAction action = new PlaceNodeXmlAction();
		action.setNode(controller.getNodeID(pNode));
		action.setCursorLatitude(pPosition.getLat());
		action.setCursorLongitude(pPosition.getLon());
		action.setMapCenterLatitude(pMapCenter.getLat());
		action.setMapCenterLongitude(pMapCenter.getLon());
		action.setZoom(pZoom);
		action.setTileSource(pTileSource);
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
			MindMapNode node = controller.getNodeFromID(placeAction.getNode());
			MapNodePositionHolder hook = MapNodePositionHolder.getHook(node);
			if (hook != null) {
				hook.setMapCenter(new Coordinate(placeAction
						.getMapCenterLatitude(), placeAction
						.getMapCenterLongitude()));
				hook.setPosition(new Coordinate(
						placeAction.getCursorLatitude(), placeAction
								.getCursorLongitude()));
				hook.setZoom(placeAction.getZoom());
				hook.setTileSource(placeAction.getTileSource());
				hook.recreateTooltip();
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
	 * @see freemind.modes.mindmapmode.actions.xml.ActorXml#getDoActionClass()
	 */
	public Class getDoActionClass() {
		return PlaceNodeXmlAction.class;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener#
	 * tileLoadingFinished(org.openstreetmap.gui.jmapviewer.Tile, boolean)
	 */
	public void tileLoadingFinished(Tile pTile, boolean pSuccess) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.MenuItemEnabledListener#isEnabled(javax.swing.JMenuItem
	 * , javax.swing.Action)
	 */
	public boolean isEnabled(JMenuItem pItem, Action pAction) {
		String hookName = ((NodeHookAction) pAction).getHookName();
		logger.fine("Enabled for " + hookName);
		if (SearchInMapForNodeTextAction.NODE_CONTEXT_PLUGIN_NAME
				.equals(hookName)) {
			return true;
		}
		if (ShowMapToNodeAction.NODE_CONTEXT_PLUGIN_NAME.equals(hookName)
				|| AddLinkToMapAction.NODE_CONTEXT_PLUGIN_NAME.equals(hookName)
				|| RemoveMapToNodeAction.NODE_CONTEXT_PLUGIN_NAME
						.equals(hookName)
				|| AddMapImageToNodeAction.NODE_CONTEXT_PLUGIN_NAME
						.equals(hookName)) {
			for (Iterator it = controller.getSelecteds().iterator(); it
					.hasNext();) {
				MindMapNode node = (MindMapNode) it.next();
				MapNodePositionHolder hook = MapNodePositionHolder
						.getHook(node);
				if (hook != null) {
					return true;
				}
			}
		}
		return false;
	}

	public MapDialog getMapDialog() {
		return mMapDialog;
	}

	public void setMapDialog(MapDialog pMapDialog) {
		mMapDialog = pMapDialog;
	}

	public interface NodeVisibilityListener {
		void nodeVisibilityChanged(
				MapNodePositionHolder pMapNodePositionHolder, boolean pVisible);

	}

	private HashSet mNodeVisibilityListeners = new HashSet();

	public void registerNodeVisibilityListener(
			NodeVisibilityListener pNodeVisibilityListener) {
		mNodeVisibilityListeners.add(pNodeVisibilityListener);
	}

	public void deregisterNodeVisibilityListener(
			NodeVisibilityListener pNodeVisibilityListener) {
		mNodeVisibilityListeners.remove(pNodeVisibilityListener);
	}

	/**
	 * @param pVisible
	 *            is true, when a node is visible now.
	 * @param pMapNodePositionHolder
	 */
	public void fireNodeVisibilityChanged(boolean pVisible,
			MapNodePositionHolder pMapNodePositionHolder) {
		for (Iterator it = mNodeVisibilityListeners.iterator(); it.hasNext();) {
			NodeVisibilityListener listener = (NodeVisibilityListener) it
					.next();
			try {
				listener.nodeVisibilityChanged(pMapNodePositionHolder, pVisible);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}

}
