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
/*$Id: NodeAdapter.java,v 1.19 2003-12-07 21:00:20 christianfoltin Exp $*/

package freemind.modes;

import freemind.modes.MindIcon;
import freemind.main.FreeMindMain;
import freemind.view.mindmapview.NodeView;
import java.util.*;
import java.awt.Color;
import java.awt.Font;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Vector;

/**
 * This class represents a single Node of a Tree. It contains direct handles 
 * to its parent and children and to its view.
 */
public abstract class NodeAdapter implements MindMapNode {
	
    protected Object userObject = "no text";
    private String link = null; //Change this to vector in future for full graph support

    //these Attributes have default values, so it can be useful to directly access them in
    //the save() method instead of using getXXX(). This way the stored file is smaller and looks better.
    //(if the default is used, it is not stored) Look at mindmapmode for an example.
    protected String style;
    /**stores the icons associated with this node.*/
    protected Vector/*<MindIcon>*/ icons = new Vector();
//     /**stores the label associated with this node:*/
//     protected String mLabel;
    /** parameters of an eventually associated cloud*/
    protected MindMapCloud cloud;

    protected Color color;
    protected boolean folded;


    protected List children;
    private MindMapNode preferredChild; 

    protected Font font;
    protected boolean underlined = false;

    private MindMapNode parent;
    private MindMapEdge edge;//the edge which leads to this node, only root has none
    //In future it has to hold more than one view, maybe with a Vector in which the index specifies
    //the MapView which contains the NodeViews
    private NodeView viewer = null;
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

    public MindMapCloud getCloud() {
        return cloud;
    }

    public void setCloud( MindMapCloud cloud ) {
        this.cloud = cloud;
    }

    /**A Node-Style like MindMapNode.STYLE_FORK or MindMapNode.STYLE_BUBBLE*/
    public String getStyle() {
	if (style==null) {
	    if(this.isRoot()) {
		return getFrame().getProperty("standardnodestyle");
	    }
	    return getParentNode().getStyle();
	}
	return style;
    }


    /**The Foreground/Font Color*/
    public Color getColor() {
       return color; }

    //////
    // The set methods. I'm not sure if they should be here or in the implementing class.
    /////

    public void setStyle(String style) {
	this.style = style;
    }

    public void setColor(Color color) {
	this.color = color;
    }

    //   
    //  font handling
    // 

    
    //   Remark to setBold and setItalic implemetation
    //
    // Using deriveFont() is a bad idea, because it does not really choose
    // the appropriate face. For example, instead of choosing face
    // "Arial Bold", it derives the bold face from "Arial".

    // Node holds font only in the case that the font is not default.

    public void estabilishOwnFont() {
       font = (font != null) ? font : getFrame().getController().getDefaultFont(); }

    public void setBold(boolean bold) {
	if (bold != isBold()) {
           toggleBold(); }}

    public void toggleBold() {
       estabilishOwnFont();
       setFont(getFrame().getController().getFontThroughMap
               (new Font(font.getFamily(), font.getStyle() ^ Font.BOLD, font.getSize()))); }

    public void setItalic(boolean italic) {
        if (italic != isItalic()) {
           toggleItalic(); }}

    public void toggleItalic() {
       estabilishOwnFont();
       setFont(getFrame().getController().getFontThroughMap
               (new Font(font.getFamily(), font.getStyle() ^ Font.ITALIC, font.getSize()))); }

    public void setUnderlined(boolean underlined) {
       this.underlined = underlined; }

    public void setFont(Font font) {
       this.font = font; }

    public MindMapNode getParentNode() {
       return parent; }

    public void setFontSize(int fontSize) {
       estabilishOwnFont();
       setFont(getFrame().getController().getFontThroughMap
               (new Font(font.getFamily(), font.getStyle(), fontSize))); }

    public Font getFont() {
       return font; }

    public String getFontSize(){
       if (getFont() != null){
          return new Integer (getFont().getSize()).toString();}
       else {
          return getFrame().getProperty("defaultfontsize");}
    }

    public String getFontFamilyName(){
       if (getFont() != null){
          return getFont().getFamily();}
       else {
          return getFrame().getProperty("defaultfont");}
    }

    public boolean isBold() {
       return font != null ? font.isBold() : false; }

    public boolean isItalic() {
       return font != null ? font.isItalic() : false; }

    public boolean isUnderlined() {        // not implemented
       return underlined; }

    public boolean isFolded() {
       return folded; }

    // fc, 24.9.2003:
    public Vector/*<MindIcon>*/ getIcons() { return icons;};

    public void   addIcon(MindIcon _icon) { icons.add(_icon); };

    public int   removeLastIcon() { if(icons.size() > 0) icons.setSize(icons.size()-1); return icons.size();};

