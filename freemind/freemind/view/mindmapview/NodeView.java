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
/*$Id: NodeView.java,v 1.17 2003-11-03 10:39:53 sviles Exp $*/

package freemind.view.mindmapview;

import freemind.main.Tools;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;//This should not be done.

import java.util.LinkedList;
import java.util.ListIterator;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.net.MalformedURLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This class represents a single Node of a MindMap
 * (in analogy to TreeCellRenderer).
 */
public abstract class NodeView extends JLabel {

    protected MindMapNode model;
    protected MapView map;
    protected EdgeView edge;
    protected final static Color selectedColor = new Color(210,210,210); //Color.lightGray; //the Color of the Rectangle of a selected Node
    protected final static Color dragColor = Color.lightGray; //the Color of appearing GradientBox on drag over
    protected int treeHeight;
    private boolean left = true; //is the node left of root?
    int relYPos;//the relative Y Position to it's parent

    final static int DRAGGED_OVER_NO = 0;
    final static int DRAGGED_OVER_SON = 1;
    final static int DRAGGED_OVER_SIBLING = 2;

    protected int isDraggedOver = 0;
    public void setDraggedOver(int draggedOver) {
       isDraggedOver = draggedOver; }
    public int getDraggedOver() {
       return isDraggedOver; }

	final static int ALIGN_BOTTOM = -1;
	final static int ALIGN_CENTER = 0;
	final static int ALIGN_TOP = 1;

