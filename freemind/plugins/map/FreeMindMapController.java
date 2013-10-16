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

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import freemind.common.FreeMindTask;
import freemind.common.XmlBindingTools;
import freemind.controller.MenuItemEnabledListener;
import freemind.controller.MenuItemSelectedListener;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.Place;
import freemind.controller.actions.generated.instance.Result;
import freemind.controller.actions.generated.instance.Reversegeocode;
import freemind.controller.actions.generated.instance.Searchresults;
import freemind.extensions.ExportHook;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.common.plugins.MapNodePositionHolderBase;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.EditNodeBase;
import freemind.view.mindmapview.EditNodeTextField;
import freemind.view.mindmapview.NodeView;

/**
 * Default map controller which implements map moving by pressing the right
 * mouse button and zooming by double click or by mouse wheel.
 * 
 * @author Jan Peter Stotz
 * 
 *         FreeMind Extensions: - Move with button 1 (consistency with FreeMind
 *         UI) OK - Single click for Set Cursor OK - Mouse Wheel: Zoom OK -
 *         Control-Mouse Wheel: ? - (Right click +) Menu: popup menu mit * If
 *         right click, then the cursor is set to that position (consistency
 *         with FM-UI) * Place node(s) ==> the node gets a
 *         {@link MapMarkerLocation} here. The position, the position of the map
 *         and the zoom is stored in the node. *
 * 
 *         Node Extra Menu Items: * Show node(s) in Map ==> Chooses the best
 *         view for the nodes and selects them.
 * 
 * 
 *         FIXME: On undo place node, the position is gone. (Undo action
 *         contains the initial zeros, I guess).
 */
