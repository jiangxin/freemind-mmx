/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000  Joerg Mueller <joergmueller@bigfoot.com>
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
/*$Id: Controller.java,v 1.7 2000-10-23 21:38:17 ponder Exp $*/

package freemind.controller;

import freemind.main.FreeMind;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import freemind.modes.ModesCreator;
import freemind.modes.Mode;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.awt.Component;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.print.PrinterJob;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
//Documentation
import java.io.File;
import freemind.modes.ControllerAdapter;

/**
 * Provides the methods to edit/change a Node.
 * Forwards all messages to MapModel(editing) or MapView(navigation).
 */
public class Controller {

    private Map mapmodules = new TreeMap(); //The instances of mode, ie. the Model/View pairs
    private MapModule mapmodule; //reference to the current mode, could be done with an index to mapmodules, too.
    private Map modes; //hash of all possible modes
    private Mode mode; //The current mode
    private FreeMind frame;
    private JToolBar toolbar;
    private JPopupMenu popupmenu;
    private NodeMouseListener nodeMouseListener;
    private NodeKeyListener nodeKeyListener;
    private ModesCreator modescreator = new ModesCreator(this);

    Action close = new CloseAction();
    Action print = new PrintAction();
    public Action quit = new QuitAction(this);
    Action background = new BackgroundAction();
    Action about = new AboutAction();
    Action documentation = new DocumentationAction();
    Action license = new LicenseAction();
    Action previousMap = new PreviousMapAction(this);
    Action nextMap = new NextMapAction(this);

    Action moveToRoot = new MoveToRootAction(this);

    //
    // Constructors
    //

    public Controller(FreeMind frame) {
	this.frame = frame;
	modes = modescreator.getAllModes();

  	nodeMouseListener = new NodeMouseListener(this);
	nodeKeyListener = new NodeKeyListener(this);
	setAllActions(false);

	//Create the ToolBar
	toolbar = new MainToolBar(this);
	getFrame().getContentPane().add( toolbar, BorderLayout.NORTH );
    }

    //
    // get/set methods
    //

    public FreeMind getFrame() {
	return frame;
    }

    /**Returns the current model*/
    public MindMap getModel() {
	return getMapModule().getModel();
    }

    public MapView getView() {
	if (getMapModule() != null) {
	    return getMapModule().getView();
	}
	return null;
    }

    Map getMapModules() {
	return mapmodules;
    }

    public MapModule getMapModule() {
	return mapmodule;
    }

    private void setMapModule(MapModule mapmodule) {
	this.mapmodule = mapmodule;
	if (mapmodule != null) {
	    frame.setView(mapmodule.getView());
	} else {
	    frame.setView(null);
	}
    }

    Map getModes() {
	return modes;
    }

    Mode getMode() {
	return mode;
    }

    public void changeToMode(String mode) {
	if (mode.equals(getMode())) {
	    return;
	}
	if (getMode() != null) {
	    if (getMode().getModeToolBar() != null) {
		toolbar.remove(getMode().getModeToolBar());
	    }
	}
	if (getMapModule() != null) {
	    setMapModule(null);
	    mapModuleChanged();
	}
	this.mode = (Mode)modes.get(mode);
		
	if (getMode().getModeToolBar() != null) {
	    toolbar.add(getMode().getModeToolBar());
	    getMode().getModeToolBar().repaint();
	}
	toolbar.validate();
	toolbar.repaint();
	
	popupmenu = getMode().getPopupMenu();

	getFrame().setTitle("FreeMind - "+mode+" Mode");
	getMode().activate();

	getFrame().getFreeMindMenuBar().updateFileMenu();
	getFrame().getFreeMindMenuBar().updateEditMenu();

	if (getMapModule() == null) {
	    setAllActions(false);
	}
    }


    public NodeKeyListener getNodeKeyListener() {
	return nodeKeyListener;
    }

    public NodeMouseListener getNodeMouseListener() {
	return nodeMouseListener;
    }

