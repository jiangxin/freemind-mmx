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
/*$Id: MindMapController.java,v 1.35.10.34 2004-10-09 22:11:32 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
import javax.xml.bind.Unmarshaller;

import freemind.common.JaxbTools;
import freemind.controller.MenuBar;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.MenuActionBase;
import freemind.controller.actions.generated.instance.MenuCategoryBase;
import freemind.controller.actions.generated.instance.MenuCheckedAction;
import freemind.controller.actions.generated.instance.MenuSeparator;
import freemind.controller.actions.generated.instance.MenuStructure;
import freemind.controller.actions.generated.instance.MenuSubmenu;
import freemind.extensions.HookFactory;
import freemind.extensions.HookRegistration;
import freemind.main.Tools;
import freemind.modes.ControllerAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.actions.ChangeArrowsInArrowLinkAction;
import freemind.modes.actions.ColorArrowLinkAction;
import freemind.modes.actions.IconAction;
import freemind.modes.actions.NewMapAction;
import freemind.modes.actions.NewPreviousSiblingAction;
import freemind.modes.actions.NewSiblingAction;
import freemind.modes.actions.NodeBackgroundColorAction;
import freemind.modes.actions.NodeGeneralAction;
import freemind.modes.actions.NodeHookAction;
import freemind.modes.actions.RemoveArrowLinkAction;
import freemind.modes.actions.SingleNodeOperation;





public class MindMapController extends ControllerAdapter {

	private static Logger logger;
	private Vector hookActions;
	/** Stores the menu items belonging to the given action. */
	private HashMap actionToMenuPositions;
	//    Mode mode;
    private MindMapPopupMenu popupmenu;
    //private JToolBar toolbar;
    private MindMapToolBar toolbar;
    private boolean addAsChildMode = false;

   public Action newMap = new NewMapAction(this, this);
   public Action open = new OpenAction(this);
   public Action save = new SaveAction(this);
   public Action saveAs = new SaveAsAction(this);
   public Action exportToHTML = new ExportToHTMLAction(this);
   public Action exportBranchToHTML = new ExportBranchToHTMLAction(this);

   public Action editLong = new EditLongAction();
   public Action newSibling = new NewSiblingAction(this);
   public Action newPreviousSibling = new NewPreviousSiblingAction(this);
   public Action setLinkByFileChooser = new SetLinkByFileChooserAction();
   public Action setImageByFileChooser = new SetImageByFileChooserAction();
   public Action setLinkByTextField = new SetLinkByTextFieldAction();
   public Action followLink = new FollowLinkAction();
   public Action exportBranch = new ExportBranchAction();
   public Action importBranch = new ImportBranchAction();
   public Action importLinkedBranch = new ImportLinkedBranchAction();
   public Action importLinkedBranchWithoutRoot = new ImportLinkedBranchWithoutRootAction();
   public Action importExplorerFavorites = new ImportExplorerFavoritesAction();
   public Action importFolderStructure = new ImportFolderStructureAction();
   public Action joinNodes = new JoinNodesAction();
   public Action find = new FindAction();
   public Action findNext = new FindNextAction();


