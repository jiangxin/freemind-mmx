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
/*$Id: ControllerAdapter.java,v 1.14 2001-03-24 22:45:45 ponder Exp $*/

package freemind.modes;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.controller.Controller;
import freemind.modes.MindMap;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ListIterator;
import java.awt.Point;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.JFileChooser;
import javax.swing.text.Keymap;
import javax.swing.text.DefaultEditorKit;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.filechooser.FileFilter;

/**
 * Derive from this class to implement the Controller for your mode. Overload the methods
 * you need for your data model, or use the defaults. There are some default Actions you may want
 * to use for easy editing of your model. Take MindMapController as a sample.
 */
public abstract class ControllerAdapter implements ModeController {

    Mode mode;
    private int noOfMaps = 0;//The number of currently open maps
    private MindMapNode clipboard;

    public Action cut;
    public Action paste;

    public ControllerAdapter() {
    }

    public ControllerAdapter(Mode mode) {
	this.mode = mode;

	cut = new CutAction(this);
	paste = new PasteAction(this);

    }

    //
    // Methods that should be overloaded
    //

    protected abstract MindMapNode newNode();

    /**
     * You _must_ implement this if you use one of the following actions:
     * OpenAction, NewMapAction.
     */
    public MapAdapter newModel() {
	throw new java.lang.UnsupportedOperationException();
    }

    /**
     * You may want to implement this...
     * It returns the FileFilter that is used by the open() and save()
     * JFileChoosers.
     */
    protected FileFilter getFileFilter() {
	return null;
    }

    public void doubleClick() {
	toggleFolded();
    }

    //
    // Map Management
    //

    public void newMap() {
	getController().newMapModule(newModel());
	mapOpened(true);
    }

    /**
     * You may decide to overload this or take the default
     * and implement the functionality in your MapModel (implements MindMap)
     */
    public void load(File file) throws FileNotFoundException {
	MapAdapter model = newModel();
	model.load(file);
	getController().newMapModule(model);
	mapOpened(true);
    }

    public void save() {
	if (getModel().isSaved()) return;
	if (getModel().getFile()==null) {
	    saveAs();
	} else {
	    save(getModel().getFile());
	}
    }

    /**
     * See load()
     */
    public void save(File file) {
	getModel().save(file);
    }


    //
    // Dialogs with user
    //

