/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: Controller.java,v 1.40.14.21.2.64 2010/02/22 21:18:53 christianfoltin Exp $*/

package freemind.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import freemind.common.BooleanProperty;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.controller.actions.generated.instance.MindmapLastStateStorage;
import freemind.controller.filter.FilterController;
import freemind.controller.printpreview.PreviewDialog;
import freemind.main.FreeMind;
import freemind.main.FreeMindCommon;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.ModesCreator;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.browsemode.BrowseMode;
import freemind.modes.mindmapmode.attributeactors.AttributeManagerDialog;
import freemind.preferences.FreemindPropertyListener;
import freemind.preferences.layout.OptionPanel;
import freemind.preferences.layout.OptionPanel.OptionPanelFeedback;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;

/**
 * Provides the methods to edit/change a Node. Forwards all messages to
 * MapModel(editing) or MapView(navigation).
 */
public class Controller implements MapModuleChangeObserver {

	/**
	 * 
	 */
	private static final String PAGE_FORMAT_PROPERTY = "page_format";
	private HashSet mMapTitleChangeListenerSet = new HashSet();
	private HashSet mZoomListenerSet = new HashSet();
	private HashSet mMapTitleContributorSet = new HashSet();
	/**
	 * Converts from a local link to the real file URL of the documentation map.
	 * (Used to change this behaviour under MacOSX).
	 */
	private static Logger logger;
	/** Used for MAC!!! */
	public static LocalLinkConverter localDocumentationLinkConverter;
	private static JColorChooser colorChooser = new JColorChooser();
	private LastOpenedList lastOpened;// A list of the pathnames of all the maps
										// that were opened in the last time
	private MapModuleManager mapModuleManager;// new MapModuleManager();
	/** The current mode */
	private Mode mMode;
	private FreeMindMain frame;
	private MainToolBar toolbar;
	private JToolBar filterToolbar;
	private JPanel northToolbarPanel;
	private NodeMouseMotionListener nodeMouseMotionListener;
	private NodeMotionListener nodeMotionListener;
	private NodeKeyListener nodeKeyListener;
	private NodeDragListener nodeDragListener;
	private NodeDropListener nodeDropListener;
	private MapMouseMotionListener mapMouseMotionListener;
	private MapMouseWheelListener mapMouseWheelListener;
	private ModesCreator mModescreator = new ModesCreator(this);
	private PageFormat pageFormat = null;
	private PrinterJob printerJob = null;
	private Icon bswatch = new BackgroundSwatch();// needed for BackgroundAction
	private boolean antialiasEdges = false;
	private boolean antialiasAll = false;
	private Map fontMap = new HashMap();

	private FilterController mFilterController;

	boolean isPrintingAllowed = true;
	boolean menubarVisible = true;
	boolean toolbarVisible = true;
	boolean leftToolbarVisible = true;

	public CloseAction close;
	public Action print;
	public Action printDirect;
	public Action printPreview;
	public Action page;
	public Action quit;

	public Action showAllAttributes = new ShowAllAttributesAction();
	public Action showSelectedAttributes = new ShowSelectedAttributesAction();
	public Action hideAllAttributes = new HideAllAttributesAction();

	public OptionAntialiasAction optionAntialiasAction;
	public Action optionHTMLExportFoldingAction;
	public Action optionSelectionMechanismAction;

	public Action about;
	public Action faq;
	public Action keyDocumentation;
	public Action webDocu;
	public Action documentation;
	public Action license;
	public Action showFilterToolbarAction;
	public Action showAttributeManagerAction;
	public Action navigationPreviousMap;
	public Action navigationNextMap;
	public Action navigationMoveMapLeftAction;
	public Action navigationMoveMapRightAction;

	public Action moveToRoot;
	public Action toggleMenubar;
	public Action toggleToolbar;
	public Action toggleLeftToolbar;

	public Action zoomIn;
	public Action zoomOut;

	public Action showSelectionAsRectangle;
	public PropertyAction propertyAction;
	public OpenURLAction freemindUrl;

	private static final float[] zoomValues = { 25 / 100f, 50 / 100f,
			75 / 100f, 100 / 100f, 150 / 100f, 200 / 100f, 300 / 100f,
			400 / 100f };

	private static Vector propertyChangeListeners = new Vector();

	private AttributeManagerDialog attributeDialog = null;
	private Vector mTabbedPaneMapModules;
	private JTabbedPane mTabbedPane;
	private boolean mTabbedPaneSelectionUpdate = true;

	//
	// Constructors
	//
	public Controller(FreeMindMain frame) {
		this.frame = frame;
		if (logger == null) {
			logger = frame.getLogger(this.getClass().getName());
		}
	}

	public void init() {
		initialization();

		nodeMouseMotionListener = new NodeMouseMotionListener(this);
		nodeMotionListener = new NodeMotionListener(this);
		nodeKeyListener = new NodeKeyListener(this);
		nodeDragListener = new NodeDragListener(this);
		nodeDropListener = new NodeDropListener(this);

		mapMouseMotionListener = new MapMouseMotionListener(this);
		mapMouseWheelListener = new MapMouseWheelListener(this);

		close = new CloseAction(this);

		print = new PrintAction(this, true);
		printDirect = new PrintAction(this, false);
		printPreview = new PrintPreviewAction(this);
		page = new PageAction(this);
		quit = new QuitAction(this);
		about = new AboutAction(this);
		freemindUrl = new OpenURLAction(this, getResourceString("FreeMind"),
				getProperty("webFreeMindLocation"));
		faq = new OpenURLAction(this, getResourceString("FAQ"),
				getProperty("webFAQLocation"));
		keyDocumentation = new KeyDocumentationAction(this);
		webDocu = new OpenURLAction(this, getResourceString("webDocu"),
				getProperty("webDocuLocation"));
		documentation = new DocumentationAction(this);
		license = new LicenseAction(this);
		navigationPreviousMap = new NavigationPreviousMapAction(this);
		navigationNextMap = new NavigationNextMapAction(this);
		navigationMoveMapLeftAction = new NavigationMoveMapLeftAction(this);
		navigationMoveMapRightAction = new NavigationMoveMapRightAction(this);
		showFilterToolbarAction = new ShowFilterToolbarAction(this);
		showAttributeManagerAction = new ShowAttributeDialogAction(this);
		toggleMenubar = new ToggleMenubarAction(this);
		toggleToolbar = new ToggleToolbarAction(this);
		toggleLeftToolbar = new ToggleLeftToolbarAction(this);
		optionAntialiasAction = new OptionAntialiasAction(this);
		optionHTMLExportFoldingAction = new OptionHTMLExportFoldingAction(this);
		optionSelectionMechanismAction = new OptionSelectionMechanismAction(
				this);

		zoomIn = new ZoomInAction(this);
		zoomOut = new ZoomOutAction(this);
		propertyAction = new PropertyAction(this);

		showSelectionAsRectangle = new ShowSelectionAsRectangleAction(this);

		moveToRoot = new MoveToRootAction(this);

		// Create the ToolBar
		northToolbarPanel = new JPanel(new BorderLayout());
		toolbar = new MainToolBar(this);
		mFilterController = new FilterController(this);
		filterToolbar = mFilterController.getFilterToolbar();
		getFrame().getContentPane().add(northToolbarPanel, BorderLayout.NORTH);
		northToolbarPanel.add(toolbar, BorderLayout.NORTH);
		northToolbarPanel.add(filterToolbar, BorderLayout.SOUTH);

		setAllActions(false);

	}

