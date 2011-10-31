package plugins.map;

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import plugins.map.MapNodePositionHolder.MapNodePositionListener;
import plugins.map.MapNodePositionHolder.Registration;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.actions.generated.instance.MapWindowConfigurationStorage;
import freemind.main.Tools;
import freemind.modes.Mode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.hooks.MindMapHookAdapter;
import freemind.view.MapModule;

/**
 *
 * Demonstrates the usage of {@link JMapViewer}
 *
 * @author Jan Peter Stotz
 *
 */
public class MapDialog extends MindMapHookAdapter implements JMapViewerEventListener, MapModuleChangeObserver, MapNodePositionListener  {

    private static final long serialVersionUID = 1L;

	private static final String WINDOW_PREFERENCE_STORAGE_PROPERTY = MapDialog.class.getName();

    private JCursorMapViewer map = null;

    private JLabel zoomLabel=null;
    private JLabel zoomValue=null;

    private JLabel mperpLabelName=null;
    private JLabel mperpLabelValue = null;

	private MindMapController mMyMindMapController;

	private JDialog mMapDialog;
	
	private HashMap /* < MapNodePositionHolder, MapMarkerLocation > */ mMarkerMap = new HashMap();

	/* (non-Javadoc)
	 * @see freemind.extensions.HookAdapter#startupMapHook()
	 */
	public void startupMapHook() {
		// TODO Auto-generated method stub
		super.startupMapHook();
		mMyMindMapController = super.getMindMapController();
		getMindMapController().getController().getMapModuleManager().addListener(this);
		mMapDialog = new JDialog(getController().getFrame().getJFrame(), false /* unmodal */);
		mMapDialog.setTitle(getResourceString("MapDialog_title"));
		mMapDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mMapDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				disposeDialog();
			}
		});
		Tools.addEscapeActionToDialog(mMapDialog, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				disposeDialog();
			}
		});
		mMapDialog.setSize(400, 400);

        map = new JCursorMapViewer(getMindMapController(), mMapDialog);

        // Listen to the map viewer for user operations so components will
        // recieve events and update
        map.addJMVListener(this);

        mMapDialog.setLayout(new BorderLayout());
