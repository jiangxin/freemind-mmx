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
/*$Id: ControllerAdapter.java,v 1.25 2003-11-03 10:15:45 sviles Exp $*/

package freemind.modes;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.controller.Controller;
import freemind.modes.MindMap;
import freemind.modes.Pattern;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ListIterator;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.text.Keymap;
import javax.swing.text.DefaultEditorKit;
import javax.swing.filechooser.FileFilter;


/**
 * Derive from this class to implement the Controller for your mode. Overload the methods
 * you need for your data model, or use the defaults. There are some default Actions you may want
 * to use for easy editing of your model. Take MindMapController as a sample.
 */
public abstract class ControllerAdapter implements ModeController {

    Mode mode;
    private int noOfMaps = 0;//The number of currently open maps
//	private MindMapNode clipboard;
//	private Clipboard clipboard = new Clipboard("principal");
	private Clipboard clipboard;

    public Action copy = null;
    public Action cut = null;
    public Action paste = null;

    public ControllerAdapter() {
    }

    public ControllerAdapter(Mode mode) {
	this.mode = mode;

	cut = new CutAction(this);
	paste = new PasteAction(this);
        copy = new CopyAction(this);

	DropTarget dropTarget = new DropTarget(
					       getFrame().getViewport(),
					       new FileOpener()
						   );

	clipboard = getFrame().getViewport().getToolkit().getSystemSelection();
        // SystemSelection is a strange clipboard used for instance on
        // Linux. To get data into this clipboard user just selects the area
        // without pressing Ctrl+C like on Windows.
        
        if (clipboard == null) {
           clipboard = getFrame().getViewport().getToolkit().getSystemClipboard(); }
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

    public void doubleClick(MouseEvent e) {
	toggleFolded();
    }

    public void plainClick(MouseEvent e) {
       if (getView().getSelected().followLink(e.getX())) {
          /*
          if (getView().getSelected().getModel().getLink() != null
           public boolean onConflictToggleFolded(double xCoord) {
       return xCoord < getSize().width/2; }

           && e.getX() < getView().getSelected().getSize().width/2) {
          */
          loadURL(); }
       else {
          toggleFolded(); }}

    //
    // Map Management
    //

    protected String getText(String textId) {
       return getFrame().getResources().getString(textId); }

    public void newMap() {
	getController().getMapModuleManager().newMapModule(newModel());
	mapOpened(true);
    }

    protected void newMap(MindMap map) {
	getController().getMapModuleManager().newMapModule(map);
	mapOpened(true);
    }

    /**
     * You may decide to overload this or take the default
     * and implement the functionality in your MapModel (implements MindMap)
     */
    public void load(File file) throws FileNotFoundException, IOException, XMLParseException {
	MapAdapter model = newModel();
	model.load(file);
	getController().getMapModuleManager().newMapModule(model);
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


    protected void add(JMenu menu, Action action, String keystroke) { 
       JMenuItem item = menu.add(action);
       item.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty(keystroke))); }

