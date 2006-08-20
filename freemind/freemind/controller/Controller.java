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
/*$Id: Controller.java,v 1.40.14.21.2.14 2006-08-20 19:34:25 christianfoltin Exp $*/

package freemind.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
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
import java.awt.print.PrinterJob;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import freemind.controller.MapModuleManager.MapModuleChangeOberser;
import freemind.controller.filter.FilterController;
import freemind.controller.printpreview.PreviewDialog;
import freemind.main.FreeMind;
import freemind.main.FreeMindCommon;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLParseException;
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
 * Provides the methods to edit/change a Node.
 * Forwards all messages to MapModel(editing) or MapView(navigation).
 */
public class Controller  implements MapModuleChangeOberser {

    /**
     * Converts from a local link to the real file URL of the
     * documentation map. (Used to change this behaviour under MacOSX).
     */
    public static LocalLinkConverter localDocumentationLinkConverter = new DefaultLocalLinkConverter();


    private static Logger logger;
    private static JColorChooser colorChooser = new JColorChooser();
	private LastOpenedList lastOpened;//A list of the pathnames of all the maps that were opened in the last time
    private MapModuleManager mapModuleManager;// new MapModuleManager();
    /**  The current mode */
    private Mode mMode;
    private FreeMindMain frame;
    private JToolBar toolbar;
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
    private Icon bswatch = new BackgroundSwatch();//needed for BackgroundAction
    private boolean antialiasEdges = false;
    private boolean antialiasAll = false;
    private Map fontMap = new HashMap();

    private FilterController fc;

    boolean isPrintingAllowed=true;
    boolean menubarVisible=true;
    boolean toolbarVisible=true;
    boolean leftToolbarVisible=true;

    public CloseAction close;
    public Action print;
    public Action printDirect;
    public Action printPreview;
    public Action page;
    public Action quit;
    public Action background;

    public Action showAllAttributes = new ShowAllAttributesAction();
    public Action showSelectedAttributes = new ShowSelectedAttributesAction();
    public Action hideAllAttributes = new HideAllAttributesAction();
    
    public OptionAntialiasAction optionAntialiasAction;
    public Action optionHTMLExportFoldingAction;
    public Action optionSelectionMechanismAction;

    public Action about;
    public Action faq;
    public Action documentation;
    public Action license;
    public Action navigationPreviousMap;
    public Action showFilterToolbarAction;
    public Action showAttributeManagerAction;
    public Action navigationNextMap;

    public Action moveToRoot;
    public Action toggleMenubar;
    public Action toggleToolbar;
    public Action toggleLeftToolbar;

    public Action zoomIn;
    public Action zoomOut;
    public PropertyAction propertyAction;

	// this values better suit at least the test purposes
    private static final String[] zooms = {"25%","50%","75%","100%","150%","200%","300%","400%"};
//    private static final String[] zooms = {"25%","40%","60%","75%","100%","125%","150%","200%"};

    //
    // Constructors
    //
    public Controller(FreeMindMain frame) {
        this.frame = frame;
        if(logger == null) {
            logger = frame.getLogger(this.getClass().getName());
        }

        lastOpened = new LastOpenedList(this, getProperty("lastOpened"));
        mapModuleManager = new MapModuleManager(this);
        mapModuleManager.addListener(this);

        nodeMouseMotionListener = new NodeMouseMotionListener(this);
        nodeMotionListener = new NodeMotionListener(this);
        nodeKeyListener = new NodeKeyListener(this);
        nodeDragListener = new NodeDragListener(this);
        nodeDropListener = new NodeDropListener(this);

        mapMouseMotionListener = new MapMouseMotionListener(this);
        mapMouseWheelListener = new MapMouseWheelListener(this);

        close = new CloseAction(this);

        print = new PrintAction(this,true);
        printDirect = new PrintAction(this,false);
        printPreview = new PrintPreviewAction(this);
        page = new PageAction(this);
        quit = new QuitAction(this);
        background = new BackgroundAction(this,bswatch);
        about = new AboutAction(this);
        faq = new OpenFAQAction(this);
        documentation = new DocumentationAction(this);
        license = new LicenseAction(this);
        navigationPreviousMap = new NavigationPreviousMapAction(this);
        showFilterToolbarAction = new ShowFilterToolbarAction(this);
        showAttributeManagerAction = new ShowAttributeDialogAction(this);
        navigationNextMap = new NavigationNextMapAction(this);
        toggleMenubar = new ToggleMenubarAction(this);
        toggleToolbar = new ToggleToolbarAction(this);
        toggleLeftToolbar = new ToggleLeftToolbarAction(this);
        optionAntialiasAction = new OptionAntialiasAction(this);
        optionHTMLExportFoldingAction = new OptionHTMLExportFoldingAction(this);
        optionSelectionMechanismAction = new OptionSelectionMechanismAction(this);

        zoomIn = new ZoomInAction(this);
        zoomOut = new ZoomOutAction(this);
        propertyAction = new PropertyAction(this);

        moveToRoot = new MoveToRootAction(this);

        //Create the ToolBar
        northToolbarPanel = new JPanel(new BorderLayout());
        toolbar = new MainToolBar(this);
        fc = new FilterController(this);
        filterToolbar = fc.getFilterToolbar();
        getFrame().getContentPane().add( northToolbarPanel, BorderLayout.NORTH );
        northToolbarPanel.add( toolbar, BorderLayout.NORTH);
        northToolbarPanel.add( filterToolbar, BorderLayout.SOUTH);

        setAllActions(false);

        if (!Tools.isAvailableFontFamily(getProperty("defaultfont"))) {
           logger.warning("Warning: the font you have set as standard - "+getProperty("defaultfont")+
                              " - is not available.");
           frame.setProperty("defaultfont","SansSerif"); }
    }

