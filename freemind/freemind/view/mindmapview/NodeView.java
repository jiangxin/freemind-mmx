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
/*$Id: NodeView.java,v 1.3 2000-10-17 17:20:29 ponder Exp $*/

package freemind.view.mindmapview;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JLabel;

/**
 * This class represents a single Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public abstract class NodeView extends JLabel {

    protected MindMapNode model;
    protected MapView map;
    protected EdgeView edge;
    protected final static Color selectedColor = Color.darkGray;//the Color of the Rectangle of a selected Node
    //caching of treeheight
    protected int treeHeight;
//     protected boolean treeheightCurrent = true;
    private boolean left = true; //is the node left of root?
    int relYPos;//the relative Y Position to it's parent


    //
    // Constructors
    //
    
    protected NodeView(MindMapNode model, MapView map) {
	this.model = model;
	setMap(map);

	//Root has no edge
	if (!isRoot()) {
	    if (getModel().getEdge().getStyle().equals("linear")) {
		edge = new LinearEdgeView(getParentView(),this);
	    } else if (getModel().getEdge().getStyle().equals("bezier")) {
		edge = new BezierEdgeView(getParentView(),this);
	    } else {
		System.out.println("Panic! Unknown Edge Type.");
	    }
	}
	addMouseListener( map.getNodeMouseListener() );
	addKeyListener( map.getNodeKeyListener() );
    }

    /**
     * Factory method which creates the right NodeView for the model.
     */ 
    protected static NodeView newNodeView(MindMapNode model, MapView map) {
	NodeView newView;
	if (model.isRoot()) {
	    newView = new RootNodeView( model, map );
	} else if (model.getStyle().equals("fork") ) {
	    newView = new ForkNodeView( model, map );
	} else if (model.getStyle().equals("bubble") ) {
	    newView = new BubbleNodeView( model, map );
	} else {
	    System.err.println("Panic! Tried to create a NodeView of unknown Style!");
	    newView = new ForkNodeView(model, map);
	}
	model.setViewer(newView);
	map.add(newView);
	newView.update();
	return newView;
    }

    //
    // public methods
    //

    public boolean isRoot() {
	return model.isRoot();
    }

    public MindMapNode getModel() {
	return model;
    }

    public void paint(Graphics graphics) {
	super.paint(graphics);
    }

    //
    // get/set methods
    //

//     protected boolean isTreeheightCurrent() {
// 	return treeheightCurrent;
//     }

//     protected void setTreeheightCurrent(boolean current) {
// 	treeheightCurrent = current;
// 	if(!current) {
// 	    getParentView().setTreeheightCurrent(false);
// 	}
//     }

    int getTreeHeight() {
	return treeHeight;
    }

    void setTreeHeight(int treeHeight) {
	this.treeHeight = treeHeight;
    }

    protected boolean isSelected() {
	return (getMap().getSelected() == this);
    }

    /**Is the node left of root?*/
    protected boolean isLeft() {
	return left;
    }

    protected void setLeft(boolean left) {
	this.left = left;
    }
	
    protected void setModel( MindMapNode model ) {
	this.model = model;
    }

    MapView getMap() {
	return map;
    }

    protected void setMap( MapView map ) {
	this.map = map;
    }

    EdgeView getEdge() {
	return edge;
    }

    void setEdge(EdgeView edge) {
	this.edge = edge;
    }

    protected NodeView getParentView() {
	return ( (MindMapNode)getModel().getParent() ).getViewer();
    }

    /**
     * This method returns the NodeViews that are children of this node.
     */
    protected Vector getChildrenViews() {
	Vector childrenViews = new Vector();
	for ( Enumeration e = getModel().children();e.hasMoreElements(); ) {
	    MindMapNode child = (MindMapNode)e.nextElement();
	    childrenViews.add( child.getViewer() );
	}
	return childrenViews;
    }
    

    /**
     * Returns the Point where the OutEdge
     * should leave the Node.
     * THIS SHOULD BE DECLARED ABSTRACT AND BE DONE IN BUBBLENODEVIEW ETC.
     */
    Point getOutPoint() {
	Dimension size = getSize();
	if( isRoot() ) {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height / 2);
	} else if( isLeft() ) {
	    return new Point(getLocation().x, getLocation().y + size.height - 2);
	} else {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height - 2);
	} 
    }

    /**
     * Returns the Point where the InEdge
     * should arrive the Node.
     * THIS SHOULD BE DECLARED ABSTRACT AND BE DONE IN BUBBLENODEVIEW ETC.
     */
    Point getInPoint() {
	Dimension size = getSize();
	if( isRoot() ) {
	    return new Point(getLocation().x, getLocation().y + size.height / 2);
	} else if( isLeft() ) {
	    return new Point(getLocation().x + size.width, getLocation().y + size.height - 2);
	} else {
	    return new Point(getLocation().x, getLocation().y + size.height - 2);
	} 
    }

    //
    // Navigation
    //

    protected NodeView getNextSibling() {
	Vector v = getParentView().getChildrenViews();
	if (getParentView().isRoot()) {
	    if (this.isLeft()) {
		v = ((RootNodeView)getParentView()).getLeft();
	    } else {
		v = ((RootNodeView)getParentView()).getRight();
	    }
	}
	NodeView sibling;
	if (v.size()-1 == v.indexOf(this)) { //this is last, return first
	    sibling = (NodeView)v.firstElement();
	} else {
	    sibling = (NodeView)v.elementAt(v.indexOf(this)+1);
	}
	return sibling;
    }

    protected NodeView getPreviousSibling() {
	Vector v = getParentView().getChildrenViews();
	if (getParentView().isRoot()) {
	    if (this.isLeft()) {
		v = ((RootNodeView)getParentView()).getLeft();
	    } else {
		v = ((RootNodeView)getParentView()).getRight();
	    }
	}
	NodeView sibling;
	if (v.indexOf(this) == 0) {//this is first, return last
	    sibling = (NodeView)v.lastElement();
	} else {
	    sibling = (NodeView)v.elementAt(v.indexOf(this)-1);
	}
	return sibling;
    }

    //
    // Update from Model
    //

    void insert() {
	for (Enumeration e = getModel().children();e.hasMoreElements();) {
	    insert((MindMapNode)e.nextElement());
	}
    }

    void insert(MindMapNode newNode) {
	NodeView newView = NodeView.newNodeView(newNode,getMap());
	newView.setLeft(this.isLeft());
	for (Enumeration e = newNode.children();e.hasMoreElements();) {
	    MindMapNode child = (MindMapNode)e.nextElement();
	    newView.insert(child);
	}
    }
    
    /**
     * This is a bit problematic, because getChildrenViews() only works if 
     * model is not yet removed. (So do not _really_ delete the model before the view 
     * removed (it needs to stay in memory)
     */
    void remove() {
	getMap().remove(this);
	getEdge().remove();
	for(Enumeration e = getChildrenViews().elements();e.hasMoreElements();) {
	    NodeView child = (NodeView)e.nextElement();
	    child.remove();
	}
    }

    void update() {
	setText(getModel().toString());
	setForeground(getModel().getColor());
	//setCursor(new Cursor(getModel().getCursor()));//getCursor() is int
	int style=0;
	if(getModel().isBold()) {
	    style=Font.BOLD;
	    if(getModel().isItalic()) {
		style=Font.BOLD|Font.ITALIC;
	    } 
	} else if (getModel().isItalic()) {
	    style=Font.ITALIC;
	}
	String font = getModel().getFont();
	int size = (int)( getModel().getFontSize()*getMap().getZoom() );
	
	if(Tools.isValidFont(font)) {
	    setFont(new Font(font,style,size));
	} else {
	    setFont(new Font("sans serif",style,size));
	}
	repaint();
    }

    void updateAll() {
	update();
	for(Enumeration e = getChildrenViews().elements();e.hasMoreElements();) {
	    NodeView child = (NodeView)e.nextElement();
	    child.updateAll();
	}
    }

