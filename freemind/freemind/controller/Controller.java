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
/*$Id: Controller.java,v 1.40.10.5 2004-05-21 21:49:10 christianfoltin Exp $*/

package freemind.controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.swing.*;

import java.net.MalformedURLException;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.Mode;
import freemind.modes.ModeController;

import freemind.modes.ModesCreator;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;

/**
 * Provides the methods to edit/change a Node.
 * Forwards all messages to MapModel(editing) or MapView(navigation).
 */
public class Controller {

    private static JColorChooser colorChooser = new JColorChooser();
	private LastOpenedList lastOpened;//A list of the pathnames of all the maps that were opened in the last time
    private MapModuleManager mapModuleManager;// new MapModuleManager();
    private HistoryManager history = new HistoryManager();
    private Map modes; //hash of all possible modes
    private Mode mode; //The current mode
    private FreeMindMain frame;
    private JToolBar toolbar;
    private NodeMouseMotionListener nodeMouseMotionListener;
    private NodeKeyListener nodeKeyListener;
    private NodeDragListener nodeDragListener;
    private NodeDropListener nodeDropListener;
    private MapMouseMotionListener mapMouseMotionListener;
    private MapMouseWheelListener mapMouseWheelListener;
    private ModesCreator modescreator = new ModesCreator(this);
    private PageFormat pageFormat = null;
    private PrinterJob printerJob = null;
    private Icon bswatch = new BackgroundSwatch();//needed for BackgroundAction
    private boolean antialiasEdges = false;
    private boolean antialiasAll = false;
    private Map fontMap = new HashMap();

    boolean isPrintingAllowed=true;     
    boolean menubarVisible=true;
    boolean toolbarVisible=true;
    boolean leftToolbarVisible=true;

    Action close; 
    Action print; 
    Action printDirect; 
    Action page; 
    public Action quit;
    Action background; 

    Action optionAntialiasAction;
    Action optionHTMLExportFoldingAction;
    Action optionSelectionMechanismAction;

    Action about;
    Action faq;
    Action documentation;
    Action license;
    Action historyPreviousMap;
    Action historyNextMap;
    Action navigationPreviousMap;
    Action navigationNextMap;

    Action moveToRoot;
    Action toggleMenubar;
    Action toggleToolbar;
    Action toggleLeftToolbar;

    Action zoomIn;
    Action zoomOut;

    private static final String[] zooms = {"25%","40%","60%","75%","100%","125%","150%","200%"};

    //
    // Constructors
    //

    public Controller(FreeMindMain frame) {
        checkJavaVersion();

        this.frame = frame;
        modes = modescreator.getAllModes();
        mapModuleManager = new MapModuleManager(this);
        lastOpened = new LastOpenedList(this, getProperty("lastOpened"));

        nodeMouseMotionListener = new NodeMouseMotionListener(this);
        nodeKeyListener = new NodeKeyListener(this);
        nodeDragListener = new NodeDragListener(this);
        nodeDropListener = new NodeDropListener(this);

        mapMouseMotionListener = new MapMouseMotionListener(this);
        mapMouseWheelListener = new MapMouseWheelListener(this);

        close = new CloseAction(this);

        print = new PrintAction(this,true);
        printDirect = new PrintAction(this,false);
        page = new PageAction(this);
        quit = new QuitAction(this);
        background = new BackgroundAction(this,bswatch);
        about = new AboutAction(this);
        faq = new OpenFAQAction(this);
        documentation = new DocumentationAction(this);
        license = new LicenseAction(this);
        historyPreviousMap = new HistoryPreviousMapAction(this);
        historyNextMap = new HistoryNextMapAction(this);
        navigationPreviousMap = new NavigationPreviousMapAction(this);
        navigationNextMap = new NavigationNextMapAction(this);
        toggleMenubar = new ToggleMenubarAction(this);
        toggleToolbar = new ToggleToolbarAction(this);
        toggleLeftToolbar = new ToggleLeftToolbarAction(this);
        optionAntialiasAction = new OptionAntialiasAction(this);
        optionHTMLExportFoldingAction = new OptionHTMLExportFoldingAction(this);
        optionSelectionMechanismAction = new OptionSelectionMechanismAction(this);

        zoomIn = new ZoomInAction(this);
        zoomOut = new ZoomOutAction(this);


        moveToRoot = new MoveToRootAction(this);

        //Create the ToolBar
        toolbar = new MainToolBar(this);
        getFrame().getContentPane().add( toolbar, BorderLayout.NORTH );

        setAllActions(false);

        if (!Tools.isAvailableFontFamily(getProperty("standardfont"))) {
           System.out.println("Warning: the font you have set as standard - "+getProperty("standardfont")+
                              " - is not available.");
           frame.setProperty("standardfont","SansSerif"); }
    }