    public void setFrame(FreeMind frame) {
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

    void select( NodeView node ) {
	getView().select(node);
    }

    void centerNode() {
	getView().centerNode(getView().getSelected());
    }

    void toggleFolded() {
	if (getSelected().isFolded()) {
	    getModel().setFolded(getSelected(),false);
	} else {
	    getModel().setFolded(getSelected(),true);
	}
    }

    private MindMapNode getSelected() {
	return getView().getSelected().getModel();
    }

    //
    // Map Navigation
    //

    void changeToMapModule(String mapmodule) {
	MapModule map =  (MapModule)(getMapModules().get(mapmodule));
	if (map.getMode() != getMode()) {
	    changeToMode(map.getMode().toString());
	}
	setMapModule(map);
	mapModuleChanged();
    }

    void nextMap() {
	List keys = new LinkedList(getMapModules().keySet());
	int index = keys.indexOf(getMapModule().toString());
	ListIterator i = keys.listIterator(index+1);
	if (i.hasNext()) {
	    changeToMapModule((String)i.next());
	}
	updateNavigationActions();
    }

    void previousMap() {
	List keys = new LinkedList(getMapModules().keySet());
	int index = keys.indexOf(getMapModule().toString());
	ListIterator i = keys.listIterator(index);
	if (i.hasPrevious()) {
	    changeToMapModule((String)i.previous());
	}
	updateNavigationActions();
    }

    public void newMapModule(MindMap map) {
	MapModule mapmodule = new MapModule(map, new MapView(map, this), getMode());
	setMapModule(mapmodule);
	addToMapModules(mapmodule.toString(), mapmodule);
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

    public void updateMapModuleName() {
	getMapModules().remove(getMapModule().toString());//removeFromViews() doesn't work because MapModuleChanged()
	//must not be called at this state
	getMapModule().rename();
	addToMapModules(getMapModule().toString(),getMapModule());
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
    
    private void mapModuleChanged() {
	frame.updateMenuBar();//to show the new map in the mindmaps menu
	updateNavigationActions();
	if (getMapModule() == null) {
	    getFrame().setTitle("FreeMind - " + getMode().toString()+" Mode");
	} else {
	    getFrame().setTitle("FreeMind - " + getMode().toString()+" Mode" + " - " + getMapModule().toString());
	}
	moveToRoot();
	//??	getView().repaint();
    }

    private void addToMapModules(String key, MapModule value) {
	mapmodules.put(key,value);
	mapModuleChanged();
	setAllActions(true);
    }

    private void removeFromMapModules(String key) {
	mapmodules.remove(key);
	mapModuleChanged();
	if(getMapModules().isEmpty()) {
	    setAllActions(false);
	}
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
	print.setEnabled(enabled);
	close.setEnabled(enabled);
	moveToRoot.setEnabled(enabled);
    }

    private void updateNavigationActions() {
	List keys = new LinkedList(getMapModules().keySet());
	if (getMapModule() == null) {
	    return;
	}
	int index = keys.indexOf(getMapModule().toString());
	ListIterator i = keys.listIterator(index);
	if (i.hasPrevious()) {
	    previousMap.setEnabled(true);
	} else {
	    previousMap.setEnabled(false);
	}
	if (i.hasNext()) {
	    i.next();
	    if (i.hasNext()) {
		nextMap.setEnabled(true);
	    } else {
		nextMap.setEnabled(false);
	    }
	}
    }

    //
    // program/map control
    //

    private void quit() {
	while (getView() != null) {
	    try {
		close();
	    } catch (Exception ex) {
		return;
	    }
	}
	System.exit(0);
    }

    private void close() throws Exception {
	getMode().getModeController().close();
	String toBeClosed = getMapModule().toString();
	changeToAnotherMap(toBeClosed);
	removeFromMapModules(toBeClosed);
    }

    private void changeToAnotherMap(String toBeClosed) {
	List keys = new LinkedList(getMapModules().keySet());
	int index = keys.indexOf(getMapModule().toString());
	for (ListIterator i = keys.listIterator(); i.hasNext();) {
	    String key = (String)i.next();
	    if (!key.equals(toBeClosed)) {
		changeToMapModule(key);
		return;
	    }
	}
	//What to do if no other maps are available?
	setMapModule(null);
    }

    //////////////
    // Inner Classes
    ////////////
    

    //
    // program/map control
    //

    private class QuitAction extends AbstractAction {
	QuitAction(Controller controller) {
	    super(FreeMind.getResources().getString("quit"));
	}
	public void actionPerformed(ActionEvent e) {
	    quit();
	}
    }

    /**This closes only the current map*/
    private class CloseAction extends AbstractAction {
	CloseAction() {
	    super(FreeMind.getResources().getString("close"));
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		close();
	    } catch (Exception ex) {
		return;
	    }
	}
    }

    private class PrintAction extends AbstractAction {
	PrintAction() {
	    super(FreeMind.getResources().getString("print"));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    PrinterJob printJob = PrinterJob.getPrinterJob();
	    printJob.setPrintable(getView());
	    if (printJob.printDialog()) {
		try {
		    printJob.print();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	    }
	}
    }

    //
    // Help
    //

    private class DocumentationAction extends AbstractAction {
	DocumentationAction() {
	    super(FreeMind.getResources().getString("documentation"));
	}
	public void actionPerformed(ActionEvent e) {
	    changeToMode("MindMap");
	    ((ControllerAdapter)getMode().getModeController()).load(new File("freemind.mm"));
	}
    }

    private class AboutAction extends AbstractAction {
	AboutAction() {
	    super(FreeMind.getResources().getString("about"));
	}
	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(getView(),FreeMind.getResources().getString("about_text")+FreeMind.version);
	}
    }

    private class LicenseAction extends AbstractAction {
	LicenseAction() {
	    super(FreeMind.getResources().getString("license"));
	}
	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(getView(),FreeMind.getResources().getString("license_text"));
	}
    }


    //
    // Map navigation
    //

    private class PreviousMapAction extends AbstractAction {
	PreviousMapAction(Controller controller) {	 
	    super(FreeMind.getResources().getString("previous_map"), new ImageIcon(controller.getClass().getResource("/images/Back24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    previousMap();
	}
    }

    private class NextMapAction extends AbstractAction {
	NextMapAction(Controller controller) {
	    super(FreeMind.getResources().getString("next_map"), new ImageIcon(controller.getClass().getResource("/images/Forward24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    nextMap();
	}
    }

    //
    // Node navigation
    //
    
    private class MoveToRootAction extends AbstractAction {
	MoveToRootAction(Controller controller) {
	    super(FreeMind.getResources().getString("move_to_root"));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    moveToRoot();
	}
    }

    //
    // Preferences
    //

    private class BackgroundAction extends AbstractAction {
	BackgroundAction() {
	    super(FreeMind.getResources().getString("background"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = JColorChooser.showDialog(getView(),"Choose Background Color:",getView().getBackground() );
	    getModel().setBackgroundColor(color);
	}
    }
}//Class Controller