    //
    // get/set methods
    //
    public static final String JAVA_VERSION = System.getProperty("java.version");

    public String getProperty(String property) {
	   return frame.getProperty(property); }

	public int getIntProperty(String property, int defaultValue) {
	   return frame.getIntProperty(property, defaultValue); }

    public void setProperty(String property, String value) {
       frame.setProperty(property, value); }

    public FreeMindMain getFrame() {
        return frame;
    }

    public JFrame getJFrame() {
        FreeMindMain f = getFrame();
        if (f instanceof JFrame) return (JFrame) f;
        return null;
    }
    
    public URL getResource(String resource) {
        return getFrame().getResource(resource);
    }

    public String getResourceString(String resource) {
          return frame.getResourceString(resource);
    }

	/** @return the current modeController. */
	public ModeController getModeController() {
		if (getMapModule() != null) {
			return getMapModule().getModeController();
		}
		// no map present: we take the default:
		return getMode().getDefaultModeController();
	}



    /**Returns the current model*/
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
           System.err.println("[Freemind-Developer-Internal-Warning (do not write a bug report, please)]: Tried to get view without being able to get map module.");
        }
        return null;
    }

    Set getModes() {
        return mModescreator.getAllModes();
    }

    public Mode getMode() {
        return mMode;
    }

    public String[] getZooms() {
       return zooms; }

    public MapModuleManager getMapModuleManager() {
        return mapModuleManager;
    }

    public LastOpenedList getLastOpenedList() {
        return lastOpened;
    }

    //

    private MapModule getMapModule() {
        return getMapModuleManager().getMapModule();
    }

    private JToolBar getToolBar() {
        return toolbar;
    }

    //

    public Font getFontThroughMap(Font font) {
       if (!fontMap.containsKey(font.toString())) {
          fontMap.put(font.toString(),font); }
       return (Font)fontMap.get(font.toString()); }

    //

    public void setAntialiasEdges(boolean antialiasEdges) {
       this.antialiasEdges = antialiasEdges; }

    public void setAntialiasAll(boolean antialiasAll) {
       this.antialiasAll = antialiasAll; }

    public boolean getAntialiasEdges() {
       return antialiasEdges; }

    public boolean getAntialiasAll() {
       return antialiasAll; }

    public Font getDefaultFont() {
       // Maybe implement handling for cases when the font is not
       // available on this system.

       int fontSize = getDefaultFontSize();
       int fontStyle = getDefaultFontStyle();
       String fontFamily = getDefaultFontFamilyName();

       return getFontThroughMap (new Font(fontFamily, fontStyle, fontSize)); }

	/**
     */
    public String getDefaultFontFamilyName() {
        String fontFamily = getProperty("defaultfont");
        return fontFamily;
    }

    /**
     */
    public int getDefaultFontStyle() {
        int fontStyle = Integer.parseInt(getFrame().getProperty("defaultfontstyle"));
        return fontStyle;
    }

    /**
     */
    public int getDefaultFontSize() {
        int fontSize = Integer.parseInt(getFrame().getProperty("defaultfontsize"));
        return fontSize;
    }

    /** Static JColorChooser to have  the recent colors feature. */
	static public JColorChooser getCommonJColorChooser() {
		return colorChooser;
	}

	public static Color showCommonJColorChooserDialog(Component component,
		String title, Color initialColor) throws HeadlessException {

		final JColorChooser pane = getCommonJColorChooser();
		pane.setColor(initialColor);

		ColorTracker ok = new ColorTracker(pane);
		JDialog dialog = JColorChooser.createDialog(component, title, true, pane, ok, null);
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

	static class Closer extends WindowAdapter implements Serializable{
		 public void windowClosing(WindowEvent e) {
			 Window w = e.getWindow();
			 w.hide();
		 }
	 }

	 static class DisposeOnClose extends ComponentAdapter implements Serializable{
		 public void componentHidden(ComponentEvent e) {
			 Window w = (Window)e.getComponent();
			 w.dispose();
		 }
	 }


	 public boolean isMapModuleChangeAllowed(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
		return true;
	}

	 public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
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
        }
        /* other toolbars are to be removed too. */
        if (oldModeController.getLeftToolBar() != null) {
            getFrame().getContentPane().remove(
                    oldModeController.getLeftToolBar());
        }
    }

	 public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
        ModeController newModeController;
        if (newMapModule != null) {
            getFrame().setView(newMapModule.getView());
            setAllActions(true);
            if (getView().getSelected() == null) {
                // Only for the new modules move to root
                moveToRoot();
            }
            lastOpened.mapOpened(newMapModule);
            ((MainToolBar) getToolbar()).setZoomComboBox(newMapModule.getView()
                    .getZoom());
            obtainFocusForSelected();
            newModeController = newMapModule.getModeController();
            newModeController.startupController();
            newModeController.setVisible(true);
        } else {
            newModeController = newMode.getDefaultModeController();
            getFrame().setView(null);
            setAllActions(false);
        }
        setTitle();
        JToolBar newToolBar = newModeController.getModeToolBar();
        if (newToolBar != null) {
            toolbar.add(newToolBar);
            newToolBar.repaint();
        }
        /* new left toolbar. */
        Component newLeftToolBar = newModeController.getLeftToolBar();
        if (newLeftToolBar != null) {
            getFrame().getContentPane().add(newLeftToolBar, BorderLayout.WEST);
            newLeftToolBar.repaint();
        }
        toolbar.validate();
        toolbar.repaint();
        MenuBar menuBar = getFrame().getFreeMindMenuBar();
        menuBar.updateMenus(newModeController);
        menuBar.validate();
        menuBar.repaint();

    }

	public void numberOfOpenMapInformation(int number) {
		navigationPreviousMap.setEnabled(number>0);
		navigationNextMap.setEnabled(number>0);
	}


    /** Creates a new mode (controller), activates the toolbars, title and deactivates all
     * actions.
     * Does nothing, if the mode is identical to the current mode.
     *
     * @return false if the change was not successful.
     */
    public boolean createNewMode(String mode) {
        if (getMode() != null && mode.equals(getMode().toString())) {
            return true;
        }

        //Check if the mode is available and create ModeController.
        Mode newMode = mModescreator.getMode(mode);
        if (newMode == null) {
            errorMessage(getResourceString("mode_na")+": "+mode);
            return false;
        }


        // change the map module to get changed toolbars etc.:
        getMapModuleManager().setMapModule(null, newMode);

        setTitle();
        getMode().activate();

        Object[] messageArguments = {
        		getMode().toString()
        };
        MessageFormat formatter = new MessageFormat(getResourceString("mode_status"));
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
        if (getMode() != null && getModeController().getLeftToolBar() != null) {
           leftToolbarVisible = visible;
           getModeController().getLeftToolBar().setVisible(leftToolbarVisible);
           ((JComponent)getModeController().getLeftToolBar().getParent()).revalidate();
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
     * I don't understand how this works now (it's called twice etc.)
     * but it _works_ now. So let it alone or fix it to be understandable,
     * if you have the time ;-)
     */
    void moveToRoot() {
        if (getMapModule() != null) {
            getView().moveToRoot();
        }
    }

    /** Closes the actual map.
     * @param force true= without save.
     */
    public void close(boolean force) {
		getMapModuleManager().close(force);
	}



