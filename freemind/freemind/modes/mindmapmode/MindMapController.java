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
/*$Id: MindMapController.java,v 1.35.10.7 2004-05-06 05:08:27 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.JAXBException;

import freemind.controller.Controller;
import freemind.controller.actions.ActionPair;
import freemind.controller.actions.ActorXml;
import freemind.controller.actions.generated.instance.BoldNodeAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookFactory;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.ControllerAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapCloud;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.NodeAdapter;
import freemind.modes.StylePattern;
import freemind.modes.actions.*;
import freemind.modes.actions.EditAction;
import freemind.modes.actions.NewMapAction;
import freemind.modes.actions.NodeGeneralAction;
import freemind.modes.actions.SingleNodeOperation;





public class MindMapController extends ControllerAdapter {

	public Vector nodeHookActions;
	public Vector modeControllerHookActions;
	//    Mode mode;
    private JPopupMenu popupmenu;
    //private JToolBar toolbar;
    private MindMapToolBar toolbar;
    private boolean addAsChildMode = false;

	public void setBold(MindMapNode node, boolean bolded) {
		bold.setBold(node, bolded);
	}

	BoldAction bold   = new BoldAction (this);
	Action deleteChild = new DeleteChildAction(this);
    Action newMap = new NewMapAction(this, this);
    Action open = new OpenAction(this);
    Action save = new SaveAction(this);
    Action saveAs = new SaveAsAction(this);
    Action exportToHTML = new ExportToHTMLAction(this);
    Action exportBranchToHTML = new ExportBranchToHTMLAction(this);

    Action edit = new EditAction(this);
    Action editLong = new EditLongAction();
    Action newChild = new NewChildAction(this);
    Action newChildWithoutFocus = new NewChildWithoutFocusAction();
    Action newSibling = new NewSiblingAction();
    Action newPreviousSibling = new NewPreviousSiblingAction();
    Action remove = new RemoveAction();
    Action toggleFolded = new ToggleFoldedAction();
    Action toggleChildrenFolded = new ToggleChildrenFoldedAction();
    Action setLinkByFileChooser = new SetLinkByFileChooserAction();
	Action setImageByFileChooser = new SetImageByFileChooserAction();
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
    Action findNext = new FindNextAction();

    Action fork = new ForkAction();
    Action bubble = new BubbleAction();
    Action nodeColor = new NodeColorAction();
    Action nodeColorBlend = new NodeGeneralAction (this, "blend_color", null,
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
          map.blendNodeColor(node); }});

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
    Action cloudColor = new CloudColorAction();

    Action italic = new NodeGeneralAction (this, "italic", "images/Italic24.gif",
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
          map.setItalic(node); }});
    Action cloud   = new NodeGeneralAction (this, "cloud", "images/Cloud24.gif",
       new SingleNodeOperation() { private MindMapCloud lastCloud;
           private MindMapNodeModel nodeOfLastCloud;
           public void apply(MindMapMapModel map, MindMapNodeModel node) {
               // store last color to enable if the node is switched on and off.
               if(node.getCloud() != null) {
                   lastCloud = node.getCloud();
                   nodeOfLastCloud = node;
               }
               map.setCloud(node); 
               // restore color:
               if((node.getCloud() != null) && (node == nodeOfLastCloud)) {
                   node.setCloud(lastCloud);
               }

           }
       });
    Action normalFont = new NodeGeneralAction (this, "normal", "images/Normal24.gif",
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
          map.setNormalFont(node); }});
    Action increaseNodeFont = new NodeGeneralAction (this, "increase_node_font_size", null,
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
          map.increaseFontSize(node,1); }});
    Action decreaseNodeFont = new NodeGeneralAction (this, "decrease_node_font_size", null,
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
          map.increaseFontSize(node,-1); }});

    // Extension Actions
    Action patterns[] = new Action[0]; // Make sure it is initialized
    public Vector iconActions = new Vector(); //fc
    Action removeLastIcon = new NodeGeneralAction (this, "remove_last_icon", "images/remove.png",
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
          map.removeLastIcon(node); }});
    Action removeAllIcons = new NodeGeneralAction (this, "remove_all_icons", "images/edittrash.png",
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
           while(map.removeLastIcon(node)>0) {}; }});




    FileFilter filefilter = new MindMapFilter();

    public MindMapController(Mode mode) {
	super(mode);
	try {
           File patternsFile = getFrame().getPatternsFile();
           if (patternsFile != null && patternsFile.exists()) {
              loadPatterns(patternsFile); }
           else {
              System.out.println("User patterns file "+patternsFile+" not found.");
              loadPatterns(new InputStreamReader(getResource("patterns.xml").openStream())); }}
        catch (XMLParseException e) {
           System.err.println("In patterns:"+e); }
	catch (Exception ex) {
           System.err.println("Patterns not loaded:"+ex); }
        // icon actions:
        createIconActions();
        //node hook actions:
        createNodeHookActions();
        //HOOK TEST
        if(modeControllerHookActions == null) {
			modeControllerHookActions = new Vector();
        }
		HookFactory factory = getFrame().getHookFactory();
        List hooks = factory.getPossibleModeControllerHooks(this.getClass());
		for(Iterator i = hooks.iterator(); i.hasNext();) {
			String desc = (String) i.next();
			ModeControllerHookAction action = new ModeControllerHookAction(desc, this);
			factory.decorateAction(desc, action);
			modeControllerHookActions.add(action);
		}
		//HOOK TEST END       

      	popupmenu = new MindMapPopupMenu(this);
	toolbar = new MindMapToolBar(this);

	setAllActions(false); 
   
        // addAsChildMode (use old model of handling CtrN) (PN)
        addAsChildMode = Tools.safeEquals(
            getFrame().getProperty("add_as_child"),"true");
    }

	public MapAdapter newModel() {
       return new MindMapMapModel(getFrame()); }

    private void loadPatterns(File file) throws Exception {
       createPatterns(StylePattern.loadPatterns(file)); }

    private void loadPatterns(Reader reader) throws Exception {
       createPatterns(StylePattern.loadPatterns(reader)); }

    private void createPatterns(List patternsList) throws Exception {
	patterns = new Action[patternsList.size()];
	for (int i=0;i<patterns.length;i++) {
        patterns[i] = new ApplyPatternAction((StylePattern)patternsList.get(i));

        // search icons for patterns:
        MindIcon patternIcon = ((StylePattern)patternsList.get(i)).getNodeIcon();
        if (patternIcon != null) {
            patterns[i].putValue(Action.SMALL_ICON, patternIcon.getIcon(getFrame()));
        }
    }}

    private void createIconActions() {
        Vector iconNames = MindIcon.getAllIconNames();
        for ( int i = 0 ; i < iconNames.size(); ++i ) {
            String iconName = ((String) iconNames.get(i));
            MindIcon myIcon     = new MindIcon(iconName);
            Action myAction = new IconAction(myIcon);
            iconActions.add(myAction);
        }
    }

	/**
	 * 
	 */
	private void createNodeHookActions() {
		if(nodeHookActions!= null)
			return;
		nodeHookActions = new Vector();
		// HOOK TEST
		HookFactory factory = getFrame().getHookFactory();
		  List list = factory.getPossibleNodeHooks(this.getClass());
		  for(Iterator i=list.iterator(); i.hasNext();){
			  String desc = (String) i.next();
			  // create hook action. 
			  //URGENT: According to its properties!!
			  NodeHookAction action = new NodeHookAction(desc, this);
			  factory.decorateAction(desc, action);
			  nodeHookActions.add(action);			
		  }
	  // HOOK TEST END
	}


    public FileFilter getFileFilter() {
       return filefilter; }

    //Node editing
    void setFontSize(int fontSize) {
	for(ListIterator e = getSelecteds().listIterator();e.hasNext();) {
           MindMapNodeModel selected = (MindMapNodeModel)e.next();
           getMindMapMapModel().setFontSize(selected,fontSize); }}

    void setFontFamily(String fontFamily) {
       for(ListIterator e = getSelecteds().listIterator();e.hasNext();) {
          MindMapNodeModel selected = (MindMapNodeModel)e.next();
          getMindMapMapModel().setFontFamily(selected,fontFamily); }}

    public void nodeChanged(MindMapNode n) {
    	super.nodeChanged(n);
    	// only for the selected node (fc, 2.5.2004)
		if (n == getSelected()) {
			toolbar.selectFontSize(n.getFontSize());
			toolbar.selectFontName(n.getFontFamilyName());
		}
	 }

    public void anotherNodeSelected(MindMapNode n) {
       super.anotherNodeSelected(n);
       toolbar.selectFontSize(((NodeAdapter)n).getFontSize());
       toolbar.selectFontName(((NodeAdapter)n).getFontFamilyName()); }

    public MindMapNode newNode() {
       return new MindMapNodeModel("" // getText("new_node") (PN) nicer when created
                                  , getFrame()); }

	    //get/set methods

    JMenu getEditMenu() {
	JMenu editMenu = new JMenu();

        JMenu leading = getLeadingNodeMenu();
        Component[] mc = leading.getMenuComponents();
        for (int i = 0; i < mc.length; i++) {
           editMenu.add(mc[i]); }

        editMenu.addSeparator();
	editMenu.add(getNodeMenu());
	editMenu.add(getBranchMenu());
	editMenu.add(getEdgeMenu());
	editMenu.add(getExtensionMenu());
	editMenu.add(getIconMenu());
	// hooks, fc, 1.3.2004:
	for (int i=0; i<nodeHookActions.size(); ++i) {          
		   JMenuItem item = editMenu.add((Action) nodeHookActions.get(i));
	}

//	List list = getFrame().getHookFactory().getPossibleNodeHooks(this.getClass());
//	for(Iterator i=list.iterator(); i.hasNext();){
//		String desc = (String) i.next();
//		// create hook class.
//		HookFactory factory = getFrame().getHookFactory();
//		NodeHook hook = factory.createNodeHook(desc, null, 
//			getMap(), 
//			this);
//		hook.nodeMenuHook(editMenu);
//	}
//	// end hook generation.


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
        // hooks: 
		// hooks, fc, 1.3.2004:
		for (int i=0; i<modeControllerHookActions.size(); ++i) {          
			   JMenuItem item = fileMenu.add((Action) modeControllerHookActions.get(i));
		}
        return fileMenu;
    }

    JMenu getExtensionMenu() {
	JMenu extensionMenu = new JMenu(getText("extension_menu"));
	for (int i=0; i<patterns.length; ++i) {          
           JMenuItem item = extensionMenu.add(patterns[i]);
           item.setAccelerator
              (KeyStroke.getKeyStroke
               (getFrame().getProperty("keystroke_apply_pattern_"+(i+1)))); }
	return extensionMenu; }

    /* fc, 12.10.2003.*/
    JMenu getIconMenu() {
	JMenu iconMenu = new JMenu(getText("icon_menu"));
    iconMenu.add(removeLastIcon);
    iconMenu.add(removeAllIcons);
    iconMenu.addSeparator();
	for (int i=0; i<iconActions.size(); ++i) {          
           JMenuItem item = iconMenu.add((Action) iconActions.get(i));
    }
	return iconMenu; }

    JMenu getBranchMenu() {
	JMenu branchMenu = new JMenu(getText("branch"));

	add(branchMenu, exportBranch, "keystroke_export_branch");
        add(branchMenu, exportBranchToHTML, "keystroke_export_branch_to_html");



	branchMenu.addSeparator();

	add(branchMenu, importBranch);
	add(branchMenu, importLinkedBranch);
	add(branchMenu, importLinkedBranchWithoutRoot);

        branchMenu.addSeparator();

        add(branchMenu, importExplorerFavorites);
        add(branchMenu, importFolderStructure);

	return branchMenu;
    }

    JMenu getLeadingNodeMenu() {
       JMenu leadingEditMenu = new JMenu();
       add(leadingEditMenu, edit, "keystroke_edit");
       add(leadingEditMenu, editLong, "keystroke_edit_long_node");
       // as mac's do not have an insert key, it is mapped to an alternative key.
	   String osName = System.getProperty("os.name");
       if (osName.startsWith("Mac OS")) {
           add(leadingEditMenu, newChild, "keystroke_add_child_mac");
       } else {
           add(leadingEditMenu, newChild, "keystroke_add_child");
       }
       leadingEditMenu.addSeparator();

       add(leadingEditMenu, cut, "keystroke_cut");
       add(leadingEditMenu, copy, "keystroke_copy");
       add(leadingEditMenu, copySingle, "keystroke_copy_single");
       add(leadingEditMenu, paste, "keystroke_paste");
       return leadingEditMenu; }

    JMenu getNodeMenu() {
	JMenu nodeMenu = new JMenu(getText("node"));

// currently only hidden feature - needs debugging %%%   
//#  if the property "add_as_child = true" is set,
//#  the old logic of inserting of a new node with Ctrl+N is used.
   
        if (addAsChildMode) {
          add(nodeMenu, newChildWithoutFocus, "keystroke_add_sibling_before");
        }
        else {
          add(nodeMenu, newPreviousSibling, "keystroke_add_sibling_before");
        }
        add(nodeMenu, newSibling, "keystroke_add");
 	add(nodeMenu, remove, "keystroke_remove");
        add(nodeMenu, joinNodes, "keystroke_join_nodes");

	nodeMenu.addSeparator();

        add(nodeMenu, find, "keystroke_find");
        add(nodeMenu, findNext, "keystroke_find_next");

	nodeMenu.addSeparator();

 	add(nodeMenu, nodeUp, "keystroke_node_up");
 	add(nodeMenu, nodeDown, "keystroke_node_down");

	nodeMenu.addSeparator();

	add(nodeMenu, followLink, "keystroke_follow_link");
	add(nodeMenu, setLinkByFileChooser, "keystroke_set_link_by_filechooser");
	add(nodeMenu, setLinkByTextField, "keystroke_set_link_by_textfield");

	nodeMenu.addSeparator();

	add(nodeMenu, setImageByFileChooser, "keystroke_set_image_by_filechooser");

	nodeMenu.addSeparator();

	add(nodeMenu, toggleFolded, "keystroke_toggle_folded");
	add(nodeMenu, toggleChildrenFolded, "keystroke_toggle_children_folded");

	nodeMenu.addSeparator();

	JMenu nodeStyle = new JMenu(getText("style"));
	nodeMenu.add(nodeStyle);

	add(nodeStyle, fork);
	add(nodeStyle, bubble);
    // and the clouds:
	nodeStyle.addSeparator();
	add(nodeStyle, cloud, "keystroke_node_toggle_cloud");
	add(nodeStyle, cloudColor);
    
    

	JMenu nodeFont = new JMenu(getText("font"));
	add(nodeFont, increaseNodeFont, "keystroke_node_increase_font_size");
	add(nodeFont, decreaseNodeFont, "keystroke_node_decrease_font_size");

	nodeFont.addSeparator();

	add(nodeFont, italic,"keystroke_node_toggle_italic"); 
	add(nodeFont, bold,"keystroke_node_toggle_boldface");
	nodeMenu.add(nodeFont);
	//	nodeFont.add(underline);
	add(nodeMenu, nodeColor, "keystroke_node_color"); 
        add(nodeMenu, nodeColorBlend, "keystroke_node_color_blend");

	return nodeMenu;
    }

    JMenu getEdgeMenu() {
	JMenu edgeMenu = new JMenu(getText("edge"));
	JMenu edgeStyle = new JMenu(getText("style"));
	edgeMenu.add(edgeStyle);
	for (int i=0; i<edgeStyles.length; ++i) { 
           edgeStyle.add(edgeStyles[i]); }
 	add(edgeMenu, edgeColor, "keystroke_edge_color");
	JMenu edgeWidth = new JMenu(getText("width"));
	edgeMenu.add(edgeWidth);
	for (int i=0; i<edgeWidths.length; ++i) { 
           edgeWidth.add(edgeWidths[i]); }
	return edgeMenu; }

    public JPopupMenu getPopupMenu() {
        return popupmenu;
    }

    /** Link implementation: If this is a link, we want to make a popup with at least removelink available.*/
    public JPopupMenu getPopupForModel(java.lang.Object obj) {
        if( obj instanceof MindMapArrowLinkModel) {
            // yes, this is a link.
            MindMapArrowLinkModel link = (MindMapArrowLinkModel) obj;
            JPopupMenu arrowLinkPopup = new JPopupMenu();
            // block the screen while showing popup.
            arrowLinkPopup.addPopupMenuListener( this.popupListenerSingleton );
            arrowLinkPopup.add(new RemoveArrowLinkAction(link.getSource(), link));
            arrowLinkPopup.add(new ColorArrowLinkAction(link.getSource(), link));
            arrowLinkPopup.addSeparator();
            /* The arrow state as radio buttons: */
            JRadioButtonMenuItem itemnn = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction("none", "images/arrow-mode-none.gif",link.getSource(), link, false, false) );
            arrowLinkPopup.add( itemnn );
            JRadioButtonMenuItem itemnt = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction("forward", "images/arrow-mode-forward.gif",link.getSource(), link, false, true) );
            arrowLinkPopup.add( itemnt );
            JRadioButtonMenuItem itemtn = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction("backward", "images/arrow-mode-backward.gif",link.getSource(), link, true, false) );
            arrowLinkPopup.add( itemtn );
            JRadioButtonMenuItem itemtt = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction("both", "images/arrow-mode-both.gif",link.getSource(), link, true, true) );
            arrowLinkPopup.add( itemtt );
            // select the right one:
            boolean a = !link.getStartArrow().equals("None");
            boolean b = !link.getEndArrow().equals("None");
            itemtt.setSelected(a&&b);
            itemnt.setSelected(!a&&b);
            itemtn.setSelected(a&&!b);
            itemnn.setSelected(!a&&!b);

            arrowLinkPopup.addSeparator();
            
            arrowLinkPopup.add(new GotoLinkNodeAction(link.getSource().toString(), link.getSource())); 
            arrowLinkPopup.add(new GotoLinkNodeAction(link.getTarget().toString(), link.getTarget())); 

            arrowLinkPopup.addSeparator();
            // add all links from target and from source:
            HashSet NodeAlreadyVisited = new HashSet();
            NodeAlreadyVisited.add(link.getSource());
            NodeAlreadyVisited.add(link.getTarget());
            Vector links = getMindMapMapModel().getLinkRegistry().getAllLinks(link.getSource());
            links.addAll(getMindMapMapModel().getLinkRegistry().getAllLinks(link.getTarget()));
            for(int i = 0; i < links.size(); ++i) {
                MindMapArrowLinkModel foreign_link = (MindMapArrowLinkModel) links.get(i);
                if(NodeAlreadyVisited.add(foreign_link.getTarget())) {
                    arrowLinkPopup.add(new GotoLinkNodeAction(foreign_link.getTarget().toString(), foreign_link.getTarget())); 
                }
                if(NodeAlreadyVisited.add(foreign_link.getSource())) {
                    arrowLinkPopup.add(new GotoLinkNodeAction(foreign_link.getSource().toString(), foreign_link.getSource())); 
                }
            }
            return arrowLinkPopup;
        }
        return null;
    }


    //convenience methods
    private MindMapMapModel getMindMapMapModel() {
	return (MindMapMapModel)getController().getModel();
    }

    MindMapToolBar getToolBar() {
	return (MindMapToolBar)toolbar;
    }

    JToolBar getLeftToolBar() {
	return ((MindMapToolBar)toolbar).getLeftToolBar();
    }

    /**
     * Enabled/Disabled all actions that are dependent on
     * whether there is a map open or not.
     */
    protected void setAllActions(boolean enabled) {
    	undo.setEnabled(enabled);
		redo.setEnabled(enabled);
        edit.setEnabled(enabled);
        editLong.setEnabled(enabled);
        newChildWithoutFocus.setEnabled(enabled);
        newSibling.setEnabled(enabled);
        newPreviousSibling.setEnabled(enabled);
        newChild.setEnabled(enabled);
        remove.setEnabled(enabled);
        toggleFolded.setEnabled(enabled);
        toggleChildrenFolded.setEnabled(enabled);
        setLinkByTextField.setEnabled(enabled);
        setLinkByFileChooser.setEnabled(enabled);
        setImageByFileChooser.setEnabled(enabled);
        followLink.setEnabled(enabled);
        italic.setEnabled(enabled);
        bold.setEnabled(enabled);
        cloud.setEnabled(enabled);
        cloudColor.setEnabled(enabled);
        normalFont.setEnabled(enabled);
        nodeColor.setEnabled(enabled);
        edgeColor.setEnabled(enabled);
        removeLastIcon.setEnabled(enabled);
        removeAllIcons.setEnabled(enabled);
        for (int i=0; i<iconActions.size(); ++i) {          
            ((Action) iconActions.get(i)).setEnabled(enabled);
        }
		for (int i=0; i<nodeHookActions.size(); ++i) {          
			((Action) nodeHookActions.get(i)).setEnabled(enabled);
		}
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
        exportToHTML.setEnabled(enabled);
        importBranch.setEnabled(enabled);
        importLinkedBranch.setEnabled(enabled);
        importLinkedBranchWithoutRoot.setEnabled(enabled);
        // hooks:
        for(Iterator i=modeControllerHookActions.iterator(); i.hasNext();) {
            ModeControllerHookAction action = (ModeControllerHookAction) i.next();
            action.setEnabled(enabled);
        }
    }




    //
    //      Actions
    //_______________________________________________________________________________



    // Export and Import
    // _________________


    // This may later be moved to ControllerAdapter. So far there is no reason for it.
    protected class ExportToHTMLAction extends AbstractAction {
	MindMapController c;
	public ExportToHTMLAction(MindMapController controller) {
	    super(getText("export_to_html"));
	    c = controller; }
	public void actionPerformed(ActionEvent e) {
           File file = new File(c.getMindMapMapModel().getFile()+".html");
           if (c.getMindMapMapModel().saveHTML((MindMapNodeModel)c.getMindMapMapModel().getRoot(),file)) {
              loadURL(file.toString()); }}}

    protected class ExportBranchToHTMLAction extends AbstractAction {
	MindMapController c;
	public ExportBranchToHTMLAction(MindMapController controller) {
	    super(getText("export_branch_to_html"));
	    c = controller; }
	public void actionPerformed(ActionEvent e) {
           try {
              File file = File.createTempFile("tmm", ".html");
              if (c.getMindMapMapModel().saveHTML((MindMapNodeModel)getSelected(),file)) {
                 loadURL(file.toString()); }}
           catch (IOException ex) {}}}

    private class ExportBranchAction extends AbstractAction {
	ExportBranchAction() {
	    super(getText("export_branch"));
	}
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel node = (MindMapNodeModel)getSelected();

	    //if something is wrong, abort.
	    if (getMap() == null || node == null || node.isRoot()) {
		getFrame().err("Could not export branch.");
		return; }
	    //If the current map is not saved yet, save it first.
	    if (getMap().getFile() == null) {
		getFrame().out("You must save the current map first!");
		save(); }

	    //Open FileChooser to choose in which file the exported
	    //branch should be stored
	    JFileChooser chooser;
	    if (getMap().getFile().getParentFile() != null) {
               chooser = new JFileChooser(getMap().getFile().getParentFile()); }
            else {
               chooser = new JFileChooser(); }
	    //chooser.setLocale(currentLocale);
	    if (getFileFilter() != null) {
               chooser.addChoosableFileFilter(getFileFilter()); }
	    int returnVal = chooser.showSaveDialog(getSelected().getViewer());
	    if (returnVal==JFileChooser.APPROVE_OPTION) {
		File f = chooser.getSelectedFile();
		URL link;
		//Force the extension to be .mm
		String ext = Tools.getExtension(f.getName());
		if(!ext.equals("mm")) {
                   f = new File(f.getParent(),f.getName()+".mm"); }
		try {
                   link = f.toURL(); }
                catch (MalformedURLException ex) {
		    JOptionPane.showMessageDialog(getView(),"couldn't create valid URL!");
		    return; }

		//Now make a copy from the node, remove the node from the map and create a new
		//Map with the node as root, store the new Map, add the copy of the node to the parent,
		//and set a link from the copy to the new Map.

		MindMapNodeModel parent = (MindMapNodeModel)node.getParentNode();
		MindMapNodeModel newNode = new MindMapNodeModel(node.toString(),getFrame());
		getMindMapMapModel().removeNodeFromParent(node);
		node.setParent(null);
		MindMapMapModel map = new MindMapMapModel(node,getFrame());
		if (getMindMapMapModel().getFile() != null) {
		    try{
			//set a link from the new root to the old map
                       map.setLink(node, Tools.toRelativeURL(f.toURL(), getMindMapMapModel().getFile().toURL())); }
                    catch(MalformedURLException ex) { }}
		map.save(f);

		getMindMapMapModel().insertNodeInto(newNode, parent, 0);
		try {
		    String linkString = Tools.toRelativeURL(getMindMapMapModel().getFile().toURL(), f.toURL());
		    getMindMapMapModel().setLink(newNode,linkString); }
                catch (MalformedURLException ex) {}
		getMindMapMapModel().save(getMindMapMapModel().getFile()); }}}

    private class ImportBranchAction extends AbstractAction {
	ImportBranchAction() {
           super(getText("import_branch")); }
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if (parent == null) {
               return; }
            JFileChooser chooser = new JFileChooser();
            //chooser.setLocale(currentLocale);
            if (getFileFilter() != null) {
               chooser.addChoosableFileFilter(getFileFilter()); }
            int returnVal = chooser.showOpenDialog(getFrame().getContentPane());
            if (returnVal==JFileChooser.APPROVE_OPTION) {
               try {
                  MindMapNodeModel node = getMindMapMapModel().loadTree(chooser.getSelectedFile());
                  getMindMapMapModel().paste(node, parent);
 				  invokeHooksRecursively(node, getMindMapMapModel());
               }
               catch (Exception ex) {
                  handleLoadingException(ex); }}}}

    private class ImportLinkedBranchAction extends AbstractAction {
	ImportLinkedBranchAction() {
           super(getText("import_linked_branch")); }
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if (parent == null || parent.getLink() == null) {
               return; }
            URL absolute = null;
            try {
               String relative = parent.getLink();
               absolute = Tools.isAbsolutePath(relative) ? new File(relative).toURL() :
                  new URL(getMap().getFile().toURL(), relative); }
            catch (MalformedURLException ex) {
               JOptionPane.showMessageDialog(getView(),"Couldn't create valid URL for:"+getMap().getFile());
               ex.printStackTrace();
               return; }
            try {
               MindMapNodeModel node = getMindMapMapModel().loadTree(new File(absolute.getFile()));
               getMindMapMapModel().paste(node, parent); 
			   invokeHooksRecursively(node, getMindMapMapModel());
            }
            catch (Exception ex) {
               handleLoadingException(ex); }}}

    /**
     * This is exactly the opposite of exportBranch.
     */
    private class ImportLinkedBranchWithoutRootAction extends AbstractAction {
	ImportLinkedBranchWithoutRootAction() {
           super(getText("import_linked_branch_without_root")); }
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel parent = (MindMapNodeModel)getSelected();
	    if (parent == null || parent.getLink() == null) {
               return; }
            URL absolute = null;
            try {
               String relative = parent.getLink();
               absolute = Tools.isAbsolutePath(relative) ? new File(relative).toURL() :
                  new URL(getMap().getFile().toURL(), relative); }
            catch (MalformedURLException ex) {
               JOptionPane.showMessageDialog(getView(),"Couldn't create valid URL.");
               return; }
            try {
               MindMapNodeModel node = getMindMapMapModel().loadTree(new File(absolute.getFile()));
               for (ListIterator i = node.childrenUnfolded();i.hasNext();) {
                  	MindMapNodeModel importNode = (MindMapNodeModel)i.next();
					getMindMapMapModel().paste(importNode, parent);
					invokeHooksRecursively(importNode, getMindMapMapModel());
                  }
               }
               //getModel().setLink(parent, null); }
            catch (Exception ex) {
               handleLoadingException(ex); }}}

    private class ImportExplorerFavoritesAction extends AbstractAction {
	ImportExplorerFavoritesAction() { super(getText("import_explorer_favorites")); }
	public void actionPerformed(ActionEvent e) {
           JFileChooser chooser = new JFileChooser();
           chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           chooser.setDialogTitle(getText("select_favorites_folder"));
           int returnVal = chooser.showOpenDialog(getFrame().getContentPane());
           if (returnVal == JFileChooser.APPROVE_OPTION) {
              File folder = chooser.getSelectedFile();
              getFrame().out("Importing Favorites ...");
              //getFrame().repaint(); // Refresh the frame, namely hide dialog and show status
              //getView().updateUI();
              // Problem: the frame should be refreshed here, but I don't know how to do it
              getMindMapMapModel().importExplorerFavorites(folder,getSelected(),/*redisplay=*/true);
              getFrame().out("Favorites imported."); }}}

    private class ImportFolderStructureAction extends AbstractAction {
	ImportFolderStructureAction() { super(getText("import_folder_structure")); }
	public void actionPerformed(ActionEvent e) {
           JFileChooser chooser = new JFileChooser();
           chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
           chooser.setDialogTitle(getText("select_folder_for_importing"));
           int returnVal = chooser.showOpenDialog(getFrame().getContentPane());
           if (returnVal == JFileChooser.APPROVE_OPTION) {
              File folder = chooser.getSelectedFile();
              getFrame().out("Importing folder structure ...");
              //getFrame().repaint(); // Refresh the frame, namely hide dialog and show status
              //getView().updateUI();
              // Problem: the frame should be refreshed here, but I don't know how to do it
              getMindMapMapModel().importFolderStructure(folder,getSelected(),/*redisplay=*/true);
              getFrame().out("Folder structure imported."); }}}


    // Color
    // __________________

    private class NodeColorAction extends AbstractAction {
       NodeColorAction() { super(getText("node_color")); }
       public void actionPerformed(ActionEvent e) {
          Color color = Controller.showCommonJColorChooserDialog(getView().getSelected(),"Choose Node Color:",getSelected().getColor() );
          if (color==null) {
             return; }
          for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
             MindMapNodeModel selected = (MindMapNodeModel)it.next();
             getMindMapMapModel().setNodeColor(selected, color); }}}

    private class EdgeColorAction extends AbstractAction {
	EdgeColorAction() { super(getText("edge_color")); }
	public void actionPerformed(ActionEvent e) {
           Color color = Controller.showCommonJColorChooserDialog(getView().getSelected(),"Choose Edge Color:",getSelected().getEdge().getColor());
           if (color==null) return;
           for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
              MindMapNodeModel selected = (MindMapNodeModel)it.next();
              getMindMapMapModel().setEdgeColor(selected,color); }}}


    private class CloudColorAction extends AbstractAction {
	CloudColorAction() { 
        super(getText("cloud_color"), new ImageIcon(getResource("images/Colors24.gif"))); 
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.NAME));}
	public void actionPerformed(ActionEvent e) {
        Color selectedColor = null;
        if(getSelected().getCloud() != null)
            selectedColor = getSelected().getCloud().getColor();
           Color color = Controller.showCommonJColorChooserDialog(getView().getSelected(),"Choose Cloud Color:",selectedColor);
           if (color==null) return;
           for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
              MindMapNodeModel selected = (MindMapNodeModel)it.next();
              getMindMapMapModel().setCloudColor(selected,color); }}}


    protected class ColorArrowLinkAction extends AbstractAction {
        MindMapNode source;
        MindMapArrowLinkModel arrowLink;
        public ColorArrowLinkAction(MindMapNode source, MindMapArrowLinkModel arrowLink) {
            super(getText("arrow_link_color"), new ImageIcon(getResource("images/Colors24.gif")));
            this.source = source;
            this.arrowLink = arrowLink;
        }

        public void actionPerformed(ActionEvent e) {
            Color selectedColor = arrowLink.getColor();
            Color color = Controller.showCommonJColorChooserDialog(getView().getSelected(),(String) this.getValue(Action.NAME),selectedColor);
            if (color==null) return;
            getMindMapMapModel().setArrowLinkColor(source, arrowLink, color); 
        }
    }
    


    // Icons
    // __________________

    public class IconAction extends AbstractAction {
        public MindIcon icon;
        public IconAction(MindIcon _icon) {
            super(_icon.getDescription(getFrame()), _icon.getIcon(getFrame()));
            putValue(Action.SHORT_DESCRIPTION, _icon.getDescription(getFrame()));
            this.icon = _icon;
        };
        
        public void actionPerformed(ActionEvent e) {
           for (ListIterator it = getSelecteds().listIterator();it.hasNext();) {
              MindMapNodeModel selected = (MindMapNodeModel)it.next();
              (getMindMapMapModel()).addIcon(selected, icon); 
            }
        };
    }

    // ArrowLinks
    // __________________

    protected class RemoveArrowLinkAction extends AbstractAction {
        MindMapNode source;
        MindMapArrowLinkModel arrowLink;
        public RemoveArrowLinkAction(MindMapNode source, MindMapArrowLinkModel arrowLink) {
            super(getText("remove_arrow_link"), new ImageIcon(getResource("images/edittrash.png")));
            this.source = source;
            this.arrowLink = arrowLink;
        }

        public void actionPerformed(ActionEvent e) {
            getMindMapMapModel().removeReference(source, arrowLink);
        }
    }

    protected class ChangeArrowsInArrowLinkAction extends AbstractAction {
        MindMapNode source;
        MindMapArrowLinkModel arrowLink;
        boolean hasStartArrow;
        boolean hasEndArrow;
        public ChangeArrowsInArrowLinkAction(String text, String iconPath, MindMapNode source, MindMapArrowLinkModel arrowLink, boolean hasStartArrow, boolean hasEndArrow) {
            super("", iconPath != null ? new ImageIcon(getResource(iconPath)) : null);
            this.source = source;
            this.arrowLink = arrowLink;
            this.hasStartArrow = hasStartArrow;
            this.hasEndArrow = hasEndArrow;
        }

        public void actionPerformed(ActionEvent e) {
            getMindMapMapModel().changeArrowsOfArrowLink(source, arrowLink, hasStartArrow, hasEndArrow);
        }
    }
    

    // Edge width
    // __________________

    private String getWidthTitle(int width) {
       if (width==EdgeAdapter.WIDTH_PARENT)
          return getText("edge_width_parent");
       if (width==EdgeAdapter.WIDTH_THIN)
          return getText("edge_width_thin");
       return Integer.toString(width); }

    private class EdgeWidthAction extends AbstractAction {
       int width;
       EdgeWidthAction(int width) {
          super(getWidthTitle(width));
          this.width = width; }
       public void actionPerformed(ActionEvent e) {
          for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
             MindMapNodeModel selected = (MindMapNodeModel)it.next();
             getMindMapMapModel().setEdgeWidth(selected,width); }}}


    // Miscelaneous
    // _________________


    private class JoinNodesAction extends AbstractAction {
	JoinNodesAction() { super(getText("join_nodes")); }
	public void actionPerformed(ActionEvent e) {
           ((MindMapMapModel)getView().getModel()).joinNodes(); }}

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() { super(getText("follow_link")); }
	public void actionPerformed(ActionEvent e) {
           loadURL(); }}

    private class ForkAction extends AbstractAction {
       ForkAction() { super(getText(MindMapNode.STYLE_FORK)); }
       public void actionPerformed(ActionEvent e) {
          for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
             MindMapNodeModel selected = (MindMapNodeModel)it.next();
             getMindMapMapModel().setNodeStyle(selected, MindMapNode.STYLE_FORK); }}}

    private class BubbleAction extends AbstractAction {
	BubbleAction() { super(getText(MindMapNode.STYLE_BUBBLE)); }
       public void actionPerformed(ActionEvent e) {
          for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
             MindMapNodeModel selected = (MindMapNodeModel)it.next();
             getMindMapMapModel().setNodeStyle(selected, MindMapNode.STYLE_BUBBLE); }}}

    private class EdgeStyleAction extends AbstractAction {
	String style;
	EdgeStyleAction(String style) {
	    super(getText(style));
            this.style = style; }	
       public void actionPerformed(ActionEvent e) {          
          for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
             MindMapNodeModel selected = (MindMapNodeModel)it.next();
             getMindMapMapModel().setEdgeStyle(selected, style); }}}

    private class ApplyPatternAction extends AbstractAction {
        StylePattern pattern;
	ApplyPatternAction(StylePattern pattern) {
	    super(pattern.getName());
	    this.pattern=pattern; }
	public void actionPerformed(ActionEvent e) {
	    for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)it.next();
                ((MindMapMapModel)getMindMapMapModel()).applyPattern(selected, pattern); }}}




    // Nonaction classes
    // ________________________________________________________________________

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