//        mMapDialog.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = new JPanel();
        JPanel helpPanel = new JPanel();

        mperpLabelName=new JLabel("Meters/Pixels: ");
        mperpLabelValue=new JLabel(format("%s",map.getMeterPerPixel()));

        zoomLabel=new JLabel("Zoom: ");
        zoomValue=new JLabel(format("%s", map.getZoom()));

        mMapDialog.add(panel, BorderLayout.NORTH);
        mMapDialog.add(helpPanel, BorderLayout.SOUTH);
        JLabel helpLabel = new JLabel("Use left mouse button to move,\n "
                + "mouse wheel to zoom, left click to set cursor.");
        helpPanel.add(helpLabel);
        JButton button = new JButton("setDisplayToFitMapMarkers");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setDisplayToFitMapMarkers();
            }
        });
        JComboBox tileSourceSelector = new JComboBox(new TileSource[] { new OsmTileSource.Mapnik(),
                new OsmTileSource.TilesAtHome(), new OsmTileSource.CycleMap(), new BingAerialTileSource() });
        tileSourceSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileSource((TileSource) e.getItem());
            }
        });
        JComboBox tileLoaderSelector;
        try {
            tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmFileCacheTileLoader(map),
                    new OsmTileLoader(map) });
        } catch (IOException e) {
            tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmTileLoader(map) });
        }
        tileLoaderSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileLoader((TileLoader) e.getItem());
            }
        });
        map.setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
        panel.add(tileSourceSelector);
        panel.add(tileLoaderSelector);
        final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
        showMapMarker.setSelected(map.getMapMarkersVisible());
        showMapMarker.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setMapMarkerVisible(showMapMarker.isSelected());
            }
        });
        panel.add(showMapMarker);
        final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
        showTileGrid.setSelected(map.isTileGridVisible());
        showTileGrid.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setTileGridVisible(showTileGrid.isSelected());
            }
        });
        panel.add(showTileGrid);
        final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
        showZoomControls.setSelected(map.getZoomContolsVisible());
        showZoomControls.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setZoomContolsVisible(showZoomControls.isSelected());
            }
        });
        panel.add(showZoomControls);
        panel.add(button);

        panel.add(zoomLabel);
        panel.add(zoomValue);
        panel.add(mperpLabelName);
        panel.add(mperpLabelValue);

        mMapDialog.add(map, BorderLayout.CENTER);

        // add known markers to the map.
        HashSet mapNodePositionHolders = ((Registration) getPluginBaseClass()).getMapNodePositionHolders();
        for (Iterator it = mapNodePositionHolders.iterator(); it.hasNext();) {
			MapNodePositionHolder nodePositionHolder = (MapNodePositionHolder) it.next();
			addMapMarker(nodePositionHolder);
		}
        ((Registration) getPluginBaseClass()).registerMapNodePositionListener(this);
        
        map.setCursorPosition(new Coordinate(49.8, 8.8));
        map.setUseCursor(true);
		// restore preferences:
		//Retrieve window size and column positions.
		MapWindowConfigurationStorage storage = (MapWindowConfigurationStorage) getMindMapController()
				.decorateDialog(mMapDialog, WINDOW_PREFERENCE_STORAGE_PROPERTY);
		if (storage != null) {
			map.setDisplayPositionByLatLon(storage.getLatitude(), storage.getLongitude(), storage.getZoom());
			map.setCursorPosition(new Coordinate(storage.getCursorLatitude(), storage.getCursorLongitude()));
		}
		mMapDialog.setVisible(true);

    }

	public void addMapMarker(MapNodePositionHolder nodePositionHolder) {
		Coordinate position = nodePositionHolder.getPosition();
		logger.info("Adding map position for " + nodePositionHolder.getNode() + " at " + position);
		MapMarkerLocation marker = new MapMarkerLocation(nodePositionHolder);
		marker.setSize(marker.getPreferredSize());
		map.addMapMarker(marker);
		mMarkerMap.put(nodePositionHolder, marker);
	}

	/**
	 * Overwritten, as this dialog is not modal, but after the plugin has terminated,
	 * the dialog is still present and needs the controller to store its values.
	 * */
	public MindMapController getMindMapController() {
		return mMyMindMapController;
	}


    /**
	 * 
	 */
	protected void disposeDialog() {
        Registration registration = (Registration) getPluginBaseClass();
		if (registration != null) {
			// on close, it is null. Why?
			registration.deregisterMapNodePositionListener(this);
		}
		// store window positions:
		MapWindowConfigurationStorage storage = new MapWindowConfigurationStorage();
		// Set coordinates
		storage.setZoom(map.getZoom());
		Coordinate position = map.getPosition();
		storage.setLongitude((float) position.getLon());
		storage.setLatitude((float) position.getLat());
		Coordinate cursorPosition = map.getCursorPosition();
		storage.setCursorLongitude((float) cursorPosition.getLon());
		storage.setCursorLatitude((float) cursorPosition.getLat());
		getMindMapController().storeDialogPositions(mMapDialog, storage, WINDOW_PREFERENCE_STORAGE_PROPERTY);

        getMindMapController().getController().getMapModuleManager().removeListener(this);
		mMapDialog.setVisible(false);
		mMapDialog.dispose();
	}


    private void updateZoomParameters() {
        if (mperpLabelValue!=null)
            mperpLabelValue.setText(format("%s",map.getMeterPerPixel()));
        if (zoomValue!=null)
            zoomValue.setText(format("%s", map.getZoom()));
    }

    /**
	 * @param pString
	 * @param pMeterPerPixel
	 * @return
	 */
	private String format(String pString, double pObject) {
		return ""+pObject;
	}

	public void processCommand(JMVCommandEvent command) {
        if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
                command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
            updateZoomParameters();
        }
    }

	/* (non-Javadoc)
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#isMapModuleChangeAllowed(freemind.view.MapModule, freemind.modes.Mode, freemind.view.MapModule, freemind.modes.Mode)
	 */
	public boolean isMapModuleChangeAllowed(MapModule pOldMapModule,
			Mode pOldMode, MapModule pNewMapModule, Mode pNewMode) {
		return true;
	}

	/* (non-Javadoc)
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#beforeMapModuleChange(freemind.view.MapModule, freemind.modes.Mode, freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void beforeMapModuleChange(MapModule pOldMapModule, Mode pOldMode,
			MapModule pNewMapModule, Mode pNewMode) {
	}

	/* (non-Javadoc)
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#afterMapClose(freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
		disposeDialog();
	}

	/* (non-Javadoc)
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#afterMapModuleChange(freemind.view.MapModule, freemind.modes.Mode, freemind.view.MapModule, freemind.modes.Mode)
	 */
	public void afterMapModuleChange(MapModule pOldMapModule, Mode pOldMode,
			MapModule pNewMapModule, Mode pNewMode) {
		disposeDialog();
	}

	/* (non-Javadoc)
	 * @see freemind.controller.MapModuleManager.MapModuleChangeObserver#numberOfOpenMapInformation(int, int)
	 */
	public void numberOfOpenMapInformation(int pNumber, int pIndex) {
	}

	/* (non-Javadoc)
	 * @see plugins.map.MapNodePositionHolder.MapNodePositionListener#registerMapNode(plugins.map.MapNodePositionHolder)
	 */
	public void registerMapNode(MapNodePositionHolder pMapNodePositionHolder) {
		addMapMarker(pMapNodePositionHolder);
	}

	/* (non-Javadoc)
	 * @see plugins.map.MapNodePositionHolder.MapNodePositionListener#deregisterMapNode(plugins.map.MapNodePositionHolder)
	 */
	public void deregisterMapNode(MapNodePositionHolder pMapNodePositionHolder) {
		MapMarkerLocation marker = (MapMarkerLocation) mMarkerMap.remove(pMapNodePositionHolder);
		if(marker != null) {
			map.removeMapMarker(marker);
		}
		
	}	
}
