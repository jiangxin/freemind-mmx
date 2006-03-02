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
/*$Id: ControllerAdapter.java,v 1.41.14.22.2.2.2.16 2006-03-02 21:00:54 dpolivaev Exp $*/

package freemind.modes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import freemind.controller.Controller;
import freemind.controller.StructuredMenuHolder;
import freemind.controller.actions.ActionFactory;
import freemind.controller.actions.ModeControllerActionHandler;
import freemind.controller.actions.UndoActionHandler;
import freemind.controller.actions.generated.instance.ObjectFactory;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.controller.attributes.AssignAttributeDialog;
import freemind.extensions.HookFactory;
import freemind.extensions.ModeControllerHook;
import freemind.extensions.NodeHook;
import freemind.extensions.PermanentNodeHook;
import freemind.extensions.UndoEventReceiver;
import freemind.main.ExampleFileFilter;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.actions.AddArrowLinkAction;
import freemind.modes.actions.AddLocalLinkAction;
import freemind.modes.actions.ApplyPatternAction;
import freemind.modes.actions.BoldAction;
import freemind.modes.actions.ChangeArrowLinkEndPoints;
import freemind.modes.actions.ChangeArrowsInArrowLinkAction;
import freemind.modes.actions.CloudAction;
import freemind.modes.actions.ColorArrowLinkAction;
import freemind.modes.actions.CompoundActionHandler;
import freemind.modes.actions.CopyAction;
import freemind.modes.actions.CopySingleAction;
import freemind.modes.actions.CutAction;
import freemind.modes.actions.DeleteChildAction;
import freemind.modes.actions.EdgeColorAction;
import freemind.modes.actions.EdgeStyleAction;
import freemind.modes.actions.EdgeWidthAction;
import freemind.modes.actions.EditAction;
import freemind.modes.actions.FindAction;
import freemind.modes.actions.FontFamilyAction;
import freemind.modes.actions.FontSizeAction;
import freemind.modes.actions.GotoLinkNodeAction;
import freemind.modes.actions.IconAction;
import freemind.modes.actions.ImportExplorerFavoritesAction;
import freemind.modes.actions.ImportFolderStructureAction;
import freemind.modes.actions.ItalicAction;
import freemind.modes.actions.JoinNodesAction;
import freemind.modes.actions.MoveNodeAction;
import freemind.modes.actions.NewChildAction;
import freemind.modes.actions.NodeBackgroundColorAction;
import freemind.modes.actions.NodeColorAction;
import freemind.modes.actions.NodeColorBlendAction;
import freemind.modes.actions.NodeHookAction;
import freemind.modes.actions.NodeStyleAction;
import freemind.modes.actions.NodeUpAction;
import freemind.modes.actions.PasteAction;
import freemind.modes.actions.RedoAction;
import freemind.modes.actions.RemoveAllIconsAction;
import freemind.modes.actions.RemoveArrowLinkAction;
import freemind.modes.actions.RemoveLastIconAction;
import freemind.modes.actions.RevertAction;
import freemind.modes.actions.SelectAllAction;
import freemind.modes.actions.SelectBranchAction;
import freemind.modes.actions.SetLinkByTextFieldAction;
import freemind.modes.actions.ToggleChildrenFoldedAction;
import freemind.modes.actions.ToggleFoldedAction;
import freemind.modes.actions.UnderlinedAction;
import freemind.modes.actions.UndoAction;
import freemind.modes.actions.FindAction.FindNextAction;
import freemind.modes.actions.NodeBackgroundColorAction.RemoveNodeBackgroundColorAction;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.modes.attributes.NodeAttributeTableModel;
import freemind.modes.attributes.mindmapmodeactors.MindMapModeAttributeController;
import freemind.modes.mindmapmode.MindMapArrowLinkModel;
import freemind.view.MapModule;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.MindMapLayout;
import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.attributeview.AttributeView;


/**
 * Derive from this class to implement the Controller for your mode. Overload the methods
 * you need for your data model, or use the defaults. There are some default Actions you may want
 * to use for easy editing of your model. Take MindMapController as a sample.
 */
public abstract class ControllerAdapter implements ModeController {

	private final static int MINIMAL_LEAF_WIDTH = MindMapLayout.MINIMAL_LEAF_WIDTH;
	private final static int MINIMAL_WIDTH = MindMapLayout.MINIMAL_WIDTH;

