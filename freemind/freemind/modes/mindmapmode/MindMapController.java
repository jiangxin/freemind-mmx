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
/*$Id: MindMapController.java,v 1.35.14.11.2.1.2.11 2006-03-02 21:00:54 dpolivaev Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Component;
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
import javax.swing.ButtonGroup;
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
import freemind.controller.actions.generated.instance.MenuRadioAction;
import freemind.controller.actions.generated.instance.MenuSeparator;
import freemind.controller.actions.generated.instance.MenuStructure;
import freemind.controller.actions.generated.instance.MenuSubmenu;
import freemind.controller.attributes.AssignAttributeDialog;
import freemind.extensions.HookFactory;
import freemind.extensions.HookRegistration;
import freemind.extensions.HookFactory.RegistrationContainer;
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
import freemind.modes.actions.FindAction;
import freemind.modes.actions.GotoLinkNodeAction;
import freemind.modes.actions.IconAction;
import freemind.modes.actions.ImportExplorerFavoritesAction;
import freemind.modes.actions.ImportFolderStructureAction;
import freemind.modes.actions.NewMapAction;
import freemind.modes.actions.NewPreviousSiblingAction;
import freemind.modes.actions.NewSiblingAction;
import freemind.modes.actions.NodeGeneralAction;
import freemind.modes.actions.NodeHookAction;
import freemind.modes.actions.RemoveArrowLinkAction;
import freemind.modes.actions.SingleNodeOperation;
import freemind.modes.attributes.AttributeTableLayoutModel;





public class MindMapController extends ControllerAdapter {

    protected class AssignAttributesAction extends AbstractAction {
        public AssignAttributesAction() {
            super(getText("attributes_assign_dialog"));
        }
        public void actionPerformed(ActionEvent e) {
             if(assignAttributeDialog == null){
                assignAttributeDialog = new AssignAttributeDialog(getView());
            }
            assignAttributeDialog.setVisible(true);
        }
     }
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
   public Action editAttributes = new EditAttributesAction();
   protected  AssignAttributeDialog assignAttributeDialog = null;
   public Action assignAttributes = new AssignAttributesAction();
   public Action newSibling = new NewSiblingAction(this);
   public Action newPreviousSibling = new NewPreviousSiblingAction(this);
   public Action setLinkByFileChooser = new SetLinkByFileChooserAction();
   public Action setImageByFileChooser = new SetImageByFileChooserAction();
   public Action followLink = new FollowLinkAction();
   public Action exportBranch = new ExportBranchAction();
   public Action importBranch = new ImportBranchAction();
   public Action importLinkedBranch = new ImportLinkedBranchAction();
   public Action importLinkedBranchWithoutRoot = new ImportLinkedBranchWithoutRootAction();

   public Action showAttributeManagerAction = null;    
   

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
    showAttributeManagerAction = getController().showAttributeManagerAction;
	if(logger == null) {
		logger = getFrame().getLogger(this.getClass().getName());
	}
	logger.info("createIconActions");
        // icon actions:
        createIconActions();
    	logger.info("createNodeHookActions");
        //node hook actions:
        createNodeHookActions();

    	logger.info("mindmap_menus");
        // load menus:
        try {
            InputStream in;
            in = this.getFrame().getResource("mindmap_menus.xml").openStream();
            mMenuStructure = updateMenusFromXml(in);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    	logger.info("MindMapPopupMenu");
        popupmenu = new MindMapPopupMenu(this);
    	logger.info("MindMapToolBar");
        toolbar = new MindMapToolBar(this);
    	logger.info("setAllActions");
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
		super.startupController();
		HookFactory hookFactory = getFrame().getHookFactory();
		List pluginRegistratios = hookFactory.getRegistrations(this.getClass());
		logger.info("mScheduledActions are executed: "
				+ pluginRegistratios.size());
		for (Iterator i = pluginRegistratios.iterator(); i.hasNext();) {
			// call constructor:
			try {
				HookFactory.RegistrationContainer container = (RegistrationContainer) i
						.next();
				Class registrationClass = container.hookRegistrationClass;
				Constructor hookConstructor = registrationClass
						.getConstructor(new Class[] { ModeController.class,
								MindMap.class });
				HookRegistration registrationInstance = (HookRegistration) hookConstructor
						.newInstance(new Object[] { this, getMap() });
				// register the instance to enable basePlugins.
				hookFactory.registerRegistrationContainer(container,
						registrationInstance);
				registrationInstance.register();
				pRegistrations.add(registrationInstance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


    public void shutdownController() {
        super.shutdownController();
        for (Iterator i = pRegistrations.iterator(); i.hasNext();) {
            HookRegistration registrationInstance = (HookRegistration) i.next();
            registrationInstance.deRegister();
        }
    }
    
	public MapAdapter newModel() {
       return new MindMapMapModel(getFrame(), this); }

    private void createIconActions() {
        Vector iconNames = MindIcon.getAllIconNames();
        for ( int i = 0 ; i < iconNames.size(); ++i ) {
            String iconName = ((String) iconNames.get(i));
            MindIcon myIcon     = MindIcon.factory(iconName);
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

    // fc, 14.12.2004: changes, such that different models can be used:
    private NewNodeCreator myNewNodeCreator = null;
    
    public interface NewNodeCreator {
        MindMapNode createNode(Object userObject, MindMap map);
    }

    public class DefaultMindMapNodeCreator implements NewNodeCreator {

        public MindMapNode createNode(Object userObject, MindMap map) {
            return new MindMapNodeModel(userObject, getFrame(), map);
        }
        
    }
    
    public void setNewNodeCreator(NewNodeCreator creator) {
        myNewNodeCreator = creator;
    }
    
    public MindMapNode newNode(Object userObject, MindMap map) {
        // singleton default:
        if (myNewNodeCreator == null) {
            myNewNodeCreator = new DefaultMindMapNodeCreator();
        }
        return myNewNodeCreator.createNode(userObject, map);
    }

    // fc, 14.12.2004: end "different models" change
    
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
		String formatMenuString = MenuBar.FORMAT_MENU;
        createPatternSubMenu(holder, formatMenuString);


//        editMenu.add(getIconMenu());
		addIconsToMenu(holder, MenuBar.INSERT_MENU + "icons");

    }

    public void addIconsToMenu(StructuredMenuHolder holder, String iconMenuString) {
		JMenu iconMenu = holder.addMenu(new JMenu(getText("icon_menu")), iconMenuString+"/.") ;
		holder.addAction(removeLastIconAction, iconMenuString+"/removeLastIcon");
		holder.addAction(removeAllIconsAction, iconMenuString+"/removeAllIcons");
		holder.addSeparator(iconMenuString);
		for (int i=0; i<iconActions.size(); ++i) {          
			   JMenuItem item = holder.addAction((Action) iconActions.get(i), iconMenuString+"/"+i);
		}
	}

	/**
     * @param holder
     * @param formatMenuString
     */
    public JMenu createPatternSubMenu(StructuredMenuHolder holder, String formatMenuString) {
        JMenu extensionMenu = holder.addMenu(new JMenu(getText("extension_menu")), formatMenuString+"patterns/.");
        for (int i = 0; i < patterns.length; ++i) {
            JMenuItem item =
                holder.addAction(
                    patterns[i],
                    formatMenuString + "patterns/" + i);
            item.setAccelerator(
                KeyStroke.getKeyStroke(
                    getFrame().getProperty(
                        "keystroke_apply_pattern_" + (i + 1))));
        }
        return extensionMenu;
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
        ButtonGroup buttonGroup = null;        
    	for (Iterator i = list.iterator(); i.hasNext();) {
            Object obj = (Object) i.next();
            if(obj instanceof MenuCategoryBase) {
				MenuCategoryBase cat = (MenuCategoryBase) obj;
            	String newCategory = categoryCopy+"/"+cat.getName();
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
					} else if (obj instanceof MenuRadioAction) {                        
                        final JRadioButtonMenuItem item = (JRadioButtonMenuItem) addRadioItem(holder, theCategory, theAction, keystroke, ((MenuRadioAction)obj).isSelected());
                        if(buttonGroup == null)
                            buttonGroup = new ButtonGroup();
                        buttonGroup.add(item);

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
            JRadioButtonMenuItem itemnt = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction(this, "forward", "images/arrow-mode-forward.gif",link, false, true) );
            JRadioButtonMenuItem itemtn = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction(this, "backward", "images/arrow-mode-backward.gif",link, true, false) );
            JRadioButtonMenuItem itemtt = new JRadioButtonMenuItem( new ChangeArrowsInArrowLinkAction(this, "both", "images/arrow-mode-both.gif",link, true, true) );
            itemnn.setText(null);
            itemnt.setText(null);
            itemtn.setText(null);
            itemtt.setText(null);
            arrowLinkPopup.add( itemnn );
            arrowLinkPopup.add( itemnt );
            arrowLinkPopup.add( itemtn );
            arrowLinkPopup.add( itemtt );
            // select the right one:
            boolean a = !link.getStartArrow().equals("None");
            boolean b = !link.getEndArrow().equals("None");
            itemtt.setSelected(a&&b);
            itemnt.setSelected(!a&&b);
            itemtn.setSelected(a&&!b);
            itemnn.setSelected(!a&&!b);

            arrowLinkPopup.addSeparator();
            
            arrowLinkPopup.add(new GotoLinkNodeAction(this, link.getSource())); 
            arrowLinkPopup.add(new GotoLinkNodeAction(this, link.getTarget())); 

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
                    arrowLinkPopup.add(new GotoLinkNodeAction(this, foreign_link.getTarget())); 
                }
                if(NodeAlreadyVisited.add(foreign_link.getSource())) {
                    arrowLinkPopup.add(new GotoLinkNodeAction(this, foreign_link.getSource())); 
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

    Component getLeftToolBar() {
	return ((MindMapToolBar)toolbar).getLeftToolBar();
    }

    /**
     * Enabled/Disabled all actions that are dependent on
     * whether there is a map open or not.
     */
    protected void setAllActions(boolean enabled) {
        super.setAllActions(enabled);
        // own actions
        increaseNodeFont.setEnabled(enabled);
        decreaseNodeFont.setEnabled(enabled);
        exportBranch.setEnabled(enabled);
        exportBranchToHTML.setEnabled(enabled);
        editLong.setEnabled(enabled);
        newSibling.setEnabled(enabled);
        newPreviousSibling.setEnabled(enabled);
        setLinkByFileChooser.setEnabled(enabled);
        setImageByFileChooser.setEnabled(enabled);
        followLink.setEnabled(enabled);
        for (int i=0; i<iconActions.size(); ++i) {          
            ((Action) iconActions.get(i)).setEnabled(enabled);
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
            MindMapNodeModel node = (MindMapNodeModel) getSelected();

            //if something is wrong, abort.
            if (getMap() == null || node == null || node.isRoot()) {
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
            int returnVal = chooser.showSaveDialog(getSelected().getViewer());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File chosenFile = chooser.getSelectedFile();
                URL link;
                //Force the extension to be .mm
                String ext = Tools.getExtension(chosenFile.getName());
                if (!ext.equals("mm")) {
                    chosenFile = new File(chosenFile.getParent(), chosenFile.getName() + ".mm");
                }
                try {
                    link = chosenFile.toURL();
                } catch (MalformedURLException ex) {
                    JOptionPane.showMessageDialog(getView(),
                            "couldn't create valid URL!");
                    return;
                }

                //Now make a copy from the node, remove the node from the map
                // and create a new
                //Map with the node as root, store the new Map, add the copy of
                // the node to the parent,
                //and set a link from the copy to the new Map.

                MindMapNodeModel parent = (MindMapNodeModel) node
                        .getParentNode();
                if (getMindMapMapModel().getFile() != null) {
                    try {
                        //set a link from the new root to the old map
                        String linkToNewMapString = Tools.toRelativeURL(chosenFile.toURL(),
                                getMindMapMapModel().getFile().toURL());
                        setLink(node, linkToNewMapString);
                    } catch (MalformedURLException ex) {
                    }
                }
                int nodePosition = parent.getChildPosition(node);
                deleteNode(node);
                // save node:
                node.setParent(null);
                MindMapMapModel map = new MindMapMapModel(getFrame(), MindMapController.this);
                node.setMap(map);
                map.setRoot(node);
                map.save(chosenFile);
                // new node instead:
                MindMapNode newNode = addNewNode(parent, nodePosition, node.isLeft());
                setNodeText(newNode, node.getText());

                try {
                    String linkString = Tools.toRelativeURL(
                            getMindMapMapModel().getFile().toURL(), chosenFile.toURL());
                    setLink(newNode, linkString);
                } catch (MalformedURLException ex) {
                }
                // map should not be save automatically!!
                //getMindMapMapModel().save(getMindMapMapModel().getFile());
            }
        }
    }

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

    private class FollowLinkAction extends AbstractAction {
	FollowLinkAction() { super(getText("follow_link")); }
	public void actionPerformed(ActionEvent e) {
           loadURL(); }}

/*
 MindMapController.java
    private class NodeViewStyleAction extends AbstractAction {
       NodeViewStyleAction(final String style) { super(getText(style)); m_style = style; }
       public void actionPerformed(ActionEvent e) {
          for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
             MindMapNodeModel selected = (MindMapNodeModel)it.next();
             getModel().setNodeStyle(selected, m_style); }}
       private String m_style;}

    private class EdgeStyleAction extends AbstractAction {
	String style;
	EdgeStyleAction(String style) {
	    super(getText(style));
            this.style = style; }	
       public void actionPerformed(ActionEvent e) {          
          for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
             MindMapNodeModel selected = (MindMapNodeModel)it.next();
             getModel().setEdgeStyle(selected, style); }}}

    private class ApplyPatternAction extends AbstractAction {
        StylePattern pattern;
	ApplyPatternAction(StylePattern pattern) {
	    super(pattern.getName());
	    this.pattern=pattern; }
	public void actionPerformed(ActionEvent e) {
	    for(ListIterator it = getSelecteds().listIterator();it.hasNext();) {
		MindMapNodeModel selected = (MindMapNodeModel)it.next();
                ((MindMapMapModel)getModel()).applyPattern(selected, pattern); }}}




    // Nonaction classes
    // ________________________________________________________________________

=======
*/
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

    public void mapChanged(MindMap newMap) {
        if(assignAttributeDialog != null){
            assignAttributeDialog.mapChanged(getView());
        }
        
    }


}
