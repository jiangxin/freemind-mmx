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
/*$Id: MindMapController.java,v 1.9 2000-11-08 12:20:31 ponder Exp $*/

package freemind.modes.mindmapmode;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import java.io.File;
import java.util.Enumeration;
import java.net.URL;
import java.net.MalformedURLException;
import java.awt.Color;
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

    Mode mode;
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
    Action setLink = new SetLinkAction();
    Action followLink = new FollowLinkAction();
    Action exportBranch = new ExportBranchAction();
    Action importBranch = new ImportBranchAction();
    Action importLinkedBranch = new ImportLinkedBranchAction();
    Action importLinkedBranchWithoutRoot = new ImportLinkedBranchWithoutRootAction();

    Action fork = new ForkAction();
    Action bubble = new BubbleAction();
    Action nodeColor = new NodeColorAction();
    Action edgeColor = new EdgeColorAction();
    Action linear = new LinearAction();
    Action bezier = new BezierAction();
    Action italic = new ItalicAction(this);
    Action bold = new BoldAction(this);
    //    Action underline = new UnderlineAction(this);
    Action normalFont = new NormalFontAction(this);

    FileFilter filefilter = new MindMapFilter();

    public MindMapController(Mode mode) {
	super(mode);
	popupmenu = new MindMapPopupMenu(this);
	toolbar = new MindMapToolBar(this);
	setAllActions(false);
    }

    public MapAdapter newModel() {
	return new MindMapMapModel();
    }

    public void save(File file) {
	getModel().save(file);
    }

    public FileFilter getFileFilter() {
	return filefilter;
    }

    public void doubleClick() {
	if (FreeMind.userProps.getProperty("mindmap_doubleclick").equals("follow_link")) {
	    loadURL();
	} else {
	    toggleFolded();
	}
    }

    //Node editing
    void setFontSize(int fontSize) {
	getModel().setFontSize(getSelected(),fontSize);
    }

    void setFont(String font) {
	getModel().setFont(getSelected(),font);
    }

    protected MindMapNode newNode() {
	return new MindMapNodeModel(FreeMind.getResources().getString("new_node"));
    }

    //get/set methods

    JMenu getEditMenu() {
	JMenu editMenu = new JMenu();
	editMenu.add(getNodeMenu());
	editMenu.add(getEdgeMenu());
	JMenuItem cutItem = editMenu.add(cut);
 	cutItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_cut")));
	JMenuItem pasteItem = editMenu.add(paste);
 	pasteItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_paste")));
	return editMenu;
    }

    JMenu getFileMenu() {
	JMenu fileMenu = new JMenu();
	JMenuItem newMapItem = fileMenu.add(newMap);
 	newMapItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_newMap")));
	JMenuItem openItem = fileMenu.add(open);
 	openItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_open")));
	JMenuItem saveItem = fileMenu.add(save);
 	saveItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_save")));
	JMenuItem saveAsItem = fileMenu.add(saveAs);
 	saveAsItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_saveAs")));
	return fileMenu;
    }

    JMenu getNodeMenu() {
	JMenu nodeMenu = new JMenu(FreeMind.getResources().getString("node"));
	JMenuItem editItem = nodeMenu.add(edit);
 	editItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_edit")));
 	JMenuItem addNewItem = nodeMenu.add(addNew);
 	addNewItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_add")));
 	JMenuItem removeItem = nodeMenu.add(remove);
 	removeItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_remove")));
	JMenuItem exportBranchItem = nodeMenu.add(exportBranch);
	// 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_follow_link")));
	JMenu importMenu = new JMenu(FreeMind.getResources().getString("import"));
	nodeMenu.add(importMenu);
	JMenuItem importBranchItem = importMenu.add(importBranch);
	// 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_follow_link")));
	JMenuItem importLinkedBranchItem = importMenu.add(importLinkedBranch);
	// 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_follow_link")));
	JMenuItem importLinkedBranchWithoutRootItem = importMenu.add(importLinkedBranchWithoutRoot);
	JMenuItem followLinkItem = nodeMenu.add(followLink);
 	followLinkItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_follow_link")));
	JMenuItem setLinkItem = nodeMenu.add(setLink);
 	setLinkItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_set_link")));
	JMenuItem toggleFoldedItem = nodeMenu.add(toggleFolded);
 	toggleFoldedItem.setAccelerator(KeyStroke.getKeyStroke(FreeMind.userProps.getProperty("keystroke_toggle_folded")));
	JMenu nodeStyle = new JMenu(FreeMind.getResources().getString("style"));
	nodeMenu.add(nodeStyle);
	nodeStyle.add(fork);
	nodeStyle.add(bubble);
	JMenu nodeFont = new JMenu(FreeMind.getResources().getString("font"));
	nodeMenu.add(nodeFont);
	nodeFont.add(italic);
	nodeFont.add(bold);
	//	nodeFont.add(underline);
	nodeMenu.add(nodeColor);
	return nodeMenu;
    }

    JMenu getEdgeMenu() {
	JMenu edgeMenu = new JMenu(FreeMind.getResources().getString("edge"));
	JMenu edgeStyle = new JMenu(FreeMind.getResources().getString("style"));
	edgeMenu.add(edgeStyle);
	edgeStyle.add(linear);
	edgeStyle.add(bezier);
	edgeMenu.add(edgeColor);
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
	setLink.setEnabled(enabled);
	followLink.setEnabled(enabled);
	italic.setEnabled(enabled);
	bold.setEnabled(enabled);
	normalFont.setEnabled(enabled);
	nodeColor.setEnabled(enabled);
	edgeColor.setEnabled(enabled);
	fork.setEnabled(enabled);
	bubble.setEnabled(enabled);
	linear.setEnabled(enabled);
	bezier.setEnabled(enabled);
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
	    super(FreeMind.getResources().getString("export_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel node = (MindMapNodeModel)getSelected();
	    if(node == null || node.isRoot()) {
		return;
	    }

	    //Open FileChooser to choose in which file the exported
	    //branch should be stored
	    JFileChooser chooser;
	    if ((getMap() != null) && (getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
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
		String ext = Tools.getExtension(f);
		if(!ext.equals("mm")) {
		    f = new File(f.getParent(),f.getName()+".mm");
		}
		try {
		    link = f.toURL();
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getController().getFrame(),"couldn't create valid URL!");
		    return;
		}

		//Now make a copy from the node, remove the node from the map and create a new
		//Map with the node as root, store the new Map, add the copy of the node to the parent,
		//and set a link from the copy to the new Map.

		MindMapNodeModel parent = (MindMapNodeModel)node.getParent();
		MindMapNodeModel newNode = new MindMapNodeModel(node.toString());
		getModel().removeNodeFromParent(node);
		node.setParent(null);
		MindMapMapModel map = new MindMapMapModel(node);
		if (getModel().getFile() != null) {
		    try{
			map.setLink(node, getModel().getFile().toURL().toString());
		    } catch(MalformedURLException ex) {
		    }
		}
		//		getController().newMapModule(map);
		map.save(f);

		getModel().insertNodeInto(newNode,parent, 0);
		String linkString = link.toString();
		getModel().setLink(newNode,linkString);
		getModel().save(getModel().getFile());
	    }
	}
    }

    private class ImportBranchAction extends AbstractAction {
	ImportBranchAction() {
	    super(FreeMind.getResources().getString("import_branch"));
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
	    super(FreeMind.getResources().getString("import_linked_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if((parent != null)&&(parent.getLink() != null)) {
		URL absolute = null;
		try {
		    absolute = new URL(getMap().getFile().toURL(), parent.getLink());
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getController().getFrame(),"couldn't create valid URL!");
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
	    super(FreeMind.getResources().getString("import_linked_branch_without_root"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if((parent != null)&&(parent.getLink() != null)) {
		URL absolute = null;
		try {
		    absolute = new URL(getMap().getFile().toURL(), parent.getLink());
		} catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getController().getFrame(),"couldn't create valid URL!");
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

    private class SetLinkAction extends AbstractAction {
	SetLinkAction() {
	    super(FreeMind.getResources().getString("set_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    setLink();
	}
    }

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() {
	    super(FreeMind.getResources().getString("follow_link"));
	}
	public void actionPerformed(ActionEvent e) {
	    loadURL();
	}
    }

    private class ForkAction extends AbstractAction {
	ForkAction() {
	    super(FreeMind.getResources().getString("fork"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNodeStyle(getSelected(), "fork");
	}
    }

    private class BubbleAction extends AbstractAction {
	BubbleAction() {
	    super(FreeMind.getResources().getString("bubble"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNodeStyle(getSelected(), "bubble");
	}
    }

    private class LinearAction extends AbstractAction {
	LinearAction() {
	    super(FreeMind.getResources().getString("linear"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setEdgeStyle(getSelected(), "linear");
	}
    }

    private class BezierAction extends AbstractAction {
	BezierAction() {
	    super(FreeMind.getResources().getString("bezier"));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setEdgeStyle(getSelected(), "bezier");
	}
    }

    //
    // Fonts
    //
    private class ItalicAction extends AbstractAction {
	ItalicAction(Object controller) {
	    super(FreeMind.getResources().getString("italic"), new ImageIcon(ClassLoader.getSystemResource("images/Italic24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setItalic(getSelected());
	}
    }

    private class BoldAction extends AbstractAction {
	BoldAction(Object controller) {
	    super(FreeMind.getResources().getString("bold"), new ImageIcon(ClassLoader.getSystemResource("images/Bold24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setBold(getSelected());
	}
    }

    private class NormalFontAction extends AbstractAction {
	NormalFontAction(Object controller) {
	    super(FreeMind.getResources().getString("normal"), new ImageIcon(ClassLoader.getSystemResource("images/Normal24.gif")));	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setNormalFont(getSelected());
	}
    }

    /**Not yet implemented*/
    private class UnderlineAction extends AbstractAction {
	UnderlineAction(Object controller) {
	    super(FreeMind.getResources().getString("underline"), new ImageIcon(ClassLoader.getSystemResource("images/Underline24.gif")));
	}
	public void actionPerformed(ActionEvent e) {
	    getModel().setUnderlined(getSelected());
	}
    }

    //
    // Color
    //

    private class NodeColorAction extends AbstractAction {
	NodeColorAction() {
	    super(FreeMind.getResources().getString("node_color"));
	}
	public void actionPerformed(ActionEvent e) {
	    Color color = JColorChooser.showDialog(getView(),"Choose Node Color:",getSelected().getColor() );
	    getModel().setNodeColor(getSelected(), color);
	}
    }

    private class EdgeColorAction extends AbstractAction {
	EdgeColorAction() {
	    super(FreeMind.getResources().getString("edge_color"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel node = getSelected();
	    Color color = JColorChooser.showDialog(getView(),"Choose Edge Color:",getSelected().getEdge().getColor());
	    getModel().setEdgeColor(node,color);
	}
    }

    //
    // Other classes
    //
    private class MindMapFilter extends FileFilter {
	public boolean accept(File f) {
	    if (f.isDirectory()) return true;
	    String extension = Tools.getExtension(f);
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
	    return FreeMind.getResources().getString("mindmaps");
	}
    }
}
