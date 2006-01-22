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
/*$Id: MapAdapter.java,v 1.24.14.7.2.1.2.5 2006-01-22 12:24:38 dpolivaev Exp $*/

package freemind.modes;

import java.awt.Color;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import freemind.controller.MindMapNodesSelection;
import freemind.controller.filter.Filter;
import freemind.extensions.PermanentNodeHook;
import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.main.XMLParseException;

public abstract class MapAdapter implements MindMap {

    private MindMapNode root;
    private EventListenerList treeModelListeners = new EventListenerList();
    /** denotes the amount of changes since the last save. The initial value is one, to ensure, that the model is dirty.*/
    protected int changesPerformedSinceLastSave = 1; 
    protected boolean readOnly = true;
    private Color backgroundColor;
    private File file;
    private FreeMindMain frame;
    static protected Logger logger;
    private MapRegistry registry;
    private Filter filter = null;


    public MapAdapter (FreeMindMain frame, ModeController modeController) {
		this.frame = frame;
		if(logger == null) {
		    logger = frame.getLogger(this.getClass().getName());
		}
		registry = new MapRegistry(this, modeController);
    }

    //
    // Abstract methods that _must_ be implemented.
    //

    public abstract boolean save(File file); 
    
    public abstract void load(File file) throws FileNotFoundException, IOException, XMLParseException ;