    //
    // get/set methods
    //

    public void checkJavaVersion() {
       if (System.getProperty("java.version").compareTo("1.4.0") < 0) {
          String message = "Warning: FreeMind requires version Java 1.4.0 or higher (your version: "+
             System.getProperty("java.version")+").";
          System.err.println(message);
          JOptionPane.showMessageDialog(null, message, "FreeMind", JOptionPane.WARNING_MESSAGE); }}

    public String getProperty(String property) {
       return frame.getProperty(property); }

    public void setProperty(String property, String value) {
       frame.setProperty(property, value); }

    public FreeMindMain getFrame() {
        return frame;
    }

    public URL getResource(String resource) {
        return getFrame().getResource(resource);
    }
                                            
    public String getResourceString(String resource) {
       try {
          return frame.getResources().getString(resource); }
       catch (Exception ex) {
          System.err.println("Warning - resource string not found:"+resource);
          return resource; }}

	/** @return the current modeController. */
	public ModeController getModeController() {
		return getMode().getModeController();
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

    Map getModes() {
        return modes;
    }

    public Mode getMode() {
        return mode;
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

       int fontSize = Integer.parseInt(getFrame().getProperty("defaultfontsize"));
       int fontStyle = Integer.parseInt(getFrame().getProperty("defaultfontstyle"));
       String fontFamily = getProperty("defaultfont");

       return getFontThroughMap (new Font(fontFamily, fontStyle, fontSize)); }

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



    public boolean changeToMode(String mode) {
        if (getMode() != null && mode.equals(getMode().toString())) {
            return true;
        }

        //Check if the mode is available
        Mode newmode = (Mode)modes.get(mode);
        if (newmode == null) {
            errorMessage(getResourceString("mode_na")+": "+mode);
            return false;
        }

        if (getMode() != null && getMode().getModeToolBar() != null) {
            toolbar.remove(getMode().getModeToolBar());
        }
        /*  other toolbars are to be removed too.*/
        if (getMode() != null && getMode().getLeftToolBar() != null) {
            getFrame().getContentPane().remove(getMode().getLeftToolBar());
        }

        if (getMapModule() != null) {
            getMapModuleManager().setMapModule(null);
            getMapModuleManager().mapModuleChanged();
        }
        this.mode = newmode;
                
        if (getMode().getModeToolBar() != null) {
            toolbar.add(getMode().getModeToolBar());
            getMode().getModeToolBar().repaint();
        }
        /* new left toolbar.*/
        if (getMode().getLeftToolBar() != null) {
            getFrame().getContentPane().add(getMode().getLeftToolBar(), BorderLayout.WEST );
            getMode().getLeftToolBar().repaint();
        }
        toolbar.validate();
        toolbar.repaint();

        setTitle();
        getMode().activate();

        getFrame().getFreeMindMenuBar().updateMenus();

        if (getMapModule() == null) {
            setAllActions(false);
        }

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

    public void setLeftToolbarVisible(boolean visible) {
        if (getMode() != null && getMode().getLeftToolBar() != null) {
           leftToolbarVisible = visible;
           getMode().getLeftToolBar().setVisible(leftToolbarVisible);
        }
    }

    public NodeKeyListener getNodeKeyListener() {
        return nodeKeyListener;
    }

    public NodeMouseMotionListener getNodeMouseMotionListener() {
        return nodeMouseMotionListener;
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
       JOptionPane.showMessageDialog(getFrame().getContentPane(), message.toString(), "FreeMind", JOptionPane.ERROR_MESSAGE); }

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
			title += " - " + getMapModule().toString() +               
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
    private void setAllActions(boolean enabled) {
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
        	boolean closingNotCancelled = getMapModuleManager().close();
        	if  (!closingNotCancelled) {
        	   return; }}

        String lastOpenedString=lastOpened.save();
        setProperty("lastOpened",lastOpenedString);
        if (currentMapRestorable != null) {
           getFrame().setProperty("onStartIfNotSpecified",currentMapRestorable); }
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
     * Manages the list of MapModules.
     * As this task is very complex, I exported it
     * from Controller to this class to keep Controller
     * simple.
     */
    public class MapModuleManager {
        // Variable below: The instances of mode, ie. the Model/View pairs. Normally, the
        // order should be the order of insertion, but such a Map is not
        // available.
        private Map mapModules = new HashMap();

        private MapModule mapModule; //reference to the current mapmodule, could be done
                                     //with an index to mapModules, too.
        // private String current;
        
        private Controller c;

        MapModuleManager(Controller c) {
           this.c=c; }

        Map getMapModules() {
           return mapModules; }
        
        public MapModule getMapModule() {
           return mapModule; }

        public void newMapModule(MindMap map) {
            MapModule mapModule = new MapModule(map, new MapView(map, c), getMode());
            setMapModule(mapModule);
            addToMapModules(mapModule.toString(), mapModule);
            history.mapChanged(mapModule);
            updateNavigationActions(); }

        public void updateMapModuleName() {
            getMapModules().remove(getMapModule().toString());
            //removeFromViews() doesn't work because MapModuleChanged()
            //must not be called at this state
            getMapModule().rename();
            addToMapModules(getMapModule().toString(),getMapModule());
        }

        void nextMapModule() {
            List keys = new LinkedList(getMapModules().keySet());
            int index = keys.indexOf(getMapModule().toString());
            ListIterator i = keys.listIterator(index+1);
            if (i.hasNext()) {
               changeToMapModule((String)i.next()); }
            else if (keys.iterator().hasNext()) {
               // Change to the first in the list
               changeToMapModule((String)keys.iterator().next()); }}

        void previousMapModule() {
            List keys = new LinkedList(getMapModules().keySet());
            int index = keys.indexOf(getMapModule().toString());
            ListIterator i = keys.listIterator(index);
            if (i.hasPrevious()) {
               changeToMapModule((String)i.previous()); }
            else {
               Iterator last = keys.listIterator(keys.size()-1);
               if (last.hasNext()) {
                  changeToMapModule((String)last.next()); }}}

        //Change MapModules
		/** This is the question whether the map is already opened. If this is the case,
		 * the map is automatically opened + returns true. Otherwise does nothing + returns false.*/
        public boolean tryToChangeToMapModule(String mapModule) {
            if (mapModule != null && getMapModules().containsKey(mapModule)) {
                changeToMapModule(mapModule);
                return true; }
            else {
               return false; }}

    	/** adds the mapModule to the history and calls changeToMapModuleWithoutHistory. */
        void changeToMapModule(String mapModule) {
            MapModule map = (MapModule)(getMapModules().get(mapModule));
            history.mapChanged(map);
            changeToMapModuleWithoutHistory(map); }

        void changeToMapModuleWithoutHistory(MapModule map) {
        	// shut down screens of old view + frame
        	getModeController().setVisible(false);
            if (map.getMode() != getMode()) {
               changeToMode(map.getMode().toString()); 
            }
            // activates the new view + frame
            setMapModule(map);
            mapModuleChanged(); 
			getMode().getModeController().setVisible(true);
        }

        public void changeToMapOfMode(Mode mode) {
            for (Iterator i = getMapModules().keySet().iterator(); i.hasNext(); ) {
                String next = (String)i.next();
                if ( ((MapModule)getMapModules().get(next)).getMode() == mode ) {
                    changeToMapModule(next);
                    return; }}}

        //private

        private void mapModuleChanged() {
//			frame.getFreeMindMenuBar().updateMapsMenu();//to show the new map in the mindmaps menu
//			lastOpened.mapOpened(getMapModule());
//			frame.getFreeMindMenuBar().updateLastOpenedList();//to show the new map in the file menu
			lastOpened.mapOpened(getMapModule());
			frame.getFreeMindMenuBar().updateMenus();//to show the new map in the mindmaps menu
            //  history.add(getMapModule());
            //updateNavigationActions();
            setTitle();
            updateZoomBar();
            c.obtainFocusForSelected(); }
       
        private void setMapModule(MapModule mapModule) {
            this.mapModule = mapModule;
            frame.setView(mapModule != null ? mapModule.getView() : null); }

        private void addToMapModules(String key, MapModule value) {
            // begin bug fix, 20.12.2003, fc.
            // check, if already present:
            String extension = "";
            int count = 1;
            while (mapModules.containsKey(key+extension)) {
                extension = "<"+(++count)+">";
            }
            // rename map:
            value.setName(key+extension);
            mapModules.put(key+extension,value);
            // end bug fix, 20.12.2003, fc.
            setAllActions(true);
            moveToRoot();                // Only for the new modules move to root
            mapModuleChanged(); }

       private void changeToAnotherMap(String toBeClosed) {
          List keys = new LinkedList(getMapModules().keySet());
          for (ListIterator i = keys.listIterator(); i.hasNext();) {
             String key = (String)i.next();
             if (!key.equals(toBeClosed)) {
                changeToMapModule(key);
                return; }}}

        private void updateNavigationActions() {
           List keys = new LinkedList(getMapModules().keySet());
           navigationPreviousMap.setEnabled(keys.size() > 1);
           navigationNextMap.setEnabled(keys.size() > 1); }

        private void updateZoomBar() {
           if (getMapModule()!=null) {
              ((MainToolBar)c.toolbar).setZoomComboBox(getMapModule().getView().getZoom()); }}

        
       /**
        *  Close the currently active map, return false if closing cancelled.
        */
       private boolean close() {
       	    // (DP) The mode controller does not close the map
            boolean closingNotCancelled = getMode().getModeController().close();
            if (!closingNotCancelled) {
               return false; }	
            
            String toBeClosed = getMapModule().toString();
            mapModules.remove(toBeClosed);
            if (mapModules.isEmpty()) {
               setAllActions(false);
               setMapModule(null);
               frame.setView(null); }
            else {
               changeToMapModule((String)mapModules.keySet().iterator().next());
               updateNavigationActions(); }
            mapModuleChanged();
            return true; }

       // }}

    }

    /**
     * Manages the history of visited maps.
     * Maybe explicitly closed maps should be removed from
     * History too?
     */

    // Daniel: To the best of my knowledge, history does not serve any
    // purpose which would not already be covered. I am disabling the
    // history. If you want to enable history and get it working, you have
    // to consider that every map module stored in history knows of the map
    // view. If you close the map, but it is not removed from history,
    // complete view together with all the models and views of nodes and
    // edges will remain in memory and cannot be collected.
    //
    // One of the solution which comes to mind is to store string names in
    // history instead of map modules. Another option is to remove maps from
    // history upon closing the maps.

    private class HistoryManager {
        private LinkedList /* of map modules */ historyList = new LinkedList();
        private int current;

        HistoryManager() {
        }

        void nextMap() {
           if (false) {
           if (current+1 < historyList.size()) {
              getMapModuleManager().changeToMapModuleWithoutHistory((MapModule)historyList.get(++current));
              //the map is immediately added again via changeToMapModule
              historyPreviousMap.setEnabled(true);
              if ( current >= historyList.size()-1)
                 historyNextMap.setEnabled(false);
           }
           }
        }

        void previousMap() {
           if (false) {
           if (current > 0) {
              getMapModuleManager().changeToMapModuleWithoutHistory((MapModule)historyList.get(--current));
              historyNextMap.setEnabled(true);
              if ( current <= 0)
                 historyPreviousMap.setEnabled(false);
           }
           }
        }

        void mapChanged(MapModule map) {
           if (false) {
           while (current < historyList.size()-1) {
              historyList.remove(historyList.size()-1);
           }
           historyList.add(map);
           current = historyList.indexOf(map);
           if (current > 0) {
              historyPreviousMap.setEnabled(true);
           } else {
              historyPreviousMap.setEnabled(false);
           }//closeMap will cause a bug?
           if (current < historyList.size()-1) {
              historyNextMap.setEnabled(true);
           } else {
              historyNextMap.setEnabled(false);
           }
           }
        }
    }

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
    private class CloseAction extends AbstractAction {
        CloseAction(Controller controller) {
            super(controller.getResourceString("close"));
        }
        public void actionPerformed(ActionEvent e) {
            getMapModuleManager().close();
        }
    }

    private class PrintAction extends AbstractAction {
        Controller controller;
        boolean isDlg;
        PrintAction(Controller controller, boolean isDlg) {
            super(controller.getResourceString("print"),
                  new ImageIcon(getResource("images/Print24.gif")));
            this.controller = controller;
            setEnabled(false);
            this.isDlg = isDlg;
        }
        public void actionPerformed(ActionEvent e) {
            if (!acquirePrinterJobAndPageFormat()) {
               return; }

            printerJob.setPrintable(getView(),pageFormat);

            if (!isDlg || printerJob.printDialog()) {
                try {
                    printerJob.print();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
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
            changeToMode("Browse");
//             //      try {
//             String map = getProperty("docmapurl_since_version_0_7_0");
//             if (map.startsWith("."))  {
//                 map = "file:"+System.getProperty("user.dir") + map.substring(1);//remove "." and make url
//             }
//             ((BrowseController)getMode().getModeController()).loadURL(map);
//                 //IMPROVE THIS!
//                 // } catch (FileNotFoundException ex) {
//                 //   JOptionPane.showMessageDialog(getView(), getResourceString("file_not_found") + "\n Documentation Map not found.");
//                 // }
        }
    }

    private class AboutAction extends AbstractAction {
        Controller controller;
        AboutAction(Controller controller) {
            super(controller.getResourceString("about"));
            this.controller = controller;
        }
        public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(getFrame().getViewport(),controller.getResourceString("about_text")+FreeMind.version);
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

    private class HistoryPreviousMapAction extends AbstractAction {
        HistoryPreviousMapAction(Controller controller) {        
            super(controller.getResourceString("previous_map"),
                  new ImageIcon(getResource("images/Back24.gif")));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent event) {
            history.previousMap();
        }
    }

    private class HistoryNextMapAction extends AbstractAction {
        HistoryNextMapAction(Controller controller) {
            super(controller.getResourceString("next_map"),
                  new ImageIcon(getResource("images/Forward24.gif")));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent event) {
            history.nextMap();
        }
    }

    private class NavigationPreviousMapAction extends AbstractAction {
        NavigationPreviousMapAction(Controller controller) {     
            super(controller.getResourceString("previous_map"),
                  new ImageIcon(getResource("images/Back24.gif")));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent event) {
            mapModuleManager.previousMapModule();
        }
    }

    private class NavigationNextMapAction extends AbstractAction {
        NavigationNextMapAction(Controller controller) {
            super(controller.getResourceString("next_map"),
                  new ImageIcon(getResource("images/Forward24.gif")));
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
           ((MainToolBar)toolbar).zoomIn(); }}

    protected class ZoomOutAction extends AbstractAction {
        public ZoomOutAction(Controller controller) {
           super(controller.getResourceString("zoom_out")); }
        public void actionPerformed(ActionEvent e) {
           ((MainToolBar)toolbar).zoomOut(); }}

    //
    // Preferences
    //
    private class BackgroundSwatch extends ColorSwatch {
        Color getColor() {
            return getModel().getBackgroundColor();
        }
    }

    private class BackgroundAction extends AbstractAction {
        BackgroundAction(Controller controller, Icon icon) {
            super(controller.getResourceString("background"),icon);
        }
        public void actionPerformed(ActionEvent e) {
            Color color = showCommonJColorChooserDialog(getView(),"Choose Background Color:",getView().getBackground() );
            getModel().setBackgroundColor(color);
        }
    }

    private class OptionAntialiasAction extends AbstractAction {
       OptionAntialiasAction(Controller controller) {}
       public void actionPerformed(ActionEvent e) {
          if (e.getActionCommand().equals("antialias_none")) {
             setAntialiasEdges(false);
             setAntialiasAll(false); }
          if (e.getActionCommand().equals("antialias_edges")) {
             setAntialiasEdges(true);
             setAntialiasAll(false); }
          if (e.getActionCommand().equals("antialias_all")) {
             setAntialiasEdges(false);
             setAntialiasAll(true); }
          if(getView() != null)
              getView().repaint(); 
       }
    }

    private class OptionHTMLExportFoldingAction extends AbstractAction {
       OptionHTMLExportFoldingAction(Controller controller) {}
       public void actionPerformed(ActionEvent e) {
          setProperty("html_export_folding", e.getActionCommand()); }}

    // switch auto properties for selection mechanism fc, 7.12.2003.
    private class OptionSelectionMechanismAction extends AbstractAction {
        Controller c;
       OptionSelectionMechanismAction(Controller controller) {
           c = controller;
       }
       public void actionPerformed(ActionEvent e) {
          setProperty("selection_method", e.getActionCommand());
          // and update the selection method in the NodeMouseMotionListener
          freemind.controller.NodeMouseMotionListener.updateSelectionMethod(c);
          String statusBarString = c.getResourceString(e.getActionCommand());
          if(statusBarString != null) // should not happen
              c.getFrame().out(statusBarString);
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





}//Class Controller