// (PN) %%%
//    public void select( NodeView node) {
//        getView().select(node,false);
//        getView().setSiblingMaxLevel(node.getModel().getNodeLevel()); // this level is default
//    }
//
//    void selectBranch( NodeView node, boolean extend ) {
//        getView().selectBranch(node,extend);
//    }
//
//    boolean isSelected( NodeView node ) {
//        return getView().isSelected(node);
//    }
//
//    void centerNode() {
//        getView().centerNode(getView().getSelected());
//    }
//
//    private MindMapNode getSelected() {
//        return getView().getSelected().getModel();
//    }

    public void informationMessage(Object message) {
       JOptionPane.showMessageDialog(getFrame().getContentPane(), message.toString(), "FreeMind", JOptionPane.INFORMATION_MESSAGE); }

    public void informationMessage(Object message, JComponent component) {
       JOptionPane.showMessageDialog(component, message.toString(), "FreeMind", JOptionPane.INFORMATION_MESSAGE); }

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
		JOptionPane.showMessageDialog(getFrame().getContentPane(), myMessage, "FreeMind", JOptionPane.ERROR_MESSAGE);

	}

    public void errorMessage(Object message, JComponent component) {
       JOptionPane.showMessageDialog(component, message.toString(), "FreeMind", JOptionPane.ERROR_MESSAGE); }

    public void obtainFocusForSelected() {
        SwingUtilities.invokeLater( new Runnable() {
                public void run () {
                    if (getView() != null) { // is null if the last map was closed.
                        getView().getSelected().requestFocus();
                    } else {
                        // fc, 6.1.2004: bug fix, that open and quit are not working if no map is present.
                        // to avoid this, the menu bar gets the focus, and everything seems to be all right!!
                        // but I cannot avoid thinking of this change to be a bad hack ....
                        getFrame().getFreeMindMenuBar().requestFocus();
                    }
                }
            });
    }

    //
    // Map Navigation
    //

    //
    // other
    //

    public void setZoom(float zoom) {
        getView().setZoom(zoom);
        ((MainToolBar)toolbar).setZoomComboBox(zoom);
        // show text in status bar:
        Object[] messageArguments = {
         String.valueOf(zoom*100f)
        };
        MessageFormat formatter = new MessageFormat(getResourceString("user_defined_zoom_status_bar"));
        getFrame().out(formatter.format(messageArguments));
    }


    //////////////
    // Private methods. Internal implementation
    ////////////


    //
    // Node editing
    //