public class FreeMindMapController extends JMapController implements
		MouseListener, MouseMotionListener, MouseWheelListener, ActionListener,
		KeyListener {
	/**
	 * @author foltin
	 * @date 27.07.2012
	 */
	public interface CursorPositionListener {
		void cursorPositionChanged(Coordinate pCursorPosition);
	}

	/**
	 * 
	 */
	private static final int MODIFIERS_WITHOUT_SHIFT = Integer.MAX_VALUE
			^ KeyEvent.SHIFT_MASK;

	private static final String NODE_MAP_HOME_PROPERTY = "node_map_home";

	private static final String MAP_DIALOG_PROGRESS_MESSAGE = "MapDialog.progressMessage";

	private static final String MAP_DIALOG_ADD_PLACES = "MapDialog.addPlaces";

	private static final String XML_VERSION_1_0_ENCODING_UTF_8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

	private static final int MOUSE_BUTTONS_MASK = MouseEvent.BUTTON3_DOWN_MASK
			| MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK;

	private static final int MAC_MOUSE_BUTTON3_MASK = MouseEvent.CTRL_DOWN_MASK
			| MouseEvent.BUTTON1_DOWN_MASK;
	private static final int MAC_MOUSE_BUTTON1_MASK = MouseEvent.BUTTON1_DOWN_MASK;

	private static final int SCROLL_MARGIN = 5;

	private static final int SCROLL_PIXEL_AMOUNT = 25;

	private static final String OSM_NOMINATIM_CONNECT_TIMEOUT_IN_MS = "osm_nominatim_connect_timeout_in_ms";

	private static final String OSM_NOMINATIM_READ_TIMEOUT_IN_MS = "osm_nominatim_read_timeout_in_ms";

	private static final int MOVE_PIXEL_AMOUNT = 50;

	private static final float PAGE_DOWN_FACTOR = 0.85f;

	private static final int POSITION_HOLDER_LIMIT = 50;

	private static final long WHEEL_ZOOM_MINIMAL_TIME_BETWEEN_CHANGES = 333;

	protected static java.util.logging.Logger logger = freemind.main.Resources
			.getInstance().getLogger("plugins.map.FreeMindMapController");

	private JPopupMenu mPopupMenu = new JPopupMenu();

	private final MindMapController mMindMapController;

	private final JDialog mMapDialog;

	private final MapDialog mMapHook;

	private Point lastDragPoint;
	private Point mDragStartingPoint;

	private boolean mMovementEnabled = true;

	private boolean isMoving = false;

	private boolean mClickEnabled = true;

	private int movementMouseButton = MouseEvent.BUTTON1;
	private int movementMouseButtonMask = MouseEvent.BUTTON1_DOWN_MASK;

	private boolean mWheelZoomEnabled = true;

	private JPopupMenu mContextPopupMenu;

	private MapNodePositionHolder mCurrentPopupPositionHolder;

	private boolean isMapNodeMoving = false;

	private MapNodePositionHolder mMapNodeMovingSource = null;

	private Timer mMouseHitsNodeTimer;

	private boolean mIsRectangularSelect;

	private Coordinate mRectangularStart;

	private Vector mPositionHolderVector = new Vector();
	/**
	 * Marks the index of the current position or -1 if none.
	 */
	private int mPositionHolderIndex = -1;

	public static class TileSourceStore {
		TileSource mTileSource;
		String mLayerName;

		public TileSourceStore(TileSource pTileSource, String pLayerName) {
			super();
			mTileSource = pTileSource;
			mLayerName = pLayerName;
		}

	}

	public static class TransportMap extends AbstractOsmTileSource {

		// http://b.tile2.opencyclemap.org/transport/14/8800/5373.png
		private static final String PATTERN = "http://%s.tile2.opencyclemap.org/transport";

		private static final String[] SERVER = { "a", "b", "c" };

		private int SERVER_NUM = 0;

		public TransportMap() {
			super("OSM Transport Map", PATTERN);
		}

		public String getBaseUrl() {
			String url = String.format(this.baseUrl,
					new Object[] { SERVER[SERVER_NUM] });
			SERVER_NUM = (SERVER_NUM + 1) % SERVER.length;
			return url;
		}

		public int getMaxZoom() {
			return 18;
		}

		public TileUpdate getTileUpdate() {
			return TileUpdate.LastModified;
		}
	}

	public static class MapQuestOpenMap extends AbstractOsmTileSource {

		// http://otile1.mqcdn.com/tiles/1.0.0/osm/14/8800/5374.png
		private static final String PATTERN = "http://otile%s.mqcdn.com/tiles/1.0.0/osm";

		private static final String[] SERVER = { "1", "2", "3", "4" };

		private int SERVER_NUM = 0;

		public MapQuestOpenMap() {
			super("OSM MapQuest.Open Map", PATTERN);
		}

		public String getBaseUrl() {
			String url = String.format(this.baseUrl,
					new Object[] { SERVER[SERVER_NUM] });
			SERVER_NUM = (SERVER_NUM + 1) % SERVER.length;
			return url;
		}

		public int getMaxZoom() {
			return 18;
		}

		public TileUpdate getTileUpdate() {
			return TileUpdate.LastModified;
		}
	}

	private static TileSourceStore[] sTileSources = new TileSourceStore[] {
			new TileSourceStore(new OsmTileSource.Mapnik(),
					MapNodePositionHolderBase.SHORT_MAPNIK),
			new TileSourceStore(new OsmTileSource.CycleMap(),
					MapNodePositionHolderBase.SHORT_CYCLE_MAP),
			new TileSourceStore(new TransportMap(),
					MapNodePositionHolderBase.SHORT_TRANSPORT_MAP),
			new TileSourceStore(new MapQuestOpenMap(),
					MapNodePositionHolderBase.SHORT_MAP_QUEST_OPEN_MAP)
	/* , new BingAerialTileSource() license problems.... */
	};

	private final class MapEditTextFieldControl implements
			EditNodeBase.EditControl {
		private final NodeView mNodeView;
		private final MindMapNode mNewNode;
		private final MindMapNode mTargetNode;
		private boolean mIsEditOfExistingNode;

		private MapEditTextFieldControl(NodeView pNodeView,
				MindMapNode pNewNode, MindMapNode pTargetNode, boolean pIsEditOfExistingNode) {
			mNodeView = pNodeView;
			mNewNode = pNewNode;
			mTargetNode = pTargetNode;
			mIsEditOfExistingNode = pIsEditOfExistingNode;
		}

		public void cancel() {
			if (!mIsEditOfExistingNode) {
				mMindMapController.getView().selectAsTheOnlyOneSelected(
						mNodeView);
				mMindMapController.cut(Tools
						.getVectorWithSingleElement(mNewNode));
				mMindMapController.select(mMindMapController
						.getNodeView(mTargetNode));
			}
			endEdit();
		}

		public void ok(String newText) {
			mMindMapController.setNodeText(mNewNode, newText);
			MapNodePositionHolderBase hook = placeNode(mNewNode);
			endEdit();
		}

		private void endEdit() {
			setMouseControl(true);
			mMindMapController.setBlocked(false);
			mMapDialog.requestFocus();
		}

		public void split(String newText, int position) {
		}
	}

	private final class MapEditNoteTextField extends EditNodeTextField {
		private final Point mPoint;

		private MapEditNoteTextField(NodeView pNode, String pText,
				KeyEvent pFirstEvent, ModeController pController,
				EditControl pEditControl, JComponent pParent, Point pPoint) {
			super(pNode, pText, pFirstEvent, pController, pEditControl,
					pParent, pParent);
			mPoint = pPoint;
		}

		protected void setTextfieldLoaction(Point pMPoint) {
			textfield.setLocation(mPoint);
		}

		protected void addTextfield() {
			// add to front to make it visible over the map.
			mParent.add(textfield);
		}
	}

	/**
	 * @author foltin
	 * @date 16.11.2011
	 */
	public class ChangeTileSource extends AbstractAction implements
			MenuItemSelectedListener {

		private final TileSource mSource;

		/**
		 * @param pSource
		 */
		public ChangeTileSource(TileSource pSource) {
			super(Resources.getInstance().getText(
					"map_ChangeTileSource_" + getTileSourceName(pSource)));
			mSource = pSource;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent pE) {
			map.setTileSource(mSource);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * freemind.controller.MenuItemSelectedListener#isSelected(javax.swing
		 * .JMenuItem, javax.swing.Action)
		 */
		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return getTileSource() == mSource;
		}

	}

	/**
	 * @author foltin
	 * @date 31.10.2011
	 */
	private final class PlaceNodeAction extends AbstractAction {

		public PlaceNodeAction() {
			super(getText("MapControllerPopupDialog.place"),
					MapNodePositionHolder.getMapLocationIcon());
		}

		public void actionPerformed(ActionEvent actionEvent) {
			placeNode(mMindMapController.getSelected());
		}
	}

	/**
	 * @author foltin
	 * @date 31.10.2011
	 */
	private final class ShowNodeAction extends AbstractAction {

		public ShowNodeAction() {
			super(getText("MapControllerPopupDialog.show_nodes"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			showSelectedNodes();
		}
	}

	/**
	 * @author foltin
	 * @date 31.10.2011
	 */
	private abstract class MoveAction extends AbstractAction {

		/**
		 * @param pText
		 */
		public MoveAction(String pText) {
			super(pText);
		}

		public void actionPerformed(ActionEvent actionEvent) {
			if (!searchForNearestNode(false)) {
				searchForNearestNode(true);
			}
		}

		/**
		 * @param alternative
		 * @return true, if a node was found
		 */
		protected boolean searchForNearestNode(boolean alternative) {
			boolean returnValue = false;
			Coordinate cursorPosition = getMap().getCursorPosition();
			// get map marker locations:
			HashSet mapNodePositionHolders = new HashSet(
					mMapHook.getMapNodePositionHolders());
			logger.fine("Before removal " + mapNodePositionHolders.size()
					+ " elements");
			// take only those elements in the correct quadrant (eg. -45° -
			// +45°) which are not identical to the current
			for (Iterator it = mapNodePositionHolders.iterator(); it.hasNext();) {
				MapNodePositionHolder holder = (MapNodePositionHolder) it
						.next();
				Coordinate pointPosition = holder.getPosition();
				boolean inDestinationQuadrant = destinationQuadrantCheck(
						cursorPosition, pointPosition, alternative);
				if (!inDestinationQuadrant
						|| safeEquals(pointPosition, cursorPosition)) {
					it.remove();
				}
			}
			logger.fine("After removal " + mapNodePositionHolders.size()
					+ " elements");
			// now, we have all points on the left angle (eg. -45° to 45°) and
			// search
			// for the nearest
			MapNodePositionHolder nearest = null;
			double distance = Double.MAX_VALUE;
			for (Iterator it = mapNodePositionHolders.iterator(); it.hasNext();) {
				MapNodePositionHolder holder = (MapNodePositionHolder) it
						.next();
				double newDist = dist(holder.getPosition(), cursorPosition);
				logger.fine("Position " + holder + " is " + newDist);
				if (newDist < distance) {
					distance = newDist;
					nearest = holder;
				}
			}
			if (nearest != null) {
				selectNode(nearest.getNode());
				// don't change the zoom
				setCursorPosition(nearest, map.getZoom());
				returnValue = true;
			}
			return returnValue;
		}

		public boolean destinationQuadrantCheck(Coordinate cursorPosition,
				Coordinate pointPosition, boolean alternative) {
			int mapZoomMax = getMaxZoom();
			int x1 = OsmMercator.LonToX(cursorPosition.getLon(), mapZoomMax);
			int y1 = OsmMercator.LatToY(cursorPosition.getLat(), mapZoomMax);
			int x2 = OsmMercator.LonToX(pointPosition.getLon(), mapZoomMax);
			int y2 = OsmMercator.LatToY(pointPosition.getLat(), mapZoomMax);
			return destinationQuadrantCheck(x1, y1, x2, y2, alternative);
		}

		/**
		 * If no point was found from the destinationQuadrantCheck, here,
		 * alternative = true is tried
		 */
		public abstract boolean destinationQuadrantCheck(int x1, int y1,
				int x2, int y2, boolean alternative);

		/**
		 * @param pPointPosition
		 * @param pCursorPosition
		 * @return
		 */
		private boolean safeEquals(Coordinate p1, Coordinate p2) {
			return (p1 != null && p2 != null && p1.getLon() == p2.getLon() && p1
					.getLat() == p2.getLat()) || (p1 == null && p2 == null);
		}

		/**
		 * @param pPosition
		 * @param pCursorPosition
		 * @return
		 */
		private double dist(Coordinate p1, Coordinate p2) {
			return OsmMercator.getDistance(p1.getLat(), p1.getLon(),
					p2.getLat(), p2.getLon());
		}
	}

	private final class MoveLeftAction extends MoveAction {
		/**
		 * 
		 */
		public MoveLeftAction() {
			super(getText("MapControllerPopupDialog.moveLeft"));
		}

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2,
				boolean alternative) {
			if (alternative)
				return x2 < x1;
			return x2 < x1 && Math.abs(y2 - y1) < Math.abs(x2 - x1);
		}

	}

	private final class MoveRightAction extends MoveAction {
		/**
		 * 
		 */
		public MoveRightAction() {
			super(getText("MapControllerPopupDialog.moveRight"));
		}

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2,
				boolean alternative) {
			if (alternative)
				return x2 > x1;
			return x2 > x1 && Math.abs(y2 - y1) < Math.abs(x2 - x1);
		}

	}

	private final class MoveUpAction extends MoveAction {
		/**
		 * 
		 */
		public MoveUpAction() {
			super(getText("MapControllerPopupDialog.moveUp"));
		}

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2,
				boolean alternative) {
			if (alternative)
				return y2 < y1;
			return y2 < y1 && Math.abs(y2 - y1) > Math.abs(x2 - x1);
		}

	}

	private final class MoveDownAction extends MoveAction {
		/**
		 * 
		 */
		public MoveDownAction() {
			super(getText("MapControllerPopupDialog.moveDown"));
		}

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2,
				boolean alternative) {
			if (alternative)
				return y2 > y1;
			return y2 > y1 && Math.abs(y2 - y1) > Math.abs(x2 - x1);
		}

	}

	private final class MoveForwardAction extends AbstractAction implements
			MenuItemEnabledListener {
		public MoveForwardAction() {
			super(getText("MapControllerPopupDialog.moveForward"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (isEnabledCheck()) {
				PositionHolder posHolder = (PositionHolder) getPositionHolderVector()
						.get(getPositionHolderIndex() + 1);
				getMap().setCursorPosition(posHolder.getCoordinate());
				map.setDisplayPositionByLatLon(posHolder.lat, posHolder.lon,
						posHolder.zoom);
				setPositionHolderIndex(getPositionHolderIndex() + 1);
			}
		}

		protected boolean isEnabledCheck() {
			return getPositionHolderIndex() >= 0
					&& getPositionHolderIndex() < getPositionHolderVector()
							.size() - 1;
		}

		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			return isEnabledCheck();
		}

	}

	private final class MoveBackwardAction extends AbstractAction implements
			MenuItemEnabledListener {
		public MoveBackwardAction() {
			super(getText("MapControllerPopupDialog.moveBackward"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (isEnabledCheck()) {
				PositionHolder posHolder = (PositionHolder) getPositionHolderVector()
						.get(getPositionHolderIndex() - 1);
				getMap().setCursorPosition(posHolder.getCoordinate());
				map.setDisplayPositionByLatLon(posHolder.lat, posHolder.lon,
						posHolder.zoom);
				setPositionHolderIndex(getPositionHolderIndex() - 1);
			}
		}

		protected boolean isEnabledCheck() {
			return getPositionHolderIndex() > 0;
		}

		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			return isEnabledCheck();
		}

	}

	public final static class PositionHolder {
		double lat;
		double lon;
		int zoom;

		public PositionHolder(double pLat, double pLon, int pZoom) {
			super();
			lat = pLat;
			lon = pLon;
			zoom = pZoom;
		}

		public String toString() {
			return "PositionHolder [lat=" + lat + ", lon=" + lon + ", zoom="
					+ zoom + "]";
		}

		public Coordinate getCoordinate() {
			return new Coordinate(lat, lon);
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(lat);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(lon);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			result = prime * result + zoom;
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PositionHolder other = (PositionHolder) obj;
			if (Double.doubleToLongBits(lat) != Double
					.doubleToLongBits(other.lat))
				return false;
			if (Double.doubleToLongBits(lon) != Double
					.doubleToLongBits(other.lon))
				return false;
			if (zoom != other.zoom)
				return false;
			return true;
		}

	}

	private final class MoveHomeAction extends AbstractAction implements
			MenuItemEnabledListener {

		public MoveHomeAction() {
			super(getText("MapControllerPopupDialog.MoveHome"));
		}

		public void actionPerformed(ActionEvent pE) {
			PositionHolder posHolder = getPosHolder();
			if (posHolder == null) {
				return;
			}
			setZoom(posHolder.zoom);
			Coordinate coordinates = posHolder.getCoordinate();
			setCursorPosition(coordinates);
		}

		public PositionHolder getPosHolder() {
			try {
				String homeProperty = Resources.getInstance().getProperty(
						NODE_MAP_HOME_PROPERTY);
				if (homeProperty == null || homeProperty.isEmpty()) {
					return null;
				}
				String[] splitResult = homeProperty.split(":");
				if (splitResult.length != 3) {
					return null;
				}
				double lat = Double.parseDouble(splitResult[0]);
				double lon = Double.parseDouble(splitResult[1]);
				int zoom = Integer.parseInt(splitResult[2]);
				return new PositionHolder(lat, lon, zoom);
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
				return null;
			}
		}

		public boolean isEnabled(JMenuItem pItem, Action pAction) {
			return getPosHolder() != null;
		}

	}

	private final class SetHomeAction extends AbstractAction {
		/**
		 * 
		 */
		public SetHomeAction() {
			super(getText("MapControllerPopupDialog.SetHome"));
		}

		public void actionPerformed(ActionEvent pE) {
			Coordinate cursorPosition = getMap().getCursorPosition();
			String propertyValue = cursorPosition.getLat() + ":"
					+ cursorPosition.getLon() + ":" + map.getZoom();
			mMindMapController.getController().setProperty(
					NODE_MAP_HOME_PROPERTY, propertyValue);
		}

	}

	private final class SetDisplayToFitMapMarkers extends AbstractAction {

		public SetDisplayToFitMapMarkers() {
			super(getText("MapControllerPopupDialog.SetDisplayToFitMapMarkers"));
		}

		public void actionPerformed(ActionEvent pE) {
			map.setDisplayToFitMapMarkers();
		}

	}

	private final class ShowMapMarker extends AbstractAction implements
			MenuItemSelectedListener {

		public ShowMapMarker() {
			super(getText("MapControllerPopupDialog.ShowMapMarker"));
		}

		public void actionPerformed(ActionEvent pE) {
			map.setMapMarkerVisible(!map.getMapMarkersVisible());
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return map.getMapMarkersVisible();
		}

	}

	private final class TileGridVisible extends AbstractAction implements
			MenuItemSelectedListener {

		public TileGridVisible() {
			super(getText("MapControllerPopupDialog.TileGridVisible"));
		}

		public void actionPerformed(ActionEvent pE) {
			map.setTileGridVisible(!map.isTileGridVisible());
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return map.isTileGridVisible();
		}

	}

	private final class ZoomControlsVisible extends AbstractAction implements
			MenuItemSelectedListener {

		public ZoomControlsVisible() {
			super(getText("MapControllerPopupDialog.ZoomControlsVisible"));
		}

		public void actionPerformed(ActionEvent pE) {
			map.setZoomContolsVisible(!map.getZoomContolsVisible());
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return map.getZoomContolsVisible();
		}

	}

	private final class HideFoldedNodes extends AbstractAction implements
			MenuItemSelectedListener {

		public HideFoldedNodes() {
			super(getText("MapControllerPopupDialog.HideFoldedNodes"));
		}

		public void actionPerformed(ActionEvent pE) {
			getMap().setHideFoldedNodes(!getMap().isHideFoldedNodes());
			mMapHook.addMarkersToMap();
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return getMap().isHideFoldedNodes();
		}

	}

	private final class SearchControlVisible extends AbstractAction implements
			MenuItemSelectedListener {

		public SearchControlVisible() {
			super(getText("MapControllerPopupDialog.SearchControlVisible"));
		}

		public void actionPerformed(ActionEvent pE) {
			mMapHook.toggleSearchBar();
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return mMapHook.isSearchBarVisible();
		}

	}

	private final class LimitSearchToRegionAction extends AbstractAction
			implements MenuItemSelectedListener {

		public LimitSearchToRegionAction() {
			super(getText("MapControllerPopupDialog.LimitSearchToRegionAction"));
		}

		public void actionPerformed(ActionEvent pE) {
			mMapHook.toggleLimitSearchToRegion();
			mMapHook.focusSearchTerm();
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return mMapHook.isLimitSearchToRegion();
		}

	}

	private final class GotoSearch extends AbstractAction {

		public GotoSearch() {
			super(getText("MapControllerPopupDialog.GotoSearch"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (!mMapHook.isSearchBarVisible()) {
				mMapHook.toggleSearchBar();
			} else {
				mMapHook.focusSearchTerm();
			}
		}
	}

	private final class AddMapPictureToNode extends AbstractAction {

		public AddMapPictureToNode() {
			super(getText("MapControllerPopupDialog.AddMapPictureToNode"));
		}

		public void actionPerformed(ActionEvent pE) {
			addMapPictureToNode();
		}

	}

	private final class NewNodeAction extends AbstractAction {

		public NewNodeAction() {
			super(getText("MapControllerPopupDialog.NewNodeAction"));
		}

		public void actionPerformed(ActionEvent pE) {
			Point pos = getMap().getMapPosition(getMap().getCursorPosition(),
					true);
			MouseEvent e = new MouseEvent(map, 0, 0, 0, pos.x, pos.y, 1, false);
			newNode(e);
		}

	}

	private final class NewNodeReverseLookupAction extends AbstractAction {

		public NewNodeReverseLookupAction() {
			super(
					getText("MapControllerPopupDialog.NewNodeReverseLookupAction"));
		}

		public void actionPerformed(ActionEvent pE) {
			Coordinate pos = getMap().getCursorPosition();
			Reversegeocode reverseLookup = getReverseLookup(pos, getMap()
					.getZoom());
			if (reverseLookup != null) {
				for (Iterator it = reverseLookup.getListResultList().iterator(); it
						.hasNext();) {
					Result result = (Result) it.next();
					addNode(mMindMapController.getSelected(),
							result.getContent(), result.getLat(), result.getLon());
				}
			}
		}

	}

	private final class EditNodeInContextMenu extends AbstractAction {

		public EditNodeInContextMenu() {
			super(getText("MapControllerPopupDialog.EditNodeInContextMenu"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (mCurrentPopupPositionHolder == null) {
				return;
			}
			setCursorPosition(mCurrentPopupPositionHolder.getPosition());
			Point pos = getMap().getMapPosition(
					mCurrentPopupPositionHolder.getPosition(), true);
			// unfold node (and its parents):
			MindMapNode node = mCurrentPopupPositionHolder.getNode();
			while (!node.isRoot()) {
				if (node.isFolded()) {
					mMindMapController.setFolded(node, false);
				}
				node = node.getParentNode();
			}
			pos = MapMarkerLocation.adjustToTextfieldLocation(pos);
			MouseEvent e = new MouseEvent(map, 0, 0, 0, pos.x, pos.y, 1, false);
			editNode(mCurrentPopupPositionHolder, e);
		}

	}

	private final class MaxmimalZoomToCursorAction extends AbstractAction {

		private static final int CURSOR_MAXIMAL_ZOOM_HANDBREAK = 2;

		public MaxmimalZoomToCursorAction() {
			super(
					getText("MapControllerPopupDialog.MaxmimalZoomToCursorAction"));
		}

		public void actionPerformed(ActionEvent pE) {
			Coordinate cursorPosition = getMap().getCursorPosition();
			int zoom = getMaxZoom() - CURSOR_MAXIMAL_ZOOM_HANDBREAK;
			if (getMap().getZoom() >= zoom) {
				zoom += CURSOR_MAXIMAL_ZOOM_HANDBREAK;
			}
			map.setDisplayPositionByLatLon(cursorPosition.getLat(),
					cursorPosition.getLon(), zoom);
		}

	}

	private final class ZoomAction extends AbstractAction {

		private final int mZoomDelta;

		public ZoomAction(int pZoomDelta) {
			super(getText("MapControllerPopupDialog.ZoomAction" + pZoomDelta));
			mZoomDelta = pZoomDelta;
		}

		public void actionPerformed(ActionEvent pE) {
			Coordinate mapCenter = getMap().getPosition();
			int zoom = getMap().getZoom() + mZoomDelta;
			if (zoom < JMapViewer.MIN_ZOOM) {
				zoom = JMapViewer.MIN_ZOOM;
			}
			if (zoom > getMaxZoom()) {
				zoom = getMaxZoom();
			}
			map.setDisplayPositionByLatLon(mapCenter.getLat(),
					mapCenter.getLon(), zoom);
		}

	}

	private final class CopyLinkToClipboardAction extends AbstractAction {

		public CopyLinkToClipboardAction() {
			super(getText("MapControllerPopupDialog.CopyLinkToClipboardAction"));
		}

		public void actionPerformed(ActionEvent pE) {
			String link;
			if (mCurrentPopupPositionHolder != null) {
				link = getLink(mCurrentPopupPositionHolder);
			} else {
				Coordinate cursorPosition = getMap().getCursorPosition();
				Coordinate position = getMap().getPosition();
				int zoom = getMap().getZoom();
				link = getLink(getTileSourceAsString(), cursorPosition,
						position, zoom);
			}
			// Put link into clipboard.
			Tools.getClipboard().setContents(new StringSelection(link), null);
		}

	}

	private final class CopyCoordinatesToClipboardAction extends AbstractAction {

		public CopyCoordinatesToClipboardAction() {
			super(
					getText("MapControllerPopupDialog.CopyCoordinatesToClipboardAction"));
		}

		public void actionPerformed(ActionEvent pE) {
			String coordinates;
			if (mCurrentPopupPositionHolder != null) {
				coordinates = getCoordinates(mCurrentPopupPositionHolder
						.getPosition());
			} else {
				coordinates = getCoordinates(getMap().getCursorPosition());
			}
			// Put Coordinates into clipboard.
			Tools.getClipboard().setContents(new StringSelection(coordinates),
					null);
		}

		/**
		 * @param pCoordinate
		 * @return
		 */
		private String getCoordinates(Coordinate pCoordinate) {
			return pCoordinate.getLat() + " " + pCoordinate.getLon();
		}

	}

	private final class ShowNodeMapInContextMenu extends AbstractAction {

		public ShowNodeMapInContextMenu() {
			super(getText("MapControllerPopupDialog.ShowNodeMapInContextMenu"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (mCurrentPopupPositionHolder != null) {
				showNode(mCurrentPopupPositionHolder);
			}
		}

	}

	private final class SelectNodeInContextMenu extends AbstractAction {

		public SelectNodeInContextMenu() {
			super(getText("MapControllerPopupDialog.SelectNodeInContextMenu"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (mCurrentPopupPositionHolder != null) {
				selectContextMenuNode();
			}
		}

	}

	private final class SelectNodeAndCloseInContextMenu extends AbstractAction {

		public SelectNodeAndCloseInContextMenu() {
			super(
					getText("MapControllerPopupDialog.SelectNodeAndCloseInContextMenu"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (mCurrentPopupPositionHolder != null) {
				selectContextMenuNode();
				mMapHook.disposeDialog();
			}
		}

	}

	private final class RemoveNodeLocationInContextMenu extends AbstractAction {

		public RemoveNodeLocationInContextMenu() {
			super(
					getText("MapControllerPopupDialog.RemoveNodeLocationInContextMenu"));
		}

		public void actionPerformed(ActionEvent pE) {
			if (mCurrentPopupPositionHolder != null) {
				MindMapNode node = mCurrentPopupPositionHolder.getNode();
				removeNodePosition(node);
			}
		}

	}

	private final class ExportMapAction extends AbstractAction {

		public ExportMapAction() {
			super(getText("MapControllerPopupDialog.ExportMapMenu"));
		}

		public void actionPerformed(ActionEvent pE) {
			File chosenFile = ExportHook.chooseImageFile("png",
					getText("Portable_Network_Graphic"), null,
					mMindMapController);
			if (chosenFile == null) {
				return;
			}
			boolean zoomContolsVisible = map.getZoomContolsVisible();
			try {
				mMindMapController.getFrame().setWaitingCursor(true);
				map.setZoomContolsVisible(false);
				// Create an image containing the map:
				BufferedImage myImage = (BufferedImage) map.createImage(
						map.getWidth(), map.getHeight());
				map.print(myImage.getGraphics());
				FileOutputStream out = new FileOutputStream(chosenFile);
				ImageIO.write(myImage, "png", out);
				out.close();
			} catch (IOException e1) {
				freemind.main.Resources.getInstance().logException(e1);
			}
			map.setZoomContolsVisible(zoomContolsVisible);
			mMindMapController.getFrame().setWaitingCursor(false);
			return;

		}

	}

	JCursorMapViewer getMap() {
		return (JCursorMapViewer) map;
	}

	/**
	 * 
	 */
	public void addMapPictureToNode() {
		if (mCurrentPopupPositionHolder == null) {
			// strange.
			return;
		}
		addPictureToNode(mCurrentPopupPositionHolder, mMindMapController);
	}

	public static void addPictureToNode(MapNodePositionHolder positionHolder,
			MindMapController mindMapController) {
		// create picture if not present:
		File tooltipFile = positionHolder.getTooltipFile(true);
		if (!tooltipFile.exists()) {
			if (!positionHolder.createToolTip(true)) {
				// an error occurred, sorry.
				return;
			}
		}
		MindMapNode selected = positionHolder.getNode();
		MindMapNode addNewNode = mindMapController.addNewNode(selected, 0,
				selected.isLeft());
		mindMapController
				.setNodeText(addNewNode, positionHolder.getImageHtml());
	}

	public FreeMindMapController(JMapViewer map,
			MindMapController pMindMapController, final JDialog pMapDialog,
			MapDialog pMapHook) {
		super(map);
		mMapHook = pMapHook;
		mMindMapController = pMindMapController;
		mMapDialog = pMapDialog;
		mMouseHitsNodeTimer = new Timer(500, this);
		mMouseHitsNodeTimer.setRepeats(false);
		Action placeAction = new PlaceNodeAction();
		Action showAction = new ShowNodeAction();
		mZoomInAction = new ZoomAction(1);
		mZoomOutAction = new ZoomAction(-1);
		Action setDisplayToFitMapMarkers = new SetDisplayToFitMapMarkers();
		Action showMapMarker = new ShowMapMarker();
		Action tileGridVisible = new TileGridVisible();
		Action zoomControlsVisible = new ZoomControlsVisible();
		Action searchControlVisible = new SearchControlVisible();
		Action gotoSearch = new GotoSearch();
		Action hideFoldedNodes = new HideFoldedNodes();
		Action newNodeAction = new NewNodeAction();
//		Action newNodeReverseLookupAction = new NewNodeReverseLookupAction();
		Action maxmimalZoomToCursorAction = new MaxmimalZoomToCursorAction();
		Action copyLinkToClipboardAction = new CopyLinkToClipboardAction();
		Action copyCoordinatesToClipboardAction = new CopyCoordinatesToClipboardAction();
		Action exportAction = new ExportMapAction();
		/** Menu **/
		StructuredMenuHolder menuHolder = new StructuredMenuHolder();
		mMenuBar = new JMenuBar();
		JMenu mainItem = new JMenu(getText("MapControllerPopupDialog.Actions"));
		menuHolder.addMenu(mainItem, "main/actions/.");
		addAccelerator(menuHolder.addAction(placeAction, "main/actions/place"),
				"keystroke_plugins/map/MapDialog_Place");
		menuHolder.addAction(exportAction, "main/actions/exportPng");
		addAccelerator(menuHolder.addAction(pMapHook.getCloseAction(),
				"main/actions/close"), "keystroke_plugins/map/MapDialog_Close");

		JMenu searchItem = new JMenu(getText("MapControllerPopupDialog.Search"));
		menuHolder.addMenu(searchItem, "main/search/.");
		addAccelerator(menuHolder.addAction(searchControlVisible,
				"main/search/showSearchControl"),
				"keystroke_plugins/map/MapDialog_toggle_search");
		addAccelerator(
				menuHolder.addAction(gotoSearch, "main/search/gotoSearch"),
				"keystroke_plugins/map/MapDialog_goto_search");
		addAccelerator(menuHolder.addAction(new LimitSearchToRegionAction(),
				"main/search/limitSearchToRegion"),
				"keystroke_plugins/map/MapDialog_limitSearchToRegion");
		JMenu viewItem = new JMenu(getText("MapControllerPopupDialog.Views"));
		menuHolder.addMenu(viewItem, "main/view/.");
		menuHolder.addAction(showAction, "main/view/showNode");
		menuHolder.addAction(setDisplayToFitMapMarkers,
				"main/view/setDisplayToFitMapMarkers");
		menuHolder.addSeparator("main/view/");
		for (int i = 0; i < sTileSources.length; i++) {
			TileSource source = sTileSources[i].mTileSource;
			addAccelerator(menuHolder.addAction(new ChangeTileSource(source),
					"main/view/" + i),
					"keystroke_plugins/map/MapDialog_tileSource_" + i);
		}
		menuHolder.addSeparator("main/view/");
		menuHolder.addAction(showMapMarker, "main/view/showMapMarker");
		menuHolder.addAction(tileGridVisible, "main/view/tileGridVisible");
		menuHolder.addAction(zoomControlsVisible,
				"main/view/zoomControlsVisible");
		addAccelerator(menuHolder.addAction(hideFoldedNodes,
				"main/view/hideFoldedNodes"),
				"keystroke_plugins/map/MapDialog_hideFoldedNodes");
		menuHolder.addSeparator("main/view/");
		addAccelerator(
				menuHolder.addAction(mZoomInAction, "main/view/ZoomInAction"),
				"keystroke_plugins/map/MapDialog_zoomIn");
		addAccelerator(
				menuHolder.addAction(mZoomOutAction, "main/view/ZoomOutAction"),
				"keystroke_plugins/map/MapDialog_zoomOut");

		JMenu navigationItem = new JMenu(
				getText("MapControllerPopupDialog.Navigation"));
		menuHolder.addMenu(navigationItem, "main/navigation/.");
		// menuHolder.addSeparator("main/navigation/");
		addAccelerator(menuHolder.addAction(new SetHomeAction(),
				"main/navigation/SetHome"),
				"keystroke_plugins/map/MapDialogSetHome");
		addAccelerator(menuHolder.addAction(new MoveHomeAction(),
				"main/navigation/MoveHome"),
				"keystroke_plugins/map/MapDialogMoveHome");
		menuHolder.addSeparator("main/navigation/");
		mMoveBackwardAction = new MoveBackwardAction();
		addAccelerator(menuHolder.addAction(mMoveBackwardAction,
				"main/navigation/moveBackward"),
				"keystroke_plugins/map/MapDialog_moveBackward");
		mMoveForwardAction = new MoveForwardAction();
		addAccelerator(menuHolder.addAction(mMoveForwardAction,
				"main/navigation/moveForward"),
				"keystroke_plugins/map/MapDialog_moveForward");
		menuHolder.addSeparator("main/navigation/");
		addAccelerator(menuHolder.addAction(new MoveLeftAction(),
				"main/navigation/moveLeft"),
				"keystroke_plugins/map/MapDialog_moveLeft");
		addAccelerator(menuHolder.addAction(new MoveRightAction(),
				"main/navigation/moveRight"),
				"keystroke_plugins/map/MapDialog_moveRight");
		addAccelerator(menuHolder.addAction(new MoveUpAction(),
				"main/navigation/moveUp"),
				"keystroke_plugins/map/MapDialog_moveUp");
		addAccelerator(menuHolder.addAction(new MoveDownAction(),
				"main/navigation/moveDown"),
				"keystroke_plugins/map/MapDialog_moveDown");
		menuHolder.addSeparator("main/navigation/");

		menuHolder.updateMenus(mMenuBar, "main/");
		mMapDialog.setJMenuBar(mMenuBar);
		/* Popup menu */
		menuHolder.addAction(newNodeAction, "popup/newNode");
		// currently disabled, as the reverse functionality from
		// nominatim doesn't convince me.
//		menuHolder.addAction(newNodeReverseLookupAction,
//				"popup/newNodeReverseLookup");
		menuHolder.addAction(placeAction, "popup/place");
		menuHolder.addSeparator("popup/");
		menuHolder.addAction(maxmimalZoomToCursorAction,
				"popup/maxmimalZoomToCursorAction");
		menuHolder.addSeparator("popup/");
		menuHolder.addAction(copyLinkToClipboardAction,
				"popup/copyLinkToClipboardAction");
		menuHolder.addAction(copyCoordinatesToClipboardAction,
				"popup/copyCoordinatesToClipboardAction");
		menuHolder.updateMenus(mPopupMenu, "popup/");
		/*
		 * map location context menu
		 */
		menuHolder.addAction(new EditNodeInContextMenu(),
				"contextPopup/editNodeInContextMenu");
		menuHolder.addAction(new RemoveNodeLocationInContextMenu(),
				"contextPopup/RemoveNodeLocationInContextMenu");
		menuHolder.addAction(new SelectNodeInContextMenu(),
				"contextPopup/SelectNodeInContextMenu");
		menuHolder.addAction(new SelectNodeAndCloseInContextMenu(),
				"contextPopup/SelectNodeAndCloseInContextMenu");
		menuHolder.addSeparator("contextPopup/");
		menuHolder.addAction(new ShowNodeMapInContextMenu(),
				"contextPopup/showNodeMapInContextMenu");
		menuHolder.addAction(maxmimalZoomToCursorAction,
				"contextPopup/maxmimalZoomToCursorAction");
		menuHolder.addSeparator("contextPopup/");
		menuHolder.addAction(copyLinkToClipboardAction,
				"contextPopup/copyLinkToClipboardAction");
		menuHolder.addAction(new AddMapPictureToNode(),
				"contextPopup/addPictureToNode");
		menuHolder.updateMenus(getContextPopupMenu(), "contextPopup/");
		menuHolder.addAction(maxmimalZoomToCursorAction,
				"searchPopup/maxmimalZoomToCursorAction");
		menuHolder.updateMenus(getSearchPopupMenu(), "searchPopup/");

		mMapDialog.addKeyListener(this);
//		Tools.addFocusPrintTimer();
	}

	public void addAccelerator(JMenuItem menuItem, String key) {
		String keyProp = mMindMapController.getFrame().getProperty(key);
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyProp);
		// menuItem.setAccelerator(keyStroke);
		menuItem.getAction().putValue(Action.ACCELERATOR_KEY, keyStroke);
	}

	/**
	 * @param pSelected
	 * @return
	 */
	protected MapNodePositionHolderBase placeNode(MindMapNode pSelected) {
		Coordinate cursorPosition = getMap().getCursorPosition();
		Coordinate position = map.getPosition();
		int zoom = map.getZoom();
		return placeNodeAt(pSelected, cursorPosition, position, zoom);
	}

	protected MapNodePositionHolderBase placeNodeAt(MindMapNode pSelected,
			Coordinate cursorPosition, Coordinate position, int zoom) {
		MapNodePositionHolder hook = MapNodePositionHolder.getHook(pSelected);
		if (hook == null) {
			hook = addHookToNode(pSelected);
		}
		if (hook != null) {
			// set parameters:
			String tileSource = getTileSourceAsString();
			hook.changePosition(cursorPosition, position, zoom, tileSource);
		} else {
			logger.warning("Hook not found although it was recently added. Node was "
					+ pSelected);
		}
		return hook;
	}

	public String getTileSourceAsString() {
		String tileSource = getTileSourceName(getTileSource());
		return tileSource;
	}

	public TileSource getTileSource() {
		return getMap().getTileController().getTileSource();
	}

	public void removeNodePosition(MindMapNode selected) {
		MapNodePositionHolderBase hook = MapNodePositionHolder
				.getHook(selected);
		if (hook != null) {
			// double add == remove
			addHookToNode(selected);
		}
	}

	/**
	 */
	public void showSelectedNodes() {
		MindMapNode selected = mMindMapController.getSelected();
		List selecteds = mMindMapController.getSelecteds();
		if (selecteds.size() == 1) {
			MapNodePositionHolder hook = MapNodePositionHolder
					.getHook(selected);
			if (hook != null) {
				showNode(hook);
			}
			return;
		}
		// find common center. Code adapted from JMapViewer.
		int x_min = Integer.MAX_VALUE;
		int y_min = Integer.MAX_VALUE;
		int x_max = Integer.MIN_VALUE;
		int y_max = Integer.MIN_VALUE;
		int mapZoomMax = getMaxZoom();
		for (Iterator it = selecteds.iterator(); it.hasNext();) {
			MindMapNode node = (MindMapNode) it.next();
			MapNodePositionHolder hook = MapNodePositionHolder.getHook(node);

			if (hook != null) {
				int x = OsmMercator.LonToX(hook.getPosition().getLon(),
						mapZoomMax);
				int y = OsmMercator.LatToY(hook.getPosition().getLat(),
						mapZoomMax);
				x_max = Math.max(x_max, x);
				y_max = Math.max(y_max, y);
				x_min = Math.min(x_min, x);
				y_min = Math.min(y_min, y);
				if (node == selected) {
					setCursorPosition(hook.getPosition());
					changeTileSource(hook.getTileSource(), map);
				}
			}
		}
		int height = Math.max(0, getMap().getHeight());
		int width = Math.max(0, getMap().getWidth());
		int newZoom = mapZoomMax;
		int x = x_max - x_min;
		int y = y_max - y_min;
		while (x > width || y > height) {
			newZoom--;
			x >>= 1;
			y >>= 1;
		}
		x = x_min + (x_max - x_min) / 2;
		y = y_min + (y_max - y_min) / 2;
		int z = 1 << (mapZoomMax - newZoom);
		x /= z;
		y /= z;
		getMap().setDisplayPosition(x, y, newZoom);

	}

	public int getMaxZoom() {
		return getTileSource().getMaxZoom();
	}

	public void showNode(MapNodePositionHolder hook) {
		int zoom = hook.getZoom();
		changeTileSource(hook.getTileSource(), map);
		setCursorPosition(hook, zoom);
	}

	public void setCursorPosition(MapNodePositionHolder hook, int zoom) {
		Coordinate position = hook.getPosition();
		Coordinate mapCenter = hook.getMapCenter();
		setZoom(zoom);
		if (mapCenter != null) {
			// move map:
			logger.fine("Set display position to " + mapCenter
					+ " and cursor to " + position + " and zoom " + zoom
					+ " where max zoom is " + getMaxZoom());
			map.setDisplayPositionByLatLon(mapCenter.getLat(),
					mapCenter.getLon(), zoom);
		}
		setCursorPosition(position);
	}

	/**
	 * Sets the cursor to the specified position and moves the display, such
	 * that the cursor is visible.
	 */
	protected void setCursorPosition(Coordinate position) {
		getMap().setCursorPosition(position);
		// is the cursor now visible and the zoom correct? if not, display it
		// directly.
		if (map.getMapPosition(position, true) == null) {
			map.setDisplayPositionByLatLon(position.getLat(),
					position.getLon(), map.getZoom());
		}
		storeMapPosition(position);
		for (Iterator it = mCursorPositionListeners.iterator(); it.hasNext();) {
			CursorPositionListener listener = (CursorPositionListener) it
					.next();
			listener.cursorPositionChanged(position);
		}
	}

	/**
	 * Sets the zoom.
	 */
	protected void setZoom(int zoom) {
		if (zoom > getMaxZoom()) {
			zoom = getMaxZoom();
		}
		if (zoom == 0) {
			zoom = map.getZoom();
		}
		map.setZoom(zoom);
	}

	/**
	 * @param pTileSource
	 * @param pMap
	 *            if found, the map tile source is set. Set null, if you don't
	 *            want this.
	 * @return null, if the string is not found.
	 */
	public static TileSource changeTileSource(String pTileSource,
			JMapViewer pMap) {
		logger.fine("Searching for tile source " + pTileSource);
		TileSourceStore tileSource = getTileSourceByName(pTileSource);
		if (tileSource != null && pMap != null) {
			pMap.setTileSource(tileSource.mTileSource);
			return tileSource.mTileSource;
		}
		return null;
	}

	public static TileSourceStore getTileSourceByName(String sourceName) {
		for (int i = 0; i < sTileSources.length; i++) {
			TileSourceStore source = sTileSources[i];
			if (Tools.safeEquals(getTileSourceName(source.mTileSource),
					sourceName)) {
				logger.fine("Found  tile source " + source);
				return source;
			}
		}
		return null;
	}

	public static String getTileSourceName(TileSource source) {
		return source.getClass().getName();
	}

	public MapNodePositionHolder addHookToNode(MindMapNode selected) {
		MapNodePositionHolder hook;
		List selecteds = Tools.getVectorWithSingleElement(selected);
		mMindMapController.addHook(selected, selecteds,
				MapNodePositionHolderBase.NODE_MAP_HOOK_NAME, null);
		hook = MapNodePositionHolder.getHook(selected);
		return hook;
	}

	/**
	 * Translate String
	 * 
	 * @param pString
	 * @return
	 */
	private String getText(String pString) {
		return mMindMapController.getText(pString);
	}

	public void mouseDragged(MouseEvent e) {
		if (!mMovementEnabled
				|| !(isMoving || isMapNodeMoving || mIsRectangularSelect))
			return;
		if (isMapNodeMoving) {
			lastDragPoint = e.getPoint();
			int diffx = 0;
			int diffy = 0;
			if (e.getX() < SCROLL_MARGIN) {
				diffx = -SCROLL_PIXEL_AMOUNT;
			}
			if (map.getWidth() - e.getX() < SCROLL_MARGIN) {
				diffx = SCROLL_PIXEL_AMOUNT;
			}
			if (e.getY() < SCROLL_MARGIN) {
				diffy = -SCROLL_PIXEL_AMOUNT;
			}
			if (map.getHeight() - e.getY() < SCROLL_MARGIN) {
				diffy = SCROLL_PIXEL_AMOUNT;
			}
			map.moveMap(diffx, diffy);
			return;
		}
		if (mIsRectangularSelect) {
			// Actualize second point of rectangle.
			getMap().setRectangular(mRectangularStart,
					getCoordinateFromMouseEvent(e));
			getMap().setDrawRectangular(true);
			getMap().repaint();
			return;
		}
		// Is only the selected mouse button pressed?
		if ((e.getModifiersEx() & MOUSE_BUTTONS_MASK) == movementMouseButtonMask) {
			moveMapOnDrag(e);
		}
	}

	public void moveMapOnDrag(MouseEvent e) {
		Point p = e.getPoint();
		if (lastDragPoint != null) {
			int diffx = lastDragPoint.x - p.x;
			int diffy = lastDragPoint.y - p.y;
			map.moveMap(diffx, diffy);
			// System.out.println("Move to " + map.getPosition() +
			// " with zoom " + map.getZoom() );
		}
		lastDragPoint = p;
	}

	public void mouseClicked(MouseEvent e) {
		if (!mClickEnabled) {
			return;
		}
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			// on double click: new node.
			newNode(e);
			return;
		}
		// is button 1?
		if (e.getButton() == MouseEvent.BUTTON1 || Tools.isMacOsX()
				&& e.getModifiersEx() == MAC_MOUSE_BUTTON1_MASK) {
			setCursorPosition(e);
		}
	}

	private void setMouseControl(boolean pEnable) {
		setMovementEnabled(pEnable);
		setWheelZoomEnabled(pEnable);
		setClickEnabled(pEnable);
	}

	/**
	 * @param pEvent
	 *            : location
	 */
	private void newNode(MouseEvent pEvent) {
		final MindMapNode targetNode = mMindMapController.getSelected();
		final MindMapNode newNode = insertNewNode(targetNode);
		final NodeView nodeView = mMindMapController.getNodeView(newNode);
		mMindMapController.select(nodeView);
		// inline editing:
		mMindMapController.setBlocked(true);
		setMouseControl(false);
		Point point = pEvent.getPoint();
		Tools.convertPointToAncestor((Component) pEvent.getSource(), point, map);
		storeMapPosition(getMap().getCursorPosition());
		MapEditTextFieldControl editControl = new MapEditTextFieldControl(
				nodeView, newNode, targetNode, false);
		EditNodeTextField textfield = new MapEditNoteTextField(nodeView, "",
				null, mMindMapController, editControl, map, point);
		textfield.show();
	}

	public MindMapNode insertNewNode(final MindMapNode targetNode) {
		int childPosition;
		MindMapNode parent;
		if (targetNode.isRoot()) {
			parent = targetNode;
			childPosition = 0;
		} else {
			// new sibling:
			parent = targetNode.getParentNode();
			childPosition = parent.getChildPosition(targetNode);
			childPosition++;
		}
		final MindMapNode newNode = mMindMapController.addNewNode(parent,
				childPosition, targetNode.isLeft());
		return newNode;
	}

	/**
	 * @param pPositionHolder
	 * @param pEvent
	 *            : location
	 */
	private void editNode(MapNodePositionHolder pPositionHolder,
			MouseEvent pEvent) {
		final MindMapNode editNode = pPositionHolder.getNode();
		final NodeView nodeView = mMindMapController.getNodeView(editNode);
		if (nodeView == null) {
			return;
		}
		mMindMapController.select(nodeView);
		// inline editing:
		mMindMapController.setBlocked(true);
		setMouseControl(false);
		Point point = pEvent.getPoint();
		Tools.convertPointToAncestor((Component) pEvent.getSource(), point, map);
		MapEditTextFieldControl editControl = new MapEditTextFieldControl(
				nodeView, editNode, editNode, true);
		EditNodeTextField textfield = new MapEditNoteTextField(nodeView,
				editNode.getText(), null, mMindMapController, editControl, map,
				point);
		textfield.show();
	}

	public void setCursorPosition(MouseEvent e) {
		final Coordinate coordinates = map.getPosition(e.getPoint());
		setCursorPosition(coordinates);
	}

	public void mousePressed(MouseEvent e) {
		if (!mClickEnabled) {
			return;
		}
		showPopupMenu(e);
		if (e.isConsumed()) {
			return;
		}
		if (e.getButton() == movementMouseButton
				|| (Tools.isMacOsX() && e.getModifiersEx() == MAC_MOUSE_BUTTON1_MASK)) {
			if (e.isShiftDown()) {
				// rectangular select:
				mIsRectangularSelect = true;
				mRectangularStart = getCoordinateFromMouseEvent(e);
				logger.fine("Starting rect on " + mRectangularStart);
				return;
			}
			// detect collision with map marker:
			MapMarkerBase mapMarker = checkHit(e);
			if (mapMarker instanceof MapMarkerLocation) {
				MapNodePositionHolder posHolder = ((MapMarkerLocation) mapMarker)
						.getNodePositionHolder();
				mDragStartingPoint = new Point(e.getPoint());
				correctPointByMapCenter(mDragStartingPoint);
				isMapNodeMoving = true;
				mMapNodeMovingSource = posHolder;
				setCursor(Cursor.MOVE_CURSOR, true);
				return;
			}
			lastDragPoint = null;
			isMoving = true;
		}
	}

	protected void correctPointByMapCenter(Point dragStartingPoint) {
		Point center = getMap().getCenter();
		dragStartingPoint.translate(center.x, center.y);
	}

	public MapMarkerBase checkHit(MouseEvent e) {
		// check for hit on map marker:
		for (Iterator it = map.getMapMarkerList().iterator(); it.hasNext();) {
			MapMarkerBase location = (MapMarkerBase) it.next();
			Coordinate locationC = location.getCoordinate();
			Point locationXY = map.getMapPosition(locationC, true);
			if (locationXY == null) {
				continue;
			}
			boolean checkHitResult = location.checkHit(e.getX() - locationXY.x,
					e.getY() - locationXY.y);
			logger.fine("Checking for hit for location " + location
					+ " at location " + locationXY + " to event " + e.getX()
					+ " and " + e.getY() + " is " + checkHitResult);
			if (checkHitResult) {
				return location;
			}
		}
		return null;

	}

	public Coordinate getCoordinateFromMouseEvent(MouseEvent e) {
		Coordinate mousePosition = map
				.getPosition(new Point(e.getX(), e.getY()));
		return mousePosition;
	}

	/**
	 * @param e
	 *            event.
	 */
	private void showPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			JPopupMenu popupmenu = getPopupMenu();
			// check for hit on map marker:
			MapMarkerBase mapMarker = checkHit(e);
			if (mapMarker instanceof MapMarkerLocation) {
				MapNodePositionHolder posHolder = ((MapMarkerLocation) mapMarker)
						.getNodePositionHolder();
				mCurrentPopupPositionHolder = posHolder;
				setCursorPosition(posHolder.getPosition());
				getContextPopupMenu()
						.show(e.getComponent(), e.getX(), e.getY());
				e.consume();
				return;
			}
			if (mapMarker instanceof MapSearchMarkerLocation) {
				MapSearchMarkerLocation location = (MapSearchMarkerLocation) mapMarker;
				setCursorPosition(location.getCoordinate());
				getSearchPopupMenu().show(e.getComponent(), e.getX(), e.getY());
				e.consume();
				return;

			}
			mCurrentPopupPositionHolder = null;
			if (popupmenu != null) {
				setCursorPosition(e);
				popupmenu.show(e.getComponent(), e.getX(), e.getY());
				e.consume();
			}
		}

	}

	/**
	 * listener, that blocks the controler if the menu is active (PN) Take care!
	 * This listener is also used for modelpopups (as for graphical links).
	 */
	private class ControllerPopupMenuListener implements PopupMenuListener {
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			setMouseControl(false); // block controller
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			setMouseControl(true); // unblock controller
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
			setMouseControl(true); // unblock controller
		}

	}

	/**
	 * Take care! This listener is also used for modelpopups (as for graphical
	 * links).
	 */
	protected final ControllerPopupMenuListener popupListenerSingleton = new ControllerPopupMenuListener();

	private MouseEvent mTimerMouseEvent;

	private Action mZoomInAction;

	private Action mZoomOutAction;

	private MoveForwardAction mMoveForwardAction;

	private MoveBackwardAction mMoveBackwardAction;

	JMenuBar mMenuBar;

	private long mWheelZoomLastTime = 0;

	private Vector mCursorPositionListeners = new Vector();

	private JPopupMenu mSearchPopupMenu;

	public void mouseReleased(MouseEvent e) {
		if (!mClickEnabled) {
			return;
		}
		showPopupMenu(e);
		if (e.isConsumed()) {
			return;
		}

		if (e.getButton() == movementMouseButton || Tools.isMacOsX()
				&& e.getButton() == MouseEvent.BUTTON1) {
			final Coordinate coordinates = getCoordinateFromMouseEvent(e);
			if (isMapNodeMoving) {
				// check for minimal drag distance:
				Point currentPoint = new Point(e.getPoint());
				correctPointByMapCenter(currentPoint);
				if (mDragStartingPoint.distance(currentPoint) > MapMarkerLocation.CIRCLE_RADIUS) {
					Coordinate mousePosition = coordinates;
					mMapNodeMovingSource.changePosition(mousePosition,
							map.getPosition(), map.getZoom(),
							getTileSourceAsString());
				} else {
					// select the node (single click)
					MindMapNode node = mMapNodeMovingSource.getNode();
					if (e.isShiftDown()) {
						Vector sel = new Vector(
								mMindMapController.getSelecteds());
						if (sel.contains(node)) {
							// remove:
							sel.remove(node);
							node = mMindMapController.getSelected();
						} else {
							sel.add(node);
						}
						mMindMapController.select(node, sel);
					} else {
						selectNode(node);
					}
				}
				mMapNodeMovingSource = null;
				setCursor(Cursor.DEFAULT_CURSOR, false);
			}
			if (mIsRectangularSelect) {
				// gather all locations and select them:
				Vector mapNodePositionHolders = new Vector();
				// take only those elements in the correct rectangle:
				Rectangle r = getMap().getRectangle(mRectangularStart,
						coordinates);
				if (r != null) {
					MindMapNode last = null;
					for (Iterator it = mMapHook.getMapNodePositionHolders()
							.iterator(); it.hasNext();) {
						MapNodePositionHolder holder = (MapNodePositionHolder) it
								.next();
						Coordinate pointPosition = holder.getPosition();
						Point mapPosition = getMap().getMapPosition(
								pointPosition, true);
						if (mapPosition != null && r.contains(mapPosition)) {
							// ok
							mapNodePositionHolders.add(holder.getNode());
							last = holder.getNode();
						}
					}
					if (last != null) {
						// ie. at least one found:
						mMindMapController.select(last, mapNodePositionHolders);
					}
				}
			}
			getMap().setDrawRectangular(false);
			mIsRectangularSelect = false;
			mRectangularStart = null;
			isMapNodeMoving = false;
			if (lastDragPoint != null) {
				storeMapPosition(coordinates);
			}
			lastDragPoint = null;
			isMoving = false;
		}
	}

	protected void storeMapPosition(final Coordinate coordinates) {
		final PositionHolder holder = new PositionHolder(coordinates.getLat(),
				coordinates.getLon(), getMap().getZoom());
		final Vector positionHolderVector = getPositionHolderVector();
		if (getPositionHolderIndex() >= 0) {
			// check for equalness
			PositionHolder currentPosition = (PositionHolder) positionHolderVector
					.get(getPositionHolderIndex());
			if (currentPosition.equals(holder)) {
				return;
			}
		}
		// if position is not at the end, the locations in front are deleted.
		while (getPositionHolderIndex() < positionHolderVector.size() - 1) {
			positionHolderVector.remove(positionHolderVector.size() - 1);
		}
		logger.fine("Storing position " + holder + " at index "
				+ getPositionHolderIndex());
		positionHolderVector.insertElementAt(holder,
				getPositionHolderIndex() + 1);
		setPositionHolderIndex(getPositionHolderIndex() + 1);
		// assure that max size is below limit.
		while (positionHolderVector.size() >= POSITION_HOLDER_LIMIT
				&& getPositionHolderIndex() > 0) {
			setPositionHolderIndex(Math.max(getPositionHolderIndex() - 1, 0));
			positionHolderVector.remove(0);
		}
		// update actions
		mMoveForwardAction.setEnabled(mMoveForwardAction.isEnabled());
		mMoveBackwardAction.setEnabled(mMoveBackwardAction.isEnabled());
	}

	protected void setCursor(int defaultCursor, boolean pVisible) {
		Component glassPane = getGlassPane();
		glassPane.setCursor(Cursor.getPredefinedCursor(defaultCursor));
		glassPane.setVisible(pVisible);
	}

	public Component getGlassPane() {
		return map.getRootPane().getGlassPane();
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (mWheelZoomEnabled) {
			/*
			 * This is problematic under Mac as the zoom is too fast. First
			 * idea: looking for the last time the zoom was changed. It must not
			 * be changed within 100ms again. Moreover, limit the rotation
			 * number.
			 */
			if (System.currentTimeMillis() - mWheelZoomLastTime >= WHEEL_ZOOM_MINIMAL_TIME_BETWEEN_CHANGES) {
				int wheelRotation = e.getWheelRotation();
				if (Math.abs(wheelRotation) > 2) {
					wheelRotation = (int) (2 * Math.signum(wheelRotation));
				}
				map.setZoom(map.getZoom() - wheelRotation, e.getPoint());
				mWheelZoomLastTime = System.currentTimeMillis();
			}
		}
	}

	public boolean isMovementEnabled() {
		return mMovementEnabled;
	}

	/**
	 * Enables or disables that the map pane can be moved using the mouse.
	 * 
	 * @param movementEnabled
	 */
	public void setMovementEnabled(boolean movementEnabled) {
		this.mMovementEnabled = movementEnabled;
	}

	public int getMovementMouseButton() {
		return movementMouseButton;
	}

	public JPopupMenu getPopupMenu() {
		return mPopupMenu;
	}

	public JPopupMenu getContextPopupMenu() {
		if (mContextPopupMenu == null) {
			mContextPopupMenu = new JPopupMenu();
			mContextPopupMenu.addPopupMenuListener(popupListenerSingleton);
		}
		return mContextPopupMenu;
	}

	public JPopupMenu getSearchPopupMenu() {
		if (mSearchPopupMenu == null) {
			mSearchPopupMenu = new JPopupMenu();
			mSearchPopupMenu.addPopupMenuListener(popupListenerSingleton);
		}
		return mSearchPopupMenu;
	}

	/**
	 * Sets the mouse button that is used for moving the map. Possible values
	 * are:
	 * <ul>
	 * <li>{@link MouseEvent#BUTTON1} (left mouse button)</li>
	 * <li>{@link MouseEvent#BUTTON2} (middle mouse button)</li>
	 * <li>{@link MouseEvent#BUTTON3} (right mouse button)</li>
	 * </ul>
	 * 
	 * @param movementMouseButton
	 */
	public void setMovementMouseButton(int movementMouseButton) {
		this.movementMouseButton = movementMouseButton;
		switch (movementMouseButton) {
		case MouseEvent.BUTTON1:
			movementMouseButtonMask = MouseEvent.BUTTON1_DOWN_MASK;
			break;
		case MouseEvent.BUTTON2:
			movementMouseButtonMask = MouseEvent.BUTTON2_DOWN_MASK;
			break;
		case MouseEvent.BUTTON3:
			movementMouseButtonMask = MouseEvent.BUTTON3_DOWN_MASK;
			break;
		default:
			throw new RuntimeException("Unsupported button");
		}
	}

	public boolean isWheelZoomEnabled() {
		return mWheelZoomEnabled;
	}

	public void setWheelZoomEnabled(boolean wheelZoomEnabled) {
		this.mWheelZoomEnabled = wheelZoomEnabled;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		if (!mMovementEnabled) {
			return;
		}
		// Mac OSX simulates with ctrl + mouse 1 the second mouse button hence
		// no dragging events get fired.
		//
		if (Tools.isMacOsX()) {
			if (isMapNodeMoving) {
				lastDragPoint = e.getPoint();
				return;
			}
			// Is only the selected mouse button pressed?
			if (isMoving && e.getModifiersEx() == 0 /* MouseEvent.CTRL_DOWN_MASK */) {
				moveMapOnDrag(e);
				return;
			}

		}
		// no move events, thus the cursor is just moving.
		mMouseHitsNodeTimer.restart();
		mTimerMouseEvent = e;

	}

	/**
	 * Action handler for search result handling.
	 * 
	 * @param pPlace
	 */
	public void setCursorPosition(Place pPlace) {
		map.setDisplayPositionByLatLon(pPlace.getLat(), pPlace.getLon(),
				map.getZoom());
		Coordinate cursorPosition = new Coordinate(pPlace.getLat(),
				pPlace.getLon());
		setCursorPosition(cursorPosition);
	}

	/**
	 * @return true, if ok, false if error.
	 */
	public boolean search(MapDialog.ResultTableModel dataModel,
			JTable mResultTable, String mSearchText,
			Color mTableOriginalBackgroundColor) {
		// Display hour glass
		boolean returnValue = true;
		setCursor(Cursor.WAIT_CURSOR, true);
		try {
			dataModel.clear();
			// doesn't work due to event thread...
			mResultTable.setBackground(Color.GRAY);
			Searchresults results = getSearchResults(mSearchText);
			if (results == null) {
				mResultTable.setBackground(Color.RED);
			} else {
				for (Iterator it = results.getListPlaceList().iterator(); it
						.hasNext();) {
					Place place = (Place) it.next();
					logger.fine("Found place " + place.getDisplayName());
					// error handling, if the query wasn't successful.
					if (Tools.safeEquals("ERROR", place.getOsmType())) {
						mResultTable.setBackground(Color.RED);
						returnValue = false;
					} else if (Tools.safeEquals("WARNING", place.getOsmType())) {
						mResultTable.setBackground(Color.YELLOW);
						returnValue = false;
					} else {
						mResultTable.setBackground(Color.WHITE);
						mResultTable
								.setBackground(mTableOriginalBackgroundColor);
					}
					dataModel.addPlace(place);
				}
			}
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			returnValue = false;
		}
		setCursor(Cursor.DEFAULT_CURSOR, false);
		return returnValue;
	}

	public Reversegeocode getReverseLookup(Coordinate pCoordinate, int pZoom) {
		StringBuilder b = new StringBuilder();
		b.append("http://nominatim.openstreetmap.org/reverse?format=xml&email=christianfoltin%40users.sourceforge.net&addressdetails=0"); //$NON-NLS-1$
		b.append("&accept-language=").append(Locale.getDefault().getLanguage()); //$NON-NLS-1$
		b.append("&lat=");
		b.append(pCoordinate.getLat());
		b.append("&lon=");
		b.append(pCoordinate.getLon());
		b.append("&zoom=");
		b.append(pZoom);
		try {
			String result = wget(b);
			Reversegeocode reversegeocode = (Reversegeocode) XmlBindingTools
					.getInstance().unMarshall(result);
			return reversegeocode;
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
		}
		return null;
	}

	/**
	 * @param pText
	 * @return
	 */
	public Searchresults getSearchResults(String pText) {
		String result = "unknown";
		Searchresults results = new Searchresults();
		StringBuilder b = new StringBuilder();
		boolean limitSearchToRegion = mMapHook.isLimitSearchToRegion();
		try {
			if (true) {
				b.append("http://nominatim.openstreetmap.org/search/?email=christianfoltin%40users.sourceforge.net&q="); //$NON-NLS-1$
				b.append(URLEncoder.encode(pText, "UTF-8"));
				b.append("&format=xml&limit=30&accept-language=").append(Locale.getDefault().getLanguage()); //$NON-NLS-1$
				if (limitSearchToRegion) {
					Coordinate topLeftCorner = getMap().getPosition(0, 0);
					Coordinate bottomRightCorner = getMap().getPosition(
							getMap().getWidth(), getMap().getHeight());
					b.append("&viewbox=");
					b.append(topLeftCorner.getLon());
					b.append(",");
					b.append(topLeftCorner.getLat());
					b.append(",");
					b.append(bottomRightCorner.getLon());
					b.append(",");
					b.append(bottomRightCorner.getLat());
					b.append("&bounded=1");
				}
				result = wget(b);
			} else {
				// only for offline testing:
				result = XML_VERSION_1_0_ENCODING_UTF_8
						+ "<searchresults timestamp=\"Tue, 08 Nov 11 22:49:54 -0500\" attribution=\"Data Copyright OpenStreetMap Contributors, Some Rights Reserved. CC-BY-SA 2.0.\" querystring=\"innsbruck\" polygon=\"false\" exclude_place_ids=\"228452,25664166,26135863,25440203\" more_url=\"http://open.mapquestapi.com/nominatim/v1/search?format=xml&amp;exclude_place_ids=228452,25664166,26135863,25440203&amp;accept-language=&amp;q=innsbruck\">\n"
						+ "  <place place_id=\"228452\" osm_type=\"node\" osm_id=\"34840064\" place_rank=\"16\" boundingbox=\"47.2554266357,47.2754304504,11.3827679062,11.4027688599\" lat=\"47.2654296\" lon=\"11.3927685\" display_name=\"Innsbruck, Bezirk Innsbruck-Stadt, Innsbruck-Stadt, Tirol, Österreich, Europe\" class=\"place\" type=\"city\" icon=\"http://open.mapquestapi.com/nominatim/v1/images/mapicons/poi_place_city.p.20.png\"/>\n"
						+ "  <place place_id=\"25664166\" osm_type=\"way\" osm_id=\"18869490\" place_rank=\"27\" boundingbox=\"43.5348739624023,43.5354156494141,-71.1319198608398,-71.1316146850586\" lat=\"43.5351336524196\" lon=\"-71.1317853486877\" display_name=\"Innsbruck, New Durham, Strafford County, New Hampshire, United States of America\" class=\"highway\" type=\"service\"/>\n"
						+ "  <place place_id=\"26135863\" osm_type=\"way\" osm_id=\"18777572\" place_rank=\"27\" boundingbox=\"38.6950759887695,38.6965446472168,-91.1586227416992,-91.1520233154297\" lat=\"38.6957456083531\" lon=\"-91.1552550683042\" display_name=\"Innsbruck, Warren, Aspenhoff, Warren County, Missouri, United States of America\" class=\"highway\" type=\"service\"/>\n"
						+ "  <place place_id=\"25440203\" osm_type=\"way\" osm_id=\"18869491\" place_rank=\"27\" boundingbox=\"43.5335311889648,43.5358810424805,-71.1356735229492,-71.1316146850586\" lat=\"43.5341678362733\" lon=\"-71.1338615946084\" display_name=\"Innsbruck, New Durham, Strafford County, New Hampshire, 03855, United States of America\" class=\"highway\" type=\"service\"/>\n"
						+ "</searchresults>";
				result = "<?xml version=\"1.0\" encoding=\"UTF-8\""
						+ " ?><searchresults timestamp='Wed, 29 Aug"
						+ " 12 06:33:22 +0100' attribution='Data Co"
						+ "pyright OpenStreetMap Contributors, Some"
						+ " Rights Reserved. CC-BY-SA 2.0.' queryst"
						+ "ring='bäckerei' polygon='false' exclude_"
						+ "place_ids='2323884,1350101,7261519,17658"
						+ "198,16228926,7825940,8072208,16133988,51"
						+ "52777,7708711,16471512,7844042,12267468,"
						+ "6699146,7114466,6856494,856383,9874163,7"
						+ "135888,868611,11403029,6568269,16118527,"
						+ "7540110,11628259,1339026,19587330,115253"
						+ "72,11534612,11748035' more_url='http://n"
						+ "ominatim.openstreetmap.org/search?format"
						+ "=xml&amp;exclude_place_ids=2323884,13501"
						+ "01,7261519,17658198,16228926,7825940,807"
						+ "2208,16133988,5152777,7708711,16471512,7"
						+ "844042,12267468,6699146,7114466,6856494,"
						+ "856383,9874163,7135888,868611,11403029,6"
						+ "568269,16118527,7540110,11628259,1339026"
						+ ",19587330,11525372,11534612,11748035&amp"
						+ ";accept-language=de&amp;viewbox=13.24470"
						+ "5200195312%2C52.43435075954755%2C13.3324"
						+ "2416381836%2C52.461762311435194&amp;q=b%"
						+ "C3%A4ckerei'><place place_id='2323884' o"
						+ "sm_type='node' osm_id='352983574' place_"
						+ "rank='30' boundingbox=\"52.443815460205,"
						+ "52.463819274902,13.313097229004,13.33309"
						+ "8182678\" lat='52.4538175' lon='13.32309"
						+ "74' display_name='Bäckerei Mälzer, Schüt"
						+ "zenstraße, Steglitz, Steglitz-Zehlendorf"
						+ ", Berlin, 12165, Deutschland' class='sho"
						+ "p' type='bakery' icon='http://nominatim."
						+ "openstreetmap.org/images/mapicons/shoppi"
						+ "ng_bakery.p.20.png'/><place place_id='13"
						+ "50101' osm_type='node' osm_id='298794800"
						+ "' place_rank='30' boundingbox=\"52.43134"
						+ "9029541,52.451352844238,13.282660713196,"
						+ "13.30266166687\" lat='52.4413499' lon='1"
						+ "3.2926616' display_name='Bäckerei Bertra"
						+ "m, 27, Curtiusstraße, Lichterfelde, Steg"
						+ "litz-Zehlendorf, Berlin, 12205, Deutschl"
						+ "and' class='shop' type='bakery' icon='ht"
						+ "tp://nominatim.openstreetmap.org/images/"
						+ "mapicons/shopping_bakery.p.20.png'/><pla"
						+ "ce place_id='7261519' osm_type='node' os"
						+ "m_id='792690678' place_rank='30' boundin"
						+ "gbox=\"52.434942474365,52.454946289062,1"
						+ "3.282605400085,13.30260635376\" lat='52."
						+ "444945' lon='13.292606' display_name='Kn"
						+ "ese-Bäckerei, Knesebeckstraße, Lichterfe"
						+ "lde, Steglitz-Zehlendorf, Berlin, 12205,"
						+ " Deutschland' class='shop' type='bakery'"
						+ " icon='http://nominatim.openstreetmap.or"
						+ "g/images/mapicons/shopping_bakery.p.20.p"
						+ "ng'/><place place_id='17658198' osm_type"
						+ "='node' osm_id='1655185388' place_rank='"
						+ "30' boundingbox=\"52.426340332031,52.446"
						+ "344146728,13.256445159912,13.27644611358"
						+ "6\" lat='52.4363419' lon='13.2664454' di"
						+ "splay_name='Bäckerei Strauch, Berliner S"
						+ "traße, Zehlendorf, Steglitz-Zehlendorf, "
						+ "Berlin, 14169, Deutschland' class='shop'"
						+ " type='bakery' icon='http://nominatim.op"
						+ "enstreetmap.org/images/mapicons/shopping"
						+ "_bakery.p.20.png'/><place place_id='1622"
						+ "8926' osm_type='node' osm_id='1455112119"
						+ "' place_rank='30' boundingbox=\"52.43713"
						+ "973999,52.457143554687,13.296548118591,1"
						+ "3.316549072266\" lat='52.4471403' lon='1"
						+ "3.3065482' display_name='Bäckerei Hillma"
						+ "nn, 52, Moltkestraße, Lichterfelde, Steg"
						+ "litz-Zehlendorf, Berlin, 12203, Deutschl"
						+ "and' class='shop' type='bakery' icon='ht"
						+ "tp://nominatim.openstreetmap.org/images/"
						+ "mapicons/shopping_bakery.p.20.png'/><pla"
						+ "ce place_id='7825940' osm_type='node' os"
						+ "m_id='803776974' place_rank='30' boundin"
						+ "gbox=\"52.447733154297,52.467736968994,1"
						+ "3.280044784546,13.30004573822\" lat='52."
						+ "4577338' lon='13.2900455' display_name='"
						+ "Wiener Feinbäcker Heberer, Brümmerstraße"
						+ ", Dahlem, Steglitz-Zehlendorf, Berlin, 1"
						+ "4195, Deutschland' class='shop' type='ba"
						+ "kery' icon='http://nominatim.openstreetm"
						+ "ap.org/images/mapicons/shopping_bakery.p"
						+ ".20.png'/><place place_id='8072208' osm_"
						+ "type='node' osm_id='814072915' place_ran"
						+ "k='30' boundingbox=\"52.430979003906,52."
						+ "450982818603,13.279904594421,13.29990554"
						+ "8096\" lat='52.4409802' lon='13.2899047'"
						+ " display_name='Brotmeisterei Steinecke, "
						+ "36-38, Curtiusstraße, Lichterfelde, Steg"
						+ "litz-Zehlendorf, Berlin, 12205, Deutschl"
						+ "and' class='shop' type='bakery' icon='ht"
						+ "tp://nominatim.openstreetmap.org/images/"
						+ "mapicons/shopping_bakery.p.20.png'/><pla"
						+ "ce place_id='16133988' osm_type='node' o"
						+ "sm_id='1391486692' place_rank='30' bound"
						+ "ingbox=\"52.44658493042,52.466588745117,"
						+ "13.310922851563,13.330923805237\" lat='5"
						+ "2.4565867' lon='13.3209229' display_name"
						+ "='Wiedemann, Albrechtstraße, Steglitz, S"
						+ "teglitz-Zehlendorf, Berlin, 12165, Deuts"
						+ "chland' class='shop' type='bakery' icon="
						+ "'http://nominatim.openstreetmap.org/imag"
						+ "es/mapicons/shopping_bakery.p.20.png'/><"
						+ "place place_id='5152777' osm_type='node'"
						+ " osm_id='570034727' place_rank='30' boun"
						+ "dingbox=\"52.441808929443,52.46181274414"
						+ "1,13.320470085144,13.340471038818\" lat="
						+ "'52.4518101' lon='13.3304701' display_na"
						+ "me='Konditorei Rabien, Klingsorstraße, S"
						+ "teglitz, Steglitz-Zehlendorf, Berlin, 12"
						+ "167, Deutschland' class='shop' type='bak"
						+ "ery' icon='http://nominatim.openstreetma"
						+ "p.org/images/mapicons/shopping_bakery.p."
						+ "20.png'/></searchresults>";
				// result = XML_VERSION_1_0_ENCODING_UTF_8
				// +
				// "<searchresults timestamp=\"Tue, 08 Nov 11 22:49:54 -0500\" attribution=\"Data Copyright OpenStreetMap Contributors, Some Rights Reserved. CC-BY-SA 2.0.\" querystring=\"innsbruck\" polygon=\"false\" exclude_place_ids=\"228452,25664166,26135863,25440203\" more_url=\"http://open.mapquestapi.com/nominatim/v1/search?format=xml&amp;exclude_place_ids=228452,25664166,26135863,25440203&amp;accept-language=&amp;q=innsbruck\">\n"
				// + "</searchresults>";

			}
			results = (Searchresults) XmlBindingTools.getInstance().unMarshall(
					result);
			if (results == null) {
				logger.warning(result + " can't be parsed");
			}
		} catch (Exception e) {
			logger.fine("Searching for " + b.toString() + " gave an error");
			final String errorString = e.toString();
			freemind.main.Resources.getInstance().logException(e);
			logger.warning("Result was " + result);
			results.addPlace(getErrorPlace(errorString, "ERROR"));
		}
		if (results.getListPlaceList().isEmpty()) {
			String textId;
			if (limitSearchToRegion) {
				textId = "plugins.map.FreeMindMapController.LimitedSearchWithoutResult";
			} else {
				textId = "plugins.map.FreeMindMapController.SearchWithoutResult";
			}
			Object[] messageArguments = { pText };
			MessageFormat formatter = new MessageFormat(
					mMindMapController.getText(textId));
			String message = formatter.format(messageArguments);
			results.addPlace(getErrorPlace(message, "WARNING"));
		}
		return results;
	}

	public String wget(StringBuilder b) throws MalformedURLException,
			IOException, UnsupportedEncodingException {
		String result;
		mMindMapController.getFrame().setWaitingCursor(true);
		try {
			logger.fine("Searching for " + b.toString());
			URL url = new URL(b.toString());
			URLConnection urlConnection = url.openConnection();
			if (Tools.isAboveJava4()) {
				urlConnection.setConnectTimeout(Resources.getInstance()
						.getIntProperty(OSM_NOMINATIM_CONNECT_TIMEOUT_IN_MS,
								10000));
				urlConnection
						.setReadTimeout(Resources.getInstance().getIntProperty(
								OSM_NOMINATIM_READ_TIMEOUT_IN_MS, 30000));
			}
			InputStream urlStream = urlConnection.getInputStream();
			result = Tools.getFile(new InputStreamReader(urlStream));
			result = new String(result.getBytes(), "UTF-8");
			logger.fine(result + " was received for search " + b);
		} finally {
			mMindMapController.getFrame().setWaitingCursor(false);
		}
		return result;
	}

	protected Place getErrorPlace(final String errorString, String errorLevel) {
		Place place = new Place();
		place.setDisplayName(errorString);
		place.setOsmType(errorLevel);
		Coordinate cursorPosition = getMap().getCursorPosition();
		place.setLat(cursorPosition.getLat());
		place.setLon(cursorPosition.getLon());
		return place;
	}

	public boolean isClickEnabled() {
		return mClickEnabled;
	}

	public void setClickEnabled(boolean pClickEnabled) {
		mClickEnabled = pClickEnabled;
	}

	public static TileSourceStore[] getmTileSources() {
		return sTileSources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent pE) {
		String statusText = "";
		// here, we look wether or not the cursor is above a node.
		MapMarkerBase mapMarker = checkHit(mTimerMouseEvent);
		if (mapMarker instanceof MapMarkerLocation) {
			MapNodePositionHolder posHolder = ((MapMarkerLocation) mapMarker)
					.getNodePositionHolder();

			logger.fine("Looking for hit on node " + posHolder);
			if (posHolder != null) {
				statusText = Tools.getNodeTextHierarchy(posHolder.getNode(),
						mMapHook.getMindMapController()) + ". ";
			}
		}
		// calculate the distance to the cursor
		Coordinate coordinate = getCoordinateFromMouseEvent(mTimerMouseEvent);
		Coordinate cursorPosition = getMap().getCursorPosition();
		double distance = OsmMercator.getDistance(coordinate.getLat(),
				coordinate.getLon(), cursorPosition.getLat(),
				cursorPosition.getLon()) / 1000.0;
		Object[] messageArguments = { new Double(distance),
				new Double(coordinate.getLat()),
				new Double(coordinate.getLon()) };
		MessageFormat formatter = new MessageFormat(
				mMindMapController.getText("plugins/map/MapDialog_Distance"));
		String message = formatter.format(messageArguments);
		statusText += message;
		mMapHook.getStatusLabel().setText(statusText);
	}

	protected void selectContextMenuNode() {
		MindMapNode node = mCurrentPopupPositionHolder.getNode();
		selectNode(node);
	}

	protected void selectNode(MindMapNode node) {
		mMindMapController.select(node, Tools.getVectorWithSingleElement(node));
	}

	public static String getLink(MapNodePositionHolder hook) {
		String tileSource = hook.getTileSource();
		Coordinate position = hook.getPosition();
		Coordinate mapCenter = hook.getMapCenter();
		int zoom = hook.getZoom();
		return getLink(tileSource, position, mapCenter, zoom);
	}

	public static String getLink(String tileSource, Coordinate position,
			Coordinate mapCenter, int zoom) {
		String layer = "M";
		TileSourceStore tileSourceByName = FreeMindMapController
				.getTileSourceByName(tileSource);
		if (tileSourceByName != null) {
			layer = tileSourceByName.mLayerName;
		}
		/*
		 * The embedded link would work for IE, too. But it is not easy to
		 * configure as a bounding box is necessary. It reads like
		 * osm.org/export/embed.html?bbox=...
		 */
		String link = "http://www.openstreetmap.org/?" + "mlat="
				+ position.getLat() + "&mlon=" + position.getLon() + "&lat="
				+ mapCenter.getLat() + "&lon=" + mapCenter.getLon() + "&zoom="
				+ zoom + "&layers=" + layer;
		return link;
	}

	public void keyTyped(KeyEvent pEvent) {
		if (mMapHook.isSearchBarVisible()) {
			return;
		}
		Action[] specialKeyActions = { mZoomInAction, mZoomOutAction };
		Tools.invokeActionsToKeyboardLayoutDependantCharacters(pEvent,
				specialKeyActions, mMapDialog);
		if (!pEvent.isConsumed() && !pEvent.isActionKey()
				&& (Character.isLetter(pEvent.getKeyChar()))
				&& ((pEvent.getModifiers() & MODIFIERS_WITHOUT_SHIFT) == 0)) {
			// open search bar and process event.
			// logger.info("Key event processed: " + pEvent);
			mMapHook.toggleSearchBar(pEvent);
			mMapHook.setSingleSearch();
		}

	}

	public void keyReleased(KeyEvent pEvent) {
	}

	public void keyPressed(KeyEvent pEvent) {
		if (mMapHook.isSearchBarVisible()) {
			return;
		}
		int modifiers = pEvent.getModifiers() & MODIFIERS_WITHOUT_SHIFT;
		// only plain of shifted cursor keys are consumed here.
		if (modifiers == 0) {
			int dx = MOVE_PIXEL_AMOUNT;
			int dy = MOVE_PIXEL_AMOUNT;
			if (pEvent.isShiftDown()) {
				dx = (int) (map.getWidth() * PAGE_DOWN_FACTOR);
				dy = (int) (map.getHeight() * PAGE_DOWN_FACTOR);
			}
			switch (pEvent.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				map.moveMap(-dx, 0);
				pEvent.consume();
				break;
			case KeyEvent.VK_RIGHT:
				map.moveMap(dx, 0);
				pEvent.consume();
				break;
			case KeyEvent.VK_UP:
				map.moveMap(0, -dy);
				pEvent.consume();
				break;
			case KeyEvent.VK_DOWN:
				map.moveMap(0, dy);
				pEvent.consume();
				break;
			}
		}
	}

	public Vector getPositionHolderVector() {
		return mPositionHolderVector;
	}

	public int getPositionHolderIndex() {
		return mPositionHolderIndex;
	}

	/**
	 * @param positionHolderIndex
	 * @return true, if positionHolderIndex is ok.
	 */
	public boolean checkPositionHolderIndex(int positionHolderIndex) {
		return !(positionHolderIndex < -1 || positionHolderIndex >= mPositionHolderVector
				.size());
	}

	public void setPositionHolderIndex(int positionHolderIndex) {
		if (!checkPositionHolderIndex(positionHolderIndex)) {
			throw new IllegalArgumentException("Index out of range "
					+ positionHolderIndex);
		}
		mPositionHolderIndex = positionHolderIndex;
	}

	/**
	 * @param pListener
	 */
	public void addCursorPositionListener(CursorPositionListener pListener) {
		mCursorPositionListeners.add(pListener);
	}

	/**
	 * @param pSelected
	 * @param pPlace
	 */
	public void addNode(MindMapNode pSelected, Place pPlace) {
		addNode(pSelected, pPlace.getDisplayName(), pPlace.getLat(),
				pPlace.getLon());
	}

	public void addNode(MindMapNode pSelected, String pText, double lat,
			double lon) {
		final MindMapNode targetNode = pSelected;
		final MindMapNode newNode = insertNewNode(targetNode);
		mMindMapController.setNodeText(newNode, pText);
		placeNodeAt(newNode, new Coordinate(lat, lon), map.getPosition(),
				map.getZoom());
	}

	private class AddSearchResultsToMapTask extends FreeMindTask {

		private Place[] mPlaces;

		public AddSearchResultsToMapTask(int[] pSelectedRows) {
			super(mMapDialog, pSelectedRows.length, MAP_DIALOG_PROGRESS_MESSAGE);
			// deep copy
			mPlaces = new Place[pSelectedRows.length];
			for (int i = 0; i < pSelectedRows.length; i++) {
				mPlaces[i] = mMapHook.getPlace(pSelectedRows[i]);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see freemind.common.FreeMindTask#processAction()
		 */
		protected boolean processAction() throws Exception {
			int selIndex = getRounds();
			final Place place = mPlaces[selIndex];
			mProgressDescription = new ProgressDescription(
					MAP_DIALOG_ADD_PLACES,
					new Object[] { place.getDisplayName() });
			final MindMapNode selected = getMindMapController().getSelected();
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					addNode(selected, place);
				}
			});
			return true;
		}

	}

	public void addSearchResultsToMap(int[] pSelectedRows) {
		AddSearchResultsToMapTask task = new AddSearchResultsToMapTask(
				pSelectedRows);
		task.start();
	}

	private ModeController getMindMapController() {
		return mMindMapController;
	}

}
