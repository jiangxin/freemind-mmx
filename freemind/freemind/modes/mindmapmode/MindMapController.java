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
/*$Id: MindMapController.java,v 1.35.14.20 2006-03-19 20:18:30 christianfoltin Exp $*/

package freemind.modes.mindmapmode;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import freemind.common.XmlBindingTools;
import freemind.controller.MenuBar;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.generated.instance.MenuActionBase;
import freemind.controller.actions.generated.instance.MenuCategoryBase;
import freemind.controller.actions.generated.instance.MenuCheckedAction;
import freemind.controller.actions.generated.instance.MenuSeparator;
import freemind.controller.actions.generated.instance.MenuStructure;
import freemind.controller.actions.generated.instance.MenuSubmenu;
import freemind.controller.actions.generated.instance.Pattern;
import freemind.controller.actions.generated.instance.PatternIcon;
import freemind.controller.actions.generated.instance.TimeWindowConfigurationStorage;
import freemind.controller.actions.generated.instance.WindowConfigurationStorage;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.HookFactory;
import freemind.extensions.HookRegistration;
import freemind.extensions.ModeControllerHook;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.extensions.UndoEventReceiver;
import freemind.extensions.HookFactory.RegistrationContainer;
import freemind.main.ExampleFileFilter;
import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.ControllerAdapter;
import freemind.modes.EdgeAdapter;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMap;
import freemind.modes.MindMapArrowLink;
import freemind.modes.MindMapLink;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.NodeDownAction;
import freemind.modes.StylePatternFactory;
import freemind.modes.common.CommonNodeKeyListener;
import freemind.modes.common.GotoLinkNodeAction;
import freemind.modes.common.CommonNodeKeyListener.EditHandler;
import freemind.modes.common.actions.FindAction;
import freemind.modes.common.actions.NewMapAction;
import freemind.modes.common.actions.FindAction.FindNextAction;
import freemind.modes.common.listeners.CommonNodeMouseMotionListener;
import freemind.modes.mindmapmode.actions.AddArrowLinkAction;
import freemind.modes.mindmapmode.actions.AddLocalLinkAction;
import freemind.modes.mindmapmode.actions.ApplyPatternAction;
import freemind.modes.mindmapmode.actions.BoldAction;
import freemind.modes.mindmapmode.actions.ChangeArrowLinkEndPoints;
import freemind.modes.mindmapmode.actions.ChangeArrowsInArrowLinkAction;
import freemind.modes.mindmapmode.actions.CloudAction;
import freemind.modes.mindmapmode.actions.ColorArrowLinkAction;
import freemind.modes.mindmapmode.actions.CompoundActionHandler;
import freemind.modes.mindmapmode.actions.CopyAction;
import freemind.modes.mindmapmode.actions.CopySingleAction;
import freemind.modes.mindmapmode.actions.CutAction;
import freemind.modes.mindmapmode.actions.DeleteChildAction;
import freemind.modes.mindmapmode.actions.EdgeColorAction;
import freemind.modes.mindmapmode.actions.EdgeStyleAction;
import freemind.modes.mindmapmode.actions.EdgeWidthAction;
import freemind.modes.mindmapmode.actions.EditAction;
import freemind.modes.mindmapmode.actions.FontFamilyAction;
import freemind.modes.mindmapmode.actions.FontSizeAction;
import freemind.modes.mindmapmode.actions.IconAction;
import freemind.modes.mindmapmode.actions.ImportExplorerFavoritesAction;
import freemind.modes.mindmapmode.actions.ImportFolderStructureAction;
import freemind.modes.mindmapmode.actions.ItalicAction;
import freemind.modes.mindmapmode.actions.JoinNodesAction;
import freemind.modes.mindmapmode.actions.MindMapActions;
import freemind.modes.mindmapmode.actions.ModeControllerActionHandler;
import freemind.modes.mindmapmode.actions.MoveNodeAction;
import freemind.modes.mindmapmode.actions.NewChildAction;
import freemind.modes.mindmapmode.actions.NewPreviousSiblingAction;
import freemind.modes.mindmapmode.actions.NewSiblingAction;
import freemind.modes.mindmapmode.actions.NodeBackgroundColorAction;
import freemind.modes.mindmapmode.actions.NodeColorAction;
import freemind.modes.mindmapmode.actions.NodeColorBlendAction;
import freemind.modes.mindmapmode.actions.NodeGeneralAction;
import freemind.modes.mindmapmode.actions.NodeHookAction;
import freemind.modes.mindmapmode.actions.NodeStyleAction;
import freemind.modes.mindmapmode.actions.NodeUpAction;
import freemind.modes.mindmapmode.actions.PasteAction;
import freemind.modes.mindmapmode.actions.RedoAction;
import freemind.modes.mindmapmode.actions.RemoveAllIconsAction;
import freemind.modes.mindmapmode.actions.RemoveArrowLinkAction;
import freemind.modes.mindmapmode.actions.RemoveLastIconAction;
import freemind.modes.mindmapmode.actions.RevertAction;
import freemind.modes.mindmapmode.actions.SelectAllAction;
import freemind.modes.mindmapmode.actions.SelectBranchAction;
import freemind.modes.mindmapmode.actions.SetLinkByTextFieldAction;
import freemind.modes.mindmapmode.actions.SingleNodeOperation;
import freemind.modes.mindmapmode.actions.ToggleChildrenFoldedAction;
import freemind.modes.mindmapmode.actions.ToggleFoldedAction;
import freemind.modes.mindmapmode.actions.UnderlinedAction;
import freemind.modes.mindmapmode.actions.UndoAction;
import freemind.modes.mindmapmode.actions.NodeBackgroundColorAction.RemoveNodeBackgroundColorAction;
import freemind.modes.mindmapmode.actions.xml.ActionFactory;
import freemind.modes.mindmapmode.actions.xml.UndoActionHandler;
import freemind.modes.mindmapmode.hooks.MindMapHookFactory;
import freemind.modes.mindmapmode.listeners.MindMapMouseMotionManager;
import freemind.modes.mindmapmode.listeners.MindMapMouseWheelEventHandler;
import freemind.modes.mindmapmode.listeners.MindMapNodeDropListener;
import freemind.modes.mindmapmode.listeners.MindMapNodeMotionListener;
import freemind.view.mindmapview.NodeView;