	/**
	 * Does basic initializations of this class. Normally, init is called, but
	 * if you don't need the actions, call this method instead.
	 */
	public void initialization() {
		/**
		 * Arranges the keyboard focus especially after opening FreeMind.
		 * */
		KeyboardFocusManager focusManager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		focusManager.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				if ("focusOwner".equals(prop)) {
					Component comp = (Component) e.getNewValue();
					logger.fine("Focus change for " + comp);
					if (comp instanceof FreeMindMain) {
						obtainFocusForSelected();
					}
				}
			}
		});

		localDocumentationLinkConverter = new DefaultLocalLinkConverter();

		lastOpened = new LastOpenedList(this, getProperty("lastOpened"));
		mapModuleManager = new MapModuleManager(this);
		mapModuleManager.addListener(this);
		if (!Tools.isAvailableFontFamily(getProperty("defaultfont"))) {
			logger.warning("Warning: the font you have set as standard - "
					+ getProperty("defaultfont") + " - is not available.");
			frame.setProperty("defaultfont", "SansSerif");
		}
	}

	//
	// get/set methods
	//
	public static final String JAVA_VERSION = System
			.getProperty("java.version");

	public String getProperty(String property) {
		return frame.getProperty(property);
	}

	public int getIntProperty(String property, int defaultValue) {
		return frame.getIntProperty(property, defaultValue);
	}

	public void setProperty(String property, String value) {
		String oldValue = getProperty(property);
		getFrame().setProperty(property, value);
		firePropertyChanged(property, value, oldValue);
	}

	private void firePropertyChanged(String property, String value,
			String oldValue) {
		if (oldValue == null || !oldValue.equals(value)) {
			for (Iterator i = Controller.getPropertyChangeListeners()
					.iterator(); i.hasNext();) {
				FreemindPropertyListener listener = (FreemindPropertyListener) i
						.next();
				listener.propertyChanged(property, value, oldValue);
			}
		}
	}

	public FreeMindMain getFrame() {
		return frame;
	}

	public JFrame getJFrame() {
		FreeMindMain f = getFrame();
		if (f instanceof JFrame)
			return (JFrame) f;
		return null;
	}

	public URL getResource(String resource) {
		return getFrame().getResource(resource);
	}

	public String getResourceString(String resource) {
		return frame.getResourceString(resource);
	}

	/**
	 * @return the current modeController, or null, if FreeMind is just starting
	 *         and there is no modeController present.
	 */
	public ModeController getModeController() {
		if (getMapModule() != null) {
			return getMapModule().getModeController();
		}
		if (getMode() != null) {
			// no map present: we take the default:
			return getMode().getDefaultModeController();
		}
		return null;
	}

	/** Returns the current model */
	public MindMap getModel() {
		if (getMapModule() != null) {
			return getMapModule().getModel();
		}
		return null;
	}

	public MapView getView() {
		if (getMapModule() != null) {
			return getMapModule().getView();
		} else {
			// System.err.println("[Freemind-Developer-Internal-Warning (do not write a bug report, please)]: Tried to get view without being able to get map module.");
			return null;
		}
	}

	Set getModes() {
		return mModescreator.getAllModes();
	}

	public Mode getMode() {
		return mMode;
	}

	public String[] getZooms() {
		String[] zooms = new String[zoomValues.length];
		for (int i = 0; i < zoomValues.length; i++) {
			float val = zoomValues[i];
			zooms[i] = (int) (val * 100f) + "%";
		}
		return zooms;
	}

	public MapModuleManager getMapModuleManager() {
		return mapModuleManager;
	}

	public LastOpenedList getLastOpenedList() {
		return lastOpened;
	}

	//

	public MapModule getMapModule() {
		return getMapModuleManager().getMapModule();
	}

	private JToolBar getToolBar() {
		return toolbar;
	}

	//

	public Font getFontThroughMap(Font font) {
		if (!fontMap.containsKey(font.toString())) {
			fontMap.put(font.toString(), font);
		}
		return (Font) fontMap.get(font.toString());
	}

	//

	public void setAntialiasEdges(boolean antialiasEdges) {
		this.antialiasEdges = antialiasEdges;
	}

	public void setAntialiasAll(boolean antialiasAll) {
		this.antialiasAll = antialiasAll;
	}

	private boolean getAntialiasEdges() {
		return antialiasEdges;
	}

	private boolean getAntialiasAll() {
		return antialiasAll;
	}

	public Font getDefaultFont() {
		// Maybe implement handling for cases when the font is not
		// available on this system.

		int fontSize = getDefaultFontSize();
		int fontStyle = getDefaultFontStyle();
		String fontFamily = getDefaultFontFamilyName();

		return getFontThroughMap(new Font(fontFamily, fontStyle, fontSize));
	}

	/**
     */
	public String getDefaultFontFamilyName() {
		String fontFamily = getProperty("defaultfont");
		return fontFamily;
	}

	/**
     */
	public int getDefaultFontStyle() {
		int fontStyle = frame.getIntProperty("defaultfontstyle", 0);
		return fontStyle;
	}

	/**
     */
	public int getDefaultFontSize() {
		int fontSize = frame.getIntProperty("defaultfontsize", 12);
		return fontSize;
	}

	/** Static JColorChooser to have the recent colors feature. */
	static public JColorChooser getCommonJColorChooser() {
		return colorChooser;
	}

	public static Color showCommonJColorChooserDialog(Component component,
			String title, Color initialColor) throws HeadlessException {

		final JColorChooser pane = getCommonJColorChooser();
		pane.setColor(initialColor);

		ColorTracker ok = new ColorTracker(pane);
		JDialog dialog = JColorChooser.createDialog(component, title, true,
				pane, ok, null);
		dialog.addWindowListener(new Closer());
		dialog.addComponentListener(new DisposeOnClose());

		dialog.show(); // blocks until user brings dialog down...

		return ok.getColor();
	}

	private static class ColorTracker implements ActionListener, Serializable {
		JColorChooser chooser;
		Color color;

		public ColorTracker(JColorChooser c) {
			chooser = c;
		}

		public void actionPerformed(ActionEvent e) {
			color = chooser.getColor();
		}

		public Color getColor() {
			return color;
		}
	}

	static class Closer extends WindowAdapter implements Serializable {
		public void windowClosing(WindowEvent e) {
			Window w = e.getWindow();
			w.hide();
		}
	}

	static class DisposeOnClose extends ComponentAdapter implements
			Serializable {
		public void componentHidden(ComponentEvent e) {
			Window w = (Window) e.getComponent();
			w.dispose();
		}
	}

	public boolean isMapModuleChangeAllowed(MapModule oldMapModule,
			Mode oldMode, MapModule newMapModule, Mode newMode) {
		return true;
	}

	public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
	}

	public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
		ModeController oldModeController;
		this.mMode = newMode;
		if (oldMapModule != null) {
			// shut down screens of old view + frame
			oldModeController = oldMapModule.getModeController();
			oldModeController.setVisible(false);
			oldModeController.shutdownController();
		} else {
			if (oldMode != null) {
				oldModeController = oldMode.getDefaultModeController();
			} else {
				return;
			}
		}
		if (oldModeController.getModeToolBar() != null) {
			toolbar.remove(oldModeController.getModeToolBar());
			toolbar.activate(true);
			// northToolbarPanel.remove(oldModeController.getModeToolBar());
			// northToolbarPanel.add(toolbar, BorderLayout.NORTH);
		}
		/* other toolbars are to be removed too. */
		if (oldModeController.getLeftToolBar() != null) {
			getFrame().getContentPane().remove(
					oldModeController.getLeftToolBar());
		}
	}

	public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode,
			MapModule newMapModule, Mode newMode) {
		ModeController newModeController;
		if (newMapModule != null) {
			getFrame().setView(newMapModule.getView());
			setAllActions(true);
			if ((getView().getSelected() == null)) {
				// moveToRoot();
				getView().selectAsTheOnlyOneSelected(getView().getRoot());
			}
			lastOpened.mapOpened(newMapModule);
			changeZoomValueProperty(newMapModule.getView().getZoom());
			// ((MainToolBar) getToolbar()).setZoomComboBox(zoomValue);
			// old
			// obtainFocusForSelected();
			newModeController = newMapModule.getModeController();
			newModeController.startupController();
			newModeController.setVisible(true);
			// old
			// obtainFocusForSelected();
		} else {
			newModeController = newMode.getDefaultModeController();
			getFrame().setView(null);
			setAllActions(false);
		}
		setTitle();
		JToolBar newToolBar = newModeController.getModeToolBar();
		if (newToolBar != null) {
			toolbar.activate(false);
			toolbar.add(newToolBar, 0);
			// northToolbarPanel.remove(toolbar);
			// northToolbarPanel.add(newToolBar, BorderLayout.NORTH);
			newToolBar.repaint();
		}
		/* new left toolbar. */
		Component newLeftToolBar = newModeController.getLeftToolBar();
		if (newLeftToolBar != null) {
			getFrame().getContentPane().add(newLeftToolBar, BorderLayout.WEST);
			if (leftToolbarVisible) {
				newLeftToolBar.setVisible(true);
				newLeftToolBar.repaint();
			} else {
				newLeftToolBar.setVisible(false);
			}
		}
		toolbar.validate();
		toolbar.repaint();
		MenuBar menuBar = getFrame().getFreeMindMenuBar();
		menuBar.updateMenus(newModeController);
		menuBar.revalidate();
		menuBar.repaint();
		// new
		obtainFocusForSelected();
	}

	protected void changeZoomValueProperty(final float zoomValue) {
		for (Iterator it = mZoomListenerSet.iterator(); it.hasNext();) {
			ZoomListener listener = (ZoomListener) it.next();
			listener.setZoom(zoomValue);
		}
	}

	public void numberOfOpenMapInformation(int number, int pIndex) {
		navigationPreviousMap.setEnabled(number > 0);
		navigationNextMap.setEnabled(number > 0);
		logger.info("number " + number + ", pIndex " + pIndex);
		navigationMoveMapLeftAction.setEnabled(number > 1 && pIndex > 0);
		navigationMoveMapRightAction.setEnabled(number > 1
				&& pIndex < number - 1);
	}

	/**
	 * Creates a new mode (controller), activates the toolbars, title and
	 * deactivates all actions. Does nothing, if the mode is identical to the
	 * current mode.
	 * 
	 * @return false if the change was not successful.
	 */
	public boolean createNewMode(String mode) {
		if (getMode() != null && mode.equals(getMode().toString())) {
			return true;
		}

		// Check if the mode is available and create ModeController.
		Mode newMode = mModescreator.getMode(mode);
		if (newMode == null) {
			errorMessage(getResourceString("mode_na") + ": " + mode);
			return false;
		}

		// change the map module to get changed toolbars etc.:
		getMapModuleManager().setMapModule(null, newMode);

		setTitle();
		getMode().activate();

		Object[] messageArguments = { getMode().toLocalizedString() };
		MessageFormat formatter = new MessageFormat(
				getResourceString("mode_status"));
		getFrame().out(formatter.format(messageArguments));

		return true;
	}

	public void setMenubarVisible(boolean visible) {
		menubarVisible = visible;
		getFrame().getFreeMindMenuBar().setVisible(menubarVisible);
	}

	public void setToolbarVisible(boolean visible) {
		toolbarVisible = visible;
		toolbar.setVisible(toolbarVisible);
	}

	/**
	 * @return Returns the main toolbar.
	 */
	public JToolBar getToolbar() {
		return toolbar;
	}

	public void setLeftToolbarVisible(boolean visible) {
		leftToolbarVisible = visible;
		if (getMode() == null) {
			return;
		}
		final Component leftToolBar = getModeController().getLeftToolBar();
		if (leftToolBar != null) {
			leftToolBar.setVisible(leftToolbarVisible);
			((JComponent) leftToolBar.getParent()).revalidate();
		}
	}

	public NodeKeyListener getNodeKeyListener() {
		return nodeKeyListener;
	}

	public NodeMouseMotionListener getNodeMouseMotionListener() {
		return nodeMouseMotionListener;
	}

	public NodeMotionListener getNodeMotionListener() {
		return nodeMotionListener;
	}

	public MapMouseMotionListener getMapMouseMotionListener() {
		return mapMouseMotionListener;
	}

	public MapMouseWheelListener getMapMouseWheelListener() {
		return mapMouseWheelListener;
	}

	public NodeDragListener getNodeDragListener() {
		return nodeDragListener;
	}

	public NodeDropListener getNodeDropListener() {
		return nodeDropListener;
	}

	public void setFrame(FreeMindMain frame) {
		this.frame = frame;
	}

	/**
	 * I don't understand how this works now (it's called twice etc.) but it
	 * _works_ now. So let it alone or fix it to be understandable, if you have
	 * the time ;-)
	 */
	void moveToRoot() {
		if (getMapModule() != null) {
			getView().moveToRoot();
		}
	}

	/**
	 * Closes the actual map.
	 * 
	 * @param force
	 *            true= without save.
	 */
	public void close(boolean force) {
		getMapModuleManager().close(force, null);
	}

	// (PN) %%%
	// public void select( NodeView node) {
	// getView().select(node,false);
	// getView().setSiblingMaxLevel(node.getModel().getNodeLevel()); // this
	// level is default
	// }
	//
	// void selectBranch( NodeView node, boolean extend ) {
	// getView().selectBranch(node,extend);
	// }
	//
	// boolean isSelected( NodeView node ) {
	// return getView().isSelected(node);
	// }
	//
	// void centerNode() {
	// getView().centerNode(getView().getSelected());
	// }
	//
	// private MindMapNode getSelected() {
	// return getView().getSelected().getModel();
	// }

	public void informationMessage(Object message) {
		JOptionPane
				.showMessageDialog(getFrame().getContentPane(),
						message.toString(), "FreeMind",
						JOptionPane.INFORMATION_MESSAGE);
	}

	public void informationMessage(Object message, JComponent component) {
		JOptionPane.showMessageDialog(component, message.toString(),
				"FreeMind", JOptionPane.INFORMATION_MESSAGE);
	}

	public void errorMessage(Object message) {
		String myMessage = "";

		if (message != null) {
			myMessage = message.toString();
		} else {
			myMessage = getResourceString("undefined_error");
			if (myMessage == null) {
				myMessage = "Undefined error";
			}
		}
		JOptionPane.showMessageDialog(getFrame().getContentPane(), myMessage,
				"FreeMind", JOptionPane.ERROR_MESSAGE);

	}

	public void errorMessage(Object message, JComponent component) {
		JOptionPane.showMessageDialog(component, message.toString(),
				"FreeMind", JOptionPane.ERROR_MESSAGE);
	}

	public void obtainFocusForSelected() {
		// logger.finest("obtainFocusForSelected");
		if (getView() != null) { // is null if the last map was closed.
			logger.fine("Requesting Focus for " + getView() + " in model "
					+ getView().getModel());
			getView().requestFocusInWindow();
		} else {
			// fc, 6.1.2004: bug fix, that open and quit are not working if no
			// map is present.
			// to avoid this, the menu bar gets the focus, and everything seems
			// to be all right!!
			// but I cannot avoid thinking of this change to be a bad hack ....
			logger.info("No view present. No focus!");
			getFrame().getFreeMindMenuBar().requestFocus();
		}
	}

	//
	// Map Navigation
	//

	//
	// other
	//

	public void setZoom(float zoom) {
		getView().setZoom(zoom);
		changeZoomValueProperty(zoom);
		// ((MainToolBar) toolbar).setZoomComboBox(zoom);
		// show text in status bar:
		Object[] messageArguments = { String.valueOf(zoom * 100f) };
		String stringResult = Resources.getInstance().format(
				"user_defined_zoom_status_bar", messageArguments);
		getFrame().out(stringResult);
	}

	// ////////////
	// Private methods. Internal implementation
	// //////////

	//
	// Node editing
	//
	// (PN)
	// private void getFocus() {
	// getView().getSelected().requestFocus();
	// }

	//
	// Multiple Views management
	//

	/**
	 * Set the Frame title with mode and file if exist
	 */
	public void setTitle() {
		Object[] messageArguments = { getMode().toLocalizedString() };
		MessageFormat formatter = new MessageFormat(
				getResourceString("mode_title"));
		String title = formatter.format(messageArguments);
		String rawTitle = "";
		MindMap model = null;
		MapModule mapModule = getMapModule();
		if (mapModule != null) {
			model = mapModule.getModel();
			rawTitle = mapModule.toString();
			title = rawTitle
					+ (model.isSaved() ? "" : "*")
					+ " - "
					+ title
					+ (model.isReadOnly() ? " ("
							+ getResourceString("read_only") + ")" : "");
			File file = model.getFile();
			if (file != null) {
				title += " " + file.getAbsolutePath();
			}
			for (Iterator iterator = mMapTitleContributorSet.iterator(); iterator
					.hasNext();) {
				MapModuleManager.MapTitleContributor contributor = (MapModuleManager.MapTitleContributor) iterator
						.next();
				title = contributor.getMapTitle(title, mapModule, model);
			}

		}
		getFrame().setTitle(title);
		for (Iterator iterator = mMapTitleChangeListenerSet.iterator(); iterator
				.hasNext();) {
			MapModuleManager.MapTitleChangeListener listener = (MapModuleManager.MapTitleChangeListener) iterator
					.next();
			listener.setMapTitle(rawTitle, mapModule, model);
		}
	}

	public void registerMapTitleChangeListener(
			MapModuleManager.MapTitleChangeListener pMapTitleChangeListener) {
		mMapTitleChangeListenerSet.add(pMapTitleChangeListener);
	}

	public void deregisterMapTitleChangeListener(
			MapModuleManager.MapTitleChangeListener pMapTitleChangeListener) {
		mMapTitleChangeListenerSet.remove(pMapTitleChangeListener);
	}

	public void registerZoomListener(ZoomListener pZoomListener) {
		mZoomListenerSet.add(pZoomListener);
	}

	public void deregisterZoomListener(ZoomListener pZoomListener) {
		mZoomListenerSet.remove(pZoomListener);
	}

	public void registerMapTitleContributor(
			MapModuleManager.MapTitleContributor pMapTitleContributor) {
		mMapTitleContributorSet.add(pMapTitleContributor);
	}

	public void deregisterMapTitleContributor(
			MapModuleManager.MapTitleContributor pMapTitleContributor) {
		mMapTitleContributorSet.remove(pMapTitleContributor);
	}

	//
	// Actions management
	//

	/**
	 * Manage the availabilty of all Actions dependend of whether there is a map
	 * or not
	 */
	public void setAllActions(boolean enabled) {
		print.setEnabled(enabled && isPrintingAllowed);
		printDirect.setEnabled(enabled && isPrintingAllowed);
		printPreview.setEnabled(enabled && isPrintingAllowed);
		page.setEnabled(enabled && isPrintingAllowed);
		close.setEnabled(enabled);
		moveToRoot.setEnabled(enabled);
		showAllAttributes.setEnabled(enabled);
		showSelectedAttributes.setEnabled(enabled);
		hideAllAttributes.setEnabled(enabled);
		showAttributeManagerAction.setEnabled(enabled);
		((MainToolBar) getToolBar()).setAllActions(enabled);
		showSelectionAsRectangle.setEnabled(enabled);
	}

	//
	// program/map control
	//

	private void quit() {
		String currentMapRestorable = (getModel() != null) ? getModel()
				.getRestorable() : null;
		// collect all maps:
		Vector restorables = new Vector();
		// move to first map in the window.
		List mapModuleVector = getMapModuleManager().getMapModuleVector();
		if (mapModuleVector.size() > 0) {
			String displayName = ((MapModule) mapModuleVector.get(0))
					.getDisplayName();
			getMapModuleManager().changeToMapModule(displayName);
		}
		while (mapModuleVector.size() > 0) {
			if (getMapModule() != null) {
				StringBuffer restorableBuffer = new StringBuffer();
				boolean closingNotCancelled = getMapModuleManager().close(
						false, restorableBuffer);
				if (!closingNotCancelled) {
					return;
				}
				if (restorableBuffer.length() != 0) {
					String restorableString = restorableBuffer.toString();
					logger.info("Closed the map " + restorableString);
					restorables.add(restorableString);
				}
			} else {
				// map module without view open.
				// FIXME: This seems to be a bad hack. correct me!
				getMapModuleManager().nextMapModule();
			}
		}
		// store last tab session:
		int index = 0;
		String lastStateMapXml = getProperty(FreeMindCommon.MINDMAP_LAST_STATE_MAP_STORAGE);
		LastStateStorageManagement management = new LastStateStorageManagement(
				lastStateMapXml);
		management.setLastFocussedTab(-1);
		management.clearTabIndices();
		for (Iterator it = restorables.iterator(); it.hasNext();) {
			String restorable = (String) it.next();
			MindmapLastStateStorage storage = management.getStorage(restorable);
			if (storage != null) {
				storage.setTabIndex(index);
			}
			if (Tools.safeEquals(restorable, currentMapRestorable)) {
				management.setLastFocussedTab(index);
			}
			index++;
		}
		setProperty(FreeMindCommon.MINDMAP_LAST_STATE_MAP_STORAGE,
				management.getXml());

		String lastOpenedString = lastOpened.save();
		setProperty("lastOpened", lastOpenedString);
		getFrame().setProperty(FreeMindCommon.ON_START_IF_NOT_SPECIFIED,
				currentMapRestorable != null ? currentMapRestorable : "");
		// getFrame().setProperty("menubarVisible",menubarVisible ? "true" :
		// "false");
		// ^ Not allowed in application because of problems with not working key
		// shortcuts
		setProperty("toolbarVisible", toolbarVisible ? "true" : "false");
		setProperty("leftToolbarVisible", leftToolbarVisible ? "true" : "false");
		setProperty("antialiasEdges", antialiasEdges ? "true" : "false");
		setProperty("antialiasAll", antialiasAll ? "true" : "false");
		if (!getFrame().isApplet()) {
			final int winState = getFrame().getWinState();
			if (JFrame.MAXIMIZED_BOTH != (winState & JFrame.MAXIMIZED_BOTH)) {
				setProperty("appwindow_x", String.valueOf(getFrame().getWinX()));
				setProperty("appwindow_y", String.valueOf(getFrame().getWinY()));
				setProperty("appwindow_width",
						String.valueOf(getFrame().getWinWidth()));
				setProperty("appwindow_height",
						String.valueOf(getFrame().getWinHeight()));
			}
			setProperty("appwindow_state", String.valueOf(winState));
		}
		// Stop edit server!
		getFrame().saveProperties(true);
		// save to properties
		System.exit(0);
	}

	private boolean acquirePrinterJobAndPageFormat() {
		if (printerJob == null) {
			try {
				printerJob = PrinterJob.getPrinterJob();
			} catch (SecurityException ex) {
				isPrintingAllowed = false;
				return false;
			}
		}
		if (pageFormat == null) {
			pageFormat = printerJob.defaultPage();
		}
		if (Tools.safeEquals(getProperty("page_orientation"), "landscape")) {
			pageFormat.setOrientation(PageFormat.LANDSCAPE);
		} else if (Tools
				.safeEquals(getProperty("page_orientation"), "portrait")) {
			pageFormat.setOrientation(PageFormat.PORTRAIT);
		} else if (Tools.safeEquals(getProperty("page_orientation"),
				"reverse_landscape")) {
			pageFormat.setOrientation(PageFormat.REVERSE_LANDSCAPE);
		}
		String pageFormatProperty = getProperty(PAGE_FORMAT_PROPERTY);
		if (!pageFormatProperty.isEmpty()) {
			logger.info("Page format (stored): " + pageFormatProperty);
			final Paper storedPaper = new Paper();
			Tools.setPageFormatFromString(storedPaper, pageFormatProperty);
			pageFormat.setPaper(storedPaper);
		}
		return true;
	}

	// ////////////
	// Inner Classes
	// //////////

	/**
	 * Manages the history of visited maps. Maybe explicitly closed maps should
	 * be removed from History too?
	 */

	//
	// program/map control
	//

	private class QuitAction extends AbstractAction {
		QuitAction(Controller controller) {
			super(controller.getResourceString("quit"));
		}

		public void actionPerformed(ActionEvent e) {
			quit();
		}
	}

	/** This closes only the current map */
	public static class CloseAction extends AbstractAction {
		private final Controller controller;

		CloseAction(Controller controller) {
			Tools.setLabelAndMnemonic(this,
					controller.getResourceString("close"));
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent e) {
			controller.close(false);
		}
	}

	private class PrintAction extends AbstractAction {
		Controller controller;
		boolean isDlg;

		PrintAction(Controller controller, boolean isDlg) {
			super(isDlg ? controller.getResourceString("print_dialog")
					: controller.getResourceString("print"), new ImageIcon(
					getResource("images/fileprint.png")));
			this.controller = controller;
			setEnabled(false);
			this.isDlg = isDlg;
		}

		public void actionPerformed(ActionEvent e) {
			if (!acquirePrinterJobAndPageFormat()) {
				return;
			}

			printerJob.setPrintable(getView(), pageFormat);

			if (!isDlg || printerJob.printDialog()) {
				try {
					frame.setWaitingCursor(true);
					printerJob.print();
					storePageFormat();
				} catch (Exception ex) {
					freemind.main.Resources.getInstance().logException(ex);
				} finally {
					frame.setWaitingCursor(false);
				}
			}
		}
	}

	private class PrintPreviewAction extends AbstractAction {
		Controller controller;

		PrintPreviewAction(Controller controller) {
			super(controller.getResourceString("print_preview"));
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent e) {
			if (!acquirePrinterJobAndPageFormat()) {
				return;
			}
			PreviewDialog previewDialog = new PreviewDialog(
					controller.getResourceString("print_preview_title"),
					getView());
			previewDialog.pack();
			previewDialog.setLocationRelativeTo(JOptionPane
					.getFrameForComponent(getView()));
			previewDialog.setVisible(true);
		}
	}

	private class PageAction extends AbstractAction {
		Controller controller;

		PageAction(Controller controller) {
			super(controller.getResourceString("page"));
			this.controller = controller;
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			if (!acquirePrinterJobAndPageFormat()) {
				return;
			}

			// Ask about custom printing settings
			final JDialog dialog = new JDialog((JFrame) getFrame(),
					getResourceString("printing_settings"), /* modal= */true);
			final JCheckBox fitToPage = new JCheckBox(
					getResourceString("fit_to_page"), Resources.getInstance()
							.getBoolProperty("fit_to_page"));
			final JLabel userZoomL = new JLabel(getResourceString("user_zoom"));
			final JTextField userZoom = new JTextField(
					getProperty("user_zoom"), 3);
			userZoom.setEditable(!fitToPage.isSelected());
			final JButton okButton = new JButton();
			Tools.setLabelAndMnemonic(okButton, getResourceString("ok"));
			final Tools.IntHolder eventSource = new Tools.IntHolder();
			JPanel panel = new JPanel();

			GridBagLayout gridbag = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();

			eventSource.setValue(0);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					eventSource.setValue(1);
					dialog.dispose();
				}
			});
			fitToPage.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					userZoom.setEditable(e.getStateChange() == ItemEvent.DESELECTED);
				}
			});

			// c.weightx = 0.5;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			gridbag.setConstraints(fitToPage, c);
			panel.add(fitToPage);
			c.gridy = 1;
			c.gridwidth = 1;
			gridbag.setConstraints(userZoomL, c);
			panel.add(userZoomL);
			c.gridx = 1;
			c.gridwidth = 1;
			gridbag.setConstraints(userZoom, c);
			panel.add(userZoom);
			c.gridy = 2;
			c.gridx = 0;
			c.gridwidth = 3;
			c.insets = new Insets(10, 0, 0, 0);
			gridbag.setConstraints(okButton, c);
			panel.add(okButton);
			panel.setLayout(gridbag);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setContentPane(panel);
			dialog.setLocationRelativeTo((JFrame) getFrame());
			dialog.getRootPane().setDefaultButton(okButton);
			dialog.pack(); // calculate the size
			dialog.setVisible(true);

			if (eventSource.getValue() == 1) {
				setProperty("user_zoom", userZoom.getText());
				setProperty("fit_to_page", fitToPage.isSelected() ? "true"
						: "false");
			} else
				return;

			// Ask user for page format (e.g., portrait/landscape)
			pageFormat = printerJob.pageDialog(pageFormat);
			storePageFormat();
		}
	}

	public interface LocalLinkConverter {
		/**
		 * @throws MalformedURLException
		 *             if the conversion didn't work
		 */
		URL convertLocalLink(String link) throws MalformedURLException;
	}

	private class DefaultLocalLinkConverter implements LocalLinkConverter {

		public URL convertLocalLink(String map) throws MalformedURLException {
			/* new handling for relative urls. fc, 29.10.2003. */
			String applicationPath = frame.getFreemindBaseDir();
			// remove "." and make url
			return Tools
					.fileToUrl(new File(applicationPath + map.substring(1)));
			/* end: new handling for relative urls. fc, 29.10.2003. */
		}
	}

	//
	// Help
	//

	private class DocumentationAction extends AbstractAction {
		Controller controller;

		DocumentationAction(Controller controller) {
			super(controller.getResourceString("documentation"));
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				String map = controller.getFrame().getResourceString(
						"browsemode_initial_map");
				// if the current language does not provide its own translation,
				// POSTFIX_TRANSLATE_ME is appended:
				map = Tools.removeTranslateComment(map);
				URL url = null;
				if (map != null && map.startsWith(".")) {
					url = localDocumentationLinkConverter.convertLocalLink(map);
				} else {
					url = Tools.fileToUrl(new File(map));
				}
				final URL endUrl = url;
				// invokeLater is necessary, as the mode changing removes
				// all
				// menus (inclusive this action!).
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							createNewMode(BrowseMode.MODENAME);
							controller.getModeController().load(endUrl);
						} catch (Exception e1) {
							freemind.main.Resources.getInstance().logException(
									e1);
						}
					}
				});
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				freemind.main.Resources.getInstance().logException(e1);
			}
		}
	}

	private class KeyDocumentationAction extends AbstractAction {
		Controller controller;

		KeyDocumentationAction(Controller controller) {
			super(controller.getResourceString("KeyDoc"));
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent e) {
			String urlText = controller.getFrame().getResourceString(
					"pdfKeyDocLocation");
			// if the current language does not provide its own translation,
			// POSTFIX_TRANSLATE_ME is appended:
			urlText = Tools.removeTranslateComment(urlText);
			try {
				URL url = null;
				if (urlText != null && urlText.startsWith(".")) {
					url = localDocumentationLinkConverter
							.convertLocalLink(urlText);
				} else {
					url = Tools.fileToUrl(new File(urlText));
				}
				logger.info("Opening key docs under " + url);
				controller.getFrame().openDocument(url);
			} catch (Exception e2) {
				freemind.main.Resources.getInstance().logException(e2);
				return;
			}
		}
	}

	private class AboutAction extends AbstractAction {
		Controller controller;

		AboutAction(Controller controller) {
			super(controller.getResourceString("about"));
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(getView(),
					controller.getResourceString("about_text")
							+ getFrame().getFreemindVersion(),
					controller.getResourceString("about"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private class LicenseAction extends AbstractAction {
		Controller controller;

		LicenseAction(Controller controller) {
			super(controller.getResourceString("license"));
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(getView(),
					controller.getResourceString("license_text"),
					controller.getResourceString("license"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	//
	// Map navigation
	//

	private class NavigationPreviousMapAction extends AbstractAction {
		NavigationPreviousMapAction(Controller controller) {
			super(controller.getResourceString("previous_map"), new ImageIcon(
					getResource("images/1leftarrow.png")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent event) {
			mapModuleManager.previousMapModule();
		}
	}

	private class ShowAttributeDialogAction extends AbstractAction {
		private Controller c;

		ShowAttributeDialogAction(Controller c) {
			super(c.getResourceString("attributes_dialog"), new ImageIcon(
					getResource("images/showAttributes.gif")));
			this.c = c;
		}

		private AttributeManagerDialog getAttributeDialog() {
			if (attributeDialog == null) {
				attributeDialog = new AttributeManagerDialog(c);
			}
			return attributeDialog;
		}

		public void actionPerformed(ActionEvent e) {
			if (getAttributeDialog().isVisible() == false
					&& getMapModule() != null) {
				getAttributeDialog().pack();
				getAttributeDialog().show();
			}
		}
	}

	private class ShowFilterToolbarAction extends AbstractAction {
		ShowFilterToolbarAction(Controller controller) {
			super(getResourceString("filter_toolbar"), new ImageIcon(
					getResource("images/filter.gif")));
		}

		public void actionPerformed(ActionEvent event) {
			if (!getFilterController().isVisible()) {
				getFilterController().showFilterToolbar(true);
			} else {
				getFilterController().showFilterToolbar(false);
			}
		}
	}

	private class NavigationNextMapAction extends AbstractAction {
		NavigationNextMapAction(Controller controller) {
			super(controller.getResourceString("next_map"), new ImageIcon(
					getResource("images/1rightarrow.png")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent event) {
			mapModuleManager.nextMapModule();
		}
	}

	private class NavigationMoveMapLeftAction extends AbstractAction {
		NavigationMoveMapLeftAction(Controller controller) {
			super(controller.getResourceString("move_map_left"), new ImageIcon(
					getResource("images/draw-arrow-back.png")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent event) {
			if (mTabbedPane != null) {
				int selectedIndex = mTabbedPane.getSelectedIndex();
				int previousIndex = (selectedIndex > 0) ? (selectedIndex - 1)
						: (mTabbedPane.getTabCount() - 1);
				moveTab(selectedIndex, previousIndex);
			}
		}
	}

	private class NavigationMoveMapRightAction extends AbstractAction {
		NavigationMoveMapRightAction(Controller controller) {
			super(controller.getResourceString("move_map_right"),
					new ImageIcon(getResource("images/draw-arrow-forward.png")));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent event) {
			if (mTabbedPane != null) {
				int selectedIndex = mTabbedPane.getSelectedIndex();
				int previousIndex = (selectedIndex >= mTabbedPane.getTabCount() - 1) ? 0
						: (selectedIndex + 1);
				moveTab(selectedIndex, previousIndex);
			}
		}
	}

	public void moveTab(int src, int dst) {
		// snippet taken from
		// http://www.exampledepot.com/egs/javax.swing/tabbed_TpMove.html
		// Get all the properties
		Component comp = mTabbedPane.getComponentAt(src);
		String label = mTabbedPane.getTitleAt(src);
		Icon icon = mTabbedPane.getIconAt(src);
		Icon iconDis = mTabbedPane.getDisabledIconAt(src);
		String tooltip = mTabbedPane.getToolTipTextAt(src);
		boolean enabled = mTabbedPane.isEnabledAt(src);
		int keycode = mTabbedPane.getMnemonicAt(src);
		int mnemonicLoc = mTabbedPane.getDisplayedMnemonicIndexAt(src);
		Color fg = mTabbedPane.getForegroundAt(src);
		Color bg = mTabbedPane.getBackgroundAt(src);

		mTabbedPaneSelectionUpdate = false;
		// Remove the tab
		mTabbedPane.remove(src);
		// Add a new tab
		mTabbedPane.insertTab(label, icon, comp, tooltip, dst);
		Tools.swapVectorPositions(mTabbedPaneMapModules, src, dst);
		getMapModuleManager().swapModules(src, dst);
		mTabbedPane.setSelectedIndex(dst);
		mTabbedPaneSelectionUpdate = true;

		// Restore all properties
		mTabbedPane.setDisabledIconAt(dst, iconDis);
		mTabbedPane.setEnabledAt(dst, enabled);
		mTabbedPane.setMnemonicAt(dst, keycode);
		mTabbedPane.setDisplayedMnemonicIndexAt(dst, mnemonicLoc);
		mTabbedPane.setForegroundAt(dst, fg);
		mTabbedPane.setBackgroundAt(dst, bg);
	}

	//
	// Node navigation
	//

	private class MoveToRootAction extends AbstractAction {
		MoveToRootAction(Controller controller) {
			super(controller.getResourceString("move_to_root"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent event) {
			moveToRoot();
		}
	}

	private class ToggleMenubarAction extends AbstractAction implements
			MenuItemSelectedListener {
		ToggleMenubarAction(Controller controller) {
			super(controller.getResourceString("toggle_menubar"));
			setEnabled(true);
		}

		public void actionPerformed(ActionEvent event) {
			menubarVisible = !menubarVisible;
			setMenubarVisible(menubarVisible);
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return menubarVisible;
		}
	}

	private class ToggleToolbarAction extends AbstractAction implements
			MenuItemSelectedListener {
		ToggleToolbarAction(Controller controller) {
			super(controller.getResourceString("toggle_toolbar"));
			setEnabled(true);
		}

		public void actionPerformed(ActionEvent event) {
			toolbarVisible = !toolbarVisible;
			setToolbarVisible(toolbarVisible);
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			logger.info("ToggleToolbar was asked for selectedness.");
			return toolbarVisible;
		}
	}

	private class ToggleLeftToolbarAction extends AbstractAction implements
			MenuItemSelectedListener {
		ToggleLeftToolbarAction(Controller controller) {
			super(controller.getResourceString("toggle_left_toolbar"));
			setEnabled(true);
		}

		public void actionPerformed(ActionEvent event) {
			leftToolbarVisible = !leftToolbarVisible;
			setLeftToolbarVisible(leftToolbarVisible);
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return leftToolbarVisible;
		}
	}

	protected class ZoomInAction extends AbstractAction {
		public ZoomInAction(Controller controller) {
			super(controller.getResourceString("zoom_in"));
		}

		public void actionPerformed(ActionEvent e) {
			// logger.info("ZoomInAction actionPerformed");
			float currentZoom = getView().getZoom();
			for (int i = 0; i < zoomValues.length; i++) {
				float val = zoomValues[i];
				if (val > currentZoom) {
					setZoom(val);
					return;
				}
			}
			setZoom(zoomValues[zoomValues.length - 1]);
		}
	}

	protected class ZoomOutAction extends AbstractAction {
		public ZoomOutAction(Controller controller) {
			super(controller.getResourceString("zoom_out"));
		}

		public void actionPerformed(ActionEvent e) {
			float currentZoom = getView().getZoom();
			float lastZoom = zoomValues[0];
			for (int i = 0; i < zoomValues.length; i++) {
				float val = zoomValues[i];
				if (val >= currentZoom) {
					setZoom(lastZoom);
					return;
				}
				lastZoom = val;
			}
			setZoom(lastZoom);
		}
	}

	protected class ShowSelectionAsRectangleAction extends AbstractAction
			implements MenuItemSelectedListener {
		public ShowSelectionAsRectangleAction(Controller controller) {
			super(controller.getResourceString("selection_as_rectangle"));
		}

		public void actionPerformed(ActionEvent e) {
			// logger.info("ShowSelectionAsRectangleAction action Performed");
			toggleSelectionAsRectangle();
		}

		public boolean isSelected(JMenuItem pCheckItem, Action pAction) {
			return isSelectionAsRectangle();
		}
	}

	private class ShowAllAttributesAction extends AbstractAction {
		public ShowAllAttributesAction() {
			super(Resources.getInstance().getResourceString(
					"attributes_show_all"));
		};

		public void actionPerformed(ActionEvent e) {
			final MindMap map = getMap();
			setAttributeViewType(map);
		}

		public void setAttributeViewType(final MindMap map) {
			final AttributeRegistry attributes = map.getRegistry()
					.getAttributes();
			if (attributes.getAttributeViewType() != AttributeTableLayoutModel.SHOW_ALL) {
				attributes
						.setAttributeViewType(AttributeTableLayoutModel.SHOW_ALL);
			}
		}
	}

	private class HideAllAttributesAction extends AbstractAction {
		public HideAllAttributesAction() {
			super(Resources.getInstance().getResourceString(
					"attributes_hide_all"));
		};

		public void actionPerformed(ActionEvent e) {
			final MindMap map = getMap();
			setAttributeViewType(map);
		}

		public void setAttributeViewType(final MindMap map) {
			final AttributeRegistry attributes = map.getRegistry()
					.getAttributes();
			if (attributes.getAttributeViewType() != AttributeTableLayoutModel.HIDE_ALL) {
				attributes
						.setAttributeViewType(AttributeTableLayoutModel.HIDE_ALL);
			}
		}
	}

	private class ShowSelectedAttributesAction extends AbstractAction {
		public ShowSelectedAttributesAction() {
			super(Resources.getInstance().getResourceString(
					"attributes_show_selected"));
		};

		public void actionPerformed(ActionEvent e) {
			MindMap map = getMap();
			setAttributeViewType(map);
		}

		void setAttributeViewType(MindMap map) {
			final AttributeRegistry attributes = map.getRegistry()
					.getAttributes();
			if (attributes.getAttributeViewType() != AttributeTableLayoutModel.SHOW_SELECTED) {
				attributes
						.setAttributeViewType(AttributeTableLayoutModel.SHOW_SELECTED);
			}
		}
	}

	//
	// Preferences
	//

	public static Collection getPropertyChangeListeners() {
		return Collections.unmodifiableCollection(propertyChangeListeners);
	}

	public void toggleSelectionAsRectangle() {
		if (isSelectionAsRectangle()) {
			setProperty(FreeMind.RESOURCE_DRAW_RECTANGLE_FOR_SELECTION,
					BooleanProperty.FALSE_VALUE);
		} else {
			setProperty(FreeMind.RESOURCE_DRAW_RECTANGLE_FOR_SELECTION,
					BooleanProperty.TRUE_VALUE);
		}
	}

	private boolean isSelectionAsRectangle() {
		return getProperty(FreeMind.RESOURCE_DRAW_RECTANGLE_FOR_SELECTION)
				.equalsIgnoreCase(BooleanProperty.TRUE_VALUE);
	}

	/**
     */
	public MindMap getMap() {
		return getMapModule().getModel();
	}

	public static void addPropertyChangeListener(
			FreemindPropertyListener listener) {
		Controller.propertyChangeListeners.add(listener);
	}

	/**
	 * @param listener
	 *            The new listener. All currently available properties are sent
	 *            to the listener after registration. Here, the oldValue
	 *            parameter is set to null.
	 */
	public static void addPropertyChangeListenerAndPropagate(
			FreemindPropertyListener listener) {
		Controller.addPropertyChangeListener(listener);
		Properties properties = Resources.getInstance().getProperties();
		for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			listener.propertyChanged(key, properties.getProperty(key), null);
		}
	}

	public static void removePropertyChangeListener(
			FreemindPropertyListener listener) {
		Controller.propertyChangeListeners.remove(listener);
	}

	/**
	 * @author foltin
	 * 
	 */
	public class PropertyAction extends AbstractAction {

		private final Controller controller;

		/**
		 *
		 */
		public PropertyAction(Controller controller) {
			super(controller.getResourceString("property_dialog"));
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent arg0) {
			JDialog dialog = new JDialog(getFrame().getJFrame(), true /* modal */);
			dialog.setResizable(true);
			dialog.setUndecorated(false);
			final OptionPanel options = new OptionPanel((FreeMind) getFrame(),
					dialog, new OptionPanelFeedback() {

						public void writeProperties(Properties props) {
							Vector sortedKeys = new Vector();
							sortedKeys.addAll(props.keySet());
							Collections.sort(sortedKeys);
							boolean propertiesChanged = false;
							for (Iterator i = sortedKeys.iterator(); i
									.hasNext();) {
								String key = (String) i.next();
								// save only changed keys:
								String newProperty = props.getProperty(key);
								propertiesChanged = propertiesChanged
										|| !newProperty.equals(controller
												.getProperty(key));
								controller.setProperty(key, newProperty);
							}

							if (propertiesChanged) {
								JOptionPane
										.showMessageDialog(
												null,
												getResourceString("option_changes_may_require_restart"));
								controller.getFrame().saveProperties(false);
							}
						}
					});
			options.buildPanel();
			options.setProperties();
			dialog.setTitle("Freemind Properties");
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent event) {
					options.closeWindow();
				}
			});
			Action action = new AbstractAction() {

				public void actionPerformed(ActionEvent arg0) {
					options.closeWindow();
				}
			};
			Tools.addEscapeActionToDialog(dialog, action);
			dialog.setVisible(true);

		}

	}

	private class BackgroundSwatch extends ColorSwatch {
		Color getColor() {
			return getView().getBackground();
		}
	}

	public class OptionAntialiasAction extends AbstractAction implements
			FreemindPropertyListener {
		OptionAntialiasAction(Controller controller) {
			Controller.addPropertyChangeListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			changeAntialias(command);
		}

		/**
	     */
		public void changeAntialias(String command) {
			if (command == null) {
				return;
			}
			if (command.equals("antialias_none")) {
				setAntialiasEdges(false);
				setAntialiasAll(false);
			}
			if (command.equals("antialias_edges")) {
				setAntialiasEdges(true);
				setAntialiasAll(false);
			}
			if (command.equals("antialias_all")) {
				setAntialiasEdges(true);
				setAntialiasAll(true);
			}
			if (getView() != null)
				getView().repaint();
		}

		public void propertyChanged(String propertyName, String newValue,
				String oldValue) {
			if (propertyName.equals(FreeMindCommon.RESOURCE_ANTIALIAS)) {
				changeAntialias(newValue);
			}
		}
	}

	private class OptionHTMLExportFoldingAction extends AbstractAction {
		OptionHTMLExportFoldingAction(Controller controller) {
		}

		public void actionPerformed(ActionEvent e) {
			setProperty("html_export_folding", e.getActionCommand());
		}
	}

	// switch auto properties for selection mechanism fc, 7.12.2003.
	private class OptionSelectionMechanismAction extends AbstractAction
			implements FreemindPropertyListener {
		Controller c;

		OptionSelectionMechanismAction(Controller controller) {
			c = controller;
			Controller.addPropertyChangeListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			changeSelection(command);
		}

		/**
         */
		private void changeSelection(String command) {
			setProperty("selection_method", command);
			// and update the selection method in the NodeMouseMotionListener
			c.getNodeMouseMotionListener().updateSelectionMethod();
			String statusBarString = c.getResourceString(command);
			if (statusBarString != null) // should not happen
				c.getFrame().out(statusBarString);
		}

		public void propertyChanged(String propertyName, String newValue,
				String oldValue) {
			if (propertyName.equals(FreeMind.RESOURCES_SELECTION_METHOD)) {
				changeSelection(newValue);
			}
		}
	}

	// open faq url from freeminds page:
	private class OpenURLAction extends AbstractAction {
		Controller c;
		private final String url;

		OpenURLAction(Controller controller, String description, String url) {
			super(description, new ImageIcon(
					controller.getResource("images/Link.png")));
			c = controller;
			this.url = url;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				c.getFrame().openDocument(new URL(url));
			} catch (MalformedURLException ex) {
				c.errorMessage(c.getResourceString("url_error") + "\n" + ex);
			} catch (Exception ex) {
				c.errorMessage(ex);
			}
		}
	}

	public FilterController getFilterController() {
		return mFilterController;
	}

	public PageFormat getPageFormat() {
		return pageFormat;
	}

	public void setAttributeViewType(MindMap map, String value) {
		if (value.equals(AttributeTableLayoutModel.SHOW_SELECTED)) {
			((ShowSelectedAttributesAction) showSelectedAttributes)
					.setAttributeViewType(map);
		} else if (value.equals(AttributeTableLayoutModel.HIDE_ALL)) {
			((HideAllAttributesAction) hideAllAttributes)
					.setAttributeViewType(map);
		} else if (value.equals(AttributeTableLayoutModel.SHOW_ALL)) {
			((ShowAllAttributesAction) showAllAttributes)
					.setAttributeViewType(map);
		}
	}

	public Object setEdgesRenderingHint(Graphics2D g) {
		Object renderingHint = g
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				(getAntialiasEdges()) ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);
		return renderingHint;
	}

	public void setTextRenderingHint(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				(getAntialiasAll()) ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
						: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				(getAntialiasAll()) ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public void addTabbedPane(JTabbedPane pTabbedPane) {
		mTabbedPane = pTabbedPane;
		mTabbedPaneMapModules = new Vector();
		mTabbedPane.addChangeListener(new ChangeListener() {

			public synchronized void stateChanged(ChangeEvent pE) {
				tabSelectionChanged();
			}

		});
		getMapModuleManager().addListener(new MapModuleChangeObserver() {

			public void afterMapModuleChange(MapModule pOldMapModule,
					Mode pOldMode, MapModule pNewMapModule, Mode pNewMode) {
				int selectedIndex = mTabbedPane.getSelectedIndex();
				if (pNewMapModule == null) {
					return;
				}
				// search, if already present:
				for (int i = 0; i < mTabbedPaneMapModules.size(); ++i) {
					if (mTabbedPaneMapModules.get(i) == pNewMapModule) {
						if (selectedIndex != i) {
							mTabbedPane.setSelectedIndex(i);
						}
						return;
					}
				}
				// create new tab:
				mTabbedPaneMapModules.add(pNewMapModule);
				mTabbedPane.addTab(pNewMapModule.toString(), new JPanel());
				mTabbedPane.setSelectedIndex(mTabbedPane.getTabCount() - 1);
			}

			public void beforeMapModuleChange(MapModule pOldMapModule,
					Mode pOldMode, MapModule pNewMapModule, Mode pNewMode) {
			}

			public boolean isMapModuleChangeAllowed(MapModule pOldMapModule,
					Mode pOldMode, MapModule pNewMapModule, Mode pNewMode) {
				return true;
			}

			public void numberOfOpenMapInformation(int pNumber, int pIndex) {
			}

			public void afterMapClose(MapModule pOldMapModule, Mode pOldMode) {
				for (int i = 0; i < mTabbedPaneMapModules.size(); ++i) {
					if (mTabbedPaneMapModules.get(i) == pOldMapModule) {
						logger.fine("Remove tab:" + i + " with title:"
								+ mTabbedPane.getTitleAt(i));
						mTabbedPaneSelectionUpdate = false;
						mTabbedPane.removeTabAt(i);
						mTabbedPaneMapModules.remove(i);
						mTabbedPaneSelectionUpdate = true;
						tabSelectionChanged();
						return;
					}
				}
			}
		});
		registerMapTitleChangeListener(new MapModuleManager.MapTitleChangeListener() {

			public void setMapTitle(String pNewMapTitle, MapModule pMapModule,
					MindMap pModel) {
				for (int i = 0; i < mTabbedPaneMapModules.size(); ++i) {
					if (mTabbedPaneMapModules.get(i) == pMapModule) {
						mTabbedPane.setTitleAt(i,
								pNewMapTitle + ((pModel.isSaved()) ? "" : "*"));
					}
				}
			}
		});

	}

	private void tabSelectionChanged() {
		if (!mTabbedPaneSelectionUpdate)
			return;
		int selectedIndex = mTabbedPane.getSelectedIndex();
		// display nothing on the other tabs:
		for (int j = 0; j < mTabbedPane.getTabCount(); j++) {
			if (j != selectedIndex)
				mTabbedPane.setComponentAt(j, new JPanel());
		}
		if (selectedIndex < 0) {
			// nothing selected. probably, the last map was closed
			return;
		}
		MapModule module = (MapModule) mTabbedPaneMapModules.get(selectedIndex);
		logger.fine("Selected index of tab is now: " + selectedIndex
				+ " with title:" + module.toString());
		if (module != getMapModule()) {
			// we have to change the active map actively:
			getMapModuleManager().changeToMapModule(module.toString());
		}
		// mScrollPane could be set invisible by JTabbedPane
		frame.getScrollPane().setVisible(true);
		mTabbedPane.setComponentAt(selectedIndex, frame.getContentComponent());
		// double call, due to mac strangeness.
		obtainFocusForSelected();
	}

	protected void storePageFormat() {
		if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
			setProperty("page_orientation", "landscape");
		} else if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
			setProperty("page_orientation", "portrait");
		} else if (pageFormat.getOrientation() == PageFormat.REVERSE_LANDSCAPE) {
			setProperty("page_orientation", "reverse_landscape");
		}
		setProperty(PAGE_FORMAT_PROPERTY,
				Tools.getPageFormatAsString(pageFormat.getPaper()));
	}

}
