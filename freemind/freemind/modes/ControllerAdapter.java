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
/* $Id: ControllerAdapter.java,v 1.41.14.37.2.8 2006-07-07 04:26:26 christianfoltin Exp $ */

package freemind.modes;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager;
import freemind.controller.StructuredMenuHolder;
import freemind.extensions.PermanentNodeHook;
import freemind.main.FreeMindMain;
import freemind.main.Resources;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.modes.attributes.AttributeController;
import freemind.modes.attributes.AttributeRegistry;
import freemind.modes.attributes.AttributeTableLayoutModel;
import freemind.view.mindmapview.MapView;
import freemind.view.mindmapview.NodeView;
import freemind.view.mindmapview.attributeview.AttributeTable;
import freemind.view.mindmapview.attributeview.AttributeView;


/**
 * Derive from this class to implement the Controller for your mode. Overload the methods
 * you need for your data model, or use the defaults. There are some default Actions you may want
 * to use for easy editing of your model. Take MindMapController as a sample.
 */
public abstract class ControllerAdapter implements ModeController {

	// for cascading updates.
	private HashSet nodesAlreadyUpdated;
	private HashSet nodesToBeUpdated;
	// Logging:
	private static java.util.logging.Logger logger;

	private Mode mode;

    private Color selectionColor = new Color(200,220,200);
	/**
	 * The model, this controller belongs to. It may be null, if
	 * it is the default controller that does not show a map.
	 */
	private MapAdapter mModel;
    private static File lastCurrentDir =  null;

    /** Instanciation order: first me and then the model.
     * @param mode
     */
    public ControllerAdapter(Mode mode) {
        this.setMode(mode);
        if(logger==null) {
        	logger = getFrame().getLogger(this.getClass().getName());
        }
        // for updates of nodes:
		nodesAlreadyUpdated = new HashSet();
		nodesToBeUpdated    = new HashSet();
        DropTarget dropTarget = new DropTarget(getFrame().getViewport(),
                                               new FileOpener());
    }

    public void setModel(MapAdapter model) {
    		mModel = model;
    }

    //
    // Methods that should be overloaded
    //

    public abstract MindMapNode newNode(Object userObject, MindMap map);

