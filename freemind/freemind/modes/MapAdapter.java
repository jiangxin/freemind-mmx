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
/* $Id: MapAdapter.java,v 1.24.14.10.2.29 2010/01/25 20:17:59 christianfoltin Exp $ */

package freemind.modes;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.filter.DefaultFilter;
import freemind.controller.filter.Filter;
import freemind.controller.filter.condition.NoFilteringCondition;
import freemind.extensions.PermanentNodeHook;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLParseException;
import freemind.view.mindmapview.MapView;

public abstract class MapAdapter extends DefaultTreeModel implements MindMap {

    private EventListenerList treeModelListeners = new EventListenerList();
    /**
	 * denotes the amount of changes since the last save. The initial value is
	 * zero, such that new models are not to be saved.
	 */
    protected int changesPerformedSinceLastSave = 0;
    protected boolean readOnly = true;
    private File file;
    private FreeMindMain frame;
    static protected Logger logger;
    private MapRegistry registry;
    private Filter filter = null;
	protected final ModeController mModeController;



    public MapAdapter (FreeMindMain frame, ModeController modeController) {
        super(null);
		this.frame = frame;
		this.mModeController = modeController;
		mModeController.setModel(this);
		if(logger == null) {
		    logger = frame.getLogger(this.getClass().getName());
		}
		registry = new MapRegistry(this, modeController);
        filter = new DefaultFilter(NoFilteringCondition.createCondition(), true, false);
    }

    public ModeController getModeController() {
    		return mModeController;
    }

    //
    // Abstract methods that _must_ be implemented.
    //

    public abstract boolean save(File file);

    public abstract void load(URL file) throws FileNotFoundException, IOException, XMLParseException, URISyntaxException ;
    public void load(File file) throws FileNotFoundException, IOException{
    	try {
			load(Tools.fileToUrl(file));
		} catch (XMLParseException e) {
			freemind.main.Resources.getInstance().logException(e);
		} catch (URISyntaxException e) {
			freemind.main.Resources.getInstance().logException(e);
		}
    }

	/**
	 * Attempts to lock the map using semaphore file.
	 * @return If the map is locked, return the name of the locking user, return null
	 * otherwise.
	 * @throws Exception
	 */
	public String tryToLock(File file) throws Exception {
	   return null;
	}

	public void destroy() {
		// Do all the necessary destructions in your model,
		// e.g. remove file locks.
		// and remove all hooks:
		removeNodes( getRootNode());
	}

    // (PN)
    //    public void close() {
    //    }

	/**
	 */
	private void removeNodes(MindMapNode node) {
		while(node.getHooks().size()>0) {
			PermanentNodeHook hook = (PermanentNodeHook) node.getHooks().get(0);
			node.removeHook(hook);
		}
        mModeController.fireNodePreDeleteEvent(node);
		// and all children:
		for(Iterator i= node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
		    removeNodes(child);
		}
	}

	public FreeMindMain getFrame() {
	return frame;
    }

    //
    // Attributes
    //

    public boolean isSaved() {
	return (changesPerformedSinceLastSave==0);
    }

	public boolean isReadOnly() {
		return readOnly;
	}

    /** Counts the amount of actions performed.
     * @param saved true if the file was saved recently. False otherwise.
     */
    public void setSaved(boolean saved) {
		boolean setTitle = false;
        if(saved) {
            changesPerformedSinceLastSave = 0;
            setTitle = true;
        } else {
            if(changesPerformedSinceLastSave == 0) {
                setTitle = true;
            }
            ++changesPerformedSinceLastSave;
        }
        if (setTitle) {
            getModeController().getController().setTitle();
        }        
    }

    protected int getNumberOfChangesSinceLastSave() {
        return changesPerformedSinceLastSave;
    }

    public MindMapNode getRootNode() {
        return (MindMapNode)getRoot();
    }
    
