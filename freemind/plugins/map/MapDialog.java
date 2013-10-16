package plugins.map;

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource.Mapnik;

import plugins.map.FreeMindMapController.CursorPositionListener;
import plugins.map.MapNodePositionHolder.MapNodePositionListener;
import plugins.map.Registration.NodeVisibilityListener;
import accessories.plugins.time.TableSorter;
import freemind.common.TextTranslator;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.actions.generated.instance.MapLocationStorage;
import freemind.controller.actions.generated.instance.MapWindowConfigurationStorage;
import freemind.controller.actions.generated.instance.Place;
import freemind.controller.actions.generated.instance.TableColumnSetting;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.view.MapModule;
import freemind.view.mindmapview.NodeView;

/**
 * 
 * Demonstrates the usage of {@link JMapViewer}
 * 
 * @author Jan Peter Stotz adapted for FreeMind by Chris.
 */
public class MapDialog extends MindMapHookAdapter implements
		JMapViewerEventListener, MapModuleChangeObserver,
		MapNodePositionListener, NodeSelectionListener, NodeVisibilityListener {

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = MapDialog.class
			.getName();

	static final String TILE_CACHE_CLASS = "tile_cache_class";

	static final String FILE_TILE_CACHE_DIRECTORY = "file_tile_cache_directory";

	public static final String TILE_CACHE_MAX_AGE = "tile_cache_max_age";

	private static final int SEARCH_DESCRIPTION_COLUMN = 0;

	public static final int SEARCH_DISTANCE_COLUMN = 1;

	private JCursorMapViewer map = null;

	private JLabel zoomValue = null;

	private JLabel mperpLabelValue = null;

	private MindMapController mMyMindMapController;

	private JDialog mMapDialog;

	private HashMap /* < MapNodePositionHolder, MapMarkerLocation > */mMarkerMap = new HashMap();

	private CloseAction mCloseAction;

	private JPanel mSearchFieldPanel;

	private JSplitPane mSearchSplitPane;

	private boolean mSearchBarVisible;

	private JPanel mSearchPanel;

	private JTextField mSearchTerm;

	static final String MAP_HOOK_NAME = "plugins/map/MapDialog.properties";

	public static final String TILE_CACHE_PURGE_TIME = "tile_cache_purge_time";

	public static final long TILE_CACHE_PURGE_TIME_DEFAULT = 1000 * 60 * 10;

	private static final String SEARCH_DESCRIPTION_COLUMN_TEXT = "plugins/map/MapDialog.Description";

	private static final String SEARCH_DISTANCE_COLUMN_TEXT = "plugins/map/MapDialog.Distance";

	private JLabel mStatusLabel;

	/**
	 * Indicates that after a search, when a place was selected, the search
	 * dialog should close
	 */
	private boolean mSingleSearch = false;

	private JTable mResultTable;

	private ResultTableModel mResultTableModel;

	private TableSorter mResultTableSorter;

	private Color mTableOriginalBackgroundColor;

	/**
	 * I know, that the JSplitPane collects this information, but I want to
	 * handle it here.
	 */
	private int mLastDividerPosition = 300;

	private boolean mLimitSearchToRegion = false;

	private JLabel mSearchStringLabel;

	private String mResourceSearchLocationString;

	private String mResourceSearchString;

	private final class CloseAction extends AbstractAction {

		public CloseAction() {
			super(getResourceString("MapDialog_close"));
		}

		public void actionPerformed(ActionEvent arg0) {
			disposeDialog();
		}
	}

	/**
	 * @author foltin
	 * @date 25.04.2012
	 */
	public final class ResultTableModel extends AbstractTableModel implements
			CursorPositionListener {
		/**
		 * 
		 */
		private final String[] COLUMNS = new String[] {
				SEARCH_DESCRIPTION_COLUMN_TEXT, SEARCH_DISTANCE_COLUMN_TEXT };
		Vector mData = new Vector();
		private Coordinate mCursorCoordinate = new Coordinate(0, 0);
		private HashMap mMapSearchMarkerLocationHash = new HashMap();
		private final TextTranslator mTextTranslator;

		/**
		 * @param pCursorCoordinate
		 */
		public ResultTableModel(Coordinate pCursorCoordinate,
				TextTranslator pTextTranslator) {
			super();
			mCursorCoordinate = pCursorCoordinate;
			mTextTranslator = pTextTranslator;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		public Class getColumnClass(int arg0) {
			switch (arg0) {
			case SEARCH_DESCRIPTION_COLUMN:
				return String.class;
			case SEARCH_DISTANCE_COLUMN:
				return Double.class;
			default:
				return Object.class;
			}
		}

		/**
		 * @param pPlace
		 */
		public void addPlace(Place pPlace) {
			mData.add(pPlace);
			final int row = mData.size() - 1;
			MapSearchMarkerLocation location = new MapSearchMarkerLocation(
					MapDialog.this, pPlace);
			mMapSearchMarkerLocationHash.put(pPlace, location);
			getMap().addMapMarker(location);
			fireTableRowsInserted(row, row);
		}

		public MapSearchMarkerLocation getMapSearchMarkerLocation(int index) {
			if (index >= 0 && index < getRowCount()) {
				Place place = getPlace(index);
				return (MapSearchMarkerLocation) mMapSearchMarkerLocationHash
						.get(place);
			}
			throw new IllegalArgumentException("Index " + index
					+ " is out of range.");
		}

		public Place getPlace(int pIndex) {
			return (Place) mData.get(pIndex);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int pColumn) {
			return mTextTranslator.getText(COLUMNS[pColumn]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return mData.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return 2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int pRowIndex, int pColumnIndex) {
			final Place place = getPlace(pRowIndex);
			switch (pColumnIndex) {
			case SEARCH_DISTANCE_COLUMN:
				final double value = OsmMercator.getDistance(
						mCursorCoordinate.getLat(), mCursorCoordinate.getLon(),
						place.getLat(), place.getLon()) / 1000.0;
				if (Double.isInfinite(value) || Double.isNaN(value)) {
					return Double.valueOf(-1.0);
				}
				return new Double(value);
			case SEARCH_DESCRIPTION_COLUMN:
				return place.getDisplayName();
			}
			return null;
		}

		/**
		 * 
		 */
		public void clear() {
			// clear old search results:
			for (Iterator it = mMapSearchMarkerLocationHash.keySet().iterator(); it
					.hasNext();) {
				Place place = (Place) it.next();
				MapSearchMarkerLocation location = (MapSearchMarkerLocation) mMapSearchMarkerLocationHash
						.get(place);
				getMap().removeMapMarker(location);
			}
			mMapSearchMarkerLocationHash.clear();
			mData.clear();
			fireTableDataChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see plugins.map.FreeMindMapController.CursorPositionListener#
		 * cursorPositionChanged(org.openstreetmap.gui.jmapviewer.Coordinate)
		 */
		public void cursorPositionChanged(Coordinate pCursorPosition) {
			mCursorCoordinate = pCursorPosition;
			fireTableDataChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.extensions.HookAdapter#startupMapHook()
	 */
	public void startupMapHook() {
		super.startupMapHook();
		mMyMindMapController = super.getMindMapController();
		getMindMapController().getController().getMapModuleManager()
				.addListener(this);
		mMapDialog = new JDialog(getController().getFrame().getJFrame(), false /* unmodal */);
		mMapDialog.setTitle(getResourceString("MapDialog_title"));
		mMapDialog
				.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mMapDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				disposeDialog();
			}
		});
		mCloseAction = new CloseAction();
		// the action title is changed by the following method, thus we create
		// another close action.
		Tools.addEscapeActionToDialog(mMapDialog, new CloseAction());
		mMapDialog.setSize(400, 400);

		map = new JCursorMapViewer(getMindMapController(), mMapDialog,
				getRegistration().getTileCache(), this);
		map.addJMVListener(this);
		map.setScrollWrapEnabled(true);
		FreeMindMapController.changeTileSource(Mapnik.class.getName(), map);
		OsmTileLoader loader = getRegistration().createTileLoader(map);
		map.setTileLoader(loader);
		map.setCursorPosition(new Coordinate(49.8, 8.8));
		map.setUseCursor(true);

		mMapDialog.setLayout(new BorderLayout());
		mSearchPanel = new JPanel(new BorderLayout());
		mResourceSearchString = getResourceString("MapDialog_Search");
		mResourceSearchLocationString = getResourceString("MapDialog_Search_Location");
		mSearchStringLabel = new JLabel(mResourceSearchString);
		mSearchTerm = new JTextField();
		mSearchTerm.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent pEvent) {
				if (pEvent.getKeyCode() == KeyEvent.VK_DOWN
						&& pEvent.getModifiers() == 0) {
					logger.info("Set Focus to search list.");
					mResultTable.requestFocusInWindow();
					mResultTable.getSelectionModel().setSelectionInterval(0, 0);
					pEvent.consume();
				}
			}
		});
		mSearchFieldPanel = new JPanel();
		mSearchFieldPanel.setLayout(new BorderLayout(10, 0));
		JButton clearButton = new JButton(new ImageIcon(Resources.getInstance()
				.getResource("images/clear_box.png")));
		clearButton.setFocusable(false);
		mSearchFieldPanel.add(mSearchStringLabel, BorderLayout.WEST);
		mSearchFieldPanel.add(mSearchTerm, BorderLayout.CENTER);
		mSearchFieldPanel.add(clearButton, BorderLayout.EAST);
		mResultTable = new JTable();
		mTableOriginalBackgroundColor = mResultTable.getBackground();

		mResultTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		mResultTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent pEvent) {
				int index = mResultTable.getSelectedRow();
				if (index == 0 && pEvent.getKeyCode() == KeyEvent.VK_UP
						&& pEvent.getModifiers() == 0) {
					logger.info("Set Focus to search item.");
					mResultTable.clearSelection();
					mSearchTerm.requestFocusInWindow();
					pEvent.consume();
					return;
				}
				if (pEvent.getKeyCode() == KeyEvent.VK_ENTER
						&& pEvent.getModifiers() == 0 && index >= 0) {
					logger.info("Set result in map.");
					pEvent.consume();
					displaySearchItem(index);
					return;
				}
				if (pEvent.getKeyCode() == KeyEvent.VK_ENTER
						&& pEvent.isControlDown() && index >= 0) {
					pEvent.consume();
					addSearchResultsToMap();
					displaySearchItem(index);
					return;
				}

			}
		});
		mResultTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent pE) {
						clearIndexes();
						final int selectedRow = mResultTable.getSelectedRow();
						if (selectedRow >= 0
								&& selectedRow < mResultTableSorter
										.getRowCount()) {
							int index = selectedRow;
							index = mResultTableSorter.modelIndex(index);
							MapSearchMarkerLocation marker = mResultTableModel
									.getMapSearchMarkerLocation(index);
							marker.setSelected(true);
						}
						mResultTable.repaint();
						getMap().repaint();
					}

					private void clearIndexes() {
						for (int i = 0; i < mResultTableModel.getRowCount(); i++) {
							MapSearchMarkerLocation marker = mResultTableModel
									.getMapSearchMarkerLocation(i);
							marker.setSelected(false);
						}
					}
				});
		mResultTable.getTableHeader().setReorderingAllowed(false);
		mResultTableModel = new ResultTableModel(getMap().getCursorPosition(),
				getMindMapController());
		getFreeMindMapController().addCursorPositionListener(mResultTableModel);
		mResultTableSorter = new TableSorter(mResultTableModel);
		mResultTable.setModel(mResultTableSorter);
		mResultTableSorter.setTableHeader(mResultTable.getTableHeader());
		mResultTableSorter.setColumnComparator(String.class,
				TableSorter.LEXICAL_COMPARATOR);
		mResultTableSorter.setColumnComparator(Double.class,
				TableSorter.COMPARABLE_COMAPRATOR);
		// Sort by default by date.
		mResultTableSorter.setSortingStatus(SEARCH_DISTANCE_COLUMN,
				TableSorter.ASCENDING);

		clearButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				mResultTableModel.clear();
				mSearchTerm.setText("");
				mResultTable.setBackground(mTableOriginalBackgroundColor);
			}
		});
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// int index = mResultTable.locationToIndex(e.getPoint());
					int index = mResultTable.getSelectedRow();
					displaySearchItem(index);
				}
			}
		};
		mResultTable.addMouseListener(mouseListener);

		mSearchTerm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				search(mSearchTerm.getText(), false);
			}
		});
		final JScrollPane resultTableScrollPane = new JScrollPane(mResultTable);
		mSearchPanel.setLayout(new BorderLayout());
		mSearchPanel.add(mSearchFieldPanel, BorderLayout.NORTH);
		mSearchPanel.add(resultTableScrollPane, BorderLayout.CENTER);
		mSearchBarVisible = true;
		mSearchSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				mSearchPanel, map);
		mSearchSplitPane.setContinuousLayout(true);
		mSearchSplitPane.setOneTouchExpandable(false);
		Tools.correctJSplitPaneKeyMap();
		mSearchSplitPane.setResizeWeight(0d);
		mSearchSplitPane.addPropertyChangeListener(
				JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new PropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent pEvt) {
						final int dividerLocation = mSearchSplitPane
								.getDividerLocation();
						if (dividerLocation > 1) {
							mLastDividerPosition = dividerLocation;
							logger.info("Setting last divider to "
									+ mLastDividerPosition);
						}
					}
				});
		mMapDialog.add(mSearchSplitPane, BorderLayout.CENTER);
		mStatusLabel = new JLabel(" ");
		mMapDialog.add(mStatusLabel, BorderLayout.SOUTH);

		getRegistration().registerMapNodePositionListener(this);
		getRegistration().registerNodeVisibilityListener(this);
		getMindMapController().registerNodeSelectionListener(this, true);

		mMapDialog.validate();
		// restore preferences:
		// Retrieve window size and column positions.
		MapWindowConfigurationStorage storage = (MapWindowConfigurationStorage) getMindMapController()
				.decorateDialog(mMapDialog, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		if (storage != null) {
			map.setZoomContolsVisible(storage.getZoomControlsVisible());
			map.setTileGridVisible(storage.getTileGridVisible());
			map.setMapMarkerVisible(storage.getShowMapMarker());
			map.setHideFoldedNodes(storage.getHideFoldedNodes());
			int column = 0;
			for (Iterator i = storage.getListTableColumnSettingList()
					.iterator(); i.hasNext();) {
				TableColumnSetting setting = (TableColumnSetting) i.next();
				mResultTable.getColumnModel().getColumn(column)
						.setPreferredWidth(setting.getColumnWidth());
				mResultTableSorter.setSortingStatus(column,
						setting.getColumnSorting());
				column++;
			}
			// default is false, so if true, toggle it.
			if (storage.getLimitSearchToVisibleArea()) {
				toggleLimitSearchToRegion();
			}
			if (!storage.getSearchControlVisible()) {
				toggleSearchBar();
			}
			mLastDividerPosition = storage.getLastDividerPosition();
			mSearchSplitPane.setDividerLocation(mLastDividerPosition);
			// restore last map positions
			final Vector positionHolderVector = getFreeMindMapController()
					.getPositionHolderVector();
			for (Iterator it = storage.getListMapLocationStorageList()
					.iterator(); it.hasNext();) {
				MapLocationStorage location = (MapLocationStorage) it.next();
				positionHolderVector
						.add(new FreeMindMapController.PositionHolder(location
								.getCursorLatitude(), location
								.getCursorLongitude(), location.getZoom()));
			}
			if (getFreeMindMapController().checkPositionHolderIndex(
					storage.getMapLocationStorageIndex())) {
				getFreeMindMapController().setPositionHolderIndex(
						storage.getMapLocationStorageIndex());
			}
			// TODO: Better would be to store these data per map.
			map.setDisplayPositionByLatLon(storage.getMapCenterLatitude(),
					storage.getMapCenterLongitude(), storage.getZoom());
			final Coordinate position = new Coordinate(
					storage.getCursorLatitude(), storage.getCursorLongitude());
			getFreeMindMapController().setCursorPosition(position);
			FreeMindMapController
					.changeTileSource(storage.getTileSource(), map);
		}
		addMarkersToMap();
		mMapDialog.setVisible(true);
		getRegistration().setMapDialog(this);
	}

	public void addMarkersToMap() {
		// add known markers to the map.
		Set mapNodePositionHolders = getAllMapNodePositionHolders();
		for (Iterator it = mapNodePositionHolders.iterator(); it.hasNext();) {
			MapNodePositionHolder nodePositionHolder = (MapNodePositionHolder) it
					.next();
			boolean visible = !nodePositionHolder.hasFoldedParents();
			changeVisibilityOfNode(nodePositionHolder, visible);
		}

	}

	protected void changeVisibilityOfNode(
			MapNodePositionHolder nodePositionHolder, boolean pVisible) {
		if (!pVisible && map.isHideFoldedNodes()) {
			removeMapMarker(nodePositionHolder);
		} else {
			addMapMarker(nodePositionHolder);
		}
	}

	public Registration getRegistration() {
		return (Registration) getPluginBaseClass();
	}

	public void toggleSearchBar() {
		mSingleSearch = false;
		toggleSearchBar(null);
	}

	public void toggleSearchBar(AWTEvent pEvent) {
		if (mSearchBarVisible) {
			mLastDividerPosition = mSearchSplitPane.getDividerLocation();
			logger.fine("Setting last divider to " + mLastDividerPosition);
			// clear results
			mResultTableModel.clear();
			// hide search bar
			mSearchSplitPane.setBottomComponent(null);
			mMapDialog.remove(mSearchSplitPane);
			mMapDialog.add(map, BorderLayout.CENTER);
			mMapDialog.requestFocusInWindow();
			mSearchBarVisible = false;
		} else {
			// show search bar
			mMapDialog.remove(map);
			mMapDialog.add(mSearchSplitPane, BorderLayout.CENTER);
			mSearchSplitPane.setBottomComponent(map);
			mSearchSplitPane.setDividerLocation(mLastDividerPosition);
			focusSearchTerm();
			mSearchBarVisible = true;
		}
		mMapDialog.validate();
		if (pEvent != null) {
			mSearchTerm.setText("");
			mSearchTerm.dispatchEvent(pEvent);
			/* Special for mac, as otherwise, everything is selected... GRRR. */
			if (Tools.isMacOsX()) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						mSearchTerm.setCaretPosition(mSearchTerm.getDocument()
								.getLength());
					}
				});
			}
		}
	}

	protected void focusSearchTerm() {
		mSearchTerm.selectAll();
		mSearchTerm.requestFocusInWindow();
	}

	/**
	 * @return a set of MapNodePositionHolder elements of all nodes (even if
	 *         hidden)
	 */
	public Set getAllMapNodePositionHolders() {
		return getRegistration().getMapNodePositionHolders();
	}

	/**
	 * @return a set of MapNodePositionHolder elements to those nodes currently
	 *         displayed (ie. not hidden).
	 */
	public Set getMapNodePositionHolders() {
		return mMarkerMap.keySet();
	}

	protected void addMapMarker(MapNodePositionHolder nodePositionHolder) {
		if (mMarkerMap.containsKey(nodePositionHolder)) {
			// already present.
			logger.fine("Node " + nodePositionHolder + " already present.");
			return;
		}
		Coordinate position = nodePositionHolder.getPosition();
		logger.fine("Adding map position for " + nodePositionHolder.getNode()
				+ " at " + position);
		MapMarkerLocation marker = new MapMarkerLocation(nodePositionHolder,
				this);
		map.addMapMarker(marker);
		mMarkerMap.put(nodePositionHolder, marker);
	}

	protected void removeMapMarker(MapNodePositionHolder pMapNodePositionHolder) {
		MapMarkerLocation marker = (MapMarkerLocation) mMarkerMap
				.remove(pMapNodePositionHolder);
		if (marker != null) {
			map.removeMapMarker(marker);
		}
	}

	/**
	 * Overwritten, as this dialog is not modal, but after the plugin has
	 * terminated, the dialog is still present and needs the controller to store
	 * its values.
	 * */
	public MindMapController getMindMapController() {
		return mMyMindMapController;
	}

	public FreeMindMapController getFreeMindMapController() {
		return map.getFreeMindMapController();
	}

	/**
	 * 
	 */
	public void disposeDialog() {
		Registration registration = (Registration) getPluginBaseClass();
		if (registration != null) {
			// on close, it is null. Why?
			registration.setMapDialog(null);
			registration.deregisterMapNodePositionListener(this);
			registration.deregisterNodeVisibilityListener(this);
		}
		getMindMapController().deregisterNodeSelectionListener(this);

		// store window positions:
		MapWindowConfigurationStorage storage = new MapWindowConfigurationStorage();
		// Set coordinates
		storage.setZoom(map.getZoom());
		Coordinate position = map.getPosition();
		storage.setMapCenterLongitude(position.getLon());
		storage.setMapCenterLatitude(position.getLat());
		Coordinate cursorPosition = map.getCursorPosition();
		storage.setCursorLongitude(cursorPosition.getLon());
		storage.setCursorLatitude(cursorPosition.getLat());
		storage.setTileSource(map.getTileController().getTileSource()
				.getClass().getName());
		storage.setTileGridVisible(map.isTileGridVisible());
		storage.setZoomControlsVisible(map.getZoomContolsVisible());
		storage.setShowMapMarker(map.getMapMarkersVisible());
		storage.setSearchControlVisible(mSearchBarVisible);
		storage.setLimitSearchToVisibleArea(mLimitSearchToRegion);
		storage.setHideFoldedNodes(map.isHideFoldedNodes());
		storage.setLastDividerPosition(mLastDividerPosition);
		for (int i = 0; i < mResultTable.getColumnCount(); i++) {
			TableColumnSetting setting = new TableColumnSetting();
			setting.setColumnWidth(mResultTable.getColumnModel().getColumn(i)
					.getWidth());
			setting.setColumnSorting(mResultTableSorter.getSortingStatus(i));
			storage.addTableColumnSetting(setting);
		}
		for (Iterator it = getFreeMindMapController().getPositionHolderVector()
				.iterator(); it.hasNext();) {
			FreeMindMapController.PositionHolder pos = (FreeMindMapController.PositionHolder) it
					.next();
			MapLocationStorage mapLocationStorage = new MapLocationStorage();
			mapLocationStorage.setCursorLatitude(pos.lat);
			mapLocationStorage.setCursorLongitude(pos.lon);
			mapLocationStorage.setZoom(pos.zoom);
			storage.addMapLocationStorage(mapLocationStorage);
		}
		storage.setMapLocationStorageIndex(getFreeMindMapController()
				.getPositionHolderIndex());
		getMindMapController().storeDialogPositions(mMapDialog, storage,
				WINDOW_PREFERENCE_STORAGE_PROPERTY);

		getMindMapController().getController().getMapModuleManager()
				.removeListener(this);
		mMapDialog.setVisible(false);
		mMapDialog.dispose();
	}

	private void updateZoomParameters() {
		if (mperpLabelValue != null)
			mperpLabelValue.setText(format("%s", map.getMeterPerPixel()));
		if (zoomValue != null)
			zoomValue.setText(format("%s", map.getZoom()));
	}

	/**
	 * @param pString
	 * @param pMeterPerPixel
	 * @return
	 */
	private String format(String pString, double pObject) {
		return "" + pObject;
	}

	public void processCommand(JMVCommandEvent command) {
		if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM)
				|| command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
			updateZoomParameters();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * isMapModuleChangeAllowed(freemind.view.MapModule, freemind.modes.Mode,
	 * freemind.view.MapModule, freemind.modes.Mode)
	 */
	public boolean isMapModuleChangeAllowed(MapModule pOldMapModule,
			Mode pOldMode, MapModule pNewMapModule, Mode pNewMode) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * beforeMapModuleChange(freemind.view.MapModule, freemind.modes.Mode,
	 * freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void beforeMapModuleChange(MapModule pOldMapModule, Mode pOldMode,
			MapModule pNewMapModule, Mode pNewMode) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.controller.MapModuleManager.MapModuleChangeObserver#afterMapClose
	 * (freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
		disposeDialog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * afterMapModuleChange(freemind.view.MapModule, freemind.modes.Mode,
	 * freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void afterMapModuleChange(MapModule pOldMapModule, Mode pOldMode,
			MapModule pNewMapModule, Mode pNewMode) {
		disposeDialog();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#
	 * numberOfOpenMapInformation(int, int)
	 */
	public void numberOfOpenMapInformation(int pNumber, int pIndex) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.map.MapNodePositionHolder.MapNodePositionListener#registerMapNode
	 * (plugins.map.MapNodePositionHolder)
	 */
	public void registerMapNode(MapNodePositionHolder pMapNodePositionHolder) {
		addMapMarker(pMapNodePositionHolder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.map.MapNodePositionHolder.MapNodePositionListener#deregisterMapNode
	 * (plugins.map.MapNodePositionHolder)
	 */
	public void deregisterMapNode(MapNodePositionHolder pMapNodePositionHolder) {
		removeMapMarker(pMapNodePositionHolder);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController.NodeSelectionListener#onUpdateNodeHook(
	 * freemind.modes.MindMapNode)
	 */
	public void onUpdateNodeHook(MindMapNode pNode) {
		// update MapMarkerLocation if present:
		MapNodePositionHolder hook = MapNodePositionHolder.getHook(pNode);
		if (hook != null && mMarkerMap.containsKey(hook)) {
			MapMarkerLocation location = (MapMarkerLocation) mMarkerMap
					.get(hook);
			location.update();
			location.repaint();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController.NodeSelectionListener#onSelectHook(freemind
	 * .view.mindmapview.NodeView)
	 */
	public void onFocusNode(NodeView pNode) {
	}

	public void selectMapPosition(NodeView pNode, boolean sel) {
		// test for map position:
		MapNodePositionHolder hook = MapNodePositionHolder.getHook(pNode
				.getModel());
		if (hook != null) {
			if (mMarkerMap.containsKey(hook)) {
				MapMarkerLocation location = (MapMarkerLocation) mMarkerMap
						.get(hook);
				location.setSelected(sel);
				map.repaint();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController.NodeSelectionListener#onDeselectHook(freemind
	 * .view.mindmapview.NodeView)
	 */
	public void onLostFocusNode(NodeView pNode) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController.NodeSelectionListener#onSaveNode(freemind
	 * .modes.MindMapNode)
	 */
	public void onSaveNode(MindMapNode pNode) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * freemind.modes.ModeController.NodeSelectionListener#onSelectionChange
	 * (freemind.modes.MindMapNode, boolean)
	 */
	public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
		selectMapPosition(pNode, pIsSelected);

	}

	public CloseAction getCloseAction() {
		return mCloseAction;
	}

	public boolean isSearchBarVisible() {
		return mSearchBarVisible;
	}

	/**
	 * @return < MapNodePositionHolder, MapMarkerLocation > of those nodes
	 *         currently displayed (ie. not hidden)
	 */
	public Map getMarkerMap() {
		return Collections.unmodifiableMap(mMarkerMap);
	}

	public JCursorMapViewer getMap() {
		return map;
	}

	public void displaySearchItem(int index) {
		Place place = getPlace(index);
		getFreeMindMapController().setCursorPosition(place);
		if (mSingleSearch && isSearchBarVisible()) {
			toggleSearchBar();
		}
		mSingleSearch = false;
	}

	public Place getPlace(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("Index " + index
					+ " out of bounds.");
		}
		index = mResultTableSorter.modelIndex(index);
		Place place = mResultTableModel.getPlace(index);
		return place;
	}

	public JDialog getMapDialog() {
		return mMapDialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * plugins.map.Registration.NodeVisibilityListener#nodeVisibilityChanged
	 * (boolean)
	 */
	public void nodeVisibilityChanged(
			MapNodePositionHolder pMapNodePositionHolder, boolean pVisible) {
		changeVisibilityOfNode(pMapNodePositionHolder, pVisible);
	}

	public JLabel getStatusLabel() {
		return mStatusLabel;
	}

	public void search(String searchText, boolean pSelectFirstResult) {
		if (!isSearchBarVisible()) {
			toggleSearchBar();
		}
		mSearchTerm.setText(searchText);
		boolean resultOk = getFreeMindMapController().search(mResultTableModel,
				mResultTable, searchText, mTableOriginalBackgroundColor);
		final int rowCount = mResultTableModel.getRowCount();
		if (resultOk) {
			if (mSingleSearch && rowCount == 1) {
				displaySearchItem(0);
				this.map.requestFocus();
				return;
			}
			if (pSelectFirstResult) {
				if (rowCount > 0) {
					displaySearchItem(0);
				}
			}
			mResultTable.requestFocus();
		} else {
			mSearchTerm.requestFocus();
		}

	}

	/**
	 * 
	 */
	public void setSingleSearch() {
		mSingleSearch = true;

	}

	/**
	 * @return
	 */
	public boolean isLimitSearchToRegion() {
		return mLimitSearchToRegion;
	}

	/**
	 * 
	 */
	public void toggleLimitSearchToRegion() {
		mLimitSearchToRegion = !mLimitSearchToRegion;
		if (mLimitSearchToRegion) {
			mSearchStringLabel.setText(mResourceSearchLocationString);
		} else {
			mSearchStringLabel.setText(mResourceSearchString);
		}
		mSearchStringLabel.validate();
	}

	protected void addSearchResultsToMap() {
		int[] selectedRows = mResultTable.getSelectedRows();
		logger.info("Add results to map.");
		getFreeMindMapController().addSearchResultsToMap(selectedRows);
	}
}