    public final int LEFT_WIDTH_OVERHEAD = 0;
    public final int LEFT_HEIGHT_OVERHEAD = 0;

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
	    } else if (getModel().getEdge().getStyle().equals("sharp_linear")) {
		edge = new SharpLinearEdgeView(getParentView(),this);
	    } else if (getModel().getEdge().getStyle().equals("sharp_bezier")) {
		edge = new SharpBezierEdgeView(getParentView(),this);
	    } else {
		System.err.println("Unknown Edge Type.");
	    }
	}
	addMouseListener( map.getNodeMouseMotionListener() );
	addMouseMotionListener( map.getNodeMouseMotionListener() );
	addKeyListener( map.getNodeKeyListener() );
	addDragListener( map.getNodeDragListener() );
	addDropListener( map.getNodeDropListener() );
    }

    void addDragListener(DragGestureListener dgl) {
	DragSource dragSource = DragSource.getDefaultDragSource();
	dragSource.createDefaultDragGestureRecognizer 
           (this, DnDConstants.ACTION_COPY | DnDConstants.ACTION_MOVE | DnDConstants.ACTION_LINK ,dgl);
    }

    void addDropListener(DropTargetListener dtl) {
	DropTarget dropTarget = new DropTarget(this,dtl);
	dropTarget.setActive(true);
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
	    System.err.println("Tried to create a NodeView of unknown Style.");
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

    public boolean dropAsSibling(double xCoord) {
       return isLeft() ?
          xCoord > getSize().width*2/3 :
          xCoord < getSize().width/3; }

    public boolean followLink(double xCoord) {
       return getModel().getLink() != null &&
          (getModel().isRoot() || !getModel().hasChildren() || xCoord < getSize().width/2); }

    public void updateCursor(double xCoord) {
       int requiredCursor = followLink(xCoord) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
       if (getCursor().getType() != requiredCursor) {
          setCursor(new Cursor(requiredCursor)); }}

    public boolean isRoot() {
	return model.isRoot();
    }

    public boolean isSibling(NodeView myNodeView) { 
       return getParentView() == myNodeView.getParentView(); }

    public MindMapNode getModel() {
	return model;
    }

    public void paint(Graphics graphics) {
	super.paint(graphics);
    }

   public void paintSelected(Graphics2D graphics, Dimension size) {
       if( this.isSelected() ) {
          graphics.setColor(selectedColor);
          graphics.fillRect(0,0,size.width, size.height);
          //g.drawRect(0,0,size.width-1, size.height-2);
       }
    }

   public void paintDragOver(Graphics2D graphics, Dimension size) {
        if (isDraggedOver == DRAGGED_OVER_SON) {
           if (isLeft()) {
              graphics.setPaint( new GradientPaint(size.width*3/4,0,map.getBackground(), size.width/4, 0, dragColor));
              graphics.fillRect(0, 0, size.width*3/4, size.height-1); }
           else {
              graphics.setPaint( new GradientPaint(size.width/4,0,map.getBackground(), size.width*3/4, 0, dragColor));
              graphics.fillRect(size.width/4, 0, size.width-1, size.height-1); }       
	}

        if (isDraggedOver == DRAGGED_OVER_SIBLING) {
            graphics.setPaint( new GradientPaint(0,size.height*3/5,map.getBackground(), 0, size.height/5, dragColor));
            graphics.fillRect(0, 0, size.width-1, size.height-1);
	}
    }

    public int getLeftWidthOverhead() {
       return LEFT_WIDTH_OVERHEAD; }

    //
    // get/set methods
    //

    int getTreeHeight() {
	return treeHeight;
    }

    void setTreeHeight(int treeHeight) {
	this.treeHeight = treeHeight;
    }

    protected boolean isSelected() {
	return (getMap().isSelected(this));
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
	return getModel().getParentNode().getViewer();
    }

    /**
     * This method returns the NodeViews that are children of this node.
     */
    public LinkedList getChildrenViews() {
	LinkedList childrenViews = new LinkedList();
	ListIterator it = getModel().childrenUnfolded();
	if (it != null) {
           while(it.hasNext()) {
              NodeView view = ((MindMapNode)it.next()).getViewer();
              if (view != null) { // Visible view
                 childrenViews.add(view); // child.getViewer() );
              }
           }
        }
        return childrenViews;
    }
    
    protected LinkedList getSiblingViews() {
	return getParentView().getChildrenViews();
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

    /**
     * Returns the relative position of the Edge.
	 * This is used by bold edge to know how to shift the line.
     */
    int getAlignment() {
	if( isRoot() )
	    return ALIGN_CENTER;
	return ALIGN_BOTTOM;
    }
	
    //
    // Navigation
    //

    protected NodeView getNextSibling() {
	LinkedList v = null;
	if (getParentView().isRoot()) {
	    if (this.isLeft()) {
		v = ((RootNodeView)getParentView()).getLeft();
	    } else {
		v = ((RootNodeView)getParentView()).getRight();
	    }
	} else {
	    v = getParentView().getChildrenViews();
	}	
	NodeView sibling;
	if (v.size()-1 == v.indexOf(this)) { //this is last, return first
	    sibling = (NodeView)v.getFirst();
	} else {
	    sibling = (NodeView)v.get(v.indexOf(this)+1);
	}
	return sibling;
    }

    protected NodeView getPreviousSibling() {
	LinkedList v = null;
	if (getParentView().isRoot()) {
	    if (this.isLeft()) {
		v = ((RootNodeView)getParentView()).getLeft();
	    } else {
		v = ((RootNodeView)getParentView()).getRight();
	    }
	} else {
	    v = getParentView().getChildrenViews();
	}
	NodeView sibling;
	if (v.indexOf(this) <= 0) {//this is first, return last
	    sibling = (NodeView)v.getLast();
	} else {
	    sibling = (NodeView)v.get(v.indexOf(this)-1);
	}
	return sibling;
    }

    //
    // Update from Model
    //

    void insert() {
	ListIterator it = getModel().childrenFolded();
	if (it != null) {
	    while(it.hasNext()) {               
		insert((MindMapNode)it.next());
	    }
	}
    }

    void insert(MindMapNode newNode) {
	NodeView newView = NodeView.newNodeView(newNode,getMap());
	newView.setLeft(this.isLeft());

	ListIterator it = newNode.childrenFolded();// getModel().childrenFolded();
	if (it != null) {
	    while(it.hasNext()) {
		MindMapNode child = (MindMapNode)it.next();
		newView.insert(child);
	    }
	}
    }
    
    /**
     * This is a bit problematic, because getChildrenViews() only works if 
     * model is not yet removed. (So do not _really_ delete the model before the view 
     * removed (it needs to stay in memory)
     */
    void remove() {
	getMap().remove(this);
	if (getEdge()!=null) {
           getEdge().remove(); }
        getModel().setViewer(null); // Let the model know he is invisible
	for(ListIterator e = getChildrenViews().listIterator();e.hasNext();) {
           ((NodeView)e.next()).remove(); }}

    void update() {
       // Right now, this implementation is quite logical, although it allows
       // for nonconvex feature of nodes starting with <html>.

        String nodeText = getModel().toString();
        if (nodeText.startsWith("<html>")) {
           // Make it possible to use relative img references in HTML using tag <base>.
           if (nodeText.indexOf("<img")>=0 && nodeText.indexOf("<base ") < 0 ) {
              try {
                 nodeText = "<html><base href=\""+
                    map.getModel().getURL()+"\">"+nodeText.substring(6); }
              catch (MalformedURLException e) {} }
           setText(nodeText); }
        else if (getModel().isLong()) {
           setText("<html><table><tr><td width=\""+map.getZoomed(600)+"\">"+
                   Tools.toXMLEscapedText(nodeText).replaceAll("\n","<p>") + 
                   "</td></tr></html>"); }
        else {
           setText(nodeText); }

        Color color = getModel().getColor();
        if (color==null) {
           String stdcolor = map.getController().getProperty("standardnodecolor");
           if (stdcolor.length() == 7) {
              color = Tools.xmlToColor(stdcolor); }
           else {
              color = Color.black; }}
        setForeground(color);

        String link = ((NodeAdapter)getModel()).getLink();
	if ( link != null ) {
           Icon icon = new ImageIcon(((NodeAdapter)getModel()).getFrame().getResource
                                     (link.startsWith("mailto:") ? "images/Mail.png" :
                                      (Tools.executableByExtension(link) ? "images/Executable.png" :
                                       "images/Link.png")));
           setIcon(icon); }
	else {
           setIcon(null); }

        Font font = getModel().getFont();
        font = font == null ? map.getController().getDefaultFont() : font;
        if (map.getZoom() != 1F) {
           font = font.deriveFont(font.getSize()*map.getZoom()); }
        setFont(font);
        repaint(); // Because of zoom?
    }

    void updateAll() {
	update();
	for(ListIterator e = getChildrenViews().listIterator();e.hasNext();) {
	    NodeView child = (NodeView)e.next();
	    child.updateAll();
	}
    }

   protected void setRendering(Graphics2D g) {
      if (map.getController().getAntialiasAll()) {
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }}

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