//    public Action normalFont = new NodeGeneralAction (this, "normal", "images/Normal24.gif",
//       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
//          map.setNormalFont(node); }});
    public Action increaseNodeFont = new NodeGeneralAction (this, "increase_node_font_size", null,
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
           increaseFontSize(node, 1);
    }});
    public Action decreaseNodeFont = new NodeGeneralAction (this, "decrease_node_font_size", null,
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
           increaseFontSize(node, -1);
    }});

    // Extension Actions
    public Vector iconActions = new Vector(); //fc




    FileFilter filefilter = new MindMapFilter();

    private MenuStructure mMenuStructure;
    private List pRegistrations;

    public MindMapController(Mode mode) {
	super(mode);
	if(logger == null) {
		logger = getFrame().getLogger(this.getClass().getName());
	}
        // icon actions:
        createIconActions();
        //node hook actions:
        createNodeHookActions();

        // load menus:
        try {
            InputStream in;
            in = this.getFrame().getResource("mindmap_menus.xml").openStream();
            mMenuStructure = updateMenusFromXml(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        popupmenu = new MindMapPopupMenu(this);
        toolbar = new MindMapToolBar(this);
        setAllActions(false);

        // addAsChildMode (use old model of handling CtrN) (PN)
        addAsChildMode =
            Tools.safeEquals(getFrame().getProperty("add_as_child"), "true");
        pRegistrations = new Vector();
    }

    /** This method is called after and before a change of the map module.
     * Use it to perform the actions that cannot be performed at creation time.
     * 
     */
    public void startupController() {
        List pluginRegistratios = getFrame().getHookFactory().getRegistrations();
        logger.info("mScheduledActions are executed: "+pluginRegistratios.size());
        for (Iterator i = pluginRegistratios.iterator(); i.hasNext();) {
            // call constructor:
			try {
	            Class registrationClass = (Class) i.next();
                Constructor hookConstructor = registrationClass
                        .getConstructor(new Class[] { ModeController.class,
                                MindMap.class });
                HookRegistration registrationInstance = (HookRegistration) hookConstructor
                        .newInstance(new Object[] { this, getMap() });
                registrationInstance.register();
                pRegistrations.add(registrationInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void shutdownController() {
        for (Iterator i = pRegistrations.iterator(); i.hasNext();) {
            HookRegistration registrationInstance = (HookRegistration) i.next();
            registrationInstance.deRegister();
        }
    }
    
	public MapAdapter newModel() {
       return new MindMapMapModel(getFrame()); }

    private void createIconActions() {
        Vector iconNames = MindIcon.getAllIconNames();
        for ( int i = 0 ; i < iconNames.size(); ++i ) {
            String iconName = ((String) iconNames.get(i));
            MindIcon myIcon     = new MindIcon(iconName);
            IconAction myAction = new IconAction(this, myIcon,removeLastIconAction);
            iconActions.add(myAction);
        }
    }

	/**
	 * 
	 */
	private void createNodeHookActions() {
		actionToMenuPositions = new HashMap();
        if (hookActions == null) {
            hookActions = new Vector();
            // HOOK TEST
            HookFactory factory = getFrame().getHookFactory();
            List list = factory.getPossibleNodeHooks(this.getClass());
            for (Iterator i = list.iterator(); i.hasNext();) {
                String desc = (String) i.next();
                // create hook action. 
                NodeHookAction action = new NodeHookAction(desc, this);
                factory.decorateAction(desc, action);
                actionToMenuPositions.put(action, factory.getHookMenuPositions(desc));
                hookActions.add(action);
            }
            List hooks =
                factory.getPossibleModeControllerHooks(this.getClass());
            for (Iterator i = hooks.iterator(); i.hasNext();) {
                String desc = (String) i.next();
                ModeControllerHookAction action =
                    new ModeControllerHookAction(desc, this);
                factory.decorateAction(desc, action);
			   actionToMenuPositions.put(action, factory.getHookMenuPositions(desc));
                hookActions.add(action);
            }
            //HOOK TEST END       
	    }
	}


    public FileFilter getFileFilter() {
       return filefilter; }

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



	/**
	 * @param holder
	 */
    public void updateMenus(StructuredMenuHolder holder) {

		processMenuCategory(holder, mMenuStructure.getMenuCategory(), ""); /*MenuBar.MENU_BAR_PREFIX*/
		// add hook actions to this holder.
		// hooks, fc, 1.3.2004:
		for (int i = 0; i < hookActions.size(); ++i) {
			Action hookAction = (Action) hookActions.get(i);
			List positions = (List) actionToMenuPositions.get(hookAction);
			for (Iterator j = positions.iterator(); j.hasNext();) {
                String pos = (String) j.next();
                holder.addAction(hookAction, pos);
            }
		}
		// update popup and toolbar:
		popupmenu.update(holder);
		toolbar.update(holder);
        
//		editMenu.add(getExtensionMenu());
		JMenu extensionMenu = holder.addMenu(new JMenu(getText("extension_menu")), MenuBar.FORMAT_MENU+"patterns/.");
        for (int i = 0; i < patterns.length; ++i) {
            JMenuItem item =
                holder.addAction(
                    patterns[i],
                    MenuBar.FORMAT_MENU + "patterns/" + i);
            item.setAccelerator(
                KeyStroke.getKeyStroke(
                    getFrame().getProperty(
                        "keystroke_apply_pattern_" + (i + 1))));
        }


//        editMenu.add(getIconMenu());
		String iconMenuString = MenuBar.INSERT_MENU + "icons";
		JMenu iconMenu = holder.addMenu(new JMenu(getText("icon_menu")), iconMenuString+"/.") ;
		holder.addAction(removeLastIconAction, iconMenuString+"/removeLastIcon");
		holder.addAction(removeAllIconsAction, iconMenuString+"/removeAllIcons");
		holder.addSeparator(iconMenuString);
		for (int i=0; i<iconActions.size(); ++i) {          
			   JMenuItem item = holder.addAction((Action) iconActions.get(i), iconMenuString+"/"+i);
		}

    }

    public MenuStructure updateMenusFromXml(InputStream in) {
        // get from resources:
        try {
        	Unmarshaller unmarshaller = JaxbTools.getInstance().createUnmarshaller();
        	unmarshaller.setValidating(true);
        	MenuStructure menus = (MenuStructure) unmarshaller.unmarshal(in);
        	return menus;
		} catch (JAXBException e) {
        	logger.severe(e.getCause() + e.getLocalizedMessage() + e.getMessage());
        	e.printStackTrace();
        	throw new IllegalArgumentException("Menu structure could not be read.");
        }
    }


    /**
     * @param type
     */
    public void processMenuCategory(StructuredMenuHolder holder, List list, String category) {
		String categoryCopy = category;
		if(categoryCopy.endsWith("/")) {
			categoryCopy = categoryCopy.substring(0, categoryCopy.length()-1);
		}
    	for (Iterator i = list.iterator(); i.hasNext();) {
            Object obj = (Object) i.next();
            if(obj instanceof MenuCategoryBase) {
				MenuCategoryBase cat = (MenuCategoryBase) obj;
            	String newCategory = categoryCopy+"/"+cat.getName();
            	if (cat.isSetSeparatorBefore()) {
                    holder.addSeparator(categoryCopy);
                }
				holder.addCategory(newCategory);
                if(cat instanceof MenuSubmenu) {
                	MenuSubmenu submenu = (MenuSubmenu) cat;
                	holder.addMenu(new JMenu(getText(submenu.getNameRef())), newCategory+"/.");
                }
            	processMenuCategory(holder, cat.getMenuCategoryOrMenuSubmenuOrMenuAction(), newCategory);
            } else if( obj instanceof MenuActionBase ) {
				MenuActionBase action = (MenuActionBase) obj;            	
				String field = action.getField();
				String name = action.getName();
				if(name == null) {
					name = field;
				}
				String keystroke = action.getKeyRef();
				try {
					Action theAction = (Action) this.getClass().getField(field).get(this);
					String theCategory = categoryCopy+"/"+name;
					if (obj instanceof MenuCheckedAction) {
						addCheckBox(holder, theCategory, theAction, keystroke);
					} else {
						add(holder, theCategory, theAction, keystroke);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            } else if (obj instanceof MenuSeparator) {
            	holder.addSeparator(categoryCopy);
            } /* else exception */
         }
    }

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
            removeArrowLinkAction.setArrowLink(link);
            arrowLinkPopup.add(new RemoveArrowLinkAction(this, link));
            arrowLinkPopup.add(new ColorArrowLinkAction(this, link));
            arrowLinkPopup.addSeparator();
            /* The arrow state as radio buttons: */
            JRadioButtonMenuItem itemnn = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction(this, "none", "images/arrow-mode-none.gif",link, false, false) );
            arrowLinkPopup.add( itemnn );
            JRadioButtonMenuItem itemnt = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction(this, "forward", "images/arrow-mode-forward.gif",link, false, true) );
            arrowLinkPopup.add( itemnt );
            JRadioButtonMenuItem itemtn = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction(this, "backward", "images/arrow-mode-backward.gif",link, true, false) );
            arrowLinkPopup.add( itemtn );
            JRadioButtonMenuItem itemtt = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction(this, "both", "images/arrow-mode-both.gif",link, true, true) );
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
        newSibling.setEnabled(enabled);
        newPreviousSibling.setEnabled(enabled);
        newChild.setEnabled(enabled);
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
//        normalFont.setEnabled(enabled);
        nodeColor.setEnabled(enabled);
        edgeColor.setEnabled(enabled);
        removeLastIconAction.setEnabled(enabled);
        removeAllIconsAction.setEnabled(enabled);
        for (int i=0; i<iconActions.size(); ++i) {          
            ((Action) iconActions.get(i)).setEnabled(enabled);
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
		for (int i=0; i<hookActions.size(); ++i) {          
			((Action) hookActions.get(i)).setEnabled(enabled);
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
                  paste(node, parent);
 				  invokeHooksRecursively(node, getMindMapMapModel());
               }
               catch (Exception ex) {
                  handleLoadingException(ex); }}}}

    private class ImportLinkedBranchAction extends AbstractAction {
	ImportLinkedBranchAction() {
           super(getText("import_linked_branch")); }
	public void actionPerformed(ActionEvent e) {
	    MindMapNodeModel selected = (MindMapNodeModel)getSelected();
	    if (selected == null || selected.getLink() == null) {
	        JOptionPane.showMessageDialog(getView(),getText("import_linked_branch_no_link"));
            return; 
        }
            URL absolute = null;
            try {
               String relative = selected.getLink();
               absolute = Tools.isAbsolutePath(relative) ? new File(relative).toURL() :
                  new URL(getMap().getFile().toURL(), relative); }
            catch (MalformedURLException ex) {
               JOptionPane.showMessageDialog(getView(),"Couldn't create valid URL for:"+getMap().getFile());
               ex.printStackTrace();
               return; }
            try {
               MindMapNodeModel node = getMindMapMapModel().loadTree(new File(absolute.getFile()));
               paste(node, selected); 
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
	    MindMapNodeModel selected = (MindMapNodeModel)getSelected();
	    if (selected == null || selected.getLink() == null) {
	        JOptionPane.showMessageDialog(getView(),getText("import_linked_branch_no_link"));
            return;
        }
            URL absolute = null;
            try {
               String relative = selected.getLink();
               absolute = Tools.isAbsolutePath(relative) ? new File(relative).toURL() :
                  new URL(getMap().getFile().toURL(), relative); }
            catch (MalformedURLException ex) {
               JOptionPane.showMessageDialog(getView(),"Couldn't create valid URL.");
               return; }
            try {
               MindMapNodeModel node = getMindMapMapModel().loadTree(new File(absolute.getFile()));
               for (ListIterator i = node.childrenUnfolded();i.hasNext();) {
                  	MindMapNodeModel importNode = (MindMapNodeModel)i.next();
					paste(importNode, selected);
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


    private class JoinNodesAction extends AbstractAction {
	JoinNodesAction() { super(getText("join_nodes")); }
	public void actionPerformed(ActionEvent e) {
           ((MindMapMapModel)getView().getModel()).joinNodes(); }}

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() { super(getText("follow_link")); }
	public void actionPerformed(ActionEvent e) {
           loadURL(); }}

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
