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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource.Mapnik;

import plugins.map.MapNodePositionHolder.MapNodePositionListener;
import plugins.map.Registration.NodeVisibilityListener;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.actions.generated.instance.MapWindowConfigurationStorage;
import freemind.controller.actions.generated.instance.Place;
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

	private JCursorMapViewer map = null;

	private JLabel zoomLabel = null;
	private JLabel zoomValue = null;

	private JLabel mperpLabelName = null;
	private JLabel mperpLabelValue = null;

	private MindMapController mMyMindMapController;

	private JDialog mMapDialog;

	private HashMap /* < MapNodePositionHolder, MapMarkerLocation > */mMarkerMap = new HashMap();

	private CloseAction mCloseAction;

	private JPanel mSearchFieldPanel;

	private JList mResultList;

	private boolean mSearchBarVisible;

	private JPanel mSearchPanel;

	private JTextField mSearchTerm;

	private Color mListOriginalBackgroundColor;

	static final String MAP_HOOK_NAME = "plugins/map/MapDialog.properties";

	public static final String TILE_CACHE_PURGE_TIME = "tile_cache_purge_time";

	public static final long TILE_CACHE_PURGE_TIME_DEFAULT = 1000 * 60 * 10;

	private JLabel mStatusLabel;

	private SearchResultListModel mDataModel;

	/**
	 * Indicates that after a search, when a place was selected, the search dialog should close
	 */
	private boolean mSingleSearch = false;

	private final class CloseAction extends AbstractAction {

		public CloseAction() {
			super(getResourceString("MapDialog_close"));
		}

		public void actionPerformed(ActionEvent arg0) {
			disposeDialog();
		}
	}

	public final class SearchResultListModel extends AbstractListModel {
		private final List mPlaceList;
		private HashMap mMapSearchMarkerLocationHash = new HashMap();

		// private final List mListeners;

		public SearchResultListModel() {
			this.mPlaceList = new Vector();
		}

		public int getSize() {
			return mPlaceList.size();
		}

		/**
		 * @return the name of the place belonging to index.
		 */
		public Object getElementAt(int index) {
			return getPlaceAt(index).getDisplayName();
		}

		/**
		 * @return the place belonging to index.
		 */
		public Place getPlaceAt(int index) {
			return ((Place) mPlaceList.get(index));
		}

		public List getPlaceList() {
			return Collections.unmodifiableList(mPlaceList);
		}

		public void removePlace(int index) {
			if (index < 0 || index >= mPlaceList.size()) {
				throw new IllegalArgumentException(
						"try to delete in place list with an index out of range: "
								+ index);
			}
			Place place = (Place) mPlaceList.get(index);
			logger.fine("Place "
					+ place.getDisplayName()
					+ " should be removed at " + index);
			MapMarker mapMarker = (MapMarker) mMapSearchMarkerLocationHash.remove(place);
			if (mapMarker != null) {
				MapDialog.this.getMap().removeMapMarker(mapMarker);
			}
			mPlaceList.remove(index);
			fireIntervalRemoved(mPlaceList, index, index);
		}

		public void addPlace(Place newPlace) {
			mPlaceList.add(newPlace);
			int newIndex = mPlaceList.size() - 1;
			MapSearchMarkerLocation location = new MapSearchMarkerLocation(
					MapDialog.this, newPlace);
			mMapSearchMarkerLocationHash.put(newPlace, location);
			getMap().addMapMarker(location);
			fireIntervalAdded(mPlaceList, newIndex, newIndex);
		}

		public Place getPlaceByName(String name) {
			for (Iterator iter = mPlaceList.iterator(); iter.hasNext();) {
				Place place = (Place) iter.next();
				if (place.getDisplayName().equals(name)) {
					return place;
				}
			}
			return null;
		}

		public void remove(int i) {
			removePlace(i);
		}

		public void clear() {
			for (int i = mPlaceList.size(); i > 0; --i) {
				removePlace(i - 1);
			}
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
		FreeMindMapController.changeTileSource(Mapnik.class.getName(), map);
		OsmTileLoader loader = getRegistration().createTileLoader(map);
		map.setTileLoader(loader);

		mMapDialog.setLayout(new BorderLayout());
		mSearchPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(getResourceString("MapDialog_Search"));
		mSearchTerm = new JTextField();
		mSearchTerm.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent pEvent) {
				if (pEvent.getKeyCode() == KeyEvent.VK_DOWN
						&& pEvent.getModifiers() == 0) {
					logger.info("Set Focus to search list.");
					mResultList.requestFocusInWindow();
					mResultList.setSelectedIndex(0);
					pEvent.consume();
				}
			}
		});
		mSearchTerm.addKeyListener(getFreeMindMapController());
		mSearchFieldPanel = new JPanel();
		mSearchFieldPanel.setLayout(new BorderLayout(10, 0));
		JButton clearButton = new JButton(new ImageIcon(Resources.getInstance()
				.getResource("images/clear_box.png")));
		clearButton.setFocusable(false);
		mSearchFieldPanel.add(label, BorderLayout.WEST);
		mSearchFieldPanel.add(mSearchTerm, BorderLayout.CENTER);
		mSearchFieldPanel.add(clearButton, BorderLayout.EAST);
		mDataModel = new SearchResultListModel();
		mResultList = new JList(mDataModel);
		mListOriginalBackgroundColor = mResultList.getBackground();
		mResultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// mResultList.setFocusable(false);
		mResultList.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent pEvent) {
				int index = mResultList.getSelectedIndex();
				if (index == 0 && pEvent.getKeyCode() == KeyEvent.VK_UP
						&& pEvent.getModifiers() == 0) {
					logger.info("Set Focus to search item.");
					mResultList.clearSelection();
					mSearchTerm.requestFocusInWindow();
					pEvent.consume();
					return;
				}
				if (pEvent.getKeyCode() == KeyEvent.VK_ENTER
						&& pEvent.getModifiers() == 0) {
					logger.info("Set result in map.");
					pEvent.consume();
					displaySearchItem(mDataModel, index);
					return;

				}

			}
		});
		mResultList.addKeyListener(getFreeMindMapController());
		clearButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				mDataModel.clear();
				mSearchTerm.setText("");
				mResultList.setBackground(mListOriginalBackgroundColor);
			}
		});
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = mResultList.locationToIndex(e.getPoint());
					displaySearchItem(mDataModel, index);
				}
			}
		};
		mResultList.addMouseListener(mouseListener);

		mSearchTerm.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent pE) {
				search(mSearchTerm.getText(), false);
			}
		});
		mSearchPanel.setLayout(new BorderLayout());
		mSearchPanel.add(mSearchFieldPanel, BorderLayout.NORTH);
		mSearchPanel.add(new JScrollPane(mResultList), BorderLayout.CENTER);
		mSearchBarVisible = true;
		mMapDialog.add(mSearchPanel, BorderLayout.NORTH);
		mMapDialog.add(map, BorderLayout.CENTER);
		mStatusLabel = new JLabel(" ");
		mMapDialog.add(mStatusLabel, BorderLayout.SOUTH);

		map.setCursorPosition(new Coordinate(49.8, 8.8));
		map.setUseCursor(true);
		// restore preferences:
		// Retrieve window size and column positions.
		MapWindowConfigurationStorage storage = (MapWindowConfigurationStorage) getMindMapController()
				.decorateDialog(mMapDialog, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		if (storage != null) {
			// TODO: Better would be to store these data per map.
			map.setDisplayPositionByLatLon(storage.getMapCenterLatitude(),
					storage.getMapCenterLongitude(), storage.getZoom());
			getFreeMindMapController().setCursorPosition(new Coordinate(storage.getCursorLatitude(),
					storage.getCursorLongitude()), null, 0);
			FreeMindMapController
					.changeTileSource(storage.getTileSource(), map);
			map.setZoomContolsVisible(storage.getZoomControlsVisible());
			map.setTileGridVisible(storage.getTileGridVisible());
			map.setMapMarkerVisible(storage.getShowMapMarker());
			map.setHideFoldedNodes(storage.getHideFoldedNodes());
			if (!storage.getSearchControlVisible()) {
				toggleSearchBar();
			}
		}
		addMarkersToMap();
		getRegistration().registerMapNodePositionListener(this);
		getRegistration().registerNodeVisibilityListener(this);
		getMindMapController().registerNodeSelectionListener(this, true);

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
			mDataModel.clear();
			mMapDialog.remove(mSearchPanel);
			mMapDialog.requestFocusInWindow();
		} else {
			mMapDialog.add(mSearchPanel, BorderLayout.NORTH);
			mSearchTerm.selectAll();
			mSearchTerm.requestFocusInWindow();
		}
		mMapDialog.validate();
		mSearchBarVisible = !mSearchBarVisible;
		if(pEvent != null) {
			mSearchTerm.setText("");
			mSearchTerm.dispatchEvent(pEvent);
			/* Special for mac, as otherwise, everything is selected... GRRR. */
			if (Tools.isMacOsX()) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						mSearchTerm.setCaretPosition(mSearchTerm.getDocument().getLength());
					}
				});
			}
		}
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
		storage.setHideFoldedNodes(map.isHideFoldedNodes());
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

	public void displaySearchItem(final SearchResultListModel dataModel,
			int index) {
		Place place = dataModel.getPlaceAt(index);
		getFreeMindMapController().setCursorPosition(place);
		if(mSingleSearch && isSearchBarVisible()) {
			toggleSearchBar();
		}
		mSingleSearch = false;
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
		if(!isSearchBarVisible()) {
			toggleSearchBar();
		}
		mSearchTerm.setText(searchText);
		boolean resultOk = getFreeMindMapController().search(mDataModel, mResultList,
				searchText, mListOriginalBackgroundColor);
		if(resultOk && pSelectFirstResult){
			if(mDataModel.getSize()>0){
				displaySearchItem(mDataModel, 0);
			}
		}
		if(mSingleSearch && mDataModel.getSize()==1){
			displaySearchItem(mDataModel, 0);
			this.map.requestFocus();
			return;
		}
		if (resultOk) {
			mResultList.requestFocus();
		} else {
			mSearchTerm.requestFocus();
		}
		
	}

	/**
	 * 
	 */
	public void setSingleSearch() {
		mSingleSearch  = true;
		
	}
}