    public void open() {
	JFileChooser chooser = null;
	if ((getMap() != null) && (getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
	    chooser = new JFileChooser(getMap().getFile().getParentFile());
	} else {
	    chooser = new JFileChooser();
	}
	//chooser.setLocale(currentLocale);
	if (getFileFilter() != null) {
	    chooser.addChoosableFileFilter(getFileFilter());
	}
	int returnVal = chooser.showOpenDialog(getView());
	if (returnVal==JFileChooser.APPROVE_OPTION) {
	    try {
		load(chooser.getSelectedFile());
	    } catch (FileNotFoundException ex) {
		JOptionPane.showMessageDialog(getView(), getFrame().getResources().getString("file_not_found"));
	    }
	}
    }

    public void saveAs() {
	JFileChooser chooser = null;
	if ((getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
	    chooser = new JFileChooser(getMap().getFile().getParentFile());
	} else {
	    chooser = new JFileChooser();
	}
	//chooser.setLocale(currentLocale);
	if (getFileFilter() != null) {
	    chooser.addChoosableFileFilter(getFileFilter());
	}
	int returnVal = chooser.showSaveDialog(getView());
	if (returnVal==JFileChooser.APPROVE_OPTION) {//ok pressed
	    File f = chooser.getSelectedFile();
	    //Force the extension to be .mm
	    String ext = Tools.getExtension(f.getName());
	    if(!ext.equals("mm")) {
		f = new File(f.getParent(),f.getName()+".mm");
	    }
	    save(f);
	    //Update the name of the map
	    getController().updateMapModuleName();
	}
    }

    public void close() throws Exception {
	String[] options = {getFrame().getResources().getString("yes"),getFrame().getResources().getString("no"),getFrame().getResources().getString("cancel")};
	if (!getModel().isSaved()) {
	    String text = getFrame().getResources().getString("save_unsaved")+"\n"+getMapModule().toString();
	    String title = getFrame().getResources().getString("save");
	    int returnVal = JOptionPane.showOptionDialog( getView(),text,title,JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
	    if (returnVal==JOptionPane.YES_OPTION) {
		save();
	    } else if (returnVal==JOptionPane.NO_OPTION) {
	    } else if (returnVal==JOptionPane.CANCEL_OPTION) {
		throw new Exception();
		//do this because quit() must terminate (and _not_ quit the prog)
	    }
	}
	mapOpened(false);
    }

    /**
     * Call this method if you have opened a map for this mode with true,
     * and if you have closed a map of this mode with false. It updates the Actions
     * that are dependent on whether there is a map or not.
     * --> What to do if either newMap or load or close are overwritten by a concrete
     * implementation? uups.
     */
    public void mapOpened(boolean open) {
	if (open) {
	    if (noOfMaps == 0) {
		//opened the first map
		setAllActions(true);
		cut.setEnabled(true);
	    }
	    noOfMaps++;
	} else {
	    noOfMaps--;
	    if (noOfMaps == 0) {
		//closed the last map
		setAllActions(false);
		cut.setEnabled(false);
	    }
	}
    }

    /**
     * Overwrite this to set all of your actions which are
     * dependent on whether there is a map or not.
     */
    protected void setAllActions(boolean enabled) {
    }

    /**
     * Returns the number of maps currently opened for this mode.
     */
    public int getNoOfMaps() {
	return noOfMaps;
    }


    //
    // Node editing
    //

//     void addNew(NodeView parent) {
// 	getMode().getModeController().addNew(parent);
//     }

    void delete(NodeView node) {
	getMode().getModeController().remove(node);
    }

    void edit() {
	if (getView().getSelected() != null) {
	    edit(getView().getSelected(), getView().getSelected());
	}
    }

//     void edit(final NodeView node) {
// 	getMode().getModeController().edit(node,node);
//     }

    public void addNew(NodeView parent) {
	MindMapNode newNode = newNode();
	int place;
	if (getFrame().getProperty("placenewbranches").equals("last")) {
	    place = parent.getModel().getChildCount();
	} else {
	    place = 0;
	}
	getModel().insertNodeInto(newNode,parent.getModel(), place);
	edit(newNode.getViewer(),parent);
    }

    public void remove(NodeView node) {
	if (!node.isRoot()) {
	    getModel().removeNodeFromParent(node.getModel());
	}
    }

    public void edit(final NodeView node,final NodeView toBeSelected) {
	getView().scrollNodeToVisible(node);
	final JTextField input = new JTextField(node.getModel().toString());
	//	Keymap keymap = input.addKeymap("My",input.getKeymap());
	//	Action act = input.getActions()[0];//DefaultEditorKit.InsertContentAction;
	//	KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_A,0);
	//	keymap.addActionForKeyStroke(key,act);

	Point position = getAbsoluteNodePosition(node);
	input.setBounds(position.x,position.y,input.getPreferredSize().width,15);
	input.selectAll();
	input.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getView().select(toBeSelected);
		    //Focus is lost, so focusLost() is called, which does the work
		}
	    });
	input.addFocusListener(new FocusAdapter() {
		public void focusLost(FocusEvent e) {
  		    getModel().changeNode(node.getModel(),input.getText());
  		    getFrame().getLayeredPane().remove(input);
		}
	    });
		
	getFrame().getLayeredPane().add(input,2000);
	getFrame().repaint();
	input.requestFocus();
    }

    protected void toggleFolded() {
	MindMapNode node = getSelected();
	if (node.isFolded()) {
	    getModel().setFolded(node,false);
	} else {
	    getModel().setFolded(node,true);
	}
	getView().select(node.getViewer());
    }

    /**
     * If any children are folded, unfold all folded children.
     * Otherwise, fold all children.
     */
    protected void toggleChildrenFolded() {
	// have NodeAdapter; need NodeView
	MindMapNode parent = getSelected();
	ListIterator children_it = parent.getViewer().getChildrenViews().listIterator();
	boolean areAnyFolded = false;
	while(children_it.hasNext() && !areAnyFolded) {
	    NodeView child = (NodeView)children_it.next();
	    if(child.getModel().isFolded()) {
		areAnyFolded = true;
	    }
	}

	children_it = parent.getViewer().getChildrenViews().listIterator();
	if(areAnyFolded) {
	    while(children_it.hasNext()) {
		NodeView child = (NodeView)children_it.next();
		if(child.getModel().isFolded()) {
		    getModel().setFolded(child.getModel(), false);
		}
	    }
	} else {
	    while(children_it.hasNext()) {
		NodeView child = (NodeView)children_it.next();
		getModel().setFolded(child.getModel(), true);
	    }
	}
	getView().select(parent.getViewer());
    }

    protected void setLinkByTextField() {
	String old = getModel().getLink(getSelected());
	if (old != null && old != "") {
	    getFrame().out("Old link is: "+old);
	}
	String link = JOptionPane.showInputDialog("Link:");
	if (link != null) {
	    getModel().setLink(getSelected(),link);
	    getFrame().out("Set Link to: "+link);
	}
    }