    private ActionFactory actionFactory;
	// for cascading updates.
	private HashSet nodesAlreadyUpdated;
	private HashSet nodesToBeUpdated;
	// for MouseEventHandlers 
	private HashSet mRegisteredMouseWheelEventHandler = new HashSet();
	// Logging: 
	private static java.util.logging.Logger logger;
    public ApplyPatternAction patterns[] = new ApplyPatternAction[0]; // Make sure it is initialized

	private Mode mode;
    private Clipboard clipboard;
    private int status;
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
    public freemind.modes.actions.CloudColorAction cloudColor = null;
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

    public Action showAllAttributes = new ShowAllAttributesAction();
    public Action showSelectedAttributes = new ShowSelectedAttributesAction();
    public Action hideAllAttributes = new HideAllAttributesAction();

	/** Executes series of actions. */
	private CompoundActionHandler compound = null;

    private Color selectionColor = new Color(200,220,200);
    public NodeColorAction nodeColor = null;
    private Set globalPatternList;

    public ControllerAdapter(Mode mode) {
        this.setMode(mode);
        if(logger==null) {
        	logger = getFrame().getLogger(this.getClass().getName());
        }
        // for updates of nodes:
		nodesAlreadyUpdated = new HashSet();
		nodesToBeUpdated    = new HashSet();
        // create action factory:
		actionFactory = new ActionFactory(getController());
        // create attrubute controller
        attributeController = new MindMapModeAttributeController(this);
		// prepare undo:
		undo = new UndoAction(this);
		redo = new RedoAction(this);
		// register default action handler:
		// the executor must be the first here, because it is executed last then.
		getActionFactory().registerHandler(new ModeControllerActionHandler(getActionFactory()));
		getActionFactory().registerHandler(new UndoActionHandler(this, undo, redo));
		//debug:						getActionFactory().registerHandler(new freemind.controller.actions.PrintActionHandler(this));

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
	    EdgeWidth_WIDTH_PARENT = new EdgeWidthAction(this, EdgeAdapter.WIDTH_PARENT);
		EdgeWidth_WIDTH_THIN = new EdgeWidthAction(this, EdgeAdapter.WIDTH_THIN);
		EdgeWidth_1 = new EdgeWidthAction(this, 1);
		EdgeWidth_2 = new EdgeWidthAction(this, 2);
		EdgeWidth_4 = new EdgeWidthAction(this, 4);
		EdgeWidth_8 = new EdgeWidthAction(this, 8);
	    edgeWidths =  new EdgeWidthAction[]{
			EdgeWidth_WIDTH_PARENT, EdgeWidth_WIDTH_THIN, EdgeWidth_1, EdgeWidth_2, EdgeWidth_4, EdgeWidth_8
	    };
		EdgeStyle_linear = new EdgeStyleAction(this, "linear");
		EdgeStyle_bezier = new EdgeStyleAction(this, "bezier");
		EdgeStyle_sharp_linear = new EdgeStyleAction(this, "sharp_linear");
		EdgeStyle_sharp_bezier = new EdgeStyleAction(this, "sharp_bezier");
	    edgeStyles =  new EdgeStyleAction[]{
			EdgeStyle_linear,
			EdgeStyle_bezier,
			EdgeStyle_sharp_linear,
			EdgeStyle_sharp_bezier
	    };
	    cloud = new CloudAction(this);
	    cloudColor = new freemind.modes.actions.CloudColorAction(this);
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
	    
	    compound = new CompoundActionHandler(this);
        DropTarget dropTarget = new DropTarget(getFrame().getViewport(),
                                               new FileOpener());

        clipboard = getFrame().getViewport().getToolkit().getSystemSelection();

        // SystemSelection is a strange clipboard used for instance on
        // Linux. To get data into this clipboard user just selects the area
        // without pressing Ctrl+C like on Windows.
        
        if (clipboard == null) {
           clipboard = getFrame().getViewport().getToolkit().getSystemClipboard(); }

    }

    private void loadPatterns(File file) throws Exception {
        createPatterns(StylePattern.loadPatterns(file));
    }

    private void loadPatterns(Reader reader) throws Exception {
        createPatterns(StylePattern.loadPatterns(reader));
    }

