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

package freemind.modes;

import freemind.main.FreeMind;
import freemind.main.Tools;
import freemind.modes.MindMap;
import freemind.modes.MindMapNode;
import freemind.modes.MindMapEdge;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.EventListenerList;
import java.util.Enumeration;
import java.awt.Color;
import java.io.File;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.net.URL;
//XML Specification (Interfaces)
import  org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
//XML Parser, currently apache xerces
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

public abstract class MapAdapter implements MindMap {

    private MindMapNode root;
    private MindMapNode clipboard;
    private EventListenerList treeModelListeners  = new EventListenerList();
    private boolean saved=true;
    private Color backgroundColor;
    private File file;

    
    //
    // Abstract methods that _must_ be implemented.
    //

    public abstract void save(File file); 
    
    public abstract void load(File file);

    public void close() {
    }

    //
    // Attributes
    //

    public boolean isSaved() {
	return saved;
    }

    protected void setSaved(boolean saved) {
	this.saved = saved;
    }

    public Color getBackgroundColor() {
	if (backgroundColor==null) {
	    return Tools.xmlToColor(FreeMind.userProps.getProperty("standardbackgroundcolor"));
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

    protected void setRoot(MindMapNode root) {
	this.root = root;
    }

    /**
     * Change this to always return null if your model doesn't support files.
     */
    public File getFile() {
	return file;
    }

    protected void setFile(File file) {
	this.file = file;
    }

    //
    // Node editing
    //

    public void setFolded( MindMapNode node, boolean folded ) {
	if (node.isFolded()) {
	    if (!folded) {
		//unfold the node
		node.setFolded(false);
		nodeStructureChanged(node);
	    }
	} else {
	    if (folded) {
		//fold the node
		node.setFolded(true);
		nodeStructureChanged(node);
	    }
	}
    }

    public void setLink( NodeAdapter node, URL link ) {
	node.setLink(link);
    }

    public Object[] getPathToRoot( TreeNode node ) {
	return ( ((MindMapNode)node).getPath() ).getPath();//Create Object[] from TreePath
    }
    
    //
    // cut'n'paste
    //
    public void cut(MindMapNode node) {
	clipboard = (MindMapNode)node;
	removeNodeFromParent(node);
    }

    public void paste(MindMapNode parent) {
	if (clipboard != null) {
	    insertNodeInto(clipboard,parent,0);
	    clipboard=null;
	}
    }


    /**
     * Use this method to add children because it will cause the appropriate event.
     */
    public void insertNodeInto(MutableTreeNode newChild,
			       MutableTreeNode parent, int index){
	parent.insert(newChild, index);
	
	int[] newIndexs = new int[1];
	
	newIndexs[0] = index;
	nodesWereInserted(parent, newIndexs);
    }

    /**
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation
     * for you.
     */
    public void removeNodeFromParent(MutableTreeNode node) {
	MutableTreeNode         parent = (MutableTreeNode)node.getParent();
	    
	if(parent == null)
	    throw new IllegalArgumentException("node does not have a parent.");

	int[]            childIndex = new int[1];
	Object[]         removedArray = new Object[1];
	    
	childIndex[0] = parent.getIndex(node);
	parent.remove(node);
	removedArray[0] = node;
	nodesWereRemoved(parent, childIndex, removedArray);
    }

    public void changeNode(MindMapNode node, String newText) {
	node.setUserObject(newText);
	nodeChanged(node);
    }

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
	if(treeModelListeners != null && node != null && childIndices != null
	   && childIndices.length > 0) {
	    setSaved(false);
	    int               cCount = childIndices.length;
	    Object[]          newChildren = new Object[cCount];
	    
	    for(int counter = 0; counter < cCount; counter++)
		newChildren[counter] = node.getChildAt(childIndices[counter]);
	    fireTreeNodesInserted(this, getPathToRoot( (MindMapNode)node ), childIndices, 
				  newChildren);
	}
    }
    
    //
    // TreeNodesChanged
    //

    /**
      * Invoke this method after you've changed how node is to be
      * represented in the tree.
      */
    protected void nodeChanged(TreeNode node) {
        if(treeModelListeners != null && node != null) {
            TreeNode parent = node.getParent();

            if(parent != null) {
                int        anIndex = parent.getIndex(node);
                if(anIndex != -1) {
                    int[]        cIndexs = new int[1];

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
	setSaved(false);
        if(node != null) {
	    if (childIndices != null) {
		int            cCount = childIndices.length;

		if(cCount > 0) {
		    Object[]       cChildren = new Object[cCount];

		    for(int counter = 0; counter < cCount; counter++)
			cChildren[counter] = node.getChildAt
			    (childIndices[counter]);
		    fireTreeNodesChanged(this, getPathToRoot(node),
					 childIndices, cChildren);
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
        if(node != null) {
           fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }

    /**
     * Invoke this method if you've modified the TreeNodes upon which this
     * model depends.  The model will notify all of its listeners that the
     * model has changed below the node <code>node</code> (PENDING).
     */
    protected void reload(TreeNode node) {
        if(node != null) {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }




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
                    e = new TreeModelEvent(source, path, 
                                           childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }          
        }
    }
}








//     private void setFont(NodeModel node, String font) {
// 	node.setFont(font);
// 	nodeStructureChanged(node);
//     }

//     private void setFontSize(NodeModel node, int fontSize) {
// 	node.setFontSize(fontSize);
// 	nodeStructureChanged(node);
//     }

//     private void setUnderlined(NodeModel node) {
// 	if (isUnderlined(node)) {
// 	    node.setUnderlined(false);
// 	} else {
// 	    node.setUnderlined(true);
// 	}
// 	nodeChanged(node);
//     }

//     private void setNormalFont(NodeModel node) {
// 	node.setItalic(false);
// 	node.setBold(false);
// 	node.setUnderlined(false);
// 	nodeChanged(node);
//     }

//     private void setBold(NodeModel node) {
// 	if (isBold(node)) {
// 	    node.setBold(false);
// 	} else {
// 	    node.setBold(true);
// 	}
// 	nodeChanged(node);
//     }

//     private void setItalic(NodeModel node) {
// 	if (isItalic(node)) {
// 	    node.setItalic(false);
// 	} else {
// 	    node.setItalic(true);
// 	}
// 	nodeChanged(node);
//     }

//     private void setEdgeStyle(NodeModel node, String style) {
// 	EdgeModel edge = (EdgeModel)node.getEdge();
// 	edge.setStyle(style);
// 	nodeStructureChanged(node);
//     }

//     private void setNodeStyle(NodeModel node, String style) {
// 	node.setStyle(style);
// 	nodeStructureChanged(node);
//     }

//     private void setNodeColor(NodeModel node, Color color) {
// 	node.setColor(color);
// 	nodeChanged(node);
//     }

//     private void setEdgeColor(NodeModel node, Color color) {
// 	((EdgeModel)node.getEdge()).setColor(color);
// 	nodeChanged(node);
//     }