    protected void setLinkByFileChooser() {
	URL link;
	String relative;
	File input;
	JFileChooser chooser = null;
	if ((getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
	    chooser = new JFileChooser(getMap().getFile().getParentFile());
	} else {
	    chooser = new JFileChooser();
	}
	if (getFileFilter() != null) {
	    chooser.addChoosableFileFilter(getFileFilter());
	}
	int returnVal = chooser.showOpenDialog(getView());
	if (returnVal==JFileChooser.APPROVE_OPTION) {
	    input = chooser.getSelectedFile();
	    try {
		link = input.toURL();
		relative = link.toString();
	    } catch (MalformedURLException ex) {
		JOptionPane.showMessageDialog(getView(), getFrame().getResources().getString("url_error"));
		return;
	    }
	    if (getFrame().getProperty("links").equals("relative")) {
		//Create relative URL
		try {
		    relative = Tools.toRelativeURL(getMap().getFile().toURL(), link);
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(), getFrame().getResources().getString("url_error"));
		    return;
		}
	    }
	    getModel().setLink(getSelected(),relative);
	}
    }

    protected void loadURL(String relative) {
	URL absolute = null;
	try {
	    absolute = new URL(getMap().getFile().toURL(), relative);
	} catch (MalformedURLException ex) {
	    JOptionPane.showMessageDialog(getView(), getFrame().getResources().getString("url_error"));
	    return;
	}
	try {
	    String fileName = absolute.getFile();
	    File file = new File(fileName);
	    if(!getController().tryToChangeToMapModule(file.getName())) {//this can lead to confusion if the user handles multiple maps with the same name.
		load(file);
	    }
	} catch (FileNotFoundException e) {
	    int returnVal = JOptionPane.showConfirmDialog(getView(), getFrame().getResources().getString("repair_link_question"), getFrame().getResources().getString("repair_link"),JOptionPane.YES_NO_OPTION);
	    if (returnVal==JOptionPane.YES_OPTION) {
		setLinkByTextField();
	    } 
	}
    }

    protected void loadURL() {
	String link = getSelected().getLink();
	if (link != null) {
	    loadURL(link);
	}
    }

    public void applyPattern(NodeAdapter node, Pattern pattern) {
	if (pattern.getText() != null) {
	    node.setUserObject(pattern.getText());
	}
	node.setColor(pattern.getNodeColor());
	node.setStyle(pattern.getNodeStyle());
	node.setFont(pattern.getNodeFont());
	
	EdgeAdapter edge = (EdgeAdapter)node.getEdge();
	edge.setColor(pattern.getEdgeColor());
	edge.setStyle(pattern.getEdgeStyle());
    }
	

    //
    // Convenience methods
    //

    protected Mode getMode() {
	return mode;
    }

    protected void setMode(Mode mode) {
	this.mode = mode;
    }

    protected MapModule getMapModule() {
	return getController().getMapModule();
    }

    protected MapAdapter getMap() {
	if (getMapModule() != null) {
	    return (MapAdapter)getMapModule().getModel();
	} else {
	    return null;
	}
    }

    protected URL getResource (String name) {
	return getFrame().getResource(name);
    }

    protected Controller getController() {
	return getMode().getController();
    }

    public FreeMindMain getFrame() {
	return getController().getFrame();
    }

    private MapAdapter getModel() {
	return (MapAdapter)getController().getModel();
    }

    protected MapView getView() {
	return getController().getView();
    }

    private NodeAdapter getSelected() {
	return (NodeAdapter)getView().getSelected().getModel();
    }

    /**
     * Calculates the absolute position of a node on the layered pane.
     * Used in edit() to place a Textfield right above the node.
     * useful if you overload edit() to use something else than a
     * JTextfield (eg. two JTextfields for key/value)
     */
    protected Point getAbsoluteNodePosition(NodeView node) {
	Point loc = node.getLocation();
	
	Point scroll = ((JViewport)getView().getParent()).getViewPosition();
	Point content = getFrame().getContentPane().getLocation();
	Point position = new Point();

	position.x = loc.x-scroll.x+content.x;
	//Todo: Remove the constant(40) (the menubar?) from this calc
	position.y = loc.y-scroll.y+content.y+40;

	return position;
    }

    ////////////
    //  Actions
    ///////////

    protected class NewMapAction extends AbstractAction {
	ControllerAdapter c;
	public NewMapAction(ControllerAdapter controller) {
	    super(getFrame().getResources().getString("new"), new ImageIcon(getResource("images/New24.gif")));
	    c = controller;
	    //Workaround to get the images loaded in jar file.
	    //they have to be added to jar manually with full path from root
	    //I really don't like this, but it's a bug of java
	}
	public void actionPerformed(ActionEvent e) {
	    c.newMap();
	}
    }