    // end, fc, 24.9.2003

//     public     String getLabel() { return mLabel; }

//     public     void setLabel(String newLabel) { mLabel = newLabel; /* bad hack: registry fragen.*/ };

//     public Vector/* of NodeLinkStruct*/ getReferences() { return mNodeLinkVector; };

//     public void removeReferenceAt(int i) {
//         if(mNodeLinkVector.size() > i) {
//             mNodeLinkVector.removeElementAt(i);
//         } else {
//             /* exception. */
//         }
//     }
    
//     public     void addReference(MindMapLink mindMapLink) { mNodeLinkVector.add(mindMapLink); };


    /**
     *  True iff one of node's <i>strict</i> descendants is folded. A node N
     *  is not its strict descendant - the fact that node itself is folded
     *  is not sufficient to return true.
     */
    public boolean hasFoldedStrictDescendant() {
       
       for (ListIterator e = childrenUnfolded(); e.hasNext(); ) {
          NodeAdapter child = (NodeAdapter)e.next();
          if (child.isFolded() || child.hasFoldedStrictDescendant()) {
             return true; }}

	return false;
    }

    public void setFolded(boolean folded) {
	this.folded = folded;
    }

    protected MindMapNode basicCopy() {
       return null; }
	
    public MindMapNode shallowCopy() {
       MindMapNode copy = basicCopy();
       copy.setColor(getColor());
       copy.setFont(getFont());
       copy.setLink(getLink());
       Vector icons = getIcons();
       for(int i = 0; i < icons.size(); ++i) {
           copy.addIcon((MindIcon) icons.get(i));
       }
       return copy; }

    //
    // other
    //

    public String toString() {
	String string = userObject.toString();
// (PN) %%%
// Why? If because of presentation, this level shall be self responsible... 
// Model shall NOT change the real data!!!
//
//	if (string.equals("")) {
//	    string = "   ";
//	}
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

    public boolean hasChildren() {
        return children != null && !children.isEmpty(); }

    public int getChildPosition(MindMapNode childNode) {
       int position = 0;
       for (ListIterator i=children.listIterator(); i.hasNext(); ++position) {
          if (((MindMapNode)i.next()) == childNode) {
             return position; }}
       return -1;
    }

    public ListIterator childrenUnfolded() {
        return children != null ? children.listIterator() :
           Collections.EMPTY_LIST.listIterator(); }

    public ListIterator childrenFolded() {
       if (isFolded()) {
          return Collections.EMPTY_LIST.listIterator();
       }
       return childrenUnfolded();
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

    public int getChildCount() {
       return children == null ? 0 : children.size();
    }

// (PN)
//    public int getChildCount() {
//	if (isFolded()) {
//	    return 0;
//	}
//	return children.size();
//    }
//    // Daniel: ^ The name of this method is confusing. It does nto convey
//    // the meaning, at least not to me.

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
        if (index < 0) { // add to the end (used in xml load) (PN) 
          index = getChildCount();
          children.add( index, child );
        }
        else {           // mind preferred child :-)
          children.add( index, child );
          preferredChild = (MindMapNode)child;
        }
    	child.setParent( this );
    }
    
    
    public void remove( int index ) {
        MutableTreeNode node = (MutableTreeNode)children.get(index);
        if (node == this.preferredChild) { // mind preferred child :-) (PN) 
          if (children.size() > index + 1) {
            this.preferredChild = (MindMapNode)(children.get(index + 1));
          }
          else {
            this.preferredChild = (index > 0) ? 
                (MindMapNode)(children.get(index - 1)) : null;
          }
        }
        node.setParent(null);
        children.remove( index );
    }
    
    public void remove( MutableTreeNode node ) {
        if (node == this.preferredChild) { // mind preferred child :-) (PN) 
          int index = children.indexOf(node);
          if (children.size() > index + 1) {
            this.preferredChild = (MindMapNode)(children.get(index + 1));
          }
          else {
            this.preferredChild = (index > 0) ? 
                (MindMapNode)(children.get(index - 1)) : null;
          }
        }
        node.setParent(null);
    	children.remove( node );
    }
    
    public MindMapNode getPreferredChild() { // mind preferred child :-) (PN) 
      if (this.children.contains(this.preferredChild)) {
        return this.preferredChild;
      }
      else if (!isLeaf()) {
        return (MindMapNode)(this.children.get((getChildCount() + 1) / 2 - 1));
      }
      else {
        return null;
      }
    }
    public void setPreferredChild(MindMapNode node) {
      this.preferredChild = node;
      if (node == null) {
        return;
      }
      else if (this.parent != null) {
        // set also preffered child of parents...
        this.parent.setPreferredChild(this);
      }
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

    public int getNodeLevel() {  //for cursor navigation within a level (PN)  
      int level = 0;
      MindMapNode parent;
      for (parent = this; !parent.isRoot(); parent = parent.getParentNode()) {
        level++;
      }
      return level;
    }
}
