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
/*$Id: NodeAdapter.java,v 1.12 2003-11-03 10:15:45 sviles Exp $*/

package freemind.modes;

import freemind.main.FreeMind;
import freemind.main.FreeMindMain;
import freemind.main.Tools;
import freemind.controller.Controller;
import freemind.view.mindmapview.NodeView;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.net.URL;
import java.awt.Color;
import java.awt.Font;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public abstract class NodeAdapter implements MindMapNode {
	
    private Object userObject = "no text";
    private String link;//Change this to vector in future for full graph support

    //these Attributes have default values, so it can be useful to directly access them in
    //the save() method instead of using getXXX(). This way the stored file is smaller and looks better.
    //(if the default is used, it is not stored) Look at mindmapmode for an example.
    protected String style;
    protected Color color;
    protected boolean folded;


    protected List children;

    protected Font font;
    protected boolean underlined = false;
//      protected int fontSize;
//      protected String font;
//      protected boolean bold = false;
//      protected boolean italic = false;


    private MindMapNode parent;
    private MindMapEdge edge;//the edge which leads to this node, only root has none
    //In future it has to hold more than one view, maybe with a Vector in which the index specifies
    //the MapView which contains the NodeViews
    private NodeView viewer;
    private FreeMindMain frame;
    private static final boolean ALLOWSCHILDREN = true;
    private static final boolean ISLEAF = false; //all nodes may have children

    //
    // Constructors
    //

    protected NodeAdapter(FreeMindMain frame) {
	this.frame = frame;
    }

    protected NodeAdapter(Object userObject, FreeMindMain frame) {
	this.userObject = userObject;
	this.frame = frame;
    }

    public String getLink() {
 	return link;
    }
    
    public void setLink(String link) {
 	this.link = link;
    }

    public FreeMindMain getFrame() {
	return frame;
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
		return getFrame().getProperty("standardnodestyle");
	    }
	    return getParentNode().getStyle();
	}
	return style;
    }


    /**The Foreground/Font Color*/
    public Color getColor() {
	if(color==null) {
	    String stdcolor = getFrame().getProperty("standardnodecolor");
	    if (stdcolor.length() == 7) {
		return Tools.xmlToColor(stdcolor);
	    }
	    return Color.blue;
	}
	return color;
    }

    //////
    // The set methods. I'm not sure if they should be here or in the implementing class.
    /////

    public void setStyle(String style) {
	this.style = style;
    }

    public void setColor(Color color) {
	this.color = color;
    }
    
    public void setBold(boolean bold) {
	// ** use font object
	// this.bold = bold;
	if(bold && font.isBold()) return;
	if(!bold && !font.isBold()) return;

	if(bold) setFont(font.deriveFont(font.getStyle()+Font.BOLD));
	if(!bold) setFont(font.deriveFont(font.getStyle()-Font.BOLD));
    }

    public void setItalic(boolean italic) {
	// ** use font object
	// this.italic = italic;

	if(italic && font.isItalic()) return;
	if(!italic && !font.isItalic()) return;

	if(italic) setFont(font.deriveFont(font.getStyle()+Font.ITALIC));
	if(!italic) setFont(font.deriveFont(font.getStyle()-Font.ITALIC));
    }

    public void setUnderlined(boolean underlined) {
	this.underlined = underlined;
    }

    public void setFont(Font font) {
	this.font = font;
    }

    public MindMapNode getParentNode() {
       return parent;
    }     

    // **
    // ** font handling
    // **

    public Font getFont() {
	if(font == null) {
	    // ** Maybe implement handling for cases when
	    //    the font is not available on this system

	    int fontSize = Integer.parseInt(getFrame().getProperty("standardfontsize"));
	    int fontStyle = Integer.parseInt(getFrame().getProperty("standardfontstyle"));
	    String fontName = getFrame().getProperty("standardfont");

	    font = new Font(fontName, fontStyle, fontSize);
	}
	return font;
    }

    public boolean isBold() {
	if(font == null) getFont(); // ** initialize font
	return font.isBold();
    }

    public boolean isItalic() {
	if(font == null) getFont(); // ** initialize font
	return font.isItalic();
    }

    public boolean isUnderlined() {
	return underlined;
    }


    // ** much better to use the java.awt.Font object (Sebastian)

//      public int getFontSize() {
//  	if (fontSize==0) return Integer.parseInt(getFrame().getProperty("standardfontsize"));
//  	return fontSize;
//      }

    /**Maybe implement handling for cases when the font is not available on this system*/
//      public String getFont() {
//  	if (font==null) return getFrame().getProperty("standardfont");
//  	return font;
//      }

    public boolean isFolded() {
	return folded;
    }

    public boolean hasFoldedStrictDescendant() {
       /**
        *  True iff one of node's <i>strict</i> descendants is folded. A node N is not its strict descendant - 
        *  the fact that node itself is folded is not sufficient to return true.
        */
       
       for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
          NodeAdapter child = (NodeAdapter)e.next();
          if (child.isFolded() || child.hasFoldedStrictDescendant()) {
             return true; }}

	return false;
    }

    public void setFolded(boolean folded) {

	this.folded = folded;
    }
	

    //
    // other
    //

    public String toString() {
	String string = userObject.toString();
	if (string.equals("")) {
	    string = "   ";
	}
	return string;
    }

    /**
     * Returns whether the argument is parent
     * or parent of one of the grandpa's of this node.
     * (transitive)
     */
    public boolean isDescendantOf(MindMapNode node) {
	if(this.isRoot())
	    return false;
	else if (node == getParentNode())
	    return true;
	else
	    return getParentNode().isDescendantOf(node);
    }	    
	 
    public boolean isRoot() {
	return (parent==null);
    }

    public ListIterator childrenUnfolded() {
	return children.listIterator();
    }

    public boolean hasChildren() {
       return !children.isEmpty(); }

    public int getChildPosition(MindMapNode childNode) {
       int position = 0;
       for (ListIterator i=children.listIterator(); i.hasNext(); ++position) {
          if (((MindMapNode)i.next()) == childNode) {
             return position; }}
       return -1;
    }

    public ListIterator childrenFolded() {
	if (isFolded()) {
	    return null;//return empty Enumeration
	}
	return children.listIterator();
    }

    //
    //  Interface TreeNode
    //

    /**
     * AFAIK there is no way to get an enumeration out of a linked list. So this exception must be
     * thrown, or we can't implement TreeNode anymore (maybe we shouldn't?)
     */
    public Enumeration children() {
	throw new UnsupportedOperationException("Use childrenFolded or childrenUnfolded instead");
    }
	
    public boolean getAllowsChildren() {
	return ALLOWSCHILDREN;
    }
	
    public TreeNode getChildAt( int childIndex ) {
	if (isFolded()) {
	    return null;
	}
	return (TreeNode)children.get( childIndex );
    }

    public int getRealChildCount() {
	return children.size();
    }

    public int getChildCount() {
	if (isFolded()) {
	    return 0;
	}
	return children.size();
    }
    // Daniel: ^ The name of this method is confusing. It does nto convey
    // the meaning, at least not to me.

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
        parent = (MindMapNode) newParent;
    }

    public void setParent( MindMapNode newParent ) {
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