public class MindMapController extends ControllerAdapter implements MindMapActions{


	private static Logger logger;
	// for MouseEventHandlers 
	private HashSet mRegisteredMouseWheelEventHandler = new HashSet();

    private ActionFactory actionFactory;
	private Vector hookActions;
	/** Stores the menu items belonging to the given action. */
	private HashMap actionToMenuPositions;
	//    Mode mode;
    private MindMapPopupMenu popupmenu;
    //private JToolBar toolbar;
    private MindMapToolBar toolbar;
    private boolean addAsChildMode = false;
    private Clipboard clipboard;
    private HookFactory nodeHookFactory;

    /**
     * This handler evaluates the compound xml actions. Don't delete it!
     */
    private CompoundActionHandler compound = null;
    
    public ApplyPatternAction patterns[] = new ApplyPatternAction[0]; // Make sure it is initialized
   public Action newMap = new NewMapAction(this);
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
   public Action followLink = new FollowLinkAction();
   public Action exportBranch = new ExportBranchAction();
   public Action importBranch = new ImportBranchAction();
   public Action importLinkedBranch = new ImportLinkedBranchAction();
   public Action importLinkedBranchWithoutRoot = new ImportLinkedBranchWithoutRootAction();


    public Action increaseNodeFont = new NodeGeneralAction (this, "increase_node_font_size", null,
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
           increaseFontSize(node, 1);
    }});
    public Action decreaseNodeFont = new NodeGeneralAction (this, "decrease_node_font_size", null,
       new SingleNodeOperation() { public void apply(MindMapMapModel map, MindMapNodeModel node) {
           increaseFontSize(node, -1);
    }});

    public UndoAction undo=null;
    public RedoAction redo=null;
    public CopyAction copy = null;
    public Action copySingle = null;
    public CutAction cut = null;
    public PasteAction paste = null;
    public BoldAction bold = null;
    public ItalicAction italic = null;
    public UnderlinedAction underlined = null;
    public FontSizeAction fontSize = null;
    public FontFamilyAction fontFamily = null;
    public EditAction edit = null;
    public NewChildAction newChild = null;
    public DeleteChildAction deleteChild = null;
    public ToggleFoldedAction toggleFolded = null;
    public ToggleChildrenFoldedAction toggleChildrenFolded = null;
    public NodeUpAction nodeUp = null;
    public NodeDownAction nodeDown = null;
    public EdgeColorAction edgeColor = null;
    public EdgeWidthAction EdgeWidth_WIDTH_PARENT = null;
    public EdgeWidthAction EdgeWidth_WIDTH_THIN = null;
    public EdgeWidthAction EdgeWidth_1 = null;
    public EdgeWidthAction EdgeWidth_2 = null;
    public EdgeWidthAction EdgeWidth_4 = null;
    public EdgeWidthAction EdgeWidth_8 = null;
    public EdgeWidthAction edgeWidths[] = null;
    public EdgeStyleAction EdgeStyle_linear = null;
    public EdgeStyleAction EdgeStyle_bezier = null;
    public EdgeStyleAction EdgeStyle_sharp_linear = null;
    public EdgeStyleAction EdgeStyle_sharp_bezier = null;
    public EdgeStyleAction edgeStyles[] = null;
    public NodeColorBlendAction nodeColorBlend = null;
    public NodeStyleAction fork = null;
    public NodeStyleAction bubble = null;
    public CloudAction cloud = null;
    public freemind.modes.mindmapmode.actions.CloudColorAction cloudColor = null;
    public AddArrowLinkAction addArrowLinkAction = null; 
    public RemoveArrowLinkAction removeArrowLinkAction = null;
    public ColorArrowLinkAction colorArrowLinkAction = null;
    public ChangeArrowsInArrowLinkAction changeArrowsInArrowLinkAction = null;
    public NodeBackgroundColorAction nodeBackgroundColor = null;
    public RemoveNodeBackgroundColorAction removeNodeBackgroundColor = null;

    public IconAction unknwonIconAction = null;
    public RemoveLastIconAction removeLastIconAction = null;
    public RemoveAllIconsAction removeAllIconsAction = null;
    public SetLinkByTextFieldAction setLinkByTextField = null;
    public AddLocalLinkAction addLocalLinkAction = null;
    public GotoLinkNodeAction gotoLinkNodeAction = null;
    public JoinNodesAction joinNodes = null;
    public MoveNodeAction moveNodeAction = null;
    public ImportExplorerFavoritesAction importExplorerFavorites = null;
    public ImportFolderStructureAction importFolderStructure = null;
    public ChangeArrowLinkEndPoints changeArrowLinkEndPoints = null;

    public FindAction find=null;
    public FindNextAction findNext=null;
    public NodeHookAction nodeHookAction = null;
    public RevertAction revertAction = null;
    public SelectBranchAction selectBranchAction = null;
    public SelectAllAction selectAllAction = null;

    
    
    // Extension Actions
    public Vector iconActions = new Vector(); //fc

    FileFilter filefilter = new MindMapFilter();

    private MenuStructure mMenuStructure;
    private List mRegistrations;

    public MindMapController(Mode mode) {
	super(mode);
	if(logger == null) {
		logger = getFrame().getLogger(this.getClass().getName());
	}
    // create action factory:
    actionFactory = new ActionFactory(getController());
    // create compound handler, that evaluates the compound xml actions.
    compound = new CompoundActionHandler(this);

	logger.info("createIconActions");
        // create standard actions:
        createStandardActions();
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
        mRegistrations = new Vector();
        Toolkit toolkit = getFrame().getViewport().getToolkit();
		clipboard = toolkit.getSystemSelection();

        // SystemSelection is a strange clipboard used for instance on
        // Linux. To get data into this clipboard user just selects the area
        // without pressing Ctrl+C like on Windows.
        
        if (clipboard == null) {
           clipboard = toolkit.getSystemClipboard(); 
        }

    }

    private void createStandardActions() {
        // prepare undo:
        undo = new UndoAction(this);
        redo = new RedoAction(this);
        // register default action handler:
        // the executor must be the first here, because it is executed last then.
        getActionFactory().registerHandler(new ModeControllerActionHandler(getActionFactory()));
        getActionFactory().registerHandler(new UndoActionHandler(this, undo, redo));
        //debug:        getActionFactory().registerHandler(new freemind.modes.mindmapmode.actions.xml.PrintActionHandler(this));

        cut = new CutAction(this);
        paste = new PasteAction(this);
        copy = new CopyAction(this);
        copySingle = new CopySingleAction(this);
        bold = new BoldAction (this);
        italic = new ItalicAction(this);
        underlined = new UnderlinedAction(this);
        fontSize = new FontSizeAction(this);
        fontFamily = new FontFamilyAction(this);
        edit = new EditAction(this);
        newChild = new NewChildAction(this);
        deleteChild = new DeleteChildAction(this);
        toggleFolded = new ToggleFoldedAction(this);
        toggleChildrenFolded = new ToggleChildrenFoldedAction(this);
        nodeUp = new NodeUpAction(this);
        nodeDown = new NodeDownAction(this);
        edgeColor = new EdgeColorAction(this);
        nodeColor = new NodeColorAction(this);
        nodeColorBlend = new NodeColorBlendAction(this);
        fork = new NodeStyleAction(this, MindMapNode.STYLE_FORK);
        bubble = new NodeStyleAction(this, MindMapNode.STYLE_BUBBLE);
        // this is an unknown icon and thus corrected by mindicon:
        removeLastIconAction = new RemoveLastIconAction(this);
        // this action handles the xml stuff: (undo etc.)
        unknwonIconAction = new IconAction(this, MindIcon.factory((String) MindIcon
                .getAllIconNames().get(0)), removeLastIconAction);
        removeLastIconAction.setIconAction(unknwonIconAction);
        removeAllIconsAction = new RemoveAllIconsAction(this, unknwonIconAction);
        // load pattern actions:
        try {
            loadPatterns(getPatternReader());
        } catch (XMLParseException e) {
            System.err.println("In patterns:" + e);
        } catch (Exception ex) {
            System.err.println("Patterns not loaded:" + ex);
        }
        EdgeWidth_WIDTH_PARENT = new EdgeWidthAction(this, EdgeAdapter.WIDTH_PARENT);
        EdgeWidth_WIDTH_THIN = new EdgeWidthAction(this, EdgeAdapter.WIDTH_THIN);
        EdgeWidth_1 = new EdgeWidthAction(this, 1);
        EdgeWidth_2 = new EdgeWidthAction(this, 2);
        EdgeWidth_4 = new EdgeWidthAction(this, 4);
        EdgeWidth_8 = new EdgeWidthAction(this, 8);
        edgeWidths =  new EdgeWidthAction[]{
            EdgeWidth_WIDTH_PARENT, EdgeWidth_WIDTH_THIN, EdgeWidth_1, EdgeWidth_2, EdgeWidth_4, EdgeWidth_8
        };
        EdgeStyle_linear = new EdgeStyleAction(this, EdgeAdapter.EDGESTYLE_LINEAR);
        EdgeStyle_bezier = new EdgeStyleAction(this, EdgeAdapter.EDGESTYLE_BEZIER);
        EdgeStyle_sharp_linear = new EdgeStyleAction(this, EdgeAdapter.EDGESTYLE_SHARP_LINEAR);
        EdgeStyle_sharp_bezier = new EdgeStyleAction(this, EdgeAdapter.EDGESTYLE_SHARP_BEZIER);
        edgeStyles =  new EdgeStyleAction[]{
            EdgeStyle_linear,
            EdgeStyle_bezier,
            EdgeStyle_sharp_linear,
            EdgeStyle_sharp_bezier
        };
        cloud = new CloudAction(this);
        cloudColor = new freemind.modes.mindmapmode.actions.CloudColorAction(this);
        addArrowLinkAction = new AddArrowLinkAction(this);
        removeArrowLinkAction = new RemoveArrowLinkAction(this, null);
        addArrowLinkAction.setRemoveAction(removeArrowLinkAction);
        colorArrowLinkAction = new ColorArrowLinkAction(this, null);
        changeArrowsInArrowLinkAction = new ChangeArrowsInArrowLinkAction(this, "none", null, null, true, true);
        nodeBackgroundColor = new NodeBackgroundColorAction(this);
        removeNodeBackgroundColor = new RemoveNodeBackgroundColorAction(this);
        setLinkByTextField = new SetLinkByTextFieldAction(this);
        addLocalLinkAction = new AddLocalLinkAction(this);
        gotoLinkNodeAction = new GotoLinkNodeAction(this, null);
        moveNodeAction = new MoveNodeAction(this);
        joinNodes = new JoinNodesAction(this);
        importExplorerFavorites = new ImportExplorerFavoritesAction(this);
        importFolderStructure = new ImportFolderStructureAction(this);
        changeArrowLinkEndPoints = new ChangeArrowLinkEndPoints(this);
        find = new FindAction(this);
        findNext = new FindNextAction(this,find);
        nodeHookAction = new NodeHookAction("no_title", this); 
        revertAction = new RevertAction(this);
        selectBranchAction = new SelectBranchAction(this);
        selectAllAction = new SelectAllAction(this);
    }

    /**
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Reader getPatternReader() throws FileNotFoundException, IOException {
        Reader reader = null;
        File patternsFile = getFrame().getPatternsFile();
        if (patternsFile != null && patternsFile.exists()) {
            reader = new FileReader(patternsFile);
        } else {
            System.out.println("User patterns file " + patternsFile
                    + " not found.");
            reader = new InputStreamReader(getResource("patterns.xml")
                    .openStream());
        }
        return reader;
    }

    /**
     * @return
     */
    public Clipboard getClipboard() {
        return clipboard;
    }
    
    public boolean isUndoAction() {
        return undo.isUndoAction() || redo.isUndoAction();
    }

    public void load(String xmlMapContents) {
        revertAction.openXmlInsteadOfMap(xmlMapContents);
    }


    
    private void loadPatterns(Reader reader) throws Exception {
        createPatterns(StylePatternFactory.loadPatterns(reader));
    }

    private void createPatterns(List patternsList) throws Exception {
        patterns = new ApplyPatternAction[patternsList.size()];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = new ApplyPatternAction(this,
                    (Pattern) patternsList.get(i));

            // search icons for patterns:
            PatternIcon patternIcon = ((Pattern) patternsList.get(i))
                    .getPatternIcon();
            if (patternIcon != null && patternIcon.getValue() != null) {
                patterns[i].putValue(Action.SMALL_ICON, MindIcon.factory(
                        patternIcon.getValue()).getIcon(getFrame()));
            }
        }
    }

    /** This method is called after and before a change of the map module.
     * Use it to perform the actions that cannot be performed at creation time.
     * 
     */
    public void startupController() {
		super.startupController();
		HookFactory hookFactory = getHookFactory();
		List pluginRegistrations = hookFactory.getRegistrations();
		logger.info("mScheduledActions are executed: "
				+ pluginRegistrations.size());
		for (Iterator i = pluginRegistrations.iterator(); i.hasNext();) {
			// call constructor:
			try {
				RegistrationContainer container = (RegistrationContainer) i
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
				mRegistrations.add(registrationInstance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
        // register mouse motion handler:
        getController().getMapMouseMotionListener().register(new MindMapMouseMotionManager(this));
        getController().getNodeDropListener().register(new MindMapNodeDropListener(this));
        getController().getNodeKeyListener().register(new CommonNodeKeyListener(this, new EditHandler(){

			public void edit(KeyEvent e, boolean addNew, boolean editLong) {
				MindMapController.this.edit(e, addNew, editLong);
				
			}}));
        getController().getNodeMotionListener().register(new MindMapNodeMotionListener(this));
        getController().getNodeMouseMotionListener().register(new CommonNodeMouseMotionListener(this));
        getController().getMapMouseWheelListener().register(new MindMapMouseWheelEventHandler(this));
	}


    public void shutdownController() {
        super.shutdownController();
        for (Iterator i = mRegistrations.iterator(); i.hasNext();) {
            HookRegistration registrationInstance = (HookRegistration) i.next();
            registrationInstance.deRegister();
        }
        getHookFactory().deregisterAllRegistrationContainer();
        mRegistrations.clear();
        // deregister motion handler
        getController().getMapMouseMotionListener().deregister();
        getController().getNodeDropListener().deregister();
        getController().getNodeKeyListener().deregister();
        getController().getNodeMotionListener().deregister();
        getController().getMapMouseWheelListener().deregister();
    }
    
	public MapAdapter newModel(ModeController modeController) {
       return new MindMapMapModel(getFrame(), modeController); }

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
            MindMapHookFactory factory = (MindMapHookFactory) getHookFactory();
            List list = factory.getPossibleNodeHooks();
            for (Iterator i = list.iterator(); i.hasNext();) {
                String desc = (String) i.next();
                // create hook action. 
                NodeHookAction action = new NodeHookAction(desc, this);
                factory.decorateAction(desc, action);
                actionToMenuPositions.put(action, factory.getHookMenuPositions(desc));
                hookActions.add(action);
            }
            List hooks =
                factory.getPossibleModeControllerHooks();
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
        MindMapNode createNode(Object userObject);
    }

    public class DefaultMindMapNodeCreator implements NewNodeCreator {

        public MindMapNode createNode(Object userObject) {
            return new MindMapNodeModel(userObject, getFrame());
        }
        
    }
    
    public void setNewNodeCreator(NewNodeCreator creator) {
        myNewNodeCreator = creator;
    }
    
    public MindMapNode newNode(Object userObject) {
        // singleton default:
        if (myNewNodeCreator == null) {
            myNewNodeCreator = new DefaultMindMapNodeCreator();
        }
        return myNewNodeCreator.createNode(userObject);
    }

    // fc, 14.12.2004: end "different models" change
    
	    //get/set methods



	/**
	 * @param holder
	 */
    public void updateMenus(StructuredMenuHolder holder) {

		processMenuCategory(holder, mMenuStructure.getListChoiceList(), ""); /*MenuBar.MENU_BAR_PREFIX*/
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
       	    IUnmarshallingContext unmarshaller = XmlBindingTools.getInstance().createUnmarshaller();
        	MenuStructure menus = (MenuStructure) unmarshaller.unmarshalDocument(in, null);
        	return menus;
        } catch (JiBXException e) {
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
            	processMenuCategory(holder, cat.getListChoiceList(), newCategory);
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

    public JToolBar getModeToolBar() {
    		return getToolBar();
    }
    
    MindMapToolBar getToolBar() {
	return (MindMapToolBar)toolbar;
    }

    public JToolBar getLeftToolBar() {
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
        cut.setEnabled(enabled);
        copy.setEnabled(enabled);
        copySingle.setEnabled(enabled);
        paste.setEnabled(enabled);
        undo.setEnabled(enabled);
        redo.setEnabled(enabled);
        edit.setEnabled(enabled);
        newChild.setEnabled(enabled);
        toggleFolded.setEnabled(enabled);
        toggleChildrenFolded.setEnabled(enabled);
        setLinkByTextField.setEnabled(enabled);
        italic.setEnabled(enabled);
        bold.setEnabled(enabled);
        find.setEnabled(enabled);
        findNext.setEnabled(enabled);
        addArrowLinkAction.setEnabled(enabled);
        addLocalLinkAction.setEnabled(enabled);
        nodeColorBlend.setEnabled(enabled);
        nodeUp.setEnabled(enabled);
        nodeBackgroundColor.setEnabled(enabled);
        nodeDown.setEnabled(enabled);
        importExplorerFavorites.setEnabled(enabled);
        importFolderStructure.setEnabled(enabled);
        joinNodes.setEnabled(enabled);
        deleteChild.setEnabled(enabled);
        cloud.setEnabled(enabled);
        cloudColor.setEnabled(enabled);
//        normalFont.setEnabled(enabled);
        nodeColor.setEnabled(enabled);
        edgeColor.setEnabled(enabled);
        removeLastIconAction.setEnabled(enabled);
        removeAllIconsAction.setEnabled(enabled);
        selectAllAction.setEnabled(enabled);
        selectBranchAction.setEnabled(enabled);
        removeNodeBackgroundColor.setEnabled(enabled);
        moveNodeAction.setEnabled(enabled);
        revertAction.setEnabled(enabled);
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

    }




    //
    //      Actions
    //_______________________________________________________________________________

    protected class ModeControllerHookAction extends AbstractAction {
        String hookName;
        ModeController controller;
        public ModeControllerHookAction(String hookName, ModeController controller) {
            super(hookName);
            this.hookName = hookName;
            this.controller = controller;
        }

        public void actionPerformed(ActionEvent arg0) {
            HookFactory hookFactory = getHookFactory();
            // two different invocation methods:single or selecteds
            ModeControllerHook hook = hookFactory.createModeControllerHook(hookName);
            hook.setController(controller);
            invokeHook(hook);                           
        } 
            
    }



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
                MindMapMapModel map = new MindMapMapModel(node, getFrame(), 
                		/* DON'T COPY THIS, AS THIS IS A BAD HACK! 
                		 * The Constructor needs a new instance of a modecontroller.*/ 
                		MindMapController.this);
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

    

    public void setBold(MindMapNode node, boolean bolded) {
        bold.setBold(node, bolded);
    }

    public void setItalic(MindMapNode node, boolean isItalic) {
        italic.setItalic(node, isItalic);
    }

    public void setCloud(MindMapNode node, boolean enable) {
        cloud.setCloud(node, enable);
    }
    public void setCloudColor(MindMapNode node, Color color) {
        cloudColor.setCloudColor(node, color);
    }
    //Node editing
    public void setFontSize(MindMapNode node, String fontSizeValue) {
        fontSize.setFontSize(node, fontSizeValue);
    }

    /**
     *
     */

    public void increaseFontSize(MindMapNode node, int increment) {
        int newSize = Integer.valueOf(node.getFontSize()).intValue()+increment;
        
        if (newSize > 0) {
            setFontSize(node, Integer.toString(newSize));
        }
    }
    
    public void setFontFamily(MindMapNode node, String fontFamilyValue) {
        fontFamily.setFontFamily(node, fontFamilyValue);
    }

    public void setNodeColor(MindMapNode node, Color color) {
        nodeColor.setNodeColor(node, color);
    }
    
    public void setNodeBackgroundColor(MindMapNode node, Color color) {
        nodeBackgroundColor.setNodeBackgroundColor(node, color);
    }
    public void blendNodeColor(MindMapNode node) {
        Color mapColor = getMap().getBackgroundColor();
        Color nodeColor = node.getColor();
        if (nodeColor == null) {
            nodeColor = Tools.xmlToColor(getFrame().getProperty(
                    FreeMind.RESOURCES_NODE_COLOR));
        }
        setNodeColor(node, new Color(
                (3 * mapColor.getRed() + nodeColor.getRed()) / 4, (3 * mapColor
                        .getGreen() + nodeColor.getGreen()) / 4, (3 * mapColor
                        .getBlue() + nodeColor.getBlue()) / 4));
    }


    public void setEdgeColor(MindMapNode node, Color color) {
        edgeColor.setEdgeColor(node, color);
    }

    public void applyPattern(MindMapNode node, String patternName){
        for (int i = 0; i < patterns.length; i++) {
            ApplyPatternAction patternAction = patterns[i];
            if(patternAction.getPattern().getName().equals(patternName)){
                patternAction.applyPattern(node, patternAction.getPattern());
                break;
            }
        }
    }

    

    public void applyPattern(MindMapNode node, Pattern pattern) {
        if(patterns.length > 0) {
            patterns[0].applyPattern(node, pattern);
        } else {
            throw new IllegalArgumentException("No pattern defined.");
        }
    }
    
    public void addIcon(MindMapNode node, MindIcon icon) {
        unknwonIconAction.addIcon(node, icon);
    }


    public void removeAllIcons(MindMapNode node) {
        removeAllIconsAction.removeAllIcons(node);
    }

    public int removeLastIcon(MindMapNode node) {
        return removeLastIconAction.removeLastIcon(node);
    }
    /**
     *
     */

    public void addLink(MindMapNode source, MindMapNode target) {
        addArrowLinkAction.addLink(source, target);
    }

    public void removeReference(MindMapLink arrowLink){
        removeArrowLinkAction.removeReference(arrowLink);
    }

    public void setArrowLinkColor(MindMapLink arrowLink, Color color) {
        colorArrowLinkAction.setArrowLinkColor(arrowLink, color);
    }
    
    /**
     *
     */

    public void changeArrowsOfArrowLink(MindMapArrowLinkModel arrowLink,
            boolean hasStartArrow, boolean hasEndArrow) {
        changeArrowsInArrowLinkAction.changeArrowsOfArrowLink(arrowLink, hasStartArrow, hasEndArrow);
    }
    
    public void setArrowLinkEndPoints(MindMapArrowLink link, Point startPoint,
            Point endPoint) {
        changeArrowLinkEndPoints.setArrowLinkEndPoints(link, startPoint, endPoint);
    }
    public void setLink(MindMapNode node, String link) {
        setLinkByTextField.setLink(node, link);
    }
    /**
     *
     */

    public void setToolTip(MindMapNode node, String key, String value) {
        node.setToolTip(key, value);
        nodeRefresh(node);
    }
    // edit begins with home/end or typing (PN 6.2)
    public void edit(KeyEvent e, boolean addNew, boolean editLong) {
        edit.edit(e, addNew, editLong);
    }

    
    public void setNodeText(MindMapNode selected, String newText) {
        edit.setNodeText(selected, newText);
    }

    /**
     *
     */

    public void setEdgeWidth(MindMapNode node, int width) {
        EdgeWidth_1.setEdgeWidth(node, width);
    }
    /**
     *
     */

    public void setEdgeStyle(MindMapNode node, String style) {
        EdgeStyle_bezier.setEdgeStyle(node, style);
    }
    /**
     *
     */

    public void setNodeStyle(MindMapNode node, String style) {
        fork.setStyle(node, style);
    }
     public Transferable cut() {
        return cut(getSelecteds());
    }

     
        
    public Transferable cut(List nodeList) {
        return cut.cut(nodeList);
    }
    public void paste(Transferable t, MindMapNode parent) {
        boolean isLeft = false;
        if(parent.isLeft()!= null)
            isLeft = parent.isLeft().getValue();
        paste(t, /*target=*/parent, /*asSibling=*/ false, isLeft); }

    /** @param isLeft determines, whether or not the node is placed on the left or right. **/
    public void paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft) {
        paste.paste(t, target, asSibling, isLeft);
    }

    public void paste(MindMapNode node, MindMapNode parent) {
        paste.paste(node, parent);
    }

    public MindMapNode addNew(final MindMapNode target, final int newNodeMode, final KeyEvent e) {
        return newChild.addNew(target, newNodeMode, e);
    }
    
    public  MindMapNode addNewNode(MindMapNode parent, int index, freemind.main.Tools.BooleanHolder newNodeIsLeft) {
        return newChild.addNewNode(parent, index, newNodeIsLeft);
    }


    public void deleteNode(MindMapNode selectedNode) {
        deleteChild.deleteNode(selectedNode);
    }

    public void toggleFolded() {
        toggleFolded.toggleFolded();
    }
    
    public void setFolded(MindMapNode node, boolean folded) {
        toggleFolded.setFolded(node, folded);
    }
    
    public void moveNodes(MindMapNode selected, List selecteds, int direction){
        nodeUp.moveNodes(selected, selecteds, direction);
    }

    public void joinNodes(MindMapNode selectedNode, List selectedNodes) {
        joinNodes.joinNodes(selectedNode, selectedNodes);
    }

    protected void setLinkByFileChooser() {
        String relative = getLinkByFileChooser(getFileFilter());
        if (relative != null) 
            setLink((NodeAdapter) getSelected(),relative);
    }
    
    protected void setImageByFileChooser() {
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("jpg");
        filter.addExtension("jpeg");
        filter.addExtension("png");
        filter.addExtension("gif");
        filter.setDescription("JPG, PNG and GIF Images");

                // Are there any selected nodes with pictures?                
                boolean picturesAmongSelecteds = false;
                for (ListIterator e = getSelecteds().listIterator();e.hasNext();) {
                   String link = ((MindMapNode)e.next()).getLink();
                   if (link != null) {
                      if (filter.accept(new File(link))) {
                         picturesAmongSelecteds = true;
                         break;
                      }
                   }
                }

                try {
                    // FIXME: Is this used?????
                   if (picturesAmongSelecteds) {
                      for (ListIterator e = getSelecteds().listIterator();e.hasNext();) {
                         MindMapNode node = (MindMapNode)e.next();
                         if (node.getLink() != null) {
                            String possiblyRelative = node.getLink();
                            String relative = Tools.isAbsolutePath(possiblyRelative) ?
                               new File(possiblyRelative).toURL().toString() : possiblyRelative;
                            if (relative != null) {
                               String strText = "<html><img src=\"" + relative + "\">";
                               setLink(node, null);
                               setNodeText(node, strText);
                            }
                         }
                      }
                   }
                   else {
                      String relative = getLinkByFileChooser(filter);
                      if (relative != null) {
                         String strText = "<html><img src=\"" + relative + "\">"; 
                         setNodeText((MindMapNode)getSelected(),strText);
                      } 
                   }
                }
                catch (MalformedURLException e) {e.printStackTrace(); }
    }
   
    protected String getLinkByFileChooser(FileFilter fileFilter) {
        URL link;
        String relative = null;
        File input;
        JFileChooser chooser = null;
        if (getMap().getFile() == null) {
            JOptionPane.showMessageDialog(getFrame().getContentPane(), getText("not_saved_for_link_error"), 
                                          "FreeMind", JOptionPane.WARNING_MESSAGE);
            return null;
            // In the previous version Freemind automatically displayed save
            // dialog. It happened very often, that user took this save
            // dialog to be an open link dialog; as a result, the new map
            // overwrote the linked map.

        }
        if (getLastCurrentDir() != null) {
            chooser = new JFileChooser(getLastCurrentDir());
        } else {
            chooser = new JFileChooser();
        }

        if (fileFilter != null) {
           // Set filters, make sure AcceptAll filter comes first
           chooser.setFileFilter(fileFilter);
        } else {
            chooser.setFileFilter(chooser.getAcceptAllFileFilter());
        }
 
        int returnVal = chooser.showOpenDialog(getFrame().getContentPane());
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            input = chooser.getSelectedFile();
            setLastCurrentDir(input.getParentFile());
            try {
                link = input.toURL();
                relative = link.toString();
            } catch (MalformedURLException ex) {
                getController().errorMessage(getText("url_error"));
                return null;
            }
            if (getFrame().getProperty("links").equals("relative")) {
                //Create relative URL
                try {
                    relative = Tools.toRelativeURL(getMap().getFile().toURL(), link);
                } catch (MalformedURLException ex) {
                    getController().errorMessage(getText("url_error"));
                    return null;
                }
            }
        }
        return relative;
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
            else if(relative.startsWith("#")){
                // inner map link, fc, 12.10.2004
                logger.finest("found relative link to "+relative);
                String target = relative.substring(1);
                try {
                    MindMapNode node = getNodeFromID(target);
                    centerNode(node);
                    return;
                } catch (Exception e) {
                    // give "not found" message
                    throw new FileNotFoundException(null);
                }
                
            } else{
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
                 load(file.toURL()); }}
           else {                                                 // ---- Open URL in browser
               // fc, 14.12.2003: The following code seems not to be very good. Imagine file names with spaces. Then they occur as %20, now the OS does not find the file, 
               // etc. If this is necessary, this should be done in the openDocument command.
//               if (absolute.getProtocol().equals("file")) {                 
//                  File file = new File (Tools.urlGetFile(absolute));
//                  // If file does not exist, try http protocol (but only if it is reasonable)
//                  if (!file.exists()) {
//                     if (relative.matches("^[-\\.a-z0-9]*/?$")) {
//                        absolute = new URL("http://"+relative); }
//                     else {
//                        // This cannot be a base, to which http:// may be added.
//                        getController().errorMessage("File \""+file+"\" does not exist.");
//                        return; }}}
              getFrame().openDocument(absolute); }}
        catch (MalformedURLException ex) {
            getController().errorMessage(getText("url_error")+"\n"+ex);
            return; }
        catch (FileNotFoundException e) {
            int returnVal = JOptionPane.showConfirmDialog
               (getView(),
                getText("repair_link_question"),
                getText("repair_link"),
                JOptionPane.YES_NO_OPTION);
            if (returnVal==JOptionPane.YES_OPTION) {
                setLinkByTextField.actionPerformed(null);
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        getFrame().setWaitingCursor(false);
    }

    public void loadURL() {
        String link = getSelected().getLink();
        if (link != null) {
            loadURL(link);
        }
    }

    public void addHook(MindMapNode focussed, List selecteds, String hookName) {
        nodeHookAction.addHook(focussed, selecteds, hookName);
    }
    
    protected class SetLinkByFileChooserAction extends AbstractAction {
        public SetLinkByFileChooserAction() {
            super(getText("set_link_by_filechooser"));
        }
        public void actionPerformed(ActionEvent e) {
            setLinkByFileChooser();
        }
    }

    protected class SetImageByFileChooserAction extends AbstractAction {
        public SetImageByFileChooserAction() {
            super(getText("set_image_by_filechooser"));
        }
        public void actionPerformed(ActionEvent e) {
                       setImageByFileChooser();
                        getController().obtainFocusForSelected();
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
    

    public void moveNodePosition(MindMapNode node, int vGap, int hGap,
            int shiftY) {
        moveNodeAction.moveNodeTo(node, vGap, hGap, shiftY);
    }

    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    
    
    public void plainClick(MouseEvent e) {
        /* perform action only if one selected node.*/
        if(getSelecteds().size() != 1)
            return;
        MindMapNode node = ((NodeView)(e.getComponent())).getModel();
        if (getView().getSelected().isInFollowLinkRegion(e.getX())) {
            loadURL(); }
        else {
            if (!node.hasChildren()) {
                // the emulate the plain click. 
                doubleClick(e);
                return;
            }
            toggleFolded(); 
        }
    }

	public HookFactory getHookFactory() {
		// lazy creation.
		if(nodeHookFactory == null) {
			nodeHookFactory = new MindMapHookFactory(getFrame());
		}
		return nodeHookFactory;
	}

    public NodeHook createNodeHook(String hookName, MindMapNode node,
            MindMap map) {
        HookFactory hookFactory = getHookFactory();
        NodeHook hook = (NodeHook) hookFactory.createNodeHook(hookName);
        hook.setController(this);
        hook.setMap(map);
        if (hook instanceof PermanentNodeHook) {
            PermanentNodeHook permHook = (PermanentNodeHook) hook;
            if(hookFactory.getInstanciationMethod(hookName).isSingleton()) {
                // search for already instanciated hooks of this type:
                PermanentNodeHook otherHook = hookFactory.getHookInNode(node, hookName);
                if(otherHook != null) {
                    return otherHook;
                }
            }
            node.addHook(permHook);
        }
        return hook;
    }
    
    public void invokeHook(ModeControllerHook hook) {
        hook.setController(this);
        // initialize:
        // the main invocation:
        hook.startupMapHook();
        // and good bye.
        hook.shutdownMapHook();
    }
    
    public ActionFactory getActionFactory() {
        return actionFactory;
    }

    protected class EditLongAction extends AbstractAction {
        public EditLongAction() {
            super(getText("edit_long_node"));
        }
        public void actionPerformed(ActionEvent e) {
            edit(null, false, true);
        }
    }

    /**
     * @param node
     * @param position
     * @param newText
     */
    public void splitNode(MindMapNode node, int caretPosition, String newText) {
            //If there are children, they go to the node below
            String futureText = newText != null ? newText : node.toString();

            String newLowerContent = futureText.substring(caretPosition, futureText.length());
            String newUpperContent = futureText.substring(0,caretPosition);

            setNodeText(node, newLowerContent);

            MindMapNode parent = node.getParentNode();
            MindMapNode upperNode = addNewNode(parent, parent.getChildPosition(node), parent.isLeft());
            upperNode.setColor(node.getColor());
            upperNode.setFont(node.getFont());
            setNodeText(upperNode, newUpperContent);
        
    }

    protected void updateNode(MindMapNode node) {
        recursiveCallUpdateHooks((MindMapNode) node, (MindMapNode) node /* self update */);
    }

    /**
     * @param node
     */
    private void recursiveCallUpdateHooks(MindMapNode node, MindMapNode changedNode) {
        // Tell any node hooks that the node is changed:
        if(node instanceof MindMapNode) {
            for(Iterator i=  ((MindMapNode)node).getActivatedHooks().iterator(); i.hasNext();) {
                PermanentNodeHook hook = (PermanentNodeHook) i.next();
                if ( (! isUndoAction())  || hook instanceof UndoEventReceiver) {
                    if (node == changedNode)
                        hook.onUpdateNodeHook();
                    else
                        hook.onUpdateChildrenHook(changedNode);
                }
            }
        }
        if(!node.isRoot() && node.getParentNode()!= null)
            recursiveCallUpdateHooks(node.getParentNode(), changedNode);
    }

    
    public void doubleClick(MouseEvent e) {
        /* perform action only if one selected node.*/
        if(getSelecteds().size() != 1)
            return;
        MindMapNode node = ((NodeView)(e.getComponent())).getModel();
        // edit the node only if the node is a leaf (fc 0.7.1)
        if (node.hasChildren()) {
            // the emulate the plain click. 
            plainClick(e);
            return;
        }
        if (!e.isAltDown() 
            && !e.isControlDown() 
            && !e.isShiftDown() 
            && !e.isPopupTrigger()
            && e.getButton() == MouseEvent.BUTTON1
            && (node.getLink() == null)) {
            edit(null, false, false);
        }
    }

    
    public void select( NodeView node) {
    		if(node == null) {
    			logger.warning("Select with null NodeView called!");
    			return;
    		}
        getView().scrollNodeToVisible(node);
        getView().selectAsTheOnlyOneSelected(node);
        getView().setSiblingMaxLevel(node.getModel().getNodeLevel()); // this level is default
    }

    public void select( MindMapNode selected) {
        // are they visible visible?
        displayNode(selected);
        select(selected.getViewer());
    }

    public void selectMultipleNodes(MindMapNode focussed, Collection selecteds) {
        // are they visible visible?
        for (Iterator i = selecteds.iterator(); i.hasNext();) {
            MindMapNode node = (MindMapNode) i.next();
            displayNode(node);
        }
        // this one must be visible.
        select(focussed);
        for (Iterator i = selecteds.iterator(); i.hasNext();) {
            MindMapNode node = (MindMapNode) i.next();
            getView().makeTheSelected(node.getViewer());
        }
        getController().obtainFocusForSelected(); // focus fix
    }
    
    public void selectBranch(MindMapNode selected, boolean extend) {
        displayNode(selected);
        getView().selectBranch(selected.getViewer(), extend);
    }
    
    public boolean extendSelection(MouseEvent e) {
        NodeView newlySelectedNodeView = (NodeView)e.getSource();
        //MindMapNode newlySelectedNode = newlySelectedNodeView.getModel();
        boolean extend = e.isControlDown(); 
        boolean range = e.isShiftDown(); 
        boolean branch = e.isAltGraphDown() || e.isAltDown(); /* windows alt, linux altgraph .... */ 
        boolean retValue = false;

        if (extend || range || branch || !getView().isSelected(newlySelectedNodeView)) {
            if (!range) {
                if (extend)
                    getView().toggleSelected(newlySelectedNodeView);
                else
                    select(newlySelectedNodeView);
                retValue = true;
            }
            else {
                retValue = getView().selectContinuous(newlySelectedNodeView); 
//                 /* fc, 25.1.2004: replace getView by controller methods.*/
//                 if (newlySelectedNodeView != getView().getSelected() &&
//                     newlySelectedNodeView.isSiblingOf(getView().getSelected())) {
//                     getView().selectContinuous(newlySelectedNodeView); 
//                     retValue = true;
//                 } else {
//                     /* if shift was down, but no range can be selected, then the new node is simply selected: */
//                     if(!getView().isSelected(newlySelectedNodeView)) {
//                         getView().toggleSelected(newlySelectedNodeView);
//                         retValue = true;
//                     }
            }
            if(branch) {
                getView().selectBranch(newlySelectedNodeView, extend); 
                retValue = true;
            }    
        }

        if(retValue) {
            e.consume();
        
            // Display link in status line
            String link = newlySelectedNodeView.getModel().getLink();
            link = (link != null ? link : " ");
            getController().getFrame().out(link); 
        }
        return retValue;
    }

    

    public void registerMouseWheelEventHandler(MouseWheelEventHandler handler) {
        logger.info("Registered   MouseWheelEventHandler "+handler);
        mRegisteredMouseWheelEventHandler.add(handler);
    }
    public void deRegisterMouseWheelEventHandler(MouseWheelEventHandler handler) {
        logger.info("Deregistered MouseWheelEventHandler "+handler);
        mRegisteredMouseWheelEventHandler.remove(handler);
    }

    public Set getRegisteredMouseWheelEventHandler() {
    		return Collections.unmodifiableSet(mRegisteredMouseWheelEventHandler);
    	
    }
    
    public String marshall(XmlAction action) {
        return XmlBindingTools.getInstance().marshall(action);
	}

	public XmlAction unMarshall(String inputString) {
        return XmlBindingTools.getInstance().unMarshall(inputString);
	}

	public void storeDialogPositions(JDialog dialog, TimeWindowConfigurationStorage storage, String window_preference_storage_property) {
		XmlBindingTools.getInstance().storeDialogPositions(getController(), dialog, storage, window_preference_storage_property);
	}

	public WindowConfigurationStorage decorateDialog(JDialog dialog, String window_preference_storage_property) {
		return XmlBindingTools.getInstance().decorateDialog(getController(), dialog, window_preference_storage_property);
	}



}
