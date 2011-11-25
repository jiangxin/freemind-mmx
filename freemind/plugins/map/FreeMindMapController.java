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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import freemind.common.XmlBindingTools;
import freemind.controller.MenuItemSelectedListener;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.Place;
import freemind.controller.actions.generated.instance.Searchresults;
import freemind.main.FreeMind;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

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
		MouseListener, MouseMotionListener, MouseWheelListener {
	/**
	 * @author foltin
	 * @date 16.11.2011
	 */
	public class ChangeTileSource extends AbstractAction implements MenuItemSelectedListener {

		private final TileSource mSource;

		/**
		 * @param pSource
		 */
		public ChangeTileSource(TileSource pSource) {
			super(Resources.getInstance().getText(
					"map_ChangeTileSource_" + pSource.getClass().getName()));
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

		/* (non-Javadoc)
		 * @see freemind.controller.MenuItemSelectedListener#isSelected(javax.swing.JMenuItem, javax.swing.Action)
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
			placeNodes();
		}
	}

	/**
	 * @author foltin
	 * @date 31.10.2011
	 */
	private final class RemovePlaceNodeAction extends AbstractAction {

		public RemovePlaceNodeAction() {
			super(getText("MapControllerPopupDialog.removeplace"));
		}

		public void actionPerformed(ActionEvent actionEvent) {
			removePlaceNodes(actionEvent);
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
			showNode(actionEvent);
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
			logger.fine("Left action!");
			Coordinate cursorPosition = getMap().getCursorPosition();
			// get map marker locations:
			HashSet mapNodePositionHolders = new HashSet(
					mMapHook.getMapNodePositionHolders());
			logger.fine("Before removal " + mapNodePositionHolders.size()
					+ " elements");
			for (Iterator it = mapNodePositionHolders.iterator(); it.hasNext();) {
				MapNodePositionHolder holder = (MapNodePositionHolder) it
						.next();
				Coordinate pointPosition = holder.getPosition();
				boolean inDestinationQuadrant = destinationQuadrantCheck(
						cursorPosition, pointPosition);
				if (!inDestinationQuadrant
						|| safeEquals(pointPosition, cursorPosition)) {
					it.remove();
				}
			}
			logger.fine("After removal " + mapNodePositionHolders.size()
					+ " elements");
			// now, we have all points on the left angle -45° to 45° and search
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
				showNode(nearest);
			}
		}

		public boolean destinationQuadrantCheck(Coordinate cursorPosition,
				Coordinate pointPosition) {
			int mapZoomMax = getMaxZoom();
			int x1 = OsmMercator.LonToX(cursorPosition.getLon(), mapZoomMax);
			int y1 = OsmMercator.LatToY(cursorPosition.getLat(), mapZoomMax);
			int x2 = OsmMercator.LonToX(pointPosition.getLon(), mapZoomMax);
			int y2 = OsmMercator.LatToY(pointPosition.getLat(), mapZoomMax);
			return destinationQuadrantCheck(x1, y1, x2, y2);
		}

		public abstract boolean destinationQuadrantCheck(int x1, int y1,
				int x2, int y2);

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

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2) {
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

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2) {
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

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2) {
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

		public boolean destinationQuadrantCheck(int x1, int y1, int x2, int y2) {
			return y2 > y1 && Math.abs(y2 - y1) > Math.abs(x2 - x1);
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

	private final class ShowMapMarker extends AbstractAction  implements MenuItemSelectedListener{

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
	
	private final class TileGridVisible extends AbstractAction  implements MenuItemSelectedListener{
		
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
	
	private final class ZoomControlsVisible extends AbstractAction  implements MenuItemSelectedListener{
		
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
	
	private final class SearchControlVisible extends AbstractAction  implements MenuItemSelectedListener{
		
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
	
	JCursorMapViewer getMap() {
		return (JCursorMapViewer) map;
	}

	private static final int MOUSE_BUTTONS_MASK = MouseEvent.BUTTON3_DOWN_MASK
			| MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK;

	private static final int MAC_MOUSE_BUTTON3_MASK = MouseEvent.CTRL_DOWN_MASK
			| MouseEvent.BUTTON1_DOWN_MASK;
	private static final int MAC_MOUSE_BUTTON1_MASK = MouseEvent.BUTTON1_DOWN_MASK;

	private JPopupMenu mPopupMenu = new JPopupMenu();

	private final MindMapController mMindMapController;

	private final JDialog mMapDialog;

	protected static java.util.logging.Logger logger = freemind.main.Resources.getInstance().getLogger(
			"plugins.map.FreeMindMapController");

	private final MapDialog mMapHook;

	public FreeMindMapController(JMapViewer map,
			MindMapController pMindMapController, final JDialog pMapDialog,
			MapDialog pMapHook) {
		super(map);
		mMapHook = pMapHook;
		mMindMapController = pMindMapController;
		mMapDialog = pMapDialog;
		Action placeAction = new PlaceNodeAction();
		Action removePlaceAction = new RemovePlaceNodeAction();
		Action showAction = new ShowNodeAction();
		Action setDisplayToFitMapMarkers = new SetDisplayToFitMapMarkers();
		Action showMapMarker = new ShowMapMarker();
		Action tileGridVisible = new TileGridVisible();
		Action zoomControlsVisible = new ZoomControlsVisible();
		Action searchControlVisible = new SearchControlVisible();

		/** Menu **/
		StructuredMenuHolder menuHolder = new StructuredMenuHolder();
		JMenuBar menu = new JMenuBar();
		JMenu mainItem = new JMenu(getText("MapControllerPopupDialog.Actions"));
		menuHolder.addMenu(mainItem, "main/actions/.");
		menuHolder.addAction(placeAction, "main/actions/place");
		menuHolder.addAction(removePlaceAction, "main/actions/removeplace");
		menuHolder.addAction(pMapHook.getCloseAction(), "main/actions/close");
		JMenu viewItem = new JMenu(getText("MapControllerPopupDialog.Views"));
		menuHolder.addMenu(viewItem, "main/view/.");
		menuHolder.addAction(showAction, "main/view/showNode");
		menuHolder.addAction(setDisplayToFitMapMarkers, "main/view/setDisplayToFitMapMarkers");
		menuHolder.addSeparator("main/view/");
		for (int i = 0; i < mTileSources.length; i++) {
			TileSource source = mTileSources[i];
			menuHolder
					.addAction(new ChangeTileSource(source), "main/view/" + i);
		}
		menuHolder.addSeparator("main/view/");
		JMenuItem searchItem = menuHolder.addAction(searchControlVisible, "main/view/showSearchControl");
		String keyProp = mMindMapController.getFrame()
				.getProperty("keystroke_plugins/map/MapDialog_toggle_search");
		searchItem.setAccelerator(KeyStroke.getKeyStroke(keyProp));
		menuHolder.addAction(showMapMarker, "main/view/showMapMarker");
		menuHolder.addAction(tileGridVisible, "main/view/tileGridVisible");
		menuHolder.addAction(zoomControlsVisible, "main/view/zoomControlsVisible");
		JMenu navigationItem = new JMenu(getText("MapControllerPopupDialog.Navigation"));
		menuHolder.addMenu(navigationItem, "main/navigation/.");
		menuHolder.addAction(new MoveLeftAction(), "main/navigation/moveLeft");
		menuHolder.addAction(new MoveRightAction(), "main/navigation/moveRight");
		menuHolder.addAction(new MoveUpAction(), "main/navigation/moveUp");
		menuHolder.addAction(new MoveDownAction(), "main/navigation/moveDown");
		menuHolder.updateMenus(menu, "main/");
		mMapDialog.setJMenuBar(menu);
		/* Popup menu */
		menuHolder.addAction(placeAction, "popup/place");
		menuHolder.addAction(removePlaceAction, "popup/removeplace");
		menuHolder.addAction(showAction, "popup/showNode");
		menuHolder.updateMenus(mPopupMenu, "popup/");
	}

	/**
	 */
	protected void placeNodes() {
		MindMapNode selected = mMindMapController.getSelected();
		MapNodePositionHolder hook = MapNodePositionHolder.getHook(selected);
		if (hook == null) {
			hook = addHookToNode(selected);
		}
		if (hook != null) {
			// set parameters:
			String tileSource = getTileSource()
					.getClass().getName();
			hook.changePosition(hook, getMap().getCursorPosition(),
					map.getPosition(), map.getZoom(), tileSource);
		}
	}

	public TileSource getTileSource() {
		return getMap().getTileController().getTileSource();
	}

	/**
	 * @param pActionEvent
	 */
	public void removePlaceNodes(ActionEvent pActionEvent) {
		MindMapNode selected = mMindMapController.getSelected();
		MapNodePositionHolder hook = MapNodePositionHolder.getHook(selected);
		if (hook != null) {
			// double add == remove
			addHookToNode(selected);
		}

	}

	/**
	 * @param pActionEvent
	 */
	public void showNode(ActionEvent pActionEvent) {
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
					getMap().setCursorPosition(hook.getPosition());
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
		changeTileSource(hook.getTileSource(), map);
		getMap().setCursorPosition(hook.getPosition());
		// move map:
		Coordinate mapCenter = hook.getMapCenter();
		map.setDisplayPositionByLatLon(mapCenter.getLat(), mapCenter.getLon(),
				hook.getZoom());
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
		logger.info("Searching for tile source " + pTileSource);
		for (int i = 0; i < mTileSources.length; i++) {
			TileSource source = mTileSources[i];
			if (Tools.safeEquals(source.getClass().getName(), pTileSource)) {
				logger.info("Found  tile source " + source);
				if (pMap != null) {
					pMap.setTileSource(source);
				}
				return source;
			}
		}
		return null;
	}

	public MapNodePositionHolder addHookToNode(MindMapNode selected) {
		MapNodePositionHolder hook;
		List selecteds = Arrays.asList(new MindMapNode[] { selected });
		mMindMapController.addHook(selected, selecteds,
				MapNodePositionHolder.NODE_MAP_HOOK_NAME);
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

	private Point lastDragPoint;

	private boolean isMoving = false;

	private boolean movementEnabled = true;

	private int movementMouseButton = MouseEvent.BUTTON1;
	private int movementMouseButtonMask = MouseEvent.BUTTON1_DOWN_MASK;

	private boolean wheelZoomEnabled = true;

	private static TileSource[] mTileSources = new TileSource[] {
			new OsmTileSource.Mapnik(), new OsmTileSource.TilesAtHome(),
			new OsmTileSource.CycleMap(), new BingAerialTileSource() };

	public void mouseDragged(MouseEvent e) {
		if (!movementEnabled || !isMoving)
			return;
		// Is only the selected mouse button pressed?
		if ((e.getModifiersEx() & MOUSE_BUTTONS_MASK) == movementMouseButtonMask) {
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
	}

	public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
        	// on double click: set position of node.
        	placeNodes();
        	return;
        }
		// is button 1?
		if (e.getButton() == MouseEvent.BUTTON1 || Tools.isMacOsX()
				&& e.getModifiersEx() == MAC_MOUSE_BUTTON1_MASK) {
			setCursorPosition(e);
		}
	}

	public void setCursorPosition(MouseEvent e) {
		getMap().setCursorPosition(map.getPosition(e.getPoint()));
	}

	public void mousePressed(MouseEvent e) {
		showPopupMenu(e);
		if (e.isConsumed()) {
			return;
		}
		if (e.getButton() == movementMouseButton || Tools.isMacOsX()
				&& e.getModifiersEx() == MAC_MOUSE_BUTTON1_MASK) {
			lastDragPoint = null;
			isMoving = true;
		}
	}

	/**
	 * @param e
	 *            event.
	 */
	private void showPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			JPopupMenu popupmenu = getPopupMenu();
			if (popupmenu != null) {
				// popupmenu.addPopupMenuListener( this.popupListenerSingleton
				// );
				setCursorPosition(e);
				popupmenu.show(e.getComponent(), e.getX(), e.getY());
				e.consume();
			}
		}

	}

	public void mouseReleased(MouseEvent e) {
		showPopupMenu(e);
		if (e.isConsumed()) {
			return;
		}

		if (e.getButton() == movementMouseButton || Tools.isMacOsX()
				&& e.getButton() == MouseEvent.BUTTON1) {
			lastDragPoint = null;
			isMoving = false;
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (wheelZoomEnabled) {
			map.setZoom(map.getZoom() - e.getWheelRotation(), e.getPoint());
		}
	}

	public boolean isMovementEnabled() {
		return movementEnabled;
	}

	/**
	 * Enables or disables that the map pane can be moved using the mouse.
	 * 
	 * @param movementEnabled
	 */
	public void setMovementEnabled(boolean movementEnabled) {
		this.movementEnabled = movementEnabled;
	}

	public int getMovementMouseButton() {
		return movementMouseButton;
	}

	public JPopupMenu getPopupMenu() {
		return mPopupMenu;
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
		return wheelZoomEnabled;
	}

	public void setWheelZoomEnabled(boolean wheelZoomEnabled) {
		this.wheelZoomEnabled = wheelZoomEnabled;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		// Mac OSX simulates with ctrl + mouse 1 the second mouse button hence
		// no dragging events get fired.
		//
		if (Tools.isMacOsX()) {
			if (!movementEnabled || !isMoving)
				return;
			// Is only the selected mouse button pressed?
			if (e.getModifiersEx() == 0 /* MouseEvent.CTRL_DOWN_MASK */) {
				Point p = e.getPoint();
				if (lastDragPoint != null) {
					int diffx = lastDragPoint.x - p.x;
					int diffy = lastDragPoint.y - p.y;
					map.moveMap(diffx, diffy);
				}
				lastDragPoint = p;
			}

		}

	}

	/**
	 * Action handler for search result handling.
	 * @param pPlace
	 */
	public void setCursorPosition(Place pPlace) {
		map.setDisplayPositionByLatLon(pPlace.getLat(),
				pPlace.getLon(), map.getZoom());
		getMap().setCursorPosition(new Coordinate(pPlace.getLat(), pPlace
				.getLon()));

	}

	/**
	 * @param pText
	 * @return
	 */
	public Searchresults getSearchResults(String pText) {
		String result;
		Searchresults results = new Searchresults();
		try {
			if (true) {
				URL url = new URI("http",
						"//nominatim.openstreetmap.org/search?format=xml&q="
								+ pText, null).toURL();
				result = Tools.getFile(new InputStreamReader(url.openStream()));
			} else {
				// only for offline testing:
				result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<searchresults timestamp=\"Tue, 08 Nov 11 22:49:54 -0500\" attribution=\"Data Copyright OpenStreetMap Contributors, Some Rights Reserved. CC-BY-SA 2.0.\" querystring=\"innsbruck\" polygon=\"false\" exclude_place_ids=\"228452,25664166,26135863,25440203\" more_url=\"http://open.mapquestapi.com/nominatim/v1/search?format=xml&amp;exclude_place_ids=228452,25664166,26135863,25440203&amp;accept-language=&amp;q=innsbruck\">\n"
						+ "  <place place_id=\"228452\" osm_type=\"node\" osm_id=\"34840064\" place_rank=\"16\" boundingbox=\"47.2554266357,47.2754304504,11.3827679062,11.4027688599\" lat=\"47.2654296\" lon=\"11.3927685\" display_name=\"Innsbruck, Bezirk Innsbruck-Stadt, Innsbruck-Stadt, Tirol, Österreich, Europe\" class=\"place\" type=\"city\" icon=\"http://open.mapquestapi.com/nominatim/v1/images/mapicons/poi_place_city.p.20.png\"/>\n"
						+ "  <place place_id=\"25664166\" osm_type=\"way\" osm_id=\"18869490\" place_rank=\"27\" boundingbox=\"43.5348739624023,43.5354156494141,-71.1319198608398,-71.1316146850586\" lat=\"43.5351336524196\" lon=\"-71.1317853486877\" display_name=\"Innsbruck, New Durham, Strafford County, New Hampshire, United States of America\" class=\"highway\" type=\"service\"/>\n"
						+ "  <place place_id=\"26135863\" osm_type=\"way\" osm_id=\"18777572\" place_rank=\"27\" boundingbox=\"38.6950759887695,38.6965446472168,-91.1586227416992,-91.1520233154297\" lat=\"38.6957456083531\" lon=\"-91.1552550683042\" display_name=\"Innsbruck, Warren, Aspenhoff, Warren County, Missouri, United States of America\" class=\"highway\" type=\"service\"/>\n"
						+ "  <place place_id=\"25440203\" osm_type=\"way\" osm_id=\"18869491\" place_rank=\"27\" boundingbox=\"43.5335311889648,43.5358810424805,-71.1356735229492,-71.1316146850586\" lat=\"43.5341678362733\" lon=\"-71.1338615946084\" display_name=\"Innsbruck, New Durham, Strafford County, New Hampshire, 03855, United States of America\" class=\"highway\" type=\"service\"/>\n"
						+ "</searchresults>";
			}
			results = (Searchresults) XmlBindingTools.getInstance().unMarshall(
					result);
		} catch (Exception e) {
			freemind.main.Resources.getInstance().logException(e);
			Place place = new Place();
			place.setDisplayName(e.toString());
			place.setOsmType("ERROR");
			Coordinate cursorPosition = getMap().getCursorPosition();
			place.setLat(cursorPosition.getLat());
			place.setLon(cursorPosition.getLon());
			results.addPlace(place);
		}
		return results;
	}

}
