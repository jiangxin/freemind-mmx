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
/*$Id: Controller.java,v 1.28 2003-11-03 10:15:44 sviles Exp $*/

package freemind.controller;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import freemind.modes.ModesCreator;
import freemind.modes.Mode;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.browsemode.BrowseController;//this isn't good
import java.util.*;
import java.text.MessageFormat;
import java.net.URL;
import java.awt.Component;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.print.PrinterJob;
import java.awt.print.PageFormat;
import java.awt.event.ActionEvent;
import javax.swing.*;
//Documentation
import java.io.File;
import java.io.FileNotFoundException;
import freemind.modes.ControllerAdapter;

/**
 * Provides the methods to edit/change a Node.
 * Forwards all messages to MapModel(editing) or MapView(navigation).
 */
public class Controller {

    private LastOpenedList lastOpened;//A list of the pathnames of all the maps that were opened in the last time
    private MapModuleManager mapModuleManager;// new MapModuleManager();
    private HistoryManager history = new HistoryManager();
    private Map modes; //hash of all possible modes
    private Mode mode; //The current mode
    private FreeMindMain frame;
    private JToolBar toolbar;
    private JPopupMenu popupmenu;
    private NodeMouseMotionListener nodeMouseMotionListener;
    private NodeKeyListener nodeKeyListener;
    private NodeDragListener nodeDragListener;
    private NodeDropListener nodeDropListener;
    private MapMouseMotionListener mapMouseMotionListener;
    private MapMouseWheelListener mapMouseWheelListener;
    private ModesCreator modescreator = new ModesCreator(this);
    private PageFormat pageFormat;
    boolean isPrintingAllowed=true;
    private Icon bswatch = new BackgroundSwatch();//needed for BackgroundAction
 

    Action close; 
    Action print; 
    Action printDirect; 
    Action page; 
    public Action quit;
    Action background; 
    Action about;
    Action documentation;
    Action license;
    Action historyPreviousMap;
    Action historyNextMap;
    Action navigationPreviousMap;
    Action navigationNextMap;

    Action moveToRoot;

    //
    // Constructors
    //

    public Controller(FreeMindMain frame) {
	this.frame = frame;
	modes = modescreator.getAllModes();
	mapModuleManager=new MapModuleManager(this);
	lastOpened=new LastOpenedList(this, getFrame().getProperty("lastOpened"));

  	nodeMouseMotionListener = new NodeMouseMotionListener(this);
	nodeKeyListener = new NodeKeyListener(this);
	nodeDragListener = new NodeDragListener(this);
	nodeDropListener = new NodeDropListener(this);

  	mapMouseMotionListener = new MapMouseMotionListener(this);
  	mapMouseWheelListener = new MapMouseWheelListener(this);

	try {
	    pageFormat = PrinterJob.getPrinterJob().defaultPage();
	} catch (SecurityException ex) {
	    isPrintingAllowed=false;
	}

	close = new CloseAction(this);

	print = new PrintAction(this,true);
	printDirect = new PrintAction(this,false);
	page = new PageAction(this);
	quit = new QuitAction(this);
	background = new BackgroundAction(this,bswatch);
	about = new AboutAction(this);
	documentation = new DocumentationAction(this);
	license = new LicenseAction(this);
	historyPreviousMap = new HistoryPreviousMapAction(this);
	historyNextMap = new HistoryNextMapAction(this);
	navigationPreviousMap = new NavigationPreviousMapAction(this);
	navigationNextMap = new NavigationNextMapAction(this);
	
	moveToRoot = new MoveToRootAction(this);

	//Create the ToolBar
	toolbar = new MainToolBar(this);
	getFrame().getContentPane().add( toolbar, BorderLayout.NORTH );

	setAllActions(false);
    }

    //
    // get/set methods
    //

    public FreeMindMain getFrame() {
	return frame;
    }

