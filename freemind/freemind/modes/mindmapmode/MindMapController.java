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
/*$Id: MindMapController.java,v 1.19 2001-05-05 13:58:46 ponder Exp $*/

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

    Action edit = new EditAction();
    Action addNew = new AddNewAction();
    Action remove = new RemoveAction();
    Action toggleFolded = new ToggleFoldedAction();
    Action toggleChildrenFolded = new toggleChildrenFoldedAction();
    Action setLinkByFileChooser = new SetLinkByFileChooserAction();
    Action setLinkByTextField = new SetLinkByTextFieldAction();
    Action followLink = new FollowLinkAction();
    Action exportBranch = new ExportBranchAction();
    Action importBranch = new ImportBranchAction();
    Action importLinkedBranch = new ImportLinkedBranchAction();
    Action importLinkedBranchWithoutRoot = new ImportLinkedBranchWithoutRootAction();
    Action nodeUp = new NodeUpAction();
    Action nodeDown = new NodeDownAction();

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
    Action patterns[];

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
	loadPatterns(new File(Tools.expandFileName(getFrame().getProperty("patternsfile"))));
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

    private void loadPatterns(File file) {
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

    public void doubleClick() {
	if (getFrame().getProperty("mindmap_doubleclick").equals("toggle_folded")) {
	    toggleFolded();
	} else {
	    loadURL();
	}
    }

    //Node editing
    void setFontSize(int fontSize) {
//	getModel().setFontSize(getSelected(),fontSize);
	for(ListIterator e = getSelecteds().listIterator();e.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)e.next();
		getModel().setFontSize(selected,fontSize);
	}
    }

    void setFont(String font) {
//	getModel().setFont(getSelected(),font);
	for(ListIterator e = getSelecteds().listIterator();e.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)e.next();
		getModel().setFont(selected,font);
	}
    }

    protected MindMapNode newNode() {
	return new MindMapNodeModel(getFrame().getResources().getString("new_node"),getFrame());
    }

    //get/set methods

    JMenu getEditMenu() {
	JMenu editMenu = new JMenu();
	editMenu.add(getNodeMenu());
	editMenu.add(getBranchMenu());
	editMenu.add(getEdgeMenu());
	editMenu.add(getExtensionMenu());
	JMenuItem cutItem = editMenu.add(cut);
 	cutItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_cut")));
	JMenuItem pasteItem = editMenu.add(paste);
 	pasteItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_paste")));
	return editMenu;
    }

    JMenu getFileMenu() {
	JMenu fileMenu = new JMenu();
	JMenuItem newMapItem = fileMenu.add(newMap);
 	newMapItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_newMap")));
	JMenuItem openItem = fileMenu.add(open);
 	openItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_open")));
	JMenuItem saveItem = fileMenu.add(save);
 	saveItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_save")));
	JMenuItem saveAsItem = fileMenu.add(saveAs);
 	saveAsItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_saveAs")));
	return fileMenu;
    }

    JMenu getExtensionMenu() {
	JMenu extensionMenu = new JMenu(getFrame().getResources().getString("extension_menu"));
	for (int i=0; i<patterns.length; ++i) { 
		extensionMenu.add(patterns[i]);
	}
	return extensionMenu;
    }

    JMenu getBranchMenu() {
	JMenu branchMenu = new JMenu(getFrame().getResources().getString("branch"));

	JMenuItem exportBranchItem = branchMenu.add(exportBranch);
	exportBranchItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_export_branch")));
	JMenu importMenu = new JMenu(getFrame().getResources().getString("import"));
	branchMenu.add(importMenu);

	JMenuItem importBranchItem = importMenu.add(importBranch);
	// 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_follow_link")));
	JMenuItem importLinkedBranchItem = importMenu.add(importLinkedBranch);
	// 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_follow_link")));
	JMenuItem importLinkedBranchWithoutRootItem = importMenu.add(importLinkedBranchWithoutRoot);

	branchMenu.addSeparator();

	JMenuItem boldifyBranchItem = branchMenu.add(boldifyBranch);
	JMenuItem nonBoldifyBranchItem = branchMenu.add(nonBoldifyBranch);
	JMenuItem toggleBoldBranchItem = branchMenu.add(toggleBoldBranch);

	branchMenu.addSeparator();

	JMenuItem italiciseBranchItem = branchMenu.add(italiciseBranch);
	JMenuItem nonItaliciseBranchItem = branchMenu.add(nonItaliciseBranch);
	JMenuItem toggleItalicBranchItem = branchMenu.add(toggleItalicBranch);

	branchMenu.addSeparator();

	JMenuItem increaseBranchFontItem = branchMenu.add(increaseBranchFont);
  	increaseBranchFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_branch_increase_font_size")));

	JMenuItem decreaseBranchFontItem = branchMenu.add(decreaseBranchFont);
	decreaseBranchFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_branch_decrease_font_size")));

	return branchMenu;
    }


    JMenu getNodeMenu() {
	JMenu nodeMenu = new JMenu(getFrame().getResources().getString("node"));
	JMenuItem editItem = nodeMenu.add(edit);
 	editItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_edit")));
 	JMenuItem addNewItem = nodeMenu.add(addNew);
 	addNewItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_add")));
 	JMenuItem removeItem = nodeMenu.add(remove);
 	removeItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_remove")));

 	JMenuItem nodeUpItem = nodeMenu.add(nodeUp);
 	nodeUpItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_up")));
 	JMenuItem nodeDownItem = nodeMenu.add(nodeDown);
 	nodeDownItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_down")));

	nodeMenu.addSeparator();

	JMenuItem followLinkItem = nodeMenu.add(followLink);
 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_follow_link")));
	JMenuItem setLinkByFileChooserItem = nodeMenu.add(setLinkByFileChooser);
 	setLinkByFileChooserItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_set_link_by_filechooser")));
	JMenuItem setLinkByTextFieldItem = nodeMenu.add(setLinkByTextField);
 	setLinkByTextFieldItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_set_link_by_textfield")));

	nodeMenu.addSeparator();

	JMenuItem toggleFoldedItem = nodeMenu.add(toggleFolded);
 	toggleFoldedItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_toggle_folded")));
	JMenuItem toggleChildrenFoldedItem = nodeMenu.add(toggleChildrenFolded);
	toggleChildrenFoldedItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_toggle_children_folded")));

	nodeMenu.addSeparator();

	JMenu nodeStyle = new JMenu(getFrame().getResources().getString("style"));
	nodeMenu.add(nodeStyle);
	nodeStyle.add(fork);
	nodeStyle.add(bubble);
	JMenu nodeFont = new JMenu(getFrame().getResources().getString("font"));
	JMenuItem increaseNodeFontItem = nodeFont.add(increaseNodeFont);
  	increaseNodeFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_increase_font_size")));

	JMenuItem decreaseNodeFontItem = nodeFont.add(decreaseNodeFont);
	decreaseNodeFontItem.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty("keystroke_node_decrease_font_size")));

	nodeFont.addSeparator();

	nodeFont.add(italic);
	nodeFont.add(bold);
	nodeMenu.add(nodeFont);
	//	nodeFont.add(underline);
	nodeMenu.add(nodeColor);

	return nodeMenu;
    }

    JMenu getEdgeMenu() {
	JMenu edgeMenu = new JMenu(getFrame().getResources().getString("edge"));
	JMenu edgeStyle = new JMenu(getFrame().getResources().getString("style"));
	edgeMenu.add(edgeStyle);
	for (int i=0; i<edgeStyles.length; ++i) { 
		edgeStyle.add(edgeStyles[i]);
	}
	edgeMenu.add(edgeColor);
	JMenu edgeWidth = new JMenu(getFrame().getResources().getString("width"));
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

    private class ExportBranchAction extends AbstractAction {
	ExportBranchAction() {
	    super(getFrame().getResources().getString("export_branch"));
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

		MindMapNodeModel parent = (MindMapNodeModel)node.getParent();
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

		getModel().insertNodeInto(newNode,parent, 0);
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
	    super(getFrame().getResources().getString("import_branch"));
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
		    MindMapNodeModel node = getModel().loadTree(chooser.getSelectedFile());
		    getModel().paste(node, parent);
		}
	    }
	}
    }

    private class ImportLinkedBranchAction extends AbstractAction {
	ImportLinkedBranchAction() {
	    super(getFrame().getResources().getString("import_linked_branch"));
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
		MindMapNodeModel node = getModel().loadTree(new File(absolute.getFile()));
		getModel().paste(node, parent);
	    }
	}
    }


    /**
     * This is exactly the opposite of exportBranch.
     */
    private class ImportLinkedBranchWithoutRootAction extends AbstractAction {
	ImportLinkedBranchWithoutRootAction() {
	    super(getFrame().getResources().getString("import_linked_branch_without_root"));
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
		MindMapNodeModel node = getModel().loadTree(new File(absolute.getFile()));
		for (Enumeration enum=node.children();enum.hasMoreElements();) {
		    getModel().paste((MindMapNodeModel)enum.nextElement(), parent);
		}
		getModel().setLink(parent, null);
	    }
	}
    }

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() {
	    super(getFrame().getResources().getString("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }

    private class ForkAction extends AbstractAction {
	ForkAction() {
	    super(getFrame().getResources().getString("fork"));
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
	    super(getFrame().getResources().getString("bubble"));
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
	    super(getFrame().getResources().getString(style));
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
	    super(getFrame().getResources().getString("italic"), new ImageIcon(getResource("images/Italic24.gif")));
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
	    super(getFrame().getResources().getString("bold"), new ImageIcon(getResource("images/Bold24.gif")));
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
	    super(getFrame().getResources().getString("normal"), new ImageIcon(getResource("images/Normal24.gif")));	}
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
	    super(getFrame().getResources().getString("underline"), new ImageIcon(getResource("images/Underline24.gif")));
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
	    super(getFrame().getResources().getString("node_color"));
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
	    super(getFrame().getResources().getString("edge_color"));
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
			return getFrame().getResources().getString("edge_width_parent");
		if (width==EdgeAdapter.WIDTH_THIN)
			return getFrame().getResources().getString("edge_width_thin");
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
	    super(getFrame().getResources().getString("increase_node_font_size"));
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
	    super(getFrame().getResources().getString("decrease_node_font_size"));
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
	    super(getFrame().getResources().getString("increase_branch_font_size"));
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
	    super(getFrame().getResources().getString("decrease_branch_font_size"));
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
		System.out.println(pattern.getNodeColor());
		
		getModel().setNodeFont(selected, pattern.getNodeFont());
		System.out.println(pattern.getNodeFont());
		
	    }
	}
    }

    //
    // Branch Format Actions
    //

    private class BoldifyBranchAction extends AbstractAction {
	BoldifyBranchAction() {
	    super(getFrame().getResources().getString("boldify_branch"),
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
	    super(getFrame().getResources().getString("nonboldify_branch"));
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
	    super(getFrame().getResources().getString("toggle_bold_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setBranchToggleBold(getSelected());
	}
    }


    private class ItaliciseBranchAction extends AbstractAction {
	ItaliciseBranchAction() {
	    super(getFrame().getResources().getString("italicise_branch"),
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
	    super(getFrame().getResources().getString("nonitalicise_branch"));
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
	    super(getFrame().getResources().getString("toggle_italic_branch"));
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
	    return getFrame().getResources().getString("mindmaps_desc");
	}
    }
}