    protected void add(JMenu menu, Action action) {
       menu.add(action); }

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
	    } catch (Exception ex) {
               handleLoadingException (ex); } {
	    }
	}
    }

    public void handleLoadingException (Exception ex) {
       String exceptionType = ex.getClass().getName();
       if (exceptionType.equals("freemind.main.XMLParseException")) {
          int showDetail = JOptionPane.showConfirmDialog
             (getView(), getText("map_corrupted"),"FreeMind",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
          if (showDetail==JOptionPane.YES_OPTION) {
             Tools.errorMessage(ex); }}
       else if (exceptionType.equals("java.io.FileNotFoundException")) {
          Tools.errorMessage(getText("file_not_found"));}
       else {
          Tools.errorMessage(ex); }
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
            // If file exists, ask before overwriting.
            int overWriteMap = JOptionPane.YES_OPTION;
            if (f.exists()) {
               overWriteMap = JOptionPane.showConfirmDialog
                  (getView(), getText("map_already_exists"), "FreeMind", JOptionPane.YES_NO_OPTION ); }
            if (overWriteMap == JOptionPane.YES_OPTION) {
               save(f);
               //Update the name of the map
               getController().getMapModuleManager().updateMapModuleName(); }
	}
    }

    public void close() throws Exception {
	String[] options = {getText("yes"),
                            getText("no"),
                            getText("cancel")};
	if (!getModel().isSaved()) {
	    String text = getText("save_unsaved")+"\n"+getMapModule().toString();
	    String title = getText("save");
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
		if (cut!=null) cut.setEnabled(true);
		if (copy!=null) copy.setEnabled(true);
		if (paste!=null) paste.setEnabled(true);
	    }
	    if (getFrame().getView()!=null) {
		DropTarget dropTarget = new DropTarget(
						       getFrame().getView(),
						       new FileOpener()
							   );
	    }
	    noOfMaps++;
	} else {
	    noOfMaps--;
	    if (noOfMaps == 0) {
		//closed the last map
		setAllActions(false);
		if (cut!=null) cut.setEnabled(false);
		if (copy!=null) copy.setEnabled(false);
		if (paste!=null) paste.setEnabled(false);
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
	    edit(getView().getSelected(),getView().getSelected());
	}
    }

    void editLong(final NodeView node, NodeView toBeSelected) {
	String text = node.getModel().toString();

        final JDialog dialog = new JDialog((JFrame)getFrame(), getText("edit_long_node"), true);

        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true); // wrap around words rather tham characters


        JScrollPane editorScrollPane = new JScrollPane(textArea);
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(500, 160));

        final JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton(getText("cancel"));
        okButton.addActionListener (new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 okButton.setEnabled(false);
                 dialog.dispose(); }});

        cancelButton.addActionListener (new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                 dialog.dispose(); }});

        // On Enter act as if OK button was pressed
        textArea.addKeyListener(new KeyListener() {
              public void keyPressed(KeyEvent e) {
                 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    okButton.setEnabled(false);
                    dialog.dispose(); }}
              public void keyTyped(KeyEvent e) {}
              public void keyReleased(KeyEvent e) {}
           });

        JPanel panel = new JPanel();
        panel.add(editorScrollPane);

        JPanel buttonPane = new JPanel();
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        buttonPane.setMaximumSize(new Dimension(1000, 20));

        panel.add(buttonPane);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);
        //dialog.setLocationRelativeTo(node);
        dialog.setLocation(node.getLocationOnScreen());
        dialog.pack();
        dialog.setVisible(true);

        // We use isEnables property to communicate the fact, that OK button
        // was pressed.
        if (!okButton.isEnabled()) {
           getModel().changeNode(node.getModel(), textArea.getText()); }
    }

    void edit(final NodeView node, NodeView toBeSelected) {
	String text = node.getModel().toString();
        if (text.length() > 100) {
           editLong(node,toBeSelected);
           return; }

	final JTextField input = (text.length() < 8)
           ? new JTextField(text,8) 	//Make fields for short texts editable
           : new JTextField(text);

	FocusListener whenEdited = new FocusAdapter() {
              public void focusLost(FocusEvent e) {
                 getModel().changeNode(node.getModel(),input.getText());
                 getFrame().getLayeredPane().remove(input);
              }
           };
        
	edit(node, toBeSelected,input,whenEdited);
    }

    public void edit( NodeView node,final NodeView toBeSelected, JTextField input, FocusListener whenEdited) {
	getView().scrollNodeToVisible(node);
	//	Keymap keymap = input.addKeymap("My",input.getKeymap());
	//	Action act = input.getActions()[0];//DefaultEditorKit.InsertContentAction;
	//	KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_A,0);
	//	keymap.addActionForKeyStroke(key,act);
	Point position = getAbsoluteNodePosition(node);
	input.setBounds(position.x,position.y + 5,input.getPreferredSize().width,18); // offset 5 is heuristic workaround
        //input.setLocation(node.getLocation());
	input.selectAll();
	input.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    getView().select(toBeSelected);
		    //Focus is lost, so focusLost() is called, which does the work
		}
	    });
	input.addFocusListener( whenEdited );

	getFrame().getLayeredPane().add(input,2000);
	getFrame().repaint();
	input.requestFocus();
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
        // Requires J2SDK1.4.0!
        String inputValue = JOptionPane.showInputDialog
           (getText("edit_link_manually"), getModel().getLink(getSelected()));
        if (inputValue != null) {
           if (inputValue.equals("")) {
              inputValue = null;        // In case of no entry unset link
           }
           getModel().setLink(getSelected(),inputValue);
        }
    }

    protected void setLinkByFileChooser() {
	URL link;
	String relative;
	File input;
	JFileChooser chooser = null;
	if (getMap().getFile() == null) {
            JOptionPane.showMessageDialog(getView(), getText("not_saved_for_link_error"), 
                                          "FreeMind", JOptionPane.WARNING_MESSAGE);
            return;
            // In the previous version Freemind automatically displayed save
            // dialog. It happened very often, that user took this save
            // dialog to be an open link dialog; as a result, the new map
            // overwrote the linked map.

	}
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
                Tools.errorMessage(getText("url_error"));
		return;
	    }
	    if (getFrame().getProperty("links").equals("relative")) {
		//Create relative URL
		try {
		    relative = Tools.toRelativeURL(getMap().getFile().toURL(), link);
		} catch (MalformedURLException ex) {
                    Tools.errorMessage(getText("url_error"));
		    return;
		}
	    }
	    getModel().setLink(getSelected(),relative);
	}
    }

    public void loadURL(String relative) {
	URL absolute = null;
	if (getMap().getFile() == null) {
	    getFrame().out("You must save the current map first!");
	    save(); }
	try {
           if (Tools.isAbsolutePath(relative)) {
              // Protocol can be identified by rexep pattern "[a-zA-Z]://.*".
              // This should distinguish a protocol path from a file path on most platforms.
              // 1)  UNIX / Linux - obviously
              // 2)  Windows - relative path does not contain :, in absolute path is : followed by \.
              // 3)  Mac - cannot remember

              // If relative is an absolute path, then it cannot be a protocol.
              // At least on Unix and Windows. But this is not true for Mac!!

              // Here is hidden an assumption that the existence of protocol implies !Tools.isAbsolutePath(relative).
              // The code should probably be rewritten to convey more logical meaning, on the other hand
              // it works on Windows and Linux.

              //absolute = new URL("file://"+relative); }
              absolute = new File(relative).toURL(); }
            else {
              absolute = new URL(getMap().getFile().toURL(), relative);
              // Remark: getMap().getFile().toURL() returns URLs like file:/C:/...
              // It seems, that it does not cause any problems.
            }

           String extension = Tools.getExtension(absolute.toString());
           if ((extension != null) && extension.equals("mm")) {   // ---- Open Mind Map
              String fileName = absolute.getFile();
              File file = new File(fileName);
              if(!getController().getMapModuleManager().tryToChangeToMapModule(file.getName())) {
                 //this can lead to confusion if the user handles multiple maps with the same name.
                 getFrame().setWaitingCursor(true);
                 load(file); }}
           else {                                                 // ---- Open URL in browser
              if (absolute.getProtocol().equals("file")) {                 
                 File file = new File (Tools.urlGetFile(absolute));
                 // If file does not exist, try http protocol (but only if it is reasonable)
                 if (!file.exists()) {
                    if (relative.matches("^[-\\.a-z0-9]*/?$")) {
                       absolute = new URL("http://"+relative); }
                    else {
                       // This cannot be a base, to which http:// may be added.
                       Tools.errorMessage("File \""+file+"\" does not exist.");
                       return; }}}
              getFrame().openDocument(absolute); }}
        catch (MalformedURLException ex) {
            Tools.errorMessage(getText("url_error")+"\n"+ex);
	    return; }
        catch (FileNotFoundException e) {
	    int returnVal = JOptionPane.showConfirmDialog
               (getView(),
                getText("repair_link_question"),
                getText("repair_link"),
                JOptionPane.YES_NO_OPTION);
	    if (returnVal==JOptionPane.YES_OPTION) {
               setLinkByTextField(); }}
        catch (Exception e) { e.printStackTrace(); }
        getFrame().setWaitingCursor(false);
    }

    public void loadURL() {
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
	return getController().getMapModuleManager().getMapModule();
    }

    public MapAdapter getMap() {
	if (getMapModule() != null) {
	    return (MapAdapter)getMapModule().getModel();
	} else {
	    return null;
	}
    }

    public URL getResource (String name) {
	return getFrame().getResource(name);
    }

    public Controller getController() {
	return getMode().getController();
    }

    public FreeMindMain getFrame() {
	return getController().getFrame();
    }

    private MapAdapter getModel() {
	return (MapAdapter)getController().getModel();
    }

    public MapView getView() {
	return getController().getView();
    }

    protected void updateMapModuleName() {
	getController().getMapModuleManager().updateMapModuleName();
    }

    private NodeAdapter getSelected() {
	return (NodeAdapter)getView().getSelected().getModel();
    }

    public void changeToMapOfMode(Mode mode) {
	getController().getMapModuleManager().changeToMapOfMode(mode);
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
	    super(getText("new"), new ImageIcon(getResource("images/New24.gif")));
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
	    super(getText("open"), new ImageIcon(getResource("images/Open24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.open();
	}
    }

    protected class SaveAction extends AbstractAction {
	ControllerAdapter c;
	public SaveAction(ControllerAdapter controller) {
	    super(getText("save"), new ImageIcon(getResource("images/Save24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.save();
	}
    }

    protected class SaveAsAction extends AbstractAction {
	ControllerAdapter c;
	public SaveAsAction(ControllerAdapter controller) {
	    super(getText("save_as"), new ImageIcon(getResource("images/SaveAs24.gif")));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
	    c.saveAs();
	}
    }

    protected class FindAction extends AbstractAction {
	public FindAction() {
           super(getText("find")); }
	public void actionPerformed(ActionEvent e) {
           String what = JOptionPane.showInputDialog(getText("find_what"));

           if (what != null && !what.equals("")) {
              boolean found = getView().getModel().find
                 (getView().getSelected().getModel(), what, /*caseSensitive=*/ false);
              if (!found) {
                 JOptionPane.showMessageDialog(null, "\""+what+"\" "+getText("not_found")); }}}}

    //
    // Node editing
    //

    protected class EditAction extends AbstractAction {
	public EditAction() {
	    super(getText("edit"));
	}
	public void actionPerformed(ActionEvent e) {
	    edit();
	}
    }

    protected class AddNewAction extends AbstractAction {
	public AddNewAction() {
	    super(getText("new_node"));
	}
	public void actionPerformed(ActionEvent e) {
	    addNew(getView().getSelected());
	}
    }

    protected class RemoveAction extends AbstractAction {
	public RemoveAction() {
	    super(getText("remove_node"));
	}
	public void actionPerformed(ActionEvent e) {
//	    delete(getView().getSelected());
		for(ListIterator i = getView().getSelecteds().listIterator();i.hasNext();) {
			NodeView selected = (NodeView)i.next();
			delete(selected);
		}
	}
    }

    protected class NodeUpAction extends AbstractAction {
	public NodeUpAction() {
	    super(getText("node_up"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNode selected = getView().getSelected().getModel();
	    if(!selected.isRoot()) {
		MindMapNode parent = selected.getParentNode();
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
	    super(getText("node_down"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNode selected = getView().getSelected().getModel();
	    if(!selected.isRoot()) {
		MindMapNode parent = selected.getParentNode();
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
            super(getText("toggle_folded"));
	}
	public void actionPerformed(ActionEvent e) {
	    toggleFolded();
	}
    }

    protected class ToggleChildrenFoldedAction extends AbstractAction {
	public ToggleChildrenFoldedAction() {
	    super(getText("toggle_children_folded"));
	}
	public void actionPerformed(ActionEvent e) {
	    toggleChildrenFolded();
	}
    }

    protected class SetLinkByFileChooserAction extends AbstractAction {
	public SetLinkByFileChooserAction() {
	    super(getText("set_link_by_filechooser"));
	}
	public void actionPerformed(ActionEvent e) {
	    setLinkByFileChooser();
	}
    }

    protected class SetLinkByTextFieldAction extends AbstractAction {
	public SetLinkByTextFieldAction() {
	    super(getText("set_link_by_textfield"));
	}
	public void actionPerformed(ActionEvent e) {
	    setLinkByTextField();
	}
    }


    protected class FollowLinkAction extends AbstractAction {
	public FollowLinkAction() {
	    super(getText("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }

    protected class CopyAction extends AbstractAction {
	public CopyAction(Object controller) {
	    super(getText("copy"), new ImageIcon(getResource("images/Copy24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
           if(getMapModule() != null) {
              Transferable copy = getView().getModel().copy();
              if (copy != null) {
                 clipboard.setContents(copy,null); }}}}

    protected class CutAction extends AbstractAction {
	public CutAction(Object controller) {
	    super(getText("cut"), new ImageIcon(getResource("images/Cut24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
           if(getMapModule() != null) {
              Transferable copy = getView().getModel().cut();
              if (copy != null) {
                 clipboard.setContents(copy,null); }}}}

    protected class PasteAction extends AbstractAction {
	public PasteAction(Object controller) {
	    super(getText("paste"),new ImageIcon(getResource("images/Paste24.gif")));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    if(clipboard != null) {
               getModel().paste(clipboard.getContents(this), getView().getSelected().getModel()); }}}

    protected class FileOpener implements DropTargetListener {
	private boolean isDragAcceptable(DropTargetDragEvent event) {
	    // check if there is at least one File Type in the list
	    DataFlavor[] flavors = event.getCurrentDataFlavors();
	    for (int i = 0; i < flavors.length; i++) {
		if (flavors[i].isFlavorJavaFileListType()) {
		    //		    event.acceptDrag(DnDConstants.ACTION_COPY);
		    return true;
		}
	    }
	    //	    event.rejectDrag();
	    return false;
	}

	private boolean isDropAcceptable(DropTargetDropEvent event) {
	    // check if there is at least one File Type in the list
	    DataFlavor[] flavors = event.getCurrentDataFlavors();
	    for (int i = 0; i < flavors.length; i++) {
		if (flavors[i].isFlavorJavaFileListType()) {
		    return true;
		}
	    }
	    return false;
	}

	public void drop (DropTargetDropEvent dtde) {
	    if(!isDropAcceptable(dtde)) {
		dtde.rejectDrop();
		return;
	    }
	    dtde.acceptDrop(DnDConstants.ACTION_COPY);
	    try {
		Object data =
		    dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		if (data == null) {
		    // Shouldn't happen because dragEnter() rejects drags w/out at least
		    // one javaFileListFlavor. But just in case it does ...
		    dtde.dropComplete(false);
		    return;
		}
		Iterator iterator = ((List)data).iterator();
		while (iterator.hasNext()) {
		    File file = (File)iterator.next();
		    load(file);
		}
	    }
	    catch (Exception e) {
		JOptionPane.showMessageDialog(getView(),
					      "Couldn't open dropped file(s). Reason: " + e.getMessage()
					      //getText("file_not_found")
					      );
		dtde.dropComplete(false);
		return;
	    }
	    dtde.dropComplete(true);
	}

	public void dragEnter (DropTargetDragEvent dtde) {
	    if(!isDragAcceptable(dtde)) {
		dtde.rejectDrag();
		return;
	    }
	}

	public void dragOver (DropTargetDragEvent e) {}
	public void dragExit (DropTargetEvent e) {}
	public void dragScroll (DropTargetDragEvent e) {}
	public void dropActionChanged (DropTargetDragEvent e) {}
    }

}