    public URL getResource(String resource) {
	return getFrame().getResource(resource);
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
	}
	return null;
    }

    Map getModes() {
	return modes;
    }

    public Mode getMode() {
	return mode;
    }

    public MapModuleManager getMapModuleManager() {
	return mapModuleManager;
    }

    public LastOpenedList getLastOpenedList() {
	return lastOpened;
    }

    private MapModule getMapModule() {
	return getMapModuleManager().getMapModule();
    }

    private JToolBar getToolBar() {
	return toolbar;
    }

    public boolean changeToMode(String mode) {
	if (getMode() != null && mode.equals(getMode().toString())) {
	    return true;
	}

	//Check if the mode is available
	Mode newmode = (Mode)modes.get(mode);
	if (newmode == null) {
            Tools.errorMessage(getFrame().getResources().getString("mode_na")+": "+mode);
	    return false;
	}

	if (getMode() != null && getMode().getModeToolBar() != null) {
            toolbar.remove(getMode().getModeToolBar());
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
	toolbar.validate();
	toolbar.repaint();
	
	popupmenu = getMode().getPopupMenu();

	setTitle();
	getMode().activate();

	getFrame().getFreeMindMenuBar().updateFileMenu();
	getFrame().getFreeMindMenuBar().updateEditMenu();

	if (getMapModule() == null) {
	    setAllActions(false);
	}

	Object[] messageArguments = {
         getMode().toString()
	};
	MessageFormat formatter = new MessageFormat(
		getFrame().getResources().getString("mode_status"));
	getFrame().out(formatter.format(messageArguments));
	
	return true;
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

    //
    // Node Navigation
    //

    void moveUp() {
	getView().moveUp();
    }

    void moveDown() {
	getView().moveDown();
    }

    void moveLeft() {
	getView().moveLeft();
    }

    void moveRight() {
	getView().moveRight();
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

    void select( NodeView node, boolean extend ) {
	getView().select(node,extend);
    }

    void selectBranch( NodeView node, boolean extend ) {
	getView().selectBranch(node,extend);
    }
	
    boolean isSelected( NodeView node ) {
	return getView().isSelected(node);
    }

    void centerNode() {
	getView().centerNode(getView().getSelected());
    }

    private MindMapNode getSelected() {
	return getView().getSelected().getModel();
    }

    //
    // Map Navigation
    //

    //
    // other
    //

    void showPopupMenu(Component c, int x, int y) {
	if (popupmenu != null) {
	    popupmenu.show(c,x,y);
	}
    }

    void setZoom(float zoom) {
	getView().setZoom(zoom);
    }


    //////////////
    // Private methods. Internal implementation
    ////////////


    //
    // Node editing
    //

    private void getFocus() {
	getView().getSelected().requestFocus();
    }

    //
    // Multiple Views management
    //
	
	/** return the Frame title with mode and file if exist */
    private void setTitle() {
	Object[] messageArguments = {
	    getMode().toString()
	};
	MessageFormat formatter = new MessageFormat(
		getFrame().getResources().getString("mode_title"));
	String title = formatter.format(messageArguments);
	if (getMapModule() != null) {
	    title = title + " - " + getMapModule().toString();
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
        // Restoreable is not an English word, while Restorable is. We will leave the rest of the code as it is anyway,
        String currentMapRestorable = (getModel()!=null) ? getModel().getRestoreable() : null;
	while (getView() != null) {
	    try {
		getMapModuleManager().close();
	    } catch (Exception ex) {
		//		System.out.println("Error: "+ex);
		return;
	    }
	}

	String lastOpenedString=lastOpened.save();
	getFrame().setProperty("lastOpened",lastOpenedString);
        if (currentMapRestorable != null) {
           getFrame().setProperty("onStartIfNotSpecified",currentMapRestorable); }
	getFrame().saveProperties();
	//save to properties
	System.exit(0);
    }

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
	private Map mapmodules = new HashMap(); //The instances of mode, ie. the Model/View pairs. Normally, the order should
	//be the order of insertion, but such a Map is not available...
	private MapModule mapmodule; //reference to the current mapmodule, could be done with an index to mapmodules, too.
	//	private String current;
	
	private Controller c;

	MapModuleManager(Controller c) {
	    this.c=c;
	}

	Map getMapModules() {
	    return mapmodules;
	}
	
	public MapModule getMapModule() {
	    return mapmodule;
	}

	public void newMapModule(MindMap map) {
	    MapModule mapmodule = new MapModule(map, new MapView(map, c), getMode());
	    setMapModule(mapmodule);
	    addToMapModules(mapmodule.toString(), mapmodule);
	    navigationNextMap.setEnabled(false);
	    history.mapChanged(mapmodule);
	}

	public void updateMapModuleName() {
	    getMapModules().remove(getMapModule().toString());//removeFromViews() doesn't work because MapModuleChanged()
	    //must not be called at this state
	    getMapModule().rename();
	    addToMapModules(getMapModule().toString(),getMapModule());
	}

	void nextMapModule() {
	    List keys = new LinkedList(getMapModules().keySet());
	    int index = keys.indexOf(getMapModule().toString());
	    ListIterator i = keys.listIterator(index+1);
	    if (i.hasNext()) {
		changeToMapModule((String)i.next());
	    }
	}

	void previousMapModule() {
	    List keys = new LinkedList(getMapModules().keySet());
	    int index = keys.indexOf(getMapModule().toString());
	    ListIterator i = keys.listIterator(index);
	    if (i.hasPrevious()) {
		changeToMapModule((String)i.previous());
	    }
	}

	//Change MapModules

	public boolean tryToChangeToMapModule(String mapmodule) {
	    if (mapmodule != null && getMapModules().containsKey(mapmodule)) {
		changeToMapModule(mapmodule);
		return true;
	    } else {
		return false;
	    }
	}
    
	void changeToMapModule(String mapmodule) {
	    MapModule map = (MapModule)(getMapModules().get(mapmodule));
	    history.mapChanged(map);
	    changeToMapModuleWithoutHistory(map);
	}

	void changeToMapModuleWithoutHistory(MapModule map) {
	    if (map.getMode() != getMode()) {
		changeToMode(map.getMode().toString());
	    }
	    setMapModule(map);
	    mapModuleChanged();
	}

	public void changeToMapOfMode(Mode mode) {
	    for (Iterator i = getMapModules().keySet().iterator(); i.hasNext(); ) {
		String next = (String)i.next();
		if ( ((MapModule)getMapModules().get(next)).getMode() == mode ) {
		    changeToMapModule(next);
		    return;
		}
	    }
	}

	//private

	private void changeToAnotherMap(String toBeClosed) {
	    if(!(getMapModules().size() > 1)) {
		setMapModule(null);
		return;
	    }
	    List keys = new LinkedList(getMapModules().keySet());
	    //	    int index = keys.indexOf(getMapModule().toString());
	    for (ListIterator i = keys.listIterator(); i.hasNext();) {
		String key = (String)i.next();
		if (!key.equals(toBeClosed)) {
		    changeToMapModule(key);
		    return;
		}
	    }
	}

	private void mapModuleChanged() {
	    frame.getFreeMindMenuBar().updateMapsMenu();//to show the new map in the mindmaps menu
	    lastOpened.mapOpened(getMapModule());
	    frame.getFreeMindMenuBar().updateLastOpenedList();//to show the new map in the file menu
	    //	history.add(getMapModule());
	    updateNavigationActions();
	    setTitle();
	    moveToRoot();
	}

	private void setMapModule(MapModule mapmodule) {
	    this.mapmodule = mapmodule;
	    if (mapmodule != null) {
		frame.setView(mapmodule.getView());
	    } else {
		frame.setView(null);
	    }
	}

	private void addToMapModules(String key, MapModule value) {
	    mapmodules.put(key,value);
	    setAllActions(true);
	    mapModuleChanged();
	}
	
	private void removeFromMapModules(String key) {
	    mapmodules.remove(key);
	    mapModuleChanged();
	    if(getMapModules().isEmpty()) {
		setAllActions(false);
	    }
	}

	private void close() throws Exception {
	    getMode().getModeController().close();//exception is thrown here if user cancels operation
	    String toBeClosed = getMapModule().toString();
	    changeToAnotherMap(toBeClosed);
	    removeFromMapModules(toBeClosed);
	    updateNavigationActions();//is this needed here?
	}

	private void updateNavigationActions() {
	    List keys = new LinkedList(getMapModules().keySet());
	    if (getMapModule() == null) {
		return;
	    }
	    int index = keys.indexOf(getMapModule().toString());
	    ListIterator i = keys.listIterator(index);
	    if (i.hasPrevious()) {
		navigationPreviousMap.setEnabled(true);
	    } else {
		navigationPreviousMap.setEnabled(false);
	    }
	    if (i.hasNext()) {
		i.next();
		if (i.hasNext()) {
		    navigationNextMap.setEnabled(true);
		} else {
		    navigationNextMap.setEnabled(false);
		}
	    }
	}
    }

    /**
     * Manages the history of visited maps.
     * Maybe explicitly closed maps should be removed from
     * History too?
     */
    private class HistoryManager {
	private LinkedList historyList = new LinkedList();;
	private int current;

	HistoryManager() {
	}

	void nextMap() {
	    if (current+1 < historyList.size()) {
		getMapModuleManager().changeToMapModuleWithoutHistory((MapModule)historyList.get(++current));
		//the map is immediately added again via changeToMapModule
		historyPreviousMap.setEnabled(true);
		if ( current >= historyList.size()-1)
		    historyNextMap.setEnabled(false);
	    }
	}

	void previousMap() {
	    if (current > 0) {
		getMapModuleManager().changeToMapModuleWithoutHistory((MapModule)historyList.get(--current));
		historyNextMap.setEnabled(true);
		if ( current <= 0)
		    historyPreviousMap.setEnabled(false);
	    }
	}

	void mapChanged(MapModule map) {
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

    //
    // program/map control
    //

    private class QuitAction extends AbstractAction {
	QuitAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("quit"));
	}
	public void actionPerformed(ActionEvent e) {
	    quit();
	}
    }

    /**This closes only the current map*/
    private class CloseAction extends AbstractAction {
	CloseAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("close"));
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		getMapModuleManager().close();
	    } catch (Exception ex) {
		return;
	    }
	}
    }

    private class PrintAction extends AbstractAction {
	Controller controller;
	boolean isDlg;
	PrintAction(Controller controller, boolean isDlg) {
	    super(controller.getFrame().getResources().getString("print"), new ImageIcon(getResource("images/Print24.gif")));
	    this.controller = controller;
	    setEnabled(false);
		this.isDlg = isDlg;
	}
	public void actionPerformed(ActionEvent e) {
	    PrinterJob printJob = PrinterJob.getPrinterJob();

	    printJob.setPrintable(getView(),pageFormat);

	    if (!isDlg || printJob.printDialog()) {
		try {
		    printJob.print();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	    }
	}
    }

    private class PageAction extends AbstractAction {
	Controller controller;
	PageAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("page"));
	    this.controller = controller;
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    PrinterJob printJob = PrinterJob.getPrinterJob();
		// Ask user for page format (e.g., portrait/landscape)
		pageFormat = printJob.pageDialog(pageFormat);
	}
    }

    //
    // Help
    //

    private class DocumentationAction extends AbstractAction {
	Controller controller;
	DocumentationAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("documentation"));
	    this.controller = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    changeToMode("Browse");
	    //	    try {
	    String map = getFrame().getProperty("docmapurl");  //(new File("doc/maps/freemind.mm"));
	    if (map.startsWith("."))  {
		map = "file:"+System.getProperty("user.dir") + map.substring(1);//remove "." and make url
	    }
	    ((BrowseController)getMode().getModeController()).loadURL(map);
		//IMPROVE THIS!
		//	    } catch (FileNotFoundException ex) {
		//		JOptionPane.showMessageDialog(getView(), getFrame().getResources().getString("file_not_found") + "\n Documentation Map not found.");
		//	    }
	}
    }

    private class AboutAction extends AbstractAction {
	Controller controller;
	AboutAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("about"));
	    this.controller = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(getView(),controller.getFrame().getResources().getString("about_text")+FreeMind.version);
	}
    }

    private class LicenseAction extends AbstractAction {
	Controller controller;
	LicenseAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("license"));
	    this.controller = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(getView(),controller.getFrame().getResources().getString("license_text"));
	}
    }


    //
    // Map navigation
    //

    private class HistoryPreviousMapAction extends AbstractAction {
	HistoryPreviousMapAction(Controller controller) {	 
	    super(controller.getFrame().getResources().getString("previous_map"), new ImageIcon(getResource("images/Back24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    history.previousMap();
	}
    }

    private class HistoryNextMapAction extends AbstractAction {
	HistoryNextMapAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("next_map"), new ImageIcon(getResource("images/Forward24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    history.nextMap();
	}
    }

    private class NavigationPreviousMapAction extends AbstractAction {
	NavigationPreviousMapAction(Controller controller) {	 
	    super(controller.getFrame().getResources().getString("previous_map"), new ImageIcon(getResource("images/Back24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    mapModuleManager.previousMapModule();
	}
    }

    private class NavigationNextMapAction extends AbstractAction {
	NavigationNextMapAction(Controller controller) {
	    super(controller.getFrame().getResources().getString("next_map"), new ImageIcon(getResource("images/Forward24.gif")));
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
	    super(controller.getFrame().getResources().getString("move_to_root"));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    moveToRoot();
	}
    }

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
	    super(controller.getFrame().getResources().getString("background"),icon);
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = JColorChooser.showDialog(getView(),"Choose Background Color:",getView().getBackground() );
	    getModel().setBackgroundColor(color);
	}
    }
}//Class Controller

