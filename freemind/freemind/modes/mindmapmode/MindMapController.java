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
/*$Id: MindMapController.java,v 1.21 2003-11-03 10:15:46 sviles Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.Pattern;
import freemind.view.mindmapview.NodeView;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JColorChooser;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileFilter;

public class MindMapController extends ControllerAdapter {

    //    Mode mode;
    private JPopupMenu popupmenu;
    private JToolBar toolbar;

    Action newMap = new NewMapAction(this);
    Action open = new OpenAction(this);
    Action save = new SaveAction(this);
    Action saveAs = new SaveAsAction(this);
    Action exportToHTML = new ExportToHTMLAction(this);
    Action exportBranchToHTML = new ExportBranchToHTMLAction(this);

    Action edit = new EditAction();
    Action addNew = new AddNewAction();
    Action remove = new RemoveAction();
    Action toggleFolded = new ToggleFoldedAction();
    Action toggleChildrenFolded = new ToggleChildrenFoldedAction();
    Action setLinkByFileChooser = new SetLinkByFileChooserAction();
    Action setLinkByTextField = new SetLinkByTextFieldAction();
    Action followLink = new FollowLinkAction();
    Action exportBranch = new ExportBranchAction();
    Action importBranch = new ImportBranchAction();
    Action importLinkedBranch = new ImportLinkedBranchAction();
    Action importLinkedBranchWithoutRoot = new ImportLinkedBranchWithoutRootAction();
    Action importExplorerFavorites = new ImportExplorerFavoritesAction();
    Action importFolderStructure = new ImportFolderStructureAction();
    Action joinNodes = new JoinNodesAction();
    Action nodeUp = new NodeUpAction();
    Action nodeDown = new NodeDownAction();
    Action find = new FindAction();

    Action fork = new ForkAction();
    Action bubble = new BubbleAction();
    Action nodeColor = new NodeColorAction();
    Action edgeColor = new EdgeColorAction();
    Action edgeWidths[] = {
       new EdgeWidthAction(EdgeAdapter.WIDTH_PARENT),
       new EdgeWidthAction(EdgeAdapter.WIDTH_THIN),
       new EdgeWidthAction(1),
       new EdgeWidthAction(2),
       new EdgeWidthAction(4),
       new EdgeWidthAction(8)
    };
    Action edgeStyles[] = {
       new EdgeStyleAction("linear"),
       new EdgeStyleAction("bezier"),
       new EdgeStyleAction("sharp_linear"),
       new EdgeStyleAction("sharp_bezier")
    };
    Action italic = new ItalicAction(this);
    Action bold = new BoldAction(this);
    //    Action underline = new UnderlineAction(this);
    Action normalFont = new NormalFontAction(this);

    Action increaseNodeFont = new IncreaseNodeFontAction();
    Action decreaseNodeFont = new DecreaseNodeFontAction();

    Action increaseBranchFont = new IncreaseBranchFontAction();
    Action decreaseBranchFont = new DecreaseBranchFontAction();

    // Extension Actions
    Action patterns[] = new Action[0]; // Make sure it is initialized

    // Branch Font Actions
    Action boldifyBranch = new BoldifyBranchAction();
    Action nonBoldifyBranch = new NonBoldifyBranchAction();
    Action toggleBoldBranch = new ToggleBoldBranchAction();

    Action italiciseBranch = new ItaliciseBranchAction();
    Action nonItaliciseBranch = new NonItaliciseBranchAction();
    Action toggleItalicBranch = new ToggleItalicBranchAction();


    FileFilter filefilter = new MindMapFilter();

    public MindMapController(Mode mode) {
	super(mode);
	//what to do if no patterns file is available?
	try {
           //loadPatterns(new File(Tools.expandFileName(getFrame().getProperty("patternsfile"))));
           File patternsFile = getFrame().getPatternsFile();
           if (patternsFile.exists()) {
              loadPatterns(patternsFile); }
           else {
              System.err.println("Patterns file "+patternsFile+" not found."); }
	} catch (Exception ex) {
	    System.err.println("Patterns not loaded.");
	}
	popupmenu = new MindMapPopupMenu(this);
	toolbar = new MindMapToolBar(this);
	setAllActions(false);
    }

    public MapAdapter newModel() {
	return new MindMapMapModel(getFrame());
    }

    public void save(File file) {
	getModel().save(file);
    }

    private void loadPatterns(File file) throws Exception {
	List patternsList = Pattern.loadPatterns(file);
	//	Action[] patternsActions = Array.newInstance(Action.class,patternsList.length());
	patterns = new Action[patternsList.size()];
	for (int i=0;i<patterns.length;i++) {
	    patterns[i] = new ApplyPatternAction((Pattern)patternsList.get(i));
	}
    }

    public FileFilter getFileFilter() {
	return filefilter;
    }

    //Node editing
    void setFontSize(int fontSize) {
	for(ListIterator e = getSelecteds().listIterator();e.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)e.next();
		getModel().setFontSize(selected,fontSize);
	}
    }

    void setFont(String font) {
	for(ListIterator e = getSelecteds().listIterator();e.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)e.next();
		getModel().setFont(selected,font);
	}
    }

    protected MindMapNode newNode() {
	return new MindMapNodeModel(getText("new_node"),getFrame());
    }

    //get/set methods

    JMenu getEditMenu() {
	JMenu editMenu = new JMenu();
	editMenu.add(getNodeMenu());
	editMenu.add(getBranchMenu());
	editMenu.add(getEdgeMenu());
	editMenu.add(getExtensionMenu());

        editMenu.addSeparator();

 	add(editMenu, cut, "keystroke_cut");
        add(editMenu, copy, "keystroke_copy");
	add(editMenu, paste, "keystroke_paste");
	return editMenu;
    }

    JMenu getFileMenu() {
	JMenu fileMenu = new JMenu();
	add(fileMenu, newMap, "keystroke_newMap");
	add(fileMenu, open, "keystroke_open");
	add(fileMenu, save, "keystroke_save");
	add(fileMenu, saveAs, "keystroke_saveAs");
        fileMenu.addSeparator();
        add(fileMenu, exportToHTML, "keystroke_export_to_html");
	return fileMenu;
    }

    JMenu getExtensionMenu() {
	JMenu extensionMenu = new JMenu(getText("extension_menu"));
	for (int i=0; i<patterns.length; ++i) {          
		extensionMenu.add(patterns[i]);
	}
	return extensionMenu;
    }

    JMenu getBranchMenu() {
	JMenu branchMenu = new JMenu(getText("branch"));

	add(branchMenu, exportBranch, "keystroke_export_branch");
        add(branchMenu, exportBranchToHTML, "keystroke_export_branch_to_html");
	JMenu importMenu = new JMenu(getText("import"));
	branchMenu.add(importMenu);

	add(importMenu, importBranch);
	add(importMenu, importLinkedBranch);
	add(importMenu, importLinkedBranchWithoutRoot);

        importMenu.addSeparator();

        add(importMenu, importExplorerFavorites);
        add(importMenu, importFolderStructure);
        
	branchMenu.addSeparator();

	add(branchMenu, boldifyBranch);
	add(branchMenu, nonBoldifyBranch);
	add(branchMenu, toggleBoldBranch);

	branchMenu.addSeparator();

	add(branchMenu, italiciseBranch);
	add(branchMenu, nonItaliciseBranch);
	add(branchMenu, toggleItalicBranch);

	branchMenu.addSeparator();

	add(branchMenu, increaseBranchFont, "keystroke_branch_increase_font_size");
	add(branchMenu, decreaseBranchFont, "keystroke_branch_decrease_font_size");

	return branchMenu;
    }


    JMenu getNodeMenu() {
	JMenu nodeMenu = new JMenu(getText("node"));
	add(nodeMenu, edit, "keystroke_edit");
 	add(nodeMenu, addNew, "keystroke_add");
 	add(nodeMenu, remove, "keystroke_remove");
        add(nodeMenu, joinNodes, "keystroke_join_nodes");

	nodeMenu.addSeparator();

        add(nodeMenu, find, "keystroke_find");

	nodeMenu.addSeparator();

 	add(nodeMenu, nodeUp, "keystroke_node_up");
 	add(nodeMenu, nodeDown, "keystroke_node_down");

	nodeMenu.addSeparator();

	add(nodeMenu, followLink, "keystroke_follow_link");
	add(nodeMenu, setLinkByFileChooser, "keystroke_set_link_by_filechooser");
	add(nodeMenu, setLinkByTextField, "keystroke_set_link_by_textfield");

	nodeMenu.addSeparator();

	add(nodeMenu, toggleFolded, "keystroke_toggle_folded");
	add(nodeMenu, toggleChildrenFolded, "keystroke_toggle_children_folded");

	nodeMenu.addSeparator();

	JMenu nodeStyle = new JMenu(getText("style"));
	nodeMenu.add(nodeStyle);

	add(nodeStyle, fork);
	add(nodeStyle, bubble);

	JMenu nodeFont = new JMenu(getText("font"));
	add(nodeFont, increaseNodeFont, "keystroke_node_increase_font_size");
	add(nodeFont, decreaseNodeFont, "keystroke_node_decrease_font_size");

	nodeFont.addSeparator();

	add(nodeFont,italic,"keystroke_node_toggle_italic"); 
	add(nodeFont,bold,"keystroke_node_toggle_boldface");
	nodeMenu.add(nodeFont);
	//	nodeFont.add(underline);
	add(nodeMenu,nodeColor,"keystroke_node_color"); 

	return nodeMenu;
    }

    JMenu getEdgeMenu() {
	JMenu edgeMenu = new JMenu(getText("edge"));
	JMenu edgeStyle = new JMenu(getText("style"));
	edgeMenu.add(edgeStyle);
	for (int i=0; i<edgeStyles.length; ++i) { 
		edgeStyle.add(edgeStyles[i]);
	}
	edgeMenu.add(edgeColor);
	JMenu edgeWidth = new JMenu(getText("width"));
	edgeMenu.add(edgeWidth);
	for (int i=0; i<edgeWidths.length; ++i) { 
		edgeWidth.add(edgeWidths[i]);
	}
	return edgeMenu;
    }


    JPopupMenu getPopupMenu() {
	return popupmenu;
    }

    //convenience methods
    private MindMapMapModel getModel() {
	return (MindMapMapModel)getController().getModel();
    }

    private MindMapNodeModel getSelected() {
	return (MindMapNodeModel)getView().getSelected().getModel();
    }

    private LinkedList getSelecteds() {
	LinkedList selecteds = new LinkedList();
	ListIterator it = getView().getSelecteds().listIterator();
	if (it != null) {
	    while(it.hasNext()) {
		NodeView selected = (NodeView)it.next();
		selecteds.add( selected.getModel() );
	    }
	}
	return selecteds;
    }

    MindMapToolBar getToolBar() {
	return (MindMapToolBar)toolbar;
    }

    /**
     * Enabled/Disabled all actions that are dependent on
     * whether there is a map open or not.
     */
    protected void setAllActions(boolean enabled) {
	edit.setEnabled(enabled);
	addNew.setEnabled(enabled);
	remove.setEnabled(enabled);
	toggleFolded.setEnabled(enabled);
	toggleChildrenFolded.setEnabled(enabled);
	setLinkByTextField.setEnabled(enabled);
	setLinkByFileChooser.setEnabled(enabled);
	followLink.setEnabled(enabled);
	italic.setEnabled(enabled);
	bold.setEnabled(enabled);
	normalFont.setEnabled(enabled);
	nodeColor.setEnabled(enabled);
	edgeColor.setEnabled(enabled);
	for (int i=0; i<edgeWidths.length; ++i) { 
		edgeWidths[i].setEnabled(enabled);
	}
	fork.setEnabled(enabled);
	bubble.setEnabled(enabled);
	for (int i=0; i<edgeStyles.length; ++i) { 
		edgeStyles[i].setEnabled(enabled);
	}
	for (int i=0; i<patterns.length; ++i) { 
		patterns[i].setEnabled(enabled);
	}
	save.setEnabled(enabled);
	saveAs.setEnabled(enabled);
	getToolBar().setAllActions(enabled);
	exportBranch.setEnabled(enabled);
	importBranch.setEnabled(enabled);
	importLinkedBranch.setEnabled(enabled);
	importLinkedBranchWithoutRoot.setEnabled(enabled);
    }

    //////////
    // Actions
    /////////

    // This may later be moved to ControllerAdapter. So far there is no reason for it.
    protected class ExportToHTMLAction extends AbstractAction {
	MindMapController c;
	public ExportToHTMLAction(MindMapController controller) {
	    super(getText("export_to_html"));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
           File file = new File(c.getModel().getFile()+".html");
           if (c.getModel().saveHTML((MindMapNodeModel)c.getModel().getRoot(),file)) {
              loadURL(file.toString()); }
	}
    }
    protected class ExportBranchToHTMLAction extends AbstractAction {
	MindMapController c;
	public ExportBranchToHTMLAction(MindMapController controller) {
	    super(getText("export_branch_to_html"));
	    c = controller;
	}
	public void actionPerformed(ActionEvent e) {
           try {
              File file = File.createTempFile("tmm", ".html");
              if (c.getModel().saveHTML((MindMapNodeModel)getSelected(),file)) {
                 loadURL(file.toString()); }}
           catch (IOException ex) {}
	}
    }

    private class ExportBranchAction extends AbstractAction {
	ExportBranchAction() {
	    super(getText("export_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel node = (MindMapNodeModel)getSelected();

	    //if something is wrong, abort.
	    if(getMap() == null || node == null || node.isRoot()) {
		getFrame().err("Could not export branch.");
		return;
	    }
	    //If the current map is not saved yet, save it first.
	    if (getMap().getFile() == null) {
		getFrame().out("You must save the current map first!");
		save();
	    }

	    //Open FileChooser to choose in which file the exported
	    //branch should be stored
	    JFileChooser chooser;
	    if (getMap().getFile().getParentFile() != null) {
		chooser = new JFileChooser(getMap().getFile().getParentFile());
	    } else {
		chooser = new JFileChooser();
	    }
	    //chooser.setLocale(currentLocale);
	    if (getFileFilter() != null) {
		chooser.addChoosableFileFilter(getFileFilter());
	    }
	    int returnVal = chooser.showSaveDialog(getView());
	    if (returnVal==JFileChooser.APPROVE_OPTION) {
		File f = chooser.getSelectedFile();
		URL link;
		//Force the extension to be .mm
		String ext = Tools.getExtension(f.getName());
		if(!ext.equals("mm")) {
		    f = new File(f.getParent(),f.getName()+".mm");
		}
		try {
		    link = f.toURL();
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(),"couldn't create valid URL!");
		    return;
		}

		//Now make a copy from the node, remove the node from the map and create a new
		//Map with the node as root, store the new Map, add the copy of the node to the parent,
		//and set a link from the copy to the new Map.

		MindMapNodeModel parent = (MindMapNodeModel)node.getParentNode();
		MindMapNodeModel newNode = new MindMapNodeModel(node.toString(),getFrame());
		getModel().removeNodeFromParent(node);
		node.setParent(null);
		MindMapMapModel map = new MindMapMapModel(node,getFrame());
		if (getModel().getFile() != null) {
		    try{
			//set a link from the new root to the old map
			map.setLink(node, Tools.toRelativeURL(f.toURL(), getModel().getFile().toURL()));
		    } catch(MalformedURLException ex) {
		    }
		}
		//		getController().newMapModule(map);
		map.save(f);

		getModel().insertNodeInto(newNode, parent, 0);
		try {
		    String linkString = Tools.toRelativeURL(getModel().getFile().toURL(), f.toURL());
		    getModel().setLink(newNode,linkString);
		} catch (MalformedURLException ex) {
		}
		getModel().save(getModel().getFile());
	    }
	}
    }

    private class ImportBranchAction extends AbstractAction {
	ImportBranchAction() {
	    super(getText("import_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if(parent != null) {
		JFileChooser chooser = new JFileChooser();
		//chooser.setLocale(currentLocale);
		if (getFileFilter() != null) {
		    chooser.addChoosableFileFilter(getFileFilter());
		}
		int returnVal = chooser.showOpenDialog(getView());
		if (returnVal==JFileChooser.APPROVE_OPTION) {
                    try {
                       MindMapNodeModel node = getModel().loadTree(chooser.getSelectedFile());
                       getModel().paste(node, parent); }
                    catch (Exception ex) {
                       handleLoadingException(ex);
                    }
		}
	    }
	}
    }

    private class ImportLinkedBranchAction extends AbstractAction {
	ImportLinkedBranchAction() {
	    super(getText("import_linked_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if((parent != null)&&(parent.getLink() != null)) {
		URL absolute = null;
		try {
		    absolute = new URL(getMap().getFile().toURL(), parent.getLink());
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(),"couldn't create valid URL!");
		    return;
		}
                try {
                   MindMapNodeModel node = getModel().loadTree(new File(absolute.getFile()));
                   getModel().paste(node, parent); }
                catch (Exception ex) {
                   handleLoadingException(ex);}
	    }
	}
    }


    /**
     * This is exactly the opposite of exportBranch.
     */
    private class ImportLinkedBranchWithoutRootAction extends AbstractAction {
	ImportLinkedBranchWithoutRootAction() {
	    super(getText("import_linked_branch_without_root"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if((parent != null)&&(parent.getLink() != null)) {
		URL absolute = null;
		try {
		    absolute = new URL(getMap().getFile().toURL(), parent.getLink());
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(),"couldn't create valid URL!");
		    return;
		}
                try {
                   MindMapNodeModel node = getModel().loadTree(new File(absolute.getFile()));
                   for (Enumeration enum=node.children();enum.hasMoreElements();) {
                      getModel().paste((MindMapNodeModel)enum.nextElement(), parent);
                   }
                   getModel().setLink(parent, null); }
                catch (Exception ex) {
                   handleLoadingException(ex); }
	    }
	}
    }

    private class ImportExplorerFavoritesAction extends AbstractAction {
	ImportExplorerFavoritesAction() {
	    super(getText("import_explorer_favorites"));
	}
	public void actionPerformed(ActionEvent e) {
           JFileChooser chooser = new JFileChooser();
           chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           chooser.setDialogTitle(getText("select_favorites_folder"));
           int returnVal = chooser.showOpenDialog(getView());
           if (returnVal == JFileChooser.APPROVE_OPTION) {
              File folder = chooser.getSelectedFile();
              getFrame().out("Importing Favorites ...");
              //getFrame().repaint(); // Refresh the frame, namely hide dialog and show status
              //getView().updateUI();
              // Problem: the frame should be refreshed here, but I don't know how to do it
              getModel().importExplorerFavorites(folder,getSelected(),/*redisplay=*/true);
              getFrame().out("Favorites imported.");
           }
	}
    }

    private class ImportFolderStructureAction extends AbstractAction {
	ImportFolderStructureAction() {
            super(getText("import_folder_structure"));
	}
	public void actionPerformed(ActionEvent e) {
           JFileChooser chooser = new JFileChooser();
           chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           chooser.setDialogTitle(getText("select_folder_for_importing"));
           int returnVal = chooser.showOpenDialog(getView());
           if (returnVal == JFileChooser.APPROVE_OPTION) {
              File folder = chooser.getSelectedFile();
              getFrame().out("Importing folder structure ...");
              //getFrame().repaint(); // Refresh the frame, namely hide dialog and show status
              //getView().updateUI();
              // Problem: the frame should be refreshed here, but I don't know how to do it
              getModel().importFolderStructure(folder,getSelected(),/*redisplay=*/true);
              getFrame().out("Folder structure imported.");
           }
	}
    }

    private class JoinNodesAction extends AbstractAction {
	JoinNodesAction() {
	    super(getText("join_nodes"));
	}
	public void actionPerformed(ActionEvent e) {
           ((MindMapMapModel)getView().getModel()).joinNodes();
	}
    }

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() {
	    super(getText("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }

    private class ForkAction extends AbstractAction {
	ForkAction() {
	    super(getText("fork"));
	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setNodeStyle(getSelected(), "fork");
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setNodeStyle(selected, "fork");
		}
	}
    }

    private class BubbleAction extends AbstractAction {
	BubbleAction() {
	    super(getText("bubble"));
	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setNodeStyle(getSelected(), "bubble");
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setNodeStyle(selected, "bubble");
		}
	}
    }

    private class EdgeStyleAction extends AbstractAction {
	String style;
	EdgeStyleAction(String style) {
	    super(getText(style));
		this.style = style;
	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setEdgeStyle(getSelected(), style);
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setEdgeStyle(selected, style);
		}
	}
    }

    //
    // Fonts
    //
    private class ItalicAction extends AbstractAction {
	ItalicAction(Object controller) {
	    super(getText("italic"), new ImageIcon(getResource("images/Italic24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setItalic(getSelected());
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setItalic(selected);
		}
	}
    }

    private class BoldAction extends AbstractAction {
	BoldAction(Object controller) {
	    super(getText("bold"), new ImageIcon(getResource("images/Bold24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setBold(getSelected());
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setBold(selected);
		}
	}
    }

    private class NormalFontAction extends AbstractAction {
	NormalFontAction(Object controller) {
	    super(getText("normal"), new ImageIcon(getResource("images/Normal24.gif")));	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setNormalFont(getSelected());
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setNormalFont(selected);
		}
	}
    }

    /**Not yet implemented*/
    private class UnderlineAction extends AbstractAction {
	UnderlineAction(Object controller) {
	    super(getText("underline"), new ImageIcon(getResource("images/Underline24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setUnderlined(getSelected());
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setUnderlined(selected);
		}
	}
    }

    //
    // Color
    //

    private class NodeColorAction extends AbstractAction {
	NodeColorAction() {
	    super(getText("node_color"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = JColorChooser.showDialog(getView(),"Choose Node Color:",getSelected().getColor() );
		if (color==null) return;
//	    getModel().setNodeColor(getSelected(), color);
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setNodeColor(selected, color);
		}
	}
    }

    private class EdgeColorAction extends AbstractAction {
	EdgeColorAction() {
	    super(getText("edge_color"));
	}
	public void actionPerformed(ActionEvent e) {
//	    MindMapNodeModel node = getSelected();
	    Color color = JColorChooser.showDialog(getView(),"Choose Edge Color:",getSelected().getEdge().getColor());
		if (color==null) return;
//	    getModel().setEdgeColor(node,color);
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setEdgeColor(selected,color);
		}
	}
    }

	private String getWidthTitle(int width) {
		if (width==EdgeAdapter.WIDTH_PARENT)
			return getText("edge_width_parent");
		if (width==EdgeAdapter.WIDTH_THIN)
			return getText("edge_width_thin");
		return Integer.toString(width);
	}

    private class EdgeWidthAction extends AbstractAction {
	int width;
	EdgeWidthAction(int width) {
			super(getWidthTitle(width));
			this.width = width;
	}
	public void actionPerformed(ActionEvent e) {
//	    getModel().setEdgeWidth(getSelected(),width);
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setEdgeWidth(selected,width);
		}
	}
    }

    private class IncreaseNodeFontAction extends AbstractAction {
	IncreaseNodeFontAction() {
	    super(getText("increase_node_font_size"));
	}
	public void actionPerformed(ActionEvent e) {
//	    MindMapNodeModel n = getSelected();
	    // we assume you have true type, so +1 works
//	    getModel().setFontSize(n,n.getFont().getSize()+1);

		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setFontSize(selected,selected.getFont().getSize()+1);
		}
	}
    }

    private class DecreaseNodeFontAction extends AbstractAction {
	DecreaseNodeFontAction() {
	    super(getText("decrease_node_font_size"));
	}
	public void actionPerformed(ActionEvent e) {
	    // we assume you have true type, so -1 works
//	    getModel().increaseBranchFontSize(getSelected(),-1);
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setFontSize(selected,selected.getFont().getSize()-1);
		}
	}
    }

    private class IncreaseBranchFontAction extends AbstractAction {
	IncreaseBranchFontAction() {
	    super(getText("increase_branch_font_size"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel n = getSelected();
		// we assume you have true type, so +1 works
		// getModel().setBranchFontSize(n,n.getFont().getSize()+1);
	    getModel().increaseBranchFontSize(getSelected(),1);
/*
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().increaseBranchFontSize(selected,1);
		}
*/
	}
    }

    private class DecreaseBranchFontAction extends AbstractAction {
	DecreaseBranchFontAction() {
	    super(getText("decrease_branch_font_size"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel n = getSelected();
	    // we assume you have true type, so -1 works
	    getModel().setBranchFontSize(n,n.getFont().getSize()-1);
/*
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setBranchFontSize(selected,selected.getFont().getSize()-1);
		}
*/
	}
    }


    //
    // Evaluation
    //

    private class ApplyPatternAction extends AbstractAction {
	Pattern pattern;
	ApplyPatternAction(Pattern pattern) {
	    super(pattern.getName());
	    this.pattern=pattern;
	}
	public void actionPerformed(ActionEvent e) {
	    for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)it.next();
		getModel().setNodeColor(selected, pattern.getNodeColor());		
		getModel().setNodeFont(selected, pattern.getNodeFont());
	    }
	}
    }

    //
    // Branch Format Actions
    //

    private class BoldifyBranchAction extends AbstractAction {
	BoldifyBranchAction() {
	    super(getText("boldify_branch"),
		  new ImageIcon(getResource("images/Bold24.gif")));
	}

	public void actionPerformed(ActionEvent e) {
//	    getModel().setBranchBold(getSelected());
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setBranchBold(selected);
		}
	}
    }

    private class NonBoldifyBranchAction extends AbstractAction {
	NonBoldifyBranchAction() {
	    super(getText("nonboldify_branch"));
	}

	public void actionPerformed(ActionEvent e) {
//	    getModel().setBranchNonBold(getSelected());
	    for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)it.next();
		getModel().setBranchNonBold(selected);
	    }
	}
    }

    private class ToggleBoldBranchAction extends AbstractAction {
	ToggleBoldBranchAction() {
	    super(getText("toggle_bold_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchToggleBold(getSelected());
	}
    }


    private class ItaliciseBranchAction extends AbstractAction {
	ItaliciseBranchAction() {
	    super(getText("italicise_branch"),
		  new ImageIcon(getResource("images/Italic24.gif")));
	}

	public void actionPerformed(ActionEvent e) {
//	    getModel().setBranchItalic(getSelected());
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setBranchItalic(selected);
		}
	}
    }

    private class NonItaliciseBranchAction extends AbstractAction {
	NonItaliciseBranchAction() {
	    super(getText("nonitalicise_branch"));
	}

	public void actionPerformed(ActionEvent e) {
//	    getModel().setBranchNonItalic(getSelected());
		for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
			MindMapNodeModel selected = (MindMapNodeModel)it.next();
			getModel().setBranchNonItalic(selected);
		}
	}
    }

    private class ToggleItalicBranchAction extends AbstractAction {
	ToggleItalicBranchAction() {
	    super(getText("toggle_italic_branch"));
	}

	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchToggleItalic(getSelected());
	}
    }


    //
    // Other classes
    //
    private class MindMapFilter extends FileFilter {
	public boolean accept(File f) {
	    if (f.isDirectory()) return true;
	    String extension = Tools.getExtension(f.getName());
	    if (extension != null) {
		if (extension.equals("mm")) {
		    return true;
		} else {
		    return false;
		}
	    }
	    return false;
	}
	
	public String getDescription() {
	    return getText("mindmaps_desc");
	}
    }
}
