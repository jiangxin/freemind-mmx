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

import java.awt.Color;
import freemind.modes.MindMapEdge;
import freemind.modes.MindMapNode;
import freemind.view.mindmapview.NodeView;
import freemind.main.Tools;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;
import java.net.MalformedURLException;
import freemind.main.FreeMind;
//XML Definition (Interfaces)
import  org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public abstract class NodeAdapter implements MindMapNode {
	
    private Object userObject = "no text";
    private URL link;//Change this to vector in future for full graph support

    //these Attributes have default values, so it can be useful to directly access them in
    //the save() method instead of using getXXX(). This way the stored file is smaller and looks better.
    //(if the default is used, it is not stored) Look at mindmapmode for an example.
    protected String style;
    protected Color color;
    protected boolean folded;
    protected int fontSize;
    protected String font;
    protected boolean bold = false;
    protected boolean italic = false;
    protected boolean underlined = false;

    protected Vector children;

    private MutableTreeNode parent;
    private MindMapEdge edge;//the edge which leads to this node, only root has none
    //In future it has to hold more than one view, maybe with a Vector in which the index specifies
    //the MapView which contains the NodeViews
    private NodeView viewer;
    private static final boolean ALLOWSCHILDREN = true;
    private static final boolean ISLEAF = false; //all nodes may have children

    //
    // Constructors
    //

    protected NodeAdapter() {
    }

    protected NodeAdapter(Object userObject) {
	children = new Vector();
	this.userObject = userObject;
    }

    public URL getLink() {
 	return link;
    }
    
    public void setLink(URL link) {
 	this.link = link;
    }

    //
    // Interface MindMapNode
    //

    //
    // get/set methods
    //

    public NodeView getViewer() {
	return viewer;
    }

    public void setViewer( NodeView viewer ) {
	this.viewer = viewer;
    }

    /** Creates the TreePath recursively */
    public TreePath getPath() {
	Vector pathVector = new Vector();
	TreePath treePath;
	this.addToPathVector( pathVector );
	treePath = new TreePath( pathVector.toArray() );
	return treePath;
    }

    public MindMapEdge getEdge() {
	return edge;
    }

    public void setEdge( MindMapEdge edge ) {
	this.edge = edge;
    }

    /**A Node-Style like "fork" or "bubble"*/
    public String getStyle() {
	if(style==null) {
	    if(this.isRoot()) {
		return FreeMind.userProps.getProperty("standardnodestyle");
	    }
	    return ( (MindMapNode)getParent() ).getStyle();
	}
	return style;
    }


    /**The Foreground/Font Color*/
    public Color getColor() {
	if(color==null) {
	    if(this.isRoot()) {
		String stdcolor = FreeMind.userProps.getProperty("standardnodecolor");
		if (stdcolor.length() == 7) {
		    return Tools.xmlToColor(stdcolor);
		}
		return Color.blue;
	    }
	    return ( (MindMapNode)getParent() ).getColor();
	}
	return color;
    }

    public boolean isBold() {
	return bold;
    }

    public boolean isItalic() {
	return italic;
    }

    public boolean isUnderlined() {
	return underlined;
    }

    public int getFontSize() {
	if (fontSize==0) return Integer.parseInt(FreeMind.userProps.getProperty("standardfontsize"));
	return fontSize;
    }

    /**Maybe implement handling for cases when the font is not available on this system*/
    public String getFont() {
	if (font==null) return FreeMind.userProps.getProperty("standardfont");
	return font;
    }

    public boolean isFolded() {
	return folded;
    }

    public void setFolded(boolean folded) {
	this.folded = folded;
    }
	

    //
    // other
    //

    public String toString() {
	return userObject.toString();
    }
	 
    public boolean isRoot() {
	return (parent==null);
    }

    //
    //  Interface TreeNode
    //

    public Enumeration children() {
	if (isFolded()) {
	    return new Vector().elements();//return empty Enumeration
	}
	return children.elements();
    }
	
    public boolean getAllowsChildren() {
	return ALLOWSCHILDREN;
    }
	
    public TreeNode getChildAt( int childIndex ) {
	if (isFolded()) {
	    return null;
	}
	return (TreeNode)children.elementAt( childIndex );
    }

    public int getChildCount() {
	if (isFolded()) {
	    return 0;
	}
	return children.size();
    }

    public int getIndex( TreeNode node ) {
	return children.indexOf( (MindMapNode)node ); //uses equals()
    } 

    public TreeNode getParent() {
	return parent;
    }

    public boolean isLeaf() {
	return getChildCount() == 0;
    }

    //
    //  Interface MutableTreeNode
    //
    
    //do all remove methods have to work recursively to make the 
    //Garbage Collection work (Nodes in removed Sub-Trees reference each other)?
    
    public void insert( MutableTreeNode child, int index) {
      	children.add( index, child );
    	child.setParent( this );
    }
    
    public void remove( int index ) {
	children.remove( index );
    }
    
    public void remove( MutableTreeNode node ) {
    	children.remove( node );
    }

    public void removeFromParent() {
    	parent.remove( this );
    }

    public void setParent( MutableTreeNode newParent ) {
    	parent = newParent;
    }

    public void setUserObject( Object object ) {
	userObject = object;
    }

    ////////////////
    // Private methods. Internal Implementation
    //////////////

    /** Recursive Method for getPath() */
    private void addToPathVector( Vector pathVector ) {
	pathVector.add( 0, this ); //Add myself to beginning of Vector
	if ( parent != null ) {
	    ( (NodeAdapter)parent ).addToPathVector( pathVector );
	}
    }
}

	