//     /**
//      * THIS SHOULD BE MOVED TO MINDMAPLAYOUT
//      */
//     protected int getTreeHeight( Vector v ) { //Returns the height of all NodeViews in the Vector
// 	if ( v == null || v.size() == 0 ) {
// 	    return getPreferredSize().height + 5;
// 	}
// 	int height = 0;
// 	for ( Enumeration e = v.elements(); e.hasMoreElements(); ) {
// 	    NodeView node = (NodeView)e.nextElement();
// 	    if (node == null) {
// 		break;
// 	    }
// 	    height += node.getTreeHeight();
// 	}
// 	if (height > getPreferredSize().height) {
// 	    return height;
// 	} else {
// 	    return getPreferredSize().height;
// 	}
//     }
    
//     /**
//      * THIS SHOULD BE MOVED TO MINDMAPLAYOUT
//      */
//     protected int getTreeHeight() { //Returns the height of this subtree in pixels;
// 	if(!isTreeheightCurrent()) {
// 	    Vector v = getChildrenViews();
// 	    treeheight = getTreeHeight(v);
// 	    setTreeheightCurrent(true);
// 	}
// 	return treeheight;
//     }
}














    //
    // Layout   >>>> This must be moved to MindMapLayout <<<<
    //

//     /**
//      * Determines the logical Position of all of its direct children.
//      */
//     protected void layoutChildren() {
// 	if (getChildrenViews().size() == 0) return;//nothing to do

// 	if (this.isLeft()) { //Node is left
// 	    layout( getChildrenViews(), true );
// 	} else { //Node is right
// 	    layout( getChildrenViews(), false );
// 	} 
//     }


//     /**
//      * Places all children contained in the Vector, dependent whether they are left or right
//      */
//     protected void layout( Vector v, boolean isLeft ) {
// 	int pointer = -(getTreeHeight(v) / 2);
// 	for ( Enumeration e = v.elements(); e.hasMoreElements(); ) {
// 	    NodeView child = (NodeView)e.nextElement();
	    
// 	    //Calculate y position
// 	    pointer += (child.getTreeHeight() / 2);
// 	    int y = this.getPosition().y + pointer;
	    
// 	    //Calculate |x|
// 	    int x = child.getModel().getPath().getPathCount() - 1;

// 	    if (isLeft) x = (-x);

// 	    child.setPosition( x,y );
// 	    child.layoutChildren(); //Work recursively
// 	    pointer += (child.getTreeHeight() / 2);
// 	} //for (every Node)
//     }		