    protected class OpenAction extends AbstractAction {
	ControllerAdapter c;
	public OpenAction(ControllerAdapter controller) {
	    super(getFrame().getResources().getString("open"), new ImageIcon(getResource("images/Open24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.open();
	}
    }

    protected class SaveAction extends AbstractAction {
	ControllerAdapter c;
	public SaveAction(ControllerAdapter controller) {
	    super(getFrame().getResources().getString("save"), new ImageIcon(getResource("images/Save24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.save();
	}
    }	    

    protected class SaveAsAction extends AbstractAction {
	ControllerAdapter c;
	public SaveAsAction(ControllerAdapter controller) {
	    super(getFrame().getResources().getString("save_as"), new ImageIcon(getResource("images/SaveAs24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.saveAs();
	}
    }

    //
    // Node editing
    //

    protected class EditAction extends AbstractAction {
	public EditAction() {
	    super(getFrame().getResources().getString("edit"));
	}
	public void actionPerformed(ActionEvent e) {
	    edit();
	}
    }

    protected class AddNewAction extends AbstractAction {
	public AddNewAction() {
	    super(getFrame().getResources().getString("new_node"));
	}
	public void actionPerformed(ActionEvent e) {
	    addNew(getView().getSelected());
	}
    }

    protected class RemoveAction extends AbstractAction {
	public RemoveAction() {
	    super(getFrame().getResources().getString("remove_node"));
	}
	public void actionPerformed(ActionEvent e) {
	    delete(getView().getSelected());
	}
    }

    protected class NodeUpAction extends AbstractAction {
	public NodeUpAction() {
	    super(getFrame().getResources().getString("node_up"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNode selected = getView().getSelected().getModel();
	    if(!selected.isRoot()) {
		MindMapNode parent = (MindMapNode)selected.getParent();
		int index = getModel().getIndexOfChild(parent, selected);
		delete(getView().getSelected());
		int maxindex = getModel().getChildCount(parent);
		if(index - 1 <0) { 
		    getModel().insertNodeInto(selected,parent,maxindex);
		} else {
		    getModel().insertNodeInto(selected,parent,index - 1);
		}
		getModel().nodeStructureChanged(parent);
		getView().select(selected.getViewer());
	    }
	}
    }

    protected class NodeDownAction extends AbstractAction {
	public NodeDownAction() {
	    super(getFrame().getResources().getString("node_down"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNode selected = getView().getSelected().getModel();
	    if(!selected.isRoot()) {
		MindMapNode parent = (MindMapNode)selected.getParent();
		int index = getModel().getIndexOfChild(parent, selected);
		delete(getView().getSelected());
		int maxindex = getModel().getChildCount(parent);
		if(index + 1 > maxindex) { 
		    getModel().insertNodeInto(selected,parent,0);
		} else {
		    getModel().insertNodeInto(selected,parent,index + 1);
		}
		getModel().nodeStructureChanged(parent);
		getView().select(selected.getViewer());
	    }
	}
    }

    protected class ToggleFoldedAction extends AbstractAction {
	public ToggleFoldedAction() {
	    super(getFrame().getResources().getString("toggle_folded"));
	}
	public void actionPerformed(ActionEvent e) {
	    toggleFolded();
	}
    }

    protected class toggleChildrenFoldedAction extends AbstractAction {
	public toggleChildrenFoldedAction() {
	    super(getFrame().getResources().getString("toggle_children_folded"));
	}
	public void actionPerformed(ActionEvent e) {
	    toggleChildrenFolded();
	}
    }

    protected class SetLinkByFileChooserAction extends AbstractAction {
	public SetLinkByFileChooserAction() {
	    super(getFrame().getResources().getString("set_link_by_filechooser"));
	}
	public void actionPerformed(ActionEvent e) {
	    setLinkByFileChooser();
	}
    }

    protected class SetLinkByTextFieldAction extends AbstractAction {
	public SetLinkByTextFieldAction() {
	    super(getFrame().getResources().getString("set_link_by_textfield"));
	}
	public void actionPerformed(ActionEvent e) {
	    setLinkByTextField();
	}
    }


    protected class FollowLinkAction extends AbstractAction {
	public FollowLinkAction() {
	    super(getFrame().getResources().getString("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }

    protected class CutAction extends AbstractAction {
	public CutAction(Object controller) {
	    super(getFrame().getResources().getString("cut"), new ImageIcon(getResource("images/Cut24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    if(getController().getMapModule() != null) {
		MindMapNode node = getView().getSelected().getModel();
		if (node.isRoot()) return;
		paste.setEnabled(true);
		clipboard = getModel().cut(node);
	    }
	}
    }

    protected class PasteAction extends AbstractAction {
	public PasteAction(Object controller) {
	    super(getFrame().getResources().getString("paste"),new ImageIcon(getResource("images/Paste24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    setEnabled(false);
	    if(clipboard != null) {
		getModel().paste(clipboard, getView().getSelected().getModel());
	    }
	}
    }

}