    private void createPatterns(List patternsList) throws Exception {
        globalPatternList = new HashSet();
        globalPatternList.addAll(patternsList);
        patterns = new ApplyPatternAction[patternsList.size()];
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = new ApplyPatternAction(this,
                    (StylePattern) patternsList.get(i));

            // search icons for patterns:
            MindIcon patternIcon = ((StylePattern) patternsList.get(i))
                    .getNodeIcon();
            if (patternIcon != null) {
                patterns[i].putValue(Action.SMALL_ICON, patternIcon
                        .getIcon());
            }
        }
    }

    public ActionFactory getActionFactory() {
        return actionFactory;
    }


    //
    // Methods that should be overloaded
    //

    public abstract MindMapNode newNode(Object userObject, MindMap map);
    protected MindMapNode newNode(MindMap map) {
        return newNode(null);
    }

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

	
	/** Currently, this method is called by the mapAdapter. This is buggy, and is to be changed.*/
    public void nodeChanged(MindMapNode node) {
        getMap().setSaved(false);
        nodeRefresh(node,true);
    }

    public void nodeRefresh(MindMapNode node) {
        nodeRefresh(node,false);
    }
    private void nodeRefresh(MindMapNode node, boolean isUpdate) {
    	logger.finest("nodeChanged called for node "+node+" parent="+node.getParentNode());
		if(nodesAlreadyUpdated.contains(node)) {			
			return;
		}
		nodesToBeUpdated.add(node);
		nodesAlreadyUpdated.add(node);
		if (isUpdate) {
			// update modification times:
			if(node.getHistoryInformation()!= null) {
				node.getHistoryInformation().setLastModifiedAt(new Date());
			}
            // Tell any node hooks that the node is changed:
            recursiveCallUpdateHooks((MindMapNode) node, (MindMapNode) node /* self update */);
        }
		getMap().nodeChangedMapInternal(node);
		nodesToBeUpdated.remove(node);
		if(nodesToBeUpdated.size()==0) {
			// this is the end of all updates:
			nodesAlreadyUpdated.clear();
		}
    }

	/**
	 * @param parent
	 */
	public void nodeStructureChanged(MindMapNode node) {
		getMap().nodeStructureChanged(node);
	}

	public boolean isUndoAction() {
	    return undo.isUndoAction() || redo.isUndoAction();
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

    public void anotherNodeSelected(MindMapNode n) {
    }

    public void doubleClick(MouseEvent e) {
        /* perform action only if one selected node.*/
        if(getSelecteds().size() != 1)
            return;
        MindMapNode node = ((NodeView)(e.getComponent().getParent())).getModel();
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

    public void plainClick(MouseEvent e) {
        /* perform action only if one selected node.*/
        if(getSelecteds().size() != 1)
            return;
        MindMapNode node = ((NodeView)(e.getComponent().getParent())).getModel();
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

    //
    // Map Management
    //

    public String getText(String textId) {
       return getController().getResourceString(textId); }

    protected boolean binOptionIsTrue(String option) {
       return getFrame().getProperty(option).equals("true");}

    public MindMap newMap() {
        MapAdapter newModel = newModel();
        newMap(newModel);
        return newModel;
    }

    public void newMap(MindMap mapModel) {
        getController().getMapModuleManager().newMapModule(mapModel);
    }

    /**
     * You may decide to overload this or take the default
     * and implement the functionality in your MapModel (implements MindMap)
     */
    public void load (File file) throws FileNotFoundException, IOException, XMLParseException {
        MapAdapter model = newModel();
		model.load(file);
		getController().getMapModuleManager().newMapModule(model);
		//FIXME: This must be done in the mapModuleManager. Ojo: The
		// ModeController has changed.
		getController().getModeController().invokeHooksRecursively(
				(NodeAdapter) getModel().getRoot(), getModel());
    }

    public boolean save() {
        if (getModel().isSaved()) return true;
        if (getModel().getFile() == null || getModel().isReadOnly()) {
           return saveAs(); }
        else {
           return save(getModel().getFile()); }}
    
    
    public void load(String xmlMapContents) {
        revertAction.openXmlInsteadOfMap(xmlMapContents);
    }

    /** fc, 24.1.2004: having two methods getSelecteds with different return values 
     * (linkedlists of models resp. views) is asking for trouble. @see MapView
     * @return returns a list of MindMapNode s. */
    public List getSelecteds() {
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

    
	/**
	 * This class sortes nodes by ascending depth of their paths to root. This
	 * is useful to assure that children are cutted <b>before </b> their
	 * fathers!!!.
	 * 
	 * Moreover, it sorts nodes with the same depth according to their
	 * position relative to each other.
	 */
	protected class nodesDepthComparator implements Comparator{
		public nodesDepthComparator() {}
		/* the < relation.*/
		public int compare(Object p1, Object p2) {
			MindMapNode n1 = ((MindMapNode) p1);
			MindMapNode n2 = ((MindMapNode) p2);
			Object[] path1 = getModel().getPathToRoot(n1);
			Object[] path2 = getModel().getPathToRoot(n2);
			int depth = path1.length - path2.length;
			if(depth > 0)
				return -1;
			if(depth < 0)
				return 1;
			if(n1.isRoot()) // if n1 is root, n2 is root, too ;)
				return 0;
			return n1.getParentNode().getChildPosition(n1) - n2.getParentNode().getChildPosition(n2);
		}
	}

	public List getSelectedsByDepth() {
		// return an ArrayList of MindMapNodes.
		List result = getSelecteds();
		sortNodesByDepth(result);
		return result;
	}


	public void sortNodesByDepth(List inPlaceList) {
		Collections.sort(inPlaceList, new nodesDepthComparator());
		logger.finest("Sort result: "+inPlaceList);
	}

	/**
     * Return false is the action was cancelled, e.g. when
     * it has to lead to saving as.
     */
    public boolean save(File file) {
       return getModel().save(file); }      

    /** @return returns the new JMenuItem.*/
    protected JMenuItem add(JMenu menu, Action action, String keystroke) { 
       JMenuItem item = menu.add(action);
       item.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty(keystroke)));
       return item;
    }

	/** @return returns the new JMenuItem.
	 * @param keystroke can be null, if no keystroke should be assigned. */
	protected JMenuItem add(StructuredMenuHolder holder, String category, Action action, String keystroke) { 
	   JMenuItem item = holder.addMenuItem(new JMenuItem(action), category);
	   if(keystroke != null) {
		item.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty(keystroke)));
	   }
	   return item;
	}

	/** @return returns the new JCheckBoxMenuItem.
	 * @param keystroke can be null, if no keystroke should be assigned. */
	protected JMenuItem addCheckBox(StructuredMenuHolder holder, String category, Action action, String keystroke) { 
		JCheckBoxMenuItem item = (JCheckBoxMenuItem) holder.addMenuItem(new JCheckBoxMenuItem(action), category);
	   if(keystroke != null) {
		item.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty(keystroke)));
	   }
	   return item;
	}

	protected JMenuItem addRadioItem(StructuredMenuHolder holder, String category, Action action, String keystroke, boolean isSelected) {
	    JRadioButtonMenuItem item = (JRadioButtonMenuItem) holder.addMenuItem(new JRadioButtonMenuItem(action), category);
	    if(keystroke != null) {
	        item.setAccelerator(KeyStroke.getKeyStroke(getFrame().getProperty(keystroke)));
	    }
	    item.setSelected(isSelected);
	    return item;
	}

    
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
        getController().setTitle();
    }

    public void handleLoadingException (Exception ex) {
       String exceptionType = ex.getClass().getName();
       if (exceptionType.equals("freemind.main.XMLParseException")) {
          int showDetail = JOptionPane.showConfirmDialog
             (getView(), getText("map_corrupted"),"FreeMind",JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
          if (showDetail==JOptionPane.YES_OPTION) {
             getController().errorMessage(ex); }}
       else if (exceptionType.equals("java.io.FileNotFoundException")) {
          getController().errorMessage(ex.getMessage()); }
       else {
       	  ex.printStackTrace();
          getController().errorMessage(ex); }
    }

    /**
     * Save as; return false is the action was cancelled
     */
    public boolean saveAs() {
        JFileChooser chooser = null;
        if ((getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
            chooser = new JFileChooser(getMap().getFile().getParentFile()); }
        else {
           chooser = new JFileChooser();
           chooser.setSelectedFile(new File(getFileNameProposal()+".mm"));
        }
        //chooser.setLocale(currentLocale);
        if (getFileFilter() != null) {
            chooser.addChoosableFileFilter(getFileFilter()); }
        
        chooser.setDialogTitle(getText("save_as"));
        int returnVal = chooser.showSaveDialog(getView());
        if (returnVal != JFileChooser.APPROVE_OPTION) {// not ok pressed
        	return false; }
        
        // |= Pressed O.K.    
        File f = chooser.getSelectedFile();
        //Force the extension to be .mm
        String ext = Tools.getExtension(f.getName());
        if(!ext.equals("mm")) {
           f = new File(f.getParent(),f.getName()+".mm"); }        
                
        if (f.exists()) { // If file exists, ask before overwriting.
			int overwriteMap = JOptionPane.showConfirmDialog
			   (getView(), getText("map_already_exists"), "FreeMind", JOptionPane.YES_NO_OPTION );
			if (overwriteMap != JOptionPane.YES_OPTION) {
			   return false; }}

		try { // We have to lock the file of the map even when it does not exist yet
		   String lockingUser = getModel().tryToLock(f);
		   if (lockingUser != null) {          
		      getFrame().getController().informationMessage(
			    Tools.expandPlaceholders(getText("map_locked_by_save_as"), f.getName(), lockingUser));
		      return false; }}
		catch (Exception e){ // Throwed by tryToLock
		  getFrame().getController().informationMessage(
		    Tools.expandPlaceholders(getText("locking_failed_by_save_as"), f.getName()));	 
		  return false; } 	        	                              
              
        save(f);
        //Update the name of the map
        getController().getMapModuleManager().updateMapModuleName();        
        return true;
    }
    /**
     * Creates a proposal for a file name to save the map. Removes all illegal
     * characters.
     * 
     * Fixed: When creating file names based on the text of the root node, now all the
     * extra unicode characters are replaced with _. This is not very good. For
     * chinese content, you would only get a list of ______ as a file name. Only
     * characters special for building file paths shall be removed (rather than
     * replaced with _), like : or /. The exact list of dangeous characters
     * needs to be investigated. 0.8.0RC3.
     * 
     * @return
     */
    private String getFileNameProposal() {
        String rootText = ((MindMapNode)getMap().getRoot()).toString();
        rootText = rootText.replaceAll("[&:/\\\\\0%$#~\\?\\*]+", "");
        return rootText;
    }

    /**
     * Return false if user has canceled. 
     */
    public boolean close(boolean force) {
        String[] options = {getText("yes"),
                            getText("no"),
                            getText("cancel")};
        if (!force && !getModel().isSaved()) {
            String text = getText("save_unsaved")+"\n"+getMapModule().toString();
            String title = getText("save");
            int returnVal = JOptionPane.showOptionDialog(getFrame().getContentPane(),text,title,JOptionPane.YES_NO_CANCEL_OPTION,
                                                         JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
            if (returnVal==JOptionPane.YES_OPTION) {
               boolean savingNotCancelled = save();
               if (!savingNotCancelled) {
               	  return false; }}
			else if ((returnVal==JOptionPane.CANCEL_OPTION) || (returnVal == JOptionPane.CLOSED_OPTION)) {
				return false; }}
                
        getModel().destroy();
        return true; }
    


	/* (non-Javadoc)
	 * @see freemind.modes.ModeController#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			MindMapNode node = getSelected();
			for (Iterator j = node.getActivatedHooks().iterator();
				j.hasNext();
				) {
				PermanentNodeHook hook = (PermanentNodeHook) j.next();
				hook.onReceiveFocusHook();
			}
		} else {
			MindMapNode node = getSelected();
			// bug fix, fc 18.5.2004. This should not be here.
			if (node != null) {
                for (Iterator j = node.getActivatedHooks().iterator();
                    j.hasNext();
                    ) {
                    PermanentNodeHook hook = (PermanentNodeHook) j.next();
                    hook.onLooseFocusHook();
                }
            }
		}
	}


    /**
     * Overwrite this to set all of your actions which are
     * dependent on whether there is a map or not.
     */
    protected void setAllActions(boolean enabled) {
        // controller actions:
        getController().zoomIn.setEnabled(enabled);
        getController().zoomOut.setEnabled(enabled);
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
    // Node editing
    //

    private JPopupMenu popupmenu;

    /** listener, that blocks the controler if the menu is active (PN)
        Take care! This listener is also used for modelpopups (as for graphical links).*/
    private class ControllerPopupMenuListener implements PopupMenuListener  {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        setBlocked(true);         // block controller
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        setBlocked(false);        // unblock controller
      }
      public void popupMenuCanceled(PopupMenuEvent e) {
        setBlocked(false);        // unblock controller
      }

    }
    /** Take care! This listener is also used for modelpopups (as for graphical links).*/
    protected final ControllerPopupMenuListener popupListenerSingleton
        = new ControllerPopupMenuListener();
    
    public void showPopupMenu(MouseEvent e) {
      if (e.isPopupTrigger()) {
        JPopupMenu popupmenu = getPopupMenu();
        if (popupmenu != null) {
          // adding listener could be optimized but without much profit...
          popupmenu.addPopupMenuListener( this.popupListenerSingleton );
          popupmenu.show(e.getComponent(),e.getX(),e.getY());
          e.consume();
        }
      }
    }

    /** Default implementation: no context menu.*/
    public JPopupMenu getPopupForModel(java.lang.Object obj) {
        return null;
    }
        


    private static final int SCROLL_SKIPS = 8;
    private static final int SCROLL_SKIP = 10;
    private static final int HORIZONTAL_SCROLL_MASK 
       = InputEvent.SHIFT_MASK | InputEvent.BUTTON1_MASK 
         | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK;
    private static final int ZOOM_MASK 
       = InputEvent.CTRL_MASK;
      // |=   oldX >=0 iff we are in the drag

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (isBlocked()) {
            return; // block the scroll during edit (PN)
       }	
       for (Iterator i = mRegisteredMouseWheelEventHandler.iterator(); i.hasNext();) {
        MouseWheelEventHandler handler = (MouseWheelEventHandler) i.next();
        boolean result = handler.handleMouseWheelEvent(e);
        if(result) {
            // event was consumed:
            return;
        }
    }
        
       if ((e.getModifiers() & ZOOM_MASK) != 0) {
           // fc, 18.11.2003: when control pressed, then the zoom is changed.
           float newZoomFactor = 1f + Math.abs((float) e.getWheelRotation())/10f;
           if(e.getWheelRotation() < 0)
               newZoomFactor = 1 / newZoomFactor;
           float newZoom = ((MapView)e.getComponent()).getZoom() * newZoomFactor;
           // round the value due to possible rounding problems.
           newZoom =  (float) Math.rint(newZoom*1000f)/1000f;
           getController().setZoom(newZoom);
           // end zoomchange
       } else if ((e.getModifiers() & HORIZONTAL_SCROLL_MASK) != 0) {
          for (int i=0; i < SCROLL_SKIPS; i++) {
             ((MapView)e.getComponent()).scrollBy(
                 SCROLL_SKIP * e.getWheelRotation(), 0, false); }}
       else {
          for (int i=0; i < SCROLL_SKIPS; i++) {
             ((MapView)e.getComponent()).scrollBy(0, 
                 SCROLL_SKIP * e.getWheelRotation(), false); }}
    }

    public void registerMouseWheelEventHandler(MouseWheelEventHandler handler) {
        logger.info("Registered   MouseWheelEventHandler "+handler);
        mRegisteredMouseWheelEventHandler.add(handler);
    }
    public void deRegisterMouseWheelEventHandler(MouseWheelEventHandler handler) {
        logger.info("Deregistered MouseWheelEventHandler "+handler);
        mRegisteredMouseWheelEventHandler.remove(handler);
    }
    // this enables from outside close the edit mode
    private FocusListener textFieldListener = null;
    
    
    // status, currently: default, blocked  (PN)
    // (blocked to protect against particular events e.g. in edit mode)
    private boolean isBlocked = false;
    private AttributeController attributeController;

    public boolean isBlocked() {
      return this.isBlocked;
    }
    public void setBlocked(boolean isBlocked) {
      this.isBlocked = isBlocked;
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

    

    public void applyPattern(MindMapNode node, StylePattern pattern) {
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


    public static final int NEW_CHILD_WITHOUT_FOCUS = 1;  // old model of insertion
    public static final int NEW_CHILD = 2;
    public static final int NEW_SIBLING_BEHIND = 3;
    public static final int NEW_SIBLING_BEFORE = 4;

    public MindMapNode addNew(final MindMapNode target, final int newNodeMode, final KeyEvent e) {
    	return newChild.addNew(target, newNodeMode, e);
    }
    
    public 	MindMapNode addNewNode(MindMapNode parent, int index, freemind.main.Tools.BooleanHolder newNodeIsLeft) {
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
	
	public void displayNode(MindMapNode node){
	    find.displayNode(node, null);
	}
	
	public void centerNode(MindMapNode node){
	    	NodeView viewer = node.getViewer();
	    	if(viewer==null || ! viewer.isVisible()){
	    	    displayNode(node);
	    	}
	        // Select the node and scroll to it.
	        getView().centerNode(node.getViewer());
	        getView().selectAsTheOnlyOneSelected(node.getViewer());
	        getController().obtainFocusForSelected();
	}
	
	public String getLinkShortText(MindMapNode node) {
	    return gotoLinkNodeAction.getShortTextForLink(node);
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
        if ((getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
            chooser = new JFileChooser(getMap().getFile().getParentFile());
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
                 load(file); }}
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

    //
    // Convenience methods
    //

    protected Mode getMode() {
        return mode;
    }

    protected void setMode(Mode mode) {
        this.mode = mode;
    }

    //FIXME: Wrong as the actual controller is eventually not used (think of more than one map opened).
    protected MapModule getMapModule() {
        return getController().getMapModuleManager().getMapModule();
    }

    //FIXME: Wrong as the actual controller is eventually not used (think of more than one map opened).
    public MapAdapter getMap() {
        if (getMapModule() != null) {
            return (MapAdapter)getMapModule().getModel();
        } else {
            return null;
        }
    }

    public MindMapNode getRootNode(){
        return (MindMapNode) getMap().getRoot();
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

	/** This was inserted by fc, 10.03.04 to enable all actions to refer to its controller easily.*/
	public ControllerAdapter getModeController() {
		return this;	
	}

	// fc, 29.2.2004: there is no sense in having this private and the controller public,
	// because the getController().getModel() method is available anyway.
    public MapAdapter getModel() {
        return (MapAdapter)getController().getModel();
    }

    public MapView getView() {
        return getController().getView();
    }

    protected void updateMapModuleName() {
        getController().getMapModuleManager().updateMapModuleName();
    }

	/* ***********************************************************
	*  Helper methods
	* ***********************************************************/
	public NodeAdapter getNodeFromID(String nodeID) {
		NodeAdapter node =
			(NodeAdapter) getMap().getLinkRegistry().getTargetForID(nodeID);
		if(node == null) {
		    throw new IllegalArgumentException("Node belonging to the node id "+ nodeID + " not found.");
		}
		return node;
	}
	public String getNodeID(MindMapNode selected) {
		getMap().getLinkRegistry().registerLinkTarget(selected);
		return getMap().getLinkRegistry().getLabel(selected);
	}


    public MindMapNode getSelected() {
    	if(getView() != null && getView().getSelected()!=null)
        	return (MindMapNode)getView().getSelected().getModel();
		return null;        	
    }

    public boolean extendSelection(MouseEvent e) {
        NodeView newlySelectedNodeView = (NodeView)((Component)e.getSource()).getParent();
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

    public void select( NodeView node) {
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
    
	public void invokeHook(ModeControllerHook hook) {
		hook.setController(this);
		// initialize:
		// the main invocation:
		hook.startupMapHook();
		// and good bye.
		hook.shutdownMapHook();
	}
	
	/**
	  *  
	  */
	public void invokeHooksRecursively(NodeAdapter node, MindMap map) {
		 for(Iterator i = node.childrenUnfolded(); i.hasNext();) {
			 NodeAdapter child = (NodeAdapter) i.next();
			 invokeHooksRecursively(child, map);
		 }
		 for(Iterator i = node.getHooks().iterator(); i.hasNext();) {
			 PermanentNodeHook hook = (PermanentNodeHook) i.next();
			 hook.setController(this);
			 hook.setMap(map);
			 node.invokeHook(hook); 
		 }
	}



	public NodeHook createNodeHook(String hookName, MindMapNode node,
			MindMap map) {
		HookFactory hookFactory = getFrame().getHookFactory();
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
	
	public void addHook(MindMapNode focussed, List selecteds, String hookName) {
	    nodeHookAction.addHook(focussed, selecteds, hookName);
	}
	
	
    protected class OpenAction extends AbstractAction {
        ControllerAdapter mc;
        public OpenAction(ControllerAdapter modeController) {
            super(getText("open"), new ImageIcon(getResource("images/fileopen.png")));
            mc = modeController;
        }
        public void actionPerformed(ActionEvent e) {
            mc.open();
			getController().setTitle(); // Possible update of read-only
        }
    }

    protected class SaveAction extends AbstractAction {
        ControllerAdapter mc;
        public SaveAction(ControllerAdapter modeController) {
            super(getText("save"), new ImageIcon(getResource("images/filesave.png")));
            mc = modeController;
        }
        public void actionPerformed(ActionEvent e) {
            mc.save();
            getFrame().out(getText("saved")); // perhaps... (PN)
			getController().setTitle(); // Possible update of read-only
        }
    }

    protected class SaveAsAction extends AbstractAction {
        ControllerAdapter mc;
        public SaveAsAction(ControllerAdapter modeController) {
            super(getText("save_as"), new ImageIcon(getResource("images/filesaveas.png")));
            mc = modeController;
        }
        public void actionPerformed(ActionEvent e) {
            mc.saveAs();
			getController().setTitle(); // Possible update of read-only
        }
    }

	protected class ModeControllerHookAction extends AbstractAction {
		String hookName;
		ModeController controller;
		public ModeControllerHookAction(String hookName, ModeController controller) {
			super(hookName);
			this.hookName = hookName;
			this.controller = controller;
		}

		public void actionPerformed(ActionEvent arg0) {
			HookFactory hookFactory = getFrame().getHookFactory();
			// two different invocation methods:single or selecteds
			ModeControllerHook hook = hookFactory.createModeControllerHook(hookName);
			hook.setController(controller);
			invokeHook(hook);							
		} 
  			
	}


    public String marshall(XmlAction action) {
        return getController().marshall(action);
	}

	public XmlAction unMarshall(String inputString) {
        return getController().unMarshall(inputString);
	}

	protected class EditLongAction extends AbstractAction {
        public EditLongAction() {
            super(getText("edit_long_node"));
        }
        public void actionPerformed(ActionEvent e) {
            edit(null, false, true);
        }
    }

    
        protected class ShowAllAttributesAction extends AbstractAction {
            public ShowAllAttributesAction(){
                super(Resources.getInstance().getResourceString("attributes_show_all"));
            };
            public void actionPerformed(ActionEvent e) {
                final AttributeRegistry attributes = getMap().getRegistry().getAttributes();
                if(attributes.getAttributeViewType() != AttributeTableLayoutModel.SHOW_ALL){
                    attributes.setAttributeViewType(AttributeTableLayoutModel.SHOW_ALL); 
                }
            }
        }
        protected class HideAllAttributesAction extends AbstractAction {
            public HideAllAttributesAction(){
                super(Resources.getInstance().getResourceString("attributes_hide_all"));
            };
            public void actionPerformed(ActionEvent e) {
                final AttributeRegistry attributes = getMap().getRegistry().getAttributes();
                if(attributes.getAttributeViewType() != AttributeTableLayoutModel.HIDE_ALL){
                    attributes.setAttributeViewType(AttributeTableLayoutModel.HIDE_ALL); 
                }
            }
        }

        protected class ShowSelectedAttributesAction extends AbstractAction {
            public ShowSelectedAttributesAction(){
                super(Resources.getInstance().getResourceString("attributes_show_selected"));
            };
            public void actionPerformed(ActionEvent e) {
                final AttributeRegistry attributes = getMap().getRegistry().getAttributes();
                if(attributes.getAttributeViewType() != AttributeTableLayoutModel.SHOW_SELECTED){
                    attributes.setAttributeViewType(AttributeTableLayoutModel.SHOW_SELECTED); 
                }
            }
        }


        protected class EditAttributesAction extends AbstractAction {
            public EditAttributesAction(){
                super(Resources.getInstance().getResourceString("attributes_edit_in_place"));                
            };
            public void actionPerformed(ActionEvent e) {
                final AttributeView attributeView = getView().getSelected().getAttributeView();
                attributeView.edit();
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


    protected class FileOpener implements DropTargetListener {
        private boolean isDragAcceptable(DropTargetDragEvent event) {
            // check if there is at least one File Type in the list
            DataFlavor[] flavors = event.getCurrentDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].isFlavorJavaFileListType()) {
                    //              event.acceptDrag(DnDConstants.ACTION_COPY);
                    return true;
                }
            }
            //      event.rejectDrag();
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


	/**
	 * @return
	 */
	public ObjectFactory getActionXmlFactory() {
		return getController().getActionXmlFactory();
	}

    /**
     * @return
     */
    public Color getSelectionColor() {
        return selectionColor;
    }

    /**
     * @return
     */
    public Clipboard getClipboard() {
        return clipboard;
    }


    /* (non-Javadoc)
     * @see freemind.modes.ModeController#updatePopupMenu(freemind.controller.StructuredMenuHolder)
     */
    public void updatePopupMenu(StructuredMenuHolder holder) {

    }

    /**
     *
     */

    public void shutdownController() {
        setAllActions(false);
    }
    /**
     *
     */

    public void startupController() {
        setAllActions(true);
        if (getFrame().getView() != null) {
            DropTarget dropTarget = new DropTarget(getFrame().getView(),
                    new FileOpener());
        }
    }
    /**
     *
     */

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
	public void moveNodePosition(MindMapNode node, int vGap, int hGap,
            int shiftY) {
	    moveNodeAction.moveNodeTo(node, vGap, hGap, shiftY);
	}

    public void mapChanged(MindMap newMap) {        
    }

    public AttributeController getAttributeController(){
        return attributeController;
    }
    
    
}