// (PN)
//    private void getFocus() {
//        getView().getSelected().requestFocus();
//    }

    //
    // Multiple Views management
    //


	/**
	 * Set the Frame title with mode and file if exist
	 */
	public void setTitle() {
		Object[] messageArguments = {
			getMode().toString()
		};
		MessageFormat formatter = new MessageFormat
		   (getResourceString("mode_title"));
		String title = formatter.format(messageArguments);
		if (getMapModule() != null) {
			title = getMapModule().toString() + " - " + title +
			  ( getMapModule().getModel().isReadOnly() ?
				" ("+getResourceString("read_only")+")" : "");
		}
		getFrame().setTitle(title);
	}
    //
    // Actions management
    //

    /**
     * Manage the availabilty of all Actions dependend
     * of whether there is a map or not
     */
    public void setAllActions(boolean enabled) {
        background.setEnabled(enabled);

        if(isPrintingAllowed) {
            print.setEnabled(enabled);
            printDirect.setEnabled(enabled);
            page.setEnabled(enabled);
        } else {
            //should only be done once, or?
            print.setEnabled(false);
            printDirect.setEnabled(false);
            page.setEnabled(false);
        }
        close.setEnabled(enabled);
        moveToRoot.setEnabled(enabled);
        ((MainToolBar)getToolBar()).setAllActions(enabled);
    }

    //
    // program/map control
    //

    private void quit() {
        String currentMapRestorable = (getModel()!=null) ? getModel().getRestoreable() : null;
        while (getView() != null) {
        	boolean closingNotCancelled = getMapModuleManager().close(false);
        	if  (!closingNotCancelled) {
        	   return; }}

        String lastOpenedString=lastOpened.save();
        setProperty("lastOpened",lastOpenedString);
        if (currentMapRestorable != null) {
           getFrame().setProperty(FreeMindCommon.ON_START_IF_NOT_SPECIFIED,currentMapRestorable); }
        // getFrame().setProperty("menubarVisible",menubarVisible ? "true" : "false");
        // ^ Not allowed in application because of problems with not working key shortcuts
        setProperty("toolbarVisible", toolbarVisible ? "true" : "false");
        setProperty("leftToolbarVisible", leftToolbarVisible ? "true" : "false");
        setProperty("antialiasEdges", antialiasEdges ? "true" : "false");
        setProperty("antialiasAll", antialiasAll ? "true" : "false");
        setProperty("appwindow_width", String.valueOf(getFrame().getWinWidth()));
        setProperty("appwindow_height", String.valueOf(getFrame().getWinHeight()));
        setProperty("appwindow_state", String.valueOf(getFrame().getWinState()));
        getFrame().saveProperties();
        //save to properties
        System.exit(0);
    }

    private boolean acquirePrinterJobAndPageFormat() {
       if (printerJob == null) {
          try {
             printerJob = PrinterJob.getPrinterJob(); }
          catch (SecurityException ex) {
             isPrintingAllowed = false;
             return false; }}
       if (pageFormat == null) {
           pageFormat = printerJob.defaultPage();
           if (Tools.safeEquals(getProperty("page_orientation"), "landscape")) {
               pageFormat.setOrientation(PageFormat.LANDSCAPE);
           } else if (Tools.safeEquals(getProperty("page_orientation"), "portrait")) {
               pageFormat.setOrientation(PageFormat.PORTRAIT);
           } else if (Tools.safeEquals(getProperty("page_orientation"), "reverse_landscape")) {
               pageFormat.setOrientation(PageFormat.REVERSE_LANDSCAPE);
           }
       }
       return true; }

    //////////////
    // Inner Classes
    ////////////

    /**
     * Manages the history of visited maps.
     * Maybe explicitly closed maps should be removed from
     * History too?
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

    /**This closes only the current map*/
    public static class CloseAction extends AbstractAction {
        private final Controller controller;
		CloseAction(Controller controller) {
            super(controller.getResourceString("close"));
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
            super(controller.getResourceString("print"),
                  new ImageIcon(getResource("images/fileprint.png")));
            this.controller = controller;
            setEnabled(false);
            this.isDlg = isDlg;
        }
        public void actionPerformed(ActionEvent e) {
            if (!acquirePrinterJobAndPageFormat()) {
               return; }

            printerJob.setPrintable(getView(),pageFormat);

            if (!isDlg || printerJob.printDialog()) {
				getView().preparePrinting();
                try {
                    printerJob.print();
                } catch (Exception ex) {
                    freemind.main.Resources.getInstance().logExecption(ex);
                }
				getView().endPrinting();
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
               return; }
            PreviewDialog previewDialog = new PreviewDialog(controller.getResourceString("print_preview"), getView());
            previewDialog.pack();
            previewDialog.setLocationRelativeTo(JOptionPane.getFrameForComponent(getView()));
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
               return; }

            // Ask about custom printing settings
            final JDialog dialog = new JDialog((JFrame)getFrame(), getResourceString("printing_settings"), /*modal=*/true);
            final JCheckBox fitToPage = new JCheckBox(getResourceString("fit_to_page"), Tools.safeEquals("true", getProperty("fit_to_page")));
            final JLabel userZoomL = new JLabel(getResourceString("user_zoom"));
            final JTextField userZoom = new JTextField(getProperty("user_zoom"),3);
            userZoom.setEditable(!fitToPage.isSelected());
            final JButton okButton = new JButton(getResourceString("ok"));
            final Tools.IntHolder eventSource = new Tools.IntHolder();
            JPanel panel = new JPanel();

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();

            eventSource.setValue(0);
            okButton.addActionListener (new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     eventSource.setValue(1);
                     dialog.dispose(); }});
            fitToPage.addItemListener (new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    userZoom.setEditable(e.getStateChange() == ItemEvent.DESELECTED);
                }
            });

            //c.weightx = 0.5;
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
            c.insets = new Insets(10,0,0,0);
            gridbag.setConstraints(okButton, c);
            panel.add(okButton);
            panel.setLayout(gridbag);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setContentPane(panel);
            dialog.setLocationRelativeTo((JFrame)getFrame());
            dialog.getRootPane().setDefaultButton(okButton);
            dialog.pack();  // calculate the size
            dialog.show();

            if (eventSource.getValue() == 1) {
               setProperty("user_zoom", userZoom.getText());
               setProperty("fit_to_page", fitToPage.isSelected() ? "true" : "false"); }
            else
               return;

            // Ask user for page format (e.g., portrait/landscape)
            pageFormat = printerJob.pageDialog(pageFormat);
            if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
                setProperty("page_orientation", "landscape");
            } else if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
                setProperty("page_orientation", "portrait");
            } else if (pageFormat.getOrientation() == PageFormat.REVERSE_LANDSCAPE) {
                setProperty("page_orientation", "reverse_landscape");
            }
        }
    }

    public interface LocalLinkConverter {
    		String convertLocalLink(String link);
    }
    
    private static class DefaultLocalLinkConverter implements LocalLinkConverter {

		public String convertLocalLink(String map) {
            /* new handling for relative urls. fc, 29.10.2003.*/
			return "file:" + System.getProperty("user.dir") + map.substring(1);//remove "." and make url
			/* end: new handling for relative urls. fc, 29.10.2003.*/
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
            String map = controller.getFrame().getResourceString("browsemode_initial_map");
            // if the current language does not provide its own translation, POSTFIX_TRANSLATE_ME is appended:
            map = Tools.removeTranslateComment(map);
            if (map != null && map.startsWith("."))  {
                map = localDocumentationLinkConverter.convertLocalLink(map);
            }
            if (map != null && map != "") {
                URL url = null;
                try {
                    url = new URL(map);
                } catch (MalformedURLException e2) {
                    freemind.main.Resources.getInstance().logExecption(e2);
                    return;
                }
                final URL endUrl = url;
                // invokeLater is necessary, as the mode changing removes all menus (inclusive this action!).
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            createNewMode(BrowseMode.MODENAME);
                            controller.getModeController().load(endUrl);
                        } catch (Exception e1) {
                            freemind.main.Resources.getInstance().logExecption(e1);
                        }
                    }
                });
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
           JOptionPane.showMessageDialog(getFrame().getViewport(),controller.getResourceString("about_text")+FreeMind.VERSION);
        }
    }

    private class LicenseAction extends AbstractAction {
        Controller controller;
        LicenseAction(Controller controller) {
            super(controller.getResourceString("license"));
            this.controller = controller;
        }
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(getView(),controller.getResourceString("license_text"));
        }
    }


    //
    // Map navigation
    //

    private class NavigationPreviousMapAction extends AbstractAction {
        NavigationPreviousMapAction(Controller controller) {
            super(controller.getResourceString("previous_map"),
                  new ImageIcon(getResource("images/1leftarrow.png")));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent event) {
            mapModuleManager.previousMapModule();
        }
    }

    private class ShowAttributeDialogAction extends AbstractAction {
        private Controller c;
        ShowAttributeDialogAction(Controller c) {
            super(c.getResourceString("attributes_dialog"),
                  new ImageIcon(getResource("images/showAttributes.gif")));
            this.c = c;
        }
		private AttributeManagerDialog getAttributeDialog() {
			if (attributeDialog == null) {
			    attributeDialog = new AttributeManagerDialog(c);
			}
			return attributeDialog;
		}

		 public void actionPerformed(ActionEvent e) {
		     if (getAttributeDialog().isVisible() == false)
		     {
		         getAttributeDialog().pack();
		         getAttributeDialog().setVisible(true);
		     }
		}
    }

    private class ShowFilterToolbarAction extends AbstractAction {
        ShowFilterToolbarAction(Controller controller) {
            super("",
                  new ImageIcon(getResource("images/filter.gif")));
        }
        public void actionPerformed(ActionEvent event) {
            JToggleButton btnFilter = (JToggleButton)event.getSource();
            if(btnFilter.getModel().isSelected()){
                getFilterController().showFilterToolbar(true);
            }
            else{
                getFilterController().showFilterToolbar(false);
            }
        }
    }

    private class NavigationNextMapAction extends AbstractAction {
        NavigationNextMapAction(Controller controller) {
            super(controller.getResourceString("next_map"),
                  new ImageIcon(getResource("images/1rightarrow.png")));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent event) {
            mapModuleManager.nextMapModule();
        }
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

    private class ToggleMenubarAction extends AbstractAction {
        ToggleMenubarAction(Controller controller) {
           super(controller.getResourceString("toggle_menubar"));
           setEnabled(true);
        }
        public void actionPerformed(ActionEvent event) {
           menubarVisible=!menubarVisible;
           setMenubarVisible(menubarVisible);
        }
    }

    private class ToggleToolbarAction extends AbstractAction {
        ToggleToolbarAction(Controller controller) {
           super(controller.getResourceString("toggle_toolbar"));
           setEnabled(true);
        }
        public void actionPerformed(ActionEvent event) {
           toolbarVisible=!toolbarVisible;
           setToolbarVisible(toolbarVisible);
        }
    }

    private class ToggleLeftToolbarAction extends AbstractAction {
        ToggleLeftToolbarAction(Controller controller) {
           super(controller.getResourceString("toggle_left_toolbar"));
           setEnabled(true);
        }
        public void actionPerformed(ActionEvent event) {
           leftToolbarVisible=!leftToolbarVisible;
           setLeftToolbarVisible(leftToolbarVisible);
        }
    }

    protected class ZoomInAction extends AbstractAction {
        public ZoomInAction(Controller controller) {
           super(controller.getResourceString("zoom_in")); }
        public void actionPerformed(ActionEvent e) {
            logger.info("ZoomInAction actionPerformed");
           ((MainToolBar)toolbar).zoomIn(); }}

    protected class ZoomOutAction extends AbstractAction {
        public ZoomOutAction(Controller controller) {
           super(controller.getResourceString("zoom_out")); }
        public void actionPerformed(ActionEvent e) {
            logger.info("ZoomOutAction actionPerformed");
           ((MainToolBar)toolbar).zoomOut(); }}

    private class ShowAllAttributesAction extends AbstractAction {
        public ShowAllAttributesAction(){
            super(Resources.getInstance().getResourceString("attributes_show_all"));
        };
        public void actionPerformed(ActionEvent e) {
            final MindMap map = getMap();
            setAttributeViewType(map);
        }
        public void setAttributeViewType(final MindMap map) {
            final AttributeRegistry attributes = map.getRegistry().getAttributes();
            if(attributes.getAttributeViewType() != AttributeTableLayoutModel.SHOW_ALL){
                attributes.setAttributeViewType(AttributeTableLayoutModel.SHOW_ALL);
            }
        }
    }    
    private class HideAllAttributesAction extends AbstractAction {
        public HideAllAttributesAction(){
            super(Resources.getInstance().getResourceString("attributes_hide_all"));
        };
        public void actionPerformed(ActionEvent e) {
            final MindMap map = getMap();
            setAttributeViewType(map);
        }
        public void setAttributeViewType(final MindMap map) {
            final AttributeRegistry attributes = map.getRegistry().getAttributes();
            if(attributes.getAttributeViewType() != AttributeTableLayoutModel.HIDE_ALL){
                attributes.setAttributeViewType(AttributeTableLayoutModel.HIDE_ALL);
            }
        }
    }

    private class ShowSelectedAttributesAction extends AbstractAction {
        public ShowSelectedAttributesAction(){
            super(Resources.getInstance().getResourceString("attributes_show_selected"));
        };
        public void actionPerformed(ActionEvent e) {
            MindMap map = getMap();
            setAttributeViewType(map);
        }
        void setAttributeViewType(MindMap map){
            final AttributeRegistry attributes = map.getRegistry().getAttributes();
            if(attributes.getAttributeViewType() != AttributeTableLayoutModel.SHOW_SELECTED){
                attributes.setAttributeViewType(AttributeTableLayoutModel.SHOW_SELECTED);
            }
        }
    }

    //
    // Preferences
    //

    private static Vector propertyChangeListeners = new Vector();

    private AttributeManagerDialog attributeDialog = null;

    public static Collection getPropertyChangeListeners() {
        return Collections.unmodifiableCollection(propertyChangeListeners);
    }
    /**
     */
    public MindMap getMap() {
        return getMapModule().getModel();
    }

    public static void addPropertyChangeListener(FreemindPropertyListener listener) {
        Controller.propertyChangeListeners.add(listener);
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
			final OptionPanel options = new OptionPanel(getFrame(), dialog, new OptionPanelFeedback() {

				public void writeProperties(Properties props) {
					Vector sortedKeys = new Vector();
					sortedKeys.addAll(props.keySet());
					Collections.sort(sortedKeys);
					HashMap oldProperties = new HashMap();
					for (Iterator i = sortedKeys.iterator(); i.hasNext();) {
						String key = (String) i.next();
						// save only changed keys:
						String oldProperty = controller.getProperty(key);
                        String newProperty = props.getProperty(key);
                        if (!oldProperty.equals(newProperty)) {
						    oldProperties.put(key, oldProperty);
							controller.setProperty(key, newProperty);
						}
					}

					for (Iterator i = Controller.getPropertyChangeListeners().iterator(); i.hasNext();) {
						FreemindPropertyListener listener = (FreemindPropertyListener) i
								.next();
						for (Iterator j = oldProperties.keySet().iterator(); j
                                .hasNext();) {
                            String key = (String) j.next();
    						listener.propertyChanged(key, controller.getProperty(key), (String) oldProperties.get(key));
                        }
					}

					if (oldProperties.size() > 0) {
                        JOptionPane
                                .showMessageDialog(
                                        null,
                                        getResourceString("option_changes_may_require_restart"));
                        controller.getFrame().saveProperties();
                    }
				}
			});
			options.buildPanel();
			options.setProperties(getFrame().getProperties());
			dialog.setTitle("Freemind Properties");
			dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			dialog.addWindowListener(new WindowAdapter(){
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


			dialog.pack();
			dialog.setVisible(true);

		}


	}

	private class BackgroundSwatch extends ColorSwatch {
        Color getColor() {
            return getModel().getBackgroundColor();
        }
    }

    /** Seems to be obsolete, but we add the property listener here. fc, 14.6.2005*/
    private class BackgroundAction extends AbstractAction {
        BackgroundAction(Controller controller, Icon icon) {
            super(controller.getResourceString("background"),icon);
            Controller.addPropertyChangeListener(new FreemindPropertyListener(){

                public void propertyChanged(String propertyName, String newValue, String oldValue) {
                    if(propertyName.equals(FreeMind.RESOURCES_BACKGROUND_COLOR)) {
                        getModel().setBackgroundColor(Tools.xmlToColor(newValue));
                    }
                }});
        }
        public void actionPerformed(ActionEvent e) {
            Color color = showCommonJColorChooserDialog(getView(),getResourceString("choose_background_color"),getView().getBackground() );
            getModel().setBackgroundColor(color);

        }
    }

    public class OptionAntialiasAction extends AbstractAction implements FreemindPropertyListener {
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
	        if(command == null) {
	            return;
	        }
	        if (command.equals("antialias_none")) {
	             setAntialiasEdges(false);
	             setAntialiasAll(false); }
	          if (command.equals("antialias_edges")) {
	             setAntialiasEdges(true);
	             setAntialiasAll(false); }
	          if (command.equals("antialias_all")) {
	             setAntialiasEdges(false);
	             setAntialiasAll(true); }
	          if(getView() != null)
	              getView().repaint();
	    }
	    public void propertyChanged(String propertyName, String newValue, String oldValue) {
            if (propertyName.equals(FreeMindCommon.RESOURCE_ANTIALIAS)) {
                changeAntialias(newValue);
            }
	    }
    }

    private class OptionHTMLExportFoldingAction extends AbstractAction {
       OptionHTMLExportFoldingAction(Controller controller) {}
       public void actionPerformed(ActionEvent e) {
          setProperty("html_export_folding", e.getActionCommand()); }}

    // switch auto properties for selection mechanism fc, 7.12.2003.
    private class OptionSelectionMechanismAction extends AbstractAction implements FreemindPropertyListener {
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

        public void propertyChanged(String propertyName, String newValue, String oldValue) {
            if(propertyName.equals(FreeMind.RESOURCES_SELECTION_METHOD)) {
                changeSelection(newValue);
            }
        }
    }

    // open faq url from freeminds page:
    private class OpenFAQAction extends AbstractAction {
        Controller c;
        OpenFAQAction(Controller controller) {
            super(controller.getResourceString("FAQ"), new ImageIcon(controller.getResource("images/Link.png")));
            c = controller;
        }
        public void actionPerformed(ActionEvent e) {
            try {
                c.getFrame().openDocument(new URL("http://freemind.sourceforge.net/faq.html"));
            } catch (MalformedURLException ex) {
                c.errorMessage(c.getResourceString("url_error")+"\n"+ex);
            } catch (Exception ex) {
                c.errorMessage(ex);
            }
        }
    }


    public FilterController getFilterController() {
        return fc;
    }

    public PageFormat getPageFormat() {
        return pageFormat;
    }
    public void setAttributeViewType(MindMap map, String value) {
        if(value.equals(AttributeTableLayoutModel.SHOW_SELECTED)){
            ((ShowSelectedAttributesAction)showSelectedAttributes).setAttributeViewType(map);
        }
        else if(value.equals(AttributeTableLayoutModel.HIDE_ALL)){
            ((HideAllAttributesAction)hideAllAttributes).setAttributeViewType(map);
        }
        else if(value.equals(AttributeTableLayoutModel.SHOW_ALL)){
            ((ShowAllAttributesAction)showAllAttributes).setAttributeViewType(map);
        }
    }


}