    /**
     * You _must_ implement this if you use one of the following actions:
     * OpenAction, NewMapAction.
     * @param modeController TODO
     */
    public MapAdapter newModel(ModeController modeController) {
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
            updateNode(node);
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

    /** Overwrite this method to perform additional operations
     *  to an node update.
     * @param node
     */
    protected void updateNode(MindMapNode node){
    	// empty
    }



    public void anotherNodeSelected(MindMapNode n) {
    }

    //
    // Map Management
    //

    public String getText(String textId) {
       return getController().getResourceString(textId); }

    public MindMap newMap() {
        ModeController newModeController = getMode().createModeController();
		MapAdapter newModel = newModel(newModeController);
        newMap(newModel);
        return newModel;
    }

    public void newMap(MindMap mapModel) {
        getController().getMapModuleManager().newMapModule(mapModel, mapModel.getModeController());
    }

    /**
     * You may decide to overload this or take the default
     * and implement the functionality in your MapModel (implements MindMap)
     */
    public ModeController load (URL file) throws FileNotFoundException, IOException, XMLParseException {
	    	ModeController newModeController = getMode().createModeController();
        MapAdapter model = newModel(newModeController);
		model.load(file);
		newMap(model);
        return newModeController;
    }

    public boolean save() {
        if (getModel().isSaved()) return true;
        if (getModel().getFile() == null || getModel().isReadOnly()) {
           return saveAs(); }
        else {
           return save(getModel().getFile()); }}


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
		String keyProperty = getFrame().getProperty(keystroke);
		logger.finest("Found key stroke: " + keyProperty);
		item.setAccelerator(KeyStroke.getKeyStroke(keyProperty));
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
        JFileChooser chooser = getFileChooser();
        int returnVal = chooser.showOpenDialog(getView());
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            try {
                File theFile = chooser.getSelectedFile();
                lastCurrentDir = theFile.getParentFile();
                load(theFile.toURL());
            } catch (Exception ex) {
               handleLoadingException (ex); } {
            }
        }
        getController().setTitle();
    }

    /** Creates a file chooser with the last selected directory as default.
     * @return
     */
    protected JFileChooser getFileChooser() {
        JFileChooser chooser = new JFileChooser();
        File parentFile = getMapsParentFile();
        // choose new lastCurrentDir only, if not previously set.
        if (parentFile != null && lastCurrentDir == null) {
            lastCurrentDir = parentFile;
        }
        if (lastCurrentDir!= null) {
            chooser.setCurrentDirectory(lastCurrentDir);
        }
        if (getFileFilter() != null) {
            chooser.addChoosableFileFilter(getFileFilter());
        }
        return chooser;
    }

    private File getMapsParentFile(){
        if ((getMap() != null) && (getMap().getFile() != null) && (getMap().getFile().getParentFile() != null)) {
            return getMap().getFile().getParentFile();
        }
        return null;
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
        JFileChooser chooser = getFileChooser();
        if (getMapsParentFile() == null) {
            chooser.setSelectedFile(new File(getFileNameProposal() + ".mm"));
        }
        chooser.setDialogTitle(getText("save_as"));
        int returnVal = chooser.showSaveDialog(getView());
        if (returnVal != JFileChooser.APPROVE_OPTION) {// not ok pressed
        	return false; }

        // |= Pressed O.K.
        File f = chooser.getSelectedFile();
        lastCurrentDir = f.getParentFile();
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
     *
     * Keywords: suggest file name.
     * 
     * @return
     */
    private String getFileNameProposal() {
        String rootText = ((MindMapNode)getMap().getRoot()).getPlainTextContent();
        rootText = rootText.replaceAll("[&:/\\\\\0%$#~\\?\\*]+", "");
        return rootText;
    }

    /**
     * Return false if user has canceled.
     */
    public boolean close(boolean force, MapModuleManager mapModuleManager) {
        String[] options = {getText("yes"),
                            getText("no"),
                            getText("cancel")};
        if (!force && !getModel().isSaved()) {
            String text = getText("save_unsaved")+"\n"+mapModuleManager.getMapModule().toString();
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

    /** Overwrite this, if you have one.
     */
    public Component getLeftToolBar() {
        return null;
    }

    /** Overwrite this, if you have one.
     */
	public JToolBar getModeToolBar() {
		return null;
	}



    // status, currently: default, blocked  (PN)
    // (blocked to protect against particular events e.g. in edit mode)
    private boolean isBlocked = false;

    public boolean isBlocked() {
      return this.isBlocked;
    }
    public void setBlocked(boolean isBlocked) {
      this.isBlocked = isBlocked;
    }



    //
    // Convenience methods
    //

    public Mode getMode() {
        return mode;
    }

    protected void setMode(Mode mode) {
        this.mode = mode;
    }

    public MapAdapter getMap() {
        return mModel;
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
        return mModel;
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



    public class OpenAction extends AbstractAction {
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

    public class SaveAction extends AbstractAction {
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

    public class SaveAsAction extends AbstractAction {
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

        protected class EditAttributesAction extends AbstractAction {
            public EditAttributesAction(){
                super(Resources.getInstance().getResourceString("attributes_edit_in_place"));
            };
            public void actionPerformed(ActionEvent e) {
                final Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                final AttributeView attributeView = getView().getSelected().getAttributeView();
                boolean attributesClosed = null == AttributeView.getAncestorComponent(focusOwner, AttributeTable.class);
                if(attributesClosed){
                    attributeView.startEditing();
                }
                else{
                    attributeView.stopEditing();
                }
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
                    load(file.toURL());
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
    public Color getSelectionColor() {
        return selectionColor;
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

    /** This method is called after and before a change of the map module.
     * Use it to perform the actions that cannot be performed at creation time.
     *
     */
    public void startupController() {
        setAllActions(true);
        if (getFrame().getView() != null) {
            DropTarget dropTarget = new DropTarget(getFrame().getView(),
                    new FileOpener());
        }
    }

    /** Don't call me directly!!!
     * The basic folding method. Without undo.
     */
    public void _setFolded(MindMapNode node, boolean folded) {
        if(node == null)
            throw new IllegalArgumentException("setFolded was called with a null node.");
        // no root folding, fc, 16.5.2004
        if (node.isRoot() && folded) {
            return;
        }
        if (node.isFolded() != folded) {
            node.setFolded(folded);
            nodeStructureChanged(node);
        }
    }
    public String getLinkShortText(MindMapNode node) {
        String adaptedText = node.getLink();
        if(adaptedText== null)
            return null;
        if ( adaptedText.startsWith("#")) {
            try {
                MindMapNode dest = getNodeFromID(adaptedText.substring(1));
                return dest.getShortText(this);
            } catch(Exception e) {
                return getText("link_not_available_any_more");
            }
        }
        return adaptedText;
    }
    public void displayNode(MindMapNode node){
        displayNode(node, null);
    }



    /**
     * Display a node in the display (used by find and the goto action by arrow
     * link actions).
     */
    public void displayNode(MindMapNode node, ArrayList nodesUnfoldedByDisplay) {
        // Unfold the path to the node
        Object[] path = getMap().getPathToRoot(node);
        // Iterate the path with the exception of the last node
        for (int i = 0; i < path.length - 1; i++) {
            MindMapNode nodeOnPath = (MindMapNode) path[i];
            //System.out.println(nodeOnPath);
            if (nodeOnPath.isFolded()) {
                if (nodesUnfoldedByDisplay != null)
                    nodesUnfoldedByDisplay.add(nodeOnPath);
                setFolded(nodeOnPath, false);
            }
        }

    }


    public void centerNode(MindMapNode node){
            if(node.getViewer()==null){
                displayNode(node);
            }
            // Select the node and scroll to it.
            getView().centerNode(node.getViewer());
            getView().selectAsTheOnlyOneSelected(node.getViewer());
            getController().obtainFocusForSelected();
    }

    public File getLastCurrentDir()
    {
        return lastCurrentDir;
    }

    public void setLastCurrentDir(File pLastCurrentDir)
    {
        lastCurrentDir = pLastCurrentDir;
    }


    public AttributeController getAttributeController(){
        return null;
    }

}