    public void setRoot(MindMapNode root) {
        super.setRoot(root);
    }

    /**
     * Change this to always return null if your model doesn't support files.
     */
    public File getFile() {
	return file;
    }

    /**
     * Return URL of the map (whether as local file or a web location)
     */
    public URL getURL() throws MalformedURLException {
       return getFile() != null ? Tools.fileToUrl(getFile()) : null;
    }


    protected void setFile(File file) {
	this.file = file;
    }

    protected String getText(String textId) {
        return getFrame().getResourceString(textId);
    }
    //
    // Node editing
    //


    public String getAsPlainText(List mindMapNodes) {
       return ""; }


    public String getAsRTF(List mindMapNodes) {
       return ""; }

    public String getAsHTML(List mindMapNodes) {
       return null; }

    public String getRestoreable() {
	return null;
    }

    public MindMapLinkRegistry getLinkRegistry() { return null; }

    /**
      * This method should not be called directly!
      */
    public void nodeChanged(TreeNode node) {
        getModeController().nodeChanged((MindMapNode)node);
    }

    public void nodeRefresh(TreeNode node) {
        getModeController().nodeRefresh((MindMapNode)node);
    }
    /**
     * Invoke this method if you've totally changed the children of
     * node and its childrens children...  This will post a
     * treeStructureChanged event.
     */
    void nodeChangedInternal(TreeNode node){
        if(node != null) {
            fireTreeNodesChanged(this, getPathToRoot(node), null, null);
         }
    }
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param source the node being changed
     * @param path the path to the root node
     * @param childIndices the indices of the changed elements
     * @param children the changed elements
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object source, Object[] path, 
                                        int[] childIndices, 
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        e = fireTreeNodesInserted(source, path, childIndices, children, listeners, e);
        MindMapNode node = (MindMapNode) path[path.length-1];
        fireTreeNodesInserted(source, path, childIndices, children, node.getListeners().getListenerList(), e);
    }

    private TreeModelEvent fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }          
        }
        return e;
    }
    
    protected void fireTreeNodesRemoved(Object source, Object[] path, 
            int[] childIndices, 
            Object[] children) {
//      Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
//      Process the listeners last to first, notifying
//      those that are interested in this event
        e = fireTreeNodesRemoved(source, path, childIndices, children, listeners, e);
        MindMapNode node = (MindMapNode) path[path.length-1];
        fireTreeNodesRemoved(source, path, childIndices, children, node.getListeners().getListenerList(), e);
    }
    
    private TreeModelEvent fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
//              Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }          
        }
        return e;
    }
    protected void fireTreeStructureChanged(Object source, Object[] path, 
            int[] childIndices, 
            Object[] children) {
//      Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
//      Process the listeners last to first, notifying
//      those that are interested in this event
        e = fireTreeStructureChanged(source, path, childIndices, children, listeners, e);
        MindMapNode node = (MindMapNode) path[path.length-1];
        fireTreeStructureChanged(source, path, childIndices, children, node.getListeners().getListenerList(), e);
    }
    
    private TreeModelEvent fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
//              Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }          
        }
        return e;
    }
    protected void fireTreeNodesChanged(Object source, Object[] path, 
            int[] childIndices, 
            Object[] children) {
//      Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
//      Process the listeners last to first, notifying
//      those that are interested in this event
        e = fireTreeNodesChanged(source, path, childIndices, children, listeners, e);
        MindMapNode node = (MindMapNode) path[path.length-1];
        fireTreeNodesChanged(source, path, childIndices, children, node.getListeners().getListenerList(), e);
    }
    
    private TreeModelEvent fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children, Object[] listeners, TreeModelEvent e) {
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
//              Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                            childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }          
        }
        return e;
    }
    public MapRegistry getRegistry() {
        return registry;
    }
    public Filter getFilter() {
        return filter;
    }
    public void setFilter(Filter filter) {
        this.filter = filter;
    }
        
}

