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

package freemind.controller;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import freemind.modes.ModesCreator;
import freemind.modes.Mode;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
// import freemind.modes.mindmapmode.NodeModel;
// import freemind.modes.mindmapmode.MapModel;
// import freemind.modes.mindmapmode.MindMapMode;//testing
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.awt.Component;
import java.awt.Color;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.print.PrinterJob;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;

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
    private JMenu modemenu = new JMenu("Mode");
    private ModesCreator modescreator = new ModesCreator(this);

    Action close = new CloseAction();
    Action print = new PrintAction();
    public Action quit = new QuitAction(this);
    Action background = new BackgroundAction();
    Action about = new AboutAction();
    Action lastMap = new LastMapAction(this);
    Action nextMap = new NextMapAction(this);
    Action cut = new CutAction(this);
    Action paste = new PasteAction(this);

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

	changeToMode("MindMap");
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

    void changeToMode(String mode) {
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
	popupmenu = getMode().getPopupMenu();
	getModeMenu().removeAll();
	getMode().activate(getModeMenu());
	getFrame().setTitle("FreeMind - "+mode+" Mode");
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

    JMenu getModeMenu(){
	return modemenu;
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

    void moveToRoot() {
	getView().moveToRoot();
    }

    void select( NodeView node ) {
	getView().select(node);
    }

    void centerNode() {
	getView().centerNode(getView().getSelected());
    }

    //
    //  Node Editing
    //

    void addNew(NodeView parent) {
	getMode().getModeController().addNew(parent);
    }

    void delete(NodeView node) {
	if (!node.isRoot()) {
	    getModel().removeNodeFromParent(node.getModel());
	}
    }

    void edit() {
	if (getView().getSelected() != null) {
	    edit(getView().getSelected());
	}
    }

    void edit(final NodeView node) {
	getMode().getModeController().edit(node,node);
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
	getView().init();
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
	cut.setEnabled(enabled);
	close.setEnabled(enabled);
    }

    private void updateNavigationActions() {
	List keys = new LinkedList(getMapModules().keySet());
	if (getMapModule() == null) {
	    return;
	}
	int index = keys.indexOf(getMapModule().toString());
	ListIterator i = keys.listIterator(index);
	if (i.hasPrevious()) {
	    lastMap.setEnabled(true);
	} else {
	    lastMap.setEnabled(false);
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

    private class AboutAction extends AbstractAction {
	AboutAction() {
	    super(FreeMind.getResources().getString("about"));
	}
	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(getView(),FreeMind.getResources().getString("about_text")+FreeMind.version);
	}
    }

    //
    // Map navigation
    //

    private class LastMapAction extends AbstractAction {
	LastMapAction(Controller controller) {
	    super("Last Map", new ImageIcon(controller.getClass().getResource("/images/Back24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    previousMap();
	}
    }

    private class NextMapAction extends AbstractAction {
	NextMapAction(Controller controller) {
	    super("Next Map", new ImageIcon(controller.getClass().getResource("/images/Forward24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent event) {
	    nextMap();
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

    //
    // Node editing
    //

    private class CutAction extends AbstractAction {
	CutAction(Object controller) {
	    super(FreeMind.getResources().getString("cut"), new ImageIcon(controller.getClass().getResource("/images/Cut24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNode node = getView().getSelected().getModel();
	    if (node.isRoot()) return;
	    paste.setEnabled(true);
	    getModel().cut(node);
	}
    }

    private class PasteAction extends AbstractAction {
	PasteAction(Object controller) {
	    super(FreeMind.getResources().getString("paste"),new ImageIcon(controller.getClass().getResource("/images/Paste24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    setEnabled(false);
	    getModel().paste(getView().getSelected().getModel());
	}
    }
}//Class Controller