	/**
	 * Attempts to lock the map using semaphore file.
	 * @param file
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
		removeHooks((MindMapNode) getRoot());
	}

    // (PN)
    //    public void close() {
    //    }

	/**
	 * @param node
	 */
	private void removeHooks(MindMapNode node) {
		while(node.getHooks().size()>0) {
			PermanentNodeHook hook = (PermanentNodeHook) node.getHooks().get(0);
			node.removeHook(hook);
		}
		// and all children:
		for(Iterator i= node.childrenUnfolded(); i.hasNext();) {
			MindMapNode child = (MindMapNode) i.next();
		    removeHooks(child);
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
    protected void setSaved(boolean saved) {
        if(saved)
            changesPerformedSinceLastSave = 0;
        else
            ++changesPerformedSinceLastSave;
    }

    protected int getNumberOfChangesSinceLastSave() {
        return changesPerformedSinceLastSave;
    }

    public Color getBackgroundColor() {
	if (backgroundColor==null) {
	    return Tools.xmlToColor(getFrame().getProperty(FreeMind.RESOURCES_BACKGROUND_COLOR));
	}
	return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
	this.backgroundColor = backgroundColor;
	nodeChanged((MindMapNode)getRoot());//update view anyhow
    }

    public Object getRoot() {
	return root;
    }

    public void setRoot(MindMapNode root) {
	this.root = root;
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
       return getFile() != null ? getFile().toURL() : null;
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

 
    public Object[] getPathToRoot( TreeNode node ) {
	return ( ((MindMapNode)node).getPath() ).getPath();//Create Object[] from TreePath
    }

    //
    // cut'n'paste
    //
    public final Transferable cut(MindMapNode node) {
       // This cut does not need any reimplementation in child Classes!
       Transferable t = copy(node);
       removeNodeFromParent(node);
       return t;
    }


   public Transferable copy(MindMapNode node) {
     return null;
   }

   public Transferable copy() {
      return copy(getFrame().getView().getSelectedNodesSortedByY(), null); }

   public Transferable copySingle() {
      MindMapNode selectedNode = (MindMapNode)getFrame().getView().getSelected().getModel();
      ArrayList selectedNodes  = new ArrayList();
      selectedNodes.add(selectedNode.shallowCopy());
      return copy(selectedNodes, selectedNode.toString()); }

   public Transferable copy(List selectedNodes, String inPlainText) {
      try {
         String forNodesFlavor = "";
         boolean firstLoop = true;
         for(Iterator it = selectedNodes.iterator();it.hasNext();) {
            MindMapNode tmpNode =  (MindMapNode)it.next();
            if (firstLoop) {
               firstLoop = false; }
            else {
               forNodesFlavor += "<nodeseparator>"; }

            forNodesFlavor += copy(tmpNode).getTransferData(MindMapNodesSelection.mindMapNodesFlavor); 
         }

         String plainText = inPlainText != null ? inPlainText : getAsPlainText(selectedNodes);
         return new MindMapNodesSelection
            (forNodesFlavor, plainText, getAsRTF(selectedNodes), null, null, null); }
         //return new StringSelection(forClipboard); }


      catch (UnsupportedFlavorException ex) { ex.printStackTrace(); }
      catch (IOException ex) { ex.printStackTrace(); }
      return null; }

    public String getAsPlainText(List mindMapNodes) {
       return ""; }
 

    public String getAsRTF(List mindMapNodes) {
       return ""; }


    public String getRestoreable() {
	return null;
    }

    public MindMapLinkRegistry getLinkRegistry() { return null; }


    public void insertNodeIntoNoEvent(MindMapNode newChild, MindMapNode parent) {
       insertNodeIntoNoEvent(newChild, parent, false); }

    public void insertNodeIntoNoEvent(MindMapNode newChild, MindMapNode parent,
                                      boolean asSibling){
        // Daniel: This is a hackery in the sense that I don't
        // understand the method "nodesWereInserted".

        // This code is based on empirical observation that in
        // some cases avoiding the mentioned method does not throw
        // exception.

        // The reason why we do such a thing at all is that calling the
        // method "nodesWereInserted" dramatically lowers the performance.

        // The default position is after the last node
        if (asSibling) {
            MindMapNode oldpa = parent.getParentNode();
            oldpa.insert(newChild, oldpa.getChildPosition(parent));
            recursiveCallAddChildren(oldpa, newChild);
        } 
        else {
            parent.insert(newChild, parent.getChildCount());
            recursiveCallAddChildren(parent, newChild);
        }
        
    }

    public void insertNodeInto(MindMapNode newChild,
			       MindMapNode parent){
        // The default position is after the last node
        insertNodeInto(newChild, parent, parent.getChildCount());
    }

	public void insertNodeInto(
		MindMapNode newChild,
		MindMapNode parent,
		int index) {
		insertNodeInto((MutableTreeNode) newChild, (MutableTreeNode) parent, index);
		recursiveCallAddChildren(parent, newChild);
	}

    /**
     * Use this method to add children because it will cause the appropriate event.
     */
    public void insertNodeInto(MutableTreeNode newChild,
			       MutableTreeNode parent, int index){
        parent.insert(newChild, index);
	nodesWereInserted(parent, new int[]{ index });
        // ^ Daniel: I don't understand the reason why this is here.  But it
        // is necessary, because user's action "New Node" throws
        // an exception otherwise.
    }

	/**URGENT: This method must be moved to the ControllerAdapter.
	 * @param node
	 */
	private void recursiveCallAddChildren(MindMapNode node, MindMapNode addedChild) {
		// Tell any node hooks that the node is added:
		if(node instanceof MindMapNode) {
			for(Iterator i=  ((MindMapNode)node).getActivatedHooks().iterator(); i.hasNext();) {
				PermanentNodeHook hook = (PermanentNodeHook) i.next();
                if (addedChild.getParentNode() == node) {
                    hook.onAddChild(addedChild);
                }
                hook.onAddChildren(addedChild);
			}
		}
		if(!node.isRoot() && node.getParentNode()!= null)
		    recursiveCallAddChildren(node.getParentNode(), addedChild);
	}

    
    /**
     * Joerg: Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     */
    public void removeNodeFromParent(MutableTreeNode node) {
        removeNodeFromParent(node, /*notify=*/ true); }

    public void removeNodeFromParent(MutableTreeNode node, boolean notify) {
	MutableTreeNode parent = (MutableTreeNode)node.getParent();
	    
	if (parent == null)
           throw new IllegalArgumentException("node does not have a parent.");

	int[] childIndex = new int[1];
	Object[] removedArray = new Object[1];
	    
	childIndex[0] = parent.getIndex(node);
	parent.remove(node);
        if (notify) {
           removedArray[0] = node;
           nodesWereRemoved(parent, childIndex, removedArray); }}

//    public void changeNode(MindMapNode node, String newText) {
//	node.setUserObject(newText);
//	nodeChanged(node);
//    }

    //
    // Interface TreeModel
    //

    public Object getChild( Object parent, int index ) {
	return ( (TreeNode)parent ).getChildAt( index );
    }

    public int getChildCount( Object parent ) {
	return ( (TreeNode)parent ).getChildCount();
    }

    public int getIndexOfChild( Object parent, Object child ) {
	return ( (TreeNode)parent ).getIndex( (TreeNode)child );
    }
	
    public boolean isLeaf( Object node ) {
	return ( (TreeNode)node ).isLeaf();
    }

    public void addTreeModelListener( TreeModelListener l ) {
	treeModelListeners.add( TreeModelListener.class, l );
    }

    public void removeTreeModelListener( TreeModelListener l ) {
	treeModelListeners.remove( TreeModelListener.class, l );
    }

    public void valueForPathChanged( TreePath path, Object newValue ) {
	( (MutableTreeNode)path.getLastPathComponent() ).setUserObject( newValue );
    }


    //
    // API for updating the view.
    //

    //
    // TreeNodesRemoved
    //

    /**
     * Invoke this method after you've removed some TreeNodes from
     * node.  childIndices should be the index of the removed elements and
     * must be sorted in ascending order. And removedChildren should be
     * the array of the children objects that were removed.
     */
    protected void nodesWereRemoved(TreeNode parent, int[] childIndices,
				 Object[] removedChildren) {
	setSaved(false);
	if(parent != null && childIndices != null) {
	    fireTreeNodesRemoved(this, getPathToRoot( (MindMapNode)parent ), childIndices, 
				 removedChildren);
	}
    }

    //
    // TreeNodesInserted
    //

    /**
     * Invoke this method after you've inserted some TreeNodes into
     * node.  childIndices should be the index of the new elements and
     * must be sorted in ascending order.
     */
    protected void nodesWereInserted(TreeNode node, int[] childIndices) {
       if (treeModelListeners != null && node != null && 
           childIndices != null && childIndices.length > 0) {
          setSaved(false);

	  Object[] newChildren = new Object[childIndices.length];	    
          for(int counter = 0; counter < childIndices.length; counter++) {
             newChildren[counter] = node.getChildAt(childIndices[counter]); }

          fireTreeNodesInserted(this, getPathToRoot( (MindMapNode)node ), childIndices, newChildren);
	}
    }
    
    //
    // TreeNodesChanged
    //

    /**
      * This method should not be called directly!
      */
    public void nodeChanged(TreeNode node) {
        frame.getController().getMode().getModeController().nodeChanged((MindMapNode)node);
    }

    public void nodeRefresh(TreeNode node) {
        frame.getController().getMode().getModeController().nodeRefresh((MindMapNode)node);
    }

	public void nodeChangedMapInternal(TreeNode node) {
		if (treeModelListeners != null && node != null) {
			TreeNode parent = node.getParent();

			if (parent != null) {
				int anIndex = parent.getIndex(node);
				if (anIndex != -1) {
					int[] cIndexs = new int[1];

					cIndexs[0] = anIndex;
					nodesChanged(parent, cIndexs);
				}
			}
			else if (((MindMapNode)node).isRoot()) {
				nodesChanged(node, null);
			}
		}
	}


	/**
      * Invoke this method after you've changed how the children identified by
      * childIndicies are to be represented in the tree.
      */
    protected void nodesChanged(TreeNode node, int[] childIndices) {

       if (node != null) {
          if (childIndices != null) {
             int cCount = childIndices.length;
             
             if (cCount > 0) {
                Object[] cChildren = new Object[cCount];
                
                for(int counter = 0; counter < cCount; counter++) {
                   cChildren[counter] = node.getChildAt
                      (childIndices[counter]);
                   logger.finest("Children array at "+counter+" = "+ cChildren[counter]);
                }
                fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
             }
          }
          else if (((MindMapNode)node).isRoot()) {
             fireTreeNodesChanged(this, getPathToRoot(node), null, null);
          }
       }
    }

    //
    // TreeStructureChanged
    //    

    /**
      * Invoke this method if you've totally changed the children of
      * node and its childrens children...  This will post a
      * treeStructureChanged event.
      */
    protected void nodeStructureChanged(TreeNode node) {
        setSaved(false);
        if (node != null) {
           fireTreeStructureChanged(this, getPathToRoot(node), null, null); }}

    /**
     * Invoke this method if you've modified the TreeNodes upon which this
     * model depends.  The model will notify all of its listeners that the
     * model has changed below the node <code>node</code> (PENDING).
     */
    protected void reload(TreeNode node) {
        if (node != null) {
           fireTreeStructureChanged(this, getPathToRoot(node), null, null); }}


    /////////
    // private methods. Internal implementation.
    ////////

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    private void fireTreeNodesRemoved(Object source, Object[] path, 
					int[] childIndices, 
					Object[] children) {
	// Guaranteed to return a non-null array
	Object[] listeners = treeModelListeners.getListenerList();
	TreeModelEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TreeModelListener.class) {
		// Lazily create the event:
		if (e == null)
		    e = new TreeModelEvent(source, path, 
					   childIndices, children);
		((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
	    }          
	}
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    private void fireTreeNodesInserted(Object source, Object[] path, 
					 int[] childIndices, 
					 Object[] children) {
	// Guaranteed to return a non-null array
	Object[] listeners = treeModelListeners.getListenerList();
	TreeModelEvent e = null;
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }          
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    private void fireTreeNodesChanged(Object source, Object[] path, 
                                        int[] childIndices, 
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = treeModelListeners.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }          
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    private void fireTreeStructureChanged(Object source, Object[] path, 
                                        int[] childIndices, 
                                        Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = treeModelListeners.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }          
        }
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

